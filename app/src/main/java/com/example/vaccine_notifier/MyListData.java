package com.example.vaccine_notifier;

import java.util.Date;

public class MyListData {
    private String centre;
    private String availability;
    private String date;
    public MyListData() {
       centre="";
       availability="";
       date="";
    }
    public String getCentre() {
        return centre;
    }
    public void setCentre(String centre) {
        this.centre = centre;
    }
    public String getAvailability() {
        return availability;
    }
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
