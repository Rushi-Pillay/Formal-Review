package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;

public class AttendEvent extends AppCompatActivity {
    private Button attend;
    private TextView eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_event);  // <-- This was missing

        SharedPreferences sharedPref3 = getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
        String eventName = sharedPref3.getString("EventName", "");

        attend = findViewById(R.id.button11);
        eventID = findViewById(R.id.textView12);

        createNotificationChannel();

        attend.setOnClickListener(Event -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 5);

            Intent intent = new Intent(AttendEvent.this, NotificationReceiver.class);
            intent.putExtra("EVENT_ID", eventName);

            // Assuming you have a unique integer ID for each event, for now we will use a hardcoded value
            int uniqueRequestCode = 1001;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AttendEvent.this, eventName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "EventReminderChannel";
            String description = "Channel for event reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("EVENT_REMINDER", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
