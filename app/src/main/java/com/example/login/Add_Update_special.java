package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Add_Update_special extends AppCompatActivity {
int SpecialID;
TextView edtName,edtDescrip;
    String sname ;
    String sdescrip ;
    ImageButton BTNBaloons,BTNBeer,BTNCocktail;
    int blueColorValue;
    int newImageVal;
    int busID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        blueColorValue = Color.parseColor("#0356fc");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_special);
        SharedPreferences sharedPref2 = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
        busID = sharedPref2.getInt("businessID", -1);
        SharedPreferences sharedPref3 = getSharedPreferences("SpecialsPrefs", Context.MODE_PRIVATE);
        int specialID = sharedPref3.getInt("SpecialID", -1);
        edtName = findViewById(R.id.SEDTName);
        edtDescrip = findViewById(R.id.SEDTDescription);
        BTNBaloons=findViewById(R.id.imageButton3);
        BTNBeer=findViewById(R.id.imageButton4);
        BTNCocktail=findViewById(R.id.imageButton5);
        if (SpecialID != -1){
            new RetrieveSpecialTask().execute();
        }
    }

    public void BalloonsOnclick(View view) {
        BTNBaloons.setBackgroundColor(blueColorValue);
        BTNCocktail.setBackgroundColor(0);
        BTNBeer.setBackgroundColor(0);
        newImageVal=2;
    }

    public void BeerOnclick(View view) {
        BTNBeer.setBackgroundColor(blueColorValue);
        BTNCocktail.setBackgroundColor(0);
        BTNBaloons.setBackgroundColor(0);
        newImageVal=3;
    }

    public void CocktailOnclick(View view) {
        BTNCocktail.setBackgroundColor(blueColorValue);
        BTNBaloons.setBackgroundColor(0);
        BTNBeer.setBackgroundColor(0);
        newImageVal=1;
    }

    public void Update_AddSpecial(View view) {
        if (SpecialID != -1){
            new InsertDataTask().execute(sname,sdescrip);
        }else{
            new UpdateDataTask().execute(sname,sdescrip);
        }
    }
    private class UpdateDataTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Connection dbconnect = com.example.login.DatabaseConnection.getInstance().getConnection();
            String name = params[0];
            String descrip = params[1];
            String sql ="UPDATE specials SET specName = ?, specDescription = ?, specImg = ? WHERE businessID = ?";

            try {
                PreparedStatement preparedStatement = dbconnect.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, descrip);
                preparedStatement.setInt(3, newImageVal);
                preparedStatement.setInt(4, busID);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(Add_Update_special.this, "Account Updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Add_Update_special.this, "Error updating account.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class InsertDataTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Connection dbconnect = com.example.login.DatabaseConnection.getInstance().getConnection();
            String name = params[0];
            String descrip = params[1];
            String sql = "INSERT INTO specials (specName, specDescription, specImg, businessID) VALUES (?, ?, ?, ?)";
            try {
                PreparedStatement preparedStatement = dbconnect.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, descrip);
                preparedStatement.setInt(3, newImageVal);
                preparedStatement.setInt(4, busID);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(Add_Update_special.this, "Record Inserted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Add_Update_special.this, "Error inserting record.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class RetrieveSpecialTask extends AsyncTask<Void, Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Connection connection = com.example.login.DatabaseConnection.getInstance().getConnection();

            try {
                String selectQuery = "SELECT * FROM specials WHERE specID ="+SpecialID+";";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    sname = resultSet.getString("specName");
                    sdescrip = resultSet.getString("specDescription");
                    int imgid = resultSet.getInt("specImg");
                    if (imgid > 0) {
                        if (imgid==1){
                            //1 is cocktail
                            BTNCocktail.setBackgroundColor(blueColorValue);
                            BTNBaloons.setBackgroundColor(0);
                            BTNBeer.setBackgroundColor(0);
                        }
                        if (imgid==2){
                            // is balloons
                            BTNBaloons.setBackgroundColor(blueColorValue);
                            BTNCocktail.setBackgroundColor(0);
                            BTNBeer.setBackgroundColor(0);
                        }
                        if (imgid==3){
                            // Beer
                            BTNBeer.setBackgroundColor(blueColorValue);
                            BTNCocktail.setBackgroundColor(0);
                            BTNBaloons.setBackgroundColor(0);
                        }
                    }
                    edtDescrip.setText(sdescrip);
                    edtName.setText(sname);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute() {
            edtDescrip.setText(sdescrip);
            edtName.setText(sname);
        }
    }
}