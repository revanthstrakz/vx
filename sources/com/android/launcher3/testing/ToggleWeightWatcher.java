package com.android.launcher3.testing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.TestingUtils;

public class ToggleWeightWatcher extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SharedPreferences prefs = Utilities.getPrefs(this);
        boolean z = !prefs.getBoolean(TestingUtils.SHOW_WEIGHT_WATCHER, true);
        prefs.edit().putBoolean(TestingUtils.SHOW_WEIGHT_WATCHER, z).apply();
        Launcher launcher = (Launcher) LauncherAppState.getInstance(this).getModel().getCallback();
        if (!(launcher == null || launcher.mWeightWatcher == null)) {
            launcher.mWeightWatcher.setVisibility(z ? 0 : 8);
        }
        finish();
    }
}
