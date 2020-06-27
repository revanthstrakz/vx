package com.kyleduo.switchbutton;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.p001v4.content.ContextCompat;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SwitchButton extends CompoundButton {
    private static int[] CHECKED_PRESSED_STATE = {16842912, 16842910, 16842919};
    public static final int DEFAULT_ANIMATION_DURATION = 250;
    public static final float DEFAULT_BACK_MEASURE_RATIO = 1.8f;
    public static final int DEFAULT_TEXT_MARGIN_DP = 2;
    public static final int DEFAULT_THUMB_MARGIN_DP = 2;
    public static final int DEFAULT_THUMB_SIZE_DP = 20;
    public static final int DEFAULT_TINT_COLOR = 3309506;
    private static int[] UNCHECKED_PRESSED_STATE = {-16842912, 16842910, 16842919};
    private long mAnimationDuration;
    private boolean mAutoAdjustTextPosition = true;
    private ColorStateList mBackColor;
    private Drawable mBackDrawable;
    private float mBackMeasureRatio;
    private float mBackRadius;
    private RectF mBackRectF;
    private OnCheckedChangeListener mChildOnCheckedChangeListener;
    private int mClickTimeout;
    private int mCurrBackColor;
    private int mCurrThumbColor;
    private Drawable mCurrentBackDrawable;
    private boolean mDrawDebugRect = false;
    private boolean mFadeBack;
    private boolean mIsBackUseDrawable;
    private boolean mIsThumbUseDrawable;
    private float mLastX;
    private int mNextBackColor;
    private Drawable mNextBackDrawable;
    private Layout mOffLayout;
    private int mOffTextColor;
    private Layout mOnLayout;
    private int mOnTextColor;
    private Paint mPaint;
    private RectF mPresentThumbRectF;
    private float mProcess;
    private ObjectAnimator mProcessAnimator;
    private Paint mRectPaint;
    private boolean mRestoring = false;
    private RectF mSafeRectF;
    private float mStartX;
    private float mStartY;
    private float mTextHeight;
    private float mTextMarginH;
    private CharSequence mTextOff;
    private RectF mTextOffRectF;
    private CharSequence mTextOn;
    private RectF mTextOnRectF;
    private TextPaint mTextPaint;
    private float mTextWidth;
    private ColorStateList mThumbColor;
    private Drawable mThumbDrawable;
    private RectF mThumbMargin;
    private float mThumbRadius;
    private RectF mThumbRectF;
    private PointF mThumbSizeF;
    private int mTintColor;
    private int mTouchSlop;

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        CharSequence offText;
        CharSequence onText;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.onText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.offText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            TextUtils.writeToParcel(this.onText, parcel, i);
            TextUtils.writeToParcel(this.offText, parcel, i);
        }
    }

    public SwitchButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet);
    }

    public SwitchButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public SwitchButton(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attributeSet) {
        boolean z;
        int i;
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        ColorStateList colorStateList;
        Drawable drawable;
        ColorStateList colorStateList2;
        float f6;
        float f7;
        float f8;
        float f9;
        Drawable drawable2;
        int i2;
        boolean z2;
        CharSequence charSequence;
        CharSequence charSequence2;
        float f10;
        float f11;
        TypedArray typedArray;
        float f12;
        boolean z3;
        AttributeSet attributeSet2 = attributeSet;
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();
        this.mPaint = new Paint(1);
        this.mRectPaint = new Paint(1);
        this.mRectPaint.setStyle(Style.STROKE);
        this.mRectPaint.setStrokeWidth(getResources().getDisplayMetrics().density);
        this.mTextPaint = getPaint();
        this.mThumbRectF = new RectF();
        this.mBackRectF = new RectF();
        this.mSafeRectF = new RectF();
        this.mThumbSizeF = new PointF();
        this.mThumbMargin = new RectF();
        this.mTextOnRectF = new RectF();
        this.mTextOffRectF = new RectF();
        this.mProcessAnimator = ObjectAnimator.ofFloat(this, "process", new float[]{0.0f, 0.0f}).setDuration(250);
        this.mProcessAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mPresentThumbRectF = new RectF();
        float f13 = getResources().getDisplayMetrics().density;
        float f14 = f13 * 2.0f;
        float f15 = f13 * 20.0f;
        float f16 = f15 / 2.0f;
        TypedArray obtainStyledAttributes = attributeSet2 == null ? null : getContext().obtainStyledAttributes(attributeSet2, C0960R.styleable.SwitchButton);
        if (obtainStyledAttributes != null) {
            drawable2 = obtainStyledAttributes.getDrawable(C0960R.styleable.SwitchButton_kswThumbDrawable);
            colorStateList = obtainStyledAttributes.getColorStateList(C0960R.styleable.SwitchButton_kswThumbColor);
            float dimension = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswThumbMargin, f14);
            float dimension2 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswThumbMarginLeft, dimension);
            f6 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswThumbMarginRight, dimension);
            float dimension3 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswThumbMarginTop, dimension);
            float dimension4 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswThumbMarginBottom, dimension);
            f5 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswThumbWidth, f15);
            float dimension5 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswThumbHeight, f15);
            float dimension6 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswThumbRadius, Math.min(f5, dimension5) / 2.0f);
            float dimension7 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswBackRadius, dimension6 + f14);
            drawable = obtainStyledAttributes.getDrawable(C0960R.styleable.SwitchButton_kswBackDrawable);
            colorStateList2 = obtainStyledAttributes.getColorStateList(C0960R.styleable.SwitchButton_kswBackColor);
            float f17 = dimension5;
            float f18 = dimension6;
            float f19 = obtainStyledAttributes.getFloat(C0960R.styleable.SwitchButton_kswBackMeasureRatio, 1.8f);
            int integer = obtainStyledAttributes.getInteger(C0960R.styleable.SwitchButton_kswAnimationDuration, 250);
            boolean z4 = obtainStyledAttributes.getBoolean(C0960R.styleable.SwitchButton_kswFadeBack, true);
            int color = obtainStyledAttributes.getColor(C0960R.styleable.SwitchButton_kswTintColor, 0);
            String string = obtainStyledAttributes.getString(C0960R.styleable.SwitchButton_kswTextOn);
            int i3 = color;
            CharSequence string2 = obtainStyledAttributes.getString(C0960R.styleable.SwitchButton_kswTextOff);
            f14 = obtainStyledAttributes.getDimension(C0960R.styleable.SwitchButton_kswTextMarginH, Math.max(f14, dimension7 / 2.0f));
            CharSequence charSequence3 = string;
            boolean z5 = obtainStyledAttributes.getBoolean(C0960R.styleable.SwitchButton_kswAutoAdjustTextPosition, true);
            obtainStyledAttributes.recycle();
            f = dimension7;
            f4 = dimension2;
            f8 = dimension4;
            f9 = f17;
            f2 = f18;
            f3 = f19;
            i = integer;
            z = z4;
            i2 = i3;
            charSequence = string2;
            f7 = dimension3;
            z2 = z5;
            charSequence2 = charSequence3;
        } else {
            f9 = f15;
            f5 = f9;
            f2 = f16;
            f = f2;
            charSequence2 = null;
            charSequence = null;
            z2 = true;
            i2 = 0;
            drawable2 = null;
            f8 = 0.0f;
            f7 = 0.0f;
            f6 = 0.0f;
            colorStateList2 = null;
            drawable = null;
            colorStateList = null;
            f4 = 0.0f;
            f3 = 1.8f;
            i = 250;
            z = true;
        }
        if (attributeSet2 == null) {
            f11 = f8;
            f10 = f7;
            typedArray = null;
        } else {
            f11 = f8;
            f10 = f7;
            typedArray = getContext().obtainStyledAttributes(attributeSet2, new int[]{16842970, 16842981});
        }
        if (typedArray != null) {
            f12 = f6;
            boolean z6 = typedArray.getBoolean(0, true);
            boolean z7 = typedArray.getBoolean(1, z6);
            setFocusable(z6);
            setClickable(z7);
            typedArray.recycle();
        } else {
            f12 = f6;
            setFocusable(true);
            setClickable(true);
        }
        this.mTextOn = charSequence2;
        this.mTextOff = charSequence;
        this.mTextMarginH = f14;
        this.mAutoAdjustTextPosition = z2;
        this.mThumbDrawable = drawable2;
        this.mThumbColor = colorStateList;
        this.mIsThumbUseDrawable = this.mThumbDrawable != null;
        this.mTintColor = i2;
        if (this.mTintColor == 0) {
            TypedValue typedValue = new TypedValue();
            z3 = true;
            if (getContext().getTheme().resolveAttribute(C0960R.attr.colorAccent, typedValue, true)) {
                this.mTintColor = typedValue.data;
            } else {
                this.mTintColor = DEFAULT_TINT_COLOR;
            }
        } else {
            z3 = true;
        }
        if (!this.mIsThumbUseDrawable && this.mThumbColor == null) {
            this.mThumbColor = ColorUtils.generateThumbColorWithTintColor(this.mTintColor);
            this.mCurrThumbColor = this.mThumbColor.getDefaultColor();
        }
        if (this.mIsThumbUseDrawable) {
            f5 = Math.max(f5, (float) this.mThumbDrawable.getMinimumWidth());
            f9 = Math.max(f9, (float) this.mThumbDrawable.getMinimumHeight());
        }
        this.mThumbSizeF.set(f5, f9);
        this.mBackDrawable = drawable;
        this.mBackColor = colorStateList2;
        if (this.mBackDrawable == null) {
            z3 = false;
        }
        this.mIsBackUseDrawable = z3;
        if (!this.mIsBackUseDrawable && this.mBackColor == null) {
            this.mBackColor = ColorUtils.generateBackColorWithTintColor(this.mTintColor);
            this.mCurrBackColor = this.mBackColor.getDefaultColor();
            this.mNextBackColor = this.mBackColor.getColorForState(CHECKED_PRESSED_STATE, this.mCurrBackColor);
        }
        this.mThumbMargin.set(f4, f10, f12, f11);
        if (this.mThumbMargin.width() >= 0.0f) {
            f3 = Math.max(f3, 1.0f);
        }
        this.mBackMeasureRatio = f3;
        this.mThumbRadius = f2;
        this.mBackRadius = f;
        this.mAnimationDuration = (long) i;
        this.mFadeBack = z;
        this.mProcessAnimator.setDuration(this.mAnimationDuration);
        if (isChecked()) {
            setProcess(1.0f);
        }
    }

    private Layout makeLayout(CharSequence charSequence) {
        StaticLayout staticLayout = new StaticLayout(charSequence, this.mTextPaint, (int) Math.ceil((double) Layout.getDesiredWidth(charSequence, this.mTextPaint)), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        return staticLayout;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.mOnLayout == null && this.mTextOn != null) {
            this.mOnLayout = makeLayout(this.mTextOn);
        }
        if (this.mOffLayout == null && this.mTextOff != null) {
            this.mOffLayout = makeLayout(this.mTextOff);
        }
        setMeasuredDimension(measureWidth(i), measureHeight(i2));
    }

    private int measureWidth(int i) {
        int size = MeasureSpec.getSize(i);
        int mode = MeasureSpec.getMode(i);
        int ceil = ceil((double) (this.mThumbSizeF.x * this.mBackMeasureRatio));
        if (this.mIsBackUseDrawable) {
            ceil = Math.max(ceil, this.mBackDrawable.getMinimumWidth());
        }
        float width = this.mOnLayout != null ? (float) this.mOnLayout.getWidth() : 0.0f;
        float width2 = this.mOffLayout != null ? (float) this.mOffLayout.getWidth() : 0.0f;
        if (width == 0.0f && width2 == 0.0f) {
            this.mTextWidth = 0.0f;
        } else {
            this.mTextWidth = Math.max(width, width2) + (this.mTextMarginH * 2.0f);
            float f = (float) ceil;
            float f2 = f - this.mThumbSizeF.x;
            if (f2 < this.mTextWidth) {
                ceil = (int) (f + (this.mTextWidth - f2));
            }
        }
        int max = Math.max(ceil, ceil((double) (((float) ceil) + this.mThumbMargin.left + this.mThumbMargin.right)));
        int max2 = Math.max(Math.max(max, getPaddingLeft() + max + getPaddingRight()), getSuggestedMinimumWidth());
        if (mode == 1073741824) {
            return Math.max(max2, size);
        }
        return mode == Integer.MIN_VALUE ? Math.min(max2, size) : max2;
    }

    private int measureHeight(int i) {
        int mode = MeasureSpec.getMode(i);
        int size = MeasureSpec.getSize(i);
        int ceil = ceil((double) Math.max(this.mThumbSizeF.y, this.mThumbSizeF.y + this.mThumbMargin.top + this.mThumbMargin.right));
        float height = this.mOnLayout != null ? (float) this.mOnLayout.getHeight() : 0.0f;
        float height2 = this.mOffLayout != null ? (float) this.mOffLayout.getHeight() : 0.0f;
        if (height == 0.0f && height2 == 0.0f) {
            this.mTextHeight = 0.0f;
        } else {
            this.mTextHeight = Math.max(height, height2);
            ceil = ceil((double) Math.max((float) ceil, this.mTextHeight));
        }
        int max = Math.max(ceil, getSuggestedMinimumHeight());
        int max2 = Math.max(max, getPaddingTop() + max + getPaddingBottom());
        if (mode == 1073741824) {
            return Math.max(max2, size);
        }
        return mode == Integer.MIN_VALUE ? Math.min(max2, size) : max2;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            setup();
        }
    }

    private int ceil(double d) {
        return (int) Math.ceil(d);
    }

    private void setup() {
        float f = 0.0f;
        float paddingTop = ((float) getPaddingTop()) + Math.max(0.0f, this.mThumbMargin.top);
        float paddingLeft = ((float) getPaddingLeft()) + Math.max(0.0f, this.mThumbMargin.left);
        if (!(this.mOnLayout == null || this.mOffLayout == null || this.mThumbMargin.top + this.mThumbMargin.bottom <= 0.0f)) {
            paddingTop += (((((float) ((getMeasuredHeight() - getPaddingBottom()) - getPaddingTop())) - this.mThumbSizeF.y) - this.mThumbMargin.top) - this.mThumbMargin.bottom) / 2.0f;
        }
        if (this.mIsThumbUseDrawable) {
            this.mThumbSizeF.x = Math.max(this.mThumbSizeF.x, (float) this.mThumbDrawable.getMinimumWidth());
            this.mThumbSizeF.y = Math.max(this.mThumbSizeF.y, (float) this.mThumbDrawable.getMinimumHeight());
        }
        this.mThumbRectF.set(paddingLeft, paddingTop, this.mThumbSizeF.x + paddingLeft, this.mThumbSizeF.y + paddingTop);
        float f2 = this.mThumbRectF.left - this.mThumbMargin.left;
        float min = Math.min(0.0f, ((Math.max(this.mThumbSizeF.x * this.mBackMeasureRatio, this.mThumbSizeF.x + this.mTextWidth) - this.mThumbRectF.width()) - this.mTextWidth) / 2.0f);
        float min2 = Math.min(0.0f, (((this.mThumbRectF.height() + this.mThumbMargin.top) + this.mThumbMargin.bottom) - this.mTextHeight) / 2.0f);
        this.mBackRectF.set(f2 + min, (this.mThumbRectF.top - this.mThumbMargin.top) + min2, (((f2 + this.mThumbMargin.left) + Math.max(this.mThumbSizeF.x * this.mBackMeasureRatio, this.mThumbSizeF.x + this.mTextWidth)) + this.mThumbMargin.right) - min, (this.mThumbRectF.bottom + this.mThumbMargin.bottom) - min2);
        this.mSafeRectF.set(this.mThumbRectF.left, 0.0f, (this.mBackRectF.right - this.mThumbMargin.right) - this.mThumbRectF.width(), 0.0f);
        this.mBackRadius = Math.min(Math.min(this.mBackRectF.width(), this.mBackRectF.height()) / 2.0f, this.mBackRadius);
        if (this.mBackDrawable != null) {
            this.mBackDrawable.setBounds((int) this.mBackRectF.left, (int) this.mBackRectF.top, ceil((double) this.mBackRectF.right), ceil((double) this.mBackRectF.bottom));
        }
        if (this.mOnLayout != null) {
            float width = this.mBackRectF.left + ((((this.mBackRectF.width() - this.mThumbRectF.width()) - this.mThumbMargin.right) - ((float) this.mOnLayout.getWidth())) / 2.0f) + (this.mThumbMargin.left < 0.0f ? this.mThumbMargin.left * -0.5f : 0.0f);
            if (!this.mIsBackUseDrawable && this.mAutoAdjustTextPosition) {
                width += this.mBackRadius / 4.0f;
            }
            float height = this.mBackRectF.top + ((this.mBackRectF.height() - ((float) this.mOnLayout.getHeight())) / 2.0f);
            this.mTextOnRectF.set(width, height, ((float) this.mOnLayout.getWidth()) + width, ((float) this.mOnLayout.getHeight()) + height);
        }
        if (this.mOffLayout != null) {
            float width2 = (this.mBackRectF.right - ((((this.mBackRectF.width() - this.mThumbRectF.width()) - this.mThumbMargin.left) - ((float) this.mOffLayout.getWidth())) / 2.0f)) - ((float) this.mOffLayout.getWidth());
            if (this.mThumbMargin.right < 0.0f) {
                f = this.mThumbMargin.right * 0.5f;
            }
            float f3 = width2 + f;
            if (!this.mIsBackUseDrawable && this.mAutoAdjustTextPosition) {
                f3 -= this.mBackRadius / 4.0f;
            }
            float height2 = this.mBackRectF.top + ((this.mBackRectF.height() - ((float) this.mOffLayout.getHeight())) / 2.0f);
            this.mTextOffRectF.set(f3, height2, ((float) this.mOffLayout.getWidth()) + f3, ((float) this.mOffLayout.getHeight()) + height2);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x012f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r13) {
        /*
            r12 = this;
            super.onDraw(r13)
            boolean r0 = r12.mIsBackUseDrawable
            r1 = 1132396544(0x437f0000, float:255.0)
            r2 = 255(0xff, float:3.57E-43)
            if (r0 == 0) goto L_0x0050
            boolean r0 = r12.mFadeBack
            if (r0 == 0) goto L_0x0044
            android.graphics.drawable.Drawable r0 = r12.mCurrentBackDrawable
            if (r0 == 0) goto L_0x0044
            android.graphics.drawable.Drawable r0 = r12.mNextBackDrawable
            if (r0 == 0) goto L_0x0044
            boolean r0 = r12.isChecked()
            if (r0 == 0) goto L_0x0020
            android.graphics.drawable.Drawable r0 = r12.mCurrentBackDrawable
            goto L_0x0022
        L_0x0020:
            android.graphics.drawable.Drawable r0 = r12.mNextBackDrawable
        L_0x0022:
            boolean r3 = r12.isChecked()
            if (r3 == 0) goto L_0x002b
            android.graphics.drawable.Drawable r3 = r12.mNextBackDrawable
            goto L_0x002d
        L_0x002b:
            android.graphics.drawable.Drawable r3 = r12.mCurrentBackDrawable
        L_0x002d:
            float r4 = r12.getProcess()
            float r4 = r4 * r1
            int r4 = (int) r4
            r0.setAlpha(r4)
            r0.draw(r13)
            int r0 = 255 - r4
            r3.setAlpha(r0)
            r3.draw(r13)
            goto L_0x00d1
        L_0x0044:
            android.graphics.drawable.Drawable r0 = r12.mBackDrawable
            r0.setAlpha(r2)
            android.graphics.drawable.Drawable r0 = r12.mBackDrawable
            r0.draw(r13)
            goto L_0x00d1
        L_0x0050:
            boolean r0 = r12.mFadeBack
            if (r0 == 0) goto L_0x00bf
            boolean r0 = r12.isChecked()
            if (r0 == 0) goto L_0x005d
            int r0 = r12.mCurrBackColor
            goto L_0x005f
        L_0x005d:
            int r0 = r12.mNextBackColor
        L_0x005f:
            boolean r3 = r12.isChecked()
            if (r3 == 0) goto L_0x0068
            int r3 = r12.mNextBackColor
            goto L_0x006a
        L_0x0068:
            int r3 = r12.mCurrBackColor
        L_0x006a:
            float r4 = r12.getProcess()
            float r4 = r4 * r1
            int r4 = (int) r4
            int r5 = android.graphics.Color.alpha(r0)
            int r5 = r5 * r4
            int r5 = r5 / r2
            android.graphics.Paint r6 = r12.mPaint
            int r7 = android.graphics.Color.red(r0)
            int r8 = android.graphics.Color.green(r0)
            int r0 = android.graphics.Color.blue(r0)
            r6.setARGB(r5, r7, r8, r0)
            android.graphics.RectF r0 = r12.mBackRectF
            float r5 = r12.mBackRadius
            float r6 = r12.mBackRadius
            android.graphics.Paint r7 = r12.mPaint
            r13.drawRoundRect(r0, r5, r6, r7)
            int r0 = 255 - r4
            int r4 = android.graphics.Color.alpha(r3)
            int r4 = r4 * r0
            int r4 = r4 / r2
            android.graphics.Paint r0 = r12.mPaint
            int r5 = android.graphics.Color.red(r3)
            int r6 = android.graphics.Color.green(r3)
            int r3 = android.graphics.Color.blue(r3)
            r0.setARGB(r4, r5, r6, r3)
            android.graphics.RectF r0 = r12.mBackRectF
            float r3 = r12.mBackRadius
            float r4 = r12.mBackRadius
            android.graphics.Paint r5 = r12.mPaint
            r13.drawRoundRect(r0, r3, r4, r5)
            android.graphics.Paint r0 = r12.mPaint
            r0.setAlpha(r2)
            goto L_0x00d1
        L_0x00bf:
            android.graphics.Paint r0 = r12.mPaint
            int r3 = r12.mCurrBackColor
            r0.setColor(r3)
            android.graphics.RectF r0 = r12.mBackRectF
            float r3 = r12.mBackRadius
            float r4 = r12.mBackRadius
            android.graphics.Paint r5 = r12.mPaint
            r13.drawRoundRect(r0, r3, r4, r5)
        L_0x00d1:
            float r0 = r12.getProcess()
            double r3 = (double) r0
            r5 = 4602678819172646912(0x3fe0000000000000, double:0.5)
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x00df
            android.text.Layout r0 = r12.mOnLayout
            goto L_0x00e1
        L_0x00df:
            android.text.Layout r0 = r12.mOffLayout
        L_0x00e1:
            float r3 = r12.getProcess()
            double r3 = (double) r3
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x00ed
            android.graphics.RectF r3 = r12.mTextOnRectF
            goto L_0x00ef
        L_0x00ed:
            android.graphics.RectF r3 = r12.mTextOffRectF
        L_0x00ef:
            r4 = 0
            if (r0 == 0) goto L_0x015b
            if (r3 == 0) goto L_0x015b
            float r7 = r12.getProcess()
            double r7 = (double) r7
            r9 = 4604930618986332160(0x3fe8000000000000, double:0.75)
            r11 = 1082130432(0x40800000, float:4.0)
            int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r7 < 0) goto L_0x010b
            float r7 = r12.getProcess()
            float r7 = r7 * r11
            r8 = 1077936128(0x40400000, float:3.0)
        L_0x0109:
            float r7 = r7 - r8
            goto L_0x0120
        L_0x010b:
            float r7 = r12.getProcess()
            double r7 = (double) r7
            r9 = 4598175219545276416(0x3fd0000000000000, double:0.25)
            int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r7 >= 0) goto L_0x011f
            r7 = 1065353216(0x3f800000, float:1.0)
            float r8 = r12.getProcess()
            float r8 = r8 * r11
            goto L_0x0109
        L_0x011f:
            r7 = 0
        L_0x0120:
            float r1 = r1 * r7
            int r1 = (int) r1
            float r7 = r12.getProcess()
            double r7 = (double) r7
            int r7 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x012f
            int r7 = r12.mOnTextColor
            goto L_0x0131
        L_0x012f:
            int r7 = r12.mOffTextColor
        L_0x0131:
            int r8 = android.graphics.Color.alpha(r7)
            int r8 = r8 * r1
            int r8 = r8 / r2
            android.text.TextPaint r1 = r0.getPaint()
            int r2 = android.graphics.Color.red(r7)
            int r9 = android.graphics.Color.green(r7)
            int r7 = android.graphics.Color.blue(r7)
            r1.setARGB(r8, r2, r9, r7)
            r13.save()
            float r1 = r3.left
            float r2 = r3.top
            r13.translate(r1, r2)
            r0.draw(r13)
            r13.restore()
        L_0x015b:
            android.graphics.RectF r0 = r12.mPresentThumbRectF
            android.graphics.RectF r1 = r12.mThumbRectF
            r0.set(r1)
            android.graphics.RectF r0 = r12.mPresentThumbRectF
            float r1 = r12.mProcess
            android.graphics.RectF r2 = r12.mSafeRectF
            float r2 = r2.width()
            float r1 = r1 * r2
            r0.offset(r1, r4)
            boolean r0 = r12.mIsThumbUseDrawable
            if (r0 == 0) goto L_0x019c
            android.graphics.drawable.Drawable r0 = r12.mThumbDrawable
            android.graphics.RectF r1 = r12.mPresentThumbRectF
            float r1 = r1.left
            int r1 = (int) r1
            android.graphics.RectF r2 = r12.mPresentThumbRectF
            float r2 = r2.top
            int r2 = (int) r2
            android.graphics.RectF r3 = r12.mPresentThumbRectF
            float r3 = r3.right
            double r3 = (double) r3
            int r3 = r12.ceil(r3)
            android.graphics.RectF r4 = r12.mPresentThumbRectF
            float r4 = r4.bottom
            double r7 = (double) r4
            int r4 = r12.ceil(r7)
            r0.setBounds(r1, r2, r3, r4)
            android.graphics.drawable.Drawable r0 = r12.mThumbDrawable
            r0.draw(r13)
            goto L_0x01ae
        L_0x019c:
            android.graphics.Paint r0 = r12.mPaint
            int r1 = r12.mCurrThumbColor
            r0.setColor(r1)
            android.graphics.RectF r0 = r12.mPresentThumbRectF
            float r1 = r12.mThumbRadius
            float r2 = r12.mThumbRadius
            android.graphics.Paint r3 = r12.mPaint
            r13.drawRoundRect(r0, r1, r2, r3)
        L_0x01ae:
            boolean r0 = r12.mDrawDebugRect
            if (r0 == 0) goto L_0x01f4
            android.graphics.Paint r0 = r12.mRectPaint
            java.lang.String r1 = "#AA0000"
            int r1 = android.graphics.Color.parseColor(r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r12.mBackRectF
            android.graphics.Paint r1 = r12.mRectPaint
            r13.drawRect(r0, r1)
            android.graphics.Paint r0 = r12.mRectPaint
            java.lang.String r1 = "#0000FF"
            int r1 = android.graphics.Color.parseColor(r1)
            r0.setColor(r1)
            android.graphics.RectF r0 = r12.mPresentThumbRectF
            android.graphics.Paint r1 = r12.mRectPaint
            r13.drawRect(r0, r1)
            android.graphics.Paint r0 = r12.mRectPaint
            java.lang.String r1 = "#00CC00"
            int r1 = android.graphics.Color.parseColor(r1)
            r0.setColor(r1)
            float r0 = r12.getProcess()
            double r0 = (double) r0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x01ed
            android.graphics.RectF r0 = r12.mTextOnRectF
            goto L_0x01ef
        L_0x01ed:
            android.graphics.RectF r0 = r12.mTextOffRectF
        L_0x01ef:
            android.graphics.Paint r1 = r12.mRectPaint
            r13.drawRect(r0, r1)
        L_0x01f4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.kyleduo.switchbutton.SwitchButton.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mIsThumbUseDrawable || this.mThumbColor == null) {
            setDrawableState(this.mThumbDrawable);
        } else {
            this.mCurrThumbColor = this.mThumbColor.getColorForState(getDrawableState(), this.mCurrThumbColor);
        }
        int[] iArr = isChecked() ? UNCHECKED_PRESSED_STATE : CHECKED_PRESSED_STATE;
        ColorStateList textColors = getTextColors();
        if (textColors != null) {
            int defaultColor = textColors.getDefaultColor();
            this.mOnTextColor = textColors.getColorForState(CHECKED_PRESSED_STATE, defaultColor);
            this.mOffTextColor = textColors.getColorForState(UNCHECKED_PRESSED_STATE, defaultColor);
        }
        if (this.mIsBackUseDrawable || this.mBackColor == null) {
            if (!(this.mBackDrawable instanceof StateListDrawable) || !this.mFadeBack) {
                this.mNextBackDrawable = null;
            } else {
                this.mBackDrawable.setState(iArr);
                this.mNextBackDrawable = this.mBackDrawable.getCurrent().mutate();
            }
            setDrawableState(this.mBackDrawable);
            if (this.mBackDrawable != null) {
                this.mCurrentBackDrawable = this.mBackDrawable.getCurrent().mutate();
                return;
            }
            return;
        }
        this.mCurrBackColor = this.mBackColor.getColorForState(getDrawableState(), this.mCurrBackColor);
        this.mNextBackColor = this.mBackColor.getColorForState(iArr, this.mCurrBackColor);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled() || !isClickable() || !isFocusable()) {
            return false;
        }
        float x = motionEvent.getX() - this.mStartX;
        float y = motionEvent.getY() - this.mStartY;
        switch (motionEvent.getAction()) {
            case 0:
                catchView();
                this.mStartX = motionEvent.getX();
                this.mStartY = motionEvent.getY();
                this.mLastX = this.mStartX;
                setPressed(true);
                break;
            case 1:
            case 3:
                setPressed(false);
                boolean statusBasedOnPos = getStatusBasedOnPos();
                float eventTime = (float) (motionEvent.getEventTime() - motionEvent.getDownTime());
                if (x >= ((float) this.mTouchSlop) || y >= ((float) this.mTouchSlop) || eventTime >= ((float) this.mClickTimeout)) {
                    if (statusBasedOnPos == isChecked()) {
                        animateToState(statusBasedOnPos);
                        break;
                    } else {
                        playSoundEffect(0);
                        setChecked(statusBasedOnPos);
                        break;
                    }
                } else {
                    performClick();
                    break;
                }
                break;
            case 2:
                float x2 = motionEvent.getX();
                setProcess(getProcess() + ((x2 - this.mLastX) / this.mSafeRectF.width()));
                this.mLastX = x2;
                break;
        }
        return true;
    }

    private boolean getStatusBasedOnPos() {
        return getProcess() > 0.5f;
    }

    public final float getProcess() {
        return this.mProcess;
    }

    public final void setProcess(float f) {
        if (f > 1.0f) {
            f = 1.0f;
        } else if (f < 0.0f) {
            f = 0.0f;
        }
        this.mProcess = f;
        invalidate();
    }

    public boolean performClick() {
        return super.performClick();
    }

    /* access modifiers changed from: protected */
    public void animateToState(boolean z) {
        if (this.mProcessAnimator != null) {
            if (this.mProcessAnimator.isRunning()) {
                this.mProcessAnimator.cancel();
            }
            this.mProcessAnimator.setDuration(this.mAnimationDuration);
            if (z) {
                this.mProcessAnimator.setFloatValues(new float[]{this.mProcess, 1.0f});
            } else {
                this.mProcessAnimator.setFloatValues(new float[]{this.mProcess, 0.0f});
            }
            this.mProcessAnimator.start();
        }
    }

    private void catchView() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    public void setChecked(boolean z) {
        if (isChecked() != z) {
            animateToState(z);
        }
        if (this.mRestoring) {
            setCheckedImmediatelyNoEvent(z);
        } else {
            super.setChecked(z);
        }
    }

    public void setCheckedNoEvent(boolean z) {
        if (this.mChildOnCheckedChangeListener == null) {
            setChecked(z);
            return;
        }
        super.setOnCheckedChangeListener(null);
        setChecked(z);
        super.setOnCheckedChangeListener(this.mChildOnCheckedChangeListener);
    }

    public void setCheckedImmediatelyNoEvent(boolean z) {
        if (this.mChildOnCheckedChangeListener == null) {
            setCheckedImmediately(z);
            return;
        }
        super.setOnCheckedChangeListener(null);
        setCheckedImmediately(z);
        super.setOnCheckedChangeListener(this.mChildOnCheckedChangeListener);
    }

    public void toggleNoEvent() {
        if (this.mChildOnCheckedChangeListener == null) {
            toggle();
            return;
        }
        super.setOnCheckedChangeListener(null);
        toggle();
        super.setOnCheckedChangeListener(this.mChildOnCheckedChangeListener);
    }

    public void toggleImmediatelyNoEvent() {
        if (this.mChildOnCheckedChangeListener == null) {
            toggleImmediately();
            return;
        }
        super.setOnCheckedChangeListener(null);
        toggleImmediately();
        super.setOnCheckedChangeListener(this.mChildOnCheckedChangeListener);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        super.setOnCheckedChangeListener(onCheckedChangeListener);
        this.mChildOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setCheckedImmediately(boolean z) {
        super.setChecked(z);
        if (this.mProcessAnimator != null && this.mProcessAnimator.isRunning()) {
            this.mProcessAnimator.cancel();
        }
        setProcess(z ? 1.0f : 0.0f);
        invalidate();
    }

    public void toggleImmediately() {
        setCheckedImmediately(!isChecked());
    }

    private void setDrawableState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(getDrawableState());
            invalidate();
        }
    }

    public boolean isDrawDebugRect() {
        return this.mDrawDebugRect;
    }

    public void setDrawDebugRect(boolean z) {
        this.mDrawDebugRect = z;
        invalidate();
    }

    public long getAnimationDuration() {
        return this.mAnimationDuration;
    }

    public void setAnimationDuration(long j) {
        this.mAnimationDuration = j;
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public void setThumbDrawable(Drawable drawable) {
        this.mThumbDrawable = drawable;
        this.mIsThumbUseDrawable = this.mThumbDrawable != null;
        setup();
        refreshDrawableState();
        requestLayout();
        invalidate();
    }

    public void setThumbDrawableRes(int i) {
        setThumbDrawable(ContextCompat.getDrawable(getContext(), i));
    }

    public Drawable getBackDrawable() {
        return this.mBackDrawable;
    }

    public void setBackDrawable(Drawable drawable) {
        this.mBackDrawable = drawable;
        this.mIsBackUseDrawable = this.mBackDrawable != null;
        setup();
        refreshDrawableState();
        requestLayout();
        invalidate();
    }

    public void setBackDrawableRes(int i) {
        setBackDrawable(ContextCompat.getDrawable(getContext(), i));
    }

    public ColorStateList getBackColor() {
        return this.mBackColor;
    }

    public void setBackColor(ColorStateList colorStateList) {
        this.mBackColor = colorStateList;
        if (this.mBackColor != null) {
            setBackDrawable(null);
        }
        invalidate();
    }

    public void setBackColorRes(int i) {
        setBackColor(ContextCompat.getColorStateList(getContext(), i));
    }

    public ColorStateList getThumbColor() {
        return this.mThumbColor;
    }

    public void setThumbColor(ColorStateList colorStateList) {
        this.mThumbColor = colorStateList;
        if (this.mThumbColor != null) {
            setThumbDrawable(null);
        }
    }

    public void setThumbColorRes(int i) {
        setThumbColor(ContextCompat.getColorStateList(getContext(), i));
    }

    public float getBackMeasureRatio() {
        return this.mBackMeasureRatio;
    }

    public void setBackMeasureRatio(float f) {
        this.mBackMeasureRatio = f;
        requestLayout();
    }

    public RectF getThumbMargin() {
        return this.mThumbMargin;
    }

    public void setThumbMargin(RectF rectF) {
        if (rectF == null) {
            setThumbMargin(0.0f, 0.0f, 0.0f, 0.0f);
        } else {
            setThumbMargin(rectF.left, rectF.top, rectF.right, rectF.bottom);
        }
    }

    public void setThumbMargin(float f, float f2, float f3, float f4) {
        this.mThumbMargin.set(f, f2, f3, f4);
        requestLayout();
    }

    public void setThumbSize(float f, float f2) {
        this.mThumbSizeF.set(f, f2);
        setup();
        requestLayout();
    }

    public float getThumbWidth() {
        return this.mThumbSizeF.x;
    }

    public float getThumbHeight() {
        return this.mThumbSizeF.y;
    }

    public void setThumbSize(PointF pointF) {
        if (pointF == null) {
            float f = getResources().getDisplayMetrics().density * 20.0f;
            setThumbSize(f, f);
            return;
        }
        setThumbSize(pointF.x, pointF.y);
    }

    public PointF getThumbSizeF() {
        return this.mThumbSizeF;
    }

    public float getThumbRadius() {
        return this.mThumbRadius;
    }

    public void setThumbRadius(float f) {
        this.mThumbRadius = f;
        if (!this.mIsThumbUseDrawable) {
            invalidate();
        }
    }

    public PointF getBackSizeF() {
        return new PointF(this.mBackRectF.width(), this.mBackRectF.height());
    }

    public float getBackRadius() {
        return this.mBackRadius;
    }

    public void setBackRadius(float f) {
        this.mBackRadius = f;
        if (!this.mIsBackUseDrawable) {
            invalidate();
        }
    }

    public boolean isFadeBack() {
        return this.mFadeBack;
    }

    public void setFadeBack(boolean z) {
        this.mFadeBack = z;
    }

    public int getTintColor() {
        return this.mTintColor;
    }

    public void setTintColor(int i) {
        this.mTintColor = i;
        this.mThumbColor = ColorUtils.generateThumbColorWithTintColor(this.mTintColor);
        this.mBackColor = ColorUtils.generateBackColorWithTintColor(this.mTintColor);
        this.mIsBackUseDrawable = false;
        this.mIsThumbUseDrawable = false;
        refreshDrawableState();
        invalidate();
    }

    public void setText(CharSequence charSequence, CharSequence charSequence2) {
        this.mTextOn = charSequence;
        this.mTextOff = charSequence2;
        this.mOnLayout = null;
        this.mOffLayout = null;
        requestLayout();
        invalidate();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.onText = this.mTextOn;
        savedState.offText = this.mTextOff;
        return savedState;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        setText(savedState.onText, savedState.offText);
        this.mRestoring = true;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mRestoring = false;
    }
}
