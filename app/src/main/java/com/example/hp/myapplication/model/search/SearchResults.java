package com.example.hp.myapplication.model.search;

import com.example.hp.myapplication.model.detail.Geometry;
import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchResults implements FoodFinderUtils {
    @SerializedName(PLACE_ID)
    @Expose
    private String placeId;
    @SerializedName(GEOMETRY)
    @Expose
    private Geometry geometry;

    public SearchResults() {
    }

    public SearchResults(String placeId, Geometry geometry) {
        this.placeId = placeId;
        this.geometry = geometry;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
