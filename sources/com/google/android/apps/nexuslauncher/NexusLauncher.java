package com.google.android.apps.nexuslauncher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.p001v4.graphics.ColorUtils;
import android.view.Menu;
import android.view.View;
import com.android.launcher3.AppInfo;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherCallbacks;
import com.android.launcher3.LauncherExterns;
import com.android.launcher3.Utilities;
import com.android.launcher3.dynamicui.WallpaperColorInfo;
import com.android.launcher3.dynamicui.WallpaperColorInfo.OnChangeListener;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.util.ComponentKeyMapper;
import com.android.launcher3.util.Themes;
import com.google.android.apps.nexuslauncher.search.ItemInfoUpdateReceiver;
import com.google.android.apps.nexuslauncher.smartspace.SmartspaceController;
import com.google.android.apps.nexuslauncher.smartspace.SmartspaceView;
import com.google.android.apps.nexuslauncher.utils.ActionIntentFilter;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;
import com.google.android.libraries.gsa.launcherclient.LauncherClientService;
import com.google.android.libraries.gsa.launcherclient.StaticInteger;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NexusLauncher {
    final LauncherCallbacks mCallbacks;
    LauncherClient mClient;
    /* access modifiers changed from: private */
    public final LauncherExterns mExterns;
    /* access modifiers changed from: private */
    public boolean mFeedRunning;
    /* access modifiers changed from: private */
    public ItemInfoUpdateReceiver mItemInfoUpdateReceiver;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    /* access modifiers changed from: private */
    public NexusLauncherOverlay mOverlay;
    /* access modifiers changed from: private */
    public boolean mRunning;
    /* access modifiers changed from: private */
    public boolean mStarted;
    /* access modifiers changed from: private */
    public final Bundle mUiInformation = new Bundle();

    class NexusLauncherCallbacks implements LauncherCallbacks, OnSharedPreferenceChangeListener, OnChangeListener {
        private final FeedReconnector mFeedReconnector = new FeedReconnector();
        private SmartspaceView mSmartspace;

        class FeedReconnector implements Runnable {
            private static final int MAX_RETRIES = 10;
            private static final int RETRY_DELAY_MS = 500;
            private int mFeedConnectionTries;
            private final Handler mHandler = new Handler();

            FeedReconnector() {
            }

            /* access modifiers changed from: 0000 */
            public void start() {
                stop();
                this.mFeedConnectionTries = 0;
                this.mHandler.post(this);
            }

            /* access modifiers changed from: 0000 */
            public void stop() {
                this.mHandler.removeCallbacks(this);
            }

            public void run() {
                if (Utilities.getPrefs(NexusLauncher.this.mLauncher).getBoolean(SettingsActivity.ENABLE_MINUS_ONE_PREF, true) && !NexusLauncher.this.mClient.mDestroyed && NexusLauncher.this.mClient.mLayoutParams != null && !NexusLauncher.this.mOverlay.mAttached) {
                    int i = this.mFeedConnectionTries;
                    this.mFeedConnectionTries = i + 1;
                    if (i < 10) {
                        NexusLauncher.this.mClient.exchangeConfig();
                        this.mHandler.postDelayed(this, 500);
                    }
                }
            }
        }

        public void finishBindingItems(boolean z) {
        }

        public Bundle getAdditionalSearchWidgetOptions() {
            return null;
        }

        public View getQsbBar() {
            return null;
        }

        public int getSearchBarHeight() {
            return 0;
        }

        public boolean handleBackPressed() {
            return false;
        }

        public boolean hasCustomContentToLeft() {
            return false;
        }

        public boolean hasSettings() {
            return true;
        }

        public void onActivityResult(int i, int i2, Intent intent) {
        }

        public void onInteractionBegin() {
        }

        public void onInteractionEnd() {
        }

        public void onLauncherProviderChange() {
        }

        public void onNewIntent(Intent intent) {
        }

        public void onPostCreate(Bundle bundle) {
        }

        public boolean onPrepareOptionsMenu(Menu menu) {
            return false;
        }

        public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        }

        public void onSaveInstanceState(Bundle bundle) {
        }

        public void onTrimMemory(int i) {
        }

        public void onWindowFocusChanged(boolean z) {
        }

        public void onWorkspaceLockedChanged() {
        }

        public void populateCustomContentContainer() {
        }

        public void preOnResume() {
        }

        public boolean shouldMoveToDefaultScreenOnHomeIntent() {
            return true;
        }

        NexusLauncherCallbacks() {
        }

        private ItemInfoUpdateReceiver getUpdateReceiver() {
            if (NexusLauncher.this.mItemInfoUpdateReceiver == null) {
                NexusLauncher.this.mItemInfoUpdateReceiver = new ItemInfoUpdateReceiver(NexusLauncher.this.mLauncher, NexusLauncher.this.mCallbacks);
            }
            return NexusLauncher.this.mItemInfoUpdateReceiver;
        }

        public void bindAllApplications(ArrayList<AppInfo> arrayList) {
            getUpdateReceiver().mo12952di();
        }

        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            SmartspaceController.get(NexusLauncher.this.mLauncher).mo12983cX(str, printWriter);
        }

        public List<ComponentKeyMapper<AppInfo>> getPredictedApps() {
            return ((CustomAppPredictor) NexusLauncher.this.mLauncher.getUserEventDispatcher()).getPredictions();
        }

        public void onAttachedToWindow() {
            NexusLauncher.this.mClient.onAttachedToWindow();
            this.mFeedReconnector.start();
        }

        public void onCreate(Bundle bundle) {
            SharedPreferences prefs = Utilities.getPrefs(NexusLauncher.this.mLauncher);
            NexusLauncher.this.mOverlay = new NexusLauncherOverlay(NexusLauncher.this.mLauncher);
            NexusLauncher.this.mClient = new LauncherClient(NexusLauncher.this.mLauncher, NexusLauncher.this.mOverlay, new StaticInteger(((prefs.getBoolean(SettingsActivity.ENABLE_MINUS_ONE_PREF, true) | true) | true) | true ? 1 : 0));
            NexusLauncher.this.mOverlay.setClient(NexusLauncher.this.mClient);
            prefs.registerOnSharedPreferenceChangeListener(this);
            SmartspaceController.get(NexusLauncher.this.mLauncher).mo12982cW();
            this.mSmartspace = (SmartspaceView) NexusLauncher.this.mLauncher.findViewById(C0622R.C0625id.search_container_workspace);
            NexusLauncher.this.mUiInformation.putInt("system_ui_visibility", NexusLauncher.this.mLauncher.getWindow().getDecorView().getSystemUiVisibility());
            WallpaperColorInfo instance = WallpaperColorInfo.getInstance(NexusLauncher.this.mLauncher);
            instance.addOnChangeListener(this);
            onExtractedColorsChanged(instance);
            getUpdateReceiver().onCreate();
        }

        public void onDestroy() {
            LauncherClient launcherClient = NexusLauncher.this.mClient;
            if (!launcherClient.mDestroyed) {
                launcherClient.mActivity.unregisterReceiver(launcherClient.googleInstallListener);
            }
            launcherClient.mDestroyed = true;
            launcherClient.mBaseService.disconnect();
            if (launcherClient.mOverlayCallback != null) {
                launcherClient.mOverlayCallback.mClient = null;
                launcherClient.mOverlayCallback.mWindowManager = null;
                launcherClient.mOverlayCallback.mWindow = null;
                launcherClient.mOverlayCallback = null;
            }
            LauncherClientService launcherClientService = launcherClient.mLauncherService;
            LauncherClient client = launcherClientService.getClient();
            if (client != null && client.equals(launcherClient)) {
                launcherClientService.mClient = null;
                if (!launcherClient.mActivity.isChangingConfigurations()) {
                    launcherClientService.disconnect();
                    if (LauncherClientService.sInstance == launcherClientService) {
                        LauncherClientService.sInstance = null;
                    }
                }
            }
            Utilities.getPrefs(NexusLauncher.this.mLauncher).unregisterOnSharedPreferenceChangeListener(this);
            WallpaperColorInfo.getInstance(NexusLauncher.this.mLauncher).removeOnChangeListener(this);
            getUpdateReceiver().onDestroy();
        }

        public void onDetachedFromWindow() {
            this.mFeedReconnector.stop();
            NexusLauncher.this.mClient.onDetachedFromWindow();
        }

        public void onHomeIntent() {
            NexusLauncher.this.mClient.hideOverlay(NexusLauncher.this.mFeedRunning);
        }

        public void onPause() {
            NexusLauncher.this.mRunning = false;
            NexusLauncher.this.mClient.onPause();
            if (this.mSmartspace != null) {
                this.mSmartspace.onPause();
            }
        }

        public void onResume() {
            NexusLauncher.this.mRunning = true;
            if (NexusLauncher.this.mStarted) {
                NexusLauncher.this.mFeedRunning = true;
            }
            NexusLauncher.this.mClient.onResume();
            if (this.mSmartspace != null) {
                this.mSmartspace.onResume();
            }
        }

        public void onStart() {
            if (!ActionIntentFilter.googleEnabled(NexusLauncher.this.mLauncher)) {
                NexusLauncher.this.mOverlay.setPersistentFlags(0);
            }
            NexusLauncher.this.mStarted = true;
            NexusLauncher.this.mClient.onStart();
        }

        public void onStop() {
            NexusLauncher.this.mStarted = false;
            NexusLauncher.this.mClient.onStop();
            if (!NexusLauncher.this.mRunning) {
                NexusLauncher.this.mFeedRunning = false;
            }
            if (NexusLauncher.this.mOverlay.mFlagsChanged) {
                NexusLauncher.this.mOverlay.mLauncher.recreate();
            }
        }

        public void preOnCreate() {
            DrawableFactory.get(NexusLauncher.this.mLauncher);
        }

        public boolean startSearch(String str, boolean z, Bundle bundle) {
            View findViewById = NexusLauncher.this.mLauncher.findViewById(C0622R.C0625id.g_icon);
            while (findViewById != null && !findViewById.isClickable()) {
                findViewById = findViewById.getParent() instanceof View ? (View) findViewById.getParent() : null;
            }
            if (findViewById == null || !findViewById.performClick()) {
                return false;
            }
            NexusLauncher.this.mExterns.clearTypedText();
            return true;
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            if (SettingsActivity.ENABLE_MINUS_ONE_PREF.equals(str)) {
                LauncherClient launcherClient = NexusLauncher.this.mClient;
                StaticInteger staticInteger = new StaticInteger(((sharedPreferences.getBoolean(SettingsActivity.ENABLE_MINUS_ONE_PREF, true) | true) | true) | true ? 1 : 0);
                if (staticInteger.mData != launcherClient.mFlags) {
                    launcherClient.mFlags = staticInteger.mData;
                    if (launcherClient.mLayoutParams != null) {
                        launcherClient.exchangeConfig();
                    }
                }
            }
        }

        public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
            int integer = NexusLauncher.this.mLauncher.getResources().getInteger(C0622R.integer.extracted_color_gradient_alpha);
            NexusLauncher.this.mUiInformation.putInt("background_color_hint", NexusLauncher.primaryColor(wallpaperColorInfo, NexusLauncher.this.mLauncher, integer));
            NexusLauncher.this.mUiInformation.putInt("background_secondary_color_hint", NexusLauncher.secondaryColor(wallpaperColorInfo, NexusLauncher.this.mLauncher, integer));
            NexusLauncher.this.mUiInformation.putBoolean("is_background_dark", Themes.getAttrBoolean(NexusLauncher.this.mLauncher, C0622R.attr.isMainColorDark));
            NexusLauncher.this.mClient.redraw(NexusLauncher.this.mUiInformation);
        }
    }

    public NexusLauncher(NexusLauncherActivity nexusLauncherActivity) {
        this.mLauncher = nexusLauncherActivity;
        this.mExterns = nexusLauncherActivity;
        this.mCallbacks = new NexusLauncherCallbacks();
        this.mExterns.setLauncherCallbacks(this.mCallbacks);
    }

    public static int primaryColor(WallpaperColorInfo wallpaperColorInfo, Context context, int i) {
        return compositeAllApps(ColorUtils.setAlphaComponent(wallpaperColorInfo.getMainColor(), i), context);
    }

    public static int secondaryColor(WallpaperColorInfo wallpaperColorInfo, Context context, int i) {
        return compositeAllApps(ColorUtils.setAlphaComponent(wallpaperColorInfo.getSecondaryColor(), i), context);
    }

    private static int compositeAllApps(int i, Context context) {
        return ColorUtils.compositeColors(Themes.getAttrColor(context, C0622R.attr.allAppsScrimColor), i);
    }
}
