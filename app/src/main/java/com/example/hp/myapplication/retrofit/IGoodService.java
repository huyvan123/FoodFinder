package com.example.hp.myapplication.retrofit;

import com.example.hp.myapplication.model.detail.DetailResponse;
import com.example.hp.myapplication.model.directions.DirectionResponse;
import com.example.hp.myapplication.model.search.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoodService{
    public static final String TYPE_PLACE = "place";
    public static final String TYPE_DETAIL = "/details";
    public static final String TYPE_SEARCH = "/nearbysearch";
    public static final String TYPE_DIRECTIONS = "directions";
    public static final String ORIGIN = "origin";
    public static final String DESTINATION ="destination";
    public static final String OUT_JSON = "/json";
    public static final String RADIUS = "radius";
    public static final String TYPE = "type";
    public static final String KEYWORD = "keyword";
    public static final String MODE = "mode";
    public static final String KEY = "key";
    public static final String PLACEID = "placeid";
    public static final String LOCATION = "location";
    public static final String LANGUAGE = "language";


    @GET(TYPE_PLACE+TYPE_SEARCH+OUT_JSON)
    Call<SearchResponse> getSearchResponse(@Query(LOCATION) String location, @Query(RADIUS) String radius,
                                           @Query(TYPE) String type, @Query(KEYWORD) String keyWord, @Query(KEY) String apiKey);
    @GET(TYPE_PLACE+TYPE_DETAIL+OUT_JSON)
    Call<DetailResponse> getDetailResponse(@Query(PLACEID) String placeID, @Query(KEY) String apiKey );

    @GET(TYPE_DIRECTIONS+OUT_JSON)
    Call<DirectionResponse> getDirectionsResponse(@Query(ORIGIN) String origin, @Query(DESTINATION) String destination, @Query(MODE) String mode, @Query(LANGUAGE) String language, @Query(KEY) String apiKey);
}
