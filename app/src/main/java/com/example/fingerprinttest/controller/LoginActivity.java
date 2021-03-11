package com.example.fingerprinttest.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.Admin;
import com.example.fingerprinttest.services.AnalyticsApplication;
import com.example.fingerprinttest.services.Api;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    Tracker mTracker;
    EditText emailText, passwordText;
    TextView signIn;
    private Api api;
    static int code;
    List<Admin> admins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = findViewById(R.id.email_admin);
        passwordText = findViewById(R.id.password_admin);
        signIn = findViewById(R.id.sign_in);

//        emailText.setText("admin@email.com");
//        passwordText.setText("123456");

        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();

                int i = 0;


                if (email.matches(emailPattern)) {
                    checkUser();
                } else {
                    emailText.setError("กรุณาใส่ E-mail ให้ถูกต้อง");
                    emailText.requestFocus();
                }

            }
        });

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker =application.getDefaultTracker();
        mTracker.setScreenName("LoginActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    public void checkUser() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        Admin admin = new Admin(emailText.getText().toString(), passwordText.getText().toString());
        Call<Admin> call = api.checkUsers(admin);
        call.enqueue(new Callback<Admin>() {

            @Override
            public void onResponse(Call<Admin> call, Response<Admin> response) {

                if (!response.isSuccessful()) {


                    SweetAlertDialog loading = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                    loading.setTitleText("ข้อมูลไม่ถูกต้อง");
                    loading.setContentText("กรุณาใส่อีเมลและรหัสผ่านใหม่อีกครั้ง");
                    loading.getProgressHelper().setBarColor(LoginActivity.this.getResources().getColor(R.color.greentea));
                    loading.setConfirmText("OK");
                    Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Error")
                            .setAction("click")
                            .setLabel("E-mailOrPassword-Incorrect")
                            .build());
                    loading.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                            Typeface face = ResourcesCompat.getFont(LoginActivity.this, R.font.kanit_light);
                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                            TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                            textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            textCon.setTextColor(getResources().getColor(R.color.black));
                            textCon.setTypeface(face);
//                                              title
                            textCon.setGravity(Gravity.CENTER);
                            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                            text.setTextColor(getResources().getColor(R.color.red25));
                            text.setTypeface(face);

                            text.setGravity(Gravity.CENTER);

                        }
                    });

                    loading.show();


                    return;
                }
                SweetAlertDialog dialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                dialog.setTitleText("แจ้งเตือน");
                dialog.setContentText("เข้าสู่ระบบเรียบร้อย");
                dialog.setConfirmText("OK");
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                        Typeface face = ResourcesCompat.getFont(LoginActivity.this, R.font.kanit_light);
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
                        Tracker t = ((AnalyticsApplication) getApplication()).getDefaultTracker();
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Intent")
                                .setAction("click")
                                .setLabel("AdminPage")
                                .build());
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        intent.putExtra("token",response.body().getEmail());
                        startActivity(intent);

                        Log.e("message",response.message());
                    }
                }); dialog.setCancelable(false);
                dialog.show();


            }

            @Override
            public void onFailure(Call<Admin> call, Throwable t) {

                SweetAlertDialog loading = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                loading.setTitleText("Server failed");
                loading.setContentText("กรุณาลองใหม่อีกครั้ง");
                loading.getProgressHelper().setBarColor(LoginActivity.this.getResources().getColor(R.color.greentea));
                loading.setConfirmText("OK");

                loading.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                        Typeface face = ResourcesCompat.getFont(LoginActivity.this, R.font.kanit_light);
                        TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                        TextView textCon = (TextView) alertDialog.findViewById(R.id.content_text);
                        textCon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        textCon.setTextColor(getResources().getColor(R.color.black));
                        textCon.setTypeface(face);
//                                              title
                        textCon.setGravity(Gravity.CENTER);
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        text.setTextColor(getResources().getColor(R.color.red25));
                        text.setTypeface(face);

                        text.setGravity(Gravity.CENTER);

                    }
                });

                loading.show();


                return;
            }
        });

        Log.e("OUTSIDE", "" + code);

    }


}