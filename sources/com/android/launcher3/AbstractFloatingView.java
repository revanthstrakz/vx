package com.android.launcher3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.android.launcher3.dragndrop.DragLayer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class AbstractFloatingView extends LinearLayout {
    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_POPUP_CONTAINER_WITH_ARROW = 2;
    public static final int TYPE_WIDGETS_BOTTOM_SHEET = 4;
    protected boolean mIsOpen;

    @Retention(RetentionPolicy.SOURCE)
    public @interface FloatingViewType {
    }

    public ExtendedEditText getActiveTextView() {
        return null;
    }

    public View getExtendedTouchView() {
        return null;
    }

    public abstract int getLogContainerType();

    /* access modifiers changed from: protected */
    public abstract void handleClose(boolean z);

    /* access modifiers changed from: protected */
    public abstract boolean isOfType(int i);

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onWidgetsBound() {
    }

    public AbstractFloatingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AbstractFloatingView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public final void close(boolean z) {
        handleClose(z & (!Utilities.isPowerSaverOn(getContext())));
        Launcher.getLauncher(getContext()).getUserEventDispatcher().resetElapsedContainerMillis();
    }

    public final boolean isOpen() {
        return this.mIsOpen;
    }

    protected static <T extends AbstractFloatingView> T getOpenView(Launcher launcher, int i) {
        DragLayer dragLayer = launcher.getDragLayer();
        for (int childCount = dragLayer.getChildCount() - 1; childCount >= 0; childCount--) {
            T childAt = dragLayer.getChildAt(childCount);
            if (childAt instanceof AbstractFloatingView) {
                T t = (AbstractFloatingView) childAt;
                if (t.isOfType(i) && t.isOpen()) {
                    return t;
                }
            }
        }
        return null;
    }

    public static void closeOpenContainer(Launcher launcher, int i) {
        AbstractFloatingView openView = getOpenView(launcher, i);
        if (openView != null) {
            openView.close(true);
        }
    }

    public static void closeAllOpenViews(Launcher launcher, boolean z) {
        DragLayer dragLayer = launcher.getDragLayer();
        for (int childCount = dragLayer.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = dragLayer.getChildAt(childCount);
            if (childAt instanceof AbstractFloatingView) {
                ((AbstractFloatingView) childAt).close(z);
            }
        }
    }

    public static void closeAllOpenViews(Launcher launcher) {
        closeAllOpenViews(launcher, true);
    }

    public static AbstractFloatingView getTopOpenView(Launcher launcher) {
        return getOpenView(launcher, 7);
    }
}
