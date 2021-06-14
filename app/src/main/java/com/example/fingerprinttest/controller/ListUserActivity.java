package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.User;
import com.example.fingerprinttest.services.Api;
import com.example.fingerprinttest.services.LoadingDialog;
import com.example.fingerprinttest.services.UserAdapter;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListUserActivity extends AppCompatActivity {

    List<User> users;
    RecyclerView recyclerView;
    Api api;
    TextView editFingerText ;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        editFingerText = findViewById(R.id.editFingerText);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://asq.ksta.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        backBtn = findViewById(R.id.backBtnList);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListUserActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

            getPosts();


        LoadingDialog loadingDialog = new LoadingDialog(ListUserActivity.this);
        loadingDialog.startLoadingDialog();
        loadingDialog.cancelDialog();
        final Handler someHandler1 = new Handler(getMainLooper());
        someHandler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        }, 3500);
        someHandler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                createUserList();

                editFingerText.setText("แก้ไขลายนิ้วมือ");
                backBtn.setImageResource(R.drawable.ic_left_arrow);
            }
        }, 3000);


    }

    public void createUserList(){

        recyclerView = findViewById(R.id.userList);



        try {
            UserAdapter userAdapter = new UserAdapter(this, users);
            recyclerView.setAdapter(userAdapter);
        }catch (Exception e){
            SweetAlertDialog loading = new SweetAlertDialog(ListUserActivity.this, SweetAlertDialog.WARNING_TYPE);
            loading.setTitleText("เกิดข้อผิดพลาดเกี่ยวกับ Internet");
            loading.setContentText("กรุณาเปิดแอพใหม่อีกครั้ง");
            loading.getProgressHelper().setBarColor(ListUserActivity.this.getResources().getColor(R.color.greentea));
            loading.setOnShowListener((DialogInterface.OnShowListener) dialog -> {
                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                Typeface face = ResourcesCompat.getFont(ListUserActivity.this, R.font.kanit_light);
                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                textCon.setTextColor(getResources().getColor(R.color.black));
                textCon.setTypeface(face);
                textCon.setGravity(Gravity.CENTER);
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
                text.setTextColor(getResources().getColor(R.color.red25));
                text.setTypeface(face);
                text.setGravity(Gravity.CENTER);

            });

            loading.show();
        }

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
            Toast.makeText(ListUserActivity.this, "error", Toast.LENGTH_SHORT);
        }


    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();

    }

}