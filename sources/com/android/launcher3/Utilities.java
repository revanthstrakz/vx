package com.android.launcher3;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.PowerManager;
import android.os.TransactionTooLargeException;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TtsSpan.TextBuilder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class Utilities {
    public static final String ALLOW_ROTATION_PREFERENCE_KEY = "pref_allowRotation";
    public static final boolean ATLEAST_LOLLIPOP_MR1;
    public static final boolean ATLEAST_MARSHMALLOW = (VERSION.SDK_INT >= 23);
    public static final boolean ATLEAST_NOUGAT = (VERSION.SDK_INT >= 24);
    public static final boolean ATLEAST_NOUGAT_MR1 = (VERSION.SDK_INT >= 25);
    public static final boolean ATLEAST_OREO = (VERSION.SDK_INT >= 26);
    public static final boolean ATLEAST_OREO_MR1 = (VERSION.SDK_INT >= 27);
    public static final int COLOR_EXTRACTION_JOB_ID = 1;
    private static final int CORE_POOL_SIZE = (CPU_COUNT + 1);
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final String EXTRA_WALLPAPER_OFFSET = "com.android.launcher3.WALLPAPER_OFFSET";
    public static final boolean IS_DEBUG_DEVICE = Build.TYPE.toLowerCase().contains("debug");
    private static final int KEEP_ALIVE = 1;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT * 2) + 1);
    private static final String TAG = "Launcher.Utilities";
    public static final String THEME_OVERRIDE_KEY = "pref_override_theme";
    public static final Executor THREAD_POOL_EXECUTOR;
    public static final int WALLPAPER_COMPAT_JOB_ID = 2;
    private static final Matrix sInverseMatrix = new Matrix();
    private static final int[] sLoc0 = new int[2];
    private static final int[] sLoc1 = new int[2];
    private static final Matrix sMatrix = new Matrix();
    private static final float[] sPoint = new float[2];
    private static final Pattern sTrimPattern = Pattern.compile("^[\\s|\\p{javaSpaceChar}]*(.*)[\\s|\\p{javaSpaceChar}]*$");

    public static int longCompare(long j, long j2) {
        int i = (j > j2 ? 1 : (j == j2 ? 0 : -1));
        if (i < 0) {
            return -1;
        }
        return i == 0 ? 0 : 1;
    }

    static {
        boolean z = false;
        if (VERSION.SDK_INT >= 22) {
            z = true;
        }
        ATLEAST_LOLLIPOP_MR1 = z;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS, new LinkedBlockingQueue());
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    public static int getThemeHints(Context context, int i) {
        String string = getPrefs(context).getString(THEME_OVERRIDE_KEY, "");
        if (TextUtils.isEmpty(string)) {
            return i;
        }
        return Integer.valueOf(string).intValue();
    }

    public static boolean isPropertyEnabled(String str) {
        return Log.isLoggable(str, 2);
    }

    public static boolean isAllowRotationPrefEnabled(Context context) {
        return getPrefs(context).getBoolean(ALLOW_ROTATION_PREFERENCE_KEY, getAllowRotationDefaultValue(context));
    }

    public static boolean getAllowRotationDefaultValue(Context context) {
        boolean z = false;
        if (!ATLEAST_NOUGAT) {
            return false;
        }
        Resources resources = context.getResources();
        if ((resources.getConfiguration().smallestScreenWidthDp * resources.getDisplayMetrics().densityDpi) / DisplayMetrics.DENSITY_DEVICE_STABLE >= 600) {
            z = true;
        }
        return z;
    }

    public static float getDescendantCoordRelativeToAncestor(View view, View view2, int[] iArr, boolean z) {
        sPoint[0] = (float) iArr[0];
        sPoint[1] = (float) iArr[1];
        float f = 1.0f;
        View view3 = view;
        while (view3 != view2 && view3 != null) {
            if (view3 != view || z) {
                float[] fArr = sPoint;
                fArr[0] = fArr[0] - ((float) view3.getScrollX());
                float[] fArr2 = sPoint;
                fArr2[1] = fArr2[1] - ((float) view3.getScrollY());
            }
            view3.getMatrix().mapPoints(sPoint);
            float[] fArr3 = sPoint;
            fArr3[0] = fArr3[0] + ((float) view3.getLeft());
            float[] fArr4 = sPoint;
            fArr4[1] = fArr4[1] + ((float) view3.getTop());
            f *= view3.getScaleX();
            view3 = (View) view3.getParent();
        }
        iArr[0] = Math.round(sPoint[0]);
        iArr[1] = Math.round(sPoint[1]);
        return f;
    }

    public static void mapCoordInSelfToDescendant(View view, View view2, int[] iArr) {
        sMatrix.reset();
        while (view != view2) {
            sMatrix.postTranslate((float) (-view.getScrollX()), (float) (-view.getScrollY()));
            sMatrix.postConcat(view.getMatrix());
            sMatrix.postTranslate((float) view.getLeft(), (float) view.getTop());
            view = (View) view.getParent();
        }
        sMatrix.postTranslate((float) (-view.getScrollX()), (float) (-view.getScrollY()));
        sMatrix.invert(sInverseMatrix);
        sPoint[0] = (float) iArr[0];
        sPoint[1] = (float) iArr[1];
        sInverseMatrix.mapPoints(sPoint);
        iArr[0] = Math.round(sPoint[0]);
        iArr[1] = Math.round(sPoint[1]);
    }

    public static boolean pointInView(View view, float f, float f2, float f3) {
        float f4 = -f3;
        return f >= f4 && f2 >= f4 && f < ((float) view.getWidth()) + f3 && f2 < ((float) view.getHeight()) + f3;
    }

    public static int[] getCenterDeltaInScreenSpace(View view, View view2) {
        view.getLocationInWindow(sLoc0);
        view2.getLocationInWindow(sLoc1);
        int[] iArr = sLoc0;
        iArr[0] = (int) (((float) iArr[0]) + ((((float) view.getMeasuredWidth()) * view.getScaleX()) / 2.0f));
        int[] iArr2 = sLoc0;
        iArr2[1] = (int) (((float) iArr2[1]) + ((((float) view.getMeasuredHeight()) * view.getScaleY()) / 2.0f));
        int[] iArr3 = sLoc1;
        iArr3[0] = (int) (((float) iArr3[0]) + ((((float) view2.getMeasuredWidth()) * view2.getScaleX()) / 2.0f));
        int[] iArr4 = sLoc1;
        iArr4[1] = (int) (((float) iArr4[1]) + ((((float) view2.getMeasuredHeight()) * view2.getScaleY()) / 2.0f));
        return new int[]{sLoc1[0] - sLoc0[0], sLoc1[1] - sLoc0[1]};
    }

    public static void scaleRectAboutCenter(Rect rect, float f) {
        if (f != 1.0f) {
            int centerX = rect.centerX();
            int centerY = rect.centerY();
            rect.offset(-centerX, -centerY);
            rect.left = (int) ((((float) rect.left) * f) + 0.5f);
            rect.top = (int) ((((float) rect.top) * f) + 0.5f);
            rect.right = (int) ((((float) rect.right) * f) + 0.5f);
            rect.bottom = (int) ((((float) rect.bottom) * f) + 0.5f);
            rect.offset(centerX, centerY);
        }
    }

    public static float shrinkRect(Rect rect, float f, float f2) {
        float min = Math.min(Math.min(f, f2), 1.0f);
        if (min < 1.0f) {
            int width = (int) (((float) rect.width()) * (f - min) * 0.5f);
            rect.left += width;
            rect.right -= width;
            int height = (int) (((float) rect.height()) * (f2 - min) * 0.5f);
            rect.top += height;
            rect.bottom -= height;
        }
        return min;
    }

    public static boolean isSystemApp(Context context, Intent intent) {
        String str;
        PackageManager packageManager = context.getPackageManager();
        ComponentName component = intent.getComponent();
        if (component == null) {
            ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 65536);
            str = (resolveActivity == null || resolveActivity.activityInfo == null) ? null : resolveActivity.activityInfo.packageName;
        } else {
            str = component.getPackageName();
        }
        if (str == null) {
            return false;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
            boolean z = true;
            if (packageInfo == null || packageInfo.applicationInfo == null || (packageInfo.applicationInfo.flags & 1) == 0) {
                z = false;
            }
            return z;
        } catch (NameNotFoundException unused) {
            return false;
        }
    }

    public static int findDominantColorByHue(Bitmap bitmap, int i) {
        Bitmap bitmap2 = bitmap;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int sqrt = (int) Math.sqrt((double) ((height * width) / i));
        char c = 1;
        if (sqrt < 1) {
            sqrt = 1;
        }
        float[] fArr = new float[3];
        float[] fArr2 = new float[360];
        char c2 = 0;
        int i2 = 0;
        float f = -1.0f;
        int i3 = -1;
        while (i2 < height) {
            int i4 = i3;
            float f2 = f;
            for (int i5 = 0; i5 < width; i5 += sqrt) {
                int pixel = bitmap2.getPixel(i5, i2);
                if (((pixel >> 24) & 255) >= 128) {
                    Color.colorToHSV(pixel | -16777216, fArr);
                    int i6 = (int) fArr[0];
                    if (i6 >= 0 && i6 < fArr2.length) {
                        fArr2[i6] = fArr2[i6] + (fArr[1] * fArr[2]);
                        if (fArr2[i6] > f2) {
                            f2 = fArr2[i6];
                            i4 = i6;
                        }
                    }
                }
            }
            i2 += sqrt;
            f = f2;
            i3 = i4;
        }
        SparseArray sparseArray = new SparseArray();
        int i7 = 0;
        int i8 = -16777216;
        float f3 = -1.0f;
        while (i7 < height) {
            int i9 = i8;
            int i10 = 0;
            while (i10 < width) {
                int pixel2 = bitmap2.getPixel(i10, i7) | -16777216;
                Color.colorToHSV(pixel2, fArr);
                if (((int) fArr[c2]) == i3) {
                    float f4 = fArr[c];
                    float f5 = fArr[2];
                    int i11 = ((int) (f4 * 100.0f)) + ((int) (f5 * 10000.0f));
                    float f6 = f4 * f5;
                    Float f7 = (Float) sparseArray.get(i11);
                    if (f7 != null) {
                        f6 = f7.floatValue() + f6;
                    }
                    sparseArray.put(i11, Float.valueOf(f6));
                    if (f6 > f3) {
                        i9 = pixel2;
                        f3 = f6;
                    }
                }
                i10 += sqrt;
                c = 1;
                c2 = 0;
            }
            i7 += sqrt;
            i8 = i9;
            c = 1;
            c2 = 0;
        }
        return i8;
    }

    static Pair<String, Resources> findSystemApk(String str, PackageManager packageManager) {
        for (ResolveInfo resolveInfo : packageManager.queryBroadcastReceivers(new Intent(str), 0)) {
            if (!(resolveInfo.activityInfo == null || (resolveInfo.activityInfo.applicationInfo.flags & 1) == 0)) {
                String str2 = resolveInfo.activityInfo.packageName;
                try {
                    return Pair.create(str2, packageManager.getResourcesForApplication(str2));
                } catch (NameNotFoundException unused) {
                    String str3 = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to find resources for ");
                    sb.append(str2);
                    Log.w(str3, sb.toString());
                }
            }
        }
        return null;
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight() * 4);
        try {
            bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException unused) {
            Log.w(TAG, "Could not write bitmap");
            return null;
        }
    }

    public static String trim(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return sTrimPattern.matcher(charSequence).replaceAll("$1");
    }

    public static int calculateTextHeight(float f) {
        Paint paint = new Paint();
        paint.setTextSize(f);
        FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) Math.ceil((double) (fontMetrics.bottom - fontMetrics.top));
    }

    public static void println(String str, Object... objArr) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(": ");
        boolean z = true;
        for (Object obj : objArr) {
            if (z) {
                z = false;
            } else {
                sb.append(", ");
            }
            sb.append(obj);
        }
        System.out.println(sb.toString());
    }

    public static boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == 1;
    }

    public static boolean isLauncherAppTarget(Intent intent) {
        boolean z = false;
        if (intent == null || !"android.intent.action.MAIN".equals(intent.getAction()) || intent.getComponent() == null || intent.getCategories() == null || intent.getCategories().size() != 1 || !intent.hasCategory("android.intent.category.LAUNCHER") || !TextUtils.isEmpty(intent.getDataString())) {
            return false;
        }
        Bundle extras = intent.getExtras();
        if (extras == null || extras.keySet().isEmpty()) {
            z = true;
        }
        return z;
    }

    public static float dpiFromPx(int i, DisplayMetrics displayMetrics) {
        return ((float) i) / (((float) displayMetrics.densityDpi) / 160.0f);
    }

    public static int pxFromDp(float f, DisplayMetrics displayMetrics) {
        return Math.round(TypedValue.applyDimension(1, f, displayMetrics));
    }

    public static int pxFromSp(float f, DisplayMetrics displayMetrics) {
        return Math.round(TypedValue.applyDimension(2, f, displayMetrics));
    }

    public static String createDbSelectionQuery(String str, Iterable<?> iterable) {
        return String.format(Locale.ENGLISH, "%s IN (%s)", new Object[]{str, TextUtils.join(", ", iterable)});
    }

    public static boolean isBootCompleted() {
        return "1".equals(getSystemProperty("sys.boot_completed", "1"));
    }

    public static String getSystemProperty(String str, String str2) {
        try {
            String str3 = (String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class}).invoke(null, new Object[]{str});
            if (!TextUtils.isEmpty(str3)) {
                return str3;
            }
            return str2;
        } catch (Exception unused) {
            Log.d(TAG, "Unable to read system properties");
        }
    }

    public static int boundToRange(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i, i3));
    }

    public static float boundToRange(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f, f3));
    }

    public static CharSequence wrapForTts(CharSequence charSequence, String str) {
        SpannableString spannableString = new SpannableString(charSequence);
        spannableString.setSpan(new TextBuilder(str).build(), 0, spannableString.length(), 18);
        return spannableString;
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, 0);
    }

    public static SharedPreferences getDevicePrefs(Context context) {
        return context.getSharedPreferences(LauncherFiles.DEVICE_PREFERENCES_KEY, 0);
    }

    public static boolean isPowerSaverOn(Context context) {
        return ((PowerManager) context.getSystemService("power")).isPowerSaveMode();
    }

    public static boolean isWallpaperAllowed(Context context) {
        if (ATLEAST_NOUGAT) {
            try {
                WallpaperManager wallpaperManager = (WallpaperManager) context.getSystemService(WallpaperManager.class);
                return ((Boolean) wallpaperManager.getClass().getDeclaredMethod("isSetWallpaperAllowed", new Class[0]).invoke(wallpaperManager, new Object[0])).booleanValue();
            } catch (Exception unused) {
            }
        }
        return true;
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    public static boolean containsAll(Bundle bundle, Bundle bundle2) {
        for (String str : bundle2.keySet()) {
            Object obj = bundle2.get(str);
            Object obj2 = bundle.get(str);
            if (obj == null) {
                if (obj2 != null) {
                    return false;
                }
            } else if (!obj.equals(obj2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static void sendCustomAccessibilityEvent(View view, int i, String str) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) view.getContext().getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i);
            view.onInitializeAccessibilityEvent(obtain);
            obtain.getText().add(str);
            accessibilityManager.sendAccessibilityEvent(obtain);
        }
    }

    public static boolean isBinderSizeError(Exception exc) {
        return (exc.getCause() instanceof TransactionTooLargeException) || (exc.getCause() instanceof DeadObjectException);
    }

    public static <T> T getOverrideObject(Class<T> cls, Context context, int i) {
        String string = context.getString(i);
        if (!TextUtils.isEmpty(string)) {
            try {
                return Class.forName(string).getDeclaredConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
            } catch (ClassCastException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                Log.e(TAG, "Bad overriden class", e);
            }
        }
        try {
            return cls.newInstance();
        } catch (IllegalAccessException | InstantiationException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static <T> HashSet<T> singletonHashSet(T t) {
        HashSet<T> hashSet = new HashSet<>(1);
        hashSet.add(t);
        return hashSet;
    }
}
