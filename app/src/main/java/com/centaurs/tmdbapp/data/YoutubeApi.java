package com.centaurs.tmdbapp.data;


import android.net.Uri;

import com.centaurs.tmdbapp.data.util.IDataCallback;
import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YoutubeApi {
    private final YouTubeExtractor youTubeExtractor = YouTubeExtractor.create();

    public void loadTrailerUriFromYoutube(final IDataCallback<Uri> dataCallback, final String youtubeKey){
        youTubeExtractor.extract(youtubeKey).enqueue(new Callback<YouTubeExtractionResult>() {
            @Override
            public void onResponse(@NotNull Call<YouTubeExtractionResult> call
                    , @NotNull Response<YouTubeExtractionResult> response) {
                dataCallback.onResponse(response.body().getBestAvailableQualityVideoUri());
            }

            @Override
            public void onFailure(@NotNull Call<YouTubeExtractionResult> call, @NotNull  Throwable t) {
                dataCallback.onFailure(t);
            }
        });
    }
}
