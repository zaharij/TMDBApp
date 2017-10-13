package com.centaurs.tmdbapp.ui;

import android.os.Bundle;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class PresenterManager {
    private static final String SIS_KEY_PRESENTER_ID = "presenter_id";
    private static final int MAX_CACHE_SIZE = 10;
    private static final int CACHE_EXPIRATION_VALUE = 30;
    private static PresenterManager instance;
    private final AtomicLong currentId;
    private final Cache<Long, IBasePresenter> presenters;

    private PresenterManager(long maxSize, long expirationValue, TimeUnit expirationUnit) {
        currentId = new AtomicLong();
        presenters = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expirationValue, expirationUnit)
                .build();
    }

    public static PresenterManager getInstance() {
        if (instance == null) {
            instance = new PresenterManager(MAX_CACHE_SIZE, CACHE_EXPIRATION_VALUE, TimeUnit.SECONDS);
        }
        return instance;
    }

    public IBasePresenter restorePresenter(Bundle savedInstanceState) {
        Long presenterId = savedInstanceState.getLong(SIS_KEY_PRESENTER_ID);
        IBasePresenter presenter = presenters.getIfPresent(presenterId);
        presenters.invalidate(presenterId);
        return presenter;
    }

    public void savePresenter(IBasePresenter presenter, Bundle outState) {
        long presenterId = currentId.incrementAndGet();
        presenters.put(presenterId, presenter);
        outState.putLong(SIS_KEY_PRESENTER_ID, presenterId);
    }
}
