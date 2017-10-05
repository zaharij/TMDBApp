package com.centaurs.tmdbapp.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.presenter.pclasses.LoginPresenter;
import com.centaurs.tmdbapp.presenter.pinterfaces.LoginPresenterInterface;
import com.centaurs.tmdbapp.view.vinterfaces.LoginViewInterface;

public class LoginFragment extends Fragment implements LoginViewInterface {
    private ImageView profileImageView;
    private TextView userNameTextView;
    private Button accountButton, signOutButton;
    private LoginPresenterInterface loginPresenterInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState == null) {
            loginPresenterInterface = LoginPresenter.getLoginPresenterInterface(getActivity());
        } else {
            loginPresenterInterface = (LoginPresenterInterface) PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loginPresenterInterface.checkAuthentication();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        profileImageView = view.findViewById(R.id.profile_image_view);
        userNameTextView = view.findViewById(R.id.username_textView);
        signOutButton = view.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(onClickListener);
        Button moviesButton = view.findViewById(R.id.movie_button);
        moviesButton.setOnClickListener(onClickListener);
        accountButton = view.findViewById(R.id.account_button);
        accountButton.setOnClickListener(onClickListener);
        loginPresenterInterface.attachView(this);
        loginPresenterInterface.refreshActivity(getActivity());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loginPresenterInterface.detachView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(loginPresenterInterface, outState);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loginPresenterInterface.onClickButton(view);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginPresenterInterface.checkIfRCSignIn(requestCode, data);
    }

    @Override
    public void updateUISignedInOrOutBooll(boolean signedIn) {
        if (signedIn) {
            accountButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            setProfileImgDefault();
            setStatusProfile(R.string.username_default_str);
            accountButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void startActivityForResultOuterCall(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void toastMessage(int strId) {
        Toast.makeText(getContext(), strId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProfileImgDefault() {
        profileImageView.setImageResource(R.drawable.ic_profile_empty);
    }

    @Override
    public void setProfileImg(Bitmap result) {
        profileImageView.setImageBitmap(result);
    }

    @Override
    public void setStatusProfile(int strId) {
        userNameTextView.setText(strId);
    }

    @Override
    public void setStatusProfile(String strStatus) {
        userNameTextView.setText(strStatus);
    }
}
