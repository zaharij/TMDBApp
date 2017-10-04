package com.centaurs.tmdbapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.presenter.pclasses.MoviesGridPresenter;
import com.centaurs.tmdbapp.presenter.pclasses.PaginationAdapter;
import com.centaurs.tmdbapp.presenter.pinterfaces.MoviesGridPresenterInterface;
import com.centaurs.tmdbapp.view.vinterfaces.MoviesGridViewInterface;

public class MoviesGridFragment extends Fragment implements MoviesGridViewInterface{
    private ProgressBar progressBar;
    private MoviesGridPresenterInterface moviesGridPresenterInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moviesGridPresenterInterface = MoviesGridPresenter.getMoviesGridPresenterInterface(getActivity()
                , onItemClickListener, R.layout.item_list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        progressBar = view.findViewById(R.id.main_progress);
        RecyclerView recyclerView = view.findViewById(R.id.main_recycler);
        moviesGridPresenterInterface.attachView(this);
        moviesGridPresenterInterface.setRecyclerView(recyclerView, getActivity());
        moviesGridPresenterInterface.setMainProgressVisibility();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        moviesGridPresenterInterface.detachView();
    }

    @Override
    public void setMainProgressVisibility(boolean isMainProgressVisible){
        if (isMainProgressVisible){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public MovieViewHolder getMovieViewHolder(View view) {
        return new MovieViewHolder(view);
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

    @Override
    public FragmentActivity getActivityForOuterCall() {
        return getActivity();
    }
}
