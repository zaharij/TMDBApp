package com.centaurs.tmdbapp.util;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.centaurs.tmdbapp.dagger2.MovieActivityScope;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import javax.inject.Inject;

@MovieActivityScope
public class LoginHelper implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private IConnectionFailedListener connectionFailedListener;

    public interface IConnectionFailedListener {
        void onConnectionFailed(ConnectionResult connectionResult);
    }

    public interface ICheckCachedSignInListener {
        void check(GoogleSignInResult result);
    }

    public interface ISignOutListener{
        void onResult(Status status);
    }

    @Inject
    LoginHelper(FragmentActivity fragmentActivity){
        configureAndBuildGoogleApiClient(fragmentActivity);
    }

    private void configureAndBuildGoogleApiClient(FragmentActivity fragmentActivity){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    public void setConnectionFailedListener(IConnectionFailedListener connectionFailedListener) {
        this.connectionFailedListener = connectionFailedListener;
    }

    private GoogleSignInAccount getGoogleSignInAccount(GoogleSignInResult result){
        return result.getSignInAccount();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connectionFailedListener.onConnectionFailed(connectionResult);
    }

    public String getUserName(GoogleSignInResult result){
        return getGoogleSignInAccount(result).getDisplayName();
    }

    public String getPhotoUrlString(GoogleSignInResult result){
        GoogleSignInAccount googleSignInAccount = getGoogleSignInAccount(result);
        if (googleSignInAccount.getPhotoUrl() != null){
            return googleSignInAccount.getPhotoUrl().toString();
        }
        return null;
    }

    public GoogleSignInResult getGoogleSignInResult(Intent data){
        return Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    }

    public Intent getSignInIntent(){
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }

    public void checkCachedSignIn(final ICheckCachedSignInListener checkCachedSignInListener){
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (optionalPendingResult.isDone()) {
            GoogleSignInResult googleSignInResult = optionalPendingResult.get();
            checkCachedSignInListener.check(googleSignInResult);
        } else {
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    checkCachedSignInListener.check(googleSignInResult);
                }
            });
        }
    }

    public void signOut(final ISignOutListener iSignOutListener){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        iSignOutListener.onResult(status);
                    }
                });
    }
}
