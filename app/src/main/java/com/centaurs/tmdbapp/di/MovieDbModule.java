package com.centaurs.tmdbapp.di;

import android.content.Context;

import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.data.YoutubeApi;
import com.centaurs.tmdbapp.data.api.MoviesApi;
import com.centaurs.tmdbapp.data.fileload.FileLoader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class MovieDbModule {

    @Provides
    @Singleton
    MoviesApi apiModule(){
        return new MoviesApi();
    }

    @Provides
    @Singleton
    ImageLoader imageLoader(Context context, MoviesApi moviesApi){
        return new ImageLoader(context, moviesApi);
    }

    @Provides
    @Singleton
    FileLoader fileLoader(){
        return new FileLoader();
    }

    @Provides
    @Singleton
    YoutubeApi youtubeApi(){
        return new YoutubeApi();
    }
}
