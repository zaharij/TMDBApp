package com.centaurs.tmdbapp.data.api;

import com.centaurs.tmdbapp.data.models.Configuration;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.data.models.MoviesTrailer;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;
import com.centaurs.tmdbapp.data.util.IDataCallback;
import com.centaurs.tmdbapp.data.util.RetrofitClient;

import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.centaurs.tmdbapp.data.constants.DataConstants.API_KEY;
import static com.centaurs.tmdbapp.data.constants.DataConstants.LANGUAGE_EN;

@Singleton
public class MoviesApi {
    private IMoviesApi api;

    public MoviesApi() {
        api = RetrofitClient.getRetrofitClient().create(IMoviesApi.class);
    }

    public void loadMoviesConfiguration(final IDataCallback<Configuration> baseImageUrlCallback){
        api.getMovieConfiguration(API_KEY).enqueue(new Callback<Configuration>() {
            @Override
            public void onResponse(@NotNull Call<Configuration> call, @NotNull Response<Configuration> response) {
                baseImageUrlCallback.onResponse(response.body());
            }
            @Override
            public void onFailure(@NotNull Call<Configuration> call, @NotNull Throwable t) {
                baseImageUrlCallback.onFailure(t);
            }
        });
    }

    public void loadPage(final IDataCallback<TopRatedMovies> dataCallback, int currentPage){
        api.getTopRatedMovies(API_KEY, LANGUAGE_EN, currentPage).enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(@NotNull Call<TopRatedMovies> call, @NotNull Response<TopRatedMovies> response) {
                dataCallback.onResponse(response.body());
            }
            @Override
            public void onFailure(@NotNull Call<TopRatedMovies> call, @NotNull Throwable t) {
                dataCallback.onFailure(t);
            }
        });
    }

    public void loadMovie(final IDataCallback<Movie> dataCallback, int movieId){
        api.getMovieById(movieId, API_KEY, LANGUAGE_EN).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NotNull Call<Movie> call, @NotNull Response<Movie> response) {
                dataCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<Movie> call, @NotNull Throwable t) {
                dataCallback.onFailure(t);
            }
        });
    }

    public void loadMovieTrailer(final IDataCallback<MoviesTrailer> dataCallback, int movieId){
        api.getMovieTrailer(movieId, API_KEY, LANGUAGE_EN).enqueue(new Callback<MoviesTrailer>() {
            @Override
            public void onResponse(@NotNull Call<MoviesTrailer> call, @NotNull Response<MoviesTrailer> response) {
                dataCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<MoviesTrailer> call, @NotNull Throwable t) {
                dataCallback.onFailure(t);
            }
        });
    }
}
