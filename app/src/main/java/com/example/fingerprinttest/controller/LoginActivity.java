package com.example.fingerprinttest.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
import android.widget.Toast;

import com.example.fingerprinttest.R;
import com.example.fingerprinttest.model.Admin;
import com.example.fingerprinttest.services.JsonPlaceHolderApi;

import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText emailText, passwordText;
    TextView signIn;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = findViewById(R.id.email_admin);
        passwordText = findViewById(R.id.password_admin);
        signIn = findViewById(R.id.sign_in);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ta.kisrateam.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmailId(emailText.getText().toString())) {
                    checkUser();
                    if (code != 200) {
                        SweetAlertDialog loading = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                        loading.setTitleText("ข้อมูลไม่ถูกต้อง");
                        loading.setContentText("กรุณาใส่อีเมลและรหัสผ่านใหม่อีกครั้ง");
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
//                                                text.setTypeface(ImFonts.getProximanova());
                                text.setGravity(Gravity.CENTER);

                            }
                        });

                        loading.show();

                    } else {
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);
                    }
                } else {
                    emailText.setError("กรุณาใส่ E-mail ให้ถูกต้อง");
                    emailText.requestFocus();
                }

            }
        });
    }

    private boolean isValidEmailId(String email) {

        return Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+").matcher(email).matches();

        //        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
        //                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        //                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
        //                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        //                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
        //                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public void checkUser() {

        Admin admin = new Admin(emailText.getText().toString(), passwordText.getText().toString());
        Call<Admin> call = jsonPlaceHolderApi.checkUsers(admin);
        call.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(Call<Admin> call, Response<Admin> response) {
                Admin admin1 = response.body();
                code = response.code();
            }

            @Override
            public void onFailure(Call<Admin> call, Throwable t) {

            }
        });

    }
}