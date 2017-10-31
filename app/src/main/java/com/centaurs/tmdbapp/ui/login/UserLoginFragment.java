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
import com.centaurs.tmdbapp.ui.movieslist.MoviesListFragment;
import com.centaurs.tmdbapp.util.LoginHelper;

public class UserLoginFragment extends Fragment implements IUserLoginContract.IView {
    private IUserLoginContract.IPresenter presenter;
    private Button signInButton, signOutButton;
    private ImageView profileImageView;
    private TextView usernameTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new UserLoginPresenter(new LoginHelper(getActivity(), connectionFailedListener));
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
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResumed();
    }

    private View.OnClickListener onButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.sign_in_button:
                    presenter.signInButtonClicked();
                    break;
                case R.id.sign_out_button:
                    presenter.signOutButtonClicked();
                    break;
                case R.id.movies_button:
                    presenter.moviesButtonClicked();
                    break;
            }
        }
    };

    private LoginHelper.IConnectionFailedListener connectionFailedListener = new LoginHelper.IConnectionFailedListener() {
        @Override
        public void onConnectionFailed() {
            presenter.onConnectionFailed();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onReceivedLoginResult(requestCode, data);
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
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
        profileImageView.setImageResource(R.drawable.profile_default);
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
