package com.centaurs.tmdbapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectionUtil {
    private static NetworkConnectionUtil networkConnectionUtil;
    private Context context;

    private NetworkConnectionUtil(Context context){
        this.context = context;
    }

    public static NetworkConnectionUtil getInstance(Context context){
        if (networkConnectionUtil == null){
            networkConnectionUtil = new NetworkConnectionUtil(context);
        }
        return networkConnectionUtil;
    }

    public static NetworkConnectionUtil getInstance(){
        return networkConnectionUtil;
    }

    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
