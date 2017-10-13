package com.centaurs.tmdbapp.ui.moviedetail;


import android.content.Context;
import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.TMoviesDBApi;
import com.centaurs.tmdbapp.data.models.Genre;
import com.centaurs.tmdbapp.data.models.Result;

import java.util.List;

import static com.centaurs.tmdbapp.data.constants.Constants.EMPTY_STRING;
import static com.centaurs.tmdbapp.data.constants.Constants.GET_DATE_INDEXES_ARR;
import static com.centaurs.tmdbapp.data.constants.Constants.VERTICAL_DIVIDER;
import static com.centaurs.tmdbapp.data.constants.Constants.WORDS_DIVIDER;

class MovieDetailPresenter implements IMovieDetailContract.IPresenter {
    private IMovieDetailContract.IView iView;
    private TMoviesDBApi tMoviesDBApi;

    MovieDetailPresenter (){
        tMoviesDBApi = TMoviesDBApi.getInstance();
    }

    @Override
    public void attachView(IMovieDetailContract.IView view) {
        iView = view;
    }

    @Override
    public void detachView() {
        iView = null;
    }

    @Override
    public void onViewAttached(Context context, Result result) {
        iView.setTitle(result.getTitle());
        iView.setAdditionalInformation(result.getReleaseDate()
                .substring(GET_DATE_INDEXES_ARR[0], GET_DATE_INDEXES_ARR[1]).concat(VERTICAL_DIVIDER)
                .concat(result.getOriginalLanguage().toUpperCase()));
        iView.setGenres(context.getString(R.string.genre).concat(getGenres(result)));
        iView.setOverview(result.getOverview());
        tMoviesDBApi.loadImage(context, true, false, result.getPosterPath(), iPosterLoadingCallback);
    }

    private TMoviesDBApi.IPosterLoadingCallback iPosterLoadingCallback = new TMoviesDBApi.IPosterLoadingCallback() {
        @Override
        public void onBitmapGet(String key, Drawable drawable) {
            iView.setPoster(drawable);
        }
    };

    private String getGenres(Result result){
        List<Integer> genreIds = result.getGenreIds();
        StringBuilder genresStrBuilder = new StringBuilder();
        for (int i = 0; i < genreIds.size(); i++){
            if (i != 0 && i < genreIds.size()){
                genresStrBuilder.append(WORDS_DIVIDER);
            }
            if (tMoviesDBApi.getMovieGenres() != null){
                genresStrBuilder.append(getGenreByIdFromList(tMoviesDBApi.getMovieGenres().getGenres(), genreIds.get(i)));
            }
        }
        return genresStrBuilder.toString();
    }

    private String getGenreByIdFromList(List<Genre> genres, int id){
        for (int i = 0; i < genres.size(); i++){
            if (genres.get(i).getId() == id){
                return genres.get(i).getName();
            }
        }
        return EMPTY_STRING;
    }
}
