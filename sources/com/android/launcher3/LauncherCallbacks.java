package com.android.launcher3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import com.android.launcher3.util.ComponentKeyMapper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public interface LauncherCallbacks {
    public static final int SEARCH_BAR_HEIGHT_NORMAL = 0;
    public static final int SEARCH_BAR_HEIGHT_TALL = 1;

    void bindAllApplications(ArrayList<AppInfo> arrayList);

    void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    void finishBindingItems(boolean z);

    Bundle getAdditionalSearchWidgetOptions();

    List<ComponentKeyMapper<AppInfo>> getPredictedApps();

    View getQsbBar();

    int getSearchBarHeight();

    boolean handleBackPressed();

    boolean hasCustomContentToLeft();

    boolean hasSettings();

    void onActivityResult(int i, int i2, Intent intent);

    void onAttachedToWindow();

    void onCreate(Bundle bundle);

    void onDestroy();

    void onDetachedFromWindow();

    void onHomeIntent();

    void onInteractionBegin();

    void onInteractionEnd();

    void onLauncherProviderChange();

    void onNewIntent(Intent intent);

    void onPause();

    void onPostCreate(Bundle bundle);

    boolean onPrepareOptionsMenu(Menu menu);

    void onRequestPermissionsResult(int i, String[] strArr, int[] iArr);

    void onResume();

    void onSaveInstanceState(Bundle bundle);

    void onStart();

    void onStop();

    void onTrimMemory(int i);

    void onWindowFocusChanged(boolean z);

    @Deprecated
    void onWorkspaceLockedChanged();

    void populateCustomContentContainer();

    void preOnCreate();

    void preOnResume();

    boolean shouldMoveToDefaultScreenOnHomeIntent();

    boolean startSearch(String str, boolean z, Bundle bundle);
}
