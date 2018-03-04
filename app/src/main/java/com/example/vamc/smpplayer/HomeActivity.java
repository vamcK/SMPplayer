package com.example.vamc.smpplayer;

import android.content.ComponentName;
import android.media.session.MediaController;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private static final int STATE_PAUSED = 0;
    private static final int STATE_PLAYING = 1;

    private int mCurrentState;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    private Button mPlayPauseToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPlayPauseToggleButton = findViewById(R.id.button);

        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, PlaybackService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();

        mPlayPauseToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( mCurrentState == STATE_PAUSED ) {
                    MediaControllerCompat.getMediaController(HomeActivity.this).getTransportControls().play();
                    mCurrentState = STATE_PLAYING;
                } else {
                    if( MediaControllerCompat.getMediaController(HomeActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
                        MediaControllerCompat.getMediaController(HomeActivity.this).getTransportControls().pause();
                    }

                    mCurrentState = STATE_PAUSED;
                }
            }
        });


    }



    private MediaControllerCompat.Callback mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if( state == null ) {
                return;
            }

            switch( state.getState() ) {
                case PlaybackStateCompat.STATE_PLAYING: {
                    mCurrentState = STATE_PLAYING;
                    break;
                }
                case PlaybackStateCompat.STATE_PAUSED: {
                    mCurrentState = STATE_PAUSED;
                    break;
                }
            }
        }
    };



    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(HomeActivity.this, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                mMediaControllerCompat.setMediaController(HomeActivity.this,mMediaControllerCompat);
                MediaControllerCompat.getMediaController(HomeActivity.this).getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);

            } catch( RemoteException e ) {

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( MediaControllerCompat.getMediaController(HomeActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(HomeActivity.this).getTransportControls().pause();
        }

        mMediaBrowserCompat.disconnect();
    }

    }
