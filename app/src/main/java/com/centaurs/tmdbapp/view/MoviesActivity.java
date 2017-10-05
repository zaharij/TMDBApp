package com.centaurs.tmdbapp.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.centaurs.tmdbapp.R;

public class MoviesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        FragmentManager fm = getSupportFragmentManager();
        if(!(fm.getFragments().size() >= 1)){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.grid_movies_fragment_container, new LoginFragment()).commit();
        }
    }
}
