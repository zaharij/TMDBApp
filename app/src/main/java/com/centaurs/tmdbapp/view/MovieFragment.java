package com.centaurs.tmdbapp.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.centaurs.tmdbapp.presenter.pagination.PaginationAdapter;

import java.util.List;

import static com.centaurs.tmdbapp.model.constants.Constants.BIG_POSTER_SIZE_W_H;
import static com.centaurs.tmdbapp.model.constants.Constants.EMPTY_STRING;
import static com.centaurs.tmdbapp.model.constants.Constants.GET_DATE_INDEXES_ARR;
import static com.centaurs.tmdbapp.model.constants.Constants.VERTICAL_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.Constants.WORDS_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.BASE_URL_IMG;

public class MovieFragment extends Fragment {
    private static final String MOVIE_ID_ARG = "movie_id";
    private ImageView posterImageView;
    private TextView titleTextView, yearTextView, genresTextView, overviewTextView;
    private PaginationAdapter paginationAdapter;
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
        paginationAdapter = PaginationAdapter.getPaginationAdapter(getActivity());
        movieId = getArguments().getInt(MOVIE_ID_ARG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        titleTextView = (TextView) view.findViewById(R.id.single_movie_title_textView);
        yearTextView = (TextView) view.findViewById(R.id.single_movie_year_textView);
        genresTextView = (TextView) view.findViewById(R.id.single_movie_genres_textView);
        overviewTextView = (TextView) view.findViewById(R.id.single_movie_overview_textView);
        posterImageView = (ImageView) view.findViewById(R.id.poster_single_imageView);
        manageContent();
        return view;
    }

    private void manageContent(){
        titleTextView.setText(paginationAdapter.getResult(movieId).getTitle());
        yearTextView.setText(paginationAdapter.getResult(movieId).getReleaseDate()
                .substring(GET_DATE_INDEXES_ARR[0], GET_DATE_INDEXES_ARR[1]).concat(VERTICAL_DIVIDER)
                .concat(paginationAdapter.getResult(movieId).getOriginalLanguage().toUpperCase()));
        genresTextView.setText(getActivity().getResources().getString(R.string.genre).concat(getGenres()));
        overviewTextView.setText(paginationAdapter.getResult(movieId).getOverview());;

        Glide.with(getActivity()).load(BASE_URL_IMG + paginationAdapter.getResult(movieId).getPosterPath())
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
                .override(BIG_POSTER_SIZE_W_H[0], BIG_POSTER_SIZE_W_H[1]).into(posterImageView);
    }

    private String getGenres(){
        List<Integer> genreIds = paginationAdapter.getResult(movieId).getGenreIds();
        StringBuilder genresStrBuilder = new StringBuilder();
        for (int i = 0; i < genreIds.size(); i++){
            if (i != 0 && i < genreIds.size()){
                genresStrBuilder.append(WORDS_DIVIDER);
            }
            genresStrBuilder.append(getGenreByIdFromList(paginationAdapter.getMovieGenres().getGenres(), genreIds.get(i)));
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
