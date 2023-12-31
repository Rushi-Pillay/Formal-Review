package com.example.login;

import android.graphics.Bitmap;

public class Event {
    private int EventID;
    private String name;
    private String date;
    private String time;

    private String venue;
    private int capacity;
    private boolean age;
    private int recurring;
    private String description;
    private int rating;
    private Bitmap image1;
    private Bitmap image2;
    private Bitmap image3;


    public Event(int eventID, String name, String date, String time,  String venue, int capacity, boolean age, int recurring, String description) {
        EventID = eventID;
        this.name = name;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.capacity = capacity;
        this.age = age;
        this.recurring = recurring;
        this.description = description;

    }

    public int getEventID() {
        return EventID;
    }

    public void setEventID(int eventID) {
        EventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean getAge() {
        return age;
    }

    public void setAge(boolean age) {
        this.age = age;
    }

    public int getRecurring() {
        return recurring;
    }

    public void setRecurring(int recurring) {
        this.recurring = recurring;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Bitmap getImage1() {
        return image1;
    }

    public void setImage1(Bitmap image1) {
        this.image1 = image1;
    }

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
}
