
package com.centaurs.tmdbapp.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable{

    private Integer id;
    private Boolean video;
    @SerializedName("vote_average")
    private Double voteAverage;
    private String title;
    private Double popularity;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("original_language")
    private String originalLanguage;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("genre_ids")
    private List<Integer> genreIds = null;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private Boolean adult;
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;

    public Result() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Boolean getVideo() {
        return video;
    }
}
