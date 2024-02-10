package com.example.todoapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

public class MyReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String datetime = intent.getStringExtra("datetime");

        // Create an intent to start the activity that displays the custom layout
        Intent displayIntent = new Intent(context, alarm_display_acticity.class);
        displayIntent.putExtra("title", title);
        displayIntent.putExtra("description", description);
        displayIntent.putExtra("datetime",datetime);
        displayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required if starting an activity from a BroadcastReceiver

        // Start the activity
        context.startActivity(displayIntent);
    }
}
