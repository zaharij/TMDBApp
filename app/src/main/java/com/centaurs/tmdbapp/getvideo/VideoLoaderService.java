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
import com.centaurs.tmdbapp.ui.MovieActivity;
import com.centaurs.tmdbapp.ui.movieslist.MoviesListFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import static com.centaurs.tmdbapp.data.fileload.FileLoader.LOAD_FILE_THREAD_POOL_NUMBER;

public class VideoLoaderService extends Service {
    public static final String MOVIE_ID_SERVICE_EXTRA = "MovieIdService";
    private final int THREAD_POOL_SIZE = LOAD_FILE_THREAD_POOL_NUMBER;
    private final int MAX_PROGRESS = 100;
    private ExecutorService executorService;
    private NotificationManager notificationManager;
    private AtomicInteger startIdsCounter;
    private HandledStartIdsTemp handledStartIdsTemp;
    @Inject
    MoviesApi moviesApi;
    @Inject
    FileLoader fileLoader;
    @Inject
    YoutubeApi youtubeApi;

    interface IVideoLoaderCallback{
        void onResult(String movieTitle, Boolean isSuccess, int startId);
    }

    interface IProgressListener{
        void onProgressChanged(String movieTitle, int id, int progress, long when);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.getInstance(this).getMovieAppComponent().inject(this);
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        startIdsCounter = new AtomicInteger(0);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        handledStartIdsTemp = new HandledStartIdsTemp(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (flags != START_FLAG_REDELIVERY || !handledStartIdsTemp.isSuccessId(startId)){
            notifyUser(this.getResources().getString(R.string.wait_message), false, startId, 0, 0);
            startIdsCounter.incrementAndGet();
            executorService.execute(new VideoLoader(progressListener, moviesApi, fileLoader, youtubeApi
                    , videoLoaderCallback, intent.getExtras().getInt(MOVIE_ID_SERVICE_EXTRA), startId, System.currentTimeMillis()));
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        handledStartIdsTemp.clearTemp();
        super.onDestroy();
    }

    private IVideoLoaderCallback videoLoaderCallback = new IVideoLoaderCallback() {
        @Override
        public void onResult(String movieTitle, Boolean isSuccess, int startId) {
            handledStartIdsTemp.putHandledStartId(startId, isSuccess);
            if (startIdsCounter.decrementAndGet() == 0){
                stopSelf();
            }
            notifyUser(movieTitle, isSuccess, startId, MAX_PROGRESS, 0);
        }
    };

    private IProgressListener progressListener = new IProgressListener() {
        @Override
        public void onProgressChanged(String movieTitle, int id, int progress, long when) {
            notifyUser(movieTitle, id, progress, when);
        }
    };

    private void notifyUser(String movieTitle, int id, int progress, long when){
        notifyUser(movieTitle, null, id, progress, when);
    }

    private void notifyUser(final String movieTitle, final Boolean isSuccess, final int id, final int progress, long when){
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_download_movies)
                .setContentText(movieTitle);
        if (when > 0){
            builder.setWhen(when);
        }
        if (progress == 0){
            builder.setProgress(0, 0, true);
            builder.setContentTitle(getApplicationContext().getResources().getString(R.string.preparing_message));
        } else if (progress < MAX_PROGRESS){
            builder.setProgress(MAX_PROGRESS, progress, false);
            builder.setContentInfo(this.getString(R.string.progress_status, String.valueOf(progress)));
            builder.setContentTitle(getApplicationContext().getResources().getString(R.string.downloading_message));
        } else {
            if (isSuccess != null) {
                Intent intent;
                if (isSuccess) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(FileLoader.DOWNLOAD_FILE_DIRECTORY.concat("/").concat(movieTitle)), "video/*");
                    builder.setContentTitle(getApplicationContext().getResources()
                            .getString(R.string.downloaded_message)).setAutoCancel(true);
                } else {
                    intent = new Intent(getApplicationContext(), MovieActivity.class);
                    intent.putExtra(MovieActivity.START_FRAGMENT_EXTRA, MoviesListFragment.MOVIES_LIST_FRAGMENT_EXTRA);
                    builder.setContentTitle(getApplicationContext().getResources()
                            .getString(R.string.not_downloaded_message)).setAutoCancel(true);
                }
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                builder.setContentIntent(pIntent);
            }
        }
        notificationManager.notify(id, builder.build());
    }
}
