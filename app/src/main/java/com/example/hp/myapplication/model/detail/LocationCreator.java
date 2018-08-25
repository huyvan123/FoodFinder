package com.example.hp.myapplication.model.detail;

import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationCreator implements FoodFinderUtils {
    @SerializedName(LAT)
    @Expose
    private Double latitude;
    @SerializedName(LNG)
    @Expose
    private Double longtitude;

    public LocationCreator() {
    }

    public LocationCreator(Double latitude, Double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }
}
