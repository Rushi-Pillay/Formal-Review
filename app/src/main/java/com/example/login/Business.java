package com.example.login;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

public class Business implements Serializable {

        private int BusinessID;
        private String Email;
        private String name;
        private String ContactNumber;
        private String Password;
        private String Specials;
        private int Capacity;
        private String Type;
        private String Location;
        private Bitmap Image1;


    public Business(int businessID, String email, String name, String contactNumber, String password, String specials, int capacity, String type, String location) {
        this.BusinessID = businessID;
        this.Email = email;
        this.name = name;
        this.ContactNumber = contactNumber;
        this.Password = password;
        this.Specials = specials;
        this.Capacity = capacity;
        this.Type = type;
        this.Location = location;

    }

    public int getBusinessID() {
        return BusinessID;
    }

    public void setBusinessID(int businessID) {
        BusinessID = businessID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getSpecials() {
        return Specials;
    }

    public void setSpecials(String specials) {
        Specials = specials;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Bitmap getImage1() {
        return Image1;
    }

    public void setImage1(Bitmap image1) {
        Image1 = image1;
    }
}
