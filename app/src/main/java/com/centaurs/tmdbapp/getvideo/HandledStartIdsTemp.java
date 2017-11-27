package com.centaurs.tmdbapp.getvideo;


import android.content.Context;
import android.content.SharedPreferences;

import com.centaurs.tmdbapp.di.Injector;

import javax.inject.Inject;

public class HandledStartIdsTemp {
    private final String MOVIES_URL_PREFERENCES = "MovieUrls";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Inject
    Context context;

    HandledStartIdsTemp(){
        Injector.getInstance().getMovieAppComponent().inject(this);
        sharedPreferences = context.getSharedPreferences(MOVIES_URL_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    void putHandledStartId(int id, boolean isSuccess){
        editor.putBoolean(String.valueOf(id), isSuccess).commit();
    }

    void clearTemp(){
        editor.clear().commit();
    }

    boolean isExistsId(int id){
        return sharedPreferences.contains(String.valueOf(id));
    }

    boolean isSuccessId(int id){
        if (isExistsId(id)){
            return sharedPreferences.getBoolean(String.valueOf(id), false);
        }
        return false;
    }
}
