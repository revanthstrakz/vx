package com.google.android.apps.nexuslauncher.search;

import com.android.launcher3.allapps.search.AllAppsSearchBarController.Callbacks;
import com.android.launcher3.util.ComponentKey;
import java.util.ArrayList;

class SearchResult {
    final ArrayList<ComponentKey> mApps = new ArrayList<>();
    final Callbacks mCallbacks;
    final String mQuery;

    SearchResult(String str, Callbacks callbacks) {
        this.mQuery = str;
        this.mCallbacks = callbacks;
    }
}
