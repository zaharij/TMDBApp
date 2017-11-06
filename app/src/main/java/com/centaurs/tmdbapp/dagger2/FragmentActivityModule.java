package com.centaurs.tmdbapp.dagger2;

import android.support.v4.app.FragmentActivity;

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
}
