package com.android.launcher3.compat;

import android.app.WallpaperManager;
import android.app.job.JobInfo.Builder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.p001v4.graphics.ColorUtils;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.WallpaperManagerCompat.OnColorsChangedListenerCompat;
import java.util.ArrayList;
import java.util.Iterator;

public class WallpaperManagerCompatVL extends WallpaperManagerCompat {
    private static final String ACTION_EXTRACTION_COMPLETE = "com.android.launcher3.compat.WallpaperManagerCompatVL.EXTRACTION_COMPLETE";
    private static final String KEY_COLORS = "wallpaper_parsed_colors";
    private static final String TAG = "WMCompatVL";
    private static final String VERSION_PREFIX = "1,";
    private WallpaperColorsCompat mColorsCompat;
    private final Context mContext;
    private final ArrayList<OnColorsChangedListenerCompat> mListeners = new ArrayList<>();

    public static class ColorExtractionService extends JobService implements Runnable {
        private static final float BRIGHT_IMAGE_MEAN_LUMINANCE = 0.75f;
        private static final float DARK_PIXEL_LUMINANCE = 0.45f;
        private static final float DARK_THEME_MEAN_LUMINANCE = 0.25f;
        private static final float MAX_DARK_AREA = 0.05f;
        private static final int MAX_WALLPAPER_EXTRACTION_AREA = 12544;
        private Handler mWorkerHandler;
        private HandlerThread mWorkerThread;

        public void onCreate() {
            super.onCreate();
            this.mWorkerThread = new HandlerThread("ColorExtractionService");
            this.mWorkerThread.start();
            this.mWorkerHandler = new Handler(this.mWorkerThread.getLooper());
        }

        public void onDestroy() {
            super.onDestroy();
            this.mWorkerThread.quit();
        }

        public boolean onStartJob(JobParameters jobParameters) {
            this.mWorkerHandler.post(this);
            return true;
        }

