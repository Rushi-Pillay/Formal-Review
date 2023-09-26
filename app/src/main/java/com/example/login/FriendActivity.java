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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    private FriendAdapter adapter1;
    private FriendAdapter adapter2;
    private List<Users> usersList1;
    private List<Users> usersList2;
    private List<Friendship> friendshipList;
    private Button Search;
    private ImageButton Return;
    private RecyclerView rc1;
    private RecyclerView rc2;
    Connection connection;
    private int userid;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        usersList2 = new ArrayList<>();
        new RetrieveFriendsTask().execute();
        rc2 = findViewById(R.id.FriendRecycler);

//        Users temp = new Users("asdfasdf","aksdf","asgfsadfg");
//        usersList2.add(temp);

        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userid = sharedPref.getInt("user_id", -1);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc2.setLayoutManager(layoutManager2);
        adapter2 = new FriendAdapter(usersList2);
        rc2.setAdapter(adapter2);





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
               // usersList2.add(new Users("sldfkdsaf","sdfsdf","sdfsdf"));
                adapter2.notifyDataSetChanged();
            }
        }
    }




}