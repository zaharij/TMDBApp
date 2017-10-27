package com.centaurs.tmdbapp.ui.movieslist;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

abstract class PaginationScrollListener extends RecyclerView.OnScrollListener{
    private GridLayoutManager layoutManager;

    PaginationScrollListener(GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        preLoadMoreItems();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= getTotalPageCount()) {
                loadMoreItems();
            }
        }
    }
    abstract void preLoadMoreItems();
    abstract void loadMoreItems();
    abstract int getTotalPageCount();
    abstract boolean isLastPage();
    abstract boolean isLoading();
}
