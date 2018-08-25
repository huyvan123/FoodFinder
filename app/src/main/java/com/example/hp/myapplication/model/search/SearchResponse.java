package com.example.hp.myapplication.model.search;

import com.example.hp.myapplication.model.utils.FoodFinderUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse implements FoodFinderUtils {
    @SerializedName(HTML_ATTRIBUTION)
    @Expose
    private List<String> htmlAttribution;
    @SerializedName(NEXT_PAGE_TOKEN)
    @Expose
    private String nextPageToken;
    @SerializedName(RESULTS)
    @Expose
    private List<SearchResults> results;
    @SerializedName(STATUS)
    @Expose
    private String status;

    public SearchResponse() {
    }

    public SearchResponse(List<String> htmlAttribution, String nextPageToken, List<SearchResults> results, String status) {
        this.htmlAttribution = htmlAttribution;
        this.nextPageToken = nextPageToken;
        this.results = results;
        this.status = status;
    }

    public List<String> getHtmlAttribution() {
        return htmlAttribution;
    }

    public void setHtmlAttribution(List<String> htmlAttribution) {
        this.htmlAttribution = htmlAttribution;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<SearchResults> getResults() {
        return results;
    }

    public void setResults(List<SearchResults> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
