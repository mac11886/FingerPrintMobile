package com.example.fingerprinttest.services;

import com.example.fingerprinttest.model.Admin;
import com.example.fingerprinttest.model.Attendance;
import com.example.fingerprinttest.model.Token;
import com.example.fingerprinttest.model.User;
import com.google.gson.JsonElement;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {

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


}
