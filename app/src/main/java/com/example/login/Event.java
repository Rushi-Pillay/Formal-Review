package com.example.login;

import android.graphics.Bitmap;
// this is a change
public class Event {
    private int EventID;
    private String name;
    private String date;
    private String time;
    private Double ticketprice;
    private String venue;
    private int capacity;
    private int age;
    private String recurring;
    private String description;
    private int rating;
    private Bitmap image1;
    private Bitmap image2;
    private Bitmap image3;


    public Event(int eventID, String name, String date, String time, Double ticketprice, String venue, int capacity, int age, String recurring, String description, int rating, Bitmap image1) {
        EventID = eventID;
        this.name = name;
        this.date = date;
        this.time = time;
        this.ticketprice = ticketprice;
        this.venue = venue;
        this.capacity = capacity;
        this.age = age;
        this.recurring = recurring;
        this.description = description;
        this.rating = rating;
        this.image1 = image1;
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

    public Double getTicketprice() {
        return ticketprice;
    }

    public void setTicketprice(Double ticketprice) {
        this.ticketprice = ticketprice;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRecurring() {
        return recurring;
    }

    public void setRecurring(String recurring) {
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
