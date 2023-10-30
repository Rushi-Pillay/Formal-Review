package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventAdd extends AppCompatActivity {
    // Variable declarations
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private static final int MAX_IMAGES = 3;
    private final int PICK_IMAGE_MULTIPLE = 1;
    private EditText Name, Description, venue, Capacity;
    private CheckBox AgeRestrict;
    private ImageView imageView;
    private Button dateButton, Weekly, Monthly, OnceOff, AddPhotos, preview, Done, TimeButton, Next;
    private ArrayList<Uri> mArrayUri = new ArrayList<>();
    private int position = 0;
    private int reoccurence = 0;
    boolean nameset = false;
    boolean dateset = false;
    boolean descset = false;
    boolean locationset = false;
    boolean timeset = false;
    boolean capacityset = false;
    boolean reoccurset = false;
    int latest;
    int businessID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        makeNice();
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
         businessID = sharedPref.getInt("businessID", -1);
        Name = findViewById(R.id.editTextTextPersonName2);
        Description = findViewById(R.id.DescriptionText);
        TimeButton = findViewById(R.id.time2);
        AgeRestrict = findViewById(R.id.checkBox);
        Capacity = findViewById(R.id.editTextNumber);
        Weekly = findViewById(R.id.button6);
        Monthly = findViewById(R.id.button7);
        OnceOff = findViewById(R.id.button5);
        AddPhotos = findViewById(R.id.button8);
        preview = findViewById(R.id.button12);
        Done = findViewById(R.id.button13);
        imageView = findViewById(R.id.imageView8);
        dateButton = findViewById(R.id.button10);
        Next = findViewById(R.id.button9);
        venue = findViewById(R.id.EditTextLoaction);
        initDatePicker();
        dateButton.setOnClickListener(event -> datePickerDialog.show());
        venue.setOnClickListener(v->{
            locationset = true;
        });
        Description.setOnClickListener(v -> {
            descset = true;
        });
        Name.setOnClickListener(v->{
            nameset = true;
        });
        Capacity.setOnClickListener(v->{
            capacityset = true;
        });


        AddPhotos.setOnClickListener(v -> {
            if (mArrayUri.size() >= MAX_IMAGES) {
                Toast.makeText(EventAdd.this, "You can only select 3 images", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
        });

        Next.setOnClickListener(v -> {
            if (position < mArrayUri.size() - 1) {
                position++;
                imageView.setImageURI(mArrayUri.get(position));
            } else {
                Toast.makeText(EventAdd.this, "Last Image Already Shown", Toast.LENGTH_SHORT).show();
            }
        });

        preview.setOnClickListener(v -> {
            if (position > 0) {
                position--;
                imageView.setImageURI(mArrayUri.get(position));
            }
        });

        initTimePicker();
        TimeButton.setOnClickListener(v -> timePickerDialog.show());

        OnceOff.setOnClickListener(v -> {
            reoccurence =0;
            reoccurset = true;
            OnceOff.setBackground(getDrawable(R.drawable.rounded_button_selected));
            Weekly.setBackground(getDrawable(R.drawable.rounded_button));
            Monthly.setBackground(getDrawable(R.drawable.rounded_button));
        });
        Weekly.setOnClickListener(v -> {
            reoccurence =7;
            reoccurset = true;
            Weekly.setBackground(getDrawable(R.drawable.rounded_button_selected));
            OnceOff.setBackground(getDrawable(R.drawable.rounded_button));
            Monthly.setBackground(getDrawable(R.drawable.rounded_button));
        });
        Monthly.setOnClickListener(v -> {
            reoccurence = 30;
            reoccurset = true;
                    Monthly.setBackground(getDrawable(R.drawable.rounded_button_selected));
                    OnceOff.setBackground(getDrawable(R.drawable.rounded_button));
                    Weekly.setBackground(getDrawable(R.drawable.rounded_button));
                });

        Done.setOnClickListener(v -> insertEventToDatabase());

    }

    private void insertEventToDatabase() {


            String eventName = Name.getText().toString().trim();
            String description = Description.getText().toString().trim();
            String eventDate = dateButton.getText().toString().trim();
            String eventTime = TimeButton.getText().toString().trim();
            boolean isAgeRestricted = AgeRestrict.isChecked();
            int capacityLimit = Integer.parseInt(Capacity.getText().toString().trim());
            String eventVenue = venue.getText().toString().trim();
            int tempage =0;
            if(isAgeRestricted ==true){
                tempage = 1;
            }
            byte[] image1 = null, image2 = null, image3 = null;
            if (mArrayUri.size() > 0) image1 = uriToByteArray(mArrayUri.get(0), this);
            if (mArrayUri.size() > 1) image2 = uriToByteArray(mArrayUri.get(1), this);
            if (mArrayUri.size() > 2) image3 = uriToByteArray(mArrayUri.get(2), this);

            if (mArrayUri.size() == 0){
                new InsertEventTask0().execute(eventName, description, eventDate, eventTime, eventVenue, tempage,reoccurence+"",capacityLimit);
            }
            else if(mArrayUri.size() == 1){
                new InsertEventTask1().execute(eventName, description, eventDate, eventTime, eventVenue, tempage,reoccurence+"",capacityLimit,image1);
            }
            else if(mArrayUri.size() == 2){
                new InsertEventTask2().execute(eventName, description, eventDate, eventTime, eventVenue, tempage,reoccurence+"",capacityLimit,image1,image2);
            }
            else{
                new InsertEventTask3().execute(eventName, description, eventDate, eventTime, eventVenue, tempage,reoccurence+"",capacityLimit, image1, image2, image3);
            }

           new GetLatestEventIDTask().execute();

          //  new InsertEventbussTask().execute(latest,businessID);

                Intent intent = new Intent(EventAdd.this,BusinessHomePage.class);
                startActivity(intent);



    }
    private byte[] uriToByteArray(Uri uri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    mArrayUri.add(mClipData.getItemAt(i).getUri());
                }
                imageView.setImageURI(mArrayUri.get(0));
            } else if (data.getData() != null) {
                mArrayUri.add(data.getData());
                imageView.setImageURI(mArrayUri.get(0));
            }
        }
    }
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = year+"-"+month+"-"+day;

            dateButton.setText(date);
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
        dateset = true;
    }

    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            String time = hourOfDay + ":" + minute;
            TimeButton.setText(time);
        };
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, timeSetListener, hour, minute, true);
        timeset = true;
    }

    private class InsertEventTask3 extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "INSERT INTO events (EventName, Description, EventDate, EventTime, Venue, AgeRestriction,Recurring, CapacityLimit, Image1, Image2, Image3) VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, (String) params[0]);
                    preparedStatement.setString(2, (String) params[1]);
                    preparedStatement.setString(3, (String) params[2]);
                    preparedStatement.setString(4, (String) params[3]);
                    preparedStatement.setString(5, (String) params[4]);
                    preparedStatement.setInt(6, (Integer) params[5]);
                    preparedStatement.setString(7, (String) params[6]);
                    preparedStatement.setInt(8, (Integer) params[7]);
                    preparedStatement.setBytes(9, (byte[]) params[8]);
                    preparedStatement.setBytes(10, (byte[]) params[9]);
                    preparedStatement.setBytes(11, (byte[]) params[10]);
                    preparedStatement.executeUpdate();

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(EventAdd.this, "Event added successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(EventAdd.this, "Error occurred while adding event", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class InsertEventTask0 extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "INSERT INTO events (EventName, Description, EventDate, EventTime, Venue, AgeRestriction,Recurring, CapacityLimit) VALUES (?, ?, ?, ?, ?, ? , ?, ? )";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query) ;
                    preparedStatement.setString(1, (String) params[0]);
                    preparedStatement.setString(2, (String) params[1]);
                    preparedStatement.setString(3, (String) params[2]);
                    preparedStatement.setString(4, (String) params[3]);
                    preparedStatement.setString(5, (String) params[4]);
                    preparedStatement.setInt(6, (Integer) params[5]);
                    preparedStatement.setString(7, (String) params[6]);
                    preparedStatement.setInt(8, (Integer) params[7]);
                    preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(EventAdd.this, "Event added successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(EventAdd.this, "Error occurred while adding event", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class InsertEventTask1 extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
             Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "INSERT INTO events (EventName, Description, EventDate, EventTime, Venue, AgeRestriction,Recurring, CapacityLimit, Image1) VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, (String) params[0]);
                    preparedStatement.setString(2, (String) params[1]);
                    preparedStatement.setString(3, (String) params[2]);
                    preparedStatement.setString(4, (String) params[3]);
                    preparedStatement.setString(5, (String) params[4]);
                    preparedStatement.setInt(6, (Integer) params[5]);
                    preparedStatement.setString(7, (String) params[6]);
                    preparedStatement.setInt(8, (Integer) params[7]);
                    preparedStatement.setBytes(9, (byte[]) params[8]);
                    preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(EventAdd.this, "Event added successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(EventAdd.this, "Error occurred while adding event", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class InsertEventTask2 extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "INSERT INTO events (EventName, Description, EventDate, EventTime, Venue, AgeRestriction,Recurring, CapacityLimit, Image1, Image2) VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, (String) params[0]);
                    preparedStatement.setString(2, (String) params[1]);
                    preparedStatement.setString(3, (String) params[2]);
                    preparedStatement.setString(4, (String) params[3]);
                    preparedStatement.setString(5, (String) params[4]);
                    preparedStatement.setInt(6, (Integer) params[5]);
                    preparedStatement.setString(7, (String) params[6]);
                    preparedStatement.setInt(8, (Integer) params[7]);
                    preparedStatement.setBytes(9, (byte[]) params[8]);
                    preparedStatement.setBytes(10, (byte[]) params[9]);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(EventAdd.this, "Event added successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(EventAdd.this, "Error occurred while adding event", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class GetLatestEventIDTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT MAX(eventID) FROM events"; // For MySQL. If using another DB, adjust this query accordingly.
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1; // Return -1 if there's an error
        }

        @Override
        protected void onPostExecute(Integer result) {
            latest = result;
            if (latest != -1) {
                Toast.makeText(EventAdd.this, "Latest Event ID fetched: " + latest, Toast.LENGTH_SHORT).show();
                new InsertEventbussTask().execute(latest,businessID);
            } else {
                Toast.makeText(EventAdd.this, "Error occurred while fetching latest event ID", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class InsertEventbussTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "INSERT INTO businessevents (EventID, BusinessID) VALUES (?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, (Integer) params[0]);
                preparedStatement.setInt(2, (Integer) params[1]);
                preparedStatement.executeUpdate();

                return true;
            } catch (SQLException e) {
                e.printStackTrace();

                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(EventAdd.this, "Event added successfully", Toast.LENGTH_SHORT).show();

                finish();
            } else {
                Toast.makeText(EventAdd.this, "Error occurred while adding event", Toast.LENGTH_SHORT).show();
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
