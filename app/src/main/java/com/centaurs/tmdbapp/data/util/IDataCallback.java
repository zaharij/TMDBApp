package com.centaurs.tmdbapp.data.util;


public interface IDataCallback<T>{
    void onResponse(T response);
    void onFailure(Throwable throwable);
}
