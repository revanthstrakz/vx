package com.google.android.apps.nexuslauncher.search;

import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel.Callbacks;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageManagerHelper;

public class AppLaunchActivity extends BaseActivity {
    private void startUri(Uri uri) {
        try {
            ComponentKey uriToComponent = AppSearchProvider.uriToComponent(uri, this);
            AppItemInfoWithIcon appItemInfoWithIcon = new AppItemInfoWithIcon(uriToComponent);
            if (getPackageManager().isSafeMode()) {
                if (!Utilities.isSystemApp(this, appItemInfoWithIcon.getIntent())) {
                    Toast.makeText(this, C0622R.string.safemode_shortcut_error, 0).show();
                    return;
                }
            }
            if (uriToComponent.user.equals(Process.myUserHandle())) {
                startActivity(appItemInfoWithIcon.getIntent());
            } else {
                LauncherAppsCompat.getInstance(this).startActivityForProfile(uriToComponent.componentName, uriToComponent.user, getIntent().getSourceBounds(), null);
            }
            View view = new View(this);
            view.setTag(appItemInfoWithIcon);
            Callbacks callback = LauncherAppState.getInstance(this).getModel().getCallback();
            if (callback instanceof Launcher) {
                int i = ((Launcher) callback).getWorkspace().getState().containerType;
            }
            String queryParameter = uri.getQueryParameter("predictionRank");
            new LogContainerProvider(this, TextUtils.isEmpty(queryParameter) ? -1 : Integer.parseInt(queryParameter)).addView(view);
        } catch (Exception unused) {
            Toast.makeText(this, C0622R.string.activity_not_found, 0).show();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDeviceProfile = LauncherAppState.getIDP(this).getDeviceProfile(this);
        Uri data = getIntent().getData();
        if (data == null) {
            String stringExtra = getIntent().getStringExtra("query");
            if (!TextUtils.isEmpty(stringExtra)) {
                startActivity(PackageManagerHelper.getMarketSearchIntent(this, stringExtra));
            }
        } else {
            startUri(data);
        }
        finish();
    }
}
