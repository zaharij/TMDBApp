package com.centaurs.tmdbapp.di.movies;

import android.content.Context;

import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.data.api.MoviesApi;
import com.centaurs.tmdbapp.ui.login.IUserLoginContract;
import com.centaurs.tmdbapp.ui.login.UserLoginPresenter;
import com.centaurs.tmdbapp.ui.moviedetail.IMovieDetailContract;
import com.centaurs.tmdbapp.ui.moviedetail.MovieDetailPresenter;
import com.centaurs.tmdbapp.ui.movieslist.IMoviesListContract;
import com.centaurs.tmdbapp.ui.movieslist.MoviesListPresenter;
import com.centaurs.tmdbapp.ui.networktroubles.INetworkConnectionTroublesContract;
import com.centaurs.tmdbapp.ui.networktroubles.NetworkConnectionTroublesPresenter;
import com.centaurs.tmdbapp.util.LoginHelper;
import com.centaurs.tmdbapp.util.NetworkConnectionUtil;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {

    @Provides
    @MovieScope
    IUserLoginContract.IPresenter userLoginPresenter(LoginHelper loginHelper
            , NetworkConnectionUtil networkConnectionUtil, ImageLoader imageLoader, Context context){
        return new UserLoginPresenter(loginHelper, networkConnectionUtil, imageLoader, context);
    }

    @Provides
    @MovieScope
    IMovieDetailContract.IPresenter movieDetailPresenter(NetworkConnectionUtil networkConnectionUtil, MoviesApi moviesApi
            , ImageLoader imageLoader, Context context){
        return new MovieDetailPresenter(networkConnectionUtil, moviesApi, imageLoader, context);
    }

    @Provides
    @MovieScope
    IMoviesListContract.IPresenter moviesListPresenter(NetworkConnectionUtil networkConnectionUtil, MoviesApi moviesApi
            , ImageLoader imageLoader, Context context){
        return new MoviesListPresenter(networkConnectionUtil, moviesApi, imageLoader, context);
    }

    @Provides
    @MovieScope
    INetworkConnectionTroublesContract.IPresenter networkConnectionTroublesPresenter(){
        return new NetworkConnectionTroublesPresenter();
    }
}
