package com.example.myapplication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @GET("/musiclist")
    Call<ResponseBody> getmusiclist();

    @POST("/getnext")
    Call<ResponseBody> getnextmusic(@Body nextmusicinfo body);

}