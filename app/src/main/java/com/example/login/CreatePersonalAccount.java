package com.example.login;

import android.os.Bundle;

import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import java.sql.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.StrictMode;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;


public class CreatePersonalAccount extends AppCompatActivity {

    EditText editText = null;
    TextView text, errorText;
    String username, name, surname, idnum, email, password,formattedDOB;
    int UserID;
    Statement statement;
    Connection connection;
    boolean found =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_personal_account);

        text = (TextView) findViewById(R.id.usernameLabel);
        errorText = (TextView) findViewById(R.id.edtEmail);
         connection = DatabaseConnection.getInstance().getConnection();

        EditText passwordEditText = findViewById(R.id.edtPass);
        editText = findViewById(R.id.nameEditText);



    }

    public void createAccount(View view) {
        getData();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {


            String insertQuery = "SELECT * FROM Users WHERE Email = '" + email + "';";

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet  rs = statement.executeQuery(insertQuery);
            if (rs.next()){
                int aid = rs.getInt("UserID");
                String title = rs.getString("Email");
            }
            else {
                String insQuery = "INSERT INTO Users (Username, FirstName, LastName, DateOfBirth, Email, Password, DisplayPicture) VALUES" +
                        "('" + username + "', '" + name + "', '" + surname + "', '" + formattedDOB + "', '" + email + "', '" + password + "','null');";


                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.executeUpdate(insQuery);
                Toast.makeText(this,"Account Created!",Toast.LENGTH_LONG).show();
            }
            Toast.makeText(this, "Please use a different email, this one is already in use", Toast.LENGTH_SHORT).show();


            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();

            }

        } catch (SQLException e) {
            copyToClipboard(e.toString());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();;

        }


    }
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error Message", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void getData() {


        EditText uName = findViewById(R.id.usernameEditText);

        username = uName.getText().toString();

        EditText Name = findViewById(R.id.nameEditText);
        name = Name.getText().toString();

        EditText Sur = findViewById(R.id.surnameEditText);
        surname = Sur.getText().toString();

        EditText id = findViewById(R.id.IDEditText);
        idnum = id.getText().toString();

        String yearPart = idnum.substring(0, 2);
        String monthPart = idnum.substring(2, 4);
        String dayPart = idnum.substring(4, 6);

        formattedDOB = yearPart + "-" + monthPart + "-" + dayPart;


        EditText Email = findViewById(R.id.edtEmail);
        email = Email.getText().toString();

        EditText Pass = findViewById(R.id.edtPass);
        password = Pass.getText().toString();
    }

}

