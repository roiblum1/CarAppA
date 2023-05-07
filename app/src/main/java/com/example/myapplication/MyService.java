package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyService extends Service {
    public int length;
    MediaPlayer myPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
        myPlayer = MediaPlayer.create(this, R.raw.song);
        myPlayer.setLooping(true); // Set looping
        myPlayer.start();

    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        myPlayer.start();
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        myPlayer.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("STOP_MUSIC_SERVICE".equals(intent.getAction())) {
            stopSelf();
        }
        return flags;
    }

    public boolean isPlaying() {
        return myPlayer.isPlaying();
    }

    public void pause() {
        myPlayer.pause();
        length = myPlayer.getCurrentPosition();
    }

    public void resume() {
        myPlayer.start();
        myPlayer.seekTo(length);
    }
}