package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
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

public class BusinessViewEvents extends AppCompatActivity {
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
        setContentView(R.layout.activity_business_view_events);
        makeNice();

        SharedPreferences sharedPref3 = getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE);
        String eventName = sharedPref3.getString("EventName", "");



        EventIDtemp = sharedPref3.getInt("EventID",Context.MODE_PRIVATE);
        EventNamelbl = findViewById(R.id.Eventname2);
        EventNamelbl.setText(eventName);
        rb = findViewById(R.id.ratingBar2);
        Date = findViewById(R.id.Datelbl);
        Time = findViewById(R.id.Timelbl);
        Venue = findViewById(R.id.venuelbl);

        new FetchEventDetailsTask().execute(EventIDtemp);
        FriendsGoing = findViewById(R.id.FriendGoing);
        usersList1 = new ArrayList<>();
        new RetrieveEventAttendeesTask().execute();
        Fadapter = new FriendAdapter2(usersList1);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        FriendsGoing.setLayoutManager(layoutManager2);
        FriendsGoing.setAdapter(Fadapter);
        FriendsGoing.addItemDecoration(new SpaceItemDecoration(10));


    }
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error Message", text);
            clipboard.setPrimaryClip(clip);
        }
    }
    private class RetrieveEventAttendeesTask extends AsyncTask<Void, Void, List<Users>> {
        @Override
        protected List<Users> doInBackground(Void... voids) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Users> attendeesList = new ArrayList<>();

            try {
                String selectQuery = "SELECT users.UserID, Username, FirstName, LastName, DisplayPicture " +
                        "FROM users " +
                        "INNER JOIN eventattendees ON users.UserID = eventattendees.UserID " +
                        "WHERE eventattendees.EventID = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, EventIDtemp); // EventID for the specific event

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int tempID = resultSet.getInt("UserID");
                    String username = resultSet.getString("Username");
                    String FirstName = resultSet.getString("FirstName");
                    String LastName = resultSet.getString("LastName");
                    byte[] imageData1 = resultSet.getBytes("DisplayPicture");

                    if (imageData1 != null && imageData1.length > 0) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Users temp = new Users(username, FirstName, LastName);
                        temp.setUserID(tempID);
                        temp.setImage1(bitmap1);
                        attendeesList.add(temp);
                    } else {
                        Users temp = new Users(username, FirstName, LastName);
                        temp.setUserID(tempID);
                        attendeesList.add(temp);
                    }
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return attendeesList;
        }

        @Override
        protected void onPostExecute(List<Users> attendeesList) {
            if (attendeesList != null && !attendeesList.isEmpty()) {
                usersList1.clear();
                usersList1.addAll(attendeesList);
                Fadapter.notifyDataSetChanged();
            } else {
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
                Toast.makeText(BusinessViewEvents.this, "Failed to fetch event details", Toast.LENGTH_SHORT).show();
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







