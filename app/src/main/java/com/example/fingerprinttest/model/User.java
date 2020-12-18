package com.example.fingerprinttest.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String name;
    private int age;
    private String interest;
    private String imguser;
    private String fingerprint;
    private Timestamp updated_at;
    private Timestamp created_at;

    public String getImguser() {
        return imguser;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public int getAge() {
        return age;
    }


    public String getInterest() {
        return interest;
    }



}