        public boolean onStopJob(JobParameters jobParameters) {
            this.mWorkerHandler.removeCallbacksAndMessages(null);
            return true;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:43:0x00a5, code lost:
            r0 = e;
         */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0095  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00a5 A[ExcHandler: IOException | RuntimeException (e java.lang.Throwable), Splitter:B:39:0x009d] */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x00b5 A[SYNTHETIC, Splitter:B:49:0x00b5] */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x00e0  */
        /* JADX WARNING: Removed duplicated region for block: B:65:0x0122  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r18 = this;
                r1 = r18
                int r2 = com.android.launcher3.compat.WallpaperManagerCompatVL.getWallpaperId(r18)
                android.app.WallpaperManager r3 = android.app.WallpaperManager.getInstance(r18)
                android.app.WallpaperInfo r0 = r3.getWallpaperInfo()
                r4 = 4668121751257874432(0x40c8800000000000, double:12544.0)
                r6 = 12544(0x3100, float:1.7578E-41)
                r7 = 0
                r8 = 0
                if (r0 == 0) goto L_0x0024
                android.content.pm.PackageManager r3 = r18.getPackageManager()
                android.graphics.drawable.Drawable r0 = r0.loadThumbnail(r3)
                r10 = r7
                goto L_0x00c4
            L_0x0024:
                boolean r0 = com.android.launcher3.Utilities.ATLEAST_NOUGAT
                if (r0 == 0) goto L_0x00b2
                r0 = 1
                android.os.ParcelFileDescriptor r9 = r3.getWallpaperFile(r0)     // Catch:{ IOException | RuntimeException -> 0x00a7 }
                java.io.FileDescriptor r0 = r9.getFileDescriptor()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                android.graphics.BitmapRegionDecoder r0 = android.graphics.BitmapRegionDecoder.newInstance(r0, r8)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r10 = r0.getWidth()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r11 = r0.getHeight()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r10 = r10 * r11
                android.graphics.BitmapFactory$Options r11 = new android.graphics.BitmapFactory$Options     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r11.<init>()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                if (r10 <= r6) goto L_0x0061
                double r12 = (double) r10     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                double r12 = r12 / r4
                double r12 = java.lang.Math.log(r12)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r14 = 4611686018427387904(0x4000000000000000, double:2.0)
                double r16 = java.lang.Math.log(r14)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                double r16 = r16 * r14
                double r12 = r12 / r16
                double r12 = java.lang.Math.floor(r12)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                double r12 = java.lang.Math.pow(r14, r12)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r10 = (int) r12     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r11.inSampleSize = r10     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
            L_0x0061:
                android.graphics.Rect r10 = new android.graphics.Rect     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r12 = r0.getWidth()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                int r13 = r0.getHeight()     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r10.<init>(r8, r8, r12, r13)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                android.graphics.Bitmap r10 = r0.decodeRegion(r10, r11)     // Catch:{ Throwable -> 0x008b, all -> 0x0086 }
                r0.recycle()     // Catch:{ Throwable -> 0x0082, all -> 0x007e }
                if (r9 == 0) goto L_0x00b3
                r9.close()     // Catch:{ IOException | RuntimeException -> 0x007b }
                goto L_0x00b3
            L_0x007b:
                r0 = move-exception
                r12 = r10
                goto L_0x00a9
            L_0x007e:
                r0 = move-exception
                r11 = r7
                r12 = r10
                goto L_0x0092
            L_0x0082:
                r0 = move-exception
                r11 = r10
                r10 = r0
                goto L_0x008e
            L_0x0086:
                r0 = move-exception
                r10 = r0
                r11 = r7
                r12 = r11
                goto L_0x0093
            L_0x008b:
                r0 = move-exception
                r10 = r0
                r11 = r7
            L_0x008e:
                throw r10     // Catch:{ all -> 0x008f }
            L_0x008f:
                r0 = move-exception
                r12 = r11
                r11 = r10
            L_0x0092:
                r10 = r0
            L_0x0093:
                if (r9 == 0) goto L_0x00a4
                if (r11 == 0) goto L_0x00a1
                r9.close()     // Catch:{ Throwable -> 0x009b }
                goto L_0x00a4
            L_0x009b:
                r0 = move-exception
                r9 = r0
                r11.addSuppressed(r9)     // Catch:{ IOException | RuntimeException -> 0x00a5, IOException | RuntimeException -> 0x00a5 }
                goto L_0x00a4
            L_0x00a1:
                r9.close()     // Catch:{ IOException | RuntimeException -> 0x00a5, IOException | RuntimeException -> 0x00a5 }
            L_0x00a4:
                throw r10     // Catch:{ IOException | RuntimeException -> 0x00a5, IOException | RuntimeException -> 0x00a5 }
            L_0x00a5:
                r0 = move-exception
                goto L_0x00a9
            L_0x00a7:
                r0 = move-exception
                r12 = r7
            L_0x00a9:
                java.lang.String r9 = "WMCompatVL"
                java.lang.String r10 = "Fetching partial bitmap failed, trying old method"
                android.util.Log.e(r9, r10, r0)
                r10 = r12
                goto L_0x00b3
            L_0x00b2:
                r10 = r7
            L_0x00b3:
                if (r10 != 0) goto L_0x00c3
                android.graphics.drawable.Drawable r0 = r3.getDrawable()     // Catch:{ RuntimeException -> 0x00ba }
                goto L_0x00c4
            L_0x00ba:
                r0 = move-exception
                r3 = r0
                java.lang.String r0 = "WMCompatVL"
                java.lang.String r9 = "Failed to extract the wallpaper drawable"
                android.util.Log.e(r0, r9, r3)
            L_0x00c3:
                r0 = r7
            L_0x00c4:
                if (r0 == 0) goto L_0x010f
                int r3 = r0.getIntrinsicWidth()
                if (r3 <= 0) goto L_0x010f
                int r3 = r0.getIntrinsicHeight()
                if (r3 <= 0) goto L_0x010f
                int r3 = r0.getIntrinsicWidth()
                int r7 = r0.getIntrinsicHeight()
                int r3 = r3 * r7
                r9 = 4607182418800017408(0x3ff0000000000000, double:1.0)
                if (r3 <= r6) goto L_0x00e6
                double r6 = (double) r3
                double r4 = r4 / r6
                double r9 = java.lang.Math.sqrt(r4)
            L_0x00e6:
                int r3 = r0.getIntrinsicWidth()
                double r3 = (double) r3
                double r3 = r3 * r9
                int r3 = (int) r3
                int r4 = r0.getIntrinsicHeight()
                double r4 = (double) r4
                double r4 = r4 * r9
                int r4 = (int) r4
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888
                android.graphics.Bitmap r10 = android.graphics.Bitmap.createBitmap(r3, r4, r5)
                android.graphics.Canvas r3 = new android.graphics.Canvas
                r3.<init>(r10)
                int r4 = r10.getWidth()
                int r5 = r10.getHeight()
                r0.setBounds(r8, r8, r4, r5)
                r0.draw(r3)
            L_0x010f:
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "1,"
                r0.append(r3)
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                if (r10 == 0) goto L_0x0198
                android.support.v7.graphics.Palette$Builder r2 = android.support.p004v7.graphics.Palette.from(r10)
                android.support.v7.graphics.Palette r2 = r2.generate()
                int r3 = calculateDarkHints(r10)
                r10.recycle()
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>(r0)
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                java.util.List r2 = r2.getSwatches()
                java.util.Iterator r2 = r2.iterator()
            L_0x0143:
                boolean r5 = r2.hasNext()
                if (r5 == 0) goto L_0x0168
                java.lang.Object r5 = r2.next()
                android.support.v7.graphics.Palette$Swatch r5 = (android.support.p004v7.graphics.Palette.Swatch) r5
                android.util.Pair r6 = new android.util.Pair
                int r7 = r5.getRgb()
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                int r5 = r5.getPopulation()
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r6.<init>(r7, r5)
                r0.add(r6)
                goto L_0x0143
            L_0x0168:
                com.android.launcher3.compat.WallpaperManagerCompatVL$ColorExtractionService$1 r2 = new com.android.launcher3.compat.WallpaperManagerCompatVL$ColorExtractionService$1
                r2.<init>()
                java.util.Collections.sort(r0, r2)
                r2 = 44
                r4.append(r2)
                r4.append(r3)
            L_0x0178:
                r3 = 3
                int r5 = r0.size()
                int r3 = java.lang.Math.min(r3, r5)
                if (r8 >= r3) goto L_0x0194
                r4.append(r2)
                java.lang.Object r3 = r0.get(r8)
                android.util.Pair r3 = (android.util.Pair) r3
                java.lang.Object r3 = r3.first
                r4.append(r3)
                int r8 = r8 + 1
                goto L_0x0178
            L_0x0194:
                java.lang.String r0 = r4.toString()
            L_0x0198:
                android.content.Intent r2 = new android.content.Intent
                java.lang.String r3 = "com.android.launcher3.compat.WallpaperManagerCompatVL.EXTRACTION_COMPLETE"
                r2.<init>(r3)
                java.lang.String r3 = r18.getPackageName()
                android.content.Intent r2 = r2.setPackage(r3)
                java.lang.String r3 = "wallpaper_parsed_colors"
                android.content.Intent r0 = r2.putExtra(r3, r0)
                r1.sendBroadcast(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.compat.WallpaperManagerCompatVL.ColorExtractionService.run():void");
        }

        private static int calculateDarkHints(Bitmap bitmap) {
            int i = 0;
            if (bitmap == null) {
                return 0;
            }
            int[] iArr = new int[(bitmap.getWidth() * bitmap.getHeight())];
            double d = 0.0d;
            int length = (int) (((float) iArr.length) * MAX_DARK_AREA);
            bitmap.getPixels(iArr, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            float[] fArr = new float[3];
            int i2 = 0;
            for (int i3 = 0; i3 < iArr.length; i3++) {
                ColorUtils.colorToHSL(iArr[i3], fArr);
                float f = fArr[2];
                int alpha = Color.alpha(iArr[i3]);
                if (f < DARK_PIXEL_LUMINANCE && alpha != 0) {
                    i2++;
                }
                d += (double) f;
            }
            double length2 = d / ((double) iArr.length);
            if (length2 > 0.75d && i2 < length) {
                i = 1;
            }
            if (length2 < 0.25d) {
                i |= 2;
            }
            return i;
        }
    }

    WallpaperManagerCompatVL(Context context) {
        int i;
        PermissionInfo[] permissionInfoArr;
        this.mContext = context;
        String string = Utilities.getDevicePrefs(this.mContext).getString(KEY_COLORS, "");
        if (string.startsWith(VERSION_PREFIX)) {
            Pair parseValue = parseValue(string);
            i = ((Integer) parseValue.first).intValue();
            this.mColorsCompat = (WallpaperColorsCompat) parseValue.second;
        } else {
            i = -1;
        }
        if (i == -1 || i != getWallpaperId(context)) {
            reloadColors();
        }
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                WallpaperManagerCompatVL.this.reloadColors();
            }
        }, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"));
        String str = null;
        try {
            for (PermissionInfo permissionInfo : context.getPackageManager().getPackageInfo(context.getPackageName(), 4096).permissions) {
                if ((permissionInfo.protectionLevel & 2) != 0) {
                    str = permissionInfo.name;
                }
            }
        } catch (NameNotFoundException e) {
            Log.d(TAG, "Unable to get permission info", e);
        }
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                WallpaperManagerCompatVL.this.handleResult(intent.getStringExtra(WallpaperManagerCompatVL.KEY_COLORS));
            }
        }, new IntentFilter(ACTION_EXTRACTION_COMPLETE), str, new Handler());
    }

    @Nullable
    public WallpaperColorsCompat getWallpaperColors(int i) {
        if (i == 1) {
            return this.mColorsCompat;
        }
        return null;
    }

    public void addOnColorsChangedListener(OnColorsChangedListenerCompat onColorsChangedListenerCompat) {
        this.mListeners.add(onColorsChangedListenerCompat);
    }

    public void updateAllListeners() {
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((OnColorsChangedListenerCompat) it.next()).onColorsChanged(this.mColorsCompat, 1);
        }
    }

    /* access modifiers changed from: private */
    public void reloadColors() {
        new Builder(2, new ComponentName(this.mContext, ColorExtractionService.class)).setMinimumLatency(0).build();
    }

    /* access modifiers changed from: private */
    public void handleResult(String str) {
        Utilities.getDevicePrefs(this.mContext).edit().putString(KEY_COLORS, str).apply();
        this.mColorsCompat = (WallpaperColorsCompat) parseValue(str).second;
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((OnColorsChangedListenerCompat) it.next()).onColorsChanged(this.mColorsCompat, 1);
        }
    }

