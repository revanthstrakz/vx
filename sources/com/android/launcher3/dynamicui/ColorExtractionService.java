package com.android.launcher3.dynamicui;

import android.app.WallpaperManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import com.android.launcher3.LauncherSettings.Settings;

public class ColorExtractionService extends JobService {
    private static final boolean DEBUG = false;
    private static final float HOTSEAT_FRACTION = 0.25f;
    private static final String TAG = "ColorExtractionService";
    private Handler mWorkerHandler;
    private HandlerThread mWorkerThread;

    public void onCreate() {
        super.onCreate();
        this.mWorkerThread = new HandlerThread(TAG);
        this.mWorkerThread.start();
        this.mWorkerHandler = new Handler(this.mWorkerThread.getLooper());
    }

    public void onDestroy() {
        super.onDestroy();
        this.mWorkerThread.quit();
    }

    public boolean onStartJob(final JobParameters jobParameters) {
        this.mWorkerHandler.post(new Runnable() {
            public void run() {
                WallpaperManager instance = WallpaperManager.getInstance(ColorExtractionService.this);
                int wallpaperId = ExtractionUtils.getWallpaperId(instance);
                ExtractedColors extractedColors = new ExtractedColors();
                if (instance.getWallpaperInfo() != null) {
                    extractedColors.updateHotseatPalette(null);
                    extractedColors.updateWallpaperThemePalette(null);
                } else {
                    extractedColors.updateHotseatPalette(ColorExtractionService.this.getHotseatPalette());
                    extractedColors.updateWallpaperThemePalette(ColorExtractionService.this.getWallpaperPalette());
                }
                String encodeAsString = extractedColors.encodeAsString();
                Bundle bundle = new Bundle();
                bundle.putInt(Settings.EXTRA_WALLPAPER_ID, wallpaperId);
                bundle.putString(Settings.EXTRA_EXTRACTED_COLORS, encodeAsString);
                ColorExtractionService.this.getContentResolver().call(Settings.CONTENT_URI, Settings.METHOD_SET_EXTRACTED_COLORS_AND_WALLPAPER_ID, null, bundle);
                ColorExtractionService.this.jobFinished(jobParameters, false);
            }
        });
        return true;
    }

