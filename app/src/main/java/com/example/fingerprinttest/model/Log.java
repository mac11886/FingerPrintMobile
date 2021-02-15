package com.example.fingerprinttest.model;

public class Log {

    int id ;

    public Log(String screen, String function, String message) {
        this.screen = screen;
        this.function = function;
        this.message = message;
    }

    String screen ;
    String function ;
    String message ;

}
