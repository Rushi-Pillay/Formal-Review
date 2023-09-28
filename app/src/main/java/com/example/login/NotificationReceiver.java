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
        String eventName = intent.getStringExtra("EVENT_ID");  // Retrieve event name from intent
        if (eventName == null) eventName = "Unknown Event";  // Default value

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "EVENT_REMINDER")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Event Reminder")
                .setContentText("Reminder: " + eventName + " is happening in 1 hour")  // Use the event name in the notification text
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL);

        // Using a hardcoded ID for now, but ideally this should be a unique ID per notification
        int notificationId = 1001;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}