package com.android.launcher3.pageindicators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View.AccessibilityDelegate;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.dynamicui.ExtractedColors;
import com.android.launcher3.dynamicui.WallpaperColorInfo;

public class PageIndicatorLineCaret extends PageIndicator {
    public static final int BLACK_ALPHA = 165;
    private static final int LINE_ALPHA_ANIMATOR_INDEX = 0;
    private static final int LINE_ANIMATE_DURATION = ViewConfiguration.getScrollBarFadeDuration();
    private static final int LINE_FADE_DELAY = ViewConfiguration.getScrollDefaultDelay();
    private static final Property<PageIndicatorLineCaret, Float> NUM_PAGES = new Property<PageIndicatorLineCaret, Float>(Float.class, "num_pages") {
        public Float get(PageIndicatorLineCaret pageIndicatorLineCaret) {
            return Float.valueOf(pageIndicatorLineCaret.mNumPagesFloat);
        }

        public void set(PageIndicatorLineCaret pageIndicatorLineCaret, Float f) {
            pageIndicatorLineCaret.mNumPagesFloat = f.floatValue();
            pageIndicatorLineCaret.invalidate();
        }
    };
    private static final int NUM_PAGES_ANIMATOR_INDEX = 1;
    private static final Property<PageIndicatorLineCaret, Integer> PAINT_ALPHA = new Property<PageIndicatorLineCaret, Integer>(Integer.class, "paint_alpha") {
        public Integer get(PageIndicatorLineCaret pageIndicatorLineCaret) {
            return Integer.valueOf(pageIndicatorLineCaret.mLinePaint.getAlpha());
        }

        public void set(PageIndicatorLineCaret pageIndicatorLineCaret, Integer num) {
            pageIndicatorLineCaret.mLinePaint.setAlpha(num.intValue());
            pageIndicatorLineCaret.invalidate();
        }
    };
    private static final String TAG = "PageIndicatorLine";
    private static final Property<PageIndicatorLineCaret, Integer> TOTAL_SCROLL = new Property<PageIndicatorLineCaret, Integer>(Integer.class, "total_scroll") {
        public Integer get(PageIndicatorLineCaret pageIndicatorLineCaret) {
            return Integer.valueOf(pageIndicatorLineCaret.mTotalScroll);
        }

        public void set(PageIndicatorLineCaret pageIndicatorLineCaret, Integer num) {
            pageIndicatorLineCaret.mTotalScroll = num.intValue();
            pageIndicatorLineCaret.invalidate();
        }
    };
    private static final int TOTAL_SCROLL_ANIMATOR_INDEX = 2;
    public static final int WHITE_ALPHA = 178;
    private static final int[] sTempCoords = new int[2];
    private int mActiveAlpha;
    private ImageView mAllAppsHandle;
    /* access modifiers changed from: private */
    public ValueAnimator[] mAnimators;
    private int mCurrentScroll;
    private final Handler mDelayedLineFadeHandler;
    private Runnable mHideLineRunnable;
    private Launcher mLauncher;
    private final int mLineHeight;
    /* access modifiers changed from: private */
    public Paint mLinePaint;
    /* access modifiers changed from: private */
    public float mNumPagesFloat;
    private boolean mShouldAutoHide;
    private int mToAlpha;
    /* access modifiers changed from: private */
    public int mTotalScroll;

    public void setActiveMarker(int i) {
    }

    public void updateColor(ExtractedColors extractedColors) {
    }

    public PageIndicatorLineCaret(Context context) {
        this(context, null);
    }

