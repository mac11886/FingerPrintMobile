package com.example.fingerprinttest.model;

import android.text.Editable;

import com.google.gson.annotations.SerializedName;

import java.io.File;
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

    public User(String name, int age, String interest, String imguser, String fingerprint) {
        this.name = name;
        this.age = age;
        this.interest = interest;
        this.imguser = imguser;
        this.fingerprint = fingerprint;

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", interest='" + interest + '\'' +
                ", imguser='" + imguser + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
