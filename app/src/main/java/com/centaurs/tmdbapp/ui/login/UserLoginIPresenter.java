package com.centaurs.tmdbapp.ui.login;


import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.data.TMoviesDBApi;
import com.centaurs.tmdbapp.util.LoginHelper;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.Status;

class UserLoginIPresenter implements IUserLoginContract.IPresenter {
    private final int RC_SIGN_IN = 0;
    private IUserLoginContract.IView iView;
    private LoginHelper loginHelper;
    private TMoviesDBApi tMoviesDBApi;

    UserLoginIPresenter(LoginHelper loginHelper){
        this.loginHelper = loginHelper;
        tMoviesDBApi = TMoviesDBApi.getInstance();
    }

    private LoginHelper.ICheckCashedSignInListener checkCashedSignIn = new LoginHelper.ICheckCashedSignInListener() {
        @Override
        public void check(GoogleSignInResult result) {
            handleSignInResult(result);
        }
    };

    private LoginHelper.ISignOutListener iSignOutListener = new LoginHelper.ISignOutListener() {
        @Override
        public void onResult(Status status) {
            updateUIWhenSignOut();
        }
    };

    @Override
    public void signInButtonClicked() {
        iView.showGoogleAccountDialog(loginHelper.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    public void signOutButtonClicked() {
        loginHelper.signOut(iSignOutListener);
    }

    @Override
    public void moviesButtonClicked() {
        iView.goToMoviesFragment();
    }

    @Override
    public void onConnectionFailed() {

    }

    @Override
    public void onReceivedLoginResult(int requestCode, Intent data) {
        if (requestCode == RC_SIGN_IN){
            handleSignInResult(loginHelper.getGoogleSignInResult(data));
        }
    }

    @Override
    public void onViewCreated() {
        loginHelper.checkCashedSignIn(checkCashedSignIn);
    }

    @Override
    public void attachView(IUserLoginContract.IView IView) {
        this.iView = IView;
    }

    @Override
    public void detachView() {
        iView = null;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            iView.setUsername(loginHelper.getUserName(result));
            if(loginHelper.getPhotoUrlString(result) != null) {
                tMoviesDBApi.loadImage(iView.getContext(), false, true
                        , loginHelper.getPhotoUrlString(result), iPosterLoadingCallback);
            }
            iView.hideSignInButton();
            iView.showSignOutButton();
        } else {
            updateUIWhenSignOut();
        }
    }

    private TMoviesDBApi.IPosterLoadingCallback iPosterLoadingCallback = new TMoviesDBApi.IPosterLoadingCallback() {
        @Override
        public void onBitmapGet(String key, Drawable drawable) {
            iView.setProfileImage(drawable);
        }
    };

    private void updateUIWhenSignOut() {
        iView.setDefaultUsername();
        iView.setDefaultProfileImage();
        iView.hideSignOutButton();
        iView.showSignInButton();
    }
}
