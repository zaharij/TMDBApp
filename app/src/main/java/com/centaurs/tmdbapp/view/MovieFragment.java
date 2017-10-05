package com.centaurs.tmdbapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.presenter.pclasses.MoviePresenter;
import com.centaurs.tmdbapp.presenter.pinterfaces.MoviePresenterInterface;
import com.centaurs.tmdbapp.view.vinterfaces.MovieViewInterface;

public class MovieFragment extends Fragment implements MovieViewInterface{
    private static final String MOVIE_ID_ARG = "movie_id";
    private ImageView posterImageView;
    private TextView titleTextView, additionalInfoTextView, genresTextView, overviewTextView;
    private MoviePresenterInterface moviePresenterInterface;

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
        if (savedInstanceState == null) {
            moviePresenterInterface = MoviePresenter.getMoviePresenterInterface(getActivity()
                    , getArguments().getInt(MOVIE_ID_ARG));
        } else {
            moviePresenterInterface = (MoviePresenterInterface)
                    PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        titleTextView = view.findViewById(R.id.single_movie_title_textView);
        additionalInfoTextView = view.findViewById(R.id.single_movie_year_textView);
        genresTextView = view.findViewById(R.id.single_movie_genres_textView);
        overviewTextView = view.findViewById(R.id.single_movie_overview_textView);
        posterImageView = view.findViewById(R.id.poster_single_imageView);
        moviePresenterInterface.attachView(this);
        moviePresenterInterface.refreshActivity(getActivity());
        moviePresenterInterface.setTextContent();
        moviePresenterInterface.setPoster();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        moviePresenterInterface.detachView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(moviePresenterInterface, outState);
    }

    @Override
    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void setAdditionalInfoYearLanguageStr(String additionalInfo) {
        additionalInfoTextView.setText(additionalInfo);
    }

    @Override
    public void setGenres(String genres) {
        genresTextView.setText(genres);
    }

    @Override
    public void setOverview(String overview) {
        overviewTextView.setText(overview);
    }

    @Override
    public ImageView getPosterImageView() {
        return posterImageView;
    }
}
