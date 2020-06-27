package com.android.launcher3.shortcuts;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.graphics.HolographicOutlineHelper;

public class ShortcutDragPreviewProvider extends DragPreviewProvider {
    private final Point mPositionShift;

    public ShortcutDragPreviewProvider(View view, Point point) {
        super(view);
        this.mPositionShift = point;
    }

    public Bitmap createDragOutline(Canvas canvas) {
        Bitmap drawScaledPreview = drawScaledPreview(canvas, Config.ALPHA_8);
        HolographicOutlineHelper.getInstance(this.mView.getContext()).applyExpensiveOutlineWithBlur(drawScaledPreview, canvas);
        canvas.setBitmap(null);
        return drawScaledPreview;
    }

    public Bitmap createDragBitmap(Canvas canvas) {
        Bitmap drawScaledPreview = drawScaledPreview(canvas, Config.ARGB_8888);
        canvas.setBitmap(null);
        return drawScaledPreview;
    }

    private Bitmap drawScaledPreview(Canvas canvas, Config config) {
        Drawable background = this.mView.getBackground();
        Rect drawableBounds = getDrawableBounds(background);
        int i = Launcher.getLauncher(this.mView.getContext()).getDeviceProfile().iconSizePx;
        Bitmap createBitmap = Bitmap.createBitmap(this.blurSizeOutline + i, this.blurSizeOutline + i, config);
        canvas.setBitmap(createBitmap);
        canvas.save(1);
        canvas.translate((float) (this.blurSizeOutline / 2), (float) (this.blurSizeOutline / 2));
        float f = (float) i;
        canvas.scale(f / ((float) drawableBounds.width()), f / ((float) drawableBounds.height()), 0.0f, 0.0f);
        canvas.translate((float) drawableBounds.left, (float) drawableBounds.top);
        background.draw(canvas);
        canvas.restore();
        return createBitmap;
    }

    public float getScaleAndPosition(Bitmap bitmap, int[] iArr) {
        Launcher launcher = Launcher.getLauncher(this.mView.getContext());
        int width = getDrawableBounds(this.mView.getBackground()).width();
        float locationInDragLayer = launcher.getDragLayer().getLocationInDragLayer(this.mView, iArr);
        int paddingStart = this.mView.getPaddingStart();
        if (Utilities.isRtl(this.mView.getResources())) {
            paddingStart = (this.mView.getWidth() - width) - paddingStart;
        }
        float f = ((float) width) * locationInDragLayer;
        iArr[0] = iArr[0] + Math.round((((float) paddingStart) * locationInDragLayer) + ((f - ((float) bitmap.getWidth())) / 2.0f) + ((float) this.mPositionShift.x));
        iArr[1] = iArr[1] + Math.round((((locationInDragLayer * ((float) this.mView.getHeight())) - ((float) bitmap.getHeight())) / 2.0f) + ((float) this.mPositionShift.y));
        return f / ((float) launcher.getDeviceProfile().iconSizePx);
    }
}
