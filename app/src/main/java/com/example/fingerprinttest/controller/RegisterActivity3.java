package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.Token;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.Adapter;
import com.example.fingerprinttest.services.DatatoActivity;
import com.example.fingerprinttest.services.JsonPlaceHolderApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.graphics.Color;
import android.widget.Toast;


public class RegisterActivity3 extends AppCompatActivity {
    String token;
    RecyclerView dataList;
    User user;
    List<String> titles;
    List<Integer> images;
    ImageView submitBtn;
    Adapter adapter;
    Button nextBtn;

    String name;
    String age;
    String imgUser;
    String finger;
    String interest = "";
    AlertDialog.Builder builder;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        dataList = findViewById(R.id.dataList);
        submitBtn = (ImageView) findViewById(R.id.summitBtn);
        builder = new AlertDialog.Builder(this);
        //regispage 1
        name = getIntent().getStringExtra("nameUser");
        age = getIntent().getStringExtra("ageUser");
        imgUser = getIntent().getStringExtra("imgUser");
        //regispage 2
        finger = getIntent().getStringExtra("fingerprint");

        token = getIntent().getStringExtra("token");
        Toast.makeText(this,"token:"+token,Toast.LENGTH_SHORT).show();
        interest = getIntent().getStringExtra("interest");


        //connectApi
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


        addCard();

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
//


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
                            jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                            Token token1 = new Token(token);
                            Call call = jsonPlaceHolderApi.deleteToken(token1);
                            call.enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) {

                                }

                                @Override
                                public void onFailure(Call call, Throwable t) {

                                }
                            });
                            Intent intent = new Intent(RegisterActivity3.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
//                            .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//                                    sDialog.dismissWithAnimation();
//                                }
//                            })
                    dialog.setCancelable(false);
                    dialog.show();
                    createPost();
                    getIntent().removeExtra("nameUser");
                    getIntent().removeExtra("ageUser");
                    getIntent().removeExtra("imgUser");
                    getIntent().removeExtra("fingerprint");
                    getIntent().removeExtra("interest");

                }
            }
        });


    }


    //save API
    public void createPost() {
        int ageEdit = Integer.parseInt(age);
        user = new User(" " + name, ageEdit, "" + adapter.getSentdata(), "" + imgUser,
                "" + finger);
        Call<User> call = jsonPlaceHolderApi.createPost(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
//                    textLog.setText("Code ERROR : " + response.code());
                    return;
                }
//                textLog.setText("" + response.code());
                //
                User userPost = response.body();
                String content = "";
                content += "ID: " + userPost.getId() + "\n";
                content += "Name: " + userPost.getName() + "\n";
                content += "Age: " + userPost.getAge() + "\n";
                content += "Interest: " + userPost.getInterest() + "\n";
                content += "ImageUser: " + userPost.getImguser() + "\n";
                content += "Fingeprint: " + userPost.getFingerprint() + "\n";
                content += "update_at: " + userPost.getUpdated_at() + "\n";
                content += "Create_at: " + userPost.getCreated_at() + "\n";
                // textDropdown.setText(response.body().toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }


        });
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