package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.Adapter;
import com.example.fingerprinttest.services.DatatoActivity;
import com.example.fingerprinttest.services.JsonPlaceHolderApi;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity3 extends AppCompatActivity implements DatatoActivity {

    RecyclerView dataList;
    User user;
    List<String> titles;
    List<Integer> images;
    Adapter adapter;
    Button nextBtn;

    String name ;
    String age ;
    String imgUser;
    String finger;
    String interest = "";

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        nextBtn = findViewById(R.id.nextBtn3);
        dataList = findViewById(R.id.dataList);

        //regispage 1
         name = getIntent().getStringExtra("nameUser");
         age = getIntent().getStringExtra("ageUser");
         imgUser = getIntent().getStringExtra("imgUser");
        //regispage 2
         finger = getIntent().getStringExtra("fingerprint");


        interest = getIntent().getStringExtra("interest");


        //connectApi
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


       addCard();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SweetAlertDialog pDialog = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.PROGRESS_TYPE);
//                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                pDialog.setTitleText("Loading");
//                pDialog.setCancelable(false);
//                pDialog.show();
                createPost();
                System.out.println("----------------------");
                System.out.println("INTEREST: " +adapter.getSentdata());
//                Log.e("TEST",interest);
                Intent intent = new Intent(RegisterActivity3.this, MainActivity.class);
                startActivity(intent);
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
                    //textDropdown.setText("Code ERROR : " + response.code());
                    return;
                }
                //textDropdown.setText("success");
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


    public  void addCard(){
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
        images.add(R.drawable.ic_photography);
        images.add(R.drawable.ic_lion);
        images.add(R.drawable.ic_tent);
        images.add(R.drawable.ic_football_players);
        images.add(R.drawable.ic_joystick);
        images.add(R.drawable.ic_car);
        images.add(R.drawable.ic_chemistry);
        images.add(R.drawable.ic_cooking);
        adapter = new Adapter(this, titles, images);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(adapter);


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void sendData(String string) {
       Intent intent = new Intent(getApplicationContext(),RegisterActivity3.class);
            intent.putExtra("interest",string);
//        Handler handler = new Handler(Looper.getMainLooper());
//      handler.post()

    }
}