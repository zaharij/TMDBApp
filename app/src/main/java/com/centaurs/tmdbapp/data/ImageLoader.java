package com.centaurs.tmdbapp.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.centaurs.tmdbapp.data.models.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private final String TAG = "ImageLoader";
    private final String LOADING_FAILED_MESSAGE = "Loading failed!";
    private static ImageLoader imageLoader;
    private String originalPosterUrl;
    private String minPosterUrl;
    private Context context;
    // storage for call parameters in case when image url is not ready
    private Map<String, IPosterLoadingCallback> originalPosterCallTempMap;
    private Map<String, IPosterLoadingCallback> minPosterCallTempMap;

    private ImageLoader(Context context){
        this.context = context;
    }

    public interface IPosterLoadingCallback{
        void onReturnImageResult(String key, @Nullable Drawable drawable);
    }

    private MoviesApi.IDataCallback<Configuration> baseOriginalPosterUrlCallback = new MoviesApi.IDataCallback<Configuration>() {
        @Override
        public void onResponse(Configuration response) {
            String basePosterUrl = response.getImages().getBaseUrl();
            originalPosterUrl = basePosterUrl.concat(response.getImages().getPosterSizes()
                    .get(response.getImages().getPosterSizes().size() - 1));
            for(String posterPath: originalPosterCallTempMap.keySet()){
                loadImageFromNetwork(originalPosterUrl, posterPath, originalPosterCallTempMap.get(posterPath), false);
            }
            originalPosterCallTempMap = null;
        }

        @Override
        public void onFailure(Throwable throwable) {
            originalPosterCallTempMap = null;
            Log.e(TAG, throwable.getMessage());
        }
    };

    private MoviesApi.IDataCallback<Configuration> baseMinPosterUrlCallback = new MoviesApi.IDataCallback<Configuration>() {
        @Override
        public void onResponse(Configuration response) {
            String basePosterUrl = response.getImages().getBaseUrl();
            minPosterUrl = basePosterUrl.concat(response.getImages().getPosterSizes().get(0));
            for(String posterPath: minPosterCallTempMap.keySet()){
                loadImageFromNetwork(minPosterUrl, posterPath, minPosterCallTempMap.get(posterPath), false);
            }
            minPosterCallTempMap = null;
        }

        @Override
        public void onFailure(Throwable throwable) {
            minPosterCallTempMap = null;
            Log.e(TAG, throwable.getMessage());
        }
    };

    public static ImageLoader getInstance(Context context){
        if (imageLoader == null){
            imageLoader = new ImageLoader(context);
        }
        return imageLoader;
    }

    public static ImageLoader getInstance(){
        return imageLoader;
    }

    /**
     * a public method, which loads posters from the Internet
     * , if image url is not ready yet, it loads the last from the net
     * , saves the call parameters to the temp storage (originalPosterCallTempMap || minPosterCallTempMap) till loading end
     * , if url is already loading just saves the call parameters.
     * @param posterPath - needed poster path
     * @param isOriginalSize - true if needed size is original
     * @param posterLoadingCallback - callback when poster is loaded
     */
    public void loadPoster(String posterPath, boolean isOriginalSize, IPosterLoadingCallback posterLoadingCallback){
        if (isOriginalSize){
            if (originalPosterUrl == null){
                if (originalPosterCallTempMap == null){
                    originalPosterCallTempMap = new HashMap<>();
                    originalPosterCallTempMap.put(posterPath, posterLoadingCallback);
                    MoviesApi.getInstance().loadMoviesConfiguration(baseOriginalPosterUrlCallback);
                } else {
                    originalPosterCallTempMap.put(posterPath, posterLoadingCallback);
                }
            } else {
                loadImageFromNetwork(originalPosterUrl, posterPath, posterLoadingCallback, false);
            }
        } else {
            if (minPosterUrl == null){
                if (minPosterCallTempMap == null){
                    minPosterCallTempMap = new HashMap<>();
                    minPosterCallTempMap.put(posterPath, posterLoadingCallback);
                    MoviesApi.getInstance().loadMoviesConfiguration(baseMinPosterUrlCallback);
                } else {
                    minPosterCallTempMap.put(posterPath, posterLoadingCallback);
                }
            } else {
                loadImageFromNetwork(minPosterUrl, posterPath, posterLoadingCallback, false);
            }
        }
    }

    public void loadImage(String posterPath, IPosterLoadingCallback posterLoadingCallback){
        loadImageFromNetwork(posterPath, null,  posterLoadingCallback, true);
    }

    private void loadImageFromNetwork(String imageUrl, final String imagePath
            , final IPosterLoadingCallback posterLoadingCallback, boolean isRound){
        String currentImageUrl;
        if (imagePath != null){
            currentImageUrl = imageUrl.concat(imagePath);
        } else {
            currentImageUrl = imageUrl;
        }
        RequestBuilder<Drawable> drawableRequestBuilder = Glide.with(context).load(currentImageUrl);
        if (isRound){
            drawableRequestBuilder.apply(RequestOptions.circleCropTransform());
        }
        drawableRequestBuilder.into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (resource != null){
                    posterLoadingCallback.onReturnImageResult(imagePath, resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                Log.e(TAG, LOADING_FAILED_MESSAGE);
                posterLoadingCallback.onReturnImageResult(imagePath, errorDrawable);
            }
        });
    }
}
