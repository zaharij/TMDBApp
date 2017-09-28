package com.centaurs.tmdbapp.model.config;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.centaurs.tmdbapp.model.constants.QueryConstants.BASE_URL;

public class RetrofitConfig {
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }
}
