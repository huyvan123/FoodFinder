package com.example.hp.myapplication.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            Gson gson = new GsonBuilder().setLenient().create();
            OkHttpClient.Builder okHttpClient = new OkHttpClient().newBuilder();
            okHttpClient.connectTimeout(30, TimeUnit.SECONDS);
            okHttpClient.readTimeout(30, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient.build())
                    .build();
        }
        return retrofit;
    }

}
