package p015me.weishu.epic.art.entry;

import com.taobao.android.dexposed.utility.Logger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import p011de.robv.android.xposed.DexposedBridge;
import p011de.robv.android.xposed.XposedHelpers;
import p015me.weishu.epic.art.Epic;
import p015me.weishu.epic.art.Epic.MethodInfo;
import p015me.weishu.epic.art.EpicNative;
import p015me.weishu.epic.art.method.ArtMethod;

/* renamed from: me.weishu.epic.art.entry.Entry64 */
public class Entry64 {
    private static final String TAG = "Entry64";
    private static Map<Class<?>, String> bridgeMethodMap = new HashMap();

    private static int onHookInt(Object obj, Object obj2, Object[] objArr) {
        return ((Integer) DexposedBridge.handleHookedArtMethod(obj, obj2, objArr)).intValue();
    }

    private static long onHookLong(Object obj, Object obj2, Object[] objArr) {
        return ((Long) DexposedBridge.handleHookedArtMethod(obj, obj2, objArr)).longValue();
    }

    private static double onHookDouble(Object obj, Object obj2, Object[] objArr) {
        return ((Double) DexposedBridge.handleHookedArtMethod(obj, obj2, objArr)).doubleValue();
    }

    private static char onHookChar(Object obj, Object obj2, Object[] objArr) {
        return ((Character) DexposedBridge.handleHookedArtMethod(obj, obj2, objArr)).charValue();
    }

    private static short onHookShort(Object obj, Object obj2, Object[] objArr) {
        return ((Short) DexposedBridge.handleHookedArtMethod(obj, obj2, objArr)).shortValue();
    }

    private static float onHookFloat(Object obj, Object obj2, Object[] objArr) {
        return ((Float) DexposedBridge.handleHookedArtMethod(obj, obj2, objArr)).floatValue();
    }

    private static Object onHookObject(Object obj, Object obj2, Object[] objArr) {
        return DexposedBridge.handleHookedArtMethod(obj, obj2, objArr);
    }

    private static void onHookVoid(Object obj, Object obj2, Object[] objArr) {
        DexposedBridge.handleHookedArtMethod(obj, obj2, objArr);
    }

    private static boolean onHookBoolean(Object obj, Object obj2, Object[] objArr) {
        return ((Boolean) DexposedBridge.handleHookedArtMethod(obj, obj2, objArr)).booleanValue();
    }

    private static byte onHookByte(Object obj, Object obj2, Object[] objArr) {
        return ((Byte) DexposedBridge.handleHookedArtMethod(obj, obj2, objArr)).byteValue();
    }

