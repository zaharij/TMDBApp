package com.centaurs.tmdbapp.data;

import com.centaurs.tmdbapp.data.models.Configuration;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;

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

    public interface IDataCallback<T> {
        void onResponse(T response);
        void onFailure(Throwable throwable);
    }

    public MoviesApi() {
        api = RetrofitClient.getRetrofitClient().create(IMoviesApi.class);
    }

    void loadMoviesConfiguration(final IDataCallback<Configuration> baseImageUrlCallback){
        getConfiguration().enqueue(new Callback<Configuration>() {
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
        getTopRatedMovies(currentPage).enqueue(new Callback<TopRatedMovies>() {
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
        getMovieById(movieId).enqueue(new Callback<Movie>() {
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

    private Call<Movie> getMovieById(int movieId){
        return api.getMovieById(movieId, API_KEY, LANGUAGE_EN);
    }

    private Call<TopRatedMovies> getTopRatedMovies(int currentPage) {
        return api.getTopRatedMovies(API_KEY, LANGUAGE_EN, currentPage);
    }

    private Call<Configuration> getConfiguration(){
        return api.getMovieConfiguration(API_KEY);
    }
}
