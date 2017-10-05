package com.centaurs.tmdbapp.presenter.pclasses;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.centaurs.tmdbapp.presenter.pinterfaces.ConnectionTroublesPresenterInterface;
import com.centaurs.tmdbapp.view.vinterfaces.ViewInterface;

public class ConnectionTroublesPresenter implements ConnectionTroublesPresenterInterface {
    private Context context;

    ConnectionTroublesPresenter(Context context){
        this.context = context;
    }

    public static ConnectionTroublesPresenterInterface getConnectionTroublesInterface(Context context){
        return new ConnectionTroublesPresenter(context);
    }

    @Override
    public void attachView(ViewInterface viewInterface) {
    }

    @Override
    public void detachView() {
        this.context = null;
    }

    @Override
    public void onClickButton(View view) {
    }

    @Override
    public boolean connectionCheckRequestForResult() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
