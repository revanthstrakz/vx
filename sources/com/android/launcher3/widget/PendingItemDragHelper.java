package com.android.launcher3.widget;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.C0622R;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.LivePreviewWidgetCell;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.graphics.HolographicOutlineHelper;
import com.android.launcher3.graphics.LauncherIcons;

public class PendingItemDragHelper extends DragPreviewProvider {
    private static final float MAX_WIDGET_SCALE = 1.25f;
    private final PendingAddItemInfo mAddInfo;
    private RemoteViews mPreview;
    private Bitmap mPreviewBitmap;

    public PendingItemDragHelper(View view) {
        super(view);
        this.mAddInfo = (PendingAddItemInfo) view.getTag();
    }

    public void setPreview(RemoteViews remoteViews) {
        this.mPreview = remoteViews;
    }

    public void startDrag(Rect rect, int i, int i2, Point point, DragSource dragSource, DragOptions dragOptions) {
        float f;
        Rect rect2;
        Point point2;
        Bitmap bitmap;
        Rect rect3 = rect;
        int i3 = i;
        int i4 = i2;
        Point point3 = point;
        Launcher launcher = Launcher.getLauncher(this.mView.getContext());
        LauncherAppState instance = LauncherAppState.getInstance(launcher);
        if (this.mAddInfo instanceof PendingAddWidgetInfo) {
            PendingAddWidgetInfo pendingAddWidgetInfo = (PendingAddWidgetInfo) this.mAddInfo;
            int min = Math.min((int) (((float) i3) * MAX_WIDGET_SCALE), launcher.getWorkspace().estimateItemSize(pendingAddWidgetInfo, true, false)[0]);
            int[] iArr = new int[1];
            Bitmap generateFromRemoteViews = this.mPreview != null ? LivePreviewWidgetCell.generateFromRemoteViews(launcher, this.mPreview, pendingAddWidgetInfo.info, min, iArr) : null;
            if (generateFromRemoteViews == null) {
                generateFromRemoteViews = instance.getWidgetCache().generateWidgetPreview(launcher, pendingAddWidgetInfo.info, min, null, iArr);
            }
            if (iArr[0] < i3) {
                int i5 = (i3 - iArr[0]) / 2;
                if (i3 > i4) {
                    i5 = (i5 * i4) / i3;
                }
                rect3.left += i5;
                rect3.right -= i5;
            }
            float width = ((float) rect.width()) / ((float) generateFromRemoteViews.getWidth());
            launcher.getDragController().addDragListener(new WidgetHostViewLoader(launcher, this.mView));
            f = width;
            bitmap = generateFromRemoteViews;
            point2 = null;
            rect2 = null;
        } else {
            Bitmap createScaledBitmapWithoutShadow = LauncherIcons.createScaledBitmapWithoutShadow(((PendingAddShortcutInfo) this.mAddInfo).activityInfo.getFullResIcon(instance.getIconCache()), launcher, 0);
            PendingAddItemInfo pendingAddItemInfo = this.mAddInfo;
            this.mAddInfo.spanY = 1;
            pendingAddItemInfo.spanX = 1;
            float width2 = ((float) launcher.getDeviceProfile().iconSizePx) / ((float) createScaledBitmapWithoutShadow.getWidth());
            Point point4 = new Point(this.previewPadding / 2, this.previewPadding / 2);
            int[] estimateItemSize = launcher.getWorkspace().estimateItemSize(this.mAddInfo, false, true);
            DeviceProfile deviceProfile = launcher.getDeviceProfile();
            int i6 = deviceProfile.iconSizePx;
            int dimensionPixelSize = launcher.getResources().getDimensionPixelSize(C0622R.dimen.widget_preview_shortcut_padding);
            rect3.left += dimensionPixelSize;
            rect3.top += dimensionPixelSize;
            Rect rect4 = new Rect();
            rect4.left = (estimateItemSize[0] - i6) / 2;
            rect4.right = rect4.left + i6;
            rect4.top = (((estimateItemSize[1] - i6) - deviceProfile.iconTextSizePx) - deviceProfile.iconDrawablePaddingPx) / 2;
            rect4.bottom = rect4.top + i6;
            bitmap = createScaledBitmapWithoutShadow;
            f = width2;
            rect2 = rect4;
            point2 = point4;
        }
        launcher.getWorkspace().prepareDragWithProvider(this);
        int width3 = point3.x + rect3.left + ((int) (((((float) bitmap.getWidth()) * f) - ((float) bitmap.getWidth())) / 2.0f));
        int height = point3.y + rect3.top + ((int) (((((float) bitmap.getHeight()) * f) - ((float) bitmap.getHeight())) / 2.0f));
        this.mPreviewBitmap = bitmap;
        launcher.getDragController().startDrag(bitmap, width3, height, dragSource, this.mAddInfo, point2, rect2, f, dragOptions);
    }

    public Bitmap createDragOutline(Canvas canvas) {
        if (this.mAddInfo instanceof PendingAddShortcutInfo) {
            Bitmap createBitmap = Bitmap.createBitmap(this.mPreviewBitmap.getWidth() + this.blurSizeOutline, this.mPreviewBitmap.getHeight() + this.blurSizeOutline, Config.ALPHA_8);
            canvas.setBitmap(createBitmap);
            int i = Launcher.getLauncher(this.mView.getContext()).getDeviceProfile().iconSizePx;
            Rect rect = new Rect(0, 0, this.mPreviewBitmap.getWidth(), this.mPreviewBitmap.getHeight());
            Rect rect2 = new Rect(0, 0, i, i);
            rect2.offset(this.blurSizeOutline / 2, this.blurSizeOutline / 2);
            canvas.drawBitmap(this.mPreviewBitmap, rect, rect2, new Paint(2));
            HolographicOutlineHelper.getInstance(this.mView.getContext()).applyExpensiveOutlineWithBlur(createBitmap, canvas);
            canvas.setBitmap(null);
            return createBitmap;
        }
        int[] estimateItemSize = Launcher.getLauncher(this.mView.getContext()).getWorkspace().estimateItemSize(this.mAddInfo, false, false);
        int i2 = estimateItemSize[0];
        int i3 = estimateItemSize[1];
        Bitmap createBitmap2 = Bitmap.createBitmap(i2, i3, Config.ALPHA_8);
        canvas.setBitmap(createBitmap2);
        Rect rect3 = new Rect(0, 0, this.mPreviewBitmap.getWidth(), this.mPreviewBitmap.getHeight());
        float min = Math.min(((float) (i2 - this.blurSizeOutline)) / ((float) this.mPreviewBitmap.getWidth()), ((float) (i3 - this.blurSizeOutline)) / ((float) this.mPreviewBitmap.getHeight()));
        int width = (int) (((float) this.mPreviewBitmap.getWidth()) * min);
        int height = (int) (min * ((float) this.mPreviewBitmap.getHeight()));
        Rect rect4 = new Rect(0, 0, width, height);
        rect4.offset((i2 - width) / 2, (i3 - height) / 2);
        canvas.drawBitmap(this.mPreviewBitmap, rect3, rect4, null);
        HolographicOutlineHelper.getInstance(this.mView.getContext()).applyExpensiveOutlineWithBlur(createBitmap2, canvas);
        canvas.setBitmap(null);
        return createBitmap2;
    }
}
