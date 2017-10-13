package com.centaurs.tmdbapp.ui;

public interface IBasePresenter<V> {
    void attachView(V view);
    void detachView();
}
