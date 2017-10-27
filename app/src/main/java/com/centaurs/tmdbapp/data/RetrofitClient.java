package com.centaurs.tmdbapp.data;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.centaurs.tmdbapp.data.constants.DataConstants.BASE_URL;

class RetrofitClient {
    private static Retrofit retrofit;

    static Retrofit getRetrofitClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }
}
