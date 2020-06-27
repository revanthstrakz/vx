package com.android.launcher3.dragndrop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatArrayEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.C0622R;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.dragndrop.DragLayer.LayoutParams;
import com.android.launcher3.graphics.IconNormalizer;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.Themes;
import java.util.Arrays;

public class DragView extends View {
    public static final int COLOR_CHANGE_DURATION = 120;
    public static final int VIEW_ZOOM_DURATION = 150;
    static float sDragAlpha = 1.0f;
    private static final ColorMatrix sTempMatrix1 = new ColorMatrix();
    private static final ColorMatrix sTempMatrix2 = new ColorMatrix();
    ValueAnimator mAnim;
    /* access modifiers changed from: private */
    public int mAnimatedShiftX;
    /* access modifiers changed from: private */
    public int mAnimatedShiftY;
    /* access modifiers changed from: private */
    public boolean mAnimationCancelled = false;
    /* access modifiers changed from: private */
    public Drawable mBadge;
    /* access modifiers changed from: private */
    public ColorMatrixColorFilter mBaseFilter;
    /* access modifiers changed from: private */
    public Drawable mBgSpringDrawable;
    /* access modifiers changed from: private */
    public Bitmap mBitmap;
    private final int mBlurSizeOutline;
    private Bitmap mCrossFadeBitmap;
    float mCrossFadeProgress = 0.0f;
    float[] mCurrentFilter;
    final DragController mDragController;
    private final DragLayer mDragLayer;
    private Rect mDragRegion = null;
    private Point mDragVisualizeOffset = null;
    /* access modifiers changed from: private */
    public boolean mDrawBitmap = true;
    /* access modifiers changed from: private */
    public Drawable mFgSpringDrawable;
    private ValueAnimator mFilterAnimator;
    private boolean mHasDrawn = false;
    private final float mInitialScale;
    private float mIntrinsicIconScale = 1.0f;
    private int mLastTouchX;
    private int mLastTouchY;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    Paint mPaint;
    private final int mRegistrationX;
    private final int mRegistrationY;
    /* access modifiers changed from: private */
    public Path mScaledMaskPath;
    private final int[] mTempLoc = new int[2];
    /* access modifiers changed from: private */
    public SpringFloatValue mTranslateX;
    /* access modifiers changed from: private */
    public SpringFloatValue mTranslateY;

    private static class FixedSizeEmptyDrawable extends ColorDrawable {
        private final int mSize;

        public FixedSizeEmptyDrawable(int i) {
            super(0);
            this.mSize = i;
        }

        public int getIntrinsicHeight() {
            return this.mSize;
        }

        public int getIntrinsicWidth() {
            return this.mSize;
        }
    }

    private static class SpringFloatValue {
        private static final float DAMPENING_RATIO = 1.0f;
        private static final int PARALLAX_MAX_IN_DP = 8;
        private static final int STIFFNESS = 4000;
        private static final FloatPropertyCompat<SpringFloatValue> VALUE = new FloatPropertyCompat<SpringFloatValue>("value") {
            public float getValue(SpringFloatValue springFloatValue) {
                return springFloatValue.mValue;
            }

            public void setValue(SpringFloatValue springFloatValue, float f) {
                springFloatValue.mValue = f;
                springFloatValue.mView.invalidate();
            }
        };
        private final float mDelta;
        private final SpringAnimation mSpring;
        /* access modifiers changed from: private */
        public float mValue;
        /* access modifiers changed from: private */
        public final View mView;

        public SpringFloatValue(View view, float f) {
            this.mView = view;
            this.mSpring = ((SpringAnimation) ((SpringAnimation) new SpringAnimation(this, VALUE, 0.0f).setMinValue(-f)).setMaxValue(f)).setSpring(new SpringForce(0.0f).setDampingRatio(1.0f).setStiffness(4000.0f));
            this.mDelta = view.getResources().getDisplayMetrics().density * 8.0f;
        }

        public void animateToPos(float f) {
            this.mSpring.animateToFinalPosition(Utilities.boundToRange(f, -this.mDelta, this.mDelta));
        }
    }

