package com.centaurs.tmdbapp.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.centaurs.tmdbapp.data.config.RetrofitConfig;
import com.centaurs.tmdbapp.data.models.MovieGenres;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.centaurs.tmdbapp.data.constants.Constants.API_KEY;
import static com.centaurs.tmdbapp.data.constants.Constants.LANGUAGE_EN;
import static com.centaurs.tmdbapp.data.constants.QueryConstants.DEFAULT_IMAGE_SIZE_W;
import static com.centaurs.tmdbapp.data.constants.QueryConstants.getBaseImageUrlStrByWidth;

public class TMoviesDBApi {
    private static TMoviesDBApi tMoviesDBApi;
    private ITMoviesDBApi ITMoviesDBApi;
    private MovieGenres movieGenres;

    public interface IDataCallback {
        void onResponse(Response<TopRatedMovies> response);
    }

    public interface IPosterLoadingCallback{
        void onBitmapGet(String key, Drawable drawable);
    }

    private TMoviesDBApi() {
        ITMoviesDBApi = RetrofitConfig.getRetrofitClient().create(ITMoviesDBApi.class);
    }

    public static TMoviesDBApi getInstance(){
        if (tMoviesDBApi == null){
            tMoviesDBApi = new TMoviesDBApi();
        }
        return tMoviesDBApi;
    }

    private Call<TopRatedMovies> getTopRatedMovies(int currentPage) {
        return ITMoviesDBApi.getTopRatedMovies(API_KEY, LANGUAGE_EN, currentPage);
    }

    private Call<MovieGenres> getGenres() {
        return ITMoviesDBApi.getMovieGenres(API_KEY, LANGUAGE_EN);
    }

    public void loadGenres(){
        getGenres().enqueue(new Callback<MovieGenres>() {
            @Override
            public void onResponse(@NotNull Call<MovieGenres> call, @NotNull Response<MovieGenres> response) {
                movieGenres = response.body();
            }

            @Override
            public void onFailure(@NotNull Call<MovieGenres> call, @NotNull Throwable t) {
            }
        });
    }

    public void loadPage(final IDataCallback iDataCallback, int currentPage){
        tMoviesDBApi.getTopRatedMovies(currentPage).enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(@NotNull Call<TopRatedMovies> call, @NotNull Response<TopRatedMovies> response) {
                iDataCallback.onResponse(response);
            }
            @Override
            public void onFailure(@NotNull Call<TopRatedMovies> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MovieGenres getMovieGenres() {
        return movieGenres;
    }

    public void loadImage(final Context context, boolean isPoster, final boolean isRound
            , final String imagePath, final IPosterLoadingCallback iPosterLoadingCallback){
        String currentImagePath;
        if (isPoster){
            currentImagePath = getBaseImageUrlStrByWidth(DEFAULT_IMAGE_SIZE_W).concat(imagePath);
        } else {
            currentImagePath = imagePath;
        }
        Glide.with(context)
                .load(currentImagePath)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        if (isRound){
                            circularBitmapDrawable.setCircular(true);
                        }
                        iPosterLoadingCallback.onBitmapGet(imagePath, circularBitmapDrawable);
                    }
                });
    }
}
