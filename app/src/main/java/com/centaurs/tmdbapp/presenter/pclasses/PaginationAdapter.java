package com.centaurs.tmdbapp.presenter.pclasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.model.MovieResultsSingleton;
import com.centaurs.tmdbapp.model.models.Result;
import com.centaurs.tmdbapp.view.MovieViewHolder;
import com.centaurs.tmdbapp.view.vinterfaces.MoviesGridViewInterface;

import java.util.List;

import static com.centaurs.tmdbapp.model.constants.Constants.GET_DATE_INDEXES_ARR;
import static com.centaurs.tmdbapp.model.constants.Constants.VERTICAL_DIVIDER;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.DEFAULT_IMAGE_SIZE_W;
import static com.centaurs.tmdbapp.model.constants.QueryConstants.getBaseImageUrlStrByWidth;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private Context context;
    private MoviesGridViewInterface moviesGridViewInterface;
    private MovieResultsSingleton movieResultsSingleton;
    private PaginationAdapter.OnItemClickListener onItemClickListener;
    private int itemViewRes;

    public interface OnItemClickListener{
        void onItemClick(int itemId);
    }

    private boolean isLoadingAdded = false;

    PaginationAdapter(Context context, MovieResultsSingleton movieResultsSingleton
            , PaginationAdapter.OnItemClickListener onItemClickListener, int itemViewRes) {
        this.itemViewRes = itemViewRes;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
        this.movieResultsSingleton = movieResultsSingleton;
    }

    void attachViewAdapter(MoviesGridViewInterface moviesGridViewInterface){
        this.moviesGridViewInterface = moviesGridViewInterface;
    }

    void detachViewAdapter(){
        this.moviesGridViewInterface = null;
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
        View viewToAdd = inflater.inflate(itemViewRes, parent, false);
        final MovieViewHolder viewHolder = moviesGridViewInterface.getMovieViewHolder(viewToAdd);
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
        Result result = movieResultsSingleton.getMovieResults().get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
                if (result.getTitle() != null){
                    movieViewHolder.setTitle(result.getTitle());
                }
                if (result.getReleaseDate() != null){
                    movieViewHolder.setAdditionalInfo(result.getReleaseDate()
                            .substring(GET_DATE_INDEXES_ARR[0], GET_DATE_INDEXES_ARR[1])
                            .concat(VERTICAL_DIVIDER).concat(result.getOriginalLanguage().toUpperCase()));
                }
                if (result.getVoteAverage() != null){
                    movieViewHolder.setRating(context.getString(R.string.rating)
                            .concat(String.valueOf(result.getVoteAverage())));
                }
                Glide.with(context).load(getBaseImageUrlStrByWidth(DEFAULT_IMAGE_SIZE_W) + result.getPosterPath())
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model
                                    , Target<GlideDrawable> target, boolean isFirstResource) {
                                movieViewHolder.hideProgress();
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model
                                    , Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                movieViewHolder.hideProgress();
                                return false;
                            }
                        }).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().crossFade()
                        .into(movieViewHolder.getPosterImageView());
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movieResultsSingleton.getMovieResults() == null ? 0
                : movieResultsSingleton.getMovieResults().size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieResultsSingleton.getMovieResults().size() - 1
                && isLoadingAdded) ? LOADING : ITEM;
    }

    private void add(Result r) {
        movieResultsSingleton.addMovieResult(r);
        notifyItemInserted(movieResultsSingleton.getMovieResults().size() - 1);
    }

    void addAll(List<Result> moveResults) {
        movieResultsSingleton.addAllMovieResults(moveResults);
        notifyItemInserted(movieResultsSingleton.getMovieResults().size() - 1);
    }

    void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Result());
    }

    void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieResultsSingleton.getMovieResults().size() - 1;
        Result result = getItem(position);

        if (result != null) {
            movieResultsSingleton.getMovieResults().remove(position);
            notifyItemRemoved(position);
        }
    }

    private Result getItem(int position) {
        return movieResultsSingleton.getMovieResults().get(position);
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{

        private LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
