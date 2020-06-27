package p015me.weishu.epic.art.entry;

import android.util.Pair;
import com.taobao.android.dexposed.utility.Debug;
import com.taobao.android.dexposed.utility.Logger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import p011de.robv.android.xposed.DexposedBridge;
import p015me.weishu.epic.art.Epic;
import p015me.weishu.epic.art.Epic.MethodInfo;
import p015me.weishu.epic.art.EpicNative;
import p015me.weishu.epic.art.method.ArtMethod;

/* renamed from: me.weishu.epic.art.entry.Entry */
public class Entry {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final String TAG = "Entry";
    private static Map<Class<?>, String> bridgeMethodMap = new HashMap();

    static {
        Class[] clsArr;
        for (Class cls : new Class[]{Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE}) {
            Map<Class<?>, String> map = bridgeMethodMap;
            StringBuilder sb = new StringBuilder();
            sb.append(cls.getName());
            sb.append("Bridge");
            map.put(cls, sb.toString());
        }
        bridgeMethodMap.put(Object.class, "referenceBridge");
        bridgeMethodMap.put(Void.TYPE, "voidBridge");
    }

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

    private static void voidBridge(int i, int i2, int i3) {
        referenceBridge(i, i2, i3);
    }

    private static boolean booleanBridge(int i, int i2, int i3) {
        return ((Boolean) referenceBridge(i, i2, i3)).booleanValue();
    }

    private static byte byteBridge(int i, int i2, int i3) {
        return ((Byte) referenceBridge(i, i2, i3)).byteValue();
    }

    private static short shortBridge(int i, int i2, int i3) {
        return ((Short) referenceBridge(i, i2, i3)).shortValue();
    }

    private static char charBridge(int i, int i2, int i3) {
        return ((Character) referenceBridge(i, i2, i3)).charValue();
    }

    private static int intBridge(int i, int i2, int i3) {
        return ((Integer) referenceBridge(i, i2, i3)).intValue();
    }

    private static long longBridge(int i, int i2, int i3) {
        return ((Long) referenceBridge(i, i2, i3)).longValue();
    }

    private static float floatBridge(int i, int i2, int i3) {
        return ((Float) referenceBridge(i, i2, i3)).floatValue();
    }

    private static double doubleBridge(int i, int i2, int i3) {
        return ((Double) referenceBridge(i, i2, i3)).doubleValue();
    }

