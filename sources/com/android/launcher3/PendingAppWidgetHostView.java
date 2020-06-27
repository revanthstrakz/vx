package com.android.launcher3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.launcher3.IconCache.ItemInfoUpdateReceiver;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.util.Themes;

public class PendingAppWidgetHostView extends LauncherAppWidgetHostView implements OnClickListener, ItemInfoUpdateReceiver {
    private static final float MIN_SATUNATION = 0.7f;
    private static final float SETUP_ICON_SIZE_FACTOR = 0.4f;
    private Drawable mCenterDrawable;
    private OnClickListener mClickListener;
    private View mDefaultView;
    private final boolean mDisabledForSafeMode;
    private boolean mDrawableSizeChanged;
    private Bitmap mIcon;
    private final LauncherAppWidgetInfo mInfo;
    private Launcher mLauncher;
    private final TextPaint mPaint;
    private final Rect mRect = new Rect();
    private Drawable mSettingIconDrawable;
    private Layout mSetupTextLayout;
    private final int mStartState;

    public void updateAppWidgetSize(Bundle bundle, int i, int i2, int i3, int i4) {
    }

    public PendingAppWidgetHostView(Context context, LauncherAppWidgetInfo launcherAppWidgetInfo, IconCache iconCache, boolean z) {
        super(new ContextThemeWrapper(context, C0622R.style.WidgetContainerTheme));
        this.mLauncher = Launcher.getLauncher(context);
        this.mInfo = launcherAppWidgetInfo;
        this.mStartState = launcherAppWidgetInfo.restoreStatus;
        this.mDisabledForSafeMode = z;
        this.mPaint = new TextPaint();
        this.mPaint.setColor(Themes.getAttrColor(getContext(), 16842806));
        this.mPaint.setTextSize(TypedValue.applyDimension(0, (float) this.mLauncher.getDeviceProfile().iconTextSizePx, getResources().getDisplayMetrics()));
        setBackgroundResource(C0622R.C0624drawable.pending_widget_bg);
        setWillNotDraw(false);
        setElevation(getResources().getDimension(C0622R.dimen.pending_widget_elevation));
        updateAppWidget(null);
        setOnClickListener(this.mLauncher);
        if (launcherAppWidgetInfo.pendingItemInfo == null) {
            launcherAppWidgetInfo.pendingItemInfo = new PackageItemInfo(launcherAppWidgetInfo.providerName.getPackageName());
            launcherAppWidgetInfo.pendingItemInfo.user = launcherAppWidgetInfo.user;
            iconCache.updateIconInBackground(this, launcherAppWidgetInfo.pendingItemInfo);
            return;
        }
        reapplyItemInfo(launcherAppWidgetInfo.pendingItemInfo);
    }

    /* access modifiers changed from: protected */
    public View getDefaultView() {
        if (this.mDefaultView == null) {
            this.mDefaultView = this.mInflater.inflate(C0622R.layout.appwidget_not_ready, this, false);
            this.mDefaultView.setOnClickListener(this);
            applyState();
        }
        return this.mDefaultView;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mClickListener = onClickListener;
    }

    public boolean isReinflateRequired(int i) {
        return this.mStartState != this.mInfo.restoreStatus;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mDrawableSizeChanged = true;
    }

