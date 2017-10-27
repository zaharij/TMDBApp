
package com.centaurs.tmdbapp.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Images {

    @SerializedName("base_url")
    private String baseUrl;
    @SerializedName("secure_base_url")
    private String secureBaseUrl;
    @SerializedName("backdrop_sizes")
    private List<String> backdropSizes = null;
    @SerializedName("logo_sizes")
    private List<String> logoSizes = null;
    @SerializedName("poster_sizes")
    private List<String> posterSizes = null;
    @SerializedName("profile_sizes")
    private List<String> profileSizes = null;
    @SerializedName("still_sizes")
    private List<String> stillSizes = null;

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<String> getPosterSizes() {
        return posterSizes;
    }

}
