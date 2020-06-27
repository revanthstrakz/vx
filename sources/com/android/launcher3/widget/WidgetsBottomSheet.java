package com.android.launcher3.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.C0622R;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Insettable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.graphics.GradientView;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.touch.SwipeDetector;
import com.android.launcher3.touch.SwipeDetector.Listener;
import com.android.launcher3.touch.SwipeDetector.ScrollInterpolator;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.SystemUiController;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.TouchController;
import java.util.List;

public class WidgetsBottomSheet extends AbstractFloatingView implements Insettable, TouchController, Listener, OnClickListener, OnLongClickListener, DragListener {
    private Interpolator mFastOutSlowInInterpolator;
    private GradientView mGradientBackground;
    private Rect mInsets;
    private Launcher mLauncher;
    private ObjectAnimator mOpenCloseAnimator;
    private ItemInfo mOriginalItemInfo;
    private ScrollInterpolator mScrollInterpolator;
    /* access modifiers changed from: private */
    public SwipeDetector mSwipeDetector;
    private int mTranslationYClosed;
    private int mTranslationYOpen;
    private float mTranslationYRange;

    public int getLogContainerType() {
        return 5;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 4) != 0;
    }

    public void onDragEnd() {
    }

    public void onDragStart(boolean z) {
    }

    public WidgetsBottomSheet(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetsBottomSheet(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setWillNotDraw(false);
        this.mLauncher = Launcher.getLauncher(context);
        this.mOpenCloseAnimator = LauncherAnimUtils.ofPropertyValuesHolder(this, new PropertyValuesHolder[0]);
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, AndroidResources.FAST_OUT_SLOW_IN);
        this.mScrollInterpolator = new ScrollInterpolator();
        this.mInsets = new Rect();
        this.mSwipeDetector = new SwipeDetector(context, (Listener) this, SwipeDetector.VERTICAL);
        this.mGradientBackground = (GradientView) this.mLauncher.getLayoutInflater().inflate(C0622R.layout.gradient_bg, this.mLauncher.getDragLayer(), false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mTranslationYOpen = 0;
        this.mTranslationYClosed = getMeasuredHeight();
        this.mTranslationYRange = (float) (this.mTranslationYClosed - this.mTranslationYOpen);
    }

    public void populateAndShow(ItemInfo itemInfo) {
        this.mOriginalItemInfo = itemInfo;
        ((TextView) findViewById(C0622R.C0625id.title)).setText(getContext().getString(C0622R.string.widgets_bottom_sheet_title, new Object[]{this.mOriginalItemInfo.title}));
        onWidgetsBound();
        this.mLauncher.getDragLayer().addView(this.mGradientBackground);
        this.mGradientBackground.setVisibility(0);
        this.mLauncher.getDragLayer().addView(this);
        measure(0, 0);
        setTranslationY((float) this.mTranslationYClosed);
        this.mIsOpen = false;
        open(true);
    }

    /* access modifiers changed from: protected */
    public void onWidgetsBound() {
        List widgetsForPackageUser = this.mLauncher.getWidgetsForPackageUser(new PackageUserKey(this.mOriginalItemInfo.getTargetComponent().getPackageName(), this.mOriginalItemInfo.user));
        ViewGroup viewGroup = (ViewGroup) findViewById(C0622R.C0625id.widgets);
        ViewGroup viewGroup2 = (ViewGroup) viewGroup.findViewById(C0622R.C0625id.widgets_cell_list);
        viewGroup2.removeAllViews();
        for (int i = 0; i < widgetsForPackageUser.size(); i++) {
            WidgetCell addItemCell = addItemCell(viewGroup2);
            addItemCell.applyFromCellItem((WidgetItem) widgetsForPackageUser.get(i), LauncherAppState.getInstance(this.mLauncher).getWidgetCache());
            addItemCell.ensurePreview();
            addItemCell.setVisibility(0);
            if (i < widgetsForPackageUser.size() - 1) {
                addDivider(viewGroup2);
            }
        }
        if (widgetsForPackageUser.size() == 1) {
            ((LayoutParams) viewGroup.getLayoutParams()).gravity = 1;
            return;
        }
        View inflate = LayoutInflater.from(getContext()).inflate(C0622R.layout.widget_list_divider, viewGroup, false);
        inflate.getLayoutParams().width = Utilities.pxFromDp(16.0f, getResources().getDisplayMetrics());
        viewGroup2.addView(inflate, 0);
    }

    private void addDivider(ViewGroup viewGroup) {
        LayoutInflater.from(getContext()).inflate(C0622R.layout.widget_list_divider, viewGroup, true);
    }

    private WidgetCell addItemCell(ViewGroup viewGroup) {
        WidgetCell widgetCell = (WidgetCell) LayoutInflater.from(getContext()).inflate(C0622R.layout.widget_cell, viewGroup, false);
        widgetCell.setOnClickListener(this);
        widgetCell.setOnLongClickListener(this);
        widgetCell.setAnimatePreview(false);
        viewGroup.addView(widgetCell);
        return widgetCell;
    }

    public void onClick(View view) {
        this.mLauncher.getWidgetsView().handleClick();
    }

    public boolean onLongClick(View view) {
        this.mLauncher.getDragController().addDragListener(this);
        return this.mLauncher.getWidgetsView().handleLongClick(view);
    }

    private void open(boolean z) {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            int i = 1;
            this.mIsOpen = true;
            boolean attrBoolean = Themes.getAttrBoolean(this.mLauncher, C0622R.attr.isMainColorDark);
            SystemUiController systemUiController = this.mLauncher.getSystemUiController();
            if (attrBoolean) {
                i = 2;
            }
            systemUiController.updateUiState(2, i);
            if (z) {
                this.mOpenCloseAnimator.setValues(new PropertyListBuilder().translationY((float) this.mTranslationYOpen).build());
                this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        WidgetsBottomSheet.this.mSwipeDetector.finishedScrolling();
                    }
                });
                this.mOpenCloseAnimator.setInterpolator(this.mFastOutSlowInInterpolator);
                this.mOpenCloseAnimator.start();
            } else {
                setTranslationY((float) this.mTranslationYOpen);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        if (this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            if (z) {
                this.mOpenCloseAnimator.setValues(new PropertyListBuilder().translationY((float) this.mTranslationYClosed).build());
                this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        WidgetsBottomSheet.this.mSwipeDetector.finishedScrolling();
                        WidgetsBottomSheet.this.onCloseComplete();
                    }
                });
                this.mOpenCloseAnimator.setInterpolator(this.mSwipeDetector.isIdleState() ? this.mFastOutSlowInInterpolator : this.mScrollInterpolator);
                this.mOpenCloseAnimator.start();
            } else {
                setTranslationY((float) this.mTranslationYClosed);
                onCloseComplete();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onCloseComplete() {
        this.mIsOpen = false;
        this.mLauncher.getDragLayer().removeView(this.mGradientBackground);
        this.mLauncher.getDragLayer().removeView(this);
        this.mLauncher.getSystemUiController().updateUiState(2, 0);
    }

    public static WidgetsBottomSheet getOpen(Launcher launcher) {
        return (WidgetsBottomSheet) getOpenView(launcher, 4);
    }

    public void setInsets(Rect rect) {
        int i = rect.left - this.mInsets.left;
        int i2 = rect.right - this.mInsets.right;
        int i3 = rect.bottom - this.mInsets.bottom;
        this.mInsets.set(rect);
        if (!Utilities.ATLEAST_OREO && !this.mLauncher.getDeviceProfile().isVerticalBarLayout()) {
            View findViewById = findViewById(C0622R.C0625id.nav_bar_bg);
            ViewGroup.LayoutParams layoutParams = findViewById.getLayoutParams();
            layoutParams.height = i3;
            findViewById.setLayoutParams(layoutParams);
            i3 = 0;
        }
        setPadding(getPaddingLeft() + i, getPaddingTop(), getPaddingRight() + i2, getPaddingBottom() + i3);
    }

    public boolean onDrag(float f, float f2) {
        setTranslationY(Utilities.boundToRange(f, (float) this.mTranslationYOpen, (float) this.mTranslationYClosed));
        return true;
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        if (this.mGradientBackground != null) {
            float f2 = (((float) this.mTranslationYClosed) - f) / this.mTranslationYRange;
            this.mGradientBackground.setProgress(f2, f2 <= 0.0f);
        }
    }

    public void onDragEnd(float f, boolean z) {
        if ((!z || f <= 0.0f) && getTranslationY() <= this.mTranslationYRange / 2.0f) {
            this.mIsOpen = false;
            this.mOpenCloseAnimator.setDuration(SwipeDetector.calculateDuration(f, (getTranslationY() - ((float) this.mTranslationYOpen)) / this.mTranslationYRange));
            open(true);
            return;
        }
        this.mScrollInterpolator.setVelocityAtZero(f);
        this.mOpenCloseAnimator.setDuration(SwipeDetector.calculateDuration(f, (((float) this.mTranslationYClosed) - getTranslationY()) / this.mTranslationYRange));
        close(true);
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeDetector.onTouchEvent(motionEvent);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        this.mSwipeDetector.setDetectableScrollConditions(this.mSwipeDetector.isIdleState() ? 2 : 0, false);
        this.mSwipeDetector.onTouchEvent(motionEvent);
        return this.mSwipeDetector.isDraggingOrSettling();
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        close(true);
    }
}
