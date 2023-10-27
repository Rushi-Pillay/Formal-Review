package com.example.login;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrowseEventsAndBusiness extends AppCompatActivity {
    private RushenBusinessAdapter adapter;
    private RushenEventAdaper adapter2;
    private TextView check;
    private ImageView imageView;
    Connection connection;
    private RecyclerView rc1;
    private RecyclerView rc2;
    private List<Business> business;
    private List<Event> events;
    private ImageView imgUser;
    int userID ;
    private EditText search;
    private String searchterm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events_and_business);
        makeNice();
        setuprecyclers();
        new RetrieveImageTask().execute(userID);
        new RetrieveEventTask().execute();
        new RetrieveBusinessTask().execute();

        imageView.setOnClickListener(events->{
            Intent intent = new Intent(BrowseEventsAndBusiness.this,ViewUserAccount.class);
            startActivity(intent);
        });

        adapter.setOnClickListener(event -> {
            try {
                RushenBusinessAdapter.BusinessViewHolder viewHolder = (RushenBusinessAdapter.BusinessViewHolder) rc1.findContainingViewHolder(event);
                if (viewHolder != null && viewHolder.business != null) {
                    SharedPreferences sharedPref2 = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPref2.edit();
                    editor2.putInt("businessID", viewHolder.business.getBusinessID());
                    editor2.apply();
                    Intent intent = new Intent(BrowseEventsAndBusiness.this,ViewBusinessProfile.class);
                    startActivity(intent);
                } else {
                    // Handle null viewHolder or null business object.
                    // You can log an error or show a Toast message for debugging.
                    Toast.makeText(BrowseEventsAndBusiness.this, "Invalid item clicked", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                copyToClipboard(e.getMessage());
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // Handle any other exceptions that may occur.
            }
        });

    }

    public void setuprecyclers(){
        business = new ArrayList<>();
        events = new ArrayList<>();
        adapter = new RushenBusinessAdapter(business);
        adapter2 = new RushenEventAdaper(events);
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userID = sharedPref.getInt("user_id", -1);

        rc1 = findViewById(R.id.recyclerView);
        rc2 = findViewById(R.id.recycleview3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc1.setLayoutManager(layoutManager);
        rc1.setAdapter(adapter);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc2.setLayoutManager(layoutManager2);
        rc2.setAdapter(adapter2);
        rc1.addItemDecoration(new SpaceItemDecoration(15));
        rc2.addItemDecoration(new SpaceItemDecoration(15));

        imageView = findViewById(R.id.imageView2);
    }
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error Message", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public void doSearch(View view) {
        setuprecyclers();
        search = findViewById(R.id.edttxtSearch);
        searchterm = String.valueOf(search.getText());
        new RetrieveSearchedBusinessTask().execute();
        new RetrieveSearchEventTask().execute();

    }

    private class RetrieveImageTask extends AsyncTask<Integer, Void, Bitmap> {
        @SuppressLint("SuspiciousIndentation")
        @Override
        protected Bitmap doInBackground(Integer... integers) {

            Connection connection = DatabaseConnection.getInstance().getConnection();
            Bitmap bitmap = null;

            try {
                String selectQuery = "SELECT DisplayPicture FROM Users WHERE Userid = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, userID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    byte[] imageData = resultSet.getBytes("DisplayPicture");
                    if (imageData!=null)
                        bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                } else {

                    Log.d("RetrieveImageTask", "Image not found for the specified user ID.");
                }

                resultSet.close();
                preparedStatement.close();
                //connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            else
            {
                imageView.setImageResource(R.drawable.defualtuser);
            }

        }
    }




    private class RetrieveBusinessTask extends AsyncTask<Void, Void, List<Business>> {
        @Override
        protected List<Business> doInBackground(Void... voids) {

            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Business> fetchedBusinesses = new ArrayList<>();

            try {
                String selectQuery = "SELECT * FROM Business";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int businessID = resultSet.getInt("BusinessID");
                    String Email = resultSet.getString("Email");
                    String password = resultSet.getString("Password");
                    String BusinessName = resultSet.getString("BusinessName");
                    String ContactNumber = resultSet.getString("ContactNumber");

                    int Capacity = resultSet.getInt("CapacityLimit");
                    String BusType = resultSet.getString("BusType");
                    String Location = resultSet.getString("Location");
                    byte[] imageData1 = resultSet.getBytes("Image1");

                    if (imageData1 != null && imageData1.length > 0) { // Check if imageData1 has a value
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Business Busnesstemp = new Business(businessID, Email, BusinessName, ContactNumber, password,  Capacity, BusType, Location);
                        Busnesstemp.setImage1(bitmap1);
                        fetchedBusinesses.add(Busnesstemp);
                    } else {
                        // Handle the case where imageData1 is null or empty.
                        // For example, you might want to create Busnesstemp without setting the image.

                        Business Busnesstemp = new Business(businessID, Email, BusinessName, ContactNumber, password, Capacity, BusType, Location);

                        fetchedBusinesses.add(Busnesstemp);
                    }


                }

                resultSet.close();
                preparedStatement.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
            return fetchedBusinesses;
        }

        @Override
        protected void onPostExecute(List<Business> fetchedBusinesses) {
            if (fetchedBusinesses != null && !fetchedBusinesses.isEmpty()) {

                business.addAll(fetchedBusinesses);
                adapter.notifyDataSetChanged();
            }
        }
    }
    private class RetrieveSearchedBusinessTask extends AsyncTask<Void, Void, List<Business>> {
        @Override
        protected List<Business> doInBackground(Void... voids) {

            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Business> fetchedBusinesses = new ArrayList<>();

            try {
                String selectQuery = "SELECT * FROM Business WHERE BusinessName LIKE '%"+searchterm+"%';";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int businessID = resultSet.getInt("BusinessID");
                    String Email = resultSet.getString("Email");
                    String password = resultSet.getString("Password");
                    String BusinessName = resultSet.getString("BusinessName");
                    String ContactNumber = resultSet.getString("ContactNumber");

                    int Capacity = resultSet.getInt("CapacityLimit");
                    String BusType = resultSet.getString("BusType");
                    String Location = resultSet.getString("Location");
                    byte[] imageData1 = resultSet.getBytes("Image1");

                    if (imageData1 != null && imageData1.length > 0) { // Check if imageData1 has a value
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Business Busnesstemp = new Business(businessID, Email, BusinessName, ContactNumber, password,  Capacity, BusType, Location);
                        Busnesstemp.setImage1(bitmap1);
                        fetchedBusinesses.add(Busnesstemp);
                    } else {
                        // Handle the case where imageData1 is null or empty.
                        // For example, you might want to create Busnesstemp without setting the image.

                        Business Busnesstemp = new Business(businessID, Email, BusinessName, ContactNumber, password, Capacity, BusType, Location);

                        fetchedBusinesses.add(Busnesstemp);
                    }


                }

                resultSet.close();
                preparedStatement.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
            return fetchedBusinesses;
        }

        @Override
        protected void onPostExecute(List<Business> fetchedBusinesses) {
            if (fetchedBusinesses != null && !fetchedBusinesses.isEmpty()) {

                business.addAll(fetchedBusinesses);
                adapter.notifyDataSetChanged();
            }
        }
    }


    private class RetrieveEventTask extends AsyncTask<Void, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(Void... voids) {

            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Event> fetchedEvents = new ArrayList<>();

            try {
                String selectQuery = "SELECT * FROM Events";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int EventID = resultSet.getInt("EventID");
                    String name = resultSet.getString("EventName");
                    String eventdate = resultSet.getString("EventDate");
                    String eventtime = resultSet.getString("EventTime");

                    String venue = resultSet.getString("Venue");
                    int capacity = resultSet.getInt("CapacityLimit");
                    int age = resultSet.getInt("AgeRestriction");
                    String reoccuring = resultSet.getString("Recurring");
                    int rating = resultSet.getInt("Rating");
                    String desc = resultSet.getString("Description");
                    byte[] imageData1 = resultSet.getBytes("Image1");
                    boolean bage =false;
                    int irec = Integer.parseInt(reoccuring);
                    if (age == 1){
                        bage = true;
                    }

                    if (imageData1 != null && imageData1.length > 0) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Event temp = new Event(EventID, name, eventdate, eventtime, venue, capacity, bage, irec, desc);

                        temp.setImage1(bitmap1);
                        temp.setRating(rating);
                        fetchedEvents.add(temp);

                    }
                    else{
                        Event temp = new Event(EventID, name, eventdate, eventtime, venue, capacity, bage, irec, desc);
                        temp.setRating(rating);
                        fetchedEvents.add(temp);
                    }

                }

                resultSet.close();
                preparedStatement.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
            return fetchedEvents;
        }

        @Override
        protected void onPostExecute(List<Event> fetchedEvents) {
            if (fetchedEvents != null && !fetchedEvents.isEmpty()) {
                events.addAll(fetchedEvents);
                adapter2.notifyDataSetChanged();
            }
        }
    }

    private class RetrieveSearchEventTask extends AsyncTask<Void, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(Void... voids) {

            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Event> fetchedEvents = new ArrayList<>();

            try {
                String selectQuery = "SELECT * FROM Events WHERE EventName LIKE '%"+searchterm+"%';";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int EventID = resultSet.getInt("EventID");
                    String name = resultSet.getString("EventName");
                    String eventdate = resultSet.getString("EventDate");
                    String eventtime = resultSet.getString("EventTime");

                    String venue = resultSet.getString("Venue");
                    int capacity = resultSet.getInt("CapacityLimit");
                    int age = resultSet.getInt("AgeRestriction");
                    String reoccuring = resultSet.getString("Recurring");
                    int rating = resultSet.getInt("Rating");
                    String desc = resultSet.getString("Description");
                    byte[] imageData1 = resultSet.getBytes("Image1");
                    boolean bage =false;
                    int irec = Integer.parseInt(reoccuring);
                    if (age == 1){
                        bage = true;
                    }

                    if (imageData1 != null && imageData1.length > 0) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Event temp = new Event(EventID, name, eventdate, eventtime, venue, capacity, bage, irec, desc);

                        temp.setImage1(bitmap1);
                        temp.setRating(rating);
                        fetchedEvents.add(temp);

                    }
                    else{
                        Event temp = new Event(EventID, name, eventdate, eventtime, venue, capacity, bage, irec, desc);
                        temp.setRating(rating);
                        fetchedEvents.add(temp);
                    }

                }

                resultSet.close();
                preparedStatement.close();
                // connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return fetchedEvents;
        }

        @Override
        protected void onPostExecute(List<Event> fetchedEvents) {
            if (fetchedEvents != null && !fetchedEvents.isEmpty()) {
                events.addAll(fetchedEvents);
                adapter2.notifyDataSetChanged();
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
