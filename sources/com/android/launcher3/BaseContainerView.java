package com.android.launcher3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import com.android.launcher3.DeviceProfile.LauncherLayoutChangeListener;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.util.TransformingTouchDelegate;

public abstract class BaseContainerView extends FrameLayout implements LauncherLayoutChangeListener {
    private static final Rect sBgPaddingRect = new Rect();
    protected final Drawable mBaseDrawable;
    private View mContent;
    private final PointF mLastTouchDownPosPx;
    private View mRevealView;
    private TransformingTouchDelegate mTouchDelegate;

    public abstract View getTouchDelegateTargetView();

    public BaseContainerView(Context context) {
        this(context, null);
    }

    public BaseContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BaseContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLastTouchDownPosPx = new PointF(-1.0f, -1.0f);
        if (this instanceof AllAppsContainerView) {
            this.mBaseDrawable = new ColorDrawable();
            return;
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0622R.styleable.BaseContainerView, i, 0);
        this.mBaseDrawable = obtainStyledAttributes.getDrawable(C0622R.styleable.BaseContainerView_revealBackground);
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Launcher.getLauncher(getContext()).getDeviceProfile().addLauncherLayoutChangedListener(this);
        View touchDelegateTargetView = getTouchDelegateTargetView();
        if (touchDelegateTargetView != null) {
            this.mTouchDelegate = new TransformingTouchDelegate(touchDelegateTargetView);
            ((View) touchDelegateTargetView.getParent()).setTouchDelegate(this.mTouchDelegate);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Launcher.getLauncher(getContext()).getDeviceProfile().removeLauncherLayoutChangedListener(this);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = findViewById(C0622R.C0625id.main_content);
        this.mRevealView = findViewById(C0622R.C0625id.reveal_view);
        updatePaddings();
    }

    public void onLauncherLayoutChanged() {
        updatePaddings();
    }

    private void updatePaddings() {
        int i;
        DeviceProfile deviceProfile = Launcher.getLauncher(getContext()).getDeviceProfile();
        int[] containerPadding = deviceProfile.getContainerPadding();
        int i2 = 0;
        int i3 = containerPadding[0];
        int i4 = containerPadding[1];
        if (!deviceProfile.isVerticalBarLayout()) {
            i3 += deviceProfile.edgeMarginPx;
            i4 += deviceProfile.edgeMarginPx;
            i2 = deviceProfile.edgeMarginPx;
            i = i2;
        } else {
            i = 0;
        }
        updateBackground(i3, i2, i4, i);
    }

    /* access modifiers changed from: protected */
    public void updateBackground(int i, int i2, int i3, int i4) {
        View view = this.mRevealView;
        int i5 = i;
        int i6 = i2;
        int i7 = i3;
        int i8 = i4;
        InsetDrawable insetDrawable = new InsetDrawable(this.mBaseDrawable, i5, i6, i7, i8);
        view.setBackground(insetDrawable);
        View view2 = this.mContent;
        InsetDrawable insetDrawable2 = new InsetDrawable(this.mBaseDrawable, i5, i6, i7, i8);
        view2.setBackground(insetDrawable2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        View touchDelegateTargetView = getTouchDelegateTargetView();
        if (touchDelegateTargetView != null) {
            getRevealView().getBackground().getPadding(sBgPaddingRect);
            this.mTouchDelegate.setBounds(touchDelegateTargetView.getLeft() - sBgPaddingRect.left, touchDelegateTargetView.getTop() - sBgPaddingRect.top, touchDelegateTargetView.getRight() + sBgPaddingRect.right, touchDelegateTargetView.getBottom() + sBgPaddingRect.bottom);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return handleTouchEvent(motionEvent);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return handleTouchEvent(motionEvent);
    }

    public void setRevealDrawableColor(int i) {
        ((ColorDrawable) this.mBaseDrawable).setColor(i);
    }

    public final View getContentView() {
        return this.mContent;
    }

    public final View getRevealView() {
        return this.mRevealView;
    }

    private boolean handleTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 3) {
            switch (action) {
                case 0:
                    View touchDelegateTargetView = getTouchDelegateTargetView();
                    float left = (float) touchDelegateTargetView.getLeft();
                    if (motionEvent.getX() < left || motionEvent.getX() > ((float) touchDelegateTargetView.getWidth()) + left) {
                        this.mLastTouchDownPosPx.set((float) ((int) motionEvent.getX()), (float) ((int) motionEvent.getY()));
                        break;
                    }
                case 1:
                    if (this.mLastTouchDownPosPx.x > -1.0f) {
                        if (PointF.length(motionEvent.getX() - this.mLastTouchDownPosPx.x, motionEvent.getY() - this.mLastTouchDownPosPx.y) < ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                            Launcher.getLauncher(getContext()).showWorkspace(true);
                            return true;
                        }
                    }
                    break;
            }
        }
        this.mLastTouchDownPosPx.set(-1.0f, -1.0f);
        return false;
    }
}
