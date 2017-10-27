package com.centaurs.tmdbapp.ui.movieslist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.data.MoviesApi;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;
import com.centaurs.tmdbapp.util.NetworkConnection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class MoviesListPresenter implements IMoviesListContract.IPresenter {
    private final String TAG = "MoviesListPresenter";
    private final int TOTAL_PAGES = 1;
    private final int PAGE_START = 1;
    private IMoviesListContract.IView view;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private MoviesApi moviesApi;
    private int currentPage = PAGE_START;
    private List<Movie> movies;
    private boolean isLoadingAdded = false;

    MoviesListPresenter (){
        moviesApi = MoviesApi.getInstance();
        movies = new ArrayList<>();
    }

    @Override
    public void attachView(IMoviesListContract.IView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    private MoviesApi.IDataCallback<TopRatedMovies> loadFirstPageCallback
            = new MoviesApi.IDataCallback<TopRatedMovies>() {
        @Override
        public void onResponse(@NotNull TopRatedMovies response) {
            view.hideMainProgress();
            onPutResultsToAdapter(response);
            if (currentPage <= TOTAL_PAGES || currentPage > PAGE_START) addLoadingFooter();
            else isLastPage = true;
        }

        @Override
        public void onFailure(Throwable throwable) {
            view.hideMainProgress();
            Log.e(TAG, throwable.getMessage());
        }
    };

    private MoviesApi.IDataCallback<TopRatedMovies> loadNextPageCallback
            = new MoviesApi.IDataCallback<TopRatedMovies>() {
        @Override
        public void onResponse(@NotNull TopRatedMovies response) {
            removeLoadingFooter();
            isLoading = false;
            onPutResultsToAdapter(response);
            if (currentPage != TOTAL_PAGES) addLoadingFooter();
            else isLastPage = true;
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
        }
    };

    private void loadFirstPage() {
        view.showMainProgress();
        moviesApi.loadPage(loadFirstPageCallback, currentPage);
    }

    private void loadNextPage() {
        moviesApi.loadPage(loadNextPageCallback, currentPage);
    }

    @Override
    public void viewAttached(Context context) {
        if (NetworkConnection.isNetworkConnected(context)){
            view.setResultListToAdapter(movies);
            if (movies.size() == 0){
                loadFirstPage();
            }
        } else {
            view.goToNetworkConnectionTroublesFragment();
        }
    }

    @Override
    public void onScrolledToEnd() {
        loadNextPage();
    }

    @Override
    public void preLoadMoreItems() {
        view.setIsLoading(isLoading);
        view.setIsLastPage(isLastPage);
        view.setTotalPages(TOTAL_PAGES);
    }

    @Override
    public void onLoadMoreItems() {
        currentPage++;
        isLoading = true;
    }

    @Override
    public void onPutResultsToAdapter(@NotNull TopRatedMovies response) {
        List<Movie> results = response.getResults();
        addAll(results);
    }

    @Override
    public void onNeedPoster(String posterPath) {
        ImageLoader.getInstance().loadPoster(posterPath,false, posterLoadingCallback);
    }

    private ImageLoader.IPosterLoadingCallback posterLoadingCallback = new ImageLoader.IPosterLoadingCallback() {
        @Override
        public void onReturnImageResult(String key, Drawable drawable) {
            view.setPoster(key, drawable);
        }
    };

    @Override
    public void onItemClicked(int movieId) {
        view.goToMovieDetailFragment(movieId);
    }

    private void add(Movie movie) {
        int startPosition = movies.size();
        movies.add(movie);
        view.notifyItemInserted(isLoadingAdded, startPosition, movies.size());
    }

    private void addAll(List<Movie> moveResults) {
        int startPosition = movies.size()+1;
        movies.addAll(moveResults);
        view.notifyItemInserted(isLoadingAdded, startPosition, moveResults.size()+1);
    }

    private void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Movie());
    }

    private void removeLoadingFooter() {
        isLoadingAdded = false;
        int startPosition = movies.size() - 1;
        Movie result = movies.get(startPosition);
        if (result != null && view != null) {
            movies.remove(startPosition);
            int deleteItemNumber = startPosition - (movies.size() - 1);
            view.notifyItemRemoved(isLoadingAdded, startPosition, deleteItemNumber);
        }
    }
}
