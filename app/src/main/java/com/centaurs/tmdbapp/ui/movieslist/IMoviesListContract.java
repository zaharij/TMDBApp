package com.centaurs.tmdbapp.ui.movieslist;

import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;
import com.centaurs.tmdbapp.ui.IBasePresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public  interface IMoviesListContract {
    interface IView {
        void hideMainProgress();
        void showMainProgress();
        void setTotalPages(int totalPages);
        void setIsLoading(boolean isLoading);
        void setIsLastPage(boolean isLastPage);
        void goToMovieDetailFragment(int movieId);
        void goToNetworkConnectionTroublesFragment();
        void setPoster(String key, Drawable drawable);
        void setResultListToAdapter(List<Movie> results);
        void setSelectedMoviesIdsListToAdapter(List<Integer> selectedMoviesIdsList);
        void notifyItemInserted(boolean isLoadingAdded, int startPosition, int position);
        void notifyItemRemoved(boolean isLoadingAdded, int positionStart, int itemCount);
        void showTroublesLoadingNextPageText(String message);
        void hideTroublesLoadingNextPageText();
        void notifyAdapterDataSetChanged();
        void showToolbar();
        void hideToolbar();
        void setSelectedMoviesNumber(String numberStr);
        void showToast(String message);
    }

    interface IPresenter extends IBasePresenter<IView>{
        void onViewResumed();
        void onScrolledToEnd();
        void preLoadMoreItems();
        void onLoadMoreItems();
        void onPutResultsToAdapter(@NotNull TopRatedMovies response);
        void onNeedPoster(String posterPath);
        void onItemClicked(int movieId);
        void onItemLongClicked(int movieId);
        boolean onBackPressed();
        void onSelectedVideosClicked();
    }
}
