package com.example.login;

import static com.example.login.R.id.DisplayIM;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;

import android.media.Image;


import android.view.View;


import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.sql.*;



import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.os.StrictMode;

import android.widget.EditText;


public class EditUserprofile extends AppCompatActivity {
    private static final int REQUEST_CODE_GALLERY = 101;
    EditText editText = null;
    EditText uName,Name,Sur,Email;
    String username, name, surname, email,userid;
    Connection connection;
    int userID;
    Statement statement;
    Bitmap bitmap = null;
    private ImageView imageView;
    ImageView displaypicIM;

    public static final int REQUEST_CODE_GET_IMAGE = 100;
    Image dp;
    private static final int REQUEST_IMAGE_PICK = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_userprofile);
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userID = sharedPref.getInt("user_id", -1);
        uName = findViewById(R.id.usernameEditText);
        Name = findViewById(R.id.nameEditText);
        Email = findViewById(R.id.emailEditText);
        Sur = findViewById(R.id.surnameEditText);
        imageView= findViewById(R.id.DisplayIM);
        new RetrieveImageTask().execute(userID);
        try {
            setdata();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void updateinfo(View view) throws SQLException {
        getData();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://156.155.64.210:3306/projectdb", "angus", "angus");
            String updateQuery = "UPDATE users SET " +
                    "Username = '" + username + "', " +
                    "FirstName = '" + name + "', " +
                    "LastName = '" + surname + "', " +
                    "Email = '" + email + "' " +
                    "WHERE UserID = '" + userid + "';";

            Statement statement = connection.createStatement();
            statement.executeUpdate(updateQuery);

            Toast.makeText(this, "Profile updated", Toast.LENGTH_LONG).show();
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error disconnecting...", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not connect...", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Error Message", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private void getData() throws SQLException {
        username = uName.getText().toString();
        name = Name.getText().toString();
        surname = Sur.getText().toString();
        email = Email.getText().toString();
    }

    private void setdata() throws SQLException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://156.155.64.210:3306/projectdb", "angus", "angus");
            //endregion
            //Toast.makeText(this, "Connected...", Toast.LENGTH_SHORT).show();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String selectQuery = "SELECT * FROM Users WHERE UserID = '"+userid+"'";
            //Toast.makeText(this,selectQuery,Toast.LENGTH_LONG).show();
            ResultSet resultSet =statement.executeQuery(selectQuery);

            if (resultSet.next()) {
                String username = resultSet.getString("Username");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String email = resultSet.getString("Email");
                //Blob img =  resultSet.getBlob("DisplayPicture");

                uName.setText(username);
                Name.setText( firstName);
                Sur.setText(lastName);
                Email.setText( email);


//                displaypicIM.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                displaypicIM.setAdjustViewBounds(true);
//                displaypicIM.setImageBitmap(BitmapFactory.decodeStream((InputStream) img));
            } else {
                System.out.println("No user found with the specified UserID.");
            }

            connection.close();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private class RetrieveImageTask extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Integer... integers) {

            Connection connection = com.example.login.DatabaseConnection.getInstance().getConnection();


            try {
                String selectQuery = "SELECT DisplayPicture FROM Users WHERE Userid = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, userID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    byte[] imageData = resultSet.getBytes("DisplayPicture");
                    if (imageData!=null)
                        bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                } else {

                    Log.d("RetrieveImageTask", "Image not found for the specified user ID.");
                }

                resultSet.close();
                preparedStatement.close();
                //connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            else
            {
            }

        }
    }
    private class addimagetouser extends AsyncTask<byte[], Void, Boolean> {

        // Declare EventID at the class level
        private byte[] imagebytearr;

        @Override
        protected Boolean doInBackground(byte[]... eventIDs) {
            imagebytearr = eventIDs[0];
            Connection connection = com.example.login.DatabaseConnection.getInstance().getConnection();
            String insertQuery = "UPDATE users Set DisplayPicture= ? WHERE UserID = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(2, userID);
                preparedStatement.setBytes(1, imagebytearr);
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
                Toast.makeText(EditUserprofile.this, "image added successfully", Toast.LENGTH_SHORT).show();
                new RetrieveImageTask().execute();
            } else {
                Toast.makeText(EditUserprofile.this, "Failed to add image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void selectimg(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                // Convert the selected image into a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                // Convert the Bitmap into a byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // TODO: Use the byteArray as needed
                new addimagetouser().execute(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

