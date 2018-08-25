package com.example.hp.myapplication.model.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.example.hp.myapplication.model.utils.FoodFinderUtils;

public class OpeningHour implements FoodFinderUtils {
    @SerializedName(OPEN_NOW)
    @Expose
    private boolean openNow;
    @SerializedName(WEEK_DAY_TEXT)
    @Expose
    private List<String> openHourInWeek;

    public OpeningHour() {
    }

    public OpeningHour(boolean openNow, List<String> openHourInWeek) {
        this.openNow = openNow;
        this.openHourInWeek = openHourInWeek;
    }

    public List<String> getOpenHourInWeek() {
        return openHourInWeek;
    }

    public void setOpenHourInWeek(List<String> openHourInWeek) {
        this.openHourInWeek = openHourInWeek;
    }

    public String getOpenNow() {
        if(this.openNow == true){
            return "yes";
        }else {
            return "no";
        }
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }
}
