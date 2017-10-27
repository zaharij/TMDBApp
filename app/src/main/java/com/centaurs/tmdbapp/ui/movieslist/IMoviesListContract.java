package com.centaurs.tmdbapp.ui.movieslist;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;
import com.centaurs.tmdbapp.ui.IBasePresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

interface IMoviesListContract {
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
        void notifyItemInserted(boolean isLoadingAdded, int startPosition, int position);
        void notifyItemRemoved(boolean isLoadingAdded, int positionStart, int itemCount);
    }

    interface IPresenter extends IBasePresenter<IView>{
        void viewAttached(Context context);
        void onScrolledToEnd();
        void preLoadMoreItems();
        void onLoadMoreItems();
        void onPutResultsToAdapter(@NotNull TopRatedMovies response);
        void onNeedPoster(String posterPath);
        void onItemClicked(int movieId);
    }
}
