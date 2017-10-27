package com.centaurs.tmdbapp;


import android.app.Application;
import android.content.Context;

import com.centaurs.tmdbapp.data.ImageLoader;

public class MovieApplication extends Application {

    public interface IOnNeedAppContext {
        Context getContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoader.getInstance(onNeedContext);
    }

    private IOnNeedAppContext onNeedContext = new IOnNeedAppContext() {
        @Override
        public Context getContext() {
            return getApplicationContext();
        }
    };
}
