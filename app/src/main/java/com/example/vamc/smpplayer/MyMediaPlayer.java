package com.example.vamc.smpplayer;

import android.content.ComponentName;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MyMediaPlayer extends AppCompatActivity {


    private MediaBrowserCompat mMediaBrowser;
    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {

                    // Get the token for the MediaSession
                    MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

                    // Create a MediaControllerCompat
                    MediaControllerCompat mediaController =
                            null;
                    try {
                        mediaController = new MediaControllerCompat(MyMediaPlayer.this, // Context
                                token);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    // Save the controller
                    MediaControllerCompat.setMediaController(MyMediaPlayer.this, mediaController);

                    // Finish building the UI
                    buildTransportControls();
                }

                @Override
                public void onConnectionSuspended() {
                    // The Service has crashed. Disable transport controls until it automatically reconnects
                }

                @Override
                public void onConnectionFailed() {
                    // The Service has refused our connection
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_media_player);

        // Create MediaBrowserServiceCompat
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MyPlaybackService.class),
                mConnectionCallbacks,
                null); // optional Bundle
    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        // (see "stay in sync with the MediaSession")
        if (MediaControllerCompat.getMediaController(MyMediaPlayer.this) != null) {
            MediaControllerCompat.getMediaController(MyMediaPlayer.this).unregisterCallback(controllerCallback);
        }
        mMediaBrowser.disconnect();

    }

    Button mPlayPause;

    void buildTransportControls() {
        // Grab the view for the play/pause button
        mPlayPause = (Button) findViewById(R.id.play_pause);

        // Attach a listener to the button
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly

                int pbState = MediaControllerCompat.getMediaController(MyMediaPlayer.this).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(MyMediaPlayer.this).getTransportControls().pause();
                } else {
                    MediaControllerCompat.getMediaController(MyMediaPlayer.this).getTransportControls().playFromMediaId(String.valueOf(R.raw.warner_tautz_off_broadway), null);
                }
            }
        });

        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(MyMediaPlayer.this);

        // Display the initial state
        MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);
    }

    MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    Toast.makeText(MyMediaPlayer.this, "Metadata changed to "+metadata.toString(), Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {

                    Toast.makeText(MyMediaPlayer.this, "Playback changed to "+state.toString(), Toast.LENGTH_SHORT).show();
                }
            };
}


