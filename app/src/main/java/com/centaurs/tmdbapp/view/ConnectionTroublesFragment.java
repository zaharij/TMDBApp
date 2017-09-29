package com.centaurs.tmdbapp.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.centaurs.tmdbapp.R;

public class ConnectionTroublesFragment extends Fragment{
    private TextView connectionMessageTextView;
    private Button retrytButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_troubles, container, false);

        connectionMessageTextView = (TextView) view.findViewById(R.id.no_connection_textView);
        connectionMessageTextView.setText(getResources().getString(R.string.no_connection_mess));

        retrytButton = (Button) view.findViewById(R.id.retry_connection_button);
        retrytButton.setText(getResources().getString(R.string.restart_button_str));
        retrytButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MoviesActivity) getActivity()).startMoviesFragment();
                ((MoviesActivity) getActivity()).startCheckingNetwork();
            }
        });
        return view;
    }
}
