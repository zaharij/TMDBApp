package com.centaurs.tmdbapp.getvideo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.data.YoutubeApi;
import com.centaurs.tmdbapp.data.api.MoviesApi;
import com.centaurs.tmdbapp.data.fileload.FileLoader;
import com.centaurs.tmdbapp.di.Injector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

public class VideoLoaderService extends Service {
    public static final String MOVIE_ID_SERVICE_EXTRA = "MovieIdService";
    private final int THREAD_POOL_SIZE = 5;
    private ExecutorService executorService;
    private NotificationManager notificationManager;
    private AtomicInteger startIdsCounter;
    private int lastStartId;
    @Inject
    MoviesApi moviesApi;
    @Inject
    FileLoader fileLoader;
    @Inject
    YoutubeApi youtubeApi;

    interface IVideoLoaderCallback{
        void onResult(String movieTitle, boolean isSuccess, int startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.getInstance(this).getMovieAppComponent().inject(this);
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        startIdsCounter = new AtomicInteger(0);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startIdsCounter.incrementAndGet();
        lastStartId = lastStartId < startId ? startId : lastStartId;
        executorService.execute(new VideoLoader(moviesApi, fileLoader, youtubeApi
                , videoLoaderCallback, intent.getExtras().getInt(MOVIE_ID_SERVICE_EXTRA), startId));
        return START_REDELIVER_INTENT;
    }

    private IVideoLoaderCallback videoLoaderCallback = new IVideoLoaderCallback() {
        @Override
        public void onResult(String movieTitle, boolean isSuccess, int startId) {
            sendNotification(movieTitle, isSuccess, startId);
            if (startId < lastStartId) {
                stopSelf(startId);
            }
            if (startIdsCounter.decrementAndGet() == 0){
                stopSelf(lastStartId);
            }
        }
    };

    private void sendNotification(String movieTitle, boolean isSuccess, int id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(FileLoader.DOWNLOAD_FILE_DIRECTORY.concat("/").concat(movieTitle)), "video/*");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_download_movies)
                .setContentText(movieTitle)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        if (isSuccess){
            builder.setContentTitle(this.getResources().getString(R.string.downloaded_message));
        } else {
            builder.setContentTitle(this.getResources().getString(R.string.not_downloaded_message));
        }
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }
}
