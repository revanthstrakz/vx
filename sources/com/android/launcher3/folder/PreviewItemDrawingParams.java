package com.android.launcher3.folder;

import android.graphics.drawable.Drawable;

class PreviewItemDrawingParams {
    FolderPreviewItemAnim anim;
    Drawable drawable;
    public boolean hidden;
    float overlayAlpha;
    float scale;
    float transX;
    float transY;

    PreviewItemDrawingParams(float f, float f2, float f3, float f4) {
        this.transX = f;
        this.transY = f2;
        this.scale = f3;
        this.overlayAlpha = f4;
    }

    public void update(float f, float f2, float f3) {
        if (this.anim != null) {
            if (this.anim.finalTransX != f && this.anim.finalTransY != f2 && this.anim.finalScale != f3) {
                this.anim.cancel();
            } else {
                return;
            }
        }
        this.transX = f;
        this.transY = f2;
        this.scale = f3;
    }
}
