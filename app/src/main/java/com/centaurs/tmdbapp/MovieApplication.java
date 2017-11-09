package com.centaurs.tmdbapp;


import android.app.Application;

import com.centaurs.tmdbapp.di.Injector;

public class MovieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Injector.getInstance(getApplicationContext());
    }
}
