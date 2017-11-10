package com.centaurs.tmdbapp.di;

import android.content.Context;

import com.centaurs.tmdbapp.util.NetworkConnectionUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Context context;

    ApplicationModule(Context context){
        this.context = context;
    }

    @Provides
    public Context context(){
        return context;
    }

    @Provides
    @Singleton
    NetworkConnectionUtil networkConnectionUtil(Context context){
        return new NetworkConnectionUtil(context);
    }
}
