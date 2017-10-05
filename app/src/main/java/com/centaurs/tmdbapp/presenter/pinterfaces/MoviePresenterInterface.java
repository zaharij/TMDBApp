package com.centaurs.tmdbapp.presenter.pinterfaces;


import android.support.v4.app.FragmentActivity;

public interface MoviePresenterInterface extends PresenterInterface {
    void setTextContent();
    void setPoster();
    void refreshActivity(FragmentActivity fragmentActivity);
}
