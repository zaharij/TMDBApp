package com.centaurs.tmdbapp;


import android.app.Application;

import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.util.NetworkConnectionUtil;

public class MovieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoader.getInstance(getApplicationContext());
        NetworkConnectionUtil.getInstance(getApplicationContext());
    }
}
