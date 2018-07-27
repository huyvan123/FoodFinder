package com.example.hp.myapplication.model.directions;

import com.example.hp.myapplication.model.utils.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Polyline implements StringUtils{
    @SerializedName(POINTS)
    @Expose
    private String point;

    public Polyline(String point) {
        this.point = point;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
