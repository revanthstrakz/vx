package com.google.android.apps.nexuslauncher;

import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import com.android.launcher3.AppFilter;
import java.util.HashSet;

public class NexusAppFilter extends AppFilter {
    private final HashSet<ComponentName> mHideList = new HashSet<>();

    public NexusAppFilter(Context context) {
        this.mHideList.add(ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/.VoiceSearchActivity"));
        this.mHideList.add(ComponentName.unflattenFromString("com.google.android.apps.wallpaper/.picker.CategoryPickerActivity"));
        this.mHideList.add(ComponentName.unflattenFromString("com.google.android.launcher/.StubApp"));
    }

    public boolean shouldShowApp(ComponentName componentName, UserHandle userHandle) {
        return !this.mHideList.contains(componentName);
    }
}
