package com.centaurs.tmdbapp;


import android.app.Activity;
import android.app.Application;

import com.centaurs.tmdbapp.dagger2.ApplicationContextModule;
import com.centaurs.tmdbapp.dagger2.DaggerMovieAppComponent;
import com.centaurs.tmdbapp.dagger2.MovieAppComponent;

public class MovieApplication extends Application {
    private MovieAppComponent movieAppComponent;

    public static MovieApplication get(Activity activity){
        return (MovieApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        movieAppComponent = DaggerMovieAppComponent.builder()
                .applicationContextModule(new ApplicationContextModule(getApplicationContext()))
                .build();
    }

    public MovieAppComponent getComponent(){
        return movieAppComponent;
    }
}
