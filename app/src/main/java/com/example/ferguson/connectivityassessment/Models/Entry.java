package com.example.ferguson.connectivityassessment.Models;

public class Entry {
    private String school_name;
    private double school_latitude;
    private double school_longitude;
    private String date_of_entry;

    public Entry(String school_name, double school_latitude, double school_longitude, String date_of_entry) {
        this.school_name = school_name;
        this.school_latitude = school_latitude;
        this.school_longitude = school_longitude;
        this.date_of_entry = date_of_entry;
    }

    public Entry() {
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public double getSchool_latitude() {
        return school_latitude;
    }

    public void setSchool_latitude(double school_latitude) {
        this.school_latitude = school_latitude;
    }

    public double getSchool_longitude() {
        return school_longitude;
    }

    public void setSchool_longitude(double school_longitude) {
        this.school_longitude = school_longitude;
    }

    public String getDate_of_entry() {
        return date_of_entry;
    }

    public void setDate_of_entry(String date_of_entry) {
        this.date_of_entry = date_of_entry;
    }
}
