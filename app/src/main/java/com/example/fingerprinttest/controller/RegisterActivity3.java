package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fingerprinttest.R;
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


public class RegisterActivity3 extends AppCompatActivity implements DatatoActivity {

    RecyclerView dataList;
    User user;
    List<String> titles;
    List<Integer> images;
    ImageView submitBtn;
    Adapter adapter;
    Button nextBtn;
    TextView textLog;
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
                    SweetAlertDialog dialog = new SweetAlertDialog(RegisterActivity3.this, SweetAlertDialog.WARNING_TYPE);
                    dialog.setTitle("Oops!");
                    dialog.setContentText("เลือกได้ 1-4 ตัวเลือก");
                    dialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
                    dialog.show();

//                            Button button = (Button) dialog.findViewById(R.id.conf)
                } else {

                    SweetAlertDialog dialog = new SweetAlertDialog(RegisterActivity3.this, SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText("แจ้งเตือน");
                    dialog.setContentText("ข้อมูลได้ถูกบันทึกแล้ว");
                    dialog.setConfirmText("OK!!");

                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            Intent intent = new Intent(RegisterActivity3.this, MainActivity.class);
                            getIntent().removeExtra("nameUser");
                            getIntent().removeExtra("ageUser");
                            getIntent().removeExtra("imgUser");
                            getIntent().removeExtra("fingerprint");
                            getIntent().removeExtra("interest");
                            createPost();
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

//                    Log.e("ERROR",""+adapter.getCountInterest());
//                AlertDialog alert = builder.create();
//                alert.setIcon(R.drawable.ic_error);
//                //Setting the title manually
//                alert.setTitle("แจ้งเตือน");
//                alert.setMessage("ลงทะเบียนเสร็จสิ้น");
//                alert.show();

                    System.out.println("----------------------");
                    System.out.println("INTEREST: " + adapter.getSentdata());


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
                    textLog.setText("Code ERROR : " + response.code());
                    return;
                }
                textLog.setText("" + response.code());
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void sendData(String string) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity3.class);
        intent.putExtra("interest", string);
//        Handler handler = new Handler(Looper.getMainLooper());
//      handler.post()

    }
}