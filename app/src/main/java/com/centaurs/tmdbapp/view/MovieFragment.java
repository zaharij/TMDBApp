package com.centaurs.tmdbapp.view;


import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.model.models.Genre;
import com.centaurs.tmdbapp.presenter.pagination.MovieResultsSingletone;

import java.util.List;

import static com.centaurs.tmdbapp.model.constants.Constants.DIFFERENCE_BETWEEN_SCREEN_AND_POSTER_SIZE_PERCENT;
import static com.centaurs.tmdbapp.model.constants.Constants.EMPTY_STRING;
import static com.centaurs.tmdbapp.model.constants.Constants.GET_DATE_INDEXES_ARR;
import static com.centaurs.tmdbapp.model.constants.Constants.VERTICAL_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.Constants.WORDS_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.DEFAULT_IMAGE_SIZE_W;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.getBaseImageUrlStrByWidth;

public class MovieFragment extends Fragment {
    private static final String MOVIE_ID_ARG = "movie_id";
    private ImageView posterImageView;
    private TextView titleTextView, yearTextView, genresTextView, overviewTextView;
    private MovieResultsSingletone movieResultsSingletone;
    private int movieId;

    public static Fragment getInstance(int movieId){
        Bundle args = new Bundle();
        args.putInt(MOVIE_ID_ARG, movieId);

        MovieFragment signalFragment = new MovieFragment();
        signalFragment.setArguments(args);
        return signalFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieId = getArguments().getInt(MOVIE_ID_ARG);
        movieResultsSingletone = MovieResultsSingletone.getMovieResultsSingletone();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        titleTextView = view.findViewById(R.id.single_movie_title_textView);
        yearTextView = view.findViewById(R.id.single_movie_year_textView);
        genresTextView = view.findViewById(R.id.single_movie_genres_textView);
        overviewTextView = view.findViewById(R.id.single_movie_overview_textView);
        posterImageView = view.findViewById(R.id.poster_single_imageView);
        manageContent();
        return view;
    }

    private void manageContent(){
        titleTextView.setText(movieResultsSingletone.getMovieResults().get(movieId).getTitle());
        yearTextView.setText(movieResultsSingletone.getMovieResults().get(movieId).getReleaseDate()
                .substring(GET_DATE_INDEXES_ARR[0], GET_DATE_INDEXES_ARR[1]).concat(VERTICAL_DIVIDER)
                .concat(movieResultsSingletone.getMovieResults().get(movieId).getOriginalLanguage().toUpperCase()));
        genresTextView.setText(getString(R.string.genre).concat(getGenres()));
        overviewTextView.setText(movieResultsSingletone.getMovieResults().get(movieId).getOverview());
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y - (size.y * DIFFERENCE_BETWEEN_SCREEN_AND_POSTER_SIZE_PERCENT) / 100;
        Glide.with(getActivity()).load(getBaseImageUrlStrByWidth(DEFAULT_IMAGE_SIZE_W)
                + movieResultsSingletone.getMovieResults().get(movieId).getPosterPath())
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
                .override(width, height).into(posterImageView);
    }

    private String getGenres(){
        List<Integer> genreIds = movieResultsSingletone.getMovieResults().get(movieId).getGenreIds();
        StringBuilder genresStrBuilder = new StringBuilder();
        for (int i = 0; i < genreIds.size(); i++){
            if (i != 0 && i < genreIds.size()){
                genresStrBuilder.append(WORDS_DIVIDER);
            }
            genresStrBuilder.append(getGenreByIdFromList(movieResultsSingletone.getMovieGenres().getGenres(), genreIds.get(i)));
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
