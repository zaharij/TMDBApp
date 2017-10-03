package com.centaurs.tmdbapp.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.model.ImageHelper;
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

import dmax.dialog.SpotsDialog;

import static com.centaurs.tmdbapp.model.ImageHelper.SIZE_PIXELS;

public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private SpotsDialog dialog;
    private int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private ImageView profileImageView;
    private TextView userNameTextView;
    private Button moviesButton, accountButton, signOutButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        profileImageView = view.findViewById(R.id.profile_image_view);
        userNameTextView = view.findViewById(R.id.username_textView);
        userNameTextView.setText(getString(R.string.username_default_str));
        signOutButton = view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(onClickListener);
        moviesButton = view.findViewById(R.id.movie_button);
        moviesButton.setOnClickListener(onClickListener);
        accountButton = view.findViewById(R.id.account_button);
        accountButton.setOnClickListener(onClickListener);
        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.movie_button:
                    if (checkConnectionIfFalseGoToFragment()){
                        getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.grid_movies_fragment_container, new MoviesGridFragment())
                            .addToBackStack(null).commit();
                    }
                    break;
                case R.id.account_button:
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    break;
                case R.id.sign_out_button:
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    updateUIIfSignedIn(false);
                                }
                            });
                    break;
            }
        }
    };

    private boolean checkConnectionIfFalseGoToFragment() {
        ConnectionTroublesFragment connectionTroublesFragment
                = ConnectionTroublesFragment.getInstanceIfNoNetwork(getActivity(), onRetryClickListener);
        if (connectionTroublesFragment != null){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.grid_movies_fragment_container, connectionTroublesFragment)
                    .disallowAddToBackStack().commit();
            return false;
        }
        return true;
    }

    private ConnectionTroublesFragment.OnRetryClickListener onRetryClickListener
            = new ConnectionTroublesFragment.OnRetryClickListener() {
        @Override
        public void onRetryClick() {
            moviesButton.performClick();
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        checkConnectionIfFalseGoToFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                userNameTextView.setText(acct.getDisplayName());
                if(acct.getPhotoUrl() != null)
                    new LoadProfileImage(profileImageView).execute(acct.getPhotoUrl().toString());
            }
            updateUIIfSignedIn(true);
        } else {
            updateUIIfSignedIn(false);
        }
    }

    private void updateUIIfSignedIn(boolean signedIn) {
        if (signedIn) {
            accountButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile_empty);
            userNameTextView.setText(R.string.username_default_str);
            accountButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }

    private void showProgressDialog() {
        dialog = new SpotsDialog(getActivity());
        dialog.show();
    }

    private void hideProgressDialog() {
        dialog.dismiss();
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        private LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

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
                bmImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap1(resized, SIZE_PIXELS));
            }
        }
    }
}