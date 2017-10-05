package com.centaurs.tmdbapp.presenter.pclasses;


import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.centaurs.tmdbapp.model.MovieCall;
import com.centaurs.tmdbapp.model.MovieResultsSingleton;
import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.Result;
import com.centaurs.tmdbapp.model.models.TopRatedMovies;
import com.centaurs.tmdbapp.presenter.pinterfaces.MoviesGridPresenterInterface;
import com.centaurs.tmdbapp.view.vinterfaces.MoviesGridViewInterface;
import com.centaurs.tmdbapp.view.vinterfaces.ViewInterface;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesGridPresenter implements MoviesGridPresenterInterface {
    private MoviesGridViewInterface moviesGridViewInterface;
    private static final int GRID_COLUMNS_NUMBER = 2;
    private static final int SCROLLING_DURATION = 1000;
    private PaginationAdapter paginationAdapter;
    private MovieCall movieCall;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;
    private boolean isMainProgressVisible = false;
    private MovieResultsSingleton movieResultsSingleton;
    private FragmentActivity fragmentActivity;

    private MoviesGridPresenter(FragmentActivity activity, PaginationAdapter.OnItemClickListener onItemClickListener, int itemViewRes, int loadingItemRes){
        movieCall = MovieCall.getInstance();
        this.fragmentActivity = activity;
        isMainProgressVisible = true;
        movieResultsSingleton = MovieResultsSingleton.getMovieResultsSingleton();
        paginationAdapter = new PaginationAdapter(activity, movieResultsSingleton, onItemClickListener, itemViewRes, loadingItemRes);
        loadFirstPage();
        movieCall.callMovieGenres(activity).enqueue(new Callback<MovieGenres>() {
            @Override
            public void onResponse(@NotNull Call<MovieGenres> call, @NotNull Response<MovieGenres> response) {
                movieResultsSingleton.setMovieGenres(response.body());
            }
            @Override
            public void onFailure(@NotNull Call<MovieGenres> call, @NotNull Throwable t) {
            }
        });
    }

    public static MoviesGridPresenterInterface getMoviesGridPresenterInterface(FragmentActivity activity
            , PaginationAdapter.OnItemClickListener onItemClickListener, int itemViewRes, int loadingItemRes){
        return new MoviesGridPresenter(activity, onItemClickListener, itemViewRes, loadingItemRes);
    }

    @Override
    public void attachView(ViewInterface viewInterface) {
        moviesGridViewInterface = (MoviesGridViewInterface) viewInterface;
        paginationAdapter.attachViewAdapter(moviesGridViewInterface);
    }

    @Override
    public void detachView() {
        moviesGridViewInterface = null;
        paginationAdapter.detachViewAdapter();
    }

    @Override
    public void onClickButton(View view) {

    }

    @Override
    public void loadFirstPage() {
        paginationAdapter.refreshContext(fragmentActivity);
        movieCall.callTopRatedMoviesApi(fragmentActivity, currentPage).enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(@NotNull Call<TopRatedMovies> call, @NotNull Response<TopRatedMovies> response) {
                isMainProgressVisible = false;
                moviesGridViewInterface.setMainProgressVisibility(false);
                putResultsToAdapter(response);
                if (currentPage <= TOTAL_PAGES) paginationAdapter.addLoadingFooter();
                else isLastPage = true;
            }
            @Override
            public void onFailure(@NotNull Call<TopRatedMovies> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void loadNextPage() {
        movieCall.callTopRatedMoviesApi(fragmentActivity, currentPage).enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(@NotNull Call<TopRatedMovies> call, @NotNull Response<TopRatedMovies> response) {
                paginationAdapter.removeLoadingFooter();
                isLoading = false;
                putResultsToAdapter(response);
                if (currentPage != TOTAL_PAGES) paginationAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(@NotNull Call<TopRatedMovies> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(fragmentActivity, GRID_COLUMNS_NUMBER);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(paginationAdapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, SCROLLING_DURATION);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    public void setMainProgressVisibility() {
        moviesGridViewInterface.setMainProgressVisibility(isMainProgressVisible);
    }

    @Override
    public void refreshActivity(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        paginationAdapter.refreshContext(fragmentActivity);
    }

    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        if (topRatedMovies != null) {
            return topRatedMovies.getResults();
        } else {
            return null;
        }
    }

    private void putResultsToAdapter(@NotNull Response<TopRatedMovies> response) {
        List<Result> results = fetchResults(response);
        paginationAdapter.addAll(results);
    }
}
