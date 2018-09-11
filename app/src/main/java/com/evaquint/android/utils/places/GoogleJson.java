package com.evaquint.android.utils.places;

import com.google.gson.annotations.SerializedName;

/**
 * Created by henry on 7/28/2018.
 */

public class GoogleJson {
    @SerializedName("html_attributions")
    private Integer html_attributions;
    @SerializedName("next_page_token")
    private Integer next_page_token;
    @SerializedName("results")
    private String results;

    public GoogleJson(Integer html_attributions, Integer id, String title, String url, String thumbnailUrl) {
        this.html_attributions = html_attributions;
        this.next_page_token = next_page_token;
        this.results = results;
    }

    public Integer getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(Integer html_attributions) {
        this.html_attributions = html_attributions;
    }

    public Integer getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(Integer next_page_token) {
        this.next_page_token = next_page_token;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
