package com.centaurs.tmdbapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.centaurs.tmdbapp.MovieApplication;
import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.dagger2.DaggerMovieActivityComponent;
import com.centaurs.tmdbapp.dagger2.FragmentActivityModule;
import com.centaurs.tmdbapp.dagger2.MovieActivityComponent;
import com.centaurs.tmdbapp.ui.login.UserLoginFragment;

public class MovieActivity extends AppCompatActivity {
    private MovieActivityComponent movieActivityComponent;

    public static MovieActivity get(Fragment fragment){
        return (MovieActivity) fragment.getActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        movieActivityComponent = DaggerMovieActivityComponent.builder()
                .fragmentActivityModule(new FragmentActivityModule(this))
                .movieAppComponent(MovieApplication.get(this).getComponent())
                .build();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new UserLoginFragment()).commit();
        }
    }

    public MovieActivityComponent getMovieActivityComponent() {
        return movieActivityComponent;
    }
}
