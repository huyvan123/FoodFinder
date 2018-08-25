package com.example.hp.myapplication.model.directions;

import com.example.hp.myapplication.model.detail.LocationCreator;
import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Leg implements FoodFinderUtils {
    @SerializedName(DISTANCE)
    @Expose
    private DisDuration distance;
    @SerializedName(DURATION)
    @Expose
    private DisDuration duration;
    @SerializedName(END_ADDRESS)
    @Expose
    private String endAddress;
    @SerializedName(END_LOCATION)
    @Expose
    private LocationCreator endLocation;
    @SerializedName(START_ADDRESS)
    @Expose
    private String startAddress;
    @SerializedName(START_LOCATION)
    @Expose
    private LocationCreator startLocation;
    @SerializedName(STEPS)
    @Expose
    private List<Step> steps;

    public Leg() {
    }

    public Leg(DisDuration distance, DisDuration duration, String endAddress, LocationCreator endLocation, String startAddress, LocationCreator startLocation, List<Step> steps) {
        this.distance = distance;
        this.duration = duration;
        this.endAddress = endAddress;
        this.endLocation = endLocation;
        this.startAddress = startAddress;
        this.startLocation = startLocation;
        this.steps = steps;
    }

    public DisDuration getDistance() {
        return distance;
    }

    public void setDistance(DisDuration distance) {
        this.distance = distance;
    }

    public DisDuration getDuration() {
        return duration;
    }

    public void setDuration(DisDuration duration) {
        this.duration = duration;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public LocationCreator getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LocationCreator endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public LocationCreator getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LocationCreator startLocation) {
        this.startLocation = startLocation;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
