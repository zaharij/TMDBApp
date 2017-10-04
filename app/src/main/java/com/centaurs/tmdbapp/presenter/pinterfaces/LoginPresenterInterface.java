package com.centaurs.tmdbapp.presenter.pinterfaces;


import android.content.Intent;

public interface LoginPresenterInterface extends PresenterInterface {
    void checkAuthentication();
    void checkIfRCSignIn(int requestQode, Intent data);
}
