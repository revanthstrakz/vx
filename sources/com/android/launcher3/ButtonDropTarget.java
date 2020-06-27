package com.android.launcher3;

import android.animation.AnimatorSet;
import android.animation.FloatArrayEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.util.Themes;

public abstract class ButtonDropTarget extends TextView implements DropTarget, DragListener, OnClickListener {
    private static final int DRAG_VIEW_DROP_DURATION = 285;
    private boolean mAccessibleDrag;
    protected boolean mActive;
    private int mBottomDragPadding;
    private AnimatorSet mCurrentColorAnim;
    ColorMatrix mCurrentFilter;
    private final int mDragDistanceThreshold;
    protected Drawable mDrawable;
    protected DropTargetBar mDropTargetBar;
    ColorMatrix mDstFilter;
    private final boolean mHideParentOnDisable;
    protected int mHoverColor;
    protected final Launcher mLauncher;
    protected ColorStateList mOriginalTextColor;
    ColorMatrix mSrcFilter;
    protected CharSequence mText;

    public abstract void completeDrop(DragObject dragObject);

    public void onDragOver(DragObject dragObject) {
    }

    public void prepareAccessibilityDrop() {
    }

    /* access modifiers changed from: protected */
    public abstract boolean supportsDrop(DragSource dragSource, ItemInfo itemInfo);

