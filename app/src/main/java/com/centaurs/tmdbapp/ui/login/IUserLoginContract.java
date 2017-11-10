package com.centaurs.tmdbapp.ui.login;


import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.centaurs.tmdbapp.ui.IBasePresenter;

public interface IUserLoginContract {
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
        void goToNetworkConnectionTroublesFragment();
        void setSomethingWrongMessage(String message);
        void hideSomethingWrongMessage();
        void startActivityForResult(Intent intent, int requestCode);
    }

    interface IPresenter extends IBasePresenter<IView>{
        void signInButtonClicked();
        void signOutButtonClicked();
        void moviesButtonClicked();
        void onReceivedLoginResult(int requestCode, Intent data);
        void onViewResumed();
    }
}
