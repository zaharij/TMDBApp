package com.centaurs.tmdbapp.presenter.pclasses;


import android.graphics.Point;
import android.view.Display;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.model.MovieResultsSingleton;
import com.centaurs.tmdbapp.model.models.Genre;
import com.centaurs.tmdbapp.presenter.pinterfaces.MoviePresenterInterface;
import com.centaurs.tmdbapp.view.vinterfaces.MovieViewInterface;
import com.centaurs.tmdbapp.view.vinterfaces.ViewInterface;

import java.util.List;

import static com.centaurs.tmdbapp.model.constants.Constants.DIFFERENCE_BETWEEN_SCREEN_AND_POSTER_SIZE_PERCENT;
import static com.centaurs.tmdbapp.model.constants.Constants.EMPTY_STRING;
import static com.centaurs.tmdbapp.model.constants.Constants.GET_DATE_INDEXES_ARR;
import static com.centaurs.tmdbapp.model.constants.Constants.VERTICAL_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.Constants.WORDS_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.DEFAULT_IMAGE_SIZE_W;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.getBaseImageUrlStrByWidth;

public class MoviePresenter implements MoviePresenterInterface {
    private MovieViewInterface movieViewInterface;
    private MovieResultsSingleton movieResultsSingleton;
    private int movieId;

    private MoviePresenter(int movieId){
        this.movieId = movieId;
        movieResultsSingleton = MovieResultsSingleton.getMovieResultsSingleton();
    }

    public static MoviePresenterInterface getMoviePresenterInterface(int movieId){
        return new MoviePresenter(movieId);
    }

    @Override
    public void attachView(ViewInterface viewInterface) {
        movieViewInterface = (MovieViewInterface) viewInterface;
     }

    @Override
    public void detachView() {
        movieViewInterface = null;
    }

    @Override
    public void onClickButton(View view) {
    }

    @Override
    public void setTextContent(){
        movieViewInterface.setTitle(movieResultsSingleton.getMovieResults().get(movieId).getTitle());
        movieViewInterface.setAdditionalInfoYearLanguageStr(movieResultsSingleton.getMovieResults().get(movieId).getReleaseDate()
                .substring(GET_DATE_INDEXES_ARR[0], GET_DATE_INDEXES_ARR[1]).concat(VERTICAL_DIVIDER)
                .concat(movieResultsSingleton.getMovieResults().get(movieId).getOriginalLanguage().toUpperCase()));
        movieViewInterface.setGenres(movieViewInterface.getActivityForOuterCall().getString(R.string.genre).concat(getGenres()));
        movieViewInterface.setOverview(movieResultsSingleton.getMovieResults().get(movieId).getOverview());
    }

    @Override
    public void setPoster() {
        Display display = movieViewInterface.getActivityForOuterCall().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y - (size.y * DIFFERENCE_BETWEEN_SCREEN_AND_POSTER_SIZE_PERCENT) / 100;
        Glide.with(movieViewInterface.getActivityForOuterCall()).load(getBaseImageUrlStrByWidth(DEFAULT_IMAGE_SIZE_W)
                + movieResultsSingleton.getMovieResults().get(movieId).getPosterPath())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model
                            , Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model
                            , Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().crossFade()
                .override(width, height).into(movieViewInterface.getPosterImageView());
    }

    private String getGenres(){
        List<Integer> genreIds = movieResultsSingleton.getMovieResults().get(movieId).getGenreIds();
        StringBuilder genresStrBuilder = new StringBuilder();
        for (int i = 0; i < genreIds.size(); i++){
            if (i != 0 && i < genreIds.size()){
                genresStrBuilder.append(WORDS_DIVIDER);
            }
            if (movieResultsSingleton.getMovieGenres() != null){
                genresStrBuilder.append(getGenreByIdFromList(movieResultsSingleton.getMovieGenres().getGenres(), genreIds.get(i)));
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
