package com.example.hp.myapplication.model.detail;

import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailResponse implements FoodFinderUtils {
    @SerializedName(HTML_ATTRIBUTION)
    @Expose
    private List<String> httpAttribution;
    @SerializedName(RESULT)
    @Expose
    private DetailResult detailResult;
    @SerializedName(STATUS)
    @Expose
    private String status;

    public DetailResponse() {
    }

    public DetailResponse(List<String> httpAttribution, DetailResult detailResult, String status) {
        this.httpAttribution = httpAttribution;
        this.detailResult = detailResult;
        this.status = status;
    }

    public List<String> getHttpAttribution() {
        return httpAttribution;
    }

    public void setHttpAttribution(List<String> httpAttribution) {
        this.httpAttribution = httpAttribution;
    }

    public DetailResult getDetailResults() {
        return detailResult;
    }

    public void setDetailResults(DetailResult detailResult) {
        this.detailResult = detailResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
