package com.example.login;

import android.graphics.Color;
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
    EditText uName;
    EditText Sur ;
    EditText Name ;
    EditText id ;
    EditText Email;
    EditText Pass ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_personal_account);

        text = (TextView) findViewById(R.id.usernameLabel);
        errorText = (TextView) findViewById(R.id.edtEmail);
         connection = DatabaseConnection.getInstance().getConnection();
        CheckBox showPasswordCheckbox = findViewById(R.id.showPasswordCheckBox);
        EditText passwordEditText = findViewById(R.id.edtPass);
        editText = findViewById(R.id.nameEditText);

        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int selection = passwordEditText.getSelectionEnd();
            if (isChecked) {
                // Show password
                passwordEditText.setTransformationMethod(null);
            } else {
                // Hide password
                passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
            }
            passwordEditText.setSelection(selection);
        });

    }

    public void createAccount(View view) {
        getData();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
            uName.setBackgroundResource(R.drawable.edit_text_border_red); // Set a red border
        } if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
            Name.setBackgroundResource(R.drawable.edit_text_border_red); // Set a red border
        } if (surname.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a surname", Toast.LENGTH_SHORT).show();
            Sur.setBackgroundResource(R.drawable.edit_text_border_red); // Set a red border
        } if (idnum.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter an ID number", Toast.LENGTH_SHORT).show();
            id.setBackgroundResource(R.drawable.edit_text_border_red); // Set a red border
        }if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
            Email.setBackgroundResource(R.drawable.edit_text_border_red); // Set a red border
        } if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
            Pass.setBackgroundResource(R.drawable.edit_text_border_red); // Set a red border
        } if (username.isEmpty() && name.isEmpty() && surname.isEmpty() && idnum.isEmpty() && email.isEmpty() && password.isEmpty()){
            // All fields have values, reset borders to default or proceed with further processing
            uName.setBackgroundResource(R.drawable.default_edit_text_border);
            Name.setBackgroundResource(R.drawable.default_edit_text_border);
            Sur.setBackgroundResource(R.drawable.default_edit_text_border);
            id.setBackgroundResource(R.drawable.default_edit_text_border);
            Email.setBackgroundResource(R.drawable.default_edit_text_border);
            Pass.setBackgroundResource(R.drawable.default_edit_text_border);
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
         Sur = findViewById(R.id.surnameEditText);
         Name = findViewById(R.id.nameEditText);
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

