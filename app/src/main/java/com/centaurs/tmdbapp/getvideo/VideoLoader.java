package com.centaurs.tmdbapp.getvideo;

import android.net.Uri;
import android.util.Log;

import com.centaurs.tmdbapp.data.YoutubeApi;
import com.centaurs.tmdbapp.data.api.MoviesApi;
import com.centaurs.tmdbapp.data.fileload.FileLoader;
import com.centaurs.tmdbapp.data.models.Movie;
import com.centaurs.tmdbapp.data.models.MovieTrailer;
import com.centaurs.tmdbapp.data.models.MoviesTrailer;
import com.centaurs.tmdbapp.data.util.IDataCallback;
import com.centaurs.tmdbapp.di.Injector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

public class VideoLoader implements Runnable{
    private final String TAG = "VideoLoader";
    private final String VIDEO_FILE_EXTENSION = ".mp4";
    private final String SITE_YOUTUBE = "YouTube";
    private Map<String, MovieTrailer> videoMap;
    private VideoLoaderService.IVideoLoaderCallback videoLoaderCallback;
    private int movieId;
    private int startId;
    private String movieTitle;
    private VideoLoaderService.IProgressListener progressListener;
    private int currentProgress;
    private long whenStartedMillis;
    @Inject
    MoviesApi moviesApi;
    @Inject
    FileLoader fileLoader;
    @Inject
    YoutubeApi youtubeApi;

    VideoLoader (VideoLoaderService.IProgressListener progressListener
            , VideoLoaderService.IVideoLoaderCallback videoLoaderCallback, int movieId
            , int startId, long whenStartedMillis){
        Injector.getInstance().getMovieAppComponent().inject(this);
        this.progressListener = progressListener;
        this.videoLoaderCallback = videoLoaderCallback;
        this.movieId = movieId;
        this.startId = startId;
        this.whenStartedMillis = whenStartedMillis;
        videoMap = new HashMap<>();
    }

    @Override
    public void run() {
        moviesApi.loadMovie(movieCallback, movieId);
    }

    private IDataCallback<Movie> movieCallback = new IDataCallback<Movie>() {
        @Override
        public void onResponse(Movie movie) {
            movieTitle = movie.getTitle().concat(VIDEO_FILE_EXTENSION);
            moviesApi.loadMovieTrailer(movieTrailerDataCallback, movieId);
        }

        @Override
        public void onFailure(Throwable throwable) {
            movieTitle = UUID.randomUUID().toString().concat(VIDEO_FILE_EXTENSION);
            moviesApi.loadMovieTrailer(movieTrailerDataCallback, movieId);
        }
    };

    private IDataCallback<MoviesTrailer> movieTrailerDataCallback = new IDataCallback<MoviesTrailer>() {
        @Override
        public void onResponse(MoviesTrailer moviesTrailer) {
            if (moviesTrailer.getResults().size() > 0 && moviesTrailer.getResults().get(0).getSite().equals(SITE_YOUTUBE)){
                youtubeApi.loadTrailerUriFromYoutube(uriCallBack, moviesTrailer.getResults().get(0).getKey());
                videoMap.put(moviesTrailer.getResults().get(0).getKey(), moviesTrailer.getResults().get(0));
            } else {
                videoLoaderCallback.onResult(movieTitle, false, startId);
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
            videoLoaderCallback.onResult(movieTitle, false, startId);
        }
    };

    private IDataCallback<Uri> uriCallBack = new IDataCallback<Uri>() {
        @Override
        public void onResponse(Uri uri) {
            if (uri != null){
                fileLoader.downloadFile(videoFileLoaderCallback, progressLoadListener, uri.toString(), movieTitle);
            } else {
                videoLoaderCallback.onResult(movieTitle, false, startId);
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
            videoLoaderCallback.onResult(movieTitle, false, startId);
        }
    };

    private IDataCallback<Boolean> videoFileLoaderCallback = new IDataCallback<Boolean>() {
        @Override
        public void onResponse(Boolean isSuccess) {
            videoLoaderCallback.onResult(movieTitle, isSuccess, startId);
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.e(TAG, throwable.getMessage());
            videoLoaderCallback.onResult(movieTitle, false, startId);
        }
    };

    private FileLoader.IProgressListener progressLoadListener = new FileLoader.IProgressListener() {
        @Override
        public void onProgressChanged(long max, long progress) {
            if ((getProgressPercents(max, progress) - currentProgress) >= 1){
                currentProgress = getProgressPercents(max, progress);
                progressListener.onProgressChanged(movieTitle, startId, currentProgress, whenStartedMillis);
            }
        }
    };

    private int getProgressPercents(long max, long progress){
        return (int) ((progress * 100) / max);
    }
}