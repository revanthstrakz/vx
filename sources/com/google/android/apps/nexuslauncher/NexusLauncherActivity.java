package com.google.android.apps.nexuslauncher;

import android.content.SharedPreferences;
import android.os.Bundle;
import com.android.launcher3.AppInfo;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.WallpaperManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.ComponentKeyMapper;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;
import java.util.List;

public class NexusLauncherActivity extends Launcher {
    private static final String PREF_IS_RELOAD = "pref_reload_workspace";
    private boolean mIsReload;
    private NexusLauncher mLauncher = new NexusLauncher(this);
    private String mThemeHints;

    public void onCreate(Bundle bundle) {
        FeatureFlags.QSB_ON_FIRST_SCREEN = showSmartspace();
        this.mThemeHints = themeHints();
        SharedPreferences prefs = Utilities.getPrefs(this);
        if (!PixelBridge.isInstalled(this)) {
            prefs.edit().putBoolean(SettingsActivity.ENABLE_MINUS_ONE_PREF, false).apply();
        }
        super.onCreate(bundle);
        boolean z = prefs.getBoolean(PREF_IS_RELOAD, false);
        this.mIsReload = z;
        if (z) {
            prefs.edit().remove(PREF_IS_RELOAD).apply();
            showOverviewMode(false);
            setWorkspaceLoading(false);
        }
    }

    public void onStart() {
        super.onStart();
        boolean z = !this.mThemeHints.equals(themeHints());
        if (FeatureFlags.QSB_ON_FIRST_SCREEN != showSmartspace() || z) {
            if (z) {
                WallpaperManagerCompat.getInstance(this).updateAllListeners();
            }
            Utilities.getPrefs(this).edit().putBoolean(PREF_IS_RELOAD, true).apply();
            recreate();
        }
    }

    public void recreate() {
        if (Utilities.ATLEAST_NOUGAT) {
            super.recreate();
            return;
        }
        finish();
        startActivity(getIntent());
    }

    public void clearPendingExecutor(ViewOnDrawExecutor viewOnDrawExecutor) {
        super.clearPendingExecutor(viewOnDrawExecutor);
        if (this.mIsReload) {
            this.mIsReload = false;
            showOverviewMode(false);
            getWorkspace().stripEmptyScreens();
        }
    }

    private boolean showSmartspace() {
        return Utilities.getPrefs(this).getBoolean(SettingsActivity.SMARTSPACE_PREF, true);
    }

    private String themeHints() {
        return Utilities.getPrefs(this).getString(Utilities.THEME_OVERRIDE_KEY, "");
    }

    public void overrideTheme(boolean z, boolean z2, boolean z3) {
        boolean z4 = false;
        if ((Utilities.getDevicePrefs(this).getInt("pref_persistent_flags", 0) & (getResources().getConfiguration().orientation == 2 ? 16 : 8)) != 0) {
            z4 = true;
        }
        boolean z5 = z2 & Utilities.ATLEAST_NOUGAT;
        if (z4 && z) {
            setTheme(C0622R.style.GoogleSearchLauncherThemeDark);
        } else if (z4 && z5) {
            setTheme(C0622R.style.GoogleSearchLauncherThemeDarkText);
        } else if (z4 && z3) {
            setTheme(C0622R.style.GoogleSearchLauncherThemeTransparent);
        } else if (z4) {
            setTheme(C0622R.style.GoogleSearchLauncherTheme);
        } else {
            super.overrideTheme(z, z5, z3);
        }
    }

    public List<ComponentKeyMapper<AppInfo>> getPredictedApps() {
        return this.mLauncher.mCallbacks.getPredictedApps();
    }

    public LauncherClient getGoogleNow() {
        return this.mLauncher.mClient;
    }
}
