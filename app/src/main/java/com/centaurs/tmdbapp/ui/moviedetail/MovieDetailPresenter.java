package com.centaurs.tmdbapp.ui.moviedetail;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.data.MoviesApi;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.util.NetworkConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.centaurs.tmdbapp.util.Constants.INPUT_DATE_FORMAT_STRING;
import static com.centaurs.tmdbapp.util.Constants.OUTPUT_DATE_FORMAT_STRING;
import static com.centaurs.tmdbapp.util.Constants.WORDS_DIVIDER;

class MovieDetailPresenter implements IMovieDetailContract.IPresenter {
    private final String TAG = "MovieDetailPresenter";
    private IMovieDetailContract.IView view;

    MovieDetailPresenter (){
    }

    @Override
    public void attachView(IMovieDetailContract.IView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onViewAttached(final Context context, final int movieId) {
        if (NetworkConnection.isNetworkConnected(context)){
            MoviesApi.getInstance().loadMovie(new MoviesApi.IDataCallback<Movie>() {
                @Override
                public void onResponse(final Movie movie) {
                    view.setGenres(context.getResources().getString(R.string.genre).concat(getGenres(movie)));
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
                    ImageLoader.getInstance().loadPoster( movie.getPosterPath(),true, posterLoadingCallback);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(TAG, throwable.getMessage());
                }
            }, movieId);
        } else {
            view.goToNetworkConnectionTroublesFragment();
        }
    }

    private ImageLoader.IPosterLoadingCallback posterLoadingCallback = new ImageLoader.IPosterLoadingCallback() {
        @Override
        public void onReturnImageResult(String key, Drawable drawable) {
            view.setPoster(drawable);
        }
    };

    private String getGenres(Movie result){
        StringBuilder genresStrBuilder = new StringBuilder();
        for (int i = 0; i < result.getGenres().size(); i++){
            if (i != 0 && i < result.getGenres().size()){
                genresStrBuilder.append(WORDS_DIVIDER);
            }
            genresStrBuilder.append(result.getGenres().get(i).getName());
        }
        return genresStrBuilder.toString();
    }
}
