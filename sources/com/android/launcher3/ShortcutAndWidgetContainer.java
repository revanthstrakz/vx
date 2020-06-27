package com.android.launcher3;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout.LayoutParams;

public class ShortcutAndWidgetContainer extends ViewGroup {
    static final String TAG = "ShortcutAndWidgetContainer";
    private int mCellHeight;
    private int mCellWidth;
    private final int mContainerType;
    private int mCountX;
    private boolean mInvertIfRtl = false;
    private Launcher mLauncher;
    private final int[] mTmpCellXY = new int[2];
    private final WallpaperManager mWallpaperManager;

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public ShortcutAndWidgetContainer(Context context, int i) {
        super(context);
        this.mLauncher = Launcher.getLauncher(context);
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        this.mContainerType = i;
    }

    public void setCellDimensions(int i, int i2, int i3, int i4) {
        this.mCellWidth = i;
        this.mCellHeight = i2;
        this.mCountX = i3;
    }

    public View getChildAt(int i, int i2) {
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams.cellX <= i && i < layoutParams.cellX + layoutParams.cellHSpan && layoutParams.cellY <= i2 && i2 < layoutParams.cellY + layoutParams.cellVSpan) {
                return childAt;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                measureChild(childAt);
            }
        }
    }

    public void setupLp(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (view instanceof LauncherAppWidgetHostView) {
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            layoutParams.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y);
            return;
        }
        layoutParams.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX);
    }

    public void setInvertIfRtl(boolean z) {
        this.mInvertIfRtl = z;
    }

    public int getCellContentHeight() {
        return Math.min(getMeasuredHeight(), this.mLauncher.getDeviceProfile().getCellHeight(this.mContainerType));
    }

    public void measureChild(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (!layoutParams.isFullscreen) {
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            if (view instanceof LauncherAppWidgetHostView) {
                layoutParams.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y);
            } else {
                layoutParams.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX);
                int max = (int) Math.max(0.0f, ((float) (layoutParams.height - getCellContentHeight())) / 2.0f);
                int i = this.mContainerType == 0 ? deviceProfile.workspaceCellPaddingXPx : (int) (((float) deviceProfile.edgeMarginPx) / 2.0f);
                view.setPadding(i, max, i, 0);
            }
        } else {
            layoutParams.f46x = 0;
            layoutParams.f47y = 0;
            layoutParams.width = getMeasuredWidth();
            layoutParams.height = getMeasuredHeight();
        }
        view.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824), MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824));
    }

    public boolean invertLayoutHorizontally() {
        return this.mInvertIfRtl && Utilities.isRtl(getResources());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (childAt instanceof LauncherAppWidgetHostView) {
                    LauncherAppWidgetHostView launcherAppWidgetHostView = (LauncherAppWidgetHostView) childAt;
                    DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
                    float f = deviceProfile.appWidgetScale.x;
                    float f2 = deviceProfile.appWidgetScale.y;
                    launcherAppWidgetHostView.setScaleToFit(Math.min(f, f2));
                    launcherAppWidgetHostView.setTranslationForCentering((-(((float) layoutParams.width) - (((float) layoutParams.width) * f))) / 2.0f, (-(((float) layoutParams.height) - (((float) layoutParams.height) * f2))) / 2.0f);
                }
                int i6 = layoutParams.f46x;
                int i7 = layoutParams.f47y;
                childAt.layout(i6, i7, layoutParams.width + i6, layoutParams.height + i7);
                if (layoutParams.dropped) {
                    layoutParams.dropped = false;
                    int[] iArr = this.mTmpCellXY;
                    getLocationOnScreen(iArr);
                    this.mWallpaperManager.sendWallpaperCommand(getWindowToken(), "android.home.drop", iArr[0] + i6 + (layoutParams.width / 2), iArr[1] + i7 + (layoutParams.height / 2), 0, null);
                }
            }
        }
    }

    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        if (view != null) {
            Rect rect = new Rect();
            view.getDrawingRect(rect);
            requestRectangleOnScreen(rect);
        }
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).cancelLongPress();
        }
    }
}
