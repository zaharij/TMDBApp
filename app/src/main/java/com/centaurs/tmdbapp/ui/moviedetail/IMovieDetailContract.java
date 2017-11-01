package com.centaurs.tmdbapp.ui.moviedetail;


import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.ui.IBasePresenter;

interface IMovieDetailContract {
    interface IView {
        void setTitle(String title);
        void setAdditionalInformation(String additionalInformation);
        void setGenres(String genres);
        void setOverview(String overview);
        void setPoster(Drawable drawable);
        void goToNetworkConnectionTroublesFragment();
        void showPosterLoadingProgress();
        void hidePosterLoadingProgress();
        void showSomethingWrongMessage();
        void showSomethingWrongImage();
    }

    interface IPresenter extends IBasePresenter<IView>{
        void onViewResumed(int movieId);
    }
}
