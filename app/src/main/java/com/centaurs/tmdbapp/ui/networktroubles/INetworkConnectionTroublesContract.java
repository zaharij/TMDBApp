package com.centaurs.tmdbapp.ui.networktroubles;


import com.centaurs.tmdbapp.ui.IBasePresenter;

interface INetworkConnectionTroublesContract {
    interface IView{
        void goBackToPreviousFragment();
    }

    interface IPresenter extends IBasePresenter<IView>{
        void onRetryButtonClicked();
    }
}
