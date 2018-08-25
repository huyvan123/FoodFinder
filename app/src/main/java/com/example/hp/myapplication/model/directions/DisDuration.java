package com.example.hp.myapplication.model.directions;

import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DisDuration implements FoodFinderUtils {
    @SerializedName(TEXT)
    @Expose
    private String text;
    @SerializedName(VALUE)
    @Expose
    private String value;

    public DisDuration() {
    }

    public DisDuration(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
