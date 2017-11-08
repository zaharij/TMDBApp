package com.centaurs.tmdbapp.di;

import com.centaurs.tmdbapp.di.movies.FragmentActivityModule;
import com.centaurs.tmdbapp.di.movies.MovieComponent;
import com.centaurs.tmdbapp.di.movies.PresenterModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, MovieDbModule.class})
public interface MovieAppComponent {
    MovieComponent plusMovieComponent(PresenterModule presenterModule, FragmentActivityModule fragmentActivityModule);
}
