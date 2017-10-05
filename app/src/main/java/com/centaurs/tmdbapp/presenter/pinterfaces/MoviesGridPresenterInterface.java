package com.centaurs.tmdbapp.presenter.pinterfaces;


import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

public interface MoviesGridPresenterInterface extends PresenterInterface {
    void loadFirstPage();
    void loadNextPage();
    void setRecyclerView(RecyclerView recyclerView);
    void setMainProgressVisibility();
    void refreshActivity(FragmentActivity fragmentActivity);
}
