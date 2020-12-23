package com.example.fingerprinttest.services;

import com.example.fingerprinttest.model.User;

import java.sql.Timestamp;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET("api/get-data")
    Call<List<User>> getPost(@Query("id") int id);


    @POST("api/save")
    Call<User> createPost(@Body  User user);

    @FormUrlEncoded
    @POST("api/save")
    Call<User> createPost(
            @Field("name") String name,
            @Field("age") int age,
            @Field("interest") String interest,
            @Field("imguser") String imguser,
            @Field("fingerprint") String fingerprint
    );
}
