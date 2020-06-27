package com.android.launcher3;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.p001v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewDebug.ExportedProperty;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.launcher3.CellLayout.LayoutParams;
import com.android.launcher3.dynamicui.ExtractedColors;
import com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.util.Themes;

public class Hotseat extends FrameLayout implements LogContainerProvider {
    private static final String HIDE_SETTINGS_KEY = "advance_settings_hide_settings";
    /* access modifiers changed from: private */
    @ExportedProperty(category = "launcher")
    public ColorDrawable mBackground;
    @ExportedProperty(category = "launcher")
    private int mBackgroundColor;
    /* access modifiers changed from: private */
    public ValueAnimator mBackgroundColorAnimator;
    private CellLayout mContent;
    @ExportedProperty(category = "launcher")
    private final boolean mHasVerticalHotseat;
    private Launcher mLauncher;
    private boolean mShowSettingsButton;

    public void updateColor(ExtractedColors extractedColors, boolean z) {
    }

    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Hotseat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowSettingsButton = true;
        SharedPreferences sharedPreferences = context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, 0);
        this.mLauncher = Launcher.getLauncher(context);
        this.mHasVerticalHotseat = this.mLauncher.getDeviceProfile().isVerticalBarLayout();
        this.mBackgroundColor = ColorUtils.setAlphaComponent(Themes.getAttrColor(context, 16843827), 0);
        this.mBackground = new ColorDrawable(this.mBackgroundColor);
        this.mShowSettingsButton = !sharedPreferences.getBoolean(HIDE_SETTINGS_KEY, false);
    }

    public CellLayout getLayout() {
        return this.mContent;
    }

    public boolean hasIcons() {
        return this.mContent.getShortcutsAndWidgets().getChildCount() > 1;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.mContent.setOnLongClickListener(onLongClickListener);
    }

    /* access modifiers changed from: 0000 */
    public int getOrderInHotseat(int i, int i2) {
        return this.mHasVerticalHotseat ? (this.mContent.getCountY() - i2) - 1 : i;
    }

    /* access modifiers changed from: 0000 */
    public int getCellXFromOrder(int i) {
        if (this.mHasVerticalHotseat) {
            return 0;
        }
        return i;
    }

    /* access modifiers changed from: 0000 */
    public int getCellYFromOrder(int i) {
        if (this.mHasVerticalHotseat) {
            return this.mContent.getCountY() - (i + 1);
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mContent = (CellLayout) findViewById(C0622R.C0625id.layout);
        if (deviceProfile.isVerticalBarLayout()) {
            this.mContent.setGridSize(1, deviceProfile.inv.numHotseatIcons);
        } else {
            this.mContent.setGridSize(deviceProfile.inv.numHotseatIcons, 1);
        }
        resetLayout();
    }

    /* access modifiers changed from: 0000 */
    public void resetLayout() {
        this.mContent.removeAllViewsInLayout();
        if (this.mShowSettingsButton) {
            Context context = getContext();
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            int allAppsButtonRank = deviceProfile.inv.getAllAppsButtonRank();
            TextView textView = (TextView) LayoutInflater.from(context).inflate(C0622R.layout.all_apps_button, this.mContent, false);
            Drawable drawable = context.getResources().getDrawable(C0622R.C0624drawable.all_apps_button_icon);
            drawable.setBounds(0, 0, deviceProfile.iconSizePx, deviceProfile.iconSizePx);
            int dimensionPixelSize = getResources().getDimensionPixelSize(C0622R.dimen.all_apps_button_scale_down);
            Rect bounds = drawable.getBounds();
            int i = dimensionPixelSize / 2;
            drawable.setBounds(bounds.left, bounds.top + i, bounds.right - dimensionPixelSize, bounds.bottom - i);
            textView.setCompoundDrawables(null, drawable, null, null);
            textView.setContentDescription(context.getString(C0622R.string.settings_button_text));
            textView.setOnKeyListener(new HotseatIconKeyEventListener());
            if (this.mLauncher != null) {
                this.mLauncher.setAllAppsButton(textView);
                textView.setOnClickListener(this.mLauncher);
                textView.setOnLongClickListener(this.mLauncher);
                textView.setOnFocusChangeListener(this.mLauncher.mFocusHandler);
            }
            LayoutParams layoutParams = new LayoutParams(getCellXFromOrder(allAppsButtonRank), getCellYFromOrder(allAppsButtonRank), 1, 1);
            layoutParams.canReorder = false;
            this.mContent.addViewToCellLayout(textView, -1, textView.getId(), layoutParams, true);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return !this.mLauncher.getWorkspace().workspaceIconsCanBeDragged() && !this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag();
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        target.gridX = itemInfo.cellX;
        target.gridY = itemInfo.cellY;
        target2.containerType = 2;
    }

    public void setBackgroundTransparent(boolean z) {
        if (z) {
            this.mBackground.setAlpha(0);
        } else {
            this.mBackground.setAlpha(255);
        }
    }

    public int getBackgroundDrawableColor() {
        return this.mBackgroundColor;
    }
}
