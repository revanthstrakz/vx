package com.android.launcher3.graphics;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

@TargetApi(24)
public class FixedScaleDrawable extends DrawableWrapper {
    private static final float LEGACY_ICON_SCALE = 0.46669f;
    private float mScaleX = LEGACY_ICON_SCALE;
    private float mScaleY = LEGACY_ICON_SCALE;

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) {
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Theme theme) {
    }

    public FixedScaleDrawable() {
        super(new ColorDrawable());
    }

    public void draw(Canvas canvas) {
        int save = canvas.save(1);
        canvas.scale(this.mScaleX, this.mScaleY, getBounds().exactCenterX(), getBounds().exactCenterY());
        super.draw(canvas);
        canvas.restoreToCount(save);
    }

    public void setScale(float f) {
        float intrinsicHeight = (float) getIntrinsicHeight();
        float intrinsicWidth = (float) getIntrinsicWidth();
        float f2 = f * LEGACY_ICON_SCALE;
        this.mScaleX = f2;
        this.mScaleY = f2;
        if (intrinsicHeight > intrinsicWidth && intrinsicWidth > 0.0f) {
            this.mScaleX *= intrinsicWidth / intrinsicHeight;
        } else if (intrinsicWidth > intrinsicHeight && intrinsicHeight > 0.0f) {
            this.mScaleY *= intrinsicHeight / intrinsicWidth;
        }
    }
}
