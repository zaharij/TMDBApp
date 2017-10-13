package com.centaurs.tmdbapp.ui.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.ui.PresenterManager;
import com.centaurs.tmdbapp.ui.movieslist.MoviesListFragment;
import com.centaurs.tmdbapp.util.LoginHelper;

public class UserLoginFragment extends Fragment implements IUserLoginContract.IView {
    private IUserLoginContract.IPresenter iPresenter;
    private Button signInButton, signOutButton;
    private ImageView profileImageView;
    private TextView usernameTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            iPresenter = new UserLoginIPresenter(new LoginHelper(getActivity(), iConnectionFailedListener));
        } else {
            iPresenter = (IUserLoginContract.IPresenter) PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
        iPresenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_login, container, false);
        signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(onButtonClickListener);
        signOutButton = view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(onButtonClickListener);
        Button moviesButton = view.findViewById(R.id.movies_button);
        moviesButton.setOnClickListener(onButtonClickListener);

        profileImageView = view.findViewById(R.id.profile_image_view);
        usernameTextView = view.findViewById(R.id.username_text_view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        iPresenter.onViewCreated();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(iPresenter, outState);
    }

    private View.OnClickListener onButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.sign_in_button:
                    iPresenter.signInButtonClicked();
                    break;
                case R.id.sign_out_button:
                    iPresenter.signOutButtonClicked();
                    break;
                case R.id.movies_button:
                    iPresenter.moviesButtonClicked();
                    break;
            }
        }
    };

    private LoginHelper.IConnectionFailedListener iConnectionFailedListener = new LoginHelper.IConnectionFailedListener() {
        @Override
        public void onConnectionFailed() {
            iPresenter.onConnectionFailed();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        iPresenter.onReceivedLoginResult(requestCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iPresenter.detachView();
    }

    @Override
    public void showSignInButton() {
        signInButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSignInButton() {
        signInButton.setVisibility(View.GONE);
    }

    @Override
    public void showSignOutButton() {
        signOutButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSignOutButton() {
        signOutButton.setVisibility(View.GONE);
    }

    @Override
    public void setProfileImage(Drawable profileImage) {
        profileImageView.setImageDrawable(profileImage);
    }

    @Override
    public void setDefaultProfileImage() {
        profileImageView.setImageResource(R.drawable.profile_image_default);
    }

    @Override
    public void showGoogleAccountDialog(Intent signInIntent, int resourceCodeSignIn) {
        startActivityForResult(signInIntent, resourceCodeSignIn);
    }

    @Override
    public void goToMoviesFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
            .add(R.id.fragment_container, new MoviesListFragment())
            .addToBackStack(null).commit();
    }

    @Override
    public void setUsername(String username) {
        usernameTextView.setText(username);
    }

    @Override
    public void setDefaultUsername() {
        usernameTextView.setText(R.string.user_status);
    }
}
