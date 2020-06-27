package com.google.android.apps.nexuslauncher.qsb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.Launcher;
import com.android.launcher3.allapps.AllAppsGridAdapter;
import com.android.launcher3.allapps.AllAppsRecyclerView;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.allapps.search.AllAppsSearchBarController;
import com.android.launcher3.allapps.search.AllAppsSearchBarController.Callbacks;
import com.android.launcher3.discovery.AppDiscoveryItem;
import com.android.launcher3.discovery.AppDiscoveryUpdateState;
import com.google.android.apps.nexuslauncher.search.SearchThread;
import java.util.ArrayList;

public class FallbackAppsSearchView extends ExtendedEditText implements Callbacks {
    private AllAppsGridAdapter mAdapter;
    private AlphabeticalAppsList mApps;
    private AllAppsRecyclerView mAppsRecyclerView;
    private AllAppsQsbLayout mQsbLayout;
    private final AllAppsSearchBarController mSearchBarController;

    public void onAppDiscoverySearchUpdate(@Nullable AppDiscoveryItem appDiscoveryItem, @NonNull AppDiscoveryUpdateState appDiscoveryUpdateState) {
    }

    public FallbackAppsSearchView(Context context) {
        this(context, null);
    }

    public FallbackAppsSearchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FallbackAppsSearchView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSearchBarController = new AllAppsSearchBarController();
    }

    private void notifyResultChanged() {
        this.mQsbLayout.useAlpha(0);
        this.mAppsRecyclerView.onSearchResultsChanged();
    }

    public void initialize(AllAppsQsbLayout allAppsQsbLayout, AlphabeticalAppsList alphabeticalAppsList, AllAppsRecyclerView allAppsRecyclerView) {
        this.mQsbLayout = allAppsQsbLayout;
        this.mApps = alphabeticalAppsList;
        this.mAppsRecyclerView = allAppsRecyclerView;
        this.mAdapter = (AllAppsGridAdapter) allAppsRecyclerView.getAdapter();
        this.mSearchBarController.initialize(new SearchThread(getContext()), this, Launcher.getLauncher(getContext()), this);
    }

    public void clearSearchResult() {
        if (getParent() != null && this.mApps.setOrderedFilter(null)) {
            notifyResultChanged();
        }
    }

    public void onSearchResult(String str, ArrayList arrayList) {
        if (arrayList != null && getParent() != null) {
            this.mApps.setOrderedFilter(arrayList);
            notifyResultChanged();
            this.mAdapter.setLastSearchQuery(str);
        }
    }

    public void refreshSearchResult() {
        this.mSearchBarController.refreshSearchResult();
    }
}
