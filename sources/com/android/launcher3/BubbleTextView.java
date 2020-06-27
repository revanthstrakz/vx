package com.android.launcher3;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.p001v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewParent;
import android.widget.TextView;
import com.android.launcher3.IconCache.IconLoadRequest;
import com.android.launcher3.IconCache.ItemInfoUpdateReceiver;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.badge.BadgeRenderer;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.FolderIconPreviewVerifier;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.graphics.HolographicOutlineHelper;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.graphics.PreloadIconDrawable;
import com.android.launcher3.model.PackageItemInfo;
import java.text.NumberFormat;

public class BubbleTextView extends TextView implements ItemInfoUpdateReceiver {
    private static final Property<BubbleTextView, Float> BADGE_SCALE_PROPERTY = new Property<BubbleTextView, Float>(Float.TYPE, "badgeScale") {
        public Float get(BubbleTextView bubbleTextView) {
            return Float.valueOf(bubbleTextView.mBadgeScale);
        }

        public void set(BubbleTextView bubbleTextView, Float f) {
            bubbleTextView.mBadgeScale = f.floatValue();
            bubbleTextView.invalidate();
        }
    };
    private static final int DISPLAY_ALL_APPS = 1;
    private static final int DISPLAY_FOLDER = 2;
    private static final int DISPLAY_WORKSPACE = 0;
    private static final int[] STATE_PRESSED = {16842919};
    public static final Property<BubbleTextView, Integer> TEXT_ALPHA_PROPERTY = new Property<BubbleTextView, Integer>(Integer.class, "textAlpha") {
        public Integer get(BubbleTextView bubbleTextView) {
            return Integer.valueOf(bubbleTextView.getTextAlpha());
        }

        public void set(BubbleTextView bubbleTextView, Integer num) {
            bubbleTextView.setTextAlpha(num.intValue());
        }
    };
    private BadgeInfo mBadgeInfo;
    private IconPalette mBadgePalette;
    private BadgeRenderer mBadgeRenderer;
    /* access modifiers changed from: private */
    public float mBadgeScale;
    private final boolean mCenterVertically;
    private final boolean mDeferShadowGenerationOnTouch;
    @ExportedProperty(category = "launcher")
    private boolean mDisableRelayout;
    private boolean mForceHideBadge;
    private Drawable mIcon;
    private IconLoadRequest mIconLoadRequest;
    private final int mIconSize;
    @ExportedProperty(category = "launcher")
    private boolean mIgnorePressedStateChange;
    private boolean mIsIconVisible;
    private final Launcher mLauncher;
    private final boolean mLayoutHorizontal;
    private final CheckLongPressHelper mLongPressHelper;
    private final HolographicOutlineHelper mOutlineHelper;
    private Bitmap mPressedBackground;
    private final float mSlop;
    @ExportedProperty(category = "launcher")
    private boolean mStayPressed;
    private final StylusEventHelper mStylusEventHelper;
    private Rect mTempIconBounds;
    private Point mTempSpaceForBadgeOffset;
    @ExportedProperty(category = "launcher")
    private int mTextColor;

    public interface BubbleTextShadowHandler {
        void setPressedIcon(BubbleTextView bubbleTextView, Bitmap bitmap);
    }

    public BubbleTextView(Context context) {
        this(context, null, 0);
    }

