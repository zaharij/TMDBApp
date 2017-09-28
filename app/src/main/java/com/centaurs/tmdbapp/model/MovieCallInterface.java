package com.centaurs.tmdbapp.model;


import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.TopRatedMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieCallInterface {
    @GET("movie/top_rated")
    Call<TopRatedMovies> getTopRatedMovies(
        @Query("api_key") String apiKey,
        @Query("language") String language,
        @Query("page") int pageIndex
    );

    @GET("genre/movie/list")
    Call<MovieGenres> getMovieGenres(
        @Query("api_key") String apiKey,
        @Query("language") String language
    );
}
