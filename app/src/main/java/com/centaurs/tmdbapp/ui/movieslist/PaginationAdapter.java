package com.centaurs.tmdbapp.ui.movieslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.models.Movie;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static com.centaurs.tmdbapp.util.Constants.INPUT_DATE_FORMAT_STRING;
import static com.centaurs.tmdbapp.util.Constants.OUTPUT_DATE_FORMAT_STRING;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    static final int ITEM_SPAN_SIZE = 2;
    private static final int LOADING_SPAN_SIZE = 1;
    private PaginationAdapter.OnItemClickListener onItemClickListener;
    private PaginationAdapter.OnItemLongClickListener onItemLongClickListener;
    private OnNeedPosterListener onNeedPosterListener;
    private int itemViewRes;
    private List<Movie> movies;
    private List<Integer> selectedMoviesIdsList;
    private boolean isLoadingAdded = false;
    @Inject
    Context context;
    
    interface OnItemClickListener{
        void onItemClick(int movieId);
    }

    interface OnItemLongClickListener{
        void onItemLongClick(int movieId);
    }
    
    interface OnNeedPosterListener {
        void onNeedPoster(ImageView imageView, String posterPath);
    }

    PaginationAdapter(PaginationAdapter.OnItemClickListener onItemClickListener
            , PaginationAdapter.OnItemLongClickListener onItemLongClickListener
            , OnNeedPosterListener onNeedPosterListener, int itemViewRes) {
        this.onNeedPosterListener = onNeedPosterListener;
        this.itemViewRes = itemViewRes;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    void setSelectedMoviesIdsList(List<Integer> selectedMoviesIdsList){
        this.selectedMoviesIdsList = selectedMoviesIdsList;
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
        final MovieViewHolder viewHolder = new MovieViewHolder(viewToAdd);
        setItemBackgroundDefault( viewHolder.getItemView());
        viewHolder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onItemLongClick(viewHolder.getLayoutPosition());;
                return true;
            }
        });

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
        Movie movie = movies.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
                if (!selectedMoviesIdsList.isEmpty() && selectedMoviesIdsList.contains(position)){
                    setItemBackgroundSelected(movieViewHolder.getItemView());
                } else {
                    setItemBackgroundDefault(movieViewHolder.getItemView());
                }

                if (movie.getTitle() != null){
                    movieViewHolder.setTitle(movie.getTitle());
                }
                String yearString = "";
                try {
                    Date date = new SimpleDateFormat(INPUT_DATE_FORMAT_STRING, Locale.getDefault()).parse(movie.getReleaseDate());
                    yearString = new SimpleDateFormat(OUTPUT_DATE_FORMAT_STRING, Locale.getDefault()).format(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (movie.getReleaseDate() != null){
                    movieViewHolder.setAdditionalInfo(context.getString(R.string.year_and_language
                            , yearString, movie.getOriginalLanguage().toUpperCase()));
                }
                if (movie.getVoteAverage() != null){
                    movieViewHolder.setRating(String.valueOf(movie.getVoteAverage()));
                }
                onNeedPosterListener.onNeedPoster(movieViewHolder.getPosterImageView(), movie.getPosterPath());
                break;
            case LOADING:
                break;
        }
    }

    private void setItemBackgroundDefault(View itemView){
        itemView.setBackgroundColor(context.getResources().getColor(R.color.item_movie_card_background));
    }

    private void setItemBackgroundSelected(View itemView){
        itemView.setBackgroundColor(context.getResources().getColor(R.color.selected_item_background));
    }

    private GridLayoutManager.SpanSizeLookup onSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return getItemViewType(position) == 0 ? LOADING_SPAN_SIZE : ITEM_SPAN_SIZE;
        }
    };

    GridLayoutManager.SpanSizeLookup getOnSpanSizeLookup(){
        return onSpanSizeLookup;
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0
                : movies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movies.size() - 1
                && isLoadingAdded) ? LOADING : ITEM;
    }

    void notifyInserted(boolean isLoadingAdded, int startPosition, int count){
        this.isLoadingAdded = isLoadingAdded;
        notifyItemRangeInserted(startPosition, count);
    }

    void notifyRemoved(boolean isLoadingAdded, int positionStart, int itemCount){
        this.isLoadingAdded = isLoadingAdded;
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}