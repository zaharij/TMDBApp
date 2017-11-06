package com.centaurs.tmdbapp.dagger2;

import com.centaurs.tmdbapp.ui.login.UserLoginPresenter;

import dagger.Component;

@MovieActivityScope
@Component(modules = {FragmentActivityModule.class}, dependencies = MovieAppComponent.class)
public interface MovieActivityComponent {
    void injectUserLoginPresenter(UserLoginPresenter userLoginPresenter);
}
