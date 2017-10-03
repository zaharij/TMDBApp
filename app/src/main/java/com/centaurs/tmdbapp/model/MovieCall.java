package com.centaurs.tmdbapp.model;


import android.content.Context;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.model.config.RetrofitConfig;
import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.TopRatedMovies;

import retrofit2.Call;

public class MovieCall{
    private static MovieCall movieCall;
    private MovieCallInterface movieCallInterface;

    private MovieCall() {
        movieCallInterface = RetrofitConfig.getRetrofitClient().create(MovieCallInterface.class);
    }

    public static MovieCall getInstance(){
        if (movieCall == null){
            movieCall = new MovieCall();
        }
        return movieCall;
    }

    public Call<TopRatedMovies> callTopRatedMoviesApi(Context context, int currentPage) {
        return movieCallInterface.getTopRatedMovies(
                context.getString(R.string.api_key),
                context.getString(R.string.language_en_US),
                currentPage
        );
    }

    public Call<MovieGenres> callMovieGenres(Context context) {
        return movieCallInterface.getMovieGenres(
                context.getString(R.string.api_key),
                context.getString(R.string.language_en_US)
        );
    }
}
