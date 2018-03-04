package com.example.vamc.smpplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.service.media.MediaBrowserService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

public class MyPlaybackService extends MediaBrowserServiceCompat {

    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String filePath = downloadDir.getAbsolutePath() + "/warner_tautz_off_broadway.mp3";
        try {
            player.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create a MediaSessionCompat
        mMediaSession = new MediaSessionCompat(this, "MediaSession");

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {

            }
        };
        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                AudioManager am = (AudioManager) MyPlaybackService.this.getSystemService(Context.AUDIO_SERVICE);
                // Request audio focus for playback, this registers the afChangeListener
                int result = am.requestAudioFocus(onAudioFocusChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    startService(new Intent(getApplicationContext(), MyPlaybackService.class));
                    mMediaSession.setActive(true);
                    player.start();
                }
            }

            @Override
            public void onPause() {
                super.onPause();
                player.pause();

            }

            @Override
            public void onStop() {
                super.onStop();
                AudioManager am = (AudioManager) MyPlaybackService.this.getSystemService(Context.AUDIO_SERVICE);
                // Abandon audio focus
                am.abandonAudioFocus(onAudioFocusChangeListener);

                stopSelf();
            }
        });

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSession.getSessionToken());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }

        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

}
