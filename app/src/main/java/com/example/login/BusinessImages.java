package com.example.login;

import android.graphics.Bitmap;

import java.io.Serializable;

public class BusinessImages implements Serializable {
    Bitmap image;
    public int imageID;
    public BusinessImages(Bitmap image){
        this.image=image;
    }
    public Bitmap getImage(){
        return image;
    }

}
