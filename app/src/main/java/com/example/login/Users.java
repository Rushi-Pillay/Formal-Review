package com.example.login;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Users implements Serializable {

        private String Username;
        private String Firstname;
        private String LastName;
        private Bitmap Image1;

    public Users(String username, String firstname, String lastName) {
        Username = username;
        Firstname = firstname;
        LastName = lastName;

    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public Bitmap getImage1() {
        return Image1;
    }

    public void setImage1(Bitmap image1) {
        Image1 = image1;
    }
}
