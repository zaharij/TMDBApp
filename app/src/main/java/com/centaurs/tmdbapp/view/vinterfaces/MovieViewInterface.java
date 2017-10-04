package com.centaurs.tmdbapp.view.vinterfaces;


import android.widget.ImageView;

public interface MovieViewInterface extends ViewInterface {
    void setTitle(String title);
    void setAdditionalInfoYearLanguageStr(String additionalInfo);
    void setGenres(String genres);
    void setOverview(String overview);
    ImageView getPosterImageView();
}
