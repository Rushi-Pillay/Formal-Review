package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.AlarmManagerCompat;

import android.app.AlarmManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;

    Connection connection;
    TextView btnBus ;
    TextView btnPersonal;
    DatabaseConnection database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeNice();


        connectToDB();

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.edtPass);
        loginButton = findViewById(R.id.BtnLogin);


        btnBus = findViewById(R.id.btnBusiness);
        btnBus.setOnClickListener(event->
        {
            Intent intent = new Intent(MainActivity.this,CreateBusinessAccount.class);
            startActivity(intent);
        });

        btnPersonal = findViewById(R.id.btnPersonal);
        btnPersonal.setOnClickListener(event->
        {
            Intent intent = new Intent(MainActivity.this,CreatePersonalAccount.class);
            startActivity(intent);
        });

    }
    private class ConnectToDBTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            database = DatabaseConnection.getInstance();
            connection = database.getConnection();
            return database.getRes();
        }
    }
    private void connectToDB() {
        new ConnectToDBTask().execute();
    }



    private void makeNice() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.WHITE);
        }

        // Make the navigation bar immersive
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Change the status bar icons color to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void LoginClicked(View view) throws SQLException {
        String email = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        new IsBusinessTask().execute(email, password);


    }
    private class IsBusinessTask extends AsyncTask<String, Void, Boolean> {
        public String email ;
        public String password ;
        @Override
        protected Boolean doInBackground(String... params) {
            email = params[0];
            password = params[1];
            String selectQuery = "SELECT Email FROM Business WHERE Email = ?";

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setString(1, email);

                ResultSet resultSet = preparedStatement.executeQuery();

                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isBusiness) {
            if (isBusiness) {
                new BusinessLoginAsyncTask().execute(email,password);
            } else {
                // You can directly access the email and password here
                new PersonalLoginAsyncTask().execute(email, password);
            }
        }
    }
    private class BusinessLoginAsyncTask extends AsyncTask<String, Void, Integer>{
        @Override
        protected Integer doInBackground(String... params)
        {
            String email = params[0];
            String password = params[1];
            String selectQuery = "SELECT Email, Password,BusinessID FROM business WHERE Email = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setString(1, email);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("Password");
                    if (retrievedPassword.equals(password)) {
                        String businessID = resultSet.getString("BusinessID");
                        SharedPreferences sharedPref2 = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = sharedPref2.edit();
                        editor2.putInt("businessID", Integer.parseInt(businessID));
                        editor2.apply();

                        return 1; // Successful login
                    } else {
                        return 0; // Incorrect password
                    }
                } else {
                    return -1; // User not found
                }

            } catch (Exception e) {
                e.printStackTrace();
                return -2; // Error occurred
            }

        }
        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    // Clear text and reset text color
                    Intent intent = new Intent(MainActivity.this , BusinessHomePage.class);
                    startActivity(intent);
                    finish();
//                    Toast.makeText(MainActivity.this, "LOGGED IN", Toast.LENGTH_LONG).show();
                    break;
                case 0:
                    // Incorrect password, change text color to red
                    //  passwordEditText.setText("");

                    passwordEditText.setTextColor(Color.RED);
                    Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                    break;
                case -1:
                    // User not found, change text color to red
                    //   usernameEditText.setText("");
                    usernameEditText.setTextColor(Color.RED);
                    // passwordEditText.setText("");
                    passwordEditText.setTextColor(Color.RED);
                    Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_LONG).show();
                    break;
                case -2:
                    // An error occurred, change text color to red
                    usernameEditText.setTextColor(Color.RED);
                    passwordEditText.setTextColor(Color.RED);
                    Toast.makeText(MainActivity.this, "Error Happened", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
    private class PersonalLoginAsyncTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            String selectQuery = "SELECT Email, Password,UserID FROM Users WHERE Email = ?";

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setString(1, email);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("Password");
                    if (retrievedPassword.equals(password)) {
                        String userID = resultSet.getString("UserID");
                        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("user_id", Integer.parseInt(userID));
                        editor.apply();


                        return 1; // Successful login
                    } else {
                        return 0; // Incorrect password
                    }
                } else {
                    return -1; // User not found
                }

            } catch (Exception e) {
                e.printStackTrace();
                return -2; // Error occurred
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case 1:
                    // Clear text and reset text color
                    Intent intent = new Intent(MainActivity.this , MainPage.class);
                    startActivity(intent);
                    finish();
//                    Toast.makeText(MainActivity.this, "LOGGED IN", Toast.LENGTH_LONG).show();
                    break;
                case 0:
                    // Incorrect password, change text color to red
                    //  passwordEditText.setText("");

                    passwordEditText.setTextColor(Color.RED);
                    Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_LONG).show();
                    break;
                case -1:
                    // User not found, change text color to red
                    //   usernameEditText.setText("");
                    usernameEditText.setTextColor(Color.RED);
                    // passwordEditText.setText("");
                    passwordEditText.setTextColor(Color.RED);
                    Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_LONG).show();
                    break;
                case -2:
                    // An error occurred, change text color to red
                    usernameEditText.setTextColor(Color.RED);
                    passwordEditText.setTextColor(Color.RED);
                    Toast.makeText(MainActivity.this, "Error Happened", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}