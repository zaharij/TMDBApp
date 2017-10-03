package com.centaurs.tmdbapp.view;

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
import com.centaurs.tmdbapp.model.models.Result;
import com.centaurs.tmdbapp.presenter.pagination.MovieResultsSingletone;

import java.io.Serializable;
import java.util.List;

import static com.centaurs.tmdbapp.model.constants.Constants.GET_DATE_INDEXES_ARR;
import static com.centaurs.tmdbapp.model.constants.Constants.VERTICAL_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.DEFAULT_IMAGE_SIZE_W;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.getBaseImageUrlStrByWidth;

class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable{
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context context;
    private MovieResultsSingletone movieResultsSingletone;
    private PaginationAdapter.OnItemClickListener onItemClickListener;

    interface OnItemClickListener{
        void onItemClick(int itemId);
    }

    private boolean isLoadingAdded = false;

    PaginationAdapter(Context context, MovieResultsSingletone movieResultsSingletone, PaginationAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.movieResultsSingletone = movieResultsSingletone;
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
                viewHolder = new LoadingViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        View viewToAdd = inflater.inflate(R.layout.item_list, parent, false);
        final MovieViewHolder viewHolder = new MovieViewHolder(viewToAdd);
        viewHolder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(viewHolder.getLayoutPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Result result = movieResultsSingletone.getMovieResults().get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
                if (result.getTitle() != null){
                    movieViewHolder.movieTitleTextView.setText(result.getTitle());
                }
                if (result.getReleaseDate() != null){
                    movieViewHolder.yearCountryTextView.setText(result.getReleaseDate()
                            .substring(GET_DATE_INDEXES_ARR[0], GET_DATE_INDEXES_ARR[1])
                            .concat(VERTICAL_DIVIDER).concat(result.getOriginalLanguage().toUpperCase()));
                }
                if (result.getVoteAverage() != null){
                    movieViewHolder.movieRatingTextView.setText(context.getString(R.string.rating)
                            .concat(String.valueOf(result.getVoteAverage())));
                }
                Glide.with(context).load(getBaseImageUrlStrByWidth(DEFAULT_IMAGE_SIZE_W) + result.getPosterPath())
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
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().crossFade()
                        .into(movieViewHolder.posterImageView);
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movieResultsSingletone.getMovieResults() == null ? 0
                : movieResultsSingletone.getMovieResults().size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResultsSingletone.getMovieResults().size() - 1
                && isLoadingAdded) ? LOADING : ITEM;
    }

    private void add(Result r) {
        movieResultsSingletone.addMovieResult(r);
        notifyItemInserted(movieResultsSingletone.getMovieResults().size() - 1);
    }

    void addAll(List<Result> moveResults) {
        movieResultsSingletone.addAllMovieResults(moveResults);
        notifyItemInserted(movieResultsSingletone.getMovieResults().size() - 1);
    }

    void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Result());
    }

    void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResultsSingletone.getMovieResults().size() - 1;
        Result result = getItem(position);

        if (result != null) {
            movieResultsSingletone.getMovieResults().remove(position);
            notifyItemRemoved(position);
        }
    }

    private Result getItem(int position) {
        return movieResultsSingletone.getMovieResults().get(position);
    }

    private class MovieViewHolder extends RecyclerView.ViewHolder{
        private TextView movieTitleTextView;
        private TextView movieRatingTextView;
        private TextView yearCountryTextView;
        private ImageView posterImageView;
        private ProgressBar progressBar;
        private View itemView;

        private View getItemView() {
            return itemView;
        }

        private MovieViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            movieTitleTextView = itemView.findViewById(R.id.movie_title);
            movieRatingTextView = itemView.findViewById(R.id.movie_rating);
            yearCountryTextView = itemView.findViewById(R.id.movie_year);
            posterImageView = itemView.findViewById(R.id.movie_poster);
            progressBar = itemView.findViewById(R.id.movie_progress);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{

        private LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
