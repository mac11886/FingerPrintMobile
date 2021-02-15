package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.Token;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.AnalyticsApplication;
import com.example.fingerprinttest.services.Api;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Thread.sleep;

public class AdminActivity extends AppCompatActivity {
    private Tracker mTracker;
    private Api api;
    RecyclerView recycler_view;

    String token;
    List<User> users;

    ImageView goToAdmin, goToRegister, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        goToAdmin = (ImageView) findViewById(R.id.goToAdmin);
        goToRegister = (ImageView) findViewById(R.id.goToRegister);
        backBtn = (ImageView) findViewById(R.id.backBtnAdmin);
        token = getIntent().getStringExtra("token");
//        Toast.makeText(this,"token:"+token,Toast.LENGTH_SHORT).show();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://ta.kisrateam.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                api = retrofit.create(Api.class);
                Token token1 = new Token(token);
                Call call = api.deleteToken(token1);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {

                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });
                Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Intent")
                        .setAction("click")
                        .setLabel("BackToMainPage")
                        .build());
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker =application.getDefaultTracker();
        mTracker.setScreenName("AdminActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Intent")
                        .setAction("click")
                        .setLabel("RegisterPage")
                        .build());
                Intent intent = new Intent(AdminActivity.this, RegisterActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });


        goToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Intent")
                        .setAction("click")
                        .setLabel("WebPage")
                        .build());

                Intent intent = new Intent(AdminActivity.this, WebActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

    }

    private List<User> getList() {
        getPosts();
        return users;
    }


    //get API   type synchronous
    public void getPosts() {
        Call<List<User>> call = api.getPost();
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