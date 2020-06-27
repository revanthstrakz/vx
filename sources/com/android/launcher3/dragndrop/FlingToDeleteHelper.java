package com.android.launcher3.dragndrop;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.C0622R;
import com.android.launcher3.DropTarget;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Launcher;
import com.android.launcher3.util.FlingAnimation;

public class FlingToDeleteHelper {
    private static final float MAX_FLING_DEGREES = 35.0f;
    private ButtonDropTarget mDropTarget;
    private final int mFlingToDeleteThresholdVelocity;
    private final Launcher mLauncher;
    private VelocityTracker mVelocityTracker;

    public FlingToDeleteHelper(Launcher launcher) {
        this.mLauncher = launcher;
        this.mFlingToDeleteThresholdVelocity = launcher.getResources().getDimensionPixelSize(C0622R.dimen.drag_flingToDeleteMinVelocity);
    }

    public void recordMotionEvent(MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
    }

    public void recordDragEvent(long j, DragEvent dragEvent) {
        int i;
        int action = dragEvent.getAction();
        if (action != 4) {
            switch (action) {
                case 1:
                    i = 0;
                    break;
                case 2:
                    i = 2;
                    break;
                default:
                    return;
            }
        } else {
            i = 1;
        }
        MotionEvent obtain = MotionEvent.obtain(j, SystemClock.uptimeMillis(), i, dragEvent.getX(), dragEvent.getY(), 0);
        recordMotionEvent(obtain);
        obtain.recycle();
    }

    public void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public DropTarget getDropTarget() {
        return this.mDropTarget;
    }

    public Runnable getFlingAnimation(DragObject dragObject) {
        PointF isFlingingToDelete = isFlingingToDelete();
        if (isFlingingToDelete == null) {
            return null;
        }
        return new FlingAnimation(dragObject, isFlingingToDelete, this.mDropTarget, this.mLauncher);
    }

    private PointF isFlingingToDelete() {
        if (this.mDropTarget == null) {
            this.mDropTarget = (ButtonDropTarget) this.mLauncher.findViewById(C0622R.C0625id.delete_target_text);
        }
        if (this.mDropTarget == null || !this.mDropTarget.isDropEnabled()) {
            return null;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, (float) ViewConfiguration.get(this.mLauncher).getScaledMaximumFlingVelocity());
        PointF pointF = new PointF(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
        float f = 36.0f;
        if (this.mVelocityTracker.getYVelocity() < ((float) this.mFlingToDeleteThresholdVelocity)) {
            f = getAngleBetweenVectors(pointF, new PointF(0.0f, -1.0f));
        } else if (this.mLauncher.getDeviceProfile().isVerticalBarLayout() && this.mVelocityTracker.getXVelocity() < ((float) this.mFlingToDeleteThresholdVelocity)) {
            f = getAngleBetweenVectors(pointF, new PointF(-1.0f, 0.0f));
        }
        if (((double) f) <= Math.toRadians(35.0d)) {
            return pointF;
        }
        return null;
    }

    private float getAngleBetweenVectors(PointF pointF, PointF pointF2) {
        return (float) Math.acos((double) (((pointF.x * pointF2.x) + (pointF.y * pointF2.y)) / (pointF.length() * pointF2.length())));
    }
}
