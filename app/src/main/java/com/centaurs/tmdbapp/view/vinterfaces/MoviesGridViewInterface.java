package com.centaurs.tmdbapp.view.vinterfaces;


import android.view.View;

import com.centaurs.tmdbapp.view.MovieViewHolder;

public interface MoviesGridViewInterface extends ViewInterface {
    void setMainProgressVisibility(boolean isMainProgressVisible);
    MovieViewHolder getMovieViewHolder(View view);
}
