package com.google.android.apps.nexuslauncher.clock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import com.android.launcher3.FastBitmapDrawable;
import java.util.TimeZone;

public class AutoUpdateClock extends FastBitmapDrawable implements Runnable {
    private ClockLayers mLayers;

    AutoUpdateClock(Bitmap bitmap, ClockLayers clockLayers) {
        super(bitmap);
        this.mLayers = clockLayers;
    }

    private void rescheduleUpdate() {
        unscheduleSelf(this);
        long uptimeMillis = SystemClock.uptimeMillis();
        scheduleSelf(this, (uptimeMillis - (uptimeMillis % 1000)) + 1000);
    }

    /* access modifiers changed from: 0000 */
    public void updateLayers(ClockLayers clockLayers) {
        this.mLayers = clockLayers;
        if (this.mLayers != null) {
            this.mLayers.mDrawable.setBounds(getBounds());
        }
        invalidateSelf();
    }

    /* access modifiers changed from: 0000 */
    public void setTimeZone(TimeZone timeZone) {
        if (this.mLayers != null) {
            this.mLayers.setTimeZone(timeZone);
            invalidateSelf();
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mLayers != null) {
            this.mLayers.updateAngles();
            Rect bounds = getBounds();
            canvas.scale(this.mLayers.scale, this.mLayers.scale, bounds.exactCenterX(), bounds.exactCenterY());
            this.mLayers.mDrawable.draw(canvas);
            rescheduleUpdate();
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        if (this.mLayers != null) {
            this.mLayers.mDrawable.setBounds(rect);
        }
    }

    public void run() {
        if (this.mLayers.updateAngles()) {
            invalidateSelf();
        } else {
            rescheduleUpdate();
        }
    }
}
