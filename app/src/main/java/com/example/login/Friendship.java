package com.example.login;

import java.io.Serializable;

public class Friendship implements Serializable {

            private int YourID;
            private int TheirID;


    public Friendship(int yourID, int theirID) {
        YourID = yourID;
        TheirID = theirID;

    }

    public int getYourID() {
        return YourID;
    }

    public void setYourID(int yourID) {
        YourID = yourID;
    }

    public int getTheirID() {
        return TheirID;
    }

    public void setTheirID(int theirID) {
        TheirID = theirID;
    }


}
