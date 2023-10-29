package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendEvent extends AppCompatActivity {
    private Button attend;
    private static TextView EventNamelbl;
    private RecyclerView EventImages;
    private static RatingBar rb;
    private static TextView Date;
    private static TextView Time;
    private static TextView Venue;
    private RecyclerView FriendsGoing;
    private List<Users> usersList1;
    private FriendAdapter2 Fadapter;
    int userID;
    int EventIDtemp;
    private boolean isAttending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_event);
        makeNice();

        SharedPreferences sharedPref3 = getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
        String eventName = sharedPref3.getString("EventName", "");
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userID = sharedPref.getInt("user_id", -1);


        EventIDtemp = sharedPref3.getInt("EventID",Context.MODE_PRIVATE);
        attend = findViewById(R.id.button11);
        EventNamelbl = findViewById(R.id.Eventname2);
        EventNamelbl.setText(eventName);
        rb = findViewById(R.id.ratingBar2);
        Date = findViewById(R.id.Datelbl);
        Time = findViewById(R.id.Timelbl);
        Venue = findViewById(R.id.venuelbl);

        new FetchEventDetailsTask().execute(EventIDtemp);


        FriendsGoing = findViewById(R.id.FriendGoing);
        usersList1 = new ArrayList<>();
        new RetrieveFriendsTask2().execute();
        Fadapter = new FriendAdapter2(usersList1);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        FriendsGoing.setLayoutManager(layoutManager2);
        FriendsGoing.setAdapter(Fadapter);
        FriendsGoing.addItemDecoration(new SpaceItemDecoration(10));

        new CheckIfAttendingTask().execute(EventIDtemp);


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
    private class CheckIfAttendingTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... eventIDs) {
            int eventId = eventIDs[0];
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try {
                String query = "SELECT * FROM eventattendees WHERE UserID = ? AND EventID = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, userID);
                statement.setInt(2, eventId);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next(); // True if attending, false otherwise
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean attending) {
            isAttending = attending;
            if (attending) {
                attend.setText("Unattend the event");
                attend.setBackgroundColor(Color.parseColor("#FF4D4D"));
            } else {
                attend.setText("Are you going to attend the event?");
                attend.setBackgroundColor(Color.parseColor("#1DB954"));
            }
        }
    }
    private class AddEventAttendeeTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... eventIDs) {
            int eventToAttend = eventIDs[0];
            Connection connection = DatabaseConnection.getInstance().getConnection();
            try {
                if (isAttending) {
                    String deleteQuery = "DELETE FROM eventattendees WHERE UserID = ? AND EventID = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setInt(1, userID);
                    deleteStatement.setInt(2, eventToAttend);
                    deleteStatement.executeUpdate();
                    return true;
                } else {
                    String insertQuery = "INSERT INTO eventattendees(UserID, EventID) VALUES(?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setInt(1, userID);
                    insertStatement.setInt(2, eventToAttend);
                    insertStatement.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            if (isSuccess) {
                if (isAttending) {
                    Toast.makeText(AttendEvent.this, "Successfully unattended the event", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AttendEvent.this, "Event attended successfully", Toast.LENGTH_SHORT).show();
                }
                // Toggle attending state and update button text
                isAttending = !isAttending;
                if (isAttending) {
                    attend.setText("Unattend the event");
                    attend.setBackgroundColor(Color.parseColor("#FF4D4D"));
                } else {
                    attend.setText("Are you going to attend the event?");
                    attend.setBackgroundColor(Color.parseColor("#1DB954"));
                }
            } else {
                Toast.makeText(AttendEvent.this, "Operation failed", Toast.LENGTH_SHORT).show();
            }
        }
    }





    private class RetrieveFriendsTask2 extends AsyncTask<Void, Void, List<Users>> {
        @Override
        protected List<Users> doInBackground(Void... voids) {

            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Users> fetchedFreinds = new ArrayList<>();

            try {
                String selectQuery = "SELECT users.UserID, Username, FirstName, LastName, DisplayPicture FROM users " +
                        "WHERE users.UserID IN (SELECT friendships.UserID2 FROM friendships WHERE friendships.UserID1 = ?) " +
                        "AND users.UserID IN (SELECT eventattendees.UserID FROM eventattendees WHERE eventattendees.EventID = ?)";

                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, userID); // ID of the currently logged-in user
                preparedStatement.setInt(2, EventIDtemp); // EventID for the specific event

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int tempID = resultSet.getInt("UserID");
                    String username = resultSet.getString("Username");
                    String Firstname = resultSet.getString("FirstName");
                    String LastName = resultSet.getString("LastName");
                    byte[] imageData1 = resultSet.getBytes("DisplayPicture");

                    if (imageData1 != null && imageData1.length > 0) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Users temp = new Users(username, Firstname, LastName);
                        temp.setUserID(tempID);
                        temp.setImage1(bitmap1);
                        fetchedFreinds.add(temp);
                    } else {
                        Users temp = new Users(username, Firstname, LastName);
                        temp.setUserID(tempID);
                        fetchedFreinds.add(temp);
                    }
                }

                resultSet.close();
                preparedStatement.close();
                // connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return fetchedFreinds;
        }

        @Override
        protected void onPostExecute(List<Users> fetchedFreinds) {
            if (fetchedFreinds != null && !fetchedFreinds.isEmpty()) {
                usersList1.clear();
                usersList1.addAll(fetchedFreinds);
                Fadapter.notifyDataSetChanged();
            }
            else{
                usersList1.clear();
                Fadapter.notifyDataSetChanged();
            }
        }
    }
    class FetchEventDetailsTask extends AsyncTask<Integer, Void, Event> {

        @Override
        protected Event doInBackground(Integer... eventIDs) {
            int eventID = eventIDs[0];
            Connection connection = DatabaseConnection.getInstance().getConnection();
            Event eventDetails = null;

            try {
                String selectQuery = "SELECT * FROM Events WHERE EventID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, eventID);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int EventID = resultSet.getInt("EventID");
                    String name = resultSet.getString("EventName");
                    String eventdate = resultSet.getString("EventDate");
                    String eventtime = resultSet.getString("EventTime");
                    String venue = resultSet.getString("Venue");
                    int capacity = resultSet.getInt("CapacityLimit");
                    boolean age = resultSet.getBoolean("AgeRestriction");
                    int reoccuring = resultSet.getInt("Recurring");
                    int rating = resultSet.getInt("Rating");
                    String desc = resultSet.getString("Description");
                    byte[] imageData1 = resultSet.getBytes("Image1");
                    byte[] imageData2 = resultSet.getBytes("Image2");
                    byte[] imageData3 = resultSet.getBytes("Image3");

                    eventDetails = new Event(EventID, name, eventdate, eventtime, venue, capacity, age, reoccuring, desc);

                    if (imageData1 != null && imageData1.length > 0) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        eventDetails.setImage1(bitmap1);
                    }
                    if (imageData2 != null && imageData2.length > 0) {
                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(imageData2, 0, imageData2.length);
                        eventDetails.setImage2(bitmap2);
                    }
                    if (imageData3 != null && imageData3.length > 0) {
                        Bitmap bitmap3 = BitmapFactory.decodeByteArray(imageData3, 0, imageData3.length);
                        eventDetails.setImage3(bitmap3);
                    }

                    eventDetails.setRating(rating);
                }

                resultSet.close();
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return eventDetails;
        }


        @Override
        protected void onPostExecute(Event eventDetails) {
            if (eventDetails != null) {
                EventNamelbl.setText(eventDetails.getName());
                Date.setText(eventDetails.getDate());
                Time.setText(eventDetails.getTime());
                Venue.setText(eventDetails.getVenue());
                rb.setRating(eventDetails.getRating());

                ImageView eventImage1 = findViewById(R.id.image1);
                ImageView eventImage2 = findViewById(R.id.image2);
                ImageView eventImage3 = findViewById(R.id.image3);

                // Check if image1 from database is not null, then set it to ImageView
                if (eventDetails.getImage1() != null) {
                    eventImage1.setImageBitmap(eventDetails.getImage1());
                    eventImage1.setVisibility(View.VISIBLE); // ensure the ImageView is visible
                } else {
                    eventImage1.setVisibility(View.GONE); // hide ImageView if no image
                }

                // Check if image2 from database is not null, then set it to ImageView
                if (eventDetails.getImage2() != null) {
                    eventImage2.setImageBitmap(eventDetails.getImage2());
                    eventImage2.setVisibility(View.VISIBLE);
                } else {
                    eventImage2.setVisibility(View.GONE);
                }

                // Check if image3 from database is not null, then set it to ImageView
                if (eventDetails.getImage3() != null) {
                    eventImage3.setImageBitmap(eventDetails.getImage3());
                    eventImage3.setVisibility(View.VISIBLE);
                } else {
                    eventImage3.setVisibility(View.GONE);
                }

            } else {
                Toast.makeText(AttendEvent.this, "Failed to fetch event details", Toast.LENGTH_SHORT).show();
            }
        }
    }


        private void makeNice() {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.parseColor("#FFF1F1F1"));
            }

            // Make the navigation bar immersive
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

            // Change the status bar icons color to black
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }







