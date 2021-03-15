package com.example.fingerprinttest.model;


import java.sql.Timestamp;

public class User {
    public User(int id) {
        this.id = id;
    }

    private int id;
    private String name;
    private String birthday ;
    private String group ;
    private String jobposition;
    private String interest;
    private String imguser;
    private String fingerprint;

    public String getFore_fingerprint() {
        return fore_fingerprint;
    }

    public void setFore_fingerprint(String fore_fingerprint) {
        this.fore_fingerprint = fore_fingerprint;
    }

    private String fore_fingerprint;
    private Timestamp updated_at;
    private Timestamp created_at;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getJobposition() {
        return jobposition;
    }

    public void setJobposition(String jobposition) {
        this.jobposition = jobposition;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getImguser() {
        return imguser;
    }

    public void setImguser(String imguser) {
        this.imguser = imguser;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }


    public User(String name, String birthday, String group, String jobposition, String interest, String imguser, String fingerprint,String fore_fingerprint) {
        this.name = name;
        this.birthday = birthday;
        this.group = group;
        this.jobposition = jobposition;
        this.interest = interest;
        this.imguser = imguser;
        this.fingerprint = fingerprint;
        this.fore_fingerprint = fore_fingerprint;
    }
}
