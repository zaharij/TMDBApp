package com.centaurs.tmdbapp.presenter.pinterfaces;


import android.view.View;

import com.centaurs.tmdbapp.view.vinterfaces.ViewInterface;

public interface PresenterInterface {
    void attachView(ViewInterface viewInterface);
    void detachView();
    void onClickButton(View view);
}
