package com.android.launcher3.util;

import android.content.Context;
import com.android.launcher3.CustomAppWidget;
import com.android.launcher3.Launcher;
import java.util.HashMap;

public class TestingUtils {
    public static final String ACTION_START_TRACKING = "com.android.launcher3.action.START_TRACKING";
    public static final String DUMMY_WIDGET = "com.android.launcher3.testing.DummyWidget";
    public static final boolean ENABLE_CUSTOM_WIDGET_TEST = false;
    public static final boolean MEMORY_DUMP_ENABLED = false;
    public static final String MEMORY_TRACKER = "com.android.launcher3.testing.MemoryTracker";
    public static final String SHOW_WEIGHT_WATCHER = "debug.show_mem";

    public static void addDummyWidget(HashMap<String, CustomAppWidget> hashMap) {
    }

    public static void addWeightWatcher(Launcher launcher) {
    }

    public static void startTrackingMemory(Context context) {
    }
}
