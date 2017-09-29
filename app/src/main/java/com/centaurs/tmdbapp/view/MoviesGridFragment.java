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
import com.centaurs.tmdbapp.model.MovieCallInterface;
import com.centaurs.tmdbapp.model.config.RetrofitConfig;
import com.centaurs.tmdbapp.model.models.MovieGenres;
import com.centaurs.tmdbapp.model.models.Result;
import com.centaurs.tmdbapp.model.models.TopRatedMovies;
import com.centaurs.tmdbapp.presenter.pagination.PaginationAdapter;
import com.centaurs.tmdbapp.presenter.pagination.PaginationScrollListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesGridFragment extends Fragment {
    private static final int GRID_COLUMNS_NUMBER = 2;
    private static final int SCROLLING_DURATION = 1000;

    private PaginationAdapter paginationAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;
    private boolean isMainProgressVisible = false;

    private MovieCallInterface movieCallInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isMainProgressVisible = true;
        paginationAdapter = PaginationAdapter.getPaginationAdapter(getActivity());
        movieCallInterface = RetrofitConfig.getRetrofitClient().create(MovieCallInterface.class);
        loadFirstPage();
        callMovieGenres().enqueue(new Callback<MovieGenres>() {
            @Override
            public void onResponse(Call<MovieGenres> call, Response<MovieGenres> response) {
                paginationAdapter.setMovieGenres(response.body());
            }

            @Override
            public void onFailure(Call<MovieGenres> call, Throwable t) {
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) view.findViewById(R.id.main_progress);
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
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                List<Result> results = fetchResults(response);
                isMainProgressVisible = false;
                setMainProgressVisibility();
                paginationAdapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) paginationAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }

    private void loadNextPage() {
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                paginationAdapter.removeLoadingFooter();
                isLoading = false;

                List<Result> results = fetchResults(response);
                paginationAdapter.addAll(results);

                if (currentPage != TOTAL_PAGES) paginationAdapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Call<TopRatedMovies> callTopRatedMoviesApi() {
        return movieCallInterface.getTopRatedMovies(
                getResources().getString(R.string.api_key),
                getResources().getString(R.string.language_en_US),
                currentPage
        );
    }

    private Call<MovieGenres> callMovieGenres() {
        return movieCallInterface.getMovieGenres(
                getResources().getString(R.string.api_key),
                getResources().getString(R.string.language_en_US)
        );
    }
}
