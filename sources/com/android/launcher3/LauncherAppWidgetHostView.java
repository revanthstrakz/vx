package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.Advanceable;
import android.widget.RemoteViews;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragLayer.TouchCompleteListener;

public class LauncherAppWidgetHostView extends AppWidgetHostView implements TouchCompleteListener, OnLongClickListener {
    private static final long ADVANCE_INTERVAL = 20000;
    private static final long ADVANCE_STAGGER = 250;
    private static final SparseBooleanArray sAutoAdvanceWidgetIds = new SparseBooleanArray();
    private Runnable mAutoAdvanceRunnable;
    @ExportedProperty(category = "launcher")
    private boolean mChildrenFocused;
    private final Context mContext;
    protected final LayoutInflater mInflater;
    private boolean mIsAttachedToWindow;
    private boolean mIsAutoAdvanceRegistered;
    private boolean mIsScrollable;
    private final CheckLongPressHelper mLongPressHelper;
    @ExportedProperty(category = "launcher")
    private int mPreviousOrientation;
    private float mScaleToFit = 1.0f;
    private float mSlop;
    private final StylusEventHelper mStylusEventHelper;
    private final PointF mTranslationForCentering = new PointF(0.0f, 0.0f);

    public LauncherAppWidgetHostView(Context context) {
        super(context);
        this.mContext = context;
        this.mLongPressHelper = new CheckLongPressHelper(this, this);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mInflater = LayoutInflater.from(context);
        setAccessibilityDelegate(Launcher.getLauncher(context).getAccessibilityDelegate());
        setBackgroundResource(C0622R.C0624drawable.widget_internal_focus_bg);
        if (Utilities.ATLEAST_OREO) {
            setExecutor(Utilities.THREAD_POOL_EXECUTOR);
        }
    }

    public boolean onLongClick(View view) {
        if (this.mIsScrollable) {
            Launcher.getLauncher(getContext()).getDragLayer().requestDisallowInterceptTouchEvent(false);
        }
        view.performLongClick();
        return true;
    }

    /* access modifiers changed from: protected */
    public View getErrorView() {
        return this.mInflater.inflate(C0622R.layout.appwidget_error, this, false);
    }

    public void updateLastInflationOrientation() {
        this.mPreviousOrientation = this.mContext.getResources().getConfiguration().orientation;
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        updateLastInflationOrientation();
        super.updateAppWidget(remoteViews);
        checkIfAutoAdvance();
    }

    private boolean checkScrollableRecursively(ViewGroup viewGroup) {
        if (viewGroup instanceof AdapterView) {
            return true;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if ((childAt instanceof ViewGroup) && checkScrollableRecursively((ViewGroup) childAt)) {
                return true;
            }
        }
        return false;
    }

