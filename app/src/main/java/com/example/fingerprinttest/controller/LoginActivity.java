package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.Admin;
import com.example.fingerprinttest.services.JsonPlaceHolderApi;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {


    EditText emailText, passwordText;
    TextView signIn;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    int code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = findViewById(R.id.email_admin);
        passwordText = findViewById(R.id.password_admin);
        signIn = findViewById(R.id.sign_in);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
                if (code != 200){
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!")
                            .show();
                }
                else {
                    Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    public void checkUser() {

        Admin admin = new Admin(emailText.getText().toString(),passwordText.getText().toString());
        Call<Admin> call = jsonPlaceHolderApi.checkUsers(admin);
        call.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(Call<Admin> call, Response<Admin> response) {
                Admin admin1 = response.body();
                code  = response.code();
            }

            @Override
            public void onFailure(Call<Admin> call, Throwable t) {

            }
        });

    }
}