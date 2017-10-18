package com.centaurs.tmdbapp.ui.moviedetail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.models.Result;

public class MovieDetailFragment extends Fragment implements IMovieDetailContract.IView{
    private static final String MOVIE_ID_ARG = "movie_id";
    private ImageView posterImageView;
    private TextView titleTextView, additionalInfoTextView, genresTextView, overviewTextView;
    private IMovieDetailContract.IPresenter presenter;


    public static MovieDetailFragment getInstance(Result result){
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_ID_ARG, result);
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        movieDetailFragment.setArguments(args);
        return movieDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MovieDetailPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        posterImageView = view.findViewById(R.id.movie_detail_poster_image_view);
        titleTextView = view.findViewById(R.id.movie_detail_title_text_view);
        additionalInfoTextView = view.findViewById(R.id.movie_detail_additional_information_text_view);
        genresTextView = view.findViewById(R.id.movie_detail_genres_text_view);
        overviewTextView = view.findViewById(R.id.movie_detail_overview_text_view);
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewAttached(getContext(), (Result) getArguments().getSerializable(MOVIE_ID_ARG));
    }

    @Override
    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void setAdditionalInformation(String additionalInformation) {
        additionalInfoTextView.setText(additionalInformation);
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
    public void setPoster(Drawable drawable) {
        posterImageView.setImageDrawable(drawable);
    }
}
