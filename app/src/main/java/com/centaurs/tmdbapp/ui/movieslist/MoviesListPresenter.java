package com.centaurs.tmdbapp.ui.movieslist;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.data.TMoviesDBApi;
import com.centaurs.tmdbapp.data.models.Result;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;
import com.centaurs.tmdbapp.util.NetworkConnection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

class MoviesListPresenter implements IMoviesListContract.IPresenter {
    private final int TOTAL_PAGES = 1;
    private final int PAGE_START = 1;
    private IMoviesListContract.IView iView;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private TMoviesDBApi tMoviesDBApi;
    private int currentPage = PAGE_START;
    private List<Result> movies;
    private boolean isLoadingAdded = false;

    MoviesListPresenter (){
        tMoviesDBApi = TMoviesDBApi.getInstance();
        movies = new ArrayList<>();
    }

    @Override
    public void attachView(IMoviesListContract.IView view) {
        this.iView = view;
    }

    @Override
    public void detachView() {
        iView = null;
    }

    private TMoviesDBApi.IDataCallback loadFirstPageCallback = new TMoviesDBApi.IDataCallback() {
        @Override
        public void onResponse(@NotNull Response<TopRatedMovies> response) {
            iView.hideMainProgress();
            onPutResultsToAdapter(response);
            if (currentPage <= TOTAL_PAGES || currentPage > PAGE_START) addLoadingFooter();
            else isLastPage = true;
        }
    };

    private TMoviesDBApi.IDataCallback loadNextPageCallback = new TMoviesDBApi.IDataCallback() {
        @Override
        public void onResponse(@NotNull Response<TopRatedMovies> response) {
            removeLoadingFooter();
            isLoading = false;
            onPutResultsToAdapter(response);
            if (currentPage != TOTAL_PAGES) addLoadingFooter();
            else isLastPage = true;
        }
    };

    private void loadFirstPage() {
        tMoviesDBApi.loadPage(loadFirstPageCallback, currentPage);
    }

    private void loadNextPage() {
        tMoviesDBApi.loadPage(loadNextPageCallback, currentPage);
    }

    @Override
    public void viewAttached(Context context) {
        if (NetworkConnection.checkNetworkConnection(context)){
            iView.setResultListToAdapter(movies);
            loadFirstPage();
        } else {
            iView.goToNetworkConnectionTroublesFragment();
        }
    }

    @Override
    public void onScrolledToEnd() {
        loadNextPage();
    }

    @Override
    public void preLoadMoreItems() {
        iView.setIsLoading(isLoading);
        iView.setIsLastPage(isLastPage);
        iView.setTotalPages(TOTAL_PAGES);
    }

    @Override
    public void onLoadMoreItems() {
        currentPage++;
        isLoading = true;
    }

    @Override
    public void onPutResultsToAdapter(@NotNull Response<TopRatedMovies> response) {
        List<Result> results = fetchResults(response);
        addAll(results);
    }

    @Override
    public void onNeedPoster(Context context, String posterPath) {
        tMoviesDBApi.loadImage(context, true, false, posterPath, iPosterLoadingCallback);
    }

    private TMoviesDBApi.IPosterLoadingCallback iPosterLoadingCallback = new TMoviesDBApi.IPosterLoadingCallback() {
        @Override
        public void onBitmapGet(String key, Drawable drawable) {
            iView.setPoster(key, drawable);
        }
    };

    @Override
    public void onItemClicked(Result result) {
        iView.goToMovieDetailFragment(result);
    }

    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        if (topRatedMovies != null) {
            return topRatedMovies.getResults();
        } else {
            return null;
        }
    }


    private void add(Result r) {
        movies.add(r);
        iView.notifyItemInserted(isLoadingAdded, movies.size() - 1);
    }

    private void addAll(List<Result> moveResults) {
        movies.addAll(moveResults);
        iView.notifyItemInserted(isLoadingAdded, movies.size() - 1);
    }

    private void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Result());
    }

    private void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = movies.size() - 1;
        Result result = movies.get(position);
        if (result != null) {
            movies.remove(position);
            iView.notifyItemRemoved(isLoadingAdded, position);
        }
    }
}
