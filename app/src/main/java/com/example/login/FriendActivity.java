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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    private UserAdapter adapter1;
    private FriendAdapter adapter2;
    private List<Users> usersList1;
    private List<Users> usersList2;

    private Button Search;
    private ImageButton Return;
    private RecyclerView rc1;
    private RecyclerView rc2;
    Connection connection;
    private int userid;
    private TextView tw;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        tw = findViewById(R.id.SeachFriendText);
        usersList2 = new ArrayList<>();
        new RetrieveFriendsTask().execute();
        rc2 = findViewById(R.id.FriendRecycler);

        makeNice();

        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userid = sharedPref.getInt("user_id", -1);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc2.setLayoutManager(layoutManager2);
        adapter2 = new FriendAdapter(usersList2);
        rc2.setAdapter(adapter2);


        rc1= findViewById(R.id.searchRecycler);
        usersList1 = new ArrayList<>();
        adapter1 = new UserAdapter(usersList1);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc1.setLayoutManager(layoutManager1);


        rc1.addItemDecoration(new SpaceItemDecoration(10));
        rc2.addItemDecoration(new SpaceItemDecoration(15));
        rc1.setAdapter(adapter1);
        tw.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called to notify you that, within s, the count characters beginning at start have just replaced old text that had length before.
                new RetrieveUsersTask().execute(s.toString()); // Initiates the AsyncTask with the current text as parameter
            }

            @Override
            public void afterTextChanged(Editable s) {
                new RetrieveUsersTask().execute(s.toString());
            }
        });




    }
    private class RetrieveFriendsTask extends AsyncTask<Void, Void, List<Users>> {
        @Override
        protected List<Users> doInBackground(Void... voids) {

            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Users> fetchedFreinds = new ArrayList<>();

            try {
                String selectQuery = "SELECT UserID,Username, FirstName, LastName, DisplayPicture FROM users " +
                        "WHERE users.UserID IN (SELECT friendships.UserID2 FROM friendships WHERE friendships.UserID1 = 33)";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int tempID = resultSet.getInt("UserID");
                  String username = resultSet.getString("Username");
                  String Firstname = resultSet.getString("FirstName");
                  String LastName = resultSet.getString("LastName");
                    byte[] imageData1 = resultSet.getBytes("DisplayPicture");
                    Bitmap bitmap1  = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);

                    Users temp = new Users(username,Firstname,LastName);
                   temp.setImage1(bitmap1);
                   temp.setUserID(tempID);
                    fetchedFreinds.add(temp);
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
                usersList2.clear();
                usersList2.addAll(fetchedFreinds);
                adapter2.notifyDataSetChanged();
            }
        }
    }

    private class RetrieveUsersTask extends AsyncTask<String, Void, List<Users>> {

        @Override
        protected List<Users> doInBackground(String... params) {
            String searchTerm = params[0];

            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Users> fetchedFriends = new ArrayList<>();

            try {
                String selectQuery = "SELECT UserID,Username, FirstName, LastName, DisplayPicture FROM Users " +
                        "WHERE (Username LIKE ? " +
                        "OR FirstName LIKE ? " +
                        "OR LastName LIKE ?) " +
                        "AND UserID NOT IN (SELECT UserID2 FROM friendships WHERE UserID1 = ?) " +
                        "AND UserID <> ? " + // Exclude the current user
                        "ORDER BY " +
                        "CASE " +
                        "WHEN Username LIKE ? THEN 1 " +
                        "WHEN FirstName LIKE ? THEN 2 " +
                        "WHEN LastName LIKE ? THEN 3 " +
                        "ELSE 4 " +
                        "END " +
                        "LIMIT 10";

                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setString(1, "%" + searchTerm + "%");
                preparedStatement.setString(2, "%" + searchTerm + "%");
                preparedStatement.setString(3, "%" + searchTerm + "%");
                preparedStatement.setInt(4, userid);  // For the friendships subquery.
                preparedStatement.setInt(5, userid);  // To exclude the current user.
                preparedStatement.setString(6, searchTerm + "%");
                preparedStatement.setString(7, searchTerm + "%");
                preparedStatement.setString(8, searchTerm + "%");

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int tempid = resultSet.getInt("UserID");
                    String username = resultSet.getString("Username");
                    String Firstname = resultSet.getString("FirstName");
                    String LastName = resultSet.getString("LastName");
                    byte[] imageData1 = resultSet.getBytes("DisplayPicture");

                    if (imageData1 != null && imageData1.length > 0) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Users temp = new Users(username, Firstname, LastName);
                        temp.setUserID(tempid);
                        temp.setImage1(bitmap1);
                        fetchedFriends.add(temp);
                    } else {
                        Users temp = new Users(username, Firstname, LastName);
                        temp.setUserID(tempid);
                        fetchedFriends.add(temp);
                    }
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return fetchedFriends;
        }

        @Override
        protected void onPostExecute(List<Users> fetchedFriends) {
            if (fetchedFriends != null && !fetchedFriends.isEmpty()) {
                usersList1.clear();
                usersList1.addAll(fetchedFriends);
                adapter1.notifyDataSetChanged();
            }
        }
    }

    public void addFriend(Users user) {
        // Logic to add a friend to the database, you might want to use AsyncTask or any other method
        // For example:
        new AddFriendTask().execute(user);
    }

    private class AddFriendTask extends AsyncTask<Users, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Users... users) {
            Users userToAdd = users[0];
            Connection connection = DatabaseConnection.getInstance().getConnection();
            // SQL statement to insert the friendship relationship in the database
            // Please adjust the query based on your database schema
            String insertQuery = "INSERT INTO friendships(UserID1, UserID2) VALUES(?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(1, userid);
                preparedStatement.setInt(2, userToAdd.getUserID()); // Assuming there's a getUserId method in your Users class
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
                Toast.makeText(FriendActivity.this, "Friend added successfully", Toast.LENGTH_SHORT).show();

                new RetrieveFriendsTask().execute();
                usersList1.clear();
                adapter1.notifyDataSetChanged();
            } else {
                Toast.makeText(FriendActivity.this, "Failed to add friend", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void removeFriend(Users friend) {

        new RemoveFriendTask().execute(friend);
    }

    private class RemoveFriendTask extends AsyncTask<Users, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Users... friends) {
            Users friendToRemove = friends[0];
            Connection connection = DatabaseConnection.getInstance().getConnection();


            String deleteQuery = "DELETE FROM friendships WHERE (UserID1 = ? AND UserID2 = ?) OR (UserID1 = ? AND UserID2 = ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, userid); // Your logged in user's id
                preparedStatement.setInt(2, friendToRemove.getUserID()); // Assuming there's a getUserId method in your Users class
                preparedStatement.setInt(3, friendToRemove.getUserID());
                preparedStatement.setInt(4, userid);
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
                Toast.makeText(FriendActivity.this, "Friend removed successfully", Toast.LENGTH_SHORT).show();
                new RetrieveFriendsTask().execute();
            } else {
                Toast.makeText(FriendActivity.this, "Failed to remove friend", Toast.LENGTH_SHORT).show();
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




