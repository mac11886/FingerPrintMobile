package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fingerprinttest.R;

public class FirstLoginActivity extends AppCompatActivity {

    EditText emailText ,passwordText;
    Button loginBtn;
    TextView signUp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);


        emailText = findViewById(R.id.inputEmail);
        passwordText = findViewById(R.id.inputPassword);
        loginBtn = findViewById(R.id.loginBtn);
        signUp = findViewById(R.id.signupText);



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FirstLoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }





}