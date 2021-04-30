package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.Log;
import com.example.fingerprinttest.model.Token;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.Adapter;
import com.example.fingerprinttest.services.AnalyticsApplication;
import com.example.fingerprinttest.services.Api;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.widget.Toast;


public class RegisterActivity3 extends AppCompatActivity {
    Tracker mTracker;
    String token;
    RecyclerView dataList;
    User user;
    List<String> titles;
    List<Integer> images;
    ImageView submitBtn;
    Adapter adapter;

    String birthday, group, job;

    String name;
    String age;
    String imgUser;
    String finger,secondFinger;
    String interest = "";
    AlertDialog.Builder builder;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        dataList = findViewById(R.id.dataList);
        submitBtn = (ImageView) findViewById(R.id.summitBtn);
        builder = new AlertDialog.Builder(this);
        //regispage 1
        name = getIntent().getStringExtra("nameUser");
        birthday = getIntent().getStringExtra("birthday");
        group = getIntent().getStringExtra("group");
        job = getIntent().getStringExtra("job");
        imgUser = getIntent().getStringExtra("imgUser");
        //regispage 2
        finger = getIntent().getStringExtra("fingerprint");
        token = getIntent().getStringExtra("token");
//        Toast.makeText(this, "token:" + token, Toast.LENGTH_SHORT).show();
        interest = getIntent().getStringExtra("interest");
        secondFinger = getIntent().getStringExtra("secondFinger");

        android.util.Log.e("firstFinger",""+finger);
        android.util.Log.e("2Finger",""+secondFinger);
        //connectApi
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.ksta.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);


        addCard();
        View view = null;
//        Snackbar.make(view, "Sleeping For Less", Snackbar.LENGTH_SHORT).show();
//        Toast.makeText(RegisterActivity3.this,"เลือกได้ 1-4 ",Toast.LENGTH_LONG).show();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapter.getCountInterest() > 4 || adapter.getCountInterest() == 0) {
                    SweetAlertDialog loading = new SweetAlertDialog(RegisterActivity3.this, SweetAlertDialog.WARNING_TYPE);
                    loading.setTitleText("แจ้งเตือน");
                    loading.setContentText("เลือกได้ 1-4 ตัวเลือก");
                    loading.getProgressHelper().setBarColor(RegisterActivity3.this.getResources().getColor(R.color.greentea));
                    loading.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                            Typeface face = ResourcesCompat.getFont(RegisterActivity3.this, R.font.kanit_light);
                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                            TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                            textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            textCon.setTextColor(getResources().getColor(R.color.black));
                            textCon.setTypeface(face);
                            textCon.setGravity(Gravity.CENTER);
                            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                            text.setTextColor(getResources().getColor(R.color.red25));
                            text.setTypeface(face);
                            text.setGravity(Gravity.CENTER);

                        }
                    });

                    loading.show();
                } else {
                    try {
                        createPost();
                        SweetAlertDialog dialog = new SweetAlertDialog(RegisterActivity3.this, SweetAlertDialog.SUCCESS_TYPE);
                        dialog.setTitleText("แจ้งเตือน");
                        dialog.setContentText("ข้อมูลได้ถูกบันทึกแล้ว");
                        dialog.setConfirmText("OK!!");

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                Typeface face = ResourcesCompat.getFont(RegisterActivity3.this, R.font.kanit_light);
                                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                                textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                textCon.setTextColor(getResources().getColor(R.color.black));
                                textCon.setTypeface(face);
                                textCon.setGravity(Gravity.CENTER);
                                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                                text.setTextColor(getResources().getColor(R.color.red25));
                                text.setTypeface(face);
                                text.setGravity(Gravity.CENTER);
                            }
                        });
                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
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
                                        .setCategory("User")
                                        .setAction("send")
                                        .setLabel("newUser")
                                        .build());
                                Intent intent = new Intent(RegisterActivity3.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });

                        dialog.setCancelable(false);
                        dialog.show();

                        getIntent().removeExtra("nameUser");
                        getIntent().removeExtra("group");
                        getIntent().removeExtra("birthday");
                        getIntent().removeExtra("group");
                        getIntent().removeExtra("job");
                        getIntent().removeExtra("imgUser");
                        getIntent().removeExtra("fingerprint");
                        getIntent().removeExtra("interest");
                    }catch (Exception e) {
//                        Toast.makeText(RegisterActivity3.this,"can't save data ",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RegisterActivity3");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    //save API
    public void createPost() {
        try {
//            int num_group = Integer.parseInt(group);
//            Toast.makeText(RegisterActivity3.this,"name:"+name +"birthday:"+birthday+"group:"+group +"job"+job,Toast.LENGTH_SHORT).show();
            user = new User( name, birthday, group, job,  adapter.getSentdata(),  imgUser,
                     finger,secondFinger);
            Call<User> call = api.createPost(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
//                    textLog.setText("Code ERROR : " + response.code());
                        return;
                    }
                    //
                    User userPost = response.body();

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    com.example.fingerprinttest.model.Log log = new com.example.fingerprinttest.model.Log("RegisterActivity3", "createPost", "can't save to API");
                    Call<com.example.fingerprinttest.model.Log> call1 = api.createLog(log);
                    call1.enqueue(new Callback<com.example.fingerprinttest.model.Log>() {
                        @Override
                        public void onResponse(Call<com.example.fingerprinttest.model.Log> call, Response<com.example.fingerprinttest.model.Log> response) {

                        }

                        @Override
                        public void onFailure(Call<com.example.fingerprinttest.model.Log> call, Throwable t) {

                        }
                    });
                }


            });
        } catch (Exception e) {
            com.example.fingerprinttest.model.Log log = new com.example.fingerprinttest.model.Log("RegisterActivity3", "createPost", "can't save to API");
            Call<com.example.fingerprinttest.model.Log> call = api.createLog(log);
            call.enqueue(new Callback<com.example.fingerprinttest.model.Log>() {
                @Override
                public void onResponse(Call<com.example.fingerprinttest.model.Log> call, Response<com.example.fingerprinttest.model.Log> response) {

                }

                @Override
                public void onFailure(Call<com.example.fingerprinttest.model.Log> call, Throwable t) {

                }
            });
        }
    }


    public void addCard() {
        titles = new ArrayList<>();
        images = new ArrayList<>();
        titles.add("Photography");
        titles.add("Animals");
        titles.add("Camping");
        titles.add("Sport");
        titles.add("Game");
        titles.add("Car");
        titles.add("Science");
        titles.add("Cooking");
        images.add(R.drawable.ic_photograph_1);
        images.add(R.drawable.ic_koala_1);
        images.add(R.drawable.ic_tent);
        images.add(R.drawable.ic_football_players);
        images.add(R.drawable.ic_joystick_2);
        images.add(R.drawable.ic_car);
        images.add(R.drawable.ic_chemistry);
        images.add(R.drawable.ic_cooking);
        adapter = new Adapter(this, titles, images);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(adapter);


    }


}