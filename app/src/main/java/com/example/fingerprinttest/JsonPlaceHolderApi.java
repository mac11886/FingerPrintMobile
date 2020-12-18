package com.example.fingerprinttest;

import com.example.fingerprinttest.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET("api/get-data")
    Call<List<User>> getPost(@Query("id") int id);
}
