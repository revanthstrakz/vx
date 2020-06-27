package com.android.launcher3.folder;

public class ClippedFolderIconLayoutRule implements PreviewLayoutRule {
    private static final int ENTER_INDEX = -3;
    private static final int EXIT_INDEX = -2;
    private static final float ITEM_RADIUS_SCALE_FACTOR = 1.33f;
    static final int MAX_NUM_ITEMS_IN_PREVIEW = 4;
    private static final float MAX_RADIUS_DILATION = 0.15f;
    private static final float MAX_SCALE = 0.58f;
    private static final int MIN_NUM_ITEMS_IN_PREVIEW = 2;
    private static final float MIN_SCALE = 0.48f;
    private float mAvailableSpace;
    private float mBaselineIconScale;
    private float mIconSize;
    private boolean mIsRtl;
    private float mRadius;
    private float[] mTmpPoint = new float[2];

    public boolean clipToBackground() {
        return true;
    }

    public int getEnterIndex() {
        return -3;
    }

    public int getExitIndex() {
        return -2;
    }

    public boolean hasEnterExitIndices() {
        return true;
    }

    public int maxNumItems() {
        return 4;
    }

    public void init(int i, float f, boolean z) {
        float f2 = (float) i;
        this.mAvailableSpace = f2;
        this.mRadius = (ITEM_RADIUS_SCALE_FACTOR * f2) / 2.0f;
        this.mIconSize = f;
        this.mIsRtl = z;
        this.mBaselineIconScale = f2 / (f * 1.0f);
    }

    public PreviewItemDrawingParams computePreviewItemDrawingParams(int i, int i2, PreviewItemDrawingParams previewItemDrawingParams) {
        float scaleForItem = scaleForItem(i, i2);
        if (i == getExitIndex()) {
            getGridPosition(0, 2, this.mTmpPoint);
        } else if (i == getEnterIndex()) {
            getGridPosition(1, 2, this.mTmpPoint);
        } else if (i >= 4) {
            float[] fArr = this.mTmpPoint;
            float f = (this.mAvailableSpace / 2.0f) - ((this.mIconSize * scaleForItem) / 2.0f);
            this.mTmpPoint[1] = f;
            fArr[0] = f;
        } else {
            getPosition(i, i2, this.mTmpPoint);
        }
        float f2 = this.mTmpPoint[0];
        float f3 = this.mTmpPoint[1];
        if (previewItemDrawingParams == null) {
            return new PreviewItemDrawingParams(f2, f3, scaleForItem, 0.0f);
        }
        previewItemDrawingParams.update(f2, f3, scaleForItem);
        previewItemDrawingParams.overlayAlpha = 0.0f;
        return previewItemDrawingParams;
    }

    private void getGridPosition(int i, int i2, float[] fArr) {
        getPosition(0, 4, fArr);
        float f = fArr[0];
        float f2 = fArr[1];
        getPosition(3, 4, fArr);
        float f3 = fArr[1] - f2;
        fArr[0] = f + (((float) i2) * (fArr[0] - f));
        fArr[1] = f2 + (((float) i) * f3);
    }

    private void getPosition(int i, int i2, float[] fArr) {
        int i3 = i;
        int max = Math.max(i2, 2);
        double d = 0.0d;
        double d2 = this.mIsRtl ? 0.0d : 3.141592653589793d;
        int i4 = this.mIsRtl ? 1 : -1;
        int i5 = 3;
        if (max == 3) {
            d = 0.5235987755982988d;
        } else if (max == 4) {
            d = 0.7853981633974483d;
        }
        double d3 = (double) i4;
        double d4 = d2 + (d * d3);
        if (max == 4 && i3 == 3) {
            i5 = 2;
        } else if (!(max == 4 && i3 == 2)) {
            i5 = i3;
        }
        float f = this.mRadius * (((((float) (max - 2)) * MAX_RADIUS_DILATION) / 2.0f) + 1.0f);
        double d5 = d4 + (((double) i5) * (6.283185307179586d / ((double) max)) * d3);
        float scaleForItem = (this.mIconSize * scaleForItem(i5, max)) / 2.0f;
        fArr[0] = ((this.mAvailableSpace / 2.0f) + ((float) ((((double) f) * Math.cos(d5)) / 2.0d))) - scaleForItem;
        fArr[1] = ((this.mAvailableSpace / 2.0f) + ((float) ((((double) (-f)) * Math.sin(d5)) / 2.0d))) - scaleForItem;
    }

    public float scaleForItem(int i, int i2) {
        float f = i2 <= 2 ? MAX_SCALE : i2 == 3 ? 0.53f : MIN_SCALE;
        return f * this.mBaselineIconScale;
    }

    public float getIconSize() {
        return this.mIconSize;
    }
}
