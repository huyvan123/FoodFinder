package com.example.hp.myapplication.model.directions;


import com.example.hp.myapplication.model.detail.LocationCreator;
import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step implements FoodFinderUtils {
    @SerializedName(DISTANCE)
    @Expose
    private DisDuration distance;
    @SerializedName(DURATION)
    @Expose
    private DisDuration duration;
    @SerializedName(START_LOCATION)
    @Expose
    private LocationCreator startLocation;
    @SerializedName(END_LOCATION)
    @Expose
    private LocationCreator endLocation;
    @SerializedName(TRAVEL_MODE)
    @Expose
    private String travelMode;
    @SerializedName(HTML_INSTRUCTIONS)
    @Expose
    private String htmlInstruction;
    @SerializedName(POLYLINE)
    @Expose
    private Polyline polyline;

    public Step(DisDuration distance, DisDuration duration, LocationCreator startLocation, LocationCreator endLocation, String travelMode, String htmlInstruction, Polyline polyline) {
        this.distance = distance;
        this.duration = duration;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.travelMode = travelMode;
        this.htmlInstruction = htmlInstruction;
        this.polyline = polyline;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
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

    public LocationCreator getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LocationCreator startLocation) {
        this.startLocation = startLocation;
    }

    public LocationCreator getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LocationCreator endLocation) {
        this.endLocation = endLocation;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getHtmlInstruction() {
        return htmlInstruction;
    }

    public void setHtmlInstruction(String htmlInstruction) {
        this.htmlInstruction = htmlInstruction;
    }
}
