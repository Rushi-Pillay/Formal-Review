package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserAccount extends AppCompatActivity {
   Connection connection;
   private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_account);
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userID = sharedPref.getInt("user_id", -1);
        connection = DatabaseConnection.getInstance().getConnection();

        // Fetch user data when the activity is created
        new GetUserDataTask().execute(userID);


    }

    public void UpdateProfileOnClick(View view) {
        Intent intent = new Intent(ViewUserAccount.this,EditUserprofile.class);
        startActivity(intent);
    }

    public void viewFriendsOnClick(View view) {
        Intent intent = new Intent(ViewUserAccount.this,FriendActivity.class);
        startActivity(intent);
    }

    private class GetUserDataTask extends AsyncTask<Integer, Void, UserData> {
        @Override
        protected UserData doInBackground(Integer... userIds) {
            int userId = userIds[0];
            UserData userData = null;

            try {
                // Create a SQL query to fetch user data based on the user ID
                String query = "SELECT Username, DisplayPicture, FirstName, LastName, Email " +
                        "FROM users " +
                        "WHERE UserID = ?";

                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, userId);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Extract user data from the result set
                    String username = resultSet.getString("Username");
                    byte[] displayPicture = resultSet.getBytes("DisplayPicture");
                    String firstName = resultSet.getString("FirstName");
                    String lastName = resultSet.getString("LastName");
                    String email = resultSet.getString("Email");
                    if (displayPicture != null && displayPicture.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(displayPicture, 0, displayPicture.length);
                        userData = new UserData(username, bitmap, firstName, lastName, email);
                    }
                    else
                    {
                        Bitmap bitmap = null;
                        userData = new UserData(username, bitmap, firstName, lastName, email);
                    }

                }

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return userData;
        }


        private String name;
        private String userName;
        private String surname;
        private Bitmap bytes;
        private String email;
        @Override
        protected void onPostExecute(UserData userData) {
            if (userData != null) {
                name = userData.getFirstName();
                userName = userData.getUsername();
                surname = userData.getLastName();
                bytes= userData.getDisplayPicture();
                email = userData.getEmail();
                TextView txtUserName = findViewById(R.id.txtUserName);
                TextView txtName = findViewById(R.id.txtName);
                TextView txtEmail = findViewById(R.id.txtEmail);
                CircleImageView imageView = findViewById(R.id.imgDisplayPicture);

                txtUserName.setText(userName);
                txtName.setText( name + " " + surname);
                txtEmail.setText(email);
                if (bytes!=null)
                imageView.setImageBitmap(bytes);
                else
                {
                    imageView.setImageResource(R.drawable.defualtuser);

                }
            }
        }
    }

    // Create a UserData class to hold the fetched user data
    private class UserData {
        String username;
        Bitmap displayPicture;
        String firstName;
        String lastName;
        String email;

        public String getUsername() {
            return username;
        }

        public Bitmap getDisplayPicture() {
            return displayPicture;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public UserData(String username, Bitmap displayPicture, String firstName, String lastName, String email) {
            this.username = username;
            this.displayPicture = displayPicture;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
    }
}
