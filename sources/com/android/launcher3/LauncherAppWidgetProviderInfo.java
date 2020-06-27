package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Process;
import android.os.UserHandle;

public class LauncherAppWidgetProviderInfo extends AppWidgetProviderInfo {
    public boolean isCustomWidget;
    public int minSpanX;
    public int minSpanY;
    public int spanX;
    public int spanY;

    public static LauncherAppWidgetProviderInfo fromProviderInfo(Context context, AppWidgetProviderInfo appWidgetProviderInfo) {
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo;
        if (appWidgetProviderInfo instanceof LauncherAppWidgetProviderInfo) {
            launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) appWidgetProviderInfo;
        } else {
            Parcel obtain = Parcel.obtain();
            appWidgetProviderInfo.writeToParcel(obtain, 0);
            obtain.setDataPosition(0);
            launcherAppWidgetProviderInfo = new LauncherAppWidgetProviderInfo(obtain);
            obtain.recycle();
        }
        launcherAppWidgetProviderInfo.initSpans(context);
        return launcherAppWidgetProviderInfo;
    }

    private LauncherAppWidgetProviderInfo(Parcel parcel) {
        super(parcel);
        this.isCustomWidget = false;
    }

    public LauncherAppWidgetProviderInfo(Context context, CustomAppWidget customAppWidget) {
        this.isCustomWidget = false;
        this.isCustomWidget = true;
        this.provider = new ComponentName(context, customAppWidget.getClass().getName());
        this.icon = customAppWidget.getIcon();
        this.label = customAppWidget.getLabel();
        this.previewImage = customAppWidget.getPreviewImage();
        this.initialLayout = customAppWidget.getWidgetLayout();
        this.resizeMode = customAppWidget.getResizeMode();
        initSpans(context);
    }

    public void initSpans(Context context) {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        Point totalWorkspacePadding = idp.landscapeProfile.getTotalWorkspacePadding();
        Point totalWorkspacePadding2 = idp.portraitProfile.getTotalWorkspacePadding();
        float calculateCellWidth = (float) DeviceProfile.calculateCellWidth(Math.min(idp.landscapeProfile.widthPx - totalWorkspacePadding.x, idp.portraitProfile.widthPx - totalWorkspacePadding2.x), idp.numColumns);
        float calculateCellWidth2 = (float) DeviceProfile.calculateCellWidth(Math.min(idp.landscapeProfile.heightPx - totalWorkspacePadding.y, idp.portraitProfile.heightPx - totalWorkspacePadding2.y), idp.numRows);
        Rect defaultPaddingForWidget = AppWidgetHostView.getDefaultPaddingForWidget(context, this.provider, null);
        this.spanX = Math.max(1, (int) Math.ceil((double) (((float) ((this.minWidth + defaultPaddingForWidget.left) + defaultPaddingForWidget.right)) / calculateCellWidth)));
        this.spanY = Math.max(1, (int) Math.ceil((double) (((float) ((this.minHeight + defaultPaddingForWidget.top) + defaultPaddingForWidget.bottom)) / calculateCellWidth2)));
        this.minSpanX = Math.max(1, (int) Math.ceil((double) (((float) ((this.minResizeWidth + defaultPaddingForWidget.left) + defaultPaddingForWidget.right)) / calculateCellWidth)));
        this.minSpanY = Math.max(1, (int) Math.ceil((double) (((float) ((this.minResizeHeight + defaultPaddingForWidget.top) + defaultPaddingForWidget.bottom)) / calculateCellWidth2)));
    }

    public String getLabel(PackageManager packageManager) {
        if (this.isCustomWidget) {
            return Utilities.trim(this.label);
        }
        return super.loadLabel(packageManager);
    }

    public Drawable getIcon(Context context, IconCache iconCache) {
        if (this.isCustomWidget) {
            return iconCache.getFullResIcon(this.provider.getPackageName(), this.icon);
        }
        return super.loadIcon(context, LauncherAppState.getIDP(context).fillResIconDpi);
    }

    public String toString(PackageManager packageManager) {
        if (this.isCustomWidget) {
            StringBuilder sb = new StringBuilder();
            sb.append("WidgetProviderInfo(");
            sb.append(this.provider);
            sb.append(")");
            return sb.toString();
        }
        return String.format("WidgetProviderInfo provider:%s package:%s short:%s label:%s", new Object[]{this.provider.toString(), this.provider.getPackageName(), this.provider.getShortClassName(), getLabel(packageManager)});
    }

    public Point getMinSpans(InvariantDeviceProfile invariantDeviceProfile, Context context) {
        int i = -1;
        int i2 = (this.resizeMode & 1) != 0 ? this.minSpanX : -1;
        if ((this.resizeMode & 2) != 0) {
            i = this.minSpanY;
        }
        return new Point(i2, i);
    }

    public UserHandle getUser() {
        return this.isCustomWidget ? Process.myUserHandle() : getProfile();
    }
}
