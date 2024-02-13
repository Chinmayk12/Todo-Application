package com.example.todoapplication;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.List;

public class MyReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "My Channel";
    private static final int NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String datetime = intent.getStringExtra("datetime");

        // Check if the app is in the foreground
        boolean isAppInForeground = isAppInForeground(context);

        if (isAppInForeground) {
            // If the app is in the foreground, start the alarm activity directly
            startAlarmActivity(context, title, description, datetime);
        } else {
            // If the app is in the background or not running, show a notification
            showNotification(context, title, description, datetime);
        }
    }

    private void startAlarmActivity(Context context, String title, String description, String datetime) {
        Intent intent = new Intent(context, alarm_display_acticity.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("datetime", datetime);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void showNotification(Context context, String title, String description, String datetime) {
        Intent intent = new Intent(context, alarm_display_acticity.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("datetime", datetime);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.calender_icon)
                .setContentTitle("Todo App")
                .setContentText("Title: " + title + "\nDescription: " + description)
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

    private boolean isAppInForeground(Context context) {
        // Get the activity manager
        android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // Get a list of running app processes
        List<android.app.ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        // Check if any of the running app processes belong to the app's package name
        if (runningAppProcesses != null) {
            for (android.app.ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                if (processInfo.processName.equals(context.getPackageName()) && processInfo.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }

        return false;
    }
}