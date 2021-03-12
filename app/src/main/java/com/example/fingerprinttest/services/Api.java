package com.example.fingerprinttest.services;

import com.example.fingerprinttest.model.Admin;
import com.example.fingerprinttest.model.Attendance;
import com.example.fingerprinttest.model.EditFingerprint;
import com.example.fingerprinttest.model.GroupData;
import com.example.fingerprinttest.model.Log;
import com.example.fingerprinttest.model.Token;
import com.example.fingerprinttest.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    @GET("api/get-data")
    Call<List<User>> getPost();

    @POST("api/login-mobile")
    Call<Admin> checkUsers(@Body Admin admin);

    @POST("api/deleteToken")
    Call<Token> deleteToken(@Body Token token);

    @POST("api/date")
    Call<Attendance> createPostDate(@Body Attendance attendance);

    @POST("api/save")
    Call<User> createPost(@Body  User user);

    @POST("api/save_logmobile")
    Call<Log> createLog(@Body Log log);


    @POST("api/edit_fingerprint")
    Call<EditFingerprint> editFingerprintApi(@Body EditFingerprint editFingerprint);

    @GET("api/getGroup-job")
    Call<GroupData> getGroupApi();



}
