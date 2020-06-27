package com.google.android.apps.nexuslauncher.qsb;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.os.Bundle;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.util.ComponentKey;
import com.google.android.apps.nexuslauncher.NexusLauncherActivity;
import com.google.android.apps.nexuslauncher.search.AppSearchProvider;
import java.lang.ref.WeakReference;

public class LongClickReceiver extends BroadcastReceiver {

    /* renamed from: bR */
    private static WeakReference<NexusLauncherActivity> f88bR = new WeakReference<>(null);

    /* renamed from: bq */
    public static void m24bq(NexusLauncherActivity nexusLauncherActivity) {
        f88bR = new WeakReference<>(nexusLauncherActivity);
    }

    public void onReceive(Context context, Intent intent) {
        NexusLauncherActivity nexusLauncherActivity = (NexusLauncherActivity) f88bR.get();
        if (nexusLauncherActivity != null) {
            ComponentKey uriToComponent = AppSearchProvider.uriToComponent(intent.getData(), context);
            LauncherActivityInfo resolveActivity = LauncherAppsCompat.getInstance(context).resolveActivity(new Intent("android.intent.action.MAIN").setComponent(uriToComponent.componentName), uriToComponent.user);
            if (resolveActivity != null) {
                ItemDragListener itemDragListener = new ItemDragListener(resolveActivity, intent.getSourceBounds());
                itemDragListener.setLauncher(nexusLauncherActivity);
                nexusLauncherActivity.showWorkspace(false);
                nexusLauncherActivity.getDragLayer().setOnDragListener(itemDragListener);
                ClipData clipData = new ClipData(new ClipDescription("", new String[]{itemDragListener.getMimeType()}), new Item(""));
                Bundle bundle = new Bundle();
                bundle.putParcelable("clip_data", clipData);
                setResult(-1, null, bundle);
            }
        }
    }
}
