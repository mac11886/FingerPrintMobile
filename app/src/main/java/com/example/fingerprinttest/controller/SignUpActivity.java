package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fingerprinttest.R;

public class SignUpActivity extends AppCompatActivity {

    EditText nameText, emailText, passwordText, secondPasswordText;
    Button signUpBtn;
    TextView loginText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameText = findViewById(R.id.inputNameSignUp);
        emailText = findViewById(R.id.inputEmailSignUp);
        passwordText = findViewById(R.id.inputPasswordSignUp);
        secondPasswordText = findViewById(R.id.inputRePasswordSignUp);
        signUpBtn = findViewById(R.id.signUpBtn);
        loginText = findViewById(R.id.loginText);


        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, FirstLoginActivity.class);
                startActivity(intent);
            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });


    }


    public void signUp() {


        if (validate()) {
            return;
        }
        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String secondPassword = secondPasswordText.getText().toString();

        Intent intent = new Intent(SignUpActivity.this,FirstLoginActivity.class);
        Bundle bundle =  new Bundle();
        bundle.putStringArray("signUp",new String[]{name,email,password,secondPassword});
        intent.putExtras(bundle);
        startActivity(intent);



    }


    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();

        String email = emailText.getText().toString();

        String password = passwordText.getText().toString();
        String reEnterPassword = secondPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("ใส่อย่างน้อย 3 ตัวอักษร");
            valid = false;
        } else {
            nameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("กรอก E-mail ไม่ถูกต้อง");
            valid = false;
        } else {
            emailText.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("ใส่รหัสได้ 4-10 ตัว");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            secondPasswordText.setError("รหัส Password ไม่ตรงกัน");
            valid = false;
        } else {
            secondPasswordText.setError(null);
        }

        return valid;
    }
}