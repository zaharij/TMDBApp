package com.centaurs.tmdbapp.di;

import android.content.Context;

import com.centaurs.tmdbapp.util.NetworkConnectionUtil;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private Context context;

    public ApplicationModule(Context context){
        this.context = context;
    }

    @Provides
    public Context context(){
        return context;
    }

    @Provides
    NetworkConnectionUtil networkConnectionUtil(Context context){
        return new NetworkConnectionUtil(context);
    }
}
