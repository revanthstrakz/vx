package com.android.launcher3.anim;

public class CircleRevealOutlineProvider extends RevealOutlineAnimation {
    private int mCenterX;
    private int mCenterY;
    private float mRadius0;
    private float mRadius1;

    public boolean shouldRemoveElevationDuringAnimation() {
        return true;
    }

    public CircleRevealOutlineProvider(int i, int i2, float f, float f2) {
        this.mCenterX = i;
        this.mCenterY = i2;
        this.mRadius0 = f;
        this.mRadius1 = f2;
    }

    public void setProgress(float f) {
        this.mOutlineRadius = ((1.0f - f) * this.mRadius0) + (f * this.mRadius1);
        this.mOutline.left = (int) (((float) this.mCenterX) - this.mOutlineRadius);
        this.mOutline.top = (int) (((float) this.mCenterY) - this.mOutlineRadius);
        this.mOutline.right = (int) (((float) this.mCenterX) + this.mOutlineRadius);
        this.mOutline.bottom = (int) (((float) this.mCenterY) + this.mOutlineRadius);
    }
}
