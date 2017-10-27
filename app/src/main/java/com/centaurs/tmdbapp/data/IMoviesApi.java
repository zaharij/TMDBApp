package com.centaurs.tmdbapp.data;

import com.centaurs.tmdbapp.data.models.Configuration;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface IMoviesApi {
    String API_KEY_QUERY = "api_key";
    String LANGUAGE_KEY = "language";
    String PAGE_QUERY = "page";

    @GET("movie/top_rated")
    Call<TopRatedMovies> getTopRatedMovies(
            @Query(API_KEY_QUERY) String apiKey,
            @Query(LANGUAGE_KEY) String language,
            @Query(PAGE_QUERY) int pageIndex
    );

    @GET("configuration")
    Call<Configuration> getMovieConfiguration(
            @Query(API_KEY_QUERY) String apiKey
    );

    @GET("movie/{movie_id}")
    Call<Movie> getMovieById(
        @Path("movie_id") int movieId,
        @Query(API_KEY_QUERY) String apiKey,
        @Query(LANGUAGE_KEY) String language
    );
}
