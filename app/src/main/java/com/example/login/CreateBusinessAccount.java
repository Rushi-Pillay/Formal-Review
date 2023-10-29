package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateBusinessAccount extends AppCompatActivity {
    EditText edtName, edtLocation, edtEmail, edtPass,edtContact;
    Button club, bar, res;
    Connection connection;
    String selectedBusType ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business_account);
        makeNice();
        edtName = findViewById(R.id.edtBusName);
        edtLocation = findViewById(R.id.edtLocation);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        edtContact = findViewById(R.id.edtContactNumber);
        club = findViewById(R.id.btnClub);
        bar = findViewById(R.id.btnBar);
        res = findViewById(R.id.btnRes);

        club.setBackgroundColor(Color.GRAY);
        bar.setBackgroundColor(Color.GRAY);
        res.setBackgroundColor(Color.GRAY);

        club.setOnClickListener(v->{
            selectedBusType = "Club";
            club.setBackgroundColor(Color.BLACK);
            bar.setBackgroundColor(Color.GRAY);
            res.setBackgroundColor(Color.GRAY);

        });
        bar.setOnClickListener(v->{
            selectedBusType = "Bar";
            bar.setBackgroundColor(Color.BLACK);
            club.setBackgroundColor(Color.GRAY);
            res.setBackgroundColor(Color.GRAY);

        });
        res.setOnClickListener(v->{
            selectedBusType = "Restaurant";
            res.setBackgroundColor(Color.BLACK);
            bar.setBackgroundColor(Color.GRAY);
            club.setBackgroundColor(Color.GRAY);
        });


        connection = DatabaseConnection.getInstance().getConnection();
    }

    public void createBusinessAccount(View view) {
        String name = edtName.getText().toString();
        String location = edtLocation.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        String contactNum = edtContact.getText().toString();

        if (!name.isEmpty() && !location.isEmpty() && !email.isEmpty() && !password.isEmpty() && selectedBusType != null && !contactNum.isEmpty()) {
            checkBusinessDetails(name, contactNum, email, password, selectedBusType, location);
        } else {
            Toast.makeText(this, "Please Fill in all the information!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkBusinessDetails(String name, String contactNum, String email, String password, String busType, String location) {
        new CheckBusinessDetailsTask().execute(name, contactNum, email, password, busType, location);
    }

    private class CheckBusinessDetailsTask extends AsyncTask<String, Void, List<String>> {
        String name, contactNum, email, password, busType, location;
        @Override
        protected List<String> doInBackground(String... params) {
            name = params[0];
            contactNum = params[1];
            email = params[2];
            password = params[3];
            busType = params[4];
            location = params[5];

            String emailSql = "SELECT COUNT(*) FROM Business WHERE Email = ?";
            String nameSql = "SELECT COUNT(*) FROM Business WHERE BusinessName = ?";
            String contactSql = "SELECT COUNT(*) FROM Business WHERE ContactNumber = ?";

            List<String> existsList = new ArrayList<>();

            try {
                PreparedStatement emailStatement = connection.prepareStatement(emailSql);
                emailStatement.setString(1, email);
                ResultSet emailResultSet = emailStatement.executeQuery();
                if (emailResultSet.next() && emailResultSet.getInt(1) > 0) {
                    existsList.add("Email");
                }
                emailResultSet.close();
                emailStatement.close();

                PreparedStatement nameStatement = connection.prepareStatement(nameSql);
                nameStatement.setString(1, name);
                ResultSet nameResultSet = nameStatement.executeQuery();
                if (nameResultSet.next() && nameResultSet.getInt(1) > 0) {
                    existsList.add("Business Name");
                }
                nameResultSet.close();
                nameStatement.close();

                PreparedStatement contactStatement = connection.prepareStatement(contactSql);
                contactStatement.setString(1, contactNum);
                ResultSet contactResultSet = contactStatement.executeQuery();
                if (contactResultSet.next() && contactResultSet.getInt(1) > 0) {
                    existsList.add("Contact Number");
                }
                contactResultSet.close();
                contactStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
                copyToClipboard(e.getMessage());
            }

            return existsList;
        }

        @Override
        protected void onPostExecute(List<String> existsList) {
            if (!existsList.isEmpty()) {
                String errorMessage = "The following already exist: " + TextUtils.join(", ", existsList);
                Toast.makeText(CreateBusinessAccount.this, errorMessage, Toast.LENGTH_SHORT).show();
                highlightEditTextFields(existsList);
            } else {
                new InsertDataTask().execute(name, location, email, password, busType, contactNum);
            }
        }
    }

    private void highlightEditTextFields(List<String> fields) {
        if (fields.contains("Email")) {
            edtEmail.setTextColor(Color.RED);
        }
        if (fields.contains("Business Name")) {
            edtName.setTextColor(Color.RED);
        }
        if (fields.contains("Contact Number")) {
            edtContact.setTextColor(Color.RED);
        }
    }



    private void makeNice() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        // Make the navigation bar immersive
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        // Change the status bar icons color to black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private class InsertDataTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String name = params[0];
            String location = params[1];
            String email = params[2];
            String password = params[3];
            String busType = params[4];
            String contactTest = params[5];

            String sql = "INSERT INTO Business (Email, Password, BusinessName, BusType, Location, ContactNumber) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";


            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, busType);
                preparedStatement.setString(5, location);
                preparedStatement.setString(6,contactTest);

                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                copyToClipboard(e.getMessage());
                // Toast.makeText(CreateBusinessAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CreateBusinessAccount.this, "Account Created!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateBusinessAccount.this, MainActivity.class);
                finish();
                startActivity(intent);

            } else {
                Toast.makeText(CreateBusinessAccount.this, "Error creating account.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error Message", text);
            clipboard.setPrimaryClip(clip);
        }
    }
}