    public PageIndicatorLineCaret(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PageIndicatorLineCaret(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAnimators = new ValueAnimator[3];
        this.mDelayedLineFadeHandler = new Handler(Looper.getMainLooper());
        this.mShouldAutoHide = true;
        this.mActiveAlpha = 0;
        this.mHideLineRunnable = new Runnable() {
            public void run() {
                PageIndicatorLineCaret.this.animateLineToAlpha(0);
            }
        };
        Resources resources = context.getResources();
        this.mLinePaint = new Paint();
        this.mLinePaint.setAlpha(0);
        this.mLauncher = Launcher.getLauncher(context);
        this.mLineHeight = resources.getDimensionPixelSize(C0622R.dimen.dynamic_grid_page_indicator_line_height);
        setCaretDrawable(new CaretDrawable(context));
        boolean supportsDarkText = WallpaperColorInfo.getInstance(context).supportsDarkText();
        this.mActiveAlpha = supportsDarkText ? BLACK_ALPHA : WHITE_ALPHA;
        this.mLinePaint.setColor(supportsDarkText ? -16777216 : -1);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mAllAppsHandle = (ImageView) findViewById(C0622R.C0625id.all_apps_handle);
        this.mAllAppsHandle.setImageDrawable(getCaretDrawable());
        this.mAllAppsHandle.setOnClickListener(this.mLauncher);
        this.mAllAppsHandle.setOnLongClickListener(this.mLauncher);
        this.mAllAppsHandle.setOnFocusChangeListener(this.mLauncher.mFocusHandler);
        this.mLauncher.setAllAppsButton(this.mAllAppsHandle);
    }

    public void setAccessibilityDelegate(AccessibilityDelegate accessibilityDelegate) {
        this.mAllAppsHandle.setAccessibilityDelegate(accessibilityDelegate);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mTotalScroll != 0 && this.mNumPagesFloat != 0.0f) {
            float boundToRange = Utilities.boundToRange(((float) this.mCurrentScroll) / ((float) this.mTotalScroll), 0.0f, 1.0f);
            int width = canvas.getWidth();
            int i = (int) (((float) width) / this.mNumPagesFloat);
            int i2 = (int) (boundToRange * ((float) (width - i)));
            Canvas canvas2 = canvas;
            canvas2.drawRect((float) i2, (float) (canvas.getHeight() - this.mLineHeight), (float) (i + i2), (float) canvas.getHeight(), this.mLinePaint);
        }
    }

    public void setContentDescription(CharSequence charSequence) {
        this.mAllAppsHandle.setContentDescription(charSequence);
    }

    public void setScroll(int i, int i2) {
        if (getAlpha() != 0.0f) {
            animateLineToAlpha(this.mActiveAlpha);
            this.mCurrentScroll = i;
            if (this.mTotalScroll == 0) {
                this.mTotalScroll = i2;
            } else if (this.mTotalScroll != i2) {
                animateToTotalScroll(i2);
            } else {
                invalidate();
            }
            if (this.mShouldAutoHide) {
                hideAfterDelay();
            }
        }
    }

    private void hideAfterDelay() {
        this.mDelayedLineFadeHandler.removeCallbacksAndMessages(null);
        this.mDelayedLineFadeHandler.postDelayed(this.mHideLineRunnable, (long) LINE_FADE_DELAY);
    }

    /* access modifiers changed from: protected */
    public void onPageCountChanged() {
        if (Float.compare((float) this.mNumPages, this.mNumPagesFloat) != 0) {
            animateToNumPages(this.mNumPages);
        }
    }

    public void setShouldAutoHide(boolean z) {
        this.mShouldAutoHide = z;
        if (z && this.mLinePaint.getAlpha() > 0) {
            hideAfterDelay();
        } else if (!z) {
            this.mDelayedLineFadeHandler.removeCallbacksAndMessages(null);
        }
    }

    /* access modifiers changed from: private */
    public void animateLineToAlpha(int i) {
        if (i != this.mToAlpha) {
            this.mToAlpha = i;
            setupAndRunAnimation(ObjectAnimator.ofInt(this, PAINT_ALPHA, new int[]{i}), 0);
        }
    }

    private void animateToNumPages(int i) {
        setupAndRunAnimation(ObjectAnimator.ofFloat(this, NUM_PAGES, new float[]{(float) i}), 1);
    }

    private void animateToTotalScroll(int i) {
        setupAndRunAnimation(ObjectAnimator.ofInt(this, TOTAL_SCROLL, new int[]{i}), 2);
    }

    private void setupAndRunAnimation(ValueAnimator valueAnimator, final int i) {
        if (this.mAnimators[i] != null) {
            this.mAnimators[i].cancel();
        }
        this.mAnimators[i] = valueAnimator;
        this.mAnimators[i].addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PageIndicatorLineCaret.this.mAnimators[i] = null;
            }
        });
        this.mAnimators[i].setDuration((long) LINE_ANIMATE_DURATION);
        this.mAnimators[i].start();
    }
}
