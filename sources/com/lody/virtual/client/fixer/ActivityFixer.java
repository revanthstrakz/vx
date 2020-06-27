package com.lody.virtual.client.fixer;

import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import mirror.com.android.internal.R_Hide.styleable;

public final class ActivityFixer {
    private ActivityFixer() {
    }

    public static void fixActivity(Activity activity) {
        Context baseContext = activity.getBaseContext();
        try {
            TypedArray obtainStyledAttributes = activity.obtainStyledAttributes((int[]) styleable.Window.get());
            if (obtainStyledAttributes != null) {
                if (obtainStyledAttributes.getBoolean(styleable.Window_windowShowWallpaper.get(), false)) {
                    activity.getWindow().setBackgroundDrawable(WallpaperManager.getInstance(activity).getDrawable());
                }
                obtainStyledAttributes.recycle();
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (VERSION.SDK_INT >= 21) {
            Intent intent = activity.getIntent();
            ApplicationInfo applicationInfo = baseContext.getApplicationInfo();
            PackageManager packageManager = activity.getPackageManager();
            if (intent != null && activity.isTaskRoot()) {
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append(applicationInfo.loadLabel(packageManager));
                    sb.append("");
                    String sb2 = sb.toString();
                    Bitmap bitmap = null;
                    Drawable loadIcon = applicationInfo.loadIcon(packageManager);
                    if (loadIcon instanceof BitmapDrawable) {
                        bitmap = ((BitmapDrawable) loadIcon).getBitmap();
                    }
                    activity.setTaskDescription(new TaskDescription(sb2, bitmap));
                } catch (Throwable th2) {
                    th2.printStackTrace();
                }
            }
        }
    }
}
