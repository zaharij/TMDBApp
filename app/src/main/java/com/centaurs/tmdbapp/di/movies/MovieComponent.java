package com.centaurs.tmdbapp.di.movies;

import com.centaurs.tmdbapp.ui.login.UserLoginFragment;
import com.centaurs.tmdbapp.ui.moviedetail.MovieDetailFragment;
import com.centaurs.tmdbapp.ui.movieslist.MoviesListFragment;
import com.centaurs.tmdbapp.ui.movieslist.PaginationAdapter;
import com.centaurs.tmdbapp.ui.networktroubles.NetworkConnectionTroublesFragment;

import dagger.Subcomponent;

@MovieScope
@Subcomponent(modules = {PresenterModule.class, FragmentActivityModule.class})
public interface MovieComponent {
    void inject(UserLoginFragment userLoginFragment);
    void inject(MovieDetailFragment movieDetailFragment);
    void inject(MoviesListFragment moviesListFragment);
    void inject(NetworkConnectionTroublesFragment networkConnectionTroublesFragment);
    void inject(PaginationAdapter paginationAdapter);

    @Subcomponent.Builder
    interface Builder {
        MovieComponent.Builder fragmentActivityModule(FragmentActivityModule fragmentActivityModule);
        MovieComponent build();
    }
}
