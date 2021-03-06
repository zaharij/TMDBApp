package com.centaurs.tmdbapp.ui.movieslist;

import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.di.Injector;
import com.centaurs.tmdbapp.ui.PresenterManager;
import com.centaurs.tmdbapp.ui.moviedetail.MovieDetailFragment;
import com.centaurs.tmdbapp.ui.networktroubles.NetworkConnectionTroublesFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.centaurs.tmdbapp.ui.movieslist.PaginationAdapter.ITEM_SPAN_SIZE;

public class MoviesListFragment extends Fragment implements IMoviesListContract.IView{
    private static final int SCROLLING_DURATION = 1000;
    private PaginationAdapter paginationAdapter;
    @Inject
    IMoviesListContract.IPresenter presenter;
    private ProgressBar moviesListProgress;
    private Map<String, ImageView> imageViewMap;
    private TextView troublesLoadingNextPageTextView;

    //not for data storage, only for receiving data from the presenter
    private int totalPages;
    private boolean isLoading;
    private boolean isLastPage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().getMovieComponent().inject(this);
        imageViewMap = new HashMap<>();
        paginationAdapter = new PaginationAdapter(onItemClickListener, onNeedPosterListener, R.layout.item);
        Injector.getInstance().getMovieComponent().inject(paginationAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_list, container, false);
        moviesListProgress = view.findViewById(R.id.movies_list_progress);
        hideMainProgress();
        troublesLoadingNextPageTextView = view.findViewById(R.id.item_progress_troubles_text_view);
        hideMainProgress();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), ITEM_SPAN_SIZE);
        gridLayoutManager.setSpanSizeLookup(paginationAdapter.getOnSpanSizeLookup());
        RecyclerView recyclerView = view.findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(paginationAdapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            void preLoadMoreItems() {
                presenter.preLoadMoreItems();
            }

            @Override
            public void loadMoreItems() {
                presenter.onLoadMoreItems();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.onScrolledToEnd();
                    }
                }, SCROLLING_DURATION);
            }

            @Override
            public int getTotalPageCount() {
                return totalPages;
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
        presenter.attachView(this);
        return view;
    }

    private PaginationAdapter.OnItemClickListener onItemClickListener = new PaginationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int movieId) {
            presenter.onItemClicked(movieId);
        }
    };

    private PaginationAdapter.OnNeedPosterListener onNeedPosterListener = new PaginationAdapter.OnNeedPosterListener() {
        @Override
        public void onNeedPoster(ImageView imageView, String posterPath) {
            imageViewMap.put(posterPath, imageView);
            presenter.onNeedPoster(posterPath);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResumed();
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(presenter, outState);
    }


    @Override
    public void hideMainProgress() {
        moviesListProgress.setVisibility(View.GONE);
    }

    @Override
    public void showMainProgress() {
        moviesListProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    @Override
    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    @Override
    public void goToMovieDetailFragment(int movieId) {
        MovieDetailFragment movieDetailFragment = MovieDetailFragment.getInstance(movieId);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, movieDetailFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void goToNetworkConnectionTroublesFragment() {
        NetworkConnectionTroublesFragment networkConnectionTroublesFragment
                = NetworkConnectionTroublesFragment.getInstance(onRetryListener);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, networkConnectionTroublesFragment)
                .commit();
    }

    @Override
    public void setPoster(String key, Drawable drawable) {
        imageViewMap.get(key).setImageDrawable(drawable);
    }

    @Override
    public void setResultListToAdapter(List<Movie> results) {
        paginationAdapter.setMovies(results);
    }

    @Override
    public void notifyItemInserted(boolean isLoadingAdded, int startPosition, int position) {
        paginationAdapter.notifyInserted(isLoadingAdded, startPosition, position);
    }

    @Override
    public void notifyItemRemoved(boolean isLoadingAdded, int positionStart, int itemCount) {
        paginationAdapter.notifyRemoved(isLoadingAdded, positionStart, itemCount);
    }

    @Override
    public void showTroublesLoadingNextPageText(String message) {
        troublesLoadingNextPageTextView.setVisibility(View.VISIBLE);
        troublesLoadingNextPageTextView.setText(message);
    }

    @Override
    public void hideTroublesLoadingNextPageText() {
        troublesLoadingNextPageTextView.setVisibility(View.GONE);
    }

    private NetworkConnectionTroublesFragment.OnRetryListener onRetryListener
            = new NetworkConnectionTroublesFragment.OnRetryListener() {
        @Override
        public Fragment onRetryGetBackFragment() {
            return new MoviesListFragment();
        }
    };
}
