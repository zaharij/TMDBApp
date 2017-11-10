package com.centaurs.tmdbapp.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import java.util.HashMap;
import java.util.Map;

import static com.centaurs.tmdbapp.util.LoginHelper.ELoginResultKeys.EMAIL;
import static com.centaurs.tmdbapp.util.LoginHelper.ELoginResultKeys.IMAGE_URL;
import static com.centaurs.tmdbapp.util.LoginHelper.ELoginResultKeys.NAME;

public class LoginHelper implements GoogleApiClient.OnConnectionFailedListener {
    public final static int REQ_CODE = 9001;
    private GoogleApiClient googleApiClient;
    private IResultListener resultListener;

    public interface IResultListener{
        void onResultReady(@Nullable Map<ELoginResultKeys, String> loginResultMap);
        void onConnectionFailed(ConnectionResult connectionResult);
    }

    public enum ELoginResultKeys{
        NAME, EMAIL, IMAGE_URL
    }

    public LoginHelper(FragmentActivity fragmentActivity) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
    }

    public void checkIfSignedInForResult(){
        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (optionalPendingResult.isDone()) {
            GoogleSignInResult result = optionalPendingResult.get();
            resultListener.onResultReady(getLoginResultMap(result));
        } else {
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    resultListener.onResultReady(getLoginResultMap(googleSignInResult));
                }
            });
        }
    }

    public void setResultListener(IResultListener resultListener) {
        this.resultListener = resultListener;

    }

    public Intent getIntentWhenSignIn(){
        return Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
    }

    public void signOut(final ResultCallback resultCallback){
        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
            googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(resultCallback);
                }

                @Override
                public void onConnectionSuspended(int i) {
                    resultListener.onConnectionFailed(null);
                }
            });
        } else Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(resultCallback);
    }

    public Map<ELoginResultKeys, String> getResult(Intent data){
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        return getLoginResultMap(result);
    }

    private Map<ELoginResultKeys, String> getLoginResultMap(GoogleSignInResult result){
        Map<ELoginResultKeys, String> loginResultMap = null;
        if (result.isSuccess()){
            loginResultMap = new HashMap<>();
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                loginResultMap.put(NAME, account.getDisplayName());
                loginResultMap.put(EMAIL, account.getEmail());
                if (account.getPhotoUrl() != null) {
                    loginResultMap.put(IMAGE_URL, account.getPhotoUrl().toString());
                }
            }
        }
        return loginResultMap;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        resultListener.onConnectionFailed(connectionResult);
    }
}
