package com.android.launcher3.folder;

public class StackFolderIconLayoutRule implements PreviewLayoutRule {
    static final int MAX_NUM_ITEMS_IN_PREVIEW = 3;
    private static final float PERSPECTIVE_SCALE_FACTOR = 0.35f;
    private static final float PERSPECTIVE_SHIFT_FACTOR = 0.18f;
    private int mAvailableSpaceInPreview;
    private float mBaselineIconScale;
    private int mBaselineIconSize;
    private float mMaxPerspectiveShift;

    public boolean clipToBackground() {
        return false;
    }

    public boolean hasEnterExitIndices() {
        return false;
    }

    public int maxNumItems() {
        return 3;
    }

    public float scaleForItem(int i, int i2) {
        return 1.0f - ((1.0f - ((((float) ((3 - i) - 1)) * 1.0f) / 2.0f)) * PERSPECTIVE_SCALE_FACTOR);
    }

    public void init(int i, float f, boolean z) {
        this.mAvailableSpaceInPreview = i;
        this.mBaselineIconScale = (((float) ((int) (((float) (this.mAvailableSpaceInPreview / 2)) * 1.8f))) * 1.0f) / ((float) ((int) (1.1800001f * f)));
        this.mBaselineIconSize = (int) (f * this.mBaselineIconScale);
        this.mMaxPerspectiveShift = ((float) this.mBaselineIconSize) * PERSPECTIVE_SHIFT_FACTOR;
    }

    public PreviewItemDrawingParams computePreviewItemDrawingParams(int i, int i2, PreviewItemDrawingParams previewItemDrawingParams) {
        float scaleForItem = scaleForItem(i, i2);
        float f = 1.0f - ((((float) ((3 - i) - 1)) * 1.0f) / 2.0f);
        float f2 = ((float) this.mBaselineIconSize) * scaleForItem;
        float f3 = (1.0f - scaleForItem) * ((float) this.mBaselineIconSize);
        float f4 = ((float) this.mAvailableSpaceInPreview) - (((this.mMaxPerspectiveShift * f) + f2) + f3);
        float f5 = (((float) this.mAvailableSpaceInPreview) - f2) / 2.0f;
        float f6 = this.mBaselineIconScale * scaleForItem;
        float f7 = (f * 80.0f) / 255.0f;
        if (previewItemDrawingParams == null) {
            return new PreviewItemDrawingParams(f5, f4, f6, f7);
        }
        previewItemDrawingParams.update(f5, f4, f6);
        previewItemDrawingParams.overlayAlpha = f7;
        return previewItemDrawingParams;
    }

    public float getIconSize() {
        return (float) this.mBaselineIconSize;
    }

    public int getExitIndex() {
        throw new RuntimeException("hasEnterExitIndices not supported");
    }

    public int getEnterIndex() {
        throw new RuntimeException("hasEnterExitIndices not supported");
    }
}
