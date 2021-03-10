package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.services.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditFingerprintActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fingerprint);

        recyclerView = findViewById(R.id.userList);
        String s1[] = {"asd", "ZXC", "QWE"};
        UserAdapter userAdapter = new UserAdapter(this, s1);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}