    public boolean isReinflateRequired(int i) {
        return this.mPreviousOrientation != i;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mLongPressHelper.cancelLongPress();
        }
        if (this.mLongPressHelper.hasPerformedLongPress()) {
            this.mLongPressHelper.cancelLongPress();
            return true;
        } else if (this.mStylusEventHelper.onMotionEvent(motionEvent)) {
            this.mLongPressHelper.cancelLongPress();
            return true;
        } else {
            switch (motionEvent.getAction()) {
                case 0:
                    DragLayer dragLayer = Launcher.getLauncher(getContext()).getDragLayer();
                    if (this.mIsScrollable) {
                        dragLayer.requestDisallowInterceptTouchEvent(true);
                    }
                    if (!this.mStylusEventHelper.inStylusButtonPressed()) {
                        this.mLongPressHelper.postCheckForLongPress();
                    }
                    dragLayer.setTouchCompleteListener(this);
                    break;
                case 1:
                case 3:
                    this.mLongPressHelper.cancelLongPress();
                    break;
                case 2:
                    if (!Utilities.pointInView(this, motionEvent.getX(), motionEvent.getY(), this.mSlop)) {
                        this.mLongPressHelper.cancelLongPress();
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 1:
            case 3:
                this.mLongPressHelper.cancelLongPress();
                break;
            case 2:
                if (!Utilities.pointInView(this, motionEvent.getX(), motionEvent.getY(), this.mSlop)) {
                    this.mLongPressHelper.cancelLongPress();
                    break;
                }
                break;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mIsAttachedToWindow = true;
        checkIfAutoAdvance();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsAttachedToWindow = false;
        checkIfAutoAdvance();
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public AppWidgetProviderInfo getAppWidgetInfo() {
        AppWidgetProviderInfo appWidgetInfo = super.getAppWidgetInfo();
        if (appWidgetInfo == null || (appWidgetInfo instanceof LauncherAppWidgetProviderInfo)) {
            return appWidgetInfo;
        }
        throw new IllegalStateException("Launcher widget must have LauncherAppWidgetProviderInfo");
    }

    public void onTouchComplete() {
        if (!this.mLongPressHelper.hasPerformedLongPress()) {
            this.mLongPressHelper.cancelLongPress();
        }
    }

    public int getDescendantFocusability() {
        return this.mChildrenFocused ? 131072 : 393216;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (!this.mChildrenFocused || keyEvent.getKeyCode() != 111 || keyEvent.getAction() != 1) {
            return super.dispatchKeyEvent(keyEvent);
        }
        this.mChildrenFocused = false;
        requestFocus();
        return true;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.mChildrenFocused || i != 66) {
            return super.onKeyDown(i, keyEvent);
        }
        keyEvent.startTracking();
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0047, code lost:
        ((android.view.View) r1.get(0)).requestFocus();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0050, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onKeyUp(int r5, android.view.KeyEvent r6) {
        /*
            r4 = this;
            boolean r0 = r6.isTracking()
            if (r0 == 0) goto L_0x0051
            boolean r0 = r4.mChildrenFocused
            if (r0 != 0) goto L_0x0051
            r0 = 66
            if (r5 != r0) goto L_0x0051
            r0 = 1
            r4.mChildrenFocused = r0
            r1 = 2
            java.util.ArrayList r1 = r4.getFocusables(r1)
            r1.remove(r4)
            int r2 = r1.size()
            r3 = 0
            switch(r2) {
                case 0: goto L_0x0044;
                case 1: goto L_0x0022;
                default: goto L_0x0021;
            }
        L_0x0021:
            goto L_0x0047
        L_0x0022:
            java.lang.Object r5 = r4.getTag()
            boolean r5 = r5 instanceof com.android.launcher3.ItemInfo
            if (r5 == 0) goto L_0x0047
            java.lang.Object r5 = r4.getTag()
            com.android.launcher3.ItemInfo r5 = (com.android.launcher3.ItemInfo) r5
            int r6 = r5.spanX
            if (r6 != r0) goto L_0x0047
            int r5 = r5.spanY
            if (r5 != r0) goto L_0x0047
            java.lang.Object r5 = r1.get(r3)
            android.view.View r5 = (android.view.View) r5
            r5.performClick()
            r4.mChildrenFocused = r3
            return r0
        L_0x0044:
            r4.mChildrenFocused = r3
            goto L_0x0051
        L_0x0047:
            java.lang.Object r5 = r1.get(r3)
            android.view.View r5 = (android.view.View) r5
            r5.requestFocus()
            return r0
        L_0x0051:
            boolean r5 = super.onKeyUp(r5, r6)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherAppWidgetHostView.onKeyUp(int, android.view.KeyEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        if (z) {
            this.mChildrenFocused = false;
            dispatchChildFocus(false);
        }
        super.onFocusChanged(z, i, rect);
    }

    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        dispatchChildFocus(this.mChildrenFocused && view2 != null);
        if (view2 != null) {
            view2.setFocusableInTouchMode(false);
        }
    }

    public void clearChildFocus(View view) {
        super.clearChildFocus(view);
        dispatchChildFocus(false);
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        return this.mChildrenFocused;
    }

    private void dispatchChildFocus(boolean z) {
        setSelected(z);
    }

    public void switchToErrorView() {
        updateAppWidget(new RemoteViews(getAppWidgetInfo().provider.getPackageName(), 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        try {
            super.onLayout(z, i, i2, i3, i4);
        } catch (RuntimeException unused) {
            post(new Runnable() {
                public void run() {
                    LauncherAppWidgetHostView.this.switchToErrorView();
                }
            });
        }
        this.mIsScrollable = checkScrollableRecursively(this);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(getClass().getName());
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        maybeRegisterAutoAdvance();
    }

    private void checkIfAutoAdvance() {
        boolean z;
        Advanceable advanceable = getAdvanceable();
        boolean z2 = false;
        if (advanceable != null) {
            advanceable.fyiWillBeAdvancedByHostKThx();
            z = true;
        } else {
            z = false;
        }
        if (sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId()) >= 0) {
            z2 = true;
        }
        if (z != z2) {
            if (z) {
                sAutoAdvanceWidgetIds.put(getAppWidgetId(), true);
            } else {
                sAutoAdvanceWidgetIds.delete(getAppWidgetId());
            }
            maybeRegisterAutoAdvance();
        }
    }

    private Advanceable getAdvanceable() {
        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo();
        Advanceable advanceable = null;
        if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1 || !this.mIsAttachedToWindow) {
            return null;
        }
        View findViewById = findViewById(appWidgetInfo.autoAdvanceViewId);
        if (findViewById instanceof Advanceable) {
            advanceable = (Advanceable) findViewById;
        }
        return advanceable;
    }

    private void maybeRegisterAutoAdvance() {
        Handler handler = getHandler();
        boolean z = getWindowVisibility() == 0 && handler != null && sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId()) >= 0;
        if (z != this.mIsAutoAdvanceRegistered) {
            this.mIsAutoAdvanceRegistered = z;
            if (this.mAutoAdvanceRunnable == null) {
                this.mAutoAdvanceRunnable = new Runnable() {
                    public void run() {
                        LauncherAppWidgetHostView.this.runAutoAdvance();
                    }
                };
            }
            handler.removeCallbacks(this.mAutoAdvanceRunnable);
            scheduleNextAdvance();
        }
    }

    private void scheduleNextAdvance() {
        if (this.mIsAutoAdvanceRegistered) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long indexOfKey = uptimeMillis + (ADVANCE_INTERVAL - (uptimeMillis % ADVANCE_INTERVAL)) + (((long) sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId())) * ADVANCE_STAGGER);
            Handler handler = getHandler();
            if (handler != null) {
                handler.postAtTime(this.mAutoAdvanceRunnable, indexOfKey);
            }
        }
    }

    /* access modifiers changed from: private */
    public void runAutoAdvance() {
        Advanceable advanceable = getAdvanceable();
        if (advanceable != null) {
            advanceable.advance();
        }
        scheduleNextAdvance();
    }

    public void setScaleToFit(float f) {
        this.mScaleToFit = f;
        setScaleX(f);
        setScaleY(f);
    }

    public float getScaleToFit() {
        return this.mScaleToFit;
    }

    public void setTranslationForCentering(float f, float f2) {
        this.mTranslationForCentering.set(f, f2);
        setTranslationX(f);
        setTranslationY(f2);
    }

    public PointF getTranslationForCentering() {
        return this.mTranslationForCentering;
    }
}
