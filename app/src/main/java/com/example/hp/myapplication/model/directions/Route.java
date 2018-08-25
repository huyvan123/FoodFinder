package com.example.hp.myapplication.model.directions;

import com.example.hp.myapplication.model.detail.ViewPort;
import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route implements FoodFinderUtils {
    @SerializedName(BOUNDS)
    @Expose
    private ViewPort bound;
    @SerializedName(LEGS)
    @Expose
    private List<Leg> legs;
    @SerializedName(OVERVIEW_POLYLINE)
    @Expose
    private Polyline overviewPolyline;

    public Route() {
    }

    public Route(ViewPort bound, List<Leg> legs, Polyline overviewPolyline) {
        this.bound = bound;
        this.legs = legs;
        this.overviewPolyline = overviewPolyline;
    }

    public ViewPort getBound() {
        return bound;
    }

    public void setBound(ViewPort bound) {
        this.bound = bound;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public Polyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(Polyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }
}
