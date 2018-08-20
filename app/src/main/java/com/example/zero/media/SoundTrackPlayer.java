package com.example.zero.media;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.zero.setting.GameSetting;

public class SoundTrackPlayer {
    Context context;
    MediaPlayer player;

    public SoundTrackPlayer(Context context) {
        this.context = context;
    }

    public void startPlayerAtOnce(int resource) {
        if (GameSetting.amThanhNen) {
            if (player == null) {
                player = MediaPlayer.create(this.context, resource);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stopPlayer();
                    }
                });
                player.start();
            }
        }
    }

    public void startPlayerNonStop(int resource) {
        if (GameSetting.amThanhNen) {
            if (player == null) {
                player = MediaPlayer.create(this.context, resource);
                player.setLooping(true);
                player.start();
            }
        }
    }

    public void startPlayerWithOnCompleteListener(int resource, MediaPlayer.OnCompletionListener listener) {
        if (GameSetting.amThanhNen) {
            if (player == null) {
                player = MediaPlayer.create(this.context, resource);
                player.setOnCompletionListener(listener);
                player.start();
            }
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.pause();
        }
    }

    public void continuePlayer() {
        if (player != null) {
            player.start();
        }
    }

    public void stopPlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
