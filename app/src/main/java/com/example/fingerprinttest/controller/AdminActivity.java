package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

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
    ImageView goToAdmin, goToRegister, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        goToAdmin = (ImageView) findViewById(R.id.goToAdmin);
        goToRegister = (ImageView) findViewById(R.id.goToRegister);
        backBtn = (ImageView) findViewById(R.id.backBtnAdmin);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        goToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://ta.kisrateam.com/login";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);


//                WebView webView = new WebView(v.getContext());
//                setContentView(webView);
//                webView.loadUrl("https://ta.kisrateam.com/login");
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