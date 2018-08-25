package com.example.hp.myapplication.model.directions;

import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionResponse implements FoodFinderUtils {
    @SerializedName(ROUTES)
    @Expose
    private List<Route> routes;
    @SerializedName(STATUS)
    @Expose
    private String status;

    public DirectionResponse() {
    }

    public DirectionResponse(List<Route> routes, String status) {
        this.routes = routes;
        this.status = status;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
