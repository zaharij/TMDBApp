package com.centaurs.tmdbapp.presenter.pinterfaces;


import android.content.Intent;
import android.support.v4.app.FragmentActivity;

public interface LoginPresenterInterface extends PresenterInterface {
    void checkAuthentication();
    void checkIfRCSignIn(int requestQode, Intent data);
    void refreshActivity(FragmentActivity fragmentActivity);
}
