package com.centaurs.tmdbapp.view;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.model.MovieCall;
import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.Result;
import com.centaurs.tmdbapp.model.models.TopRatedMovies;
import com.centaurs.tmdbapp.presenter.pagination.MovieResultsSingletone;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesGridFragment extends Fragment {
    private static final int GRID_COLUMNS_NUMBER = 2;
    private static final int SCROLLING_DURATION = 1000;

    private PaginationAdapter paginationAdapter;

    private ProgressBar progressBar;
    private MovieCall movieCall;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;
    private boolean isMainProgressVisible = false;
    private MovieResultsSingletone movieResultsSingletone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieCall = MovieCall.getInstance();
        isMainProgressVisible = true;
        movieResultsSingletone = MovieResultsSingletone.getMovieResultsSingletone();
        paginationAdapter = new PaginationAdapter(getActivity(), movieResultsSingletone, onItemClickListener);
        loadFirstPage();
        movieCall.callMovieGenres(getActivity()).enqueue(new Callback<MovieGenres>() {
            @Override
            public void onResponse(@NotNull Call<MovieGenres> call, @NotNull Response<MovieGenres> response) {
                movieResultsSingletone.setMovieGenres(response.body());
            }
            @Override
            public void onFailure(@NotNull Call<MovieGenres> call, @NotNull Throwable t) {
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.main_recycler);
        progressBar = view.findViewById(R.id.main_progress);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), GRID_COLUMNS_NUMBER);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(paginationAdapter);
        setMainProgressVisibility();
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
        return view;
    }

    private void setMainProgressVisibility(){
        if (isMainProgressVisible){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadFirstPage() {
        movieCall.callTopRatedMoviesApi(getActivity(), currentPage).enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(@NotNull Call<TopRatedMovies> call, @NotNull Response<TopRatedMovies> response) {
                isMainProgressVisible = false;
                setMainProgressVisibility();
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

    private void putResultsToAdapter(@NotNull Response<TopRatedMovies> response) {
        List<Result> results = fetchResults(response);
        paginationAdapter.addAll(results);
    }

    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        if (topRatedMovies != null) {
            return topRatedMovies.getResults();
        } else {
            return null;
        }
    }

    private void loadNextPage() {
        movieCall.callTopRatedMoviesApi(getActivity(), currentPage).enqueue(new Callback<TopRatedMovies>() {
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

    private PaginationAdapter.OnItemClickListener onItemClickListener = new PaginationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int itemId) {
            try {
                Fragment movieFragment = MovieFragment.getInstance(itemId);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.grid_movies_fragment_container, movieFragment)
                        .addToBackStack(null).commit();
            } catch (Exception ignored){
            }
        }
    };
}
