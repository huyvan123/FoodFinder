package com.example.hp.myapplication.retrofit;

import android.graphics.Bitmap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IImageService {
    @GET
    Call<Bitmap> getIconResponse(@Url String url);
}
