package com.centaurs.tmdbapp.ui.networktroubles;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.centaurs.tmdbapp.R;
import com.centaurs.tmdbapp.di.Injector;

import java.io.Serializable;

import javax.inject.Inject;

public class NetworkConnectionTroublesFragment extends Fragment implements INetworkConnectionTroublesContract.IView {
    private final static String RETRY_LISTENER_ARG = "onRetry";
    @Inject
    INetworkConnectionTroublesContract.IPresenter presenter;
    private OnRetryListener onRetryListener;

    public interface OnRetryListener extends Serializable{
        Fragment onRetryGetBackFragment();
    }

    public static NetworkConnectionTroublesFragment getInstance(OnRetryListener onRetryListener){
        Bundle args = new Bundle();
        args.putSerializable(RETRY_LISTENER_ARG, onRetryListener);
        NetworkConnectionTroublesFragment networkConnectionTroublesFragment = new NetworkConnectionTroublesFragment();
        networkConnectionTroublesFragment.setArguments(args);
        return networkConnectionTroublesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance().getMovieComponent().inject(this);
        onRetryListener = (OnRetryListener) getArguments().getSerializable(RETRY_LISTENER_ARG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network_connection_troubles, container, false);
        Button retryButton = view.findViewById(R.id.retry_connection_button);
        retryButton.setOnClickListener(onButtonClickListener);
        presenter.attachView(this);
        return view;
    }

    private View.OnClickListener onButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            presenter.onRetryButtonClicked();
        }
    };

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void goBackToPreviousFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, onRetryListener.onRetryGetBackFragment())
                .commit();
    }
}