    public ButtonDropTarget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ButtonDropTarget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHoverColor = 0;
        this.mLauncher = Launcher.getLauncher(context);
        Resources resources = getResources();
        this.mBottomDragPadding = resources.getDimensionPixelSize(C0622R.dimen.drop_target_drag_padding);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0622R.styleable.ButtonDropTarget, i, 0);
        this.mHideParentOnDisable = obtainStyledAttributes.getBoolean(C0622R.styleable.ButtonDropTarget_hideParentOnDisable, false);
        obtainStyledAttributes.recycle();
        this.mDragDistanceThreshold = resources.getDimensionPixelSize(C0622R.dimen.drag_distanceThreshold);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mText = getText();
        this.mOriginalTextColor = getTextColors();
    }

    /* access modifiers changed from: protected */
    public void setDrawable(int i) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(i, 0, 0, 0);
        this.mDrawable = getCompoundDrawablesRelative()[0];
    }

    public void setDropTargetBar(DropTargetBar dropTargetBar) {
        this.mDropTargetBar = dropTargetBar;
    }

    public final void onDragEnter(DragObject dragObject) {
        dragObject.dragView.setColor(this.mHoverColor);
        animateTextColor(this.mHoverColor);
        if (dragObject.stateAnnouncer != null) {
            dragObject.stateAnnouncer.cancel();
        }
        sendAccessibilityEvent(4);
    }

    /* access modifiers changed from: protected */
    public void resetHoverColor() {
        animateTextColor(this.mOriginalTextColor.getDefaultColor());
    }

    private void animateTextColor(int i) {
        if (this.mCurrentColorAnim != null) {
            this.mCurrentColorAnim.cancel();
        }
        this.mCurrentColorAnim = new AnimatorSet();
        this.mCurrentColorAnim.setDuration(120);
        if (this.mSrcFilter == null) {
            this.mSrcFilter = new ColorMatrix();
            this.mDstFilter = new ColorMatrix();
            this.mCurrentFilter = new ColorMatrix();
        }
        Themes.setColorScaleOnMatrix(getTextColor(), this.mSrcFilter);
        Themes.setColorScaleOnMatrix(i, this.mDstFilter);
        ValueAnimator ofObject = ValueAnimator.ofObject(new FloatArrayEvaluator(this.mCurrentFilter.getArray()), new Object[]{this.mSrcFilter.getArray(), this.mDstFilter.getArray()});
        ofObject.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ButtonDropTarget.this.mDrawable.setColorFilter(new ColorMatrixColorFilter(ButtonDropTarget.this.mCurrentFilter));
                ButtonDropTarget.this.invalidate();
            }
        });
        this.mCurrentColorAnim.play(ofObject);
        this.mCurrentColorAnim.play(ObjectAnimator.ofArgb(this, "textColor", new int[]{i}));
        this.mCurrentColorAnim.start();
    }

    public final void onDragExit(DragObject dragObject) {
        if (!dragObject.dragComplete) {
            dragObject.dragView.setColor(0);
            resetHoverColor();
            return;
        }
        dragObject.dragView.setColor(this.mHoverColor);
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        this.mActive = supportsDrop(dragObject.dragSource, dragObject.dragInfo);
        ButtonDropTarget buttonDropTarget = null;
        this.mDrawable.setColorFilter(null);
        if (this.mCurrentColorAnim != null) {
            this.mCurrentColorAnim.cancel();
            this.mCurrentColorAnim = null;
        }
        setTextColor(this.mOriginalTextColor);
        (this.mHideParentOnDisable ? (ViewGroup) getParent() : this).setVisibility(this.mActive ? 0 : 8);
        this.mAccessibleDrag = dragOptions.isAccessibleDrag;
        if (this.mAccessibleDrag) {
            buttonDropTarget = this;
        }
        setOnClickListener(buttonDropTarget);
    }

    public final boolean acceptDrop(DragObject dragObject) {
        return supportsDrop(dragObject.dragSource, dragObject.dragInfo);
    }

    public boolean isDropEnabled() {
        return this.mActive && (this.mAccessibleDrag || this.mLauncher.getDragController().getDistanceDragged() >= ((float) this.mDragDistanceThreshold));
    }

    public void onDragEnd() {
        this.mActive = false;
        setOnClickListener(null);
    }

    public void onDrop(DragObject dragObject) {
        final DragObject dragObject2 = dragObject;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect rect = new Rect();
        dragLayer.getViewRectRelativeToSelf(dragObject2.dragView, rect);
        Rect iconRect = getIconRect(dragObject);
        float width = ((float) iconRect.width()) / ((float) rect.width());
        this.mDropTargetBar.deferOnDragEnd();
        dragLayer.animateView(dragObject2.dragView, rect, iconRect, width, 1.0f, 1.0f, 0.1f, 0.1f, DRAG_VIEW_DROP_DURATION, new DecelerateInterpolator(2.0f), new LinearInterpolator(), new Runnable() {
            public void run() {
                ButtonDropTarget.this.completeDrop(dragObject2);
                ButtonDropTarget.this.mDropTargetBar.onDragEnd();
                ButtonDropTarget.this.mLauncher.exitSpringLoadedDragModeDelayed(true, 0, null);
            }
        }, 0, null);
    }

    public void getHitRectRelativeToDragLayer(Rect rect) {
        super.getHitRect(rect);
        rect.bottom += this.mBottomDragPadding;
        int[] iArr = new int[2];
        this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, iArr);
        rect.offsetTo(iArr[0], iArr[1]);
    }

    public Rect getIconRect(DragObject dragObject) {
        int i;
        int i2;
        int measuredWidth = dragObject.dragView.getMeasuredWidth();
        int measuredHeight = dragObject.dragView.getMeasuredHeight();
        int intrinsicWidth = this.mDrawable.getIntrinsicWidth();
        int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect rect = new Rect();
        dragLayer.getViewRectRelativeToSelf(this, rect);
        if (Utilities.isRtl(getResources())) {
            i2 = rect.right - getPaddingRight();
            i = i2 - intrinsicWidth;
        } else {
            i = getPaddingLeft() + rect.left;
            i2 = i + intrinsicWidth;
        }
        int measuredHeight2 = rect.top + ((getMeasuredHeight() - intrinsicHeight) / 2);
        rect.set(i, measuredHeight2, i2, measuredHeight2 + intrinsicHeight);
        rect.offset((-(measuredWidth - intrinsicWidth)) / 2, (-(measuredHeight - intrinsicHeight)) / 2);
        return rect;
    }

    public void onClick(View view) {
        this.mLauncher.getAccessibilityDelegate().handleAccessibleDrop(this, null, null);
    }

    public int getTextColor() {
        return getTextColors().getDefaultColor();
    }

    public boolean updateText(boolean z) {
        CharSequence charSequence;
        if ((z && getText().toString().isEmpty()) || (!z && this.mText.equals(getText()))) {
            return false;
        }
        if (z) {
            charSequence = "";
        } else {
            charSequence = this.mText;
        }
        setText(charSequence);
        return true;
    }

    public boolean isTextTruncated() {
        int measuredWidth = getMeasuredWidth();
        if (this.mHideParentOnDisable) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            measuredWidth = (viewGroup.getMeasuredWidth() - viewGroup.getPaddingLeft()) - viewGroup.getPaddingRight();
        }
        return !this.mText.equals(TextUtils.ellipsize(this.mText, getPaint(), (float) (measuredWidth - (((getPaddingLeft() + getPaddingRight()) + this.mDrawable.getIntrinsicWidth()) + getCompoundDrawablePadding())), TruncateAt.END));
    }
}
