package com.centaurs.tmdbapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.di.Injector;
import com.centaurs.tmdbapp.ui.login.UserLoginFragment;
import com.centaurs.tmdbapp.ui.movieslist.MoviesListFragment;

import static com.centaurs.tmdbapp.ui.movieslist.MoviesListFragment.MOVIES_LIST_FRAGMENT_EXTRA;

public class MovieActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 100;
    public static final String START_FRAGMENT_EXTRA = "MovieActivity";

    public interface OnBackPressedListener{
        boolean handleOnnBackPressedForResult();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        if (ContextCompat.checkSelfPermission(MovieActivity.this
                , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MovieActivity.this
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , PERMISSION_REQUEST);
        }

        Injector.getInstance().plusMovieComponent(this);

        if (getIntent().getStringExtra(START_FRAGMENT_EXTRA) != null
                && getIntent().getStringExtra(START_FRAGMENT_EXTRA).equals(MOVIES_LIST_FRAGMENT_EXTRA)){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new MoviesListFragment()).commit();
        } else if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new UserLoginFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (Fragment fragment: fragmentManager.getFragments()){
            if (fragment instanceof OnBackPressedListener){
                if (((OnBackPressedListener) fragment).handleOnnBackPressedForResult()){
                    return;
                }
            }
        }
        super.onBackPressed();
    }
}
