package com.example.hp.myapplication.model.detail;

import com.example.hp.myapplication.model.utils.StringUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailResult implements StringUtils {
    @SerializedName(FORMATTED_ADDRESS)
    @Expose
    private String formatedAddress;
    @SerializedName(FORMATTED_PHONENUMBER)
    @Expose
    private String formatedPhoneNumber;
    @SerializedName(GEOMETRY)
    @Expose
    private Geometry geometry;
    @SerializedName(ICON)
    @Expose
    private String iconURL;
    @SerializedName(ID)
    @Expose
    private String id;
    @SerializedName(INTERNATIONAL_PHONENUMBER)
    @Expose
    private String internationalPhoneNumber;
    @SerializedName(NAME)
    @Expose
    private String name;
    @SerializedName(OPENING_HOURS)
    @Expose
    private OpeningHour openingHour;
    @SerializedName(PHOTOS)
    @Expose
    private List<Photos> photoURLs;
    @SerializedName(PLACE_ID)
    @Expose
    private String placeId;
    @SerializedName(RATING)
    @Expose
    private String rating;
    @SerializedName(WEBSITE)
    @Expose
    private String website;

    public DetailResult() {
    }

    public DetailResult(String formatedAddress, String formatedPhoneNumber, Geometry geometry, String iconURL,
                        String id, String internationalPhoneNumber, String name, OpeningHour openingHour,
                        List<Photos> photoURLs, String placeId, String rating, String website) {
        this.formatedAddress = formatedAddress;
        this.formatedPhoneNumber = formatedPhoneNumber;
        this.geometry = geometry;
        this.iconURL = iconURL;
        this.id = id;
        this.internationalPhoneNumber = internationalPhoneNumber;
        this.name = name;
        this.openingHour = openingHour;
        this.photoURLs = photoURLs;
        this.placeId = placeId;
        this.rating = rating;
        this.website = website;
    }

    public String getFormatedAddress() {
        return formatedAddress;
    }

    public void setFormatedAddress(String formatedAddress) {
        this.formatedAddress = formatedAddress;
    }

    public String getFormatedPhoneNumber() {
        return formatedPhoneNumber;
    }

    public void setFormatedPhoneNumber(String formatedPhoneNumber) {
        this.formatedPhoneNumber = formatedPhoneNumber;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHour getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(OpeningHour openingHour) {
        this.openingHour = openingHour;
    }

    public List<Photos> getPhotoURLs() {
        return photoURLs;
    }

    public void setPhotoURLs(List<Photos> photoURLs) {
        this.photoURLs = photoURLs;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
