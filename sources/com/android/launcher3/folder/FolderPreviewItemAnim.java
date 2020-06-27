package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import com.android.launcher3.LauncherAnimUtils;

class FolderPreviewItemAnim {
    private static PreviewItemDrawingParams sTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0.0f);
    float finalScale = sTmpParams.scale;
    float finalTransX = sTmpParams.transX;
    float finalTransY = sTmpParams.transY;
    private ValueAnimator mValueAnimator;

    FolderPreviewItemAnim(PreviewItemManager previewItemManager, final PreviewItemDrawingParams previewItemDrawingParams, int i, int i2, int i3, int i4, int i5, final Runnable runnable) {
        previewItemManager.computePreviewItemDrawingParams(i3, i4, sTmpParams);
        previewItemManager.computePreviewItemDrawingParams(i, i2, sTmpParams);
        final float f = sTmpParams.scale;
        final float f2 = sTmpParams.transX;
        final float f3 = sTmpParams.transY;
        this.mValueAnimator = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        ValueAnimator valueAnimator = this.mValueAnimator;
        final PreviewItemDrawingParams previewItemDrawingParams2 = previewItemDrawingParams;
        final PreviewItemManager previewItemManager2 = previewItemManager;
        C07371 r0 = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                previewItemDrawingParams2.transX = f2 + ((FolderPreviewItemAnim.this.finalTransX - f2) * animatedFraction);
                previewItemDrawingParams2.transY = f3 + ((FolderPreviewItemAnim.this.finalTransY - f3) * animatedFraction);
                previewItemDrawingParams2.scale = f + (animatedFraction * (FolderPreviewItemAnim.this.finalScale - f));
                previewItemManager2.onParamsChanged();
            }
        };
        valueAnimator.addUpdateListener(r0);
        this.mValueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (runnable != null) {
                    runnable.run();
                }
                previewItemDrawingParams.anim = null;
            }
        });
        this.mValueAnimator.setDuration((long) i5);
    }

    public void start() {
        this.mValueAnimator.start();
    }

    public void cancel() {
        this.mValueAnimator.cancel();
    }

    public boolean hasEqualFinalState(FolderPreviewItemAnim folderPreviewItemAnim) {
        return this.finalTransY == folderPreviewItemAnim.finalTransY && this.finalTransX == folderPreviewItemAnim.finalTransX && this.finalScale == folderPreviewItemAnim.finalScale;
    }
}
