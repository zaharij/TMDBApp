package com.centaurs.tmdbapp.dagger2;

import android.content.Context;

import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.ui.moviedetail.MovieDetailPresenter;
import com.centaurs.tmdbapp.ui.movieslist.MoviesListPresenter;
import com.centaurs.tmdbapp.ui.movieslist.PaginationAdapter;
import com.centaurs.tmdbapp.util.NetworkConnectionUtil;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationContextModule.class)
public interface MovieAppComponent {
    void injectMovieDetailPresenter(MovieDetailPresenter movieDetailPresenter);
    void injectMoviesListPresenter(MoviesListPresenter moviesListPresenter);
    void injectPaginationAdapter(PaginationAdapter paginationAdapter);
    ImageLoader imageLoader();
    Context context();
    NetworkConnectionUtil networkConnectionUtil();
}
