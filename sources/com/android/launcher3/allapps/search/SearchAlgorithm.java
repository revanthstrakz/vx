package com.android.launcher3.allapps.search;

import com.android.launcher3.allapps.search.AllAppsSearchBarController.Callbacks;

public interface SearchAlgorithm {
    void cancel(boolean z);

    void doSearch(String str, Callbacks callbacks);
}
