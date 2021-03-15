package com.example.fingerprinttest.model;

public class EditFingerprint {
    public EditFingerprint(int id, String fingerprint, String typeOfFingerprint) {
        this.id = id;
        this.fingerprint = fingerprint;
        this.typeOfFingerprint = typeOfFingerprint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    int id ;
    String fingerprint ;

    public String getTypeOfFingerprint() {
        return typeOfFingerprint;
    }

    public void setTypeOfFingerprint(String typeOfFingerprint) {
        this.typeOfFingerprint = typeOfFingerprint;
    }

    String typeOfFingerprint;

}
