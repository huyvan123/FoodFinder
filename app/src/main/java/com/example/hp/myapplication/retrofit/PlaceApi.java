package com.example.hp.myapplication.retrofit;

import com.example.hp.myapplication.model.utils.FoodFinderUtils;

public class PlaceApi implements FoodFinderUtils {

    public static IGoodService getIGoodService(){
        return RetrofitClient.getClient(PLACES_API_BASE).create(IGoodService.class);
    }

}
