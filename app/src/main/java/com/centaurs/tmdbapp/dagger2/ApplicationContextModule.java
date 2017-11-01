package com.centaurs.tmdbapp.dagger2;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationContextModule {
    private Context context;

    public ApplicationContextModule(Context context){
        this.context = context;
    }

    @Provides
    public Context context(){
        return context;
    }
}
