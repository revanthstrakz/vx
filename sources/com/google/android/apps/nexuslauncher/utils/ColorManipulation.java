package com.google.android.apps.nexuslauncher.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;

public class ColorManipulation {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Matrix mMatrix = new Matrix();
    private Paint mPaint;
    private int[] mPixels;

    /* renamed from: dC */
    public static boolean m72dC(int i) {
        if (((i >> 24) & 255) < 50) {
            return true;
        }
        int i2 = (i >> 16) & 255;
        int i3 = (i >> 8) & 255;
        int i4 = i & 255;
        boolean z = false;
        if (Math.abs(i2 - i3) < 20 && Math.abs(i2 - i4) < 20 && Math.abs(i3 - i4) < 20) {
            z = true;
        }
        return z;
    }

    /* renamed from: dB */
    public boolean mo13019dB(Bitmap bitmap) {
        int i;
        int i2;
        Bitmap bitmap2;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        if (height > 64 || width > 64) {
            if (this.mBitmap == null) {
                this.mBitmap = Bitmap.createBitmap(64, 64, Config.ARGB_8888);
                this.mCanvas = new Canvas(this.mBitmap);
                this.mPaint = new Paint(1);
                this.mPaint.setFilterBitmap(true);
            }
            this.mMatrix.reset();
            this.mMatrix.setScale(64.0f / ((float) width), 64.0f / ((float) height), 0.0f, 0.0f);
            this.mCanvas.drawColor(0, Mode.SRC);
            this.mCanvas.drawBitmap(bitmap, this.mMatrix, this.mPaint);
            bitmap2 = this.mBitmap;
            i2 = 64;
            i = 64;
        } else {
            bitmap2 = bitmap;
            i = height;
            i2 = width;
        }
        int i3 = i * i2;
        resizeIfNecessary(i3);
        bitmap2.getPixels(this.mPixels, 0, i2, 0, 0, i2, i);
        for (int i4 = 0; i4 < i3; i4++) {
            if (!m72dC(this.mPixels[i4])) {
                return false;
            }
        }
        return true;
    }

    private void resizeIfNecessary(int i) {
        if (this.mPixels == null || this.mPixels.length < i) {
            this.mPixels = new int[i];
        }
    }
}
