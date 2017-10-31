package com.centaurs.tmdbapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.ui.login.UserLoginFragment;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new UserLoginFragment()).commit();
        }
    }
}
