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
        startMoviesFragment();
        startCheckingNetwork();
    }

    public void startCheckingNetwork(){
        new CheckNetwork().execute();
    }

    public void startMoviesFragment(){
        getSupportFragmentManager().beginTransaction().add(R.id.grid_movies_fragment_container, new MoviesGridFragment())
                .addToBackStack(null).commit();
    }

    public void onItemClick(View view){
        Fragment movieFragment = MovieFragment.getInstance(view.getId());
        getSupportFragmentManager().beginTransaction().replace(R.id.grid_movies_fragment_container, movieFragment)
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

    public class CheckNetwork extends AsyncTask<Void, Void , Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            return haveConnectedWifi || haveConnectedMobile;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.grid_movies_fragment_container, new ConnectionTroublesFragment()).commit();
            }
        }
    }
}