    public boolean onStopJob(JobParameters jobParameters) {
        this.mWorkerHandler.removeCallbacksAndMessages(null);
        return true;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0057, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r4.addSuppressed(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0060, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0061, code lost:
        android.util.Log.e(TAG, "Fetching partial bitmap failed, trying old method", r1);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0060 A[ExcHandler: IOException | NullPointerException (r1v3 'e' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:3:0x000c] */
    @android.annotation.TargetApi(24)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.support.p004v7.graphics.Palette getHotseatPalette() {
        /*
            r10 = this;
            android.app.WallpaperManager r0 = android.app.WallpaperManager.getInstance(r10)
            boolean r1 = com.android.launcher3.Utilities.ATLEAST_NOUGAT
            r2 = 1061158912(0x3f400000, float:0.75)
            r3 = 0
            if (r1 == 0) goto L_0x0068
            r1 = 1
            android.os.ParcelFileDescriptor r1 = r0.getWallpaperFile(r1)     // Catch:{ IOException | NullPointerException -> 0x0060 }
            r4 = 0
            java.io.FileDescriptor r5 = r1.getFileDescriptor()     // Catch:{ Throwable -> 0x004d }
            android.graphics.BitmapRegionDecoder r5 = android.graphics.BitmapRegionDecoder.newInstance(r5, r3)     // Catch:{ Throwable -> 0x004d }
            int r6 = r5.getHeight()     // Catch:{ Throwable -> 0x004d }
            android.graphics.Rect r7 = new android.graphics.Rect     // Catch:{ Throwable -> 0x004d }
            float r8 = (float) r6     // Catch:{ Throwable -> 0x004d }
            float r8 = r8 * r2
            int r8 = (int) r8     // Catch:{ Throwable -> 0x004d }
            int r9 = r5.getWidth()     // Catch:{ Throwable -> 0x004d }
            r7.<init>(r3, r8, r9, r6)     // Catch:{ Throwable -> 0x004d }
            android.graphics.Bitmap r6 = r5.decodeRegion(r7, r4)     // Catch:{ Throwable -> 0x004d }
            r5.recycle()     // Catch:{ Throwable -> 0x004d }
            if (r6 == 0) goto L_0x0045
            android.support.v7.graphics.Palette$Builder r5 = android.support.p004v7.graphics.Palette.from(r6)     // Catch:{ Throwable -> 0x004d }
            android.support.v7.graphics.Palette$Builder r5 = r5.clearFilters()     // Catch:{ Throwable -> 0x004d }
            android.support.v7.graphics.Palette r5 = r5.generate()     // Catch:{ Throwable -> 0x004d }
            if (r1 == 0) goto L_0x0044
            r1.close()     // Catch:{ IOException | NullPointerException -> 0x0060 }
        L_0x0044:
            return r5
        L_0x0045:
            if (r1 == 0) goto L_0x0068
            r1.close()     // Catch:{ IOException | NullPointerException -> 0x0060 }
            goto L_0x0068
        L_0x004b:
            r5 = move-exception
            goto L_0x004f
        L_0x004d:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x004b }
        L_0x004f:
            if (r1 == 0) goto L_0x005f
            if (r4 == 0) goto L_0x005c
            r1.close()     // Catch:{ Throwable -> 0x0057, IOException | NullPointerException -> 0x0060 }
            goto L_0x005f
        L_0x0057:
            r1 = move-exception
            r4.addSuppressed(r1)     // Catch:{ IOException | NullPointerException -> 0x0060 }
            goto L_0x005f
        L_0x005c:
            r1.close()     // Catch:{ IOException | NullPointerException -> 0x0060 }
        L_0x005f:
            throw r5     // Catch:{ IOException | NullPointerException -> 0x0060 }
        L_0x0060:
            r1 = move-exception
            java.lang.String r4 = "ColorExtractionService"
            java.lang.String r5 = "Fetching partial bitmap failed, trying old method"
            android.util.Log.e(r4, r5, r1)
        L_0x0068:
            android.graphics.drawable.Drawable r0 = r0.getDrawable()
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            android.support.v7.graphics.Palette$Builder r1 = android.support.p004v7.graphics.Palette.from(r0)
            int r4 = r0.getHeight()
            float r4 = (float) r4
            float r4 = r4 * r2
            int r2 = (int) r4
            int r4 = r0.getWidth()
            int r0 = r0.getHeight()
            android.support.v7.graphics.Palette$Builder r0 = r1.setRegion(r3, r2, r4, r0)
            android.support.v7.graphics.Palette$Builder r0 = r0.clearFilters()
            android.support.v7.graphics.Palette r0 = r0.generate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dynamicui.ColorExtractionService.getHotseatPalette():android.support.v7.graphics.Palette");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0057, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r4.addSuppressed(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0060, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0061, code lost:
        android.util.Log.e(TAG, "Fetching partial bitmap failed, trying old method", r2);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0060 A[ExcHandler: IOException | NullPointerException (r2v4 'e' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:3:0x0014] */
    @android.annotation.TargetApi(24)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.support.p004v7.graphics.Palette getStatusBarPalette() {
        /*
            r8 = this;
            android.app.WallpaperManager r0 = android.app.WallpaperManager.getInstance(r8)
            android.content.res.Resources r1 = r8.getResources()
            int r2 = com.android.launcher3.C0622R.dimen.status_bar_height
            int r1 = r1.getDimensionPixelSize(r2)
            boolean r2 = com.android.launcher3.Utilities.ATLEAST_NOUGAT
            r3 = 0
            if (r2 == 0) goto L_0x0068
            r2 = 1
            android.os.ParcelFileDescriptor r2 = r0.getWallpaperFile(r2)     // Catch:{ IOException | NullPointerException -> 0x0060 }
            r4 = 0
            java.io.FileDescriptor r5 = r2.getFileDescriptor()     // Catch:{ Throwable -> 0x004d }
            android.graphics.BitmapRegionDecoder r5 = android.graphics.BitmapRegionDecoder.newInstance(r5, r3)     // Catch:{ Throwable -> 0x004d }
            android.graphics.Rect r6 = new android.graphics.Rect     // Catch:{ Throwable -> 0x004d }
            int r7 = r5.getWidth()     // Catch:{ Throwable -> 0x004d }
            r6.<init>(r3, r3, r7, r1)     // Catch:{ Throwable -> 0x004d }
            android.graphics.Bitmap r6 = r5.decodeRegion(r6, r4)     // Catch:{ Throwable -> 0x004d }
            r5.recycle()     // Catch:{ Throwable -> 0x004d }
            if (r6 == 0) goto L_0x0045
            android.support.v7.graphics.Palette$Builder r5 = android.support.p004v7.graphics.Palette.from(r6)     // Catch:{ Throwable -> 0x004d }
            android.support.v7.graphics.Palette$Builder r5 = r5.clearFilters()     // Catch:{ Throwable -> 0x004d }
            android.support.v7.graphics.Palette r5 = r5.generate()     // Catch:{ Throwable -> 0x004d }
            if (r2 == 0) goto L_0x0044
            r2.close()     // Catch:{ IOException | NullPointerException -> 0x0060 }
        L_0x0044:
            return r5
        L_0x0045:
            if (r2 == 0) goto L_0x0068
            r2.close()     // Catch:{ IOException | NullPointerException -> 0x0060 }
            goto L_0x0068
        L_0x004b:
            r5 = move-exception
            goto L_0x004f
        L_0x004d:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x004b }
        L_0x004f:
            if (r2 == 0) goto L_0x005f
            if (r4 == 0) goto L_0x005c
            r2.close()     // Catch:{ Throwable -> 0x0057, IOException | NullPointerException -> 0x0060 }
            goto L_0x005f
        L_0x0057:
            r2 = move-exception
            r4.addSuppressed(r2)     // Catch:{ IOException | NullPointerException -> 0x0060 }
            goto L_0x005f
        L_0x005c:
            r2.close()     // Catch:{ IOException | NullPointerException -> 0x0060 }
        L_0x005f:
            throw r5     // Catch:{ IOException | NullPointerException -> 0x0060 }
        L_0x0060:
            r2 = move-exception
            java.lang.String r4 = "ColorExtractionService"
            java.lang.String r5 = "Fetching partial bitmap failed, trying old method"
            android.util.Log.e(r4, r5, r2)
        L_0x0068:
            android.graphics.drawable.Drawable r0 = r0.getDrawable()
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            android.support.v7.graphics.Palette$Builder r2 = android.support.p004v7.graphics.Palette.from(r0)
            int r0 = r0.getWidth()
            android.support.v7.graphics.Palette$Builder r0 = r2.setRegion(r3, r3, r0, r1)
            android.support.v7.graphics.Palette$Builder r0 = r0.clearFilters()
            android.support.v7.graphics.Palette r0 = r0.generate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dynamicui.ColorExtractionService.getStatusBarPalette():android.support.v7.graphics.Palette");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003c, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r2.addSuppressed(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0045, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0046, code lost:
        android.util.Log.e(TAG, "Fetching partial bitmap failed, trying old method", r1);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0045 A[ExcHandler: IOException | NullPointerException (r1v2 'e' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:3:0x0009] */
    @android.annotation.TargetApi(24)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.support.p004v7.graphics.Palette getWallpaperPalette() {
        /*
            r4 = this;
            android.app.WallpaperManager r0 = android.app.WallpaperManager.getInstance(r4)
            boolean r1 = com.android.launcher3.Utilities.ATLEAST_NOUGAT
            if (r1 == 0) goto L_0x004d
            r1 = 1
            android.os.ParcelFileDescriptor r1 = r0.getWallpaperFile(r1)     // Catch:{ IOException | NullPointerException -> 0x0045 }
            r2 = 0
            java.io.FileDescriptor r3 = r1.getFileDescriptor()     // Catch:{ Throwable -> 0x0032 }
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFileDescriptor(r3)     // Catch:{ Throwable -> 0x0032 }
            if (r3 == 0) goto L_0x002a
            android.support.v7.graphics.Palette$Builder r3 = android.support.p004v7.graphics.Palette.from(r3)     // Catch:{ Throwable -> 0x0032 }
            android.support.v7.graphics.Palette$Builder r3 = r3.clearFilters()     // Catch:{ Throwable -> 0x0032 }
            android.support.v7.graphics.Palette r3 = r3.generate()     // Catch:{ Throwable -> 0x0032 }
            if (r1 == 0) goto L_0x0029
            r1.close()     // Catch:{ IOException | NullPointerException -> 0x0045 }
        L_0x0029:
            return r3
        L_0x002a:
            if (r1 == 0) goto L_0x004d
            r1.close()     // Catch:{ IOException | NullPointerException -> 0x0045 }
            goto L_0x004d
        L_0x0030:
            r3 = move-exception
            goto L_0x0034
        L_0x0032:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0030 }
        L_0x0034:
            if (r1 == 0) goto L_0x0044
            if (r2 == 0) goto L_0x0041
            r1.close()     // Catch:{ Throwable -> 0x003c, IOException | NullPointerException -> 0x0045 }
            goto L_0x0044
        L_0x003c:
            r1 = move-exception
            r2.addSuppressed(r1)     // Catch:{ IOException | NullPointerException -> 0x0045 }
            goto L_0x0044
        L_0x0041:
            r1.close()     // Catch:{ IOException | NullPointerException -> 0x0045 }
        L_0x0044:
            throw r3     // Catch:{ IOException | NullPointerException -> 0x0045 }
        L_0x0045:
            r1 = move-exception
            java.lang.String r2 = "ColorExtractionService"
            java.lang.String r3 = "Fetching partial bitmap failed, trying old method"
            android.util.Log.e(r2, r3, r1)
        L_0x004d:
            android.graphics.drawable.Drawable r0 = r0.getDrawable()
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            android.support.v7.graphics.Palette$Builder r0 = android.support.p004v7.graphics.Palette.from(r0)
            android.support.v7.graphics.Palette$Builder r0 = r0.clearFilters()
            android.support.v7.graphics.Palette r0 = r0.generate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dynamicui.ColorExtractionService.getWallpaperPalette():android.support.v7.graphics.Palette");
    }
}