    public DragView(Launcher launcher, Bitmap bitmap, int i, int i2, final float f, float f2) {
        super(launcher);
        this.mLauncher = launcher;
        this.mDragLayer = launcher.getDragLayer();
        this.mDragController = launcher.getDragController();
        final float width = (((float) bitmap.getWidth()) + f2) / ((float) bitmap.getWidth());
        setScaleX(f);
        setScaleY(f);
        this.mAnim = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        this.mAnim.setDuration(150);
        this.mAnim.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                DragView.this.setScaleX(f + ((width - f) * floatValue));
                DragView.this.setScaleY(f + ((width - f) * floatValue));
                if (DragView.sDragAlpha != 1.0f) {
                    DragView.this.setAlpha((DragView.sDragAlpha * floatValue) + (1.0f - floatValue));
                }
                if (DragView.this.getParent() == null) {
                    valueAnimator.cancel();
                }
            }
        });
        this.mAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (!DragView.this.mAnimationCancelled) {
                    DragView.this.mDragController.onDragViewAnimationEnd();
                }
            }
        });
        this.mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
        setDragRegion(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
        this.mRegistrationX = i;
        this.mRegistrationY = i2;
        this.mInitialScale = f;
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
        measure(makeMeasureSpec, makeMeasureSpec);
        this.mPaint = new Paint(2);
        this.mBlurSizeOutline = getResources().getDimensionPixelSize(C0622R.dimen.blur_size_medium_outline);
        setElevation(getResources().getDimension(C0622R.dimen.drag_elevation));
    }

    @TargetApi(26)
    public void setItemInfo(final ItemInfo itemInfo) {
        if (Utilities.ATLEAST_OREO) {
            if (itemInfo.itemType == 0 || itemInfo.itemType == 6 || itemInfo.itemType == 2) {
                new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        LauncherAppState instance = LauncherAppState.getInstance(DragView.this.mLauncher);
                        Object[] objArr = new Object[1];
                        final Drawable access$200 = DragView.this.getFullDrawable(itemInfo, instance, objArr);
                        if (access$200 instanceof AdaptiveIconDrawable) {
                            int width = DragView.this.mBitmap.getWidth();
                            int height = DragView.this.mBitmap.getHeight();
                            int dimension = ((int) DragView.this.mLauncher.getResources().getDimension(C0622R.dimen.blur_size_medium_outline)) / 2;
                            Rect rect = new Rect(0, 0, width, height);
                            rect.inset(dimension, dimension);
                            Rect rect2 = new Rect(rect);
                            DragView.this.mBadge = DragView.this.getBadge(itemInfo, instance, objArr[0]);
                            DragView.this.mBadge.setBounds(rect2);
                            Utilities.scaleRectAboutCenter(rect, IconNormalizer.getInstance(DragView.this.mLauncher).getScale(access$200, null, null, null));
                            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) access$200;
                            Rect rect3 = new Rect(rect);
                            Utilities.scaleRectAboutCenter(rect3, 0.98f);
                            adaptiveIconDrawable.setBounds(rect3);
                            final Path iconMask = adaptiveIconDrawable.getIconMask();
                            DragView.this.mTranslateX = new SpringFloatValue(DragView.this, ((float) width) * AdaptiveIconDrawable.getExtraInsetFraction());
                            DragView.this.mTranslateY = new SpringFloatValue(DragView.this, ((float) height) * AdaptiveIconDrawable.getExtraInsetFraction());
                            rect.inset((int) (((float) (-rect.width())) * AdaptiveIconDrawable.getExtraInsetFraction()), (int) (((float) (-rect.height())) * AdaptiveIconDrawable.getExtraInsetFraction()));
                            DragView.this.mBgSpringDrawable = adaptiveIconDrawable.getBackground();
                            if (DragView.this.mBgSpringDrawable == null) {
                                DragView.this.mBgSpringDrawable = new ColorDrawable(0);
                            }
                            DragView.this.mBgSpringDrawable.setBounds(rect);
                            DragView.this.mFgSpringDrawable = adaptiveIconDrawable.getForeground();
                            if (DragView.this.mFgSpringDrawable == null) {
                                DragView.this.mFgSpringDrawable = new ColorDrawable(0);
                            }
                            DragView.this.mFgSpringDrawable.setBounds(rect);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    DragView.this.mScaledMaskPath = iconMask;
                                    DragView.this.mDrawBitmap = !(access$200 instanceof FolderAdaptiveIcon);
                                    if (itemInfo.isDisabled()) {
                                        FastBitmapDrawable fastBitmapDrawable = new FastBitmapDrawable(null);
                                        fastBitmapDrawable.setIsDisabled(true);
                                        DragView.this.mBaseFilter = (ColorMatrixColorFilter) fastBitmapDrawable.getColorFilter();
                                    }
                                    DragView.this.updateColorFilter();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    @TargetApi(26)
    public void updateColorFilter() {
        if (this.mCurrentFilter == null) {
            this.mPaint.setColorFilter(null);
            if (this.mScaledMaskPath != null) {
                this.mBgSpringDrawable.setColorFilter(this.mBaseFilter);
                this.mBgSpringDrawable.setColorFilter(this.mBaseFilter);
                this.mBadge.setColorFilter(this.mBaseFilter);
            }
        } else {
            ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(this.mCurrentFilter);
            this.mPaint.setColorFilter(colorMatrixColorFilter);
            if (this.mScaledMaskPath != null) {
                if (this.mBaseFilter != null) {
                    this.mBaseFilter.getColorMatrix(sTempMatrix1);
                    sTempMatrix2.set(this.mCurrentFilter);
                    sTempMatrix1.postConcat(sTempMatrix2);
                    colorMatrixColorFilter = new ColorMatrixColorFilter(sTempMatrix1);
                }
                this.mBgSpringDrawable.setColorFilter(colorMatrixColorFilter);
                this.mFgSpringDrawable.setColorFilter(colorMatrixColorFilter);
                this.mBadge.setColorFilter(colorMatrixColorFilter);
            }
        }
        invalidate();
    }

    /* JADX WARNING: type inference failed for: r9v0, types: [java.lang.Object[]] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Incorrect type for immutable var: ssa=java.lang.Object[], code=null, for r9v0, types: [java.lang.Object[]] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.drawable.Drawable getFullDrawable(com.android.launcher3.ItemInfo r7, com.android.launcher3.LauncherAppState r8, java.lang.Object[] r9) {
        /*
            r6 = this;
            int r0 = r7.itemType
            r1 = 0
            r2 = 0
            if (r0 != 0) goto L_0x0023
            com.android.launcher3.Launcher r0 = r6.mLauncher
            com.android.launcher3.compat.LauncherAppsCompat r0 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r0)
            android.content.Intent r3 = r7.getIntent()
            android.os.UserHandle r7 = r7.user
            android.content.pm.LauncherActivityInfo r7 = r0.resolveActivity(r3, r7)
            r9[r2] = r7
            if (r7 == 0) goto L_0x0022
            com.android.launcher3.IconCache r8 = r8.getIconCache()
            android.graphics.drawable.Drawable r1 = r8.getFullResIcon(r7, r2)
        L_0x0022:
            return r1
        L_0x0023:
            int r0 = r7.itemType
            r3 = 6
            if (r0 != r3) goto L_0x007c
            boolean r0 = r7 instanceof com.android.launcher3.widget.PendingAddShortcutInfo
            if (r0 == 0) goto L_0x003b
            com.android.launcher3.widget.PendingAddShortcutInfo r7 = (com.android.launcher3.widget.PendingAddShortcutInfo) r7
            com.android.launcher3.compat.ShortcutConfigActivityInfo r7 = r7.activityInfo
            r9[r2] = r7
            com.android.launcher3.IconCache r8 = r8.getIconCache()
            android.graphics.drawable.Drawable r7 = r7.getFullResIcon(r8)
            return r7
        L_0x003b:
            com.android.launcher3.shortcuts.ShortcutKey r7 = com.android.launcher3.shortcuts.ShortcutKey.fromItemInfo(r7)
            com.android.launcher3.Launcher r0 = r6.mLauncher
            com.android.launcher3.shortcuts.DeepShortcutManager r0 = com.android.launcher3.shortcuts.DeepShortcutManager.getInstance(r0)
            android.content.ComponentName r3 = r7.componentName
            java.lang.String r3 = r3.getPackageName()
            r4 = 1
            java.lang.String[] r4 = new java.lang.String[r4]
            java.lang.String r5 = r7.getId()
            r4[r2] = r5
            java.util.List r4 = java.util.Arrays.asList(r4)
            android.os.UserHandle r7 = r7.user
            java.util.List r7 = r0.queryForFullDetails(r3, r4, r7)
            boolean r3 = r7.isEmpty()
            if (r3 == 0) goto L_0x0065
            return r1
        L_0x0065:
            java.lang.Object r1 = r7.get(r2)
            r9[r2] = r1
            java.lang.Object r7 = r7.get(r2)
            com.android.launcher3.shortcuts.ShortcutInfoCompat r7 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r7
            com.android.launcher3.InvariantDeviceProfile r8 = r8.getInvariantDeviceProfile()
            int r8 = r8.fillResIconDpi
            android.graphics.drawable.Drawable r7 = r0.getShortcutIconDrawable(r7, r8)
            return r7
        L_0x007c:
            int r8 = r7.itemType
            r0 = 2
            if (r8 != r0) goto L_0x00a0
            com.android.launcher3.Launcher r8 = r6.mLauncher
            long r3 = r7.f52id
            android.graphics.Point r7 = new android.graphics.Point
            android.graphics.Bitmap r0 = r6.mBitmap
            int r0 = r0.getWidth()
            android.graphics.Bitmap r5 = r6.mBitmap
            int r5 = r5.getHeight()
            r7.<init>(r0, r5)
            com.android.launcher3.dragndrop.FolderAdaptiveIcon r7 = com.android.launcher3.dragndrop.FolderAdaptiveIcon.createFolderAdaptiveIcon(r8, r3, r7)
            if (r7 != 0) goto L_0x009d
            return r1
        L_0x009d:
            r9[r2] = r7
            return r7
        L_0x00a0:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dragndrop.DragView.getFullDrawable(com.android.launcher3.ItemInfo, com.android.launcher3.LauncherAppState, java.lang.Object[]):android.graphics.drawable.Drawable");
    }

    /* access modifiers changed from: private */
    @TargetApi(26)
    public Drawable getBadge(ItemInfo itemInfo, LauncherAppState launcherAppState, Object obj) {
        int i = launcherAppState.getInvariantDeviceProfile().iconBitmapSize;
        if (itemInfo.itemType == 6) {
            if (itemInfo.f52id == -1 || !(obj instanceof ShortcutInfoCompat)) {
                return new FixedSizeEmptyDrawable(i);
            }
            Bitmap shortcutInfoBadge = LauncherIcons.getShortcutInfoBadge((ShortcutInfoCompat) obj, launcherAppState.getIconCache());
            float f = (float) i;
            float dimension = (f - this.mLauncher.getResources().getDimension(C0622R.dimen.profile_badge_size)) / f;
            InsetDrawable insetDrawable = new InsetDrawable(new FastBitmapDrawable(shortcutInfoBadge), dimension, dimension, 0.0f, 0.0f);
            return insetDrawable;
        } else if (itemInfo.itemType == 2) {
            return ((FolderAdaptiveIcon) obj).getBadge();
        } else {
            return UserManagerCompat.getInstance(launcherAppState.getContext()).getUserBadgedIcon(new FixedSizeEmptyDrawable(i), itemInfo.user);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(this.mBitmap.getWidth(), this.mBitmap.getHeight());
    }

    public void setIntrinsicIconScaleFactor(float f) {
        this.mIntrinsicIconScale = f;
    }

    public float getIntrinsicIconScaleFactor() {
        return this.mIntrinsicIconScale;
    }

    public int getDragRegionLeft() {
        return this.mDragRegion.left;
    }

    public int getDragRegionTop() {
        return this.mDragRegion.top;
    }

    public int getDragRegionWidth() {
        return this.mDragRegion.width();
    }

    public int getDragRegionHeight() {
        return this.mDragRegion.height();
    }

    public void setDragVisualizeOffset(Point point) {
        this.mDragVisualizeOffset = point;
    }

    public Point getDragVisualizeOffset() {
        return this.mDragVisualizeOffset;
    }

    public void setDragRegion(Rect rect) {
        this.mDragRegion = rect;
    }

    public Rect getDragRegion() {
        return this.mDragRegion;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mHasDrawn = true;
        if (this.mDrawBitmap) {
            boolean z = this.mCrossFadeProgress > 0.0f && this.mCrossFadeBitmap != null;
            if (z) {
                this.mPaint.setAlpha(z ? (int) ((1.0f - this.mCrossFadeProgress) * 255.0f) : 255);
            }
            canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
            if (z) {
                this.mPaint.setAlpha((int) (this.mCrossFadeProgress * 255.0f));
                int save = canvas.save(1);
                canvas.scale((((float) this.mBitmap.getWidth()) * 1.0f) / ((float) this.mCrossFadeBitmap.getWidth()), (((float) this.mBitmap.getHeight()) * 1.0f) / ((float) this.mCrossFadeBitmap.getHeight()));
                canvas.drawBitmap(this.mCrossFadeBitmap, 0.0f, 0.0f, this.mPaint);
                canvas.restoreToCount(save);
            }
        }
        if (this.mScaledMaskPath != null) {
            int save2 = canvas.save();
            canvas.clipPath(this.mScaledMaskPath);
            this.mBgSpringDrawable.draw(canvas);
            canvas.translate(this.mTranslateX.mValue, this.mTranslateY.mValue);
            this.mFgSpringDrawable.draw(canvas);
            canvas.restoreToCount(save2);
            this.mBadge.draw(canvas);
        }
    }

    public void setCrossFadeBitmap(Bitmap bitmap) {
        this.mCrossFadeBitmap = bitmap;
    }

    public void crossFade(int i) {
        ValueAnimator ofFloat = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        ofFloat.setDuration((long) i);
        ofFloat.setInterpolator(new DecelerateInterpolator(1.5f));
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DragView.this.mCrossFadeProgress = valueAnimator.getAnimatedFraction();
                DragView.this.invalidate();
            }
        });
        ofFloat.start();
    }

    public void setColor(int i) {
        if (this.mPaint == null) {
            this.mPaint = new Paint(2);
        }
        if (i != 0) {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.0f);
            ColorMatrix colorMatrix2 = new ColorMatrix();
            Themes.setColorScaleOnMatrix(i, colorMatrix2);
            colorMatrix.postConcat(colorMatrix2);
            animateFilterTo(colorMatrix.getArray());
        } else if (this.mCurrentFilter == null) {
            updateColorFilter();
        } else {
            animateFilterTo(new ColorMatrix().getArray());
        }
    }

    private void animateFilterTo(float[] fArr) {
        float[] array = this.mCurrentFilter == null ? new ColorMatrix().getArray() : this.mCurrentFilter;
        this.mCurrentFilter = Arrays.copyOf(array, array.length);
        if (this.mFilterAnimator != null) {
            this.mFilterAnimator.cancel();
        }
        this.mFilterAnimator = ValueAnimator.ofObject(new FloatArrayEvaluator(this.mCurrentFilter), new Object[]{array, fArr});
        this.mFilterAnimator.setDuration(120);
        this.mFilterAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DragView.this.updateColorFilter();
            }
        });
        this.mFilterAnimator.start();
    }

    public boolean hasDrawn() {
        return this.mHasDrawn;
    }

    public void setAlpha(float f) {
        super.setAlpha(f);
        this.mPaint.setAlpha((int) (f * 255.0f));
        invalidate();
    }

    public void show(int i, int i2) {
        this.mDragLayer.addView(this);
        LayoutParams layoutParams = new LayoutParams(0, 0);
        layoutParams.width = this.mBitmap.getWidth();
        layoutParams.height = this.mBitmap.getHeight();
        layoutParams.customPosition = true;
        setLayoutParams(layoutParams);
        move(i, i2);
        post(new Runnable() {
            public void run() {
                DragView.this.mAnim.start();
            }
        });
    }

    public void cancelAnimation() {
        this.mAnimationCancelled = true;
        if (this.mAnim != null && this.mAnim.isRunning()) {
            this.mAnim.cancel();
        }
    }

    public void move(int i, int i2) {
        if (i > 0 && i2 > 0 && this.mLastTouchX > 0 && this.mLastTouchY > 0 && this.mScaledMaskPath != null) {
            this.mTranslateX.animateToPos((float) (this.mLastTouchX - i));
            this.mTranslateY.animateToPos((float) (this.mLastTouchY - i2));
        }
        this.mLastTouchX = i;
        this.mLastTouchY = i2;
        applyTranslation();
    }

    public void animateTo(int i, int i2, Runnable runnable, int i3) {
        this.mTempLoc[0] = i - this.mRegistrationX;
        this.mTempLoc[1] = i2 - this.mRegistrationY;
        this.mDragLayer.animateViewIntoPosition(this, this.mTempLoc, 1.0f, this.mInitialScale, this.mInitialScale, 0, runnable, i3);
    }

    public void animateShift(final int i, final int i2) {
        if (!this.mAnim.isStarted()) {
            this.mAnimatedShiftX = i;
            this.mAnimatedShiftY = i2;
            applyTranslation();
            this.mAnim.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animatedFraction = 1.0f - valueAnimator.getAnimatedFraction();
                    DragView.this.mAnimatedShiftX = (int) (((float) i) * animatedFraction);
                    DragView.this.mAnimatedShiftY = (int) (animatedFraction * ((float) i2));
                    DragView.this.applyTranslation();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void applyTranslation() {
        setTranslationX((float) ((this.mLastTouchX - this.mRegistrationX) + this.mAnimatedShiftX));
        setTranslationY((float) ((this.mLastTouchY - this.mRegistrationY) + this.mAnimatedShiftY));
    }

    public void remove() {
        if (getParent() != null) {
            this.mDragLayer.removeView(this);
        }
    }

    public int getBlurSizeOutline() {
        return this.mBlurSizeOutline;
    }

    public float getInitialScale() {
        return this.mInitialScale;
    }
}
