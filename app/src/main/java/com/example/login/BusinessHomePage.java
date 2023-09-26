package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class BusinessHomePage extends AppCompatActivity {
    private int businessID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home_page);
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE);
        businessID = sharedPref.getInt("businessID", -1);


    }
}