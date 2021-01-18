package com.example.fingerprinttest.model;

public class Attendance {
    public Attendance(int id, String date, String time, String status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    private int id ;
    private String date ;
    private String time;
    private String status;



}
