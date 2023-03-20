package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("toastMessage");
        context.stopService(new Intent(context, MyService.class));
        Toast.makeText(context, "Music Stopped, you can Start it again by the menu in the top right corner", Toast.LENGTH_SHORT).show();
    }

}