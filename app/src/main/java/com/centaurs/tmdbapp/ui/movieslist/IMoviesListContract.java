package com.centaurs.tmdbapp.ui.movieslist;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.data.models.Result;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;
import com.centaurs.tmdbapp.ui.IBasePresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Response;

interface IMoviesListContract {
    interface IView {
        void hideMainProgress();
        void setTotalPages(int totalPages);
        void setIsLoading(boolean isLoading);
        void setIsLastPage(boolean isLastPage);
        void goToMovieDetailFragment(Result result);
        void goToNetworkConnectionTroublesFragment();
        void setPoster(String key, Drawable drawable);
        void setResultListToAdapter(List<Result> results);
        void notifyItemInserted(boolean isLoadingAdded, int position);
        void notifyItemRemoved(boolean isLoadingAdded, int position);
    }

    interface IPresenter extends IBasePresenter<IView>{
        void viewAttached(Context context);
        void onScrolledToEnd();
        void preLoadMoreItems();
        void onLoadMoreItems();
        void onPutResultsToAdapter(@NotNull Response<TopRatedMovies> response);
        void onNeedPoster(Context context, String posterPath);
        void onItemClicked(Result result);
    }
}