    /* access modifiers changed from: private */
    public static final int getWallpaperId(Context context) {
        Drawable drawable;
        if (Utilities.ATLEAST_NOUGAT) {
            return ((WallpaperManager) context.getSystemService(WallpaperManager.class)).getWallpaperId(1);
        }
        try {
            drawable = WallpaperManager.getInstance(context).getDrawable();
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to create a wallpaper ID", e);
            drawable = null;
        }
        if (drawable == null) {
            return -1;
        }
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        int pixel = createBitmap.getPixel(0, 0);
        createBitmap.recycle();
        return pixel;
    }

    private static Pair<Integer, WallpaperColorsCompat> parseValue(String str) {
        String[] split = str.split(",");
        Integer valueOf = Integer.valueOf(Integer.parseInt(split[1]));
        if (split.length == 2) {
            return Pair.create(valueOf, null);
        }
        int i = 0;
        int parseInt = split.length > 2 ? Integer.parseInt(split[2]) : 0;
        int parseInt2 = split.length > 3 ? Integer.parseInt(split[3]) : 0;
        int parseInt3 = split.length > 4 ? Integer.parseInt(split[4]) : 0;
        if (split.length > 5) {
            i = Integer.parseInt(split[5]);
        }
        return Pair.create(valueOf, new WallpaperColorsCompat(parseInt2, parseInt3, i, parseInt));
    }
}
