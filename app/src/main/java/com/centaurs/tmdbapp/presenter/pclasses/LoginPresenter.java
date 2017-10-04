package com.centaurs.tmdbapp.presenter.pclasses;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.model.ImageHelper;
import com.centaurs.tmdbapp.presenter.pinterfaces.LoginPresenterInterface;
import com.centaurs.tmdbapp.view.ConnectionTroublesFragment;
import com.centaurs.tmdbapp.view.MoviesGridFragment;
import com.centaurs.tmdbapp.view.vinterfaces.LoginViewInterface;
import com.centaurs.tmdbapp.view.vinterfaces.ViewInterface;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.InputStream;

import static com.centaurs.tmdbapp.model.ImageHelper.SIZE_PIXELS;

public class LoginPresenter implements LoginPresenterInterface, GoogleApiClient.OnConnectionFailedListener {
    private int RC_SIGN_IN = 0;
    private LoginViewInterface loginViewInterface;
    private GoogleApiClient mGoogleApiClient;

    private LoginPresenter(FragmentActivity fragmentActivity){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    public static LoginPresenterInterface getLoginPresenterInterface(FragmentActivity fragmentActivity){
        return new LoginPresenter(fragmentActivity);
    }

    @Override
    public void attachView(ViewInterface viewInterface) {
        loginViewInterface = (LoginViewInterface) viewInterface;
    }

    @Override
    public void detachView() {
        loginViewInterface = null;
    }

    @Override
    public void onClickButton(View view) {
        switch (view.getId()){
            case R.id.movie_button:
                checkNetworkIfTrueOpenMovies();
                break;
            case R.id.account_button:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                loginViewInterface.startActivityForResultOuterCall(signInIntent, RC_SIGN_IN);
                break;
            case R.id.sign_out_button:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                loginViewInterface.updateUISignedInOrOutBooll(false);
                            }
                        });
                break;
        }
    }

    private void checkNetworkIfTrueOpenMovies() {
        if (checkConnectionIfFalseGoToFragment()){
            loginViewInterface.getActivityForOuterCall().getSupportFragmentManager().beginTransaction()
                    .add(R.id.grid_movies_fragment_container, new MoviesGridFragment())
                    .addToBackStack(null).commit();
        }
    }

    private boolean checkConnectionIfFalseGoToFragment() {
        ConnectionTroublesFragment connectionTroublesFragment
                = ConnectionTroublesFragment.getInstanceIfNoNetwork(loginViewInterface.getActivityForOuterCall()
                , onRetryClickListener);
        if (connectionTroublesFragment != null){
            loginViewInterface.getActivityForOuterCall().getSupportFragmentManager().beginTransaction()
                    .add(R.id.grid_movies_fragment_container, connectionTroublesFragment)
                    .disallowAddToBackStack().commit();
            return false;
        }
        return true;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                loginViewInterface.setStatusProfile(acct.getDisplayName());
                if(acct.getPhotoUrl() != null)
                    new LoadProfileImage().execute(acct.getPhotoUrl().toString());
            }
            loginViewInterface.updateUISignedInOrOutBooll(true);
        } else {
            loginViewInterface.updateUISignedInOrOutBooll(false);
        }
    }

    private ConnectionTroublesFragment.OnRetryClickListener onRetryClickListener
            = new ConnectionTroublesFragment.OnRetryClickListener() {
        @Override
        public void onRetryClick() {
            checkNetworkIfTrueOpenMovies();
        }
    };

    @Override
    public void checkAuthentication() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            loginViewInterface.showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    loginViewInterface.hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void checkIfRCSignIn(int requestCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        checkNetworkIfTrueOpenMovies();
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... uri) {
            String url = uri[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                Bitmap resized = Bitmap.createScaledBitmap(result, SIZE_PIXELS, SIZE_PIXELS, true);
                loginViewInterface.setProfileImg(ImageHelper.getRoundedCornerBitmap1(resized, SIZE_PIXELS));
            }
        }
    }
}
