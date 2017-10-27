
package com.centaurs.tmdbapp.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie{
    @SerializedName("genre_ids")
    private List<Integer> genreIds = null;
    private List<Genre> genres = null;
    private Integer id;
    @SerializedName("original_language")
    private String originalLanguage;
    private String overview;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("release_date")
    private String releaseDate;
    private String title;
    @SerializedName("vote_average")
    private Double voteAverage;

    public List<Genre> getGenres() {
        return genres;
    }

    public Integer getId() {
        return id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }
}
