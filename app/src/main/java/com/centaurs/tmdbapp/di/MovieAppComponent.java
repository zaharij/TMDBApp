package com.centaurs.tmdbapp.di;

import com.centaurs.tmdbapp.di.movies.MovieComponent;
import com.centaurs.tmdbapp.getvideo.HandledStartIdsTemp;
import com.centaurs.tmdbapp.getvideo.VideoLoader;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, MovieDbModule.class})
public interface MovieAppComponent {
    MovieComponent.Builder movieComponentBuilder();

    void inject(VideoLoader videoLoader);
    void inject(HandledStartIdsTemp handledStartIdsTemp);
}
