package com.example.ferguson.connectivityassessment.Models;

public class School {
    private int school_id;
    private String school_name;

    public School(int school_id, String school_name) {
        this.school_id = school_id;
        this.school_name = school_name;
    }

    public School() {
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }
}
