package com.android.launcher3.discovery;

import android.content.Intent;
import android.graphics.Bitmap;

public class AppDiscoveryItem {
    public final Bitmap bitmap;
    public final Intent installIntent;
    public final boolean isInstantApp;
    public final boolean isRecent;
    public final Intent launchIntent;
    public final String packageName;
    public final String price;
    public final String publisher;
    public final long reviewCount;
    public final float starRating;
    public final CharSequence title;

    public AppDiscoveryItem(String str, boolean z, boolean z2, float f, long j, CharSequence charSequence, String str2, Bitmap bitmap2, String str3, Intent intent, Intent intent2) {
        this.packageName = str;
        this.isInstantApp = z;
        this.isRecent = z2;
        this.starRating = f;
        this.reviewCount = j;
        this.launchIntent = intent;
        this.installIntent = intent2;
        this.title = charSequence;
        this.publisher = str2;
        this.price = str3;
        this.bitmap = bitmap2;
    }
}
