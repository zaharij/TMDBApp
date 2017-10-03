package com.centaurs.tmdbapp.view;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;

import java.io.Serializable;

public class ConnectionTroublesFragment extends Fragment{
    private static String NEXT_FRAGMENT_ARGS = "nextFragment";
    private OnRetryClickListener onRetryClickListener;

    interface OnRetryClickListener extends Serializable{
        void onRetryClick();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRetryClickListener = (OnRetryClickListener) getArguments().getSerializable(NEXT_FRAGMENT_ARGS);
    }

    public static ConnectionTroublesFragment getInstanceIfNoNetwork(Context context
            , OnRetryClickListener onRetryClickListener){
        if (!ConnectionTroublesFragment.checkNetworkForResult(context)){
            ConnectionTroublesFragment connectionTroublesFragment = new ConnectionTroublesFragment();
            Bundle args = new Bundle();
            args.putSerializable(NEXT_FRAGMENT_ARGS, onRetryClickListener);
            connectionTroublesFragment.setArguments(args);
            return connectionTroublesFragment;
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_troubles, container, false);

        TextView connectionMessageTextView = view.findViewById(R.id.no_connection_textView);
        connectionMessageTextView.setText(getString(R.string.no_connection_mess));

        Button retryButton = view.findViewById(R.id.retry_connection_button);
        retryButton.setText(getString(R.string.restart_button_str));
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryClickListener.onRetryClick();
            }
        });
        return view;
    }

    private static boolean checkNetworkForResult(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
