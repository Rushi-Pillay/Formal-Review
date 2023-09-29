package com.example.login;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");
        String eventName = intent.getStringExtra("EVENT_NAME");

        NotificationCompat.Builder builder;

        if ("REMINDER".equals(type)) {
            builder = new NotificationCompat.Builder(context, "EVENT_REMINDER")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Event Reminder")
                    .setContentText("Reminder for: " + eventName)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL);
        } else {  // Feedback notification
            builder = new NotificationCompat.Builder(context, "EVENT_REMINDER")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Feedback Requested")
                    .setContentText("How was " + eventName + "? Please rate it!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(eventName.hashCode(), builder.build());
    }
}
