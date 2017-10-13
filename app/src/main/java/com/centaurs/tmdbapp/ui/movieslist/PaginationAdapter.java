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
import com.centaurs.tmdbapp.data.models.Result;

import java.util.List;

import static com.centaurs.tmdbapp.data.constants.Constants.GET_DATE_INDEXES_ARR;
import static com.centaurs.tmdbapp.data.constants.Constants.VERTICAL_DIVIDER;

class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    static final int ITEM_SPAN_SIZE = 2;
    private static final int LOADING_SPAN_SIZE = 1;
    private Context context;
    private PaginationAdapter.OnItemClickListener onItemClickListener;
    private OnNeedPosterListener onNeedPosterListener;
    private int itemViewRes;
    private List<Result> results;
    private boolean isLoadingAdded = false;
    
    interface OnItemClickListener{
        void onItemClick(Result result);
    }
    
    interface OnNeedPosterListener {
        void onNeedPoster(ImageView imageView, String posterPath);
    }

    PaginationAdapter(Context context, PaginationAdapter.OnItemClickListener onItemClickListener
            , OnNeedPosterListener onNeedPosterListener, int itemViewRes) {
        this.onNeedPosterListener = onNeedPosterListener;
        this.itemViewRes = itemViewRes;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    void setResults(List<Result> results) {
        this.results = results;
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
        viewHolder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(results.get(viewHolder.getLayoutPosition()));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Result result = results.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                final MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
                if (result.getTitle() != null){
                    movieViewHolder.setTitle(result.getTitle());
                }
                if (result.getReleaseDate() != null && result.getReleaseDate().length() >= GET_DATE_INDEXES_ARR[1]){
                    movieViewHolder.setAdditionalInfo(result.getReleaseDate()
                            .substring(GET_DATE_INDEXES_ARR[0], GET_DATE_INDEXES_ARR[1])
                            .concat(VERTICAL_DIVIDER).concat(result.getOriginalLanguage().toUpperCase()));
                }
                if (result.getVoteAverage() != null){
                    movieViewHolder.setRating(context.getString(R.string.rating)
                            .concat(String.valueOf(result.getVoteAverage())));
                }
                onNeedPosterListener.onNeedPoster(movieViewHolder.getPosterImageView(), result.getPosterPath());
                break;
            case LOADING:
                break;
        }
    }

    private GridLayoutManager.SpanSizeLookup onSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            return position < results.size() - 1 ? LOADING_SPAN_SIZE : ITEM_SPAN_SIZE;
        }
    };

    GridLayoutManager.SpanSizeLookup getOnSpanSizeLookup(){
        return onSpanSizeLookup;
    }

    @Override
    public int getItemCount() {
        return results == null ? 0
                : results.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == results.size() - 1
                && isLoadingAdded) ? LOADING : ITEM;
    }

    private void add(Result r) {
        results.add(r);
        notifyItemInserted(results.size() - 1);
    }

    void addAll(List<Result> moveResults) {
        results.addAll(moveResults);
        notifyItemInserted(results.size() - 1);
    }

    void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Result());
    }

    void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = results.size() - 1;
        Result result = results.get(position);
        if (result != null) {
            results.remove(position);
            notifyItemRemoved(position);
        }
    }
}