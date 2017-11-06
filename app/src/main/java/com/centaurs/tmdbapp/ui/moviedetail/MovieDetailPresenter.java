package com.centaurs.tmdbapp.ui.moviedetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.data.MoviesApi;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.util.NetworkConnectionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import static com.centaurs.tmdbapp.util.Constants.INPUT_DATE_FORMAT_STRING;
import static com.centaurs.tmdbapp.util.Constants.OUTPUT_DATE_FORMAT_STRING;
import static com.centaurs.tmdbapp.util.Constants.WORDS_DIVISOR;

public class MovieDetailPresenter implements IMovieDetailContract.IPresenter {
    private final String TAG = "MovieDetailPresenter";
    private IMovieDetailContract.IView view;
    @Inject
    NetworkConnectionUtil networkConnectionUtil;
    @Inject
    MoviesApi moviesApi;
    @Inject
    ImageLoader imageLoader;
    @Inject
    Context context;

    @Override
    public void attachView(IMovieDetailContract.IView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onViewResumed(final int movieId) {
        if (networkConnectionUtil.isNetworkConnected()){
            view.showPosterLoadingProgress();
            moviesApi.loadMovie(new MoviesApi.IDataCallback<Movie>() {
                @Override
                public void onResponse(final Movie movie) {
                    view.setGenres(context.getString(R.string.genre).concat(getGenres(movie)));
                    view.setTitle(movie.getTitle());
                    String yearString = "";
                    try {
                        Date date = new SimpleDateFormat(INPUT_DATE_FORMAT_STRING, Locale.getDefault()).parse(movie.getReleaseDate());
                        yearString = new SimpleDateFormat(OUTPUT_DATE_FORMAT_STRING, Locale.getDefault()).format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    view.setAdditionalInformation(context
                            .getString(R.string.year_and_language, yearString, movie.getOriginalLanguage().toUpperCase()));
                    view.setOverview(movie.getOverview());
                    imageLoader.loadPoster( movie.getPosterPath(),true, posterLoadingCallback);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(TAG, throwable.getMessage());
                    if (!networkConnectionUtil.isNetworkConnected()){
                        view.goToNetworkConnectionTroublesFragment();
                    } else {
                        view.showSomethingWrongMessage();
                    }
                }
            }, movieId);
        } else {
            view.goToNetworkConnectionTroublesFragment();
        }
    }

    private ImageLoader.IPosterLoadingCallback posterLoadingCallback = new ImageLoader.IPosterLoadingCallback() {
        @Override
        public void onReturnImageResult(String key, @Nullable Drawable drawable) {
            view.hidePosterLoadingProgress();
            if(drawable != null){
                view.setPoster(drawable);
            } else {
                view.showSomethingWrongImage();
            }
        }
    };

    private String getGenres(Movie result){
        StringBuilder genresStrBuilder = new StringBuilder();
        for (int i = 0; i < result.getGenres().size(); i++){
            if (i != 0 && i < result.getGenres().size()){
                genresStrBuilder.append(WORDS_DIVISOR);
            }
            genresStrBuilder.append(result.getGenres().get(i).getName());
        }
        return genresStrBuilder.toString();
    }
}
