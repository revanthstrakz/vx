package com.android.launcher3.discovery;

import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.launcher3.AppInfo;
import com.android.launcher3.ShortcutInfo;

public class AppDiscoveryAppInfo extends AppInfo {
    @NonNull
    public final Intent installIntent;
    public final boolean isInstantApp;
    public final boolean isRecent;
    @NonNull
    public final Intent launchIntent;
    @Nullable
    public final String priceFormatted;
    @NonNull
    public final String publisher;
    public final float rating;
    public final long reviewCount;
    public final boolean showAsDiscoveryItem;

    public AppDiscoveryAppInfo(AppDiscoveryItem appDiscoveryItem) {
        this.intent = appDiscoveryItem.isInstantApp ? appDiscoveryItem.launchIntent : appDiscoveryItem.installIntent;
        this.title = appDiscoveryItem.title;
        this.iconBitmap = appDiscoveryItem.bitmap;
        this.isDisabled = 0;
        this.usingLowResIcon = false;
        this.isInstantApp = appDiscoveryItem.isInstantApp;
        this.isRecent = appDiscoveryItem.isRecent;
        this.rating = appDiscoveryItem.starRating;
        this.showAsDiscoveryItem = true;
        this.publisher = appDiscoveryItem.publisher != null ? appDiscoveryItem.publisher : "";
        this.priceFormatted = appDiscoveryItem.price;
        this.componentName = new ComponentName(appDiscoveryItem.packageName, "");
        this.installIntent = appDiscoveryItem.installIntent;
        this.launchIntent = appDiscoveryItem.launchIntent;
        this.reviewCount = appDiscoveryItem.reviewCount;
        this.itemType = 1;
    }

    public ShortcutInfo makeShortcut() {
        if (isDragAndDropSupported()) {
            return super.makeShortcut();
        }
        throw new RuntimeException("DnD is currently not supported for discovered store apps");
    }

    public boolean isDragAndDropSupported() {
        return this.isInstantApp;
    }
}
