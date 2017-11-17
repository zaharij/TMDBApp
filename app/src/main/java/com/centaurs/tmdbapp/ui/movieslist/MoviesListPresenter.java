package com.centaurs.tmdbapp.ui.movieslist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.data.api.MoviesApi;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.data.models.TopRatedMovies;
import com.centaurs.tmdbapp.data.util.IDataCallback;
import com.centaurs.tmdbapp.getvideo.VideoLoaderService;
import com.centaurs.tmdbapp.util.NetworkConnectionUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.centaurs.tmdbapp.getvideo.VideoLoaderService.MOVIE_ID_SERVICE_EXTRA;

public class MoviesListPresenter implements IMoviesListContract.IPresenter {
    private final String TAG = "MoviesListPresenter";
    private final int PAGE_START = 1;
    private final int SELECT_MOVIES_MAX_NUMBER = 10;
    private IMoviesListContract.IView view;
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = PAGE_START;
    private List<Movie> movies;
    private List<Integer> selectedMoviesIdsList;
    private boolean isLoadingAdded;
    private NetworkConnectionUtil networkConnectionUtil;
    private MoviesApi moviesApi;
    private ImageLoader imageLoader;
    private Context context;

    public MoviesListPresenter (NetworkConnectionUtil networkConnectionUtil, MoviesApi moviesApi
            , ImageLoader imageLoader, Context context){
        this.networkConnectionUtil = networkConnectionUtil;
        this.moviesApi = moviesApi;
        this.imageLoader = imageLoader;
        this.context = context;
        movies = new ArrayList<>();
        selectedMoviesIdsList = new ArrayList<>();
    }

    @Override
    public void attachView(IMoviesListContract.IView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    private IDataCallback<TopRatedMovies> loadFirstPageCallback
            = new IDataCallback<TopRatedMovies>() {
        @Override
        public void onResponse(@NotNull TopRatedMovies response) {
            view.hideMainProgress();
            isLastPage = response.getResults().size() < (movies.size() / currentPage);
            onPutResultsToAdapter(response);
            if (!isLastPage) addLoadingFooter();
        }

        @Override
        public void onFailure(Throwable throwable) {
            view.hideMainProgress();
            Log.e(TAG, throwable.getMessage());
            if (!networkConnectionUtil.isNetworkConnected()){
                view.goToNetworkConnectionTroublesFragment();
            }
        }
    };

    private IDataCallback<TopRatedMovies> loadNextPageCallback
            = new IDataCallback<TopRatedMovies>() {
        @Override
        public void onResponse(@NotNull TopRatedMovies response) {
            view.hideTroublesLoadingNextPageText();
            if (isLoadingAdded) removeLoadingFooter();
            isLoading = false;
            isLastPage = response.getResults().size() < (movies.size() / currentPage);
            onPutResultsToAdapter(response);
            if (!isLastPage) addLoadingFooter();
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
            if (isLoadingAdded) removeLoadingFooter();
            isLoading = false;
            if (!networkConnectionUtil.isNetworkConnected()){
                view.showTroublesLoadingNextPageText(context.getString(R.string.network_connection_troubles_message));
            } else {
                view.showTroublesLoadingNextPageText(context.getString(R.string.something_went_wrong_message));
            }
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
    public void onViewResumed() {
        view.hideToolbar();
        if (networkConnectionUtil.isNetworkConnected()){
            view.setResultListToAdapter(movies);
            view.setSelectedMoviesIdsListToAdapter(selectedMoviesIdsList);
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
        int totalPages = 1;
        view.setTotalPages(totalPages);
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
        imageLoader.loadPoster(posterPath,false, posterLoadingCallback);
    }

    private ImageLoader.IPosterLoadingCallback posterLoadingCallback = new ImageLoader.IPosterLoadingCallback() {
        @Override
        public void onReturnImageResult(String key, Drawable drawable) {
            view.setPoster(key, drawable);
        }
    };

    @Override
    public void onItemClicked(int movieId) {
        if (selectedMoviesIdsList.isEmpty()){
            view.goToMovieDetailFragment(movies.get(movieId).getId());
        } else {
            handleTapAfterLongTap(movieId);
        }
    }

    @Override
    public void onItemLongClicked(int movieId) {
        view.showToolbar();
        handleTapAfterLongTap(movieId);
    }

    private void handleTapAfterLongTap(int movieId) {
        if (!selectedMoviesIdsList.contains(movieId)){
            if (selectedMoviesIdsList.size() < SELECT_MOVIES_MAX_NUMBER){
                selectedMoviesIdsList.add(movieId);
            } else {
                view.showToast(context.getString(R.string.selected_movies_maximum_number_message));
                return;
            }
        } else {
            selectedMoviesIdsList.remove(Integer.valueOf(movieId));
        }
        if (selectedMoviesIdsList.size() > 0){
            view.setSelectedMoviesNumber(String.valueOf(selectedMoviesIdsList.size()));
        } else {
            view.hideToolbar();
        }
        view.notifyAdapterDataSetChanged();
    }

    @Override
    public boolean onBackPressed() {
        if (!selectedMoviesIdsList.isEmpty()){
            nullSelectedMovies();
            return true;
        }
        return false;
    }

    private void nullSelectedMovies() {
        selectedMoviesIdsList.clear();
        view.hideToolbar();
        view.notifyAdapterDataSetChanged();
    }

    @Override
    public void onSelectedVideosClicked() {
        for (Integer movieIndex: selectedMoviesIdsList){
            context.startService(new Intent(context, VideoLoaderService.class)
                    .putExtra(MOVIE_ID_SERVICE_EXTRA, movies.get(movieIndex).getId()));
        }
        nullSelectedMovies();
        view.showToast(context.getString(R.string.downloading_message));
    }

    private void add(Movie movie) {
        int startPosition = movies.size();
        movies.add(movie);
        view.notifyItemInserted(isLoadingAdded, startPosition, movies.size());
    }

    private void addAll(List<Movie> moveResults) {
        int startPosition = movies.size() + 1;
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
