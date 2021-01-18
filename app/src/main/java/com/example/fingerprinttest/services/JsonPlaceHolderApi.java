package com.example.fingerprinttest.services;

import com.example.fingerprinttest.model.Attendance;
import com.example.fingerprinttest.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {

    @GET("api/get-data")
    Call<List<User>> getPost();

    @POST("api/date")
    Call<Attendance> createPostDate(@Body Attendance attendance);

//    @POST("api/edit-data")
//    Call<User> createPostId(@Body User user );

    @POST("api/save")
    Call<User> createPost(@Body  User user);

//    @FormUrlEncoded
//    @POST("api/save")
//    Call<User> createPost(
//            @Field("name") String name,
//            @Field("age") int age,
//            @Field("interest") String interest,
//            @Field("imguser") String imguser,
//            @Field("fingerprint") String fingerprint
//    );
}
