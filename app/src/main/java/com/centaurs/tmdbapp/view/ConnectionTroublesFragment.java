package com.centaurs.tmdbapp.view;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.presenter.pclasses.ConnectionTroublesPresenter;
import com.centaurs.tmdbapp.presenter.pinterfaces.ConnectionTroublesPresenterInterface;

import java.io.Serializable;

public class ConnectionTroublesFragment extends Fragment{
    private static String NEXT_FRAGMENT_ARGS = "nextFragment";
    private OnRetryClickListener onRetryClickListener;

    public interface OnRetryClickListener extends Serializable{
        void onRetryClick();
    }

    public static ConnectionTroublesFragment getInstanceIfNoNetwork(Context context
            , OnRetryClickListener onRetryClickListener){
        ConnectionTroublesPresenterInterface connectionTroublesInterface
                = ConnectionTroublesPresenter.getConnectionTroublesInterface(context);
        if (!connectionTroublesInterface.connectionCheckRequestForResult()){
            ConnectionTroublesFragment connectionTroublesFragment = new ConnectionTroublesFragment();
            Bundle args = new Bundle();
            args.putSerializable(NEXT_FRAGMENT_ARGS, onRetryClickListener);
            connectionTroublesFragment.setArguments(args);
            return connectionTroublesFragment;
        }
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRetryClickListener = (OnRetryClickListener) getArguments().getSerializable(NEXT_FRAGMENT_ARGS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_troubles, container, false);

        Button retryButton = view.findViewById(R.id.retry_connection_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetryClickListener.onRetryClick();
            }
        });
        return view;
    }
}
