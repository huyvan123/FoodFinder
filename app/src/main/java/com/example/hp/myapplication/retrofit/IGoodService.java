package com.example.hp.myapplication.retrofit;

import android.graphics.Bitmap;

import com.example.hp.myapplication.model.detail.DetailResponse;
import com.example.hp.myapplication.model.search.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoodService{
    public static final String TYPE_PLACE = "place";
    public static final String TYPE_DETAIL = "/details";
    public static final String TYPE_SEARCH = "/nearbysearch";
    public static final String TYPE_PHOTO = "/photo";
    public static final String OUT_JSON = "/json";
    public static final String RADIUS = "radius";
    public static final String TYPE = "type";
    public static final String KEYWORD = "keyword";
    public static final String KEY = "key";
    public static final String PLACEID = "placeid";
    public static final String MAXWIDTH = "maxwidth";
    public static final String PHOTOREFERENCE = "photoreference";
    public static final String LOCATION = "location";


    @GET(TYPE_PLACE+TYPE_SEARCH+OUT_JSON)
    Call<SearchResponse> getSearchResponse(@Query(LOCATION) String location, @Query(RADIUS) String radius,
                                           @Query(TYPE) String type, @Query(KEYWORD) String keyWord, @Query(KEY) String apiKey);
    @GET(TYPE_PLACE+TYPE_DETAIL+OUT_JSON)
    Call<DetailResponse> getDetailResponse(@Query(PLACEID) String placeID, @Query(KEY) String apiKey );
    @GET(TYPE_PLACE+TYPE_PHOTO)
    Call<Bitmap> getImageResponse(@Query(MAXWIDTH) String maxwidth, @Query(PHOTOREFERENCE) String photoreference, @Query(KEY) String apiKey);
}
