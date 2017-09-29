package com.centaurs.tmdbapp.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.centaurs.tmdbapp.R;

public class MoviesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        Fragment fragment = new MoviesGridFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.grid_movies_fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    public void onItemClick(View view){
        Fragment fragment = MovieFragment.getInstance(view.getId());
        getSupportFragmentManager().beginTransaction().replace(R.id.grid_movies_fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
