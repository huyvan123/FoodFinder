package com.example.hp.myapplication.model.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.example.hp.myapplication.model.utils.FoodFinderUtils;

public class Geometry implements FoodFinderUtils {
    @SerializedName(LOCATION)
    @Expose
    private LocationCreator location;
    @SerializedName(VIEWPORT)
    @Expose
    private ViewPort viewPort;

    public Geometry() {
    }

    public Geometry(LocationCreator location, ViewPort viewPort) {
        this.location = location;
        this.viewPort = viewPort;
    }

    public LocationCreator getLocation() {
        return location;
    }

    public void setLocation(LocationCreator location) {
        this.location = location;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }
}
