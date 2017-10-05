package com.centaurs.tmdbapp.view.vinterfaces;


import android.content.Intent;
import android.graphics.Bitmap;

public interface LoginViewInterface extends ViewInterface {
    void setProfileImg(Bitmap bitmap);
    void setProfileImgDefault();
    void setStatusProfile(int strId);
    void setStatusProfile(String strStatus);
    void updateUISignedInOrOutBooll(boolean signedIn);
    void startActivityForResultOuterCall(Intent intent, int requestCode);
    void toastMessage(int strId);
}

