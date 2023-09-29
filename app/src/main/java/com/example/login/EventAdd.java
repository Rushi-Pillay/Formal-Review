package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        // View initializations
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
            OnceOff.setBackground(getDrawable(R.drawable.rounded_button_selected));
            Weekly.setBackground(getDrawable(R.drawable.rounded_button));
            Monthly.setBackground(getDrawable(R.drawable.rounded_button));
        });
        Weekly.setOnClickListener(v -> {
            reoccurence =7;
            Weekly.setBackground(getDrawable(R.drawable.rounded_button_selected));
            OnceOff.setBackground(getDrawable(R.drawable.rounded_button));
            Monthly.setBackground(getDrawable(R.drawable.rounded_button));
        });
        Monthly.setOnClickListener(v -> {
            reoccurence = 30;
                    Monthly.setBackground(getDrawable(R.drawable.rounded_button_selected));
                    OnceOff.setBackground(getDrawable(R.drawable.rounded_button));
                    Weekly.setBackground(getDrawable(R.drawable.rounded_button));
                });

        Done.setOnClickListener(v -> insertEventToDatabase());
    }

    private void insertEventToDatabase() {
        String eventName = Name.getText().toString();
        String description = Description.getText().toString();
        String eventDate = dateButton.getText().toString();
        String eventTime = TimeButton.getText().toString();

        boolean isAgeRestricted = AgeRestrict.isChecked();
        int capacityLimit = Integer.parseInt(Capacity.getText().toString());
        String eventVenue = venue.getText().toString();

        byte[] image1 = null, image2 = null, image3 = null;
        if (mArrayUri.size() > 0) image1 = convertBitmapToByteArray(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        if (mArrayUri.size() > 1) image2 = convertBitmapToByteArray(((BitmapDrawable) imageView.getDrawable()).getBitmap());
        if (mArrayUri.size() > 2) image3 = convertBitmapToByteArray(((BitmapDrawable) imageView.getDrawable()).getBitmap());

        new InsertEventTask().execute(eventName, description, eventDate, eventTime, eventVenue, isAgeRestricted, capacityLimit,reoccurence, image1, image2, image3);
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
            String date = day + "/" + month + "/" + year;
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
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
    }

    private class InsertEventTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
                String query = "INSERT INTO Event (EventName, Description, Date, Time, Venue, AgeRestrict,Recurring, Capacity, Image1, Image2, Image3) VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, (String) params[0]);
                    preparedStatement.setString(2, (String) params[1]);
                    preparedStatement.setString(3, (String) params[2]);
                    preparedStatement.setString(4, (String) params[3]);
                    preparedStatement.setString(5, (String) params[4]);
                    preparedStatement.setBoolean(6, (Boolean) params[5]);
                    preparedStatement.setInt(7, (Integer) params[6]);
                    preparedStatement.setInt(8, (Integer) params[7]);
                    preparedStatement.setBytes(9, (byte[]) params[8]);
                    preparedStatement.setBytes(10, (byte[]) params[9]);
                    preparedStatement.setBytes(11, (byte[]) params[10]);
                    preparedStatement.executeUpdate();
                }
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
}