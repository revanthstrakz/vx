package com.android.launcher3.testing;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import com.android.launcher3.AppInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.Launcher.CustomContentCallbacks;
import com.android.launcher3.LauncherCallbacks;
import com.android.launcher3.util.ComponentKeyMapper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LauncherExtension extends Launcher {

    public class LauncherExtensionCallbacks implements LauncherCallbacks {
        CustomContentCallbacks mCustomContentCallbacks = new CustomContentCallbacks() {
            public boolean isScrollingAllowed() {
                return true;
            }

            public void onHide() {
            }

            public void onScrollProgressChanged(float f) {
            }

            public void onShow(boolean z) {
            }
        };

        public void bindAllApplications(ArrayList<AppInfo> arrayList) {
        }

        public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        }

        public void finishBindingItems(boolean z) {
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
            return true;
        }

        public boolean hasSettings() {
            return false;
        }

        public void onActivityResult(int i, int i2, Intent intent) {
        }

        public void onAttachedToWindow() {
        }

        public void onCreate(Bundle bundle) {
        }

        public void onDestroy() {
        }

        public void onDetachedFromWindow() {
        }

        public void onHomeIntent() {
        }

        public void onInteractionBegin() {
        }

        public void onInteractionEnd() {
        }

        public void onLauncherProviderChange() {
        }

        public void onNewIntent(Intent intent) {
        }

        public void onPause() {
        }

        public void onPostCreate(Bundle bundle) {
        }

        public boolean onPrepareOptionsMenu(Menu menu) {
            return false;
        }

        public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        }

        public void onResume() {
        }

        public void onSaveInstanceState(Bundle bundle) {
        }

        public void onStart() {
        }

        public void onStop() {
        }

        public void onTrimMemory(int i) {
        }

        public void onWindowFocusChanged(boolean z) {
        }

        public void onWorkspaceLockedChanged() {
        }

        public void preOnCreate() {
        }

        public void preOnResume() {
        }

        public boolean shouldMoveToDefaultScreenOnHomeIntent() {
            return true;
        }

        public boolean startSearch(String str, boolean z, Bundle bundle) {
            return false;
        }

        public LauncherExtensionCallbacks() {
        }

        public void populateCustomContentContainer() {
            FrameLayout frameLayout = new FrameLayout(LauncherExtension.this);
            frameLayout.setBackgroundColor(-7829368);
            LauncherExtension.this.addToCustomContentPage(frameLayout, this.mCustomContentCallbacks, "");
        }

        public Bundle getAdditionalSearchWidgetOptions() {
            return new Bundle();
        }

        public List<ComponentKeyMapper<AppInfo>> getPredictedApps() {
            return new ArrayList();
        }
    }

    public void onCreate(Bundle bundle) {
        setLauncherCallbacks(new LauncherExtensionCallbacks());
        super.onCreate(bundle);
    }
}
