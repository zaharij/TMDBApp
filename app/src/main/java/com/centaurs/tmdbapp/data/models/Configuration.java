
package com.centaurs.tmdbapp.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Configuration {

    private Images images;
    @SerializedName("change_keys")
    private List<String> changeKeys = null;

    public Images getImages() {
        return images;
    }

}