    private static Object referenceBridge(int i, int i2, int i3) {
        Logger.m95i(TAG, "enter bridge function.");
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("struct:");
        long j = (long) i3;
        sb.append(Long.toHexString(j));
        Logger.m95i(str, sb.toString());
        int i4 = ByteBuffer.wrap(EpicNative.get(j, 4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] array = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array();
        byte[] bArr = EpicNative.get((long) (i3 + 4), 4);
        byte[] bArr2 = EpicNative.get((long) (i3 + 8), 4);
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("r1:");
        sb2.append(Debug.hexdump(array, 0));
        Logger.m92d(str2, sb2.toString());
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("r2:");
        sb3.append(Debug.hexdump(bArr, 0));
        Logger.m92d(str3, sb3.toString());
        String str4 = TAG;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("r3:");
        sb4.append(Debug.hexdump(bArr2, 0));
        Logger.m92d(str4, sb4.toString());
        int i5 = ByteBuffer.wrap(EpicNative.get((long) (i3 + 12), 4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        String str5 = TAG;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("sourceMethod:");
        sb5.append(Integer.toHexString(i5));
        Logger.m95i(str5, sb5.toString());
        MethodInfo methodInfo = Epic.getMethodInfo((long) i5);
        String str6 = TAG;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("originMethodInfo :");
        sb6.append(methodInfo);
        Logger.m95i(str6, sb6.toString());
        Pair constructArguments = constructArguments(methodInfo, i2, array, bArr, bArr2, i4);
        Object obj = constructArguments.first;
        Object[] objArr = (Object[]) constructArguments.second;
        String str7 = TAG;
        StringBuilder sb7 = new StringBuilder();
        sb7.append("arguments:");
        sb7.append(Arrays.toString(objArr));
        Logger.m95i(str7, sb7.toString());
        Class<?> cls = methodInfo.returnType;
        ArtMethod artMethod = methodInfo.method;
        Logger.m95i(TAG, "leave bridge function");
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

    /* JADX WARNING: Removed duplicated region for block: B:33:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0184 A[LOOP:2: B:71:0x0182->B:72:0x0184, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01a2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.util.Pair<java.lang.Object, java.lang.Object[]> constructArguments(p015me.weishu.epic.art.Epic.MethodInfo r17, int r18, byte[] r19, byte[] r20, byte[] r21, int r22) {
        /*
            r0 = r17
            r1 = r20
            r2 = r21
            boolean r4 = r0.isStatic
            r5 = 1
            r6 = 0
            if (r4 == 0) goto L_0x0011
            int r7 = r0.paramNumber
            java.lang.Class<?>[] r0 = r0.paramTypes
            goto L_0x0023
        L_0x0011:
            int r7 = r0.paramNumber
            int r7 = r7 + r5
            java.lang.Class[] r8 = new java.lang.Class[r7]
            java.lang.Class<java.lang.Object> r9 = java.lang.Object.class
            r8[r6] = r9
            java.lang.Class<?>[] r9 = r0.paramTypes
            java.lang.Class<?>[] r0 = r0.paramTypes
            int r0 = r0.length
            java.lang.System.arraycopy(r9, r6, r8, r5, r0)
            r0 = r8
        L_0x0023:
            java.lang.Object[] r8 = new java.lang.Object[r7]
            int[] r9 = new int[r7]
            r10 = 4
            r11 = 0
            r12 = 4
        L_0x002a:
            if (r11 >= r7) goto L_0x0038
            r13 = r0[r11]
            int r13 = getTypeLength(r13)
            r9[r11] = r12
            int r12 = r12 + r13
            int r11 = r11 + 1
            goto L_0x002a
        L_0x0038:
            byte[] r11 = new byte[r12]
            r13 = 23
            r15 = 8
            r5 = 12
            if (r12 > r10) goto L_0x0045
        L_0x0042:
            r1 = 16
            goto L_0x008f
        L_0x0045:
            int r14 = android.os.Build.VERSION.SDK_INT
            if (r14 < r13) goto L_0x0055
            if (r7 <= 0) goto L_0x0055
            r14 = r0[r6]
            int r14 = getTypeLength(r14)
            if (r14 != r15) goto L_0x0055
            r14 = 1
            goto L_0x0056
        L_0x0055:
            r14 = 0
        L_0x0056:
            if (r14 == 0) goto L_0x006e
            java.lang.System.arraycopy(r1, r6, r11, r10, r10)
            java.lang.System.arraycopy(r2, r6, r11, r15, r10)
            if (r12 > r5) goto L_0x0061
            goto L_0x0042
        L_0x0061:
            int r1 = r22 + 12
            long r1 = (long) r1
            byte[] r1 = p015me.weishu.epic.art.EpicNative.get(r1, r10)
            java.lang.System.arraycopy(r1, r6, r11, r5, r10)
        L_0x006b:
            r1 = 16
            goto L_0x0080
        L_0x006e:
            r14 = r19
            java.lang.System.arraycopy(r14, r6, r11, r10, r10)
            if (r12 > r15) goto L_0x0076
            goto L_0x0042
        L_0x0076:
            java.lang.System.arraycopy(r1, r6, r11, r15, r10)
            if (r12 > r5) goto L_0x007c
            goto L_0x0042
        L_0x007c:
            java.lang.System.arraycopy(r2, r6, r11, r5, r10)
            goto L_0x006b
        L_0x0080:
            if (r12 > r1) goto L_0x0083
            goto L_0x008f
        L_0x0083:
            int r2 = r22 + 16
            long r13 = (long) r2
            int r2 = r12 + -16
            byte[] r13 = p015me.weishu.epic.art.EpicNative.get(r13, r2)
            java.lang.System.arraycopy(r13, r6, r11, r1, r2)
        L_0x008f:
            int r2 = android.os.Build.VERSION.SDK_INT
            r13 = 23
            if (r2 != r13) goto L_0x0165
            if (r12 > r5) goto L_0x0099
            goto L_0x0165
        L_0x0099:
            if (r12 > r1) goto L_0x00af
            r1 = r0[r6]
            int r1 = getTypeLength(r1)
            if (r1 != r15) goto L_0x0165
            int r1 = r22 + 44
            long r1 = (long) r1
            byte[] r1 = p015me.weishu.epic.art.EpicNative.get(r1, r10)
            java.lang.System.arraycopy(r1, r6, r11, r5, r10)
            goto L_0x0165
        L_0x00af:
            r1 = 2
            if (r7 < r1) goto L_0x00d8
            r2 = r0[r6]
            int r2 = getTypeLength(r2)
            r12 = 1
            r13 = r0[r12]
            int r12 = getTypeLength(r13)
            if (r2 != r10) goto L_0x00c5
            if (r12 != r15) goto L_0x00c5
            r13 = 0
            goto L_0x00c6
        L_0x00c5:
            r13 = 1
        L_0x00c6:
            if (r7 != r1) goto L_0x00d9
            if (r2 != r15) goto L_0x00d9
            if (r12 != r15) goto L_0x00d9
            int r2 = r22 + 44
            long r12 = (long) r2
            byte[] r2 = p015me.weishu.epic.art.EpicNative.get(r12, r10)
            java.lang.System.arraycopy(r2, r6, r11, r5, r10)
            r13 = 0
            goto L_0x00d9
        L_0x00d8:
            r13 = 1
        L_0x00d9:
            r2 = 3
            if (r7 < r2) goto L_0x0109
            r12 = r0[r6]
            int r12 = getTypeLength(r12)
            r14 = 1
            r16 = r0[r14]
            int r14 = getTypeLength(r16)
            r1 = r0[r1]
            int r1 = getTypeLength(r1)
            if (r12 != r10) goto L_0x00f6
            if (r14 != r10) goto L_0x00f6
            if (r1 != r10) goto L_0x00f6
            r13 = 0
        L_0x00f6:
            if (r7 != r2) goto L_0x0109
            if (r12 != r15) goto L_0x0109
            if (r14 != r10) goto L_0x0109
            if (r1 != r15) goto L_0x0109
            int r1 = r22 + 52
            long r1 = (long) r1
            byte[] r1 = p015me.weishu.epic.art.EpicNative.get(r1, r10)
            java.lang.System.arraycopy(r1, r6, r11, r5, r10)
            r13 = 0
        L_0x0109:
            if (r13 == 0) goto L_0x0165
            int r1 = r11.length
            r2 = 16
            byte[] r1 = java.util.Arrays.copyOfRange(r11, r2, r1)
            int r2 = r1.length
            int r12 = r2 + 16
            r13 = 0
        L_0x0116:
            int r14 = r22 + r12
            long r5 = (long) r14
            byte[] r5 = p015me.weishu.epic.art.EpicNative.get(r5, r2)
            int r13 = r13 + r2
            boolean r5 = java.util.Arrays.equals(r5, r1)
            if (r5 == 0) goto L_0x0153
            int r14 = r14 - r10
            long r1 = (long) r14
            byte[] r1 = p015me.weishu.epic.art.EpicNative.get(r1, r10)
            java.lang.String r2 = "Entry"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "found other arguments in stack, index:"
            r3.append(r5)
            r3.append(r12)
            java.lang.String r5 = ", origin r3:"
            r3.append(r5)
            java.lang.String r5 = java.util.Arrays.toString(r1)
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            com.taobao.android.dexposed.utility.Logger.m92d(r2, r3)
            r2 = 0
            r5 = 12
            java.lang.System.arraycopy(r1, r2, r11, r5, r10)
            goto L_0x0165
        L_0x0153:
            r5 = 12
            r6 = 1024(0x400, float:1.435E-42)
            if (r13 > r6) goto L_0x015d
            int r12 = r12 + 4
            r6 = 0
            goto L_0x0116
        L_0x015d:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "can not found the modify r3 register!!!"
            r0.<init>(r1)
            throw r0
        L_0x0165:
            java.lang.String r1 = "Entry"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "argBytes: "
            r2.append(r3)
            r5 = 0
            java.lang.String r3 = com.taobao.android.dexposed.utility.Debug.hexdump(r11, r5)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.taobao.android.dexposed.utility.Logger.m92d(r1, r2)
            r1 = 0
        L_0x0182:
            if (r1 >= r7) goto L_0x019c
            r2 = r0[r1]
            r3 = r9[r1]
            int r5 = getTypeLength(r2)
            int r5 = r5 + r3
            byte[] r3 = java.util.Arrays.copyOfRange(r11, r3, r5)
            r5 = r18
            java.lang.Object r2 = wrapArgument(r2, r5, r3)
            r8[r1] = r2
            int r1 = r1 + 1
            goto L_0x0182
        L_0x019c:
            r0 = 0
            java.lang.Object[] r1 = EMPTY_OBJECT_ARRAY
            if (r4 == 0) goto L_0x01a2
            goto L_0x01af
        L_0x01a2:
            r0 = 0
            r0 = r8[r0]
            int r2 = r8.length
            r3 = 1
            if (r2 <= r3) goto L_0x01ae
            java.lang.Object[] r8 = java.util.Arrays.copyOfRange(r8, r3, r2)
            goto L_0x01af
        L_0x01ae:
            r8 = r1
        L_0x01af:
            android.util.Pair r0 = android.util.Pair.create(r0, r8)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p015me.weishu.epic.art.entry.Entry.constructArguments(me.weishu.epic.art.Epic$MethodInfo, int, byte[], byte[], byte[], int):android.util.Pair");
    }

    private static Object wrapArgument(Class<?> cls, int i, byte[] bArr) {
        ByteBuffer order = ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("wrapArgument: type:");
        sb.append(cls);
        Logger.m92d(str, sb.toString());
        if (!cls.isPrimitive()) {
            return EpicNative.getObject((long) i, (long) order.getInt());
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
            return Boolean.valueOf(order.getInt() != 0);
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("unknown type:");
        sb2.append(cls);
        throw new RuntimeException(sb2.toString());
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.lang.Class<?>, code=java.lang.Class, for r5v0, types: [java.lang.Class<?>, java.lang.Class] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.reflect.Method getBridgeMethod(java.lang.Class r5) {
        /*
            java.util.Map<java.lang.Class<?>, java.lang.String> r0 = bridgeMethodMap     // Catch:{ Throwable -> 0x004d }
            boolean r1 = r5.isPrimitive()     // Catch:{ Throwable -> 0x004d }
            if (r1 == 0) goto L_0x0009
            goto L_0x000b
        L_0x0009:
            java.lang.Class<java.lang.Object> r5 = java.lang.Object.class
        L_0x000b:
            java.lang.Object r5 = r0.get(r5)     // Catch:{ Throwable -> 0x004d }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Throwable -> 0x004d }
            java.lang.String r0 = "Entry"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x004d }
            r1.<init>()     // Catch:{ Throwable -> 0x004d }
            java.lang.String r2 = "bridge method:"
            r1.append(r2)     // Catch:{ Throwable -> 0x004d }
            r1.append(r5)     // Catch:{ Throwable -> 0x004d }
            java.lang.String r2 = ", map:"
            r1.append(r2)     // Catch:{ Throwable -> 0x004d }
            java.util.Map<java.lang.Class<?>, java.lang.String> r2 = bridgeMethodMap     // Catch:{ Throwable -> 0x004d }
            r1.append(r2)     // Catch:{ Throwable -> 0x004d }
            java.lang.String r1 = r1.toString()     // Catch:{ Throwable -> 0x004d }
            com.taobao.android.dexposed.utility.Logger.m95i(r0, r1)     // Catch:{ Throwable -> 0x004d }
            java.lang.Class<me.weishu.epic.art.entry.Entry> r0 = p015me.weishu.epic.art.entry.Entry.class
            r1 = 3
            java.lang.Class[] r1 = new java.lang.Class[r1]     // Catch:{ Throwable -> 0x004d }
            r2 = 0
            java.lang.Class r3 = java.lang.Integer.TYPE     // Catch:{ Throwable -> 0x004d }
            r1[r2] = r3     // Catch:{ Throwable -> 0x004d }
            java.lang.Class r2 = java.lang.Integer.TYPE     // Catch:{ Throwable -> 0x004d }
            r3 = 1
            r1[r3] = r2     // Catch:{ Throwable -> 0x004d }
            r2 = 2
            java.lang.Class r4 = java.lang.Integer.TYPE     // Catch:{ Throwable -> 0x004d }
            r1[r2] = r4     // Catch:{ Throwable -> 0x004d }
            java.lang.reflect.Method r5 = r0.getDeclaredMethod(r5, r1)     // Catch:{ Throwable -> 0x004d }
            r5.setAccessible(r3)     // Catch:{ Throwable -> 0x004d }
            return r5
        L_0x004d:
            r5 = move-exception
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "error"
            r0.<init>(r1, r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p015me.weishu.epic.art.entry.Entry.getBridgeMethod(java.lang.Class):java.lang.reflect.Method");
    }

    private static int getTypeLength(Class<?> cls) {
        return (cls == Long.TYPE || cls == Double.TYPE) ? 8 : 4;
    }
}