    public void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon) {
        Bitmap bitmap = itemInfoWithIcon.iconBitmap;
        if (this.mIcon != bitmap) {
            this.mIcon = bitmap;
            if (this.mCenterDrawable != null) {
                this.mCenterDrawable.setCallback(null);
                this.mCenterDrawable = null;
            }
            if (this.mIcon != null) {
                DrawableFactory drawableFactory = DrawableFactory.get(getContext());
                if (this.mDisabledForSafeMode) {
                    FastBitmapDrawable newIcon = drawableFactory.newIcon(this.mIcon, this.mInfo);
                    newIcon.setIsDisabled(true);
                    this.mCenterDrawable = newIcon;
                    this.mSettingIconDrawable = null;
                } else if (isReadyForClickSetup()) {
                    this.mCenterDrawable = drawableFactory.newIcon(this.mIcon, this.mInfo);
                    this.mSettingIconDrawable = getResources().getDrawable(C0622R.C0624drawable.ic_setting).mutate();
                    updateSettingColor();
                } else {
                    this.mCenterDrawable = DrawableFactory.get(getContext()).newPendingIcon(this.mIcon, getContext());
                    this.mCenterDrawable.setCallback(this);
                    this.mSettingIconDrawable = null;
                    applyState();
                }
                this.mDrawableSizeChanged = true;
            }
            invalidate();
        }
    }

    private void updateSettingColor() {
        float[] fArr = new float[3];
        Color.colorToHSV(Utilities.findDominantColorByHue(this.mIcon, 20), fArr);
        fArr[1] = Math.min(fArr[1], 0.7f);
        fArr[2] = 1.0f;
        this.mSettingIconDrawable.setColorFilter(Color.HSVToColor(fArr), Mode.SRC_IN);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mCenterDrawable || super.verifyDrawable(drawable);
    }

    public void applyState() {
        if (this.mCenterDrawable != null) {
            this.mCenterDrawable.setLevel(Math.max(this.mInfo.installProgress, 0));
        }
    }

    public void onClick(View view) {
        if (this.mClickListener != null) {
            this.mClickListener.onClick(this);
        }
    }

    public boolean isReadyForClickSetup() {
        if (this.mInfo.hasRestoreFlag(2) || (!this.mInfo.hasRestoreFlag(4) && !this.mInfo.hasRestoreFlag(1))) {
            return false;
        }
        return true;
    }

    private void updateDrawableBounds() {
        int i;
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int dimensionPixelSize = getResources().getDimensionPixelSize(C0622R.dimen.pending_widget_min_padding);
        int width = (getWidth() - paddingLeft) - paddingRight;
        int i2 = dimensionPixelSize * 2;
        int i3 = width - i2;
        int height = ((getHeight() - paddingTop) - paddingBottom) - i2;
        if (this.mSettingIconDrawable == null) {
            int min = Math.min(deviceProfile.iconSizePx, Math.min(i3, height));
            this.mRect.set(0, 0, min, min);
            this.mRect.offsetTo((getWidth() - this.mRect.width()) / 2, (getHeight() - this.mRect.height()) / 2);
            this.mCenterDrawable.setBounds(this.mRect);
            return;
        }
        float max = (float) Math.max(0, Math.min(i3, height));
        float max2 = (float) Math.max(i3, height);
        if (max * 1.8f > max2) {
            max = max2 / 1.8f;
        }
        int min2 = (int) Math.min(max, (float) deviceProfile.iconSizePx);
        int height2 = (getHeight() - min2) / 2;
        this.mSetupTextLayout = null;
        if (i3 > 0) {
            StaticLayout staticLayout = r8;
            i = paddingTop;
            StaticLayout staticLayout2 = new StaticLayout(getResources().getText(C0622R.string.gadget_setup_text), this.mPaint, i3, Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
            this.mSetupTextLayout = staticLayout;
            int height3 = this.mSetupTextLayout.getHeight();
            if (((float) height3) + (((float) min2) * 1.8f) + ((float) deviceProfile.iconDrawablePaddingPx) < ((float) height)) {
                height2 = (((getHeight() - height3) - deviceProfile.iconDrawablePaddingPx) - min2) / 2;
            } else {
                this.mSetupTextLayout = null;
            }
        } else {
            i = paddingTop;
        }
        int i4 = height2;
        this.mRect.set(0, 0, min2, min2);
        this.mRect.offset((getWidth() - min2) / 2, i4);
        this.mCenterDrawable.setBounds(this.mRect);
        int i5 = paddingLeft + dimensionPixelSize;
        this.mRect.left = i5;
        int i6 = (int) (((float) min2) * 0.4f);
        this.mRect.right = this.mRect.left + i6;
        this.mRect.top = i + dimensionPixelSize;
        this.mRect.bottom = this.mRect.top + i6;
        this.mSettingIconDrawable.setBounds(this.mRect);
        if (this.mSetupTextLayout != null) {
            this.mRect.left = i5;
            this.mRect.top = this.mCenterDrawable.getBounds().bottom + deviceProfile.iconDrawablePaddingPx;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mCenterDrawable != null) {
            if (this.mDrawableSizeChanged) {
                updateDrawableBounds();
                this.mDrawableSizeChanged = false;
            }
            this.mCenterDrawable.draw(canvas);
            if (this.mSettingIconDrawable != null) {
                this.mSettingIconDrawable.draw(canvas);
            }
            if (this.mSetupTextLayout != null) {
                canvas.save();
                canvas.translate((float) this.mRect.left, (float) this.mRect.top);
                this.mSetupTextLayout.draw(canvas);
                canvas.restore();
            }
        }
    }
}
