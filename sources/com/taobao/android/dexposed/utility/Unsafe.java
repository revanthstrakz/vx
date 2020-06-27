package com.taobao.android.dexposed.utility;

import android.util.Log;
import java.lang.reflect.Field;

public final class Unsafe {
    private static final String TAG = "Unsafe";
    private static Object unsafe;
    private static Class unsafeClass;

    /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|9) */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x002e, code lost:
        android.util.Log.w(TAG, "Unsafe not found o.O");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x001c */
    static {
        /*
            r0 = 0
            r1 = 1
            java.lang.String r2 = "sun.misc.Unsafe"
            java.lang.Class r2 = java.lang.Class.forName(r2)     // Catch:{ Exception -> 0x001c }
            unsafeClass = r2     // Catch:{ Exception -> 0x001c }
            java.lang.Class r2 = unsafeClass     // Catch:{ Exception -> 0x001c }
            java.lang.String r3 = "theUnsafe"
            java.lang.reflect.Field r2 = r2.getDeclaredField(r3)     // Catch:{ Exception -> 0x001c }
            r2.setAccessible(r1)     // Catch:{ Exception -> 0x001c }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ Exception -> 0x001c }
            unsafe = r2     // Catch:{ Exception -> 0x001c }
            goto L_0x0035
        L_0x001c:
            java.lang.Class r2 = unsafeClass     // Catch:{ Exception -> 0x002e }
            java.lang.String r3 = "THE_ONE"
            java.lang.reflect.Field r2 = r2.getDeclaredField(r3)     // Catch:{ Exception -> 0x002e }
            r2.setAccessible(r1)     // Catch:{ Exception -> 0x002e }
            java.lang.Object r0 = r2.get(r0)     // Catch:{ Exception -> 0x002e }
            unsafe = r0     // Catch:{ Exception -> 0x002e }
            goto L_0x0035
        L_0x002e:
            java.lang.String r0 = "Unsafe"
            java.lang.String r1 = "Unsafe not found o.O"
            android.util.Log.w(r0, r1)
        L_0x0035:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.taobao.android.dexposed.utility.Unsafe.<clinit>():void");
    }

    private Unsafe() {
    }

    public static int arrayBaseOffset(Class cls) {
        try {
            return ((Integer) unsafeClass.getDeclaredMethod("arrayBaseOffset", new Class[]{Class.class}).invoke(unsafe, new Object[]{cls})).intValue();
        } catch (Exception e) {
            Log.w(TAG, e);
            return 0;
        }
    }

    public static int arrayIndexScale(Class cls) {
        try {
            return ((Integer) unsafeClass.getDeclaredMethod("arrayIndexScale", new Class[]{Class.class}).invoke(unsafe, new Object[]{cls})).intValue();
        } catch (Exception e) {
            Log.w(TAG, e);
            return 0;
        }
    }

    public static long objectFieldOffset(Field field) {
        try {
            return ((Long) unsafeClass.getDeclaredMethod("objectFieldOffset", new Class[]{Field.class}).invoke(unsafe, new Object[]{field})).longValue();
        } catch (Exception e) {
            Log.w(TAG, e);
            return 0;
        }
    }

    public static int getInt(Object obj, long j) {
        try {
            return ((Integer) unsafeClass.getDeclaredMethod("getInt", new Class[]{Object.class, Long.TYPE}).invoke(unsafe, new Object[]{obj, Long.valueOf(j)})).intValue();
        } catch (Exception e) {
            Log.w(TAG, e);
            return 0;
        }
    }

    public static long getLong(Object obj, long j) {
        try {
            return ((Long) unsafeClass.getDeclaredMethod("getLong", new Class[]{Object.class, Long.TYPE}).invoke(unsafe, new Object[]{obj, Long.valueOf(j)})).longValue();
        } catch (Exception e) {
            Log.w(TAG, e);
            return 0;
        }
    }

    public static void putLong(Object obj, long j, long j2) {
        try {
            unsafeClass.getDeclaredMethod("putLongVolatile", new Class[]{Object.class, Long.TYPE, Long.TYPE}).invoke(unsafe, new Object[]{obj, Long.valueOf(j), Long.valueOf(j2)});
        } catch (Exception e) {
            try {
                unsafeClass.getDeclaredMethod("putLong", new Class[]{Object.class, Long.TYPE, Long.TYPE}).invoke(unsafe, new Object[]{obj, Long.valueOf(j), Long.valueOf(j2)});
            } catch (Exception unused) {
                Log.w(TAG, e);
            }
        }
    }

    public static void putInt(Object obj, long j, int i) {
        try {
            unsafeClass.getDeclaredMethod("putIntVolatile", new Class[]{Object.class, Long.TYPE, Integer.TYPE}).invoke(unsafe, new Object[]{obj, Long.valueOf(j), Integer.valueOf(i)});
        } catch (Exception e) {
            try {
                unsafeClass.getDeclaredMethod("putIntVolatile", new Class[]{Object.class, Long.TYPE, Integer.TYPE}).invoke(unsafe, new Object[]{obj, Long.valueOf(j), Integer.valueOf(i)});
            } catch (Exception unused) {
                Log.w(TAG, e);
            }
        }
    }

    public static long getObjectAddress(Object obj) {
        try {
            Object[] objArr = {obj};
            if (arrayIndexScale(Object[].class) == 8) {
                return getLong(objArr, (long) arrayBaseOffset(Object[].class));
            }
            return ((long) getInt(objArr, (long) arrayBaseOffset(Object[].class))) & 4294967295L;
        } catch (Exception e) {
            Log.w(TAG, e);
            return -1;
        }
    }

    public static Object getObject(long j) {
        Object[] objArr = {null};
        long arrayBaseOffset = (long) arrayBaseOffset(Object[].class);
        if (Runtime.is64Bit()) {
            putLong(objArr, arrayBaseOffset, j);
        } else {
            putInt(objArr, arrayBaseOffset, (int) j);
        }
        return objArr[0];
    }
}
