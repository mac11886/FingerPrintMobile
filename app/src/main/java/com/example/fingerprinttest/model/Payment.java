package com.example.fingerprinttest.model;

public class Payment {

    public Payment(String id, String name, String payment) {
        this.id = id;
        this.name = name;
        this.payment = payment;
    }

    String id;
    String name;
    String payment;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPayment() {
        return payment;
    }
}
