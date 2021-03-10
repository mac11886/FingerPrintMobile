package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.Api;
import com.example.fingerprinttest.services.LoadingDialog;
import com.example.fingerprinttest.services.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditFingerprintActivity extends AppCompatActivity {

    List<User> users;
    RecyclerView recyclerView;
    Api api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fingerprint);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        getPosts();
        LoadingDialog loadingDialog = new LoadingDialog(EditFingerprintActivity.this);
        loadingDialog.startLoadingDialog();
        final Handler someHandler1 = new Handler(getMainLooper());
        someHandler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
                createUserList();
            }
        }, 2000);


    }

    public void createUserList(){

        recyclerView = findViewById(R.id.userList);

        UserAdapter userAdapter = new UserAdapter(this, users);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    public void getPosts() {
        try {
            Call<List<User>> call = api.getPost();
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    users = response.body();
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {


                }
            });
        } catch (Exception e) {
            Toast.makeText(EditFingerprintActivity.this, "error", Toast.LENGTH_SHORT);
        }


    }
}