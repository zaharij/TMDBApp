package com.centaurs.tmdbapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.TMoviesDBApi;
import com.centaurs.tmdbapp.ui.login.UserLoginFragment;

public class TMovieDBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmovie_db);


        TMoviesDBApi.getInstance().loadGenres();

        FragmentManager fm = getSupportFragmentManager();
        if(!(fm.getFragments().size() >= 1)){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new UserLoginFragment()).commit();
        }
    }
}
