package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.launcher3.Utilities;

public abstract class RevealOutlineAnimation extends ViewOutlineProvider {
    protected Rect mOutline = new Rect();
    protected float mOutlineRadius;

    /* access modifiers changed from: 0000 */
    public abstract void setProgress(float f);

    /* access modifiers changed from: 0000 */
    public abstract boolean shouldRemoveElevationDuringAnimation();

    public ValueAnimator createRevealAnimator(View view) {
        return createRevealAnimator(view, false);
    }

    public ValueAnimator createRevealAnimator(final View view, boolean z) {
        float[] fArr;
        if (z) {
            fArr = new float[]{1.0f, 0.0f};
        } else {
            fArr = new float[]{0.0f, 1.0f};
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        final float elevation = view.getElevation();
        ofFloat.addListener(new AnimatorListenerAdapter() {
            private boolean mWasCanceled = false;

            public void onAnimationStart(Animator animator) {
                view.setOutlineProvider(RevealOutlineAnimation.this);
                view.setClipToOutline(true);
                if (RevealOutlineAnimation.this.shouldRemoveElevationDuringAnimation()) {
                    view.setTranslationZ(-elevation);
                }
            }

            public void onAnimationCancel(Animator animator) {
                this.mWasCanceled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (!this.mWasCanceled) {
                    view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
                    view.setClipToOutline(false);
                    if (RevealOutlineAnimation.this.shouldRemoveElevationDuringAnimation()) {
                        view.setTranslationZ(0.0f);
                    }
                }
            }
        });
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RevealOutlineAnimation.this.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
                view.invalidateOutline();
                if (!Utilities.ATLEAST_LOLLIPOP_MR1) {
                    view.invalidate();
                }
            }
        });
        return ofFloat;
    }

    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(this.mOutline, this.mOutlineRadius);
    }

    public float getRadius() {
        return this.mOutlineRadius;
    }
}
