package com.centaurs.tmdbapp.model;


import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.TopRatedMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface MovieCallInterface {
    String API_KEY_QUERY = "api_key";
    String LANGUAGE_KEY = "language";
    String PAGE_QUERY = "page";

    @GET("movie/top_rated")
    Call<TopRatedMovies> getTopRatedMovies(
        @Query(API_KEY_QUERY) String apiKey,
        @Query(LANGUAGE_KEY) String language,
        @Query(PAGE_QUERY) int pageIndex
    );

    @GET("genre/movie/list")
    Call<MovieGenres> getMovieGenres(
        @Query(API_KEY_QUERY) String apiKey,
        @Query(LANGUAGE_KEY) String language
    );
}
