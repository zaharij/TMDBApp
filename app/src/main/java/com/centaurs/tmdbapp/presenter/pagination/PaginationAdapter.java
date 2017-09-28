package com.centaurs.tmdbapp.presenter.pagination;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.Result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.centaurs.tmdbapp.model.constants.Constants.GET_DATE_INDEXES_ARR;
import static com.centaurs.tmdbapp.model.constants.Constants.VERTICAL_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.BASE_URL_IMG;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable{
    private static PaginationAdapter paginationAdapter;
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Result> movieResults;
    private MovieGenres movieGenres;
    private Context context;

    private boolean isLoadingAdded = false;
    View viewToAdd;

    public static PaginationAdapter getPaginationAdapter(Context context){
        if (paginationAdapter != null){
            return paginationAdapter;
        } else {
            paginationAdapter = new PaginationAdapter(context);
            return paginationAdapter;
        }
    }

    private PaginationAdapter(Context context) {
        this.context = context;
        movieResults = new ArrayList<>();
    }

    public void setMovieGenres(MovieGenres movieGenres){
        this.movieGenres = movieGenres;
    }

    public MovieGenres getMovieGenres(){
        return movieGenres;
    }

    public Result getResult(int i){
        return movieResults.get(i);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View view = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(view);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        viewToAdd = inflater.inflate(R.layout.item_list, parent, false);
        viewHolder = new MovieViewHolder(viewToAdd);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Result result = movieResults.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
                movieViewHolder.posterImageView.setId(position);
                movieViewHolder.movieTitleTextView.setText(result.getTitle());
                movieViewHolder.yearCountryTextView.setText(result.getReleaseDate()
                        .substring(GET_DATE_INDEXES_ARR[0], GET_DATE_INDEXES_ARR[1])
                        .concat(VERTICAL_DIVIDER).concat(result.getOriginalLanguage().toUpperCase()));
                movieViewHolder.movieRatingTextView.setText(context.getResources().getString(R.string.rating)
                        .concat(String.valueOf(result.getVoteAverage())));
                Glide.with(context).load(BASE_URL_IMG + result.getPosterPath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model
                                    , Target<GlideDrawable> target, boolean isFirstResource) {
                                movieViewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model
                                    , Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                movieViewHolder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().crossFade().into(movieViewHolder.posterImageView);
                break;

            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movieResults == null ? 0 : movieResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(Result r) {
        movieResults.add(r);
        notifyItemInserted(movieResults.size() - 1);
    }

    public void addAll(List<Result> moveResults) {
        for (Result result : moveResults) {
            add(result);
        }
    }

    public void remove(Result r) {
        int position = movieResults.indexOf(r);
        if (position > -1) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Result());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResults.size() - 1;
        Result result = getItem(position);

        if (result != null) {
            movieResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Result getItem(int position) {
        return movieResults.get(position);
    }

    protected class MovieViewHolder extends RecyclerView.ViewHolder{
        private TextView movieTitleTextView;
        private TextView movieRatingTextView;
        private TextView yearCountryTextView;
        private ImageView posterImageView;
        private ProgressBar progressBar;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieTitleTextView = (TextView) itemView.findViewById(R.id.movie_title);
            movieRatingTextView = (TextView) itemView.findViewById(R.id.movie_rating);
            yearCountryTextView = (TextView) itemView.findViewById(R.id.movie_year);
            posterImageView = (ImageView) itemView.findViewById(R.id.movie_poster);
            progressBar = (ProgressBar) itemView.findViewById(R.id.movie_progress);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder{

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }
}
