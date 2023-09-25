package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewBusinessProfile extends AppCompatActivity {
    private Connection connection;
    private int userID;
    private int businessID;
    private Button btnFollow;
    private boolean isFollowing;
    private  TextView textbus;
    private   TextView txtUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_business_profile);
        connection = DatabaseConnection.getInstance().getConnection();

        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userID = sharedPref.getInt("user_id", -1);
        btnFollow = findViewById(R.id.btnFollow);
        SharedPreferences sharedPref2 = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
        businessID = sharedPref2.getInt("businessID", -1);
        textbus = findViewById(R.id.txtBusinessID);
        txtUserID = findViewById(R.id.txtUserID);
        txtUserID.setText(userID + "");
        textbus.setText(businessID + "");


        new CheckFollowerTask().execute(userID, businessID);
    }

    public void onFollowClicked(View view) {
        if (isFollowing) {
            // If already following, unfollow by removing the entry from the 'followers' table.
            new UnfollowTask().execute(userID, businessID);
        } else {
            // If not following, follow by inserting the entry into the 'followers' table.
            new InsertFollowerTask().execute(userID, businessID);
        }
    }

    // AsyncTask to check if the user is following the business
    private class CheckFollowerTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            int userID = params[0];
            int businessID = params[1];
            try {
                // Prepare the SQL statement to check if the user is following the business
                String sql = "SELECT 1 FROM followers WHERE UserID = ? AND BusinessID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, businessID);

                // Execute the SQL statement
                ResultSet resultSet = preparedStatement.executeQuery();

                boolean isFollowing2 ;
                 isFollowing2  = resultSet.next();
               // If there's a result, the user is following.

                // Close the result set and prepared statement
                resultSet.close();
                preparedStatement.close();

                return isFollowing2;
            } catch (SQLException e) {
                Log.e("ViewBusinessProfile", "SQL Exception: " + e.getMessage()); // Handle any potential exceptions

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            isFollowing = result;
            updateFollowButton();
        }
    }

    // AsyncTask to insert data into the 'followers' table
    private class InsertFollowerTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int userID = params[0];
            int businessID = params[1];
            try {
                // Prepare the SQL statement
                String sql = "INSERT INTO followers (UserID, BusinessID) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, businessID);

                // Execute the SQL statement
                preparedStatement.executeUpdate();

                // Close the prepared statement
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Handle any potential exceptions
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isFollowing = true;
            updateFollowButton();
        }
    }

    // AsyncTask to remove the user as a follower from the 'followers' table
    private class UnfollowTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int userID = params[0];
            int businessID = params[1];
            try {
                // Prepare the SQL statement to delete the follower entry
                String sql = "DELETE FROM followers WHERE UserID = ? AND BusinessID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, businessID);

                // Execute the SQL statement
                preparedStatement.executeUpdate();

                // Close the prepared statement
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Handle any potential exceptions
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isFollowing = false;
            updateFollowButton();
        }
    }

    // Update the button text based on whether the user is following the business
    private void updateFollowButton() {
        if (isFollowing) {
            btnFollow.setText("Unfollow");
        } else {
            btnFollow.setText("Follow");
        }
    }
}
