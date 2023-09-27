package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import de.hdodenhof.circleimageview.CircleImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BusinessHomePage extends AppCompatActivity {
    private int businessID;
    private List<Event> events;
    private List<Business> business;
    private businessEventDisplayAdapter adapter2;
    private RecyclerView rc1 ;
    private RecyclerView rc2;
    private TextView txtBusinessName;
    private CircleImageView imgDisplayPic2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeNice();
        setContentView(R.layout.activity_business_home_page);
        txtBusinessName = findViewById(R.id.txtBusinessName);
        imgDisplayPic2 = findViewById(R.id.imgBusDisplayPic);
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
        businessID = sharedPref.getInt("businessID", -1);
        events = new ArrayList<>();
        business = new ArrayList<>();
        adapter2 = new businessEventDisplayAdapter(events);
        rc2 = findViewById(R.id.rcEvents);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc2.setLayoutManager(layoutManager2);
        rc2.setAdapter(adapter2);
        rc2.addItemDecoration(new SpaceItemDecoration(15));
        new getBusinessDataQueryAsyncTask().execute(businessID);
        new EventQueryTask().execute(businessID);

    }
    private class getBusinessDataQueryAsyncTask extends  AsyncTask<Integer, Void, List<Business>> {

        @Override
        protected List<Business> doInBackground(Integer... integers) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Business> fetchedBusinesses = new ArrayList<>();

            try {
                String sql = "SELECT * FROM business WHERE BusinessID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, businessID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int businessID = resultSet.getInt("BusinessID");
                    String email = resultSet.getString("Email");
                    String password = resultSet.getString("Password");
                    String businessName = resultSet.getString("BusinessName");
                    String contactNumber = resultSet.getString("ContactNumber");

                    int capacity = resultSet.getInt("CapacityLimit");
                    String busType = resultSet.getString("BusType");
                    String location = resultSet.getString("Location");
                    byte[] imageData1 = resultSet.getBytes("Image1");

                    if (imageData1 != null && imageData1.length > 0) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Business business = new Business(businessID, email, businessName, contactNumber, password, capacity, busType, location);
                        business.setImage1(bitmap1);
                        fetchedBusinesses.add(business);
                    } else {
                        Business business = new Business(businessID, email, businessName, contactNumber, password,  capacity, busType, location);
                        fetchedBusinesses.add(business);
                    }
                }

                resultSet.close();
                preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return fetchedBusinesses;
        }

        @Override
        protected void onPostExecute(List<Business> business)
        {
            if (business!=null)
            {

                txtBusinessName.setText(business.get(0).getName());
                if (business.get(0).getImage1() != null ) {
                    imgDisplayPic2.setImageBitmap(business.get(0).getImage1());
                } else {
                    imgDisplayPic2.setImageResource(R.drawable.img);
                }
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
    private class EventQueryTask extends AsyncTask<Integer, Void, List<Event>> {



        @Override
        protected List<Event> doInBackground(Integer... Integer) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Event> fetchedEvents = new ArrayList<>();

            try {
                String sql = "Select EventID from businessevents WHERE BusinessID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,businessID);
                ResultSet resultSet = preparedStatement.executeQuery();

                   while (resultSet.next()) {
                       int EventID = resultSet.getInt("EventID");
                       String sql2 = "Select * FROM events WHERE EventID = ?";
                       PreparedStatement preparedStatement1 = connection.prepareStatement(sql2);
                       preparedStatement1.setInt(1, EventID);
                       ResultSet resultSet1 = preparedStatement1.executeQuery();
                       while (resultSet1.next()) {
                           int EventID2 = resultSet1.getInt("EventID");
                           String name = resultSet1.getString("EventName");
                           String eventdate = resultSet1.getString("EventDate");
                           String eventtime = resultSet1.getString("EventTime");
                           Double ticket = resultSet1.getDouble("TicketPrice");
                           String venue = resultSet1.getString("Venue");
                           int capacity = resultSet1.getInt("CapacityLimit");
                           int age = resultSet1.getInt("AgeRestriction");
                           String reoccuring = resultSet1.getString("Recurring");
                           int rating = resultSet1.getInt("Rating");
                           String desc = resultSet1.getString("Description");
                           byte[] imageData1 = resultSet1.getBytes("Image1");
                           Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                           fetchedEvents.add(new Event(EventID2, name, eventdate, eventtime, ticket, venue, capacity, age, reoccuring, desc, rating, bitmap1));
                       }
                       Log.d("BusinessHomePage", "Number of events retrieved: " + fetchedEvents.size());
                       resultSet1.close();
                       preparedStatement1.close();
                   }

                resultSet.close();
                preparedStatement.close();

            }catch (Exception e)
            {

                e.getMessage();
            }
            return fetchedEvents;
        }

        @Override
        protected void onPostExecute(List<Event> eventData) {
            if(eventData != null)
            {
                events.addAll(eventData);
                adapter2.notifyDataSetChanged();
                rc2.setAdapter(adapter2);
            }


        }
    }

}
