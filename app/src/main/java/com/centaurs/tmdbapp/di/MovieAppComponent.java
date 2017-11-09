package com.centaurs.tmdbapp.di;

import com.centaurs.tmdbapp.di.movies.MovieComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, MovieDbModule.class})
public interface MovieAppComponent {
    MovieComponent.Builder movieComponentBuilder();
}
