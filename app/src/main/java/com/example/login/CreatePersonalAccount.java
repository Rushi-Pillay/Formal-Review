package com.example.login;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
    EditText uName;
    EditText Name;
    EditText Sur ;
    EditText id ;
    EditText Email ;
    EditText Pass ;
    Drawable border, normalborder;

    String username, name, surname, idnum, email, password,formattedDOB;
    int UserID;
    Statement statement;
    Connection connection;
    boolean found =false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_personal_account);
        border = getDrawable(R.drawable.edit_text_border_red);

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
        if (username.equals("")) {
        Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
        uName.setBackground(border); // Set a red border
    } if (name.equals("")) {
        Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
        Name.setBackground(border); // Set a red border
    } if (surname.equals("")) {
        Toast.makeText(getApplicationContext(), "Please enter a surname", Toast.LENGTH_SHORT).show();
        Sur.setBackground(border); // Set a red border
    } if (idnum.equals("")) {
        Toast.makeText(getApplicationContext(), "Please enter an ID number", Toast.LENGTH_SHORT).show();
        id.setBackground(border); // Set a red border
    }if (email.equals("")) {
        Toast.makeText(getApplicationContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
        Email.setBackground(border); // Set a red border
    } if (password.equals("")) {
        Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
        Pass.setBackground(border); // Set a red border
    } if (!username.isEmpty() && !name.isEmpty() && !surname.isEmpty() && !idnum.isEmpty() && !email.isEmpty() && !password.isEmpty()){
        // All fields have values, reset borders to default or proceed with further processing
        uName.setBackground(null);
        Name.setBackground(null);
        Sur.setBackground(null);
        id.setBackground(null);
        Email.setBackground(null);
        Pass.setBackground(null);
        // Continue with your logic or initiate some action
        try {
            String insertQuery = "SELECT * FROM Users WHERE Email = '" + email + "';";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet  rs = statement.executeQuery(insertQuery);
            if (rs.next()){
                int aid = rs.getInt("UserID");
                String title = rs.getString("Email");
                errorText.setTextColor(Color.parseColor("#FF0000"));
                Toast.makeText(this, "Please use a different email, this one is already in use", Toast.LENGTH_SHORT).show();
            }
            else {
                errorText.setTextColor(Color.parseColor("#000000"));
                String insQuery = "INSERT INTO Users (Username, FirstName, LastName, DateOfBirth, Email, Password, DisplayPicture) VALUES" +
                        "('" + username + "', '" + name + "', '" + surname + "', '" + formattedDOB + "', '" + email + "', '" + password + "','null');";
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.executeUpdate(insQuery);
                Toast.makeText(this,"Account Created!",Toast.LENGTH_LONG).show();
            }



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

}
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error Message", text);
            clipboard.setPrimaryClip(clip);
        }
    }


    private void getData() {


        uName = findViewById(R.id.usernameEditText);
        Name = findViewById(R.id.nameEditText);
        Sur = findViewById(R.id.surnameEditText);
        id = findViewById(R.id.IDEditText);
        Email = findViewById(R.id.edtEmail);
        Pass = findViewById(R.id.edtPass);

        username = uName.getText().toString();


        name = Name.getText().toString();


        surname = Sur.getText().toString();


        idnum = id.getText().toString();
        if (idnum==""){
            String yearPart = idnum.substring(0, 2);
            String monthPart = idnum.substring(2, 4);
            String dayPart = idnum.substring(4, 6);
            formattedDOB = yearPart + "-" + monthPart + "-" + dayPart;
        }


        email = Email.getText().toString();


        password = Pass.getText().toString();
    }

}

