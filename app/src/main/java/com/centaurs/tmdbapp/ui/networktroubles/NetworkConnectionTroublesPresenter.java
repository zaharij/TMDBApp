package com.centaurs.tmdbapp.ui.networktroubles;


public class NetworkConnectionTroublesPresenter implements INetworkConnectionTroublesContract.IPresenter {
    private INetworkConnectionTroublesContract.IView view;

    @Override
    public void attachView(INetworkConnectionTroublesContract.IView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onRetryButtonClicked() {
        view.goBackToPreviousFragment();
    }
}
