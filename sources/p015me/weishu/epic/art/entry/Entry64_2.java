package p015me.weishu.epic.art.entry;

import com.taobao.android.dexposed.utility.Debug;
import com.taobao.android.dexposed.utility.Logger;
import java.lang.reflect.Method;
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

/* renamed from: me.weishu.epic.art.entry.Entry64_2 */
public class Entry64_2 {
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

    private static void voidBridge(long j, long j2) {
        referenceBridge(j, j2, 0, 0, 0, 0, 0);
    }

    private static void voidBridge(long j, long j2, long j3) {
        referenceBridge(j, j2, j3, 0, 0, 0, 0);
    }

    private static void voidBridge(long j, long j2, long j3, long j4) {
        referenceBridge(j, j2, j3, j4, 0, 0, 0);
    }

    private static void voidBridge(long j, long j2, long j3, long j4, long j5) {
        referenceBridge(j, j2, j3, j4, j5, 0, 0);
    }

    private static void voidBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        referenceBridge(j, j2, j3, j4, j5, j6, 0);
    }

    private static void voidBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        referenceBridge(j, j2, j3, j4, j5, j6, j7);
    }

    private static boolean booleanBridge(long j, long j2) {
        return ((Boolean) referenceBridge(j, j2, 0, 0, 0, 0, 0)).booleanValue();
    }

    private static boolean booleanBridge(long j, long j2, long j3) {
        return ((Boolean) referenceBridge(j, j2, j3, 0, 0, 0, 0)).booleanValue();
    }

    private static boolean booleanBridge(long j, long j2, long j3, long j4) {
        return ((Boolean) referenceBridge(j, j2, j3, j4, 0, 0, 0)).booleanValue();
    }

    private static boolean booleanBridge(long j, long j2, long j3, long j4, long j5) {
        return ((Boolean) referenceBridge(j, j2, j3, j4, j5, 0, 0)).booleanValue();
    }

    private static boolean booleanBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return ((Boolean) referenceBridge(j, j2, j3, j4, j5, j6, 0)).booleanValue();
    }

    private static boolean booleanBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Boolean) referenceBridge(j, j2, j3, j4, j5, j6, j7)).booleanValue();
    }

    private static byte byteBridge(long j, long j2) {
        return ((Byte) referenceBridge(j, j2, 0, 0, 0, 0, 0)).byteValue();
    }

    private static byte byteBridge(long j, long j2, long j3) {
        return ((Byte) referenceBridge(j, j2, j3, 0, 0, 0, 0)).byteValue();
    }

    private static byte byteBridge(long j, long j2, long j3, long j4) {
        return ((Byte) referenceBridge(j, j2, j3, j4, 0, 0, 0)).byteValue();
    }

    private static byte byteBridge(long j, long j2, long j3, long j4, long j5) {
        return ((Byte) referenceBridge(j, j2, j3, j4, j5, 0, 0)).byteValue();
    }

    private static byte byteBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return ((Byte) referenceBridge(j, j2, j3, j4, j5, j6, 0)).byteValue();
    }

    private static byte byteBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Byte) referenceBridge(j, j2, j3, j4, j5, j6, j7)).byteValue();
    }

    private static short shortBridge(long j, long j2) {
        return ((Short) referenceBridge(j, j2, 0, 0, 0, 0, 0)).shortValue();
    }

    private static short shortBridge(long j, long j2, long j3) {
        return ((Short) referenceBridge(j, j2, j3, 0, 0, 0, 0)).shortValue();
    }

    private static short shortBridge(long j, long j2, long j3, long j4) {
        return ((Short) referenceBridge(j, j2, j3, j4, 0, 0, 0)).shortValue();
    }

    private static short shortBridge(long j, long j2, long j3, long j4, long j5) {
        return ((Short) referenceBridge(j, j2, j3, j4, j5, 0, 0)).shortValue();
    }

    private static short shortBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return ((Short) referenceBridge(j, j2, j3, j4, j5, j6, 0)).shortValue();
    }

    private static short shortBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Short) referenceBridge(j, j2, j3, j4, j5, j6, j7)).shortValue();
    }

    private static char charBridge(long j, long j2) {
        return ((Character) referenceBridge(j, j2, 0, 0, 0, 0, 0)).charValue();
    }

    private static char charBridge(long j, long j2, long j3) {
        return ((Character) referenceBridge(j, j2, j3, 0, 0, 0, 0)).charValue();
    }

    private static char charBridge(long j, long j2, long j3, long j4) {
        return ((Character) referenceBridge(j, j2, j3, j4, 0, 0, 0)).charValue();
    }

    private static char charBridge(long j, long j2, long j3, long j4, long j5) {
        return ((Character) referenceBridge(j, j2, j3, j4, j5, 0, 0)).charValue();
    }

    private static char charBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return ((Character) referenceBridge(j, j2, j3, j4, j5, j6, 0)).charValue();
    }

    private static char charBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Character) referenceBridge(j, j2, j3, j4, j5, j6, j7)).charValue();
    }

    private static int intBridge(long j, long j2) {
        return ((Integer) referenceBridge(j, j2, 0, 0, 0, 0, 0)).intValue();
    }

    private static int intBridge(long j, long j2, long j3) {
        return ((Integer) referenceBridge(j, j2, j3, 0, 0, 0, 0)).intValue();
    }

    private static int intBridge(long j, long j2, long j3, long j4) {
        return ((Integer) referenceBridge(j, j2, j3, j4, 0, 0, 0)).intValue();
    }

    private static int intBridge(long j, long j2, long j3, long j4, long j5) {
        return ((Integer) referenceBridge(j, j2, j3, j4, j5, 0, 0)).intValue();
    }

    private static int intBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return ((Integer) referenceBridge(j, j2, j3, j4, j5, j6, 0)).intValue();
    }

    private static int intBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Integer) referenceBridge(j, j2, j3, j4, j5, j6, j7)).intValue();
    }

    private static long longBridge(long j, long j2) {
        return ((Long) referenceBridge(j, j2, 0, 0, 0, 0, 0)).longValue();
    }

    private static long longBridge(long j, long j2, long j3) {
        return ((Long) referenceBridge(j, j2, j3, 0, 0, 0, 0)).longValue();
    }

    private static long longBridge(long j, long j2, long j3, long j4) {
        return ((Long) referenceBridge(j, j2, j3, j4, 0, 0, 0)).longValue();
    }

    private static long longBridge(long j, long j2, long j3, long j4, long j5) {
        return ((Long) referenceBridge(j, j2, j3, j4, j5, 0, 0)).longValue();
    }

    private static long longBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return ((Long) referenceBridge(j, j2, j3, j4, j5, j6, 0)).longValue();
    }

    private static long longBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Long) referenceBridge(j, j2, j3, j4, j5, j6, j7)).longValue();
    }

    private static float floatBridge(long j, long j2) {
        return ((Float) referenceBridge(j, j2, 0, 0, 0, 0, 0)).floatValue();
    }

    private static float floatBridge(long j, long j2, long j3) {
        return ((Float) referenceBridge(j, j2, j3, 0, 0, 0, 0)).floatValue();
    }

    private static float floatBridge(long j, long j2, long j3, long j4) {
        return ((Float) referenceBridge(j, j2, j3, j4, 0, 0, 0)).floatValue();
    }

    private static float floatBridge(long j, long j2, long j3, long j4, long j5) {
        return ((Float) referenceBridge(j, j2, j3, j4, j5, 0, 0)).floatValue();
    }

    private static float floatBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return ((Float) referenceBridge(j, j2, j3, j4, j5, j6, 0)).floatValue();
    }

    private static float floatBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Float) referenceBridge(j, j2, j3, j4, j5, j6, j7)).floatValue();
    }

    private static double doubleBridge(long j, long j2) {
        return ((Double) referenceBridge(j, j2, 0, 0, 0, 0, 0)).doubleValue();
    }

    private static double doubleBridge(long j, long j2, long j3) {
        return ((Double) referenceBridge(j, j2, j3, 0, 0, 0, 0)).doubleValue();
    }

    private static double doubleBridge(long j, long j2, long j3, long j4) {
        return ((Double) referenceBridge(j, j2, j3, j4, 0, 0, 0)).doubleValue();
    }

    private static double doubleBridge(long j, long j2, long j3, long j4, long j5) {
        return ((Double) referenceBridge(j, j2, j3, j4, j5, 0, 0)).doubleValue();
    }

    private static double doubleBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return ((Double) referenceBridge(j, j2, j3, j4, j5, j6, 0)).doubleValue();
    }

    private static double doubleBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return ((Double) referenceBridge(j, j2, j3, j4, j5, j6, j7)).doubleValue();
    }

    private static Object referenceBridge(long j, long j2) {
        return referenceBridge(j, j2, 0, 0, 0, 0, 0);
    }

    private static Object referenceBridge(long j, long j2, long j3) {
        return referenceBridge(j, j2, j3, 0, 0, 0, 0);
    }

    private static Object referenceBridge(long j, long j2, long j3, long j4) {
        return referenceBridge(j, j2, j3, j4, 0, 0, 0);
    }

    private static Object referenceBridge(long j, long j2, long j3, long j4, long j5) {
        return referenceBridge(j, j2, j3, j4, j5, 0, 0);
    }

    private static Object referenceBridge(long j, long j2, long j3, long j4, long j5, long j6) {
        return referenceBridge(j, j2, j3, j4, j5, j6, 0);
    }

    private static Object referenceBridge(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        MethodInfo methodInfo;
        Object obj;
        long j8 = j;
        long j9 = j2;
        long j10 = j3;
        long j11 = j4;
        long j12 = j5;
        long j13 = j6;
        long j14 = j7;
        Logger.m95i(TAG, "enter bridge function.");
        long longField = XposedHelpers.getLongField(Thread.currentThread(), "nativePeer");
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("java thread native peer:");
        sb.append(Long.toHexString(longField));
        Logger.m92d(str, sb.toString());
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("struct:");
        sb2.append(Long.toHexString(j2));
        Logger.m92d(str2, sb2.toString());
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("struct:");
        sb3.append(Debug.hexdump(EpicNative.get(j9, 24), j9));
        Logger.m92d(str3, sb3.toString());
        long j15 = ByteBuffer.wrap(EpicNative.get(j9, 8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
        String str4 = TAG;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("stack:");
        sb4.append(j15);
        Logger.m92d(str4, sb4.toString());
        byte[] array = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(j8).array();
        byte[] bArr = EpicNative.get(j9 + 8, 8);
        long j16 = ByteBuffer.wrap(EpicNative.get(j9 + 16, 8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
        String str5 = TAG;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("sourceMethod:");
        sb5.append(Long.toHexString(j16));
        Logger.m92d(str5, sb5.toString());
        MethodInfo methodInfo2 = Epic.getMethodInfo(j16);
        String str6 = TAG;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("originMethodInfo :");
        sb6.append(methodInfo2);
        Logger.m92d(str6, sb6.toString());
        boolean z = methodInfo2.isStatic;
        int i = methodInfo2.paramNumber;
        Class<?>[] clsArr = methodInfo2.paramTypes;
        Object[] objArr = new Object[i];
        if (z) {
            if (i != 0) {
                objArr[0] = wrapArgument(clsArr[0], longField, array);
                if (i != 1) {
                    objArr[1] = wrapArgument(clsArr[1], longField, bArr);
                    if (i != 2) {
                        objArr[2] = wrapArgument(clsArr[2], longField, j10);
                        if (i != 3) {
                            objArr[3] = wrapArgument(clsArr[3], longField, j11);
                            if (i != 4) {
                                objArr[4] = wrapArgument(clsArr[4], longField, j5);
                                if (i != 5) {
                                    objArr[5] = wrapArgument(clsArr[5], longField, j6);
                                    if (i != 6) {
                                        objArr[6] = wrapArgument(clsArr[6], longField, j7);
                                        if (i != 7) {
                                            for (int i2 = 7; i2 < i; i2++) {
                                                objArr[i2] = wrapArgument(clsArr[i2], longField, EpicNative.get(((long) (i2 * 8)) + j15 + 8, 8));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            methodInfo = methodInfo2;
            obj = null;
        } else {
            methodInfo = methodInfo2;
            long j17 = j15;
            long j18 = j5;
            obj = EpicNative.getObject(longField, j);
            String str7 = TAG;
            StringBuilder sb7 = new StringBuilder();
            sb7.append("this :");
            sb7.append(obj);
            Logger.m95i(str7, sb7.toString());
            if (i != 0) {
                objArr[0] = wrapArgument(clsArr[0], longField, bArr);
                if (i != 1) {
                    objArr[1] = wrapArgument(clsArr[1], longField, j10);
                    if (i != 2) {
                        objArr[2] = wrapArgument(clsArr[2], longField, j11);
                        if (i != 3) {
                            objArr[3] = wrapArgument(clsArr[3], longField, j18);
                            if (i != 4) {
                                objArr[4] = wrapArgument(clsArr[4], longField, j6);
                                if (i != 5) {
                                    objArr[5] = wrapArgument(clsArr[5], longField, j7);
                                    if (i != 6) {
                                        for (int i3 = 6; i3 < i; i3++) {
                                            objArr[i3] = wrapArgument(clsArr[i3], longField, EpicNative.get(j17 + ((long) (i3 * 8)) + 16, 8));
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
        MethodInfo methodInfo3 = methodInfo;
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

    public static Method getBridgeMethod(MethodInfo methodInfo) {
        try {
            Class cls = methodInfo.returnType;
            int i = methodInfo.isStatic ? methodInfo.paramNumber : methodInfo.paramNumber + 1;
            if (i <= 2) {
                i = 2;
            }
            Class[] clsArr = new Class[i];
            for (int i2 = 0; i2 < i; i2++) {
                clsArr[i2] = Long.TYPE;
            }
            Map<Class<?>, String> map = bridgeMethodMap;
            if (!cls.isPrimitive()) {
                cls = Object.class;
            }
            String str = (String) map.get(cls);
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("bridge method:");
            sb.append(str);
            sb.append(", map:");
            sb.append(bridgeMethodMap);
            Logger.m92d(str2, sb.toString());
            Method declaredMethod = Entry64_2.class.getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (Throwable th) {
            throw new RuntimeException("can not found bridge.", th);
        }
    }
}
