package com.centaurs.tmdbapp.util;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class LoginHelper implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private IConnectionFailedListener connectionFailedListener;

    public interface IConnectionFailedListener {
        void onConnectionFailed();
    }

    public interface ICheckCashedSignInListener {
        void check(GoogleSignInResult result);
    }

    public interface ISignOutListener{
        void onResult(Status status);
    }

    public LoginHelper(FragmentActivity fragmentActivity, IConnectionFailedListener connectionFailedListener){
        this.connectionFailedListener = connectionFailedListener;
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

    private GoogleSignInAccount getGoogleSignInAccount(GoogleSignInResult result){
        return result.getSignInAccount();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connectionFailedListener.onConnectionFailed();
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

    public void checkCashedSignIn(final ICheckCashedSignInListener iCheckCashedSignInListener){
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (optionalPendingResult.isDone()) {
            GoogleSignInResult googleSignInResult = optionalPendingResult.get();
            iCheckCashedSignInListener.check(googleSignInResult);
        } else {
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    iCheckCashedSignInListener.check(googleSignInResult);
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
