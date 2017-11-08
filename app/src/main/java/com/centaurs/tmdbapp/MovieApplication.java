package com.centaurs.tmdbapp;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.FragmentActivity;

import com.centaurs.tmdbapp.di.ApplicationModule;
import com.centaurs.tmdbapp.di.DaggerMovieAppComponent;
import com.centaurs.tmdbapp.di.MovieAppComponent;
import com.centaurs.tmdbapp.di.movies.FragmentActivityModule;
import com.centaurs.tmdbapp.di.movies.MovieComponent;
import com.centaurs.tmdbapp.di.movies.PresenterModule;

public class MovieApplication extends Application {
    private MovieAppComponent movieAppComponent;
    private MovieComponent movieComponent;

    public static MovieApplication get(Activity activity){
        return (MovieApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        movieAppComponent = DaggerMovieAppComponent.builder()
                .applicationModule(new ApplicationModule(getApplicationContext()))
                .build();
    }

    public MovieComponent plusMovieComponent(FragmentActivity fragmentActivity){
        if (movieComponent == null){
            movieComponent = movieAppComponent.plusMovieComponent(new PresenterModule()
                    , new FragmentActivityModule(fragmentActivity));
        }
        return movieComponent;
    }

    public void clearMovieComponent(){
        movieComponent = null;
    }
}
