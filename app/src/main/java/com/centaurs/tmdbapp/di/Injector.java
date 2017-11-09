package com.centaurs.tmdbapp.di;


import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.centaurs.tmdbapp.di.movies.FragmentActivityModule;
import com.centaurs.tmdbapp.di.movies.MovieComponent;

public class Injector {
    private static Injector injector;
    private MovieAppComponent movieAppComponent;
    private MovieComponent movieComponent;

    private Injector (Context appContext){
        movieAppComponent = DaggerMovieAppComponent.builder()
                .applicationModule(new ApplicationModule(appContext))
                .build();
    }

    public static Injector getInstance(Context appContext){
        if (injector == null){
            injector = new Injector(appContext);
        }
        return injector;
    }

    public static Injector getInstance(){
        return injector;
    }

    public MovieComponent plusMovieComponent(FragmentActivity fragmentActivity){
        if (movieComponent == null){
            movieComponent = movieAppComponent.movieComponentBuilder()
                    .fragmentActivityModule(new FragmentActivityModule(fragmentActivity))
                    .build();
        }
        return movieComponent;
    }

    public void clearMovieComponent(){
        movieComponent = null;
    }

    public MovieComponent getMovieComponent() {
        return movieComponent;
    }
}