    private static void voidBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        referenceBridge(j, j2, j3, j4, j5, j6, j7);
    }

    private static boolean booleanBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Boolean) referenceBridge(j, j2, j3, j4, j5, j6, j7)).booleanValue();
    }

    private static byte byteBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Byte) referenceBridge(j, j2, j3, j4, j5, j6, j7)).byteValue();
    }

    private static short shortBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Short) referenceBridge(j, j2, j3, j4, j5, j6, j7)).shortValue();
    }

    private static char charBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Character) referenceBridge(j, j2, j3, j4, j5, j6, j7)).charValue();
    }

    private static int intBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Integer) referenceBridge(j, j2, j3, j4, j5, j6, j7)).intValue();
    }

    private static long longBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Long) referenceBridge(j, j2, j3, j4, j5, j6, j7)).longValue();
    }

    private static float floatBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Float) referenceBridge(j, j2, j3, j4, j5, j6, j7)).floatValue();
    }

    private static double doubleBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Double) referenceBridge(j, j2, j3, j4, j5, j6, j7)).doubleValue();
    }

    private static Object referenceBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        Object obj;
        long j8 = j;
        long j9 = j3;
        long j10 = j4;
        long j11 = j5;
        long j12 = j6;
        long j13 = j7;
        Logger.m95i(TAG, "enter bridge function.");
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("self:");
        sb.append(Long.toHexString(j2));
        Logger.m92d(str, sb.toString());
        long longField = XposedHelpers.getLongField(Thread.currentThread(), "nativePeer");
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("java thread native peer:");
        sb2.append(Long.toHexString(longField));
        Logger.m92d(str2, sb2.toString());
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("struct:");
        sb3.append(Long.toHexString(j3));
        Logger.m92d(str3, sb3.toString());
        long j14 = ByteBuffer.wrap(EpicNative.get(j9, 8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
        String str4 = TAG;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("stack:");
        sb4.append(j14);
        Logger.m92d(str4, sb4.toString());
        byte[] array = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(j8).array();
        byte[] bArr = EpicNative.get(j9 + 8, 8);
        byte[] bArr2 = EpicNative.get(j9 + 16, 8);
        long j15 = ByteBuffer.wrap(EpicNative.get(j9 + 24, 8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
        String str5 = TAG;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("sourceMethod:");
        sb5.append(Long.toHexString(j15));
        Logger.m92d(str5, sb5.toString());
        MethodInfo methodInfo = Epic.getMethodInfo(j15);
        String str6 = TAG;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("originMethodInfo :");
        sb6.append(methodInfo);
        Logger.m92d(str6, sb6.toString());
        boolean z = methodInfo.isStatic;
        int i = methodInfo.paramNumber;
        Class<?>[] clsArr = methodInfo.paramTypes;
        Object[] objArr = new Object[i];
        MethodInfo methodInfo2 = methodInfo;
        if (z) {
            if (i != 0) {
                objArr[0] = wrapArgument(clsArr[0], longField, array);
                if (i != 1) {
                    objArr[1] = wrapArgument(clsArr[1], longField, bArr);
                    if (i != 2) {
                        objArr[2] = wrapArgument(clsArr[2], longField, bArr2);
                        if (i != 3) {
                            objArr[3] = wrapArgument(clsArr[3], longField, j10);
                            if (i != 4) {
                                objArr[4] = wrapArgument(clsArr[4], longField, j11);
                                if (i != 5) {
                                    objArr[5] = wrapArgument(clsArr[5], longField, j6);
                                    if (i != 6) {
                                        objArr[6] = wrapArgument(clsArr[6], longField, j7);
                                        if (i != 7) {
                                            for (int i2 = 7; i2 < i; i2++) {
                                                objArr[i2] = wrapArgument(clsArr[i2], longField, EpicNative.get(((long) (i2 * 8)) + j14 + 8, 8));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            obj = null;
        } else {
            long j16 = j14;
            long j17 = j6;
            obj = EpicNative.getObject(longField, j);
            String str7 = TAG;
            StringBuilder sb7 = new StringBuilder();
            sb7.append("this :");
            sb7.append(obj);
            Logger.m95i(str7, sb7.toString());
            if (i != 0) {
                objArr[0] = wrapArgument(clsArr[0], longField, bArr);
                if (i != 1) {
                    objArr[1] = wrapArgument(clsArr[1], longField, bArr2);
                    if (i != 2) {
                        objArr[2] = wrapArgument(clsArr[2], longField, j10);
                        if (i != 3) {
                            objArr[3] = wrapArgument(clsArr[3], longField, j11);
                            if (i != 4) {
                                objArr[4] = wrapArgument(clsArr[4], longField, j6);
                                if (i != 5) {
                                    objArr[5] = wrapArgument(clsArr[5], longField, j7);
                                    if (i != 6) {
                                        for (int i3 = 6; i3 < i; i3++) {
                                            objArr[i3] = wrapArgument(clsArr[i3], longField, EpicNative.get(j16 + ((long) (i3 * 8)) + 16, 8));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        String str8 = TAG;
        StringBuilder sb8 = new StringBuilder();
        sb8.append("arguments:");
        sb8.append(Arrays.toString(objArr));
        Logger.m95i(str8, sb8.toString());
        MethodInfo methodInfo3 = methodInfo2;
        Class<?> cls = methodInfo3.returnType;
        ArtMethod artMethod = methodInfo3.method;
        Logger.m92d(TAG, "leave bridge function");
        if (cls == Void.TYPE) {
            onHookVoid(artMethod, obj, objArr);
            return Integer.valueOf(0);
        } else if (cls == Character.TYPE) {
            return Character.valueOf(onHookChar(artMethod, obj, objArr));
        } else {
            if (cls == Byte.TYPE) {
                return Byte.valueOf(onHookByte(artMethod, obj, objArr));
            }
            if (cls == Short.TYPE) {
                return Short.valueOf(onHookShort(artMethod, obj, objArr));
            }
            if (cls == Integer.TYPE) {
                return Integer.valueOf(onHookInt(artMethod, obj, objArr));
            }
            if (cls == Long.TYPE) {
                return Long.valueOf(onHookLong(artMethod, obj, objArr));
            }
            if (cls == Float.TYPE) {
                return Float.valueOf(onHookFloat(artMethod, obj, objArr));
            }
            if (cls == Double.TYPE) {
                return Double.valueOf(onHookDouble(artMethod, obj, objArr));
            }
            if (cls == Boolean.TYPE) {
                return Boolean.valueOf(onHookBoolean(artMethod, obj, objArr));
            }
            return onHookObject(artMethod, obj, objArr);
        }
    }

    private static Object wrapArgument(Class<?> cls, long j, long j2) {
        return wrapArgument(cls, j, ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(j2).array());
    }

    private static Object wrapArgument(Class<?> cls, long j, byte[] bArr) {
        ByteBuffer order = ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN);
        if (!cls.isPrimitive()) {
            return EpicNative.getObject(j, order.getLong());
        }
        if (cls == Integer.TYPE) {
            return Integer.valueOf(order.getInt());
        }
        if (cls == Long.TYPE) {
            return Long.valueOf(order.getLong());
        }
        if (cls == Float.TYPE) {
            return Float.valueOf(order.getFloat());
        }
        if (cls == Short.TYPE) {
            return Short.valueOf(order.getShort());
        }
        if (cls == Byte.TYPE) {
            return Byte.valueOf(order.get());
        }
        if (cls == Character.TYPE) {
            return Character.valueOf(order.getChar());
        }
        if (cls == Double.TYPE) {
            return Double.valueOf(order.getDouble());
        }
        if (cls == Boolean.TYPE) {
            return Boolean.valueOf(order.getInt() == 0);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("unknown type:");
        sb.append(cls);
        throw new RuntimeException(sb.toString());
    }

    static {
        Class[] clsArr;
        for (Class cls : new Class[]{Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE}) {
            Map<Class<?>, String> map = bridgeMethodMap;
            StringBuilder sb = new StringBuilder();
            sb.append(cls.getName());
            sb.append("Bridge");
            map.put(cls, sb.toString());
        }
        bridgeMethodMap.put(Void.TYPE, "voidBridge");
        bridgeMethodMap.put(Object.class, "referenceBridge");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.lang.Class<?>, code=java.lang.Class, for r5v0, types: [java.lang.Class<?>, java.lang.Class] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.reflect.Method getBridgeMethod(java.lang.Class r5) {
        /*
            java.util.Map<java.lang.Class<?>, java.lang.String> r0 = bridgeMethodMap     // Catch:{ Throwable -> 0x0061 }
            boolean r1 = r5.isPrimitive()     // Catch:{ Throwable -> 0x0061 }
            if (r1 == 0) goto L_0x0009
            goto L_0x000b
        L_0x0009:
            java.lang.Class<java.lang.Object> r5 = java.lang.Object.class
        L_0x000b:
            java.lang.Object r5 = r0.get(r5)     // Catch:{ Throwable -> 0x0061 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Throwable -> 0x0061 }
            java.lang.String r0 = "Entry64"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0061 }
            r1.<init>()     // Catch:{ Throwable -> 0x0061 }
            java.lang.String r2 = "bridge method:"
            r1.append(r2)     // Catch:{ Throwable -> 0x0061 }
            r1.append(r5)     // Catch:{ Throwable -> 0x0061 }
            java.lang.String r2 = ", map:"
            r1.append(r2)     // Catch:{ Throwable -> 0x0061 }
            java.util.Map<java.lang.Class<?>, java.lang.String> r2 = bridgeMethodMap     // Catch:{ Throwable -> 0x0061 }
            r1.append(r2)     // Catch:{ Throwable -> 0x0061 }
            java.lang.String r1 = r1.toString()     // Catch:{ Throwable -> 0x0061 }
            com.taobao.android.dexposed.utility.Logger.m92d(r0, r1)     // Catch:{ Throwable -> 0x0061 }
            java.lang.Class<me.weishu.epic.art.entry.Entry64> r0 = p015me.weishu.epic.art.entry.Entry64.class
            r1 = 7
            java.lang.Class[] r1 = new java.lang.Class[r1]     // Catch:{ Throwable -> 0x0061 }
            r2 = 0
            java.lang.Class r3 = java.lang.Long.TYPE     // Catch:{ Throwable -> 0x0061 }
            r1[r2] = r3     // Catch:{ Throwable -> 0x0061 }
            java.lang.Class r2 = java.lang.Long.TYPE     // Catch:{ Throwable -> 0x0061 }
            r3 = 1
            r1[r3] = r2     // Catch:{ Throwable -> 0x0061 }
            r2 = 2
            java.lang.Class r4 = java.lang.Long.TYPE     // Catch:{ Throwable -> 0x0061 }
            r1[r2] = r4     // Catch:{ Throwable -> 0x0061 }
            r2 = 3
            java.lang.Class r4 = java.lang.Long.TYPE     // Catch:{ Throwable -> 0x0061 }
            r1[r2] = r4     // Catch:{ Throwable -> 0x0061 }
            r2 = 4
            java.lang.Class r4 = java.lang.Long.TYPE     // Catch:{ Throwable -> 0x0061 }
            r1[r2] = r4     // Catch:{ Throwable -> 0x0061 }
            r2 = 5
            java.lang.Class r4 = java.lang.Long.TYPE     // Catch:{ Throwable -> 0x0061 }
            r1[r2] = r4     // Catch:{ Throwable -> 0x0061 }
            r2 = 6
            java.lang.Class r4 = java.lang.Long.TYPE     // Catch:{ Throwable -> 0x0061 }
            r1[r2] = r4     // Catch:{ Throwable -> 0x0061 }
            java.lang.reflect.Method r5 = r0.getDeclaredMethod(r5, r1)     // Catch:{ Throwable -> 0x0061 }
            r5.setAccessible(r3)     // Catch:{ Throwable -> 0x0061 }
            return r5
        L_0x0061:
            r5 = move-exception
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "error"
            r0.<init>(r1, r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p015me.weishu.epic.art.entry.Entry64.getBridgeMethod(java.lang.Class):java.lang.reflect.Method");
    }
}
