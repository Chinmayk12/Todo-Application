package com.example.todoapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "My Channel";
    private static final int NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String datetime = intent.getStringExtra("datetime");

        // Show notification
        showNotification(context, title, description, datetime);
    }

    private void showNotification(Context context, String title, String description, String datetime) {
        // Create an intent to open AlarmDisplayActivity
        Intent intent = new Intent(context, alarm_display_acticity.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("datetime", datetime);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Add FLAG_IMMUTABLE flag to PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "foxandroid")
                .setSmallIcon(R.drawable.calender_icon) // Set a valid small icon here
                .setContentTitle("Todo App")
                .setContentText("Title:"+title+"\nDescription:"+description)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}
