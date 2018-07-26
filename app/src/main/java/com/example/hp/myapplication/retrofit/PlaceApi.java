package com.example.hp.myapplication.retrofit;

import com.example.hp.myapplication.model.utils.StringUtils;

public class PlaceApi implements StringUtils{

    public static IGoodService getIGoodService(){
        return RetrofitClient.getClient(PLACES_API_BASE).create(IGoodService.class);
    }

}
