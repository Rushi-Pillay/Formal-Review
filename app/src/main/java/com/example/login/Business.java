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

        private int Capacity;
        private String Type;
        private String Location;
        private Bitmap Image1;
        private Bitmap image2;

    public Bitmap getImage2() {
        return image2;
    }

    public void setImage2(Bitmap image2) {
        this.image2 = image2;
    }

    public Bitmap getImage3() {
        return image3;
    }

    public void setImage3(Bitmap image3) {
        this.image3 = image3;
    }

    public Bitmap getImage4() {
        return image4;
    }

    public void setImage4(Bitmap image4) {
        this.image4 = image4;
    }

    public Bitmap getImage5() {
        return image5;
    }

    public void setImage5(Bitmap image5) {
        this.image5 = image5;
    }

    public Bitmap getImage6() {
        return image6;
    }

    public void setImage6(Bitmap image6) {
        this.image6 = image6;
    }

    public Bitmap getImage7() {
        return Image7;
    }

    public void setImage7(Bitmap image7) {
        Image7 = image7;
    }

    private Bitmap image3;
         private Bitmap image4;
         private Bitmap image5;
        private Bitmap image6;
        private Bitmap Image7;


    public Business(int businessID, String email, String name, String contactNumber, String password,int capacity, String type, String location) {
        this.BusinessID = businessID;
        this.Email = email;
        this.name = name;
        this.ContactNumber = contactNumber;
        this.Password = password;
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
