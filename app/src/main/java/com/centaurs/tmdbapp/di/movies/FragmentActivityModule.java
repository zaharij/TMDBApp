package com.centaurs.tmdbapp.di.movies;

import android.support.v4.app.FragmentActivity;

import com.centaurs.tmdbapp.util.LoginHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentActivityModule {
    private FragmentActivity fragmentActivity;

    public FragmentActivityModule(FragmentActivity fragmentActivity){
        this.fragmentActivity = fragmentActivity;
    }

    @Provides
    FragmentActivity fragmentActivity(){
        return fragmentActivity;
    }

    @Provides
    LoginHelper loginHelper(FragmentActivity fragmentActivity){
        return new LoginHelper(fragmentActivity);
    }
}
