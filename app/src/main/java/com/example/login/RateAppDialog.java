package com.example.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import com.mysql.jdbc.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RateAppDialog extends Dialog {

    private RatingBar ratingBar;
    private Button submitButton;
    private Button dismissButton;
    TextView msgText;
    int eid;
    int userId = 1;
    String ename;

    public RateAppDialog(Context context) {
        super(context);
    }

    public void SetInfo(int eventid, String eventName) {
        eid = eventid;
        ename = eventName;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_app_dialog);
        msgText = findViewById(R.id.message_text);
        dismissButton = findViewById(R.id.dismissbtn);
        submitButton = findViewById(R.id.submit_button);
        ratingBar = findViewById(R.id.Rate_eventrating);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the rating from the user.
                float rating = ratingBar.getRating();
                Calendar calendar = Calendar.getInstance();
                Date currentDate = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = dateFormat.format(currentDate);
                new InsertRatingTask().execute(eid, userId, formattedDate, rating);
                dismiss();
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private class InsertRatingTask extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... params) {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            int eventID = (int) params[0];
            int userID = (int) params[1];
            String date = (String) params[2];
            float rating = (float) params[3];

            String sql = "INSERT INTO eventrating (EventID, UserID, Date, Rating) " +
                    "VALUES (?, ?, ?, ?)";

            try {
                PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
                preparedStatement.setInt(1, eventID);
                preparedStatement.setInt(2, userID);
                preparedStatement.setString(3, date);
                preparedStatement.setDouble(4, rating);

                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                //Toast.makeText(MainPage., "Rating inserted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(YourActivity.this, "Error inserting rating.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
