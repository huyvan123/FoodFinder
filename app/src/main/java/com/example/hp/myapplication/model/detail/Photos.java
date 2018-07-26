package com.example.hp.myapplication.model.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.example.hp.myapplication.model.utils.StringUtils;

public class Photos implements StringUtils {
    @SerializedName(WIDTH)
    @Expose
    private int width;
    @SerializedName(HEIGHT)
    @Expose
    private int height;
    @SerializedName(PHOTO_REFERENCE)
    @Expose
    private String photoReference;

    public Photos() {
    }

    public Photos(int width, int height, String photoReference) {
        this.width = width;
        this.height = height;
        this.photoReference = photoReference;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }
}
