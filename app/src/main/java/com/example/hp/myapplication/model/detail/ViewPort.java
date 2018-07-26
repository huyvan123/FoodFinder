package com.example.hp.myapplication.model.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.example.hp.myapplication.model.utils.StringUtils;

public class ViewPort implements StringUtils {
    @SerializedName(NORTHEAST)
    @Expose
    private LocationCreator northeast;
    @SerializedName(SOUNTHWEST)
    @Expose
    private LocationCreator sounthwest;

    public ViewPort() {
    }

    public ViewPort(LocationCreator northeast, LocationCreator sounthwest) {
        this.northeast = northeast;
        this.sounthwest = sounthwest;
    }

    public LocationCreator getNortheast() {
        return northeast;
    }

    public void setNortheast(LocationCreator northeast) {
        this.northeast = northeast;
    }

    public LocationCreator getSounthwest() {
        return sounthwest;
    }

    public void setSounthwest(LocationCreator sounthwest) {
        this.sounthwest = sounthwest;
    }
}
