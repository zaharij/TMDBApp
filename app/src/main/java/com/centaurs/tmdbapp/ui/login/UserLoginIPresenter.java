package com.centaurs.tmdbapp.ui.login;


import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.data.ImageLoader;
import com.centaurs.tmdbapp.util.LoginHelper;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.Status;

class UserLoginIPresenter implements IUserLoginContract.IPresenter {
    private final int RC_SIGN_IN = 0;
    private IUserLoginContract.IView view;
    private LoginHelper loginHelper;

    UserLoginIPresenter(LoginHelper loginHelper){
        this.loginHelper = loginHelper;
    }

    private LoginHelper.ICheckCashedSignInListener checkCashedSignIn = new LoginHelper.ICheckCashedSignInListener() {
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
                ImageLoader.getInstance().loadImage(loginHelper.getPhotoUrlString(result), posterLoadingCallback);
            }
            view.hideSignInButton();
            view.showSignOutButton();
        } else {
            updateUIWhenSignOut();
        }
    }

    private ImageLoader.IPosterLoadingCallback posterLoadingCallback = new ImageLoader.IPosterLoadingCallback() {
        @Override
        public void onReturnImageResult(String key, Drawable drawable) {
            view.setProfileImage(drawable);
        }
    };

    private void updateUIWhenSignOut() {
        view.setDefaultUsername();
        view.setDefaultProfileImage();
        view.hideSignOutButton();
        view.showSignInButton();
    }
}