    public BubbleTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BubbleTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsIconVisible = true;
        this.mTempSpaceForBadgeOffset = new Point();
        this.mTempIconBounds = new Rect();
        this.mDisableRelayout = false;
        this.mLauncher = Launcher.getLauncher(context);
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0622R.styleable.BubbleTextView, i, 0);
        this.mLayoutHorizontal = obtainStyledAttributes.getBoolean(C0622R.styleable.BubbleTextView_layoutHorizontal, false);
        this.mDeferShadowGenerationOnTouch = obtainStyledAttributes.getBoolean(C0622R.styleable.BubbleTextView_deferShadowGeneration, false);
        int integer = obtainStyledAttributes.getInteger(C0622R.styleable.BubbleTextView_iconDisplay, 0);
        int i2 = deviceProfile.iconSizePx;
        if (integer == 0) {
            setTextSize(0, (float) deviceProfile.iconTextSizePx);
            setCompoundDrawablePadding(deviceProfile.iconDrawablePaddingPx);
        } else if (integer == 1) {
            setTextSize(0, deviceProfile.allAppsIconTextSizePx);
            setCompoundDrawablePadding(deviceProfile.allAppsIconDrawablePaddingPx);
            i2 = deviceProfile.allAppsIconSizePx;
        } else if (integer == 2) {
            setTextSize(0, (float) deviceProfile.folderChildTextSizePx);
            setCompoundDrawablePadding(deviceProfile.folderChildDrawablePaddingPx);
            i2 = deviceProfile.folderChildIconSizePx;
        }
        this.mCenterVertically = obtainStyledAttributes.getBoolean(C0622R.styleable.BubbleTextView_centerVertically, false);
        this.mIconSize = obtainStyledAttributes.getDimensionPixelSize(C0622R.styleable.BubbleTextView_iconSizeOverride, i2);
        obtainStyledAttributes.recycle();
        this.mLongPressHelper = new CheckLongPressHelper(this);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mOutlineHelper = HolographicOutlineHelper.getInstance(getContext());
        setAccessibilityDelegate(this.mLauncher.getAccessibilityDelegate());
    }

    public void applyFromShortcutInfo(ShortcutInfo shortcutInfo) {
        applyFromShortcutInfo(shortcutInfo, false);
    }

    public void applyFromShortcutInfo(ShortcutInfo shortcutInfo, boolean z) {
        applyIconAndLabel(shortcutInfo.iconBitmap, shortcutInfo);
        setTag(shortcutInfo);
        if (z || shortcutInfo.hasPromiseIconUi()) {
            applyPromiseState(z);
        }
        applyBadgeState(shortcutInfo, false);
    }

    public void applyFromApplicationInfo(AppInfo appInfo) {
        applyIconAndLabel(appInfo.iconBitmap, appInfo);
        super.setTag(appInfo);
        verifyHighRes();
        if (appInfo instanceof PromiseAppInfo) {
            applyProgressLevel(((PromiseAppInfo) appInfo).level);
        }
        applyBadgeState(appInfo, false);
    }

    public void applyFromPackageItemInfo(PackageItemInfo packageItemInfo) {
        applyIconAndLabel(packageItemInfo.iconBitmap, packageItemInfo);
        super.setTag(packageItemInfo);
        verifyHighRes();
    }

    private void applyIconAndLabel(Bitmap bitmap, ItemInfo itemInfo) {
        CharSequence charSequence;
        FastBitmapDrawable newIcon = DrawableFactory.get(getContext()).newIcon(bitmap, itemInfo);
        newIcon.setIsDisabled(itemInfo.isDisabled());
        setIcon(newIcon);
        setText(itemInfo.title);
        if (itemInfo.contentDescription != null) {
            if (itemInfo.isDisabled()) {
                charSequence = getContext().getString(C0622R.string.disabled_app_label, new Object[]{itemInfo.contentDescription});
            } else {
                charSequence = itemInfo.contentDescription;
            }
            setContentDescription(charSequence);
        }
    }

    public void setLongPressTimeout(int i) {
        this.mLongPressHelper.setLongPressTimeout(i);
    }

    public void setTag(Object obj) {
        if (obj != null) {
            LauncherModel.checkItemInfo((ItemInfo) obj);
        }
        super.setTag(obj);
    }

    public void refreshDrawableState() {
        if (!this.mIgnorePressedStateChange) {
            super.refreshDrawableState();
        }
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        if (this.mStayPressed) {
            mergeDrawableStates(onCreateDrawableState, STATE_PRESSED);
        }
        return onCreateDrawableState;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        if (this.mStylusEventHelper.onMotionEvent(motionEvent)) {
            this.mLongPressHelper.cancelLongPress();
            onTouchEvent = true;
        }
        switch (motionEvent.getAction()) {
            case 0:
                if (!this.mDeferShadowGenerationOnTouch && this.mPressedBackground == null) {
                    this.mPressedBackground = this.mOutlineHelper.createMediumDropShadow(this);
                }
                if (!this.mStylusEventHelper.inStylusButtonPressed()) {
                    this.mLongPressHelper.postCheckForLongPress();
                    break;
                }
                break;
            case 1:
            case 3:
                if (!isPressed()) {
                    this.mPressedBackground = null;
                }
                this.mLongPressHelper.cancelLongPress();
                break;
            case 2:
                if (!Utilities.pointInView(this, motionEvent.getX(), motionEvent.getY(), this.mSlop)) {
                    this.mLongPressHelper.cancelLongPress();
                    break;
                }
                break;
        }
        return onTouchEvent;
    }

    /* access modifiers changed from: 0000 */
    public void setStayPressed(boolean z) {
        this.mStayPressed = z;
        if (!z) {
            HolographicOutlineHelper.getInstance(getContext()).recycleShadowBitmap(this.mPressedBackground);
            this.mPressedBackground = null;
        } else if (this.mPressedBackground == null) {
            this.mPressedBackground = this.mOutlineHelper.createMediumDropShadow(this);
        }
        ViewParent parent = getParent();
        if (parent != null && (parent.getParent() instanceof BubbleTextShadowHandler)) {
            ((BubbleTextShadowHandler) parent.getParent()).setPressedIcon(this, this.mPressedBackground);
        }
        refreshDrawableState();
    }

    /* access modifiers changed from: 0000 */
    public void clearPressedBackground() {
        setPressed(false);
        setStayPressed(false);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (!super.onKeyDown(i, keyEvent)) {
            return false;
        }
        if (this.mPressedBackground == null) {
            this.mPressedBackground = this.mOutlineHelper.createMediumDropShadow(this);
        }
        return true;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        this.mIgnorePressedStateChange = true;
        boolean onKeyUp = super.onKeyUp(i, keyEvent);
        this.mPressedBackground = null;
        this.mIgnorePressedStateChange = false;
        refreshDrawableState();
        return onKeyUp;
    }

    /* access modifiers changed from: protected */
    public void drawWithoutBadge(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBadgeIfNecessary(canvas);
    }

    /* access modifiers changed from: protected */
    public void drawBadgeIfNecessary(Canvas canvas) {
        if (this.mForceHideBadge) {
            return;
        }
        if (hasBadge() || this.mBadgeScale > 0.0f) {
            getIconBounds(this.mTempIconBounds);
            this.mTempSpaceForBadgeOffset.set((getWidth() - this.mIconSize) / 2, getPaddingTop());
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            canvas.translate((float) scrollX, (float) scrollY);
            this.mBadgeRenderer.draw(canvas, this.mBadgePalette, this.mBadgeInfo, this.mTempIconBounds, this.mBadgeScale, this.mTempSpaceForBadgeOffset);
            canvas.translate((float) (-scrollX), (float) (-scrollY));
        }
    }

    public void forceHideBadge(boolean z) {
        if (this.mForceHideBadge != z) {
            this.mForceHideBadge = z;
            if (z) {
                invalidate();
            } else if (hasBadge()) {
                ObjectAnimator.ofFloat(this, BADGE_SCALE_PROPERTY, new float[]{0.0f, 1.0f}).start();
            }
        }
    }

    private boolean hasBadge() {
        return this.mBadgeInfo != null;
    }

    public void getIconBounds(Rect rect) {
        int paddingTop = getPaddingTop();
        int width = (getWidth() - this.mIconSize) / 2;
        rect.set(width, paddingTop, this.mIconSize + width, this.mIconSize + paddingTop);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.mCenterVertically) {
            FontMetrics fontMetrics = getPaint().getFontMetrics();
            int compoundDrawablePadding = this.mIconSize + getCompoundDrawablePadding() + ((int) Math.ceil((double) (fontMetrics.bottom - fontMetrics.top)));
            setPadding(getPaddingLeft(), (MeasureSpec.getSize(i2) - compoundDrawablePadding) / 2, getPaddingRight(), getPaddingBottom());
        }
        super.onMeasure(i, i2);
    }

    public void setTextColor(int i) {
        this.mTextColor = i;
        super.setTextColor(i);
    }

    public void setTextColor(ColorStateList colorStateList) {
        this.mTextColor = colorStateList.getDefaultColor();
        super.setTextColor(colorStateList);
    }

    public boolean shouldTextBeVisible() {
        Object tag = getParent() instanceof FolderIcon ? ((View) getParent()).getTag() : getTag();
        ItemInfo itemInfo = tag instanceof ItemInfo ? (ItemInfo) tag : null;
        return itemInfo == null || itemInfo.container != -101;
    }

    public void setTextVisibility(boolean z) {
        if (z) {
            super.setTextColor(this.mTextColor);
        } else {
            setTextAlpha(0);
        }
    }

    /* access modifiers changed from: private */
    public void setTextAlpha(int i) {
        super.setTextColor(ColorUtils.setAlphaComponent(this.mTextColor, i));
    }

    /* access modifiers changed from: private */
    public int getTextAlpha() {
        return Color.alpha(getCurrentTextColor());
    }

    public ObjectAnimator createTextAlphaAnimator(boolean z) {
        return ObjectAnimator.ofInt(this, TEXT_ALPHA_PROPERTY, new int[]{(!shouldTextBeVisible() || !z) ? 0 : Color.alpha(this.mTextColor)});
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public void applyPromiseState(boolean z) {
        if (getTag() instanceof ShortcutInfo) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) getTag();
            int i = shortcutInfo.hasPromiseIconUi() ? shortcutInfo.hasStatusFlag(4) ? shortcutInfo.getInstallProgress() : 0 : 100;
            PreloadIconDrawable applyProgressLevel = applyProgressLevel(i);
            if (applyProgressLevel != null && z) {
                applyProgressLevel.maybePerformFinishedAnimation();
            }
        }
    }

    public PreloadIconDrawable applyProgressLevel(int i) {
        String str;
        PreloadIconDrawable preloadIconDrawable;
        if (getTag() instanceof ItemInfoWithIcon) {
            ItemInfoWithIcon itemInfoWithIcon = (ItemInfoWithIcon) getTag();
            if (i > 0) {
                str = getContext().getString(C0622R.string.app_downloading_title, new Object[]{itemInfoWithIcon.title, NumberFormat.getPercentInstance().format(((double) i) * 0.01d)});
            } else {
                str = getContext().getString(C0622R.string.app_waiting_download_title, new Object[]{itemInfoWithIcon.title});
            }
            setContentDescription(str);
            if (this.mIcon != null) {
                if (this.mIcon instanceof PreloadIconDrawable) {
                    preloadIconDrawable = (PreloadIconDrawable) this.mIcon;
                    preloadIconDrawable.setLevel(i);
                } else {
                    preloadIconDrawable = DrawableFactory.get(getContext()).newPendingIcon(itemInfoWithIcon.iconBitmap, getContext());
                    preloadIconDrawable.setLevel(i);
                    setIcon(preloadIconDrawable);
                }
                return preloadIconDrawable;
            }
        }
        return null;
    }

    public void applyBadgeState(ItemInfo itemInfo, boolean z) {
        if (this.mIcon instanceof FastBitmapDrawable) {
            boolean z2 = this.mBadgeInfo != null;
            this.mBadgeInfo = this.mLauncher.getPopupDataProvider().getBadgeInfoForItem(itemInfo);
            boolean z3 = this.mBadgeInfo != null;
            float f = z3 ? 1.0f : 0.0f;
            this.mBadgeRenderer = this.mLauncher.getDeviceProfile().mBadgeRenderer;
            if (z2 || z3) {
                this.mBadgePalette = IconPalette.getBadgePalette(getResources());
                if (this.mBadgePalette == null) {
                    this.mBadgePalette = ((FastBitmapDrawable) this.mIcon).getIconPalette();
                }
                if (!z || !(z3 ^ z2) || !isShown()) {
                    this.mBadgeScale = f;
                    invalidate();
                    return;
                }
                ObjectAnimator.ofFloat(this, BADGE_SCALE_PROPERTY, new float[]{f}).start();
            }
        }
    }

    public IconPalette getBadgePalette() {
        return this.mBadgePalette;
    }

    private void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        this.mIcon.setBounds(0, 0, this.mIconSize, this.mIconSize);
        if (this.mIsIconVisible) {
            applyCompoundDrawables(this.mIcon);
        }
    }

    public void setIconVisible(boolean z) {
        this.mIsIconVisible = z;
        this.mDisableRelayout = true;
        Drawable drawable = this.mIcon;
        if (!z) {
            drawable = new ColorDrawable(0);
            drawable.setBounds(0, 0, this.mIconSize, this.mIconSize);
        }
        applyCompoundDrawables(drawable);
        this.mDisableRelayout = false;
    }

    /* access modifiers changed from: protected */
    public void applyCompoundDrawables(Drawable drawable) {
        if (this.mLayoutHorizontal) {
            setCompoundDrawablesRelative(drawable, null, null, null);
        } else {
            setCompoundDrawables(null, drawable, null, null);
        }
    }

    public void requestLayout() {
        if (!this.mDisableRelayout) {
            super.requestLayout();
        }
    }

    public void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon) {
        if (getTag() == itemInfoWithIcon) {
            this.mIconLoadRequest = null;
            this.mDisableRelayout = true;
            itemInfoWithIcon.iconBitmap.prepareToDraw();
            if (itemInfoWithIcon instanceof AppInfo) {
                applyFromApplicationInfo((AppInfo) itemInfoWithIcon);
            } else if (itemInfoWithIcon instanceof ShortcutInfo) {
                applyFromShortcutInfo((ShortcutInfo) itemInfoWithIcon);
                if (new FolderIconPreviewVerifier(this.mLauncher.getDeviceProfile().inv).isItemInPreview(itemInfoWithIcon.rank) && itemInfoWithIcon.container >= 0) {
                    View homescreenIconByItemId = this.mLauncher.getWorkspace().getHomescreenIconByItemId(itemInfoWithIcon.container);
                    if (homescreenIconByItemId != null) {
                        homescreenIconByItemId.invalidate();
                    }
                }
            } else if (itemInfoWithIcon instanceof PackageItemInfo) {
                applyFromPackageItemInfo((PackageItemInfo) itemInfoWithIcon);
            }
            this.mDisableRelayout = false;
        }
    }

    public void verifyHighRes() {
        if (this.mIconLoadRequest != null) {
            this.mIconLoadRequest.cancel();
            this.mIconLoadRequest = null;
        }
        if (getTag() instanceof ItemInfoWithIcon) {
            ItemInfoWithIcon itemInfoWithIcon = (ItemInfoWithIcon) getTag();
            if (itemInfoWithIcon.usingLowResIcon) {
                this.mIconLoadRequest = LauncherAppState.getInstance(getContext()).getIconCache().updateIconInBackground(this, itemInfoWithIcon);
            }
        }
    }

    public int getIconSize() {
        return this.mIconSize;
    }
}
