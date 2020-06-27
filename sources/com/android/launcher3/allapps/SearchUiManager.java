package com.android.launcher3.allapps;

import android.support.animation.SpringAnimation;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

public interface SearchUiManager {

    public interface OnScrollRangeChangeListener {
        void onScrollRangeChanged(int i);
    }

    void addOnScrollRangeChangeListener(OnScrollRangeChangeListener onScrollRangeChangeListener);

    @NonNull
    SpringAnimation getSpringForFling();

    void initialize(AlphabeticalAppsList alphabeticalAppsList, AllAppsRecyclerView allAppsRecyclerView);

    void preDispatchKeyEvent(KeyEvent keyEvent);

    void refreshSearchResult();

    void reset();

    void startAppsSearch();
}
