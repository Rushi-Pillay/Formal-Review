package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class AttendEvent extends AppCompatActivity {
    private Button attend;
    private TextView eventID;
    int userID;
    int EventIDtemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_event);

        SharedPreferences sharedPref3 = getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
        String eventName = sharedPref3.getString("EventName", "");
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userID = sharedPref.getInt("user_id", -1);
        EventIDtemp = sharedPref3.getInt("EventID",Context.MODE_PRIVATE);
        attend = findViewById(R.id.button11);
        eventID = findViewById(R.id.textView12);

        createNotificationChannel();

        attend.setOnClickListener(Event-> {
            Calendar calendarReminder = Calendar.getInstance();
            calendarReminder.add(Calendar.SECOND, 5);
            new AddEventAttendeeTask().execute(EventIDtemp);
            Intent reminderIntent = new Intent(AttendEvent.this, NotificationReceiver.class);
            reminderIntent.putExtra("EVENT_NAME", eventName);
            reminderIntent.putExtra("type", "REMINDER");

            PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(AttendEvent.this, eventName.hashCode(), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarReminder.getTimeInMillis(), reminderPendingIntent);


            Calendar calendarFeedback = Calendar.getInstance();
            calendarFeedback.add(Calendar.SECOND, 15);

            Intent feedbackIntent = new Intent(AttendEvent.this, NotificationReceiver.class);
            feedbackIntent.putExtra("EVENT_NAME", eventName);
            feedbackIntent.putExtra("type", "FEEDBACK");

            PendingIntent feedbackPendingIntent = PendingIntent.getBroadcast(AttendEvent.this, eventName.hashCode() + 1, feedbackIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendarFeedback.getTimeInMillis(), feedbackPendingIntent);
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
    private class AddEventAttendeeTask extends AsyncTask<Integer, Void, Boolean> {


        private int eventToAttend;

        @Override
        protected Boolean doInBackground(Integer... eventIDs) {
            eventToAttend = eventIDs[0]; // Initialize it here
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String insertQuery = "INSERT INTO eventattendees(UserID, EventID) VALUES(?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, eventToAttend);
                int rowsAffected = preparedStatement.executeUpdate();
                preparedStatement.close();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                Toast.makeText(AttendEvent.this, "Event attended successfully", Toast.LENGTH_SHORT).show();
                // You can add any additional tasks or refresh UI here
            } else {
                Toast.makeText(AttendEvent.this, "Failed to attend event", Toast.LENGTH_SHORT).show();
            }
        }
    }





}
