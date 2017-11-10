package com.centaurs.tmdbapp.ui.login;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.util.LoginHelper;
import com.centaurs.tmdbapp.util.NetworkConnectionUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;

import java.util.Map;

import static com.centaurs.tmdbapp.util.LoginHelper.REQ_CODE;

public class UserLoginPresenter implements IUserLoginContract.IPresenter {
    private final static String TAG = "UserLoginPresenter";
    private IUserLoginContract.IView view;
    private LoginHelper loginHelper;
    private ImageLoader imageLoader;
    private NetworkConnectionUtil networkConnectionUtil;
    private Context context;

    public UserLoginPresenter (LoginHelper loginHelper, NetworkConnectionUtil networkConnectionUtil
            , ImageLoader imageLoader, Context context){
        this.loginHelper = loginHelper;
        this.networkConnectionUtil = networkConnectionUtil;
        this.imageLoader = imageLoader;
        this.context = context;
    }

    private LoginHelper.IResultListener resultListener = new LoginHelper.IResultListener() {
        @Override
        public void onResultReady(@Nullable Map<LoginHelper.ELoginResultKeys, String> resultMap) {
            if (resultMap != null){
                updateUIWhenSignIn(resultMap);
            } else {
                updateUIWhenSignOut();
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(TAG, connectionResult.getErrorMessage());
            if (!networkConnectionUtil.isNetworkConnected()){
                view.goToNetworkConnectionTroublesFragment();
            } else {
                view.setSomethingWrongMessage(context.getResources().getString(R.string.something_went_wrong_message));
            }
        }
    };

    @Override
    public void signInButtonClicked() {
        Intent intent = loginHelper.getIntentWhenSignIn();
        view.startActivityForResult(intent, REQ_CODE);
    }

    @Override
    public void signOutButtonClicked() {
        loginHelper.signOut(signOutResultCallback);
    }

    @Override
    public void moviesButtonClicked() {
        view.goToMoviesFragment();
    }

    @Override
    public void onReceivedLoginResult(int requestCode, Intent data) {
        if (requestCode == REQ_CODE){
            updateUIWhenSignIn(loginHelper.getResult(data));
        }
    }

    @Override
    public void onViewResumed() {
        view.hideSomethingWrongMessage();
        loginHelper.setResultListener(resultListener);
        loginHelper.checkIfSignedInForResult();
    }

    @Override
    public void attachView(IUserLoginContract.IView IView) {
        this.view = IView;
    }

    @Override
    public void detachView() {
        view = null;
    }

    private ImageLoader.IPosterLoadingCallback posterLoadingCallback = new ImageLoader.IPosterLoadingCallback() {
        @Override
        public void onReturnImageResult(String key, @Nullable Drawable drawable) {
            if (drawable != null){
                view.setProfileImage(drawable);
            }
        }
    };

    private void updateUIWhenSignIn(Map<LoginHelper.ELoginResultKeys, String> loginResultMap){
        if (loginResultMap != null){
            view.setUsername(loginResultMap.get(LoginHelper.ELoginResultKeys.NAME));
            if(loginResultMap.get(LoginHelper.ELoginResultKeys.IMAGE_URL) != null) {
                imageLoader.loadImage(loginResultMap.get(LoginHelper.ELoginResultKeys.IMAGE_URL), posterLoadingCallback);
            }
            view.hideSignInButton();
            view.showSignOutButton();
        }
    }

    private void updateUIWhenSignOut() {
        view.setDefaultUsername();
        view.setDefaultProfileImage();
        view.hideSignOutButton();
        view.showSignInButton();
    }

    private ResultCallback signOutResultCallback = new ResultCallback() {
        @Override
        public void onResult(@NonNull Result result) {
            updateUIWhenSignOut();
        }
    };
}
