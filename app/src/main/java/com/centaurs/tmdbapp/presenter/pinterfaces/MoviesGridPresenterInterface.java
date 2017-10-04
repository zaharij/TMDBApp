package com.centaurs.tmdbapp.presenter.pinterfaces;


import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;

public interface MoviesGridPresenterInterface extends PresenterInterface {
    void loadFirstPage(FragmentActivity fragmentActivity);
    void loadNextPage(FragmentActivity fragmentActivity);
    void setRecyclerView(RecyclerView recyclerView, FragmentActivity fragmentActivity);
    void setMainProgressVisibility();
}
