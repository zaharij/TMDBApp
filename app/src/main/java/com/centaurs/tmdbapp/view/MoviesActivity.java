package com.centaurs.tmdbapp.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
        getSupportFragmentManager().beginTransaction()
                .add(R.id.grid_movies_fragment_container, new LoginFragment()).commit();
    }

    public void startCheckingNetwork(){
        new CheckNetwork().execute();
    }

    public void startMoviesFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.grid_movies_fragment_container, new MoviesGridFragment())
                .addToBackStack(null).commit();
    }

    public void onItemClick(View view){
        try {
            Fragment movieFragment = MovieFragment.getInstance(view.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.grid_movies_fragment_container, movieFragment)
                    .addToBackStack(null).commit();
        } catch (NullPointerException ex){
        }
    }

    class CheckNetwork extends AsyncTask<Void, Void , Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.grid_movies_fragment_container, new ConnectionTroublesFragment())
                        .disallowAddToBackStack().commit();
            }
        }
    }
}
