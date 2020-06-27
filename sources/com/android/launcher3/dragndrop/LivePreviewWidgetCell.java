package com.android.launcher3.dragndrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.WidgetCell;

public class LivePreviewWidgetCell extends WidgetCell {
    private RemoteViews mPreview;

    public LivePreviewWidgetCell(Context context) {
        this(context, null);
    }

    public LivePreviewWidgetCell(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LivePreviewWidgetCell(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setPreview(RemoteViews remoteViews) {
        this.mPreview = remoteViews;
    }

    public void ensurePreview() {
        if (this.mPreview != null && this.mActiveRequest == null) {
            Bitmap generateFromRemoteViews = generateFromRemoteViews(this.mActivity, this.mPreview, this.mItem.widgetInfo, this.mPresetPreviewSize, new int[1]);
            if (generateFromRemoteViews != null) {
                applyPreview(generateFromRemoteViews);
                return;
            }
        }
        super.ensurePreview();
    }

    public static Bitmap generateFromRemoteViews(BaseActivity baseActivity, RemoteViews remoteViews, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, int i, int[] iArr) {
        float f;
        DeviceProfile deviceProfile = baseActivity.getDeviceProfile();
        int i2 = deviceProfile.cellWidthPx * launcherAppWidgetProviderInfo.spanX;
        int i3 = deviceProfile.cellHeightPx * launcherAppWidgetProviderInfo.spanY;
        try {
            View apply = remoteViews.apply(baseActivity, new FrameLayout(baseActivity));
            apply.measure(MeasureSpec.makeMeasureSpec(i2, 1073741824), MeasureSpec.makeMeasureSpec(i3, 1073741824));
            int measuredWidth = apply.getMeasuredWidth();
            int measuredHeight = apply.getMeasuredHeight();
            apply.layout(0, 0, measuredWidth, measuredHeight);
            iArr[0] = measuredWidth;
            if (measuredWidth > i) {
                f = ((float) i) / ((float) measuredWidth);
                measuredHeight = (int) (((float) measuredHeight) * f);
                measuredWidth = i;
            } else {
                f = 1.0f;
            }
            Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.scale(f, f);
            apply.draw(canvas);
            canvas.setBitmap(null);
            return createBitmap;
        } catch (Exception unused) {
            return null;
        }
    }
}
