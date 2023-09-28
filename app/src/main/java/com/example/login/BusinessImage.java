package com.example.login;

import android.graphics.Bitmap;

public class BusinessImage {
    public BusinessImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    private Bitmap image;
}
