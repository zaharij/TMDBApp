package com.centaurs.tmdbapp.ui.login;


import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.ui.IBasePresenter;

interface IUserLoginContract {
    interface IView {
        void showSignInButton();
        void hideSignInButton();
        void showSignOutButton();
        void hideSignOutButton();
        void setProfileImage(Drawable profileImage);
        void setDefaultProfileImage();
        void showGoogleAccountDialog(Intent signInIntent, int resourceCodeSignIn);
        void goToMoviesFragment();
        void setUsername(String username);
        void setDefaultUsername();
    }

    interface IPresenter extends IBasePresenter<IView>{
        void signInButtonClicked();
        void signOutButtonClicked();
        void moviesButtonClicked();
        void onConnectionFailed();
        void onReceivedLoginResult(int requestCode, Intent data);
        void onViewResumed();
    }
}
