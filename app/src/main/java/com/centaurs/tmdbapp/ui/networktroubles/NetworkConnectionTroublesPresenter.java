package com.centaurs.tmdbapp.ui.networktroubles;


class NetworkConnectionTroublesPresenter implements INetworkConnectionTroublesContract.IPresenter {
    private INetworkConnectionTroublesContract.IView iView;

    @Override
    public void attachView(INetworkConnectionTroublesContract.IView view) {
        iView = view;
    }

    @Override
    public void detachView() {
        iView = null;
    }

    @Override
    public void onRetryButtonClicked() {
        iView.goBackToPreviousFragment();
    }
}
