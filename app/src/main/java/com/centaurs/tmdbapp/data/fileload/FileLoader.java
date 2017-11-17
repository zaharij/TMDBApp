package com.centaurs.tmdbapp.data.fileload;


import android.os.Environment;
import android.support.annotation.NonNull;

import com.centaurs.tmdbapp.data.util.IDataCallback;
import com.centaurs.tmdbapp.data.util.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileLoader {
    private final String TAG = "FileLoaderTag";
    private final int FILE_READER_BYTE = 4096;
    private IFileLoaderClient fileDownloadClient;
    public static final String DOWNLOAD_FILE_DIRECTORY = String.valueOf(Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));

    public FileLoader(){
        fileDownloadClient = RetrofitClient.getRetrofitClient().create(IFileLoaderClient.class);
    }

    public void downloadFile(final IDataCallback<Boolean> callback, String url, final String fileName){
        fileDownloadClient.downloadVideoFile(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(writeResponseBodyToDisk(response.body(), fileName));
                    }
                }).start();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            File futureStudioIconFile = new File(DOWNLOAD_FILE_DIRECTORY, fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[FILE_READER_BYTE];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
