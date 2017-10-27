package com.centaurs.tmdbapp.ui;

import android.os.Bundle;
import android.util.SparseArray;

import java.util.concurrent.atomic.AtomicInteger;

public class PresenterManager {
    private static final String SIS_KEY_PRESENTER_ID = "presenter_id";
    private static PresenterManager instance;
    private final AtomicInteger currentId;
    private final SparseArray<IBasePresenter> presenters;

    private PresenterManager() {
        currentId = new AtomicInteger();
        presenters = new SparseArray<>();
    }

    public static PresenterManager getInstance() {
        if (instance == null) {
            instance = new PresenterManager();
        }
        return instance;
    }

    public IBasePresenter restorePresenter(Bundle savedInstanceState) {
        int presenterId = savedInstanceState.getInt(SIS_KEY_PRESENTER_ID);
        IBasePresenter presenter = presenters.get(presenterId);
        presenters.delete(presenterId);
        return presenter;
    }

    public void savePresenter(IBasePresenter presenter, Bundle outState) {
        int presenterId = currentId.incrementAndGet();
        presenters.put(presenterId, presenter);
        outState.putInt(SIS_KEY_PRESENTER_ID, presenterId);
    }
}
