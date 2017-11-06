package com.centaurs.tmdbapp.ui.login;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.util.LoginHelper;
import com.centaurs.tmdbapp.util.NetworkConnectionUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import javax.inject.Inject;

public class UserLoginPresenter implements IUserLoginContract.IPresenter {
    private final static String TAG = "UserLoginPresenter";
    private final int RC_SIGN_IN = 0;
    private IUserLoginContract.IView view;
    @Inject
    LoginHelper loginHelper;
    @Inject
    ImageLoader imageLoader;
    @Inject
    NetworkConnectionUtil networkConnectionUtil;
    @Inject
    Context context;

    private LoginHelper.IConnectionFailedListener connectionFailedListener = new LoginHelper.IConnectionFailedListener() {
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

    private LoginHelper.ICheckCachedSignInListener checkCachedSignIn = new LoginHelper.ICheckCachedSignInListener() {
        @Override
        public void check(GoogleSignInResult result) {
            handleSignInResult(result);
        }
    };

    private LoginHelper.ISignOutListener signOutListener = new LoginHelper.ISignOutListener() {
        @Override
        public void onResult(Status status) {
            updateUIWhenSignOut();
        }
    };

    @Override
    public void signInButtonClicked() {
        view.showGoogleAccountDialog(loginHelper.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    public void signOutButtonClicked() {
        loginHelper.signOut(signOutListener);
    }

    @Override
    public void moviesButtonClicked() {
        view.goToMoviesFragment();
    }

    @Override
    public void onReceivedLoginResult(int requestCode, Intent data) {
        if (requestCode == RC_SIGN_IN){
            handleSignInResult(loginHelper.getGoogleSignInResult(data));
        }
    }

    @Override
    public void onViewResumed() {
        view.hideSomethingWrongMessage();
        loginHelper.setConnectionFailedListener(connectionFailedListener);
        loginHelper.checkCachedSignIn(checkCachedSignIn);
    }

    @Override
    public void attachView(IUserLoginContract.IView IView) {
        this.view = IView;
    }

    @Override
    public void detachView() {
        view = null;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            view.setUsername(loginHelper.getUserName(result));
            if(loginHelper.getPhotoUrlString(result) != null) {
                imageLoader.loadImage(loginHelper.getPhotoUrlString(result), posterLoadingCallback);
            }
            view.hideSignInButton();
            view.showSignOutButton();
        } else {
            updateUIWhenSignOut();
        }
    }

    private ImageLoader.IPosterLoadingCallback posterLoadingCallback = new ImageLoader.IPosterLoadingCallback() {
        @Override
        public void onReturnImageResult(String key, @Nullable Drawable drawable) {
            if (drawable != null){
                view.setProfileImage(drawable);
            }
        }
    };

    private void updateUIWhenSignOut() {
        view.setDefaultUsername();
        view.setDefaultProfileImage();
        view.hideSignOutButton();
        view.showSignInButton();
    }
}
