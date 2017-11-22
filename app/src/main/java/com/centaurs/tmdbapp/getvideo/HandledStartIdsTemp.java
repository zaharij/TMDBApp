package com.centaurs.tmdbapp.getvideo;


import android.content.Context;
import android.content.SharedPreferences;

class HandledStartIdsTemp {
    private final String MOVIES_URL_PREFERENCES = "MovieUrls";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    HandledStartIdsTemp(Context context){
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
