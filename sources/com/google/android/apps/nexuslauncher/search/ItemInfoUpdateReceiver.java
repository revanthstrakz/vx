package com.google.android.apps.nexuslauncher.search;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import com.android.launcher3.AppInfo;
import com.android.launcher3.C0622R;
import com.android.launcher3.IconCache;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherCallbacks;
import com.android.launcher3.allapps.AllAppsRecyclerView;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.util.ComponentKeyMapper;
import com.google.android.apps.nexuslauncher.SettingsActivity;

public class ItemInfoUpdateReceiver implements com.android.launcher3.IconCache.ItemInfoUpdateReceiver, OnSharedPreferenceChangeListener {

    /* renamed from: eD */
    private final int f91eD;
    private final LauncherCallbacks mCallbacks;
    private final Launcher mLauncher;

    public void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon) {
    }

    public ItemInfoUpdateReceiver(Launcher launcher, LauncherCallbacks launcherCallbacks) {
        this.mLauncher = launcher;
        this.mCallbacks = launcherCallbacks;
        this.f91eD = launcher.getDeviceProfile().allAppsNumCols;
    }

    /* renamed from: di */
    public void mo12952di() {
        AlphabeticalAppsList apps = ((AllAppsRecyclerView) this.mLauncher.findViewById(C0622R.C0625id.apps_list_view)).getApps();
        IconCache iconCache = LauncherAppState.getInstance(this.mLauncher).getIconCache();
        int i = 0;
        for (ComponentKeyMapper findApp : this.mCallbacks.getPredictedApps()) {
            AppInfo findApp2 = apps.findApp(findApp);
            if (findApp2 != null) {
                if (findApp2.usingLowResIcon) {
                    iconCache.updateIconInBackground(this, findApp2);
                }
                i++;
                if (i >= this.f91eD) {
                    return;
                }
            }
        }
    }

    public void onCreate() {
        this.mLauncher.getSharedPrefs().registerOnSharedPreferenceChangeListener(this);
    }

    public void onDestroy() {
        this.mLauncher.getSharedPrefs().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if ("reflection_last_predictions".equals(str) || SettingsActivity.SHOW_PREDICTIONS_PREF.equals(str)) {
            mo12952di();
        }
    }
}
