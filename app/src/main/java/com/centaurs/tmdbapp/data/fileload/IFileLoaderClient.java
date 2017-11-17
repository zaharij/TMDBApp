package com.centaurs.tmdbapp.data.fileload;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface IFileLoaderClient {

    @Streaming
    @GET
    Call<ResponseBody> downloadVideoFile(@Url String fileUrl);
}
