package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.Payment;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.JsonPlaceHolderApi;
import com.example.fingerprinttest.services.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Thread.sleep;

public class AdminActivity extends AppCompatActivity {

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    RecyclerView recycler_view;
    UserAdapter adapter;
    List<User> users;
    List<User> ListUser;
    User user;
    ArrayList copy;
    Button linkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //connectApi
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
//        getPosts();

//        recycler_view = findViewById(R.id.recycler_view);
//        setRecyclerView();
        linkBtn = (Button) findViewById(R.id.linkBtn);

        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.google.com";
                Intent browserIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });





    }


    private void setRecyclerView() {
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(this, getList());
        recycler_view.setAdapter(adapter);
    }

    private List<User> getList() {
        getPosts();
        return users;
    }


    //get API   type synchronous
    public void getPosts() {
        Call<List<User>> call = jsonPlaceHolderApi.getPost();
        try {
            Response<List<User>> response = call.execute();
            if (!response.isSuccessful()) {
                return;
            }
            users = response.body();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}