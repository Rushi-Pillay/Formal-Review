package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
                String selectQuery = "SELECT Username, FirstName, LastName, DisplayPicture FROM users " +
                        "WHERE users.UserID IN (SELECT friendships.UserID2 FROM friendships WHERE friendships.UserID1 = 33)";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                  String username = resultSet.getString("Username");
                  String Firstname = resultSet.getString("FirstName");
                  String LastName = resultSet.getString("LastName");
                    byte[] imageData1 = resultSet.getBytes("DisplayPicture");
                    Bitmap bitmap1  = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);

                    Users temp = new Users(username,Firstname,LastName);
                   temp.setImage1(bitmap1);
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
            List<Users> fetchedFreinds = new ArrayList<>();

            try {
                String selectQuery = "SELECT Username, FirstName, LastName, DisplayPicture FROM Users " +
                        "WHERE Username LIKE ? " +
                        "OR FirstName LIKE ? " +
                        "OR LastName LIKE ? " +
                        "LIMIT 10";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setString(1, "%" + searchTerm + "%");
                preparedStatement.setString(2, "%" + searchTerm + "%");
                preparedStatement.setString(3, "%" + searchTerm + "%");

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String username = resultSet.getString("Username");
                    String Firstname = resultSet.getString("FirstName");
                    String LastName = resultSet.getString("LastName");
                    byte[] imageData1 = resultSet.getBytes("DisplayPicture");

                    if (imageData1 != null && imageData1.length > 0) {
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                        Users temp = new Users(username, Firstname, LastName);
                        temp.setImage1(bitmap1);
                        fetchedFreinds.add(temp);
                    }
                    else {
                        Users temp = new Users(username, Firstname, LastName);
                        fetchedFreinds.add(temp);
                    }


                }

                resultSet.close();
                preparedStatement.close();
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
                adapter1.notifyDataSetChanged();
            }
        }
    }

}




