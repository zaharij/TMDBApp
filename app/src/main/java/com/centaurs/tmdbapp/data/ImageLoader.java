package com.centaurs.tmdbapp.data;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.centaurs.tmdbapp.data.models.Configuration;
import com.centaurs.tmdbapp.MovieApplication;

public class ImageLoader {
    private final String TAG = "ImageLoader";
    private static ImageLoader imageLoader;
    private String originalPosterSize;
    private String minPosterSize;
    private MovieApplication.IOnNeedAppContext onNeedContext;

    private ImageLoader(MovieApplication.IOnNeedAppContext onNeedContext){
        this.onNeedContext = onNeedContext;
        MoviesApi.IDataCallback<Configuration> baseImageUrlCallback = new MoviesApi.IDataCallback<Configuration>() {
            @Override
            public void onResponse(Configuration response) {
                String basePosterUrl = response.getImages().getBaseUrl();
                originalPosterSize = basePosterUrl.concat(response.getImages().getPosterSizes()
                        .get(response.getImages().getPosterSizes().size() - 1));
                minPosterSize = basePosterUrl.concat(response.getImages().getPosterSizes().get(0));
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        };
        MoviesApi.getInstance().loadMoviesConfiguration(baseImageUrlCallback);
    }

    public static ImageLoader getInstance(MovieApplication.IOnNeedAppContext onNeedContext){
        if (imageLoader == null){
            imageLoader = new ImageLoader(onNeedContext);
        }
        return imageLoader;
    }

    public static ImageLoader getInstance(){
        return imageLoader;
    }

    public interface IPosterLoadingCallback{
        void onReturnImageResult(String key, Drawable drawable);
    }

    public void loadPoster(String posterPath, boolean isOriginalSize, IPosterLoadingCallback posterLoadingCallback){
        String imageUrl;
        if (isOriginalSize){
            imageUrl = originalPosterSize;
        } else {
            imageUrl = minPosterSize;
        }
        loadImageFromNetwork(imageUrl, posterPath, posterLoadingCallback, false);
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
        RequestBuilder<Drawable> drawableRequestBuilder = Glide.with(onNeedContext.getContext()).load(currentImageUrl);
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
        });
    }
}
