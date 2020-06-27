package com.taobao.android.dexposed.utility;

import com.microsoft.appcenter.Constants;
import java.lang.reflect.Method;
import p013io.virtualapp.utils.HanziToPinyin.Token;
import p015me.weishu.epic.art.method.ArtMethod;

public final class Debug {
    public static final boolean DEBUG = false;
    private static final String RELASE_WRAN_STRING = "none in release mode.";
    private static final String TAG = "Dexposed";

    public static String addrHex(long j) {
        return RELASE_WRAN_STRING;
    }

    public static String hexdump(byte[] bArr, long j) {
        return RELASE_WRAN_STRING;
    }

    private Debug() {
    }

    public static String longHex(long j) {
        return String.format("0x%016X", new Object[]{Long.valueOf(j)});
    }

    public static String intHex(int i) {
        return String.format("0x%08X", new Object[]{Integer.valueOf(i)});
    }

    public static String byteHex(byte b) {
        return String.format("%02X", new Object[]{Byte.valueOf(b)});
    }

    public static String dump(byte[] bArr, long j) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bArr.length; i++) {
            int i2 = i % 8;
            if (i2 == 0) {
                stringBuffer.append(addrHex(((long) i) + j));
                stringBuffer.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
            }
            stringBuffer.append(byteHex(bArr[i]));
            stringBuffer.append(Token.SEPARATOR);
            if (i2 == 7) {
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString();
    }

    public static String methodDescription(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append("->");
        sb.append(method.getName());
        sb.append(" @");
        sb.append(addrHex(ArtMethod.m105of(method).getEntryPointFromQuickCompiledCode()));
        sb.append(" +");
        sb.append(addrHex(ArtMethod.m105of(method).getAddress()));
        return sb.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x002f A[SYNTHETIC, Splitter:B:19:0x002f] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0035 A[SYNTHETIC, Splitter:B:23:0x0035] */
    /* JADX WARNING: Removed duplicated region for block: B:29:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void dumpMaps() {
        /*
            r0 = 0
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0026 }
            java.io.FileReader r2 = new java.io.FileReader     // Catch:{ IOException -> 0x0026 }
            java.lang.String r3 = "/proc/self/maps"
            r2.<init>(r3)     // Catch:{ IOException -> 0x0026 }
            r1.<init>(r2)     // Catch:{ IOException -> 0x0026 }
        L_0x000d:
            java.lang.String r0 = r1.readLine()     // Catch:{ IOException -> 0x0022, all -> 0x001d }
            if (r0 == 0) goto L_0x0019
            java.lang.String r2 = "Dexposed"
            android.util.Log.i(r2, r0)     // Catch:{ IOException -> 0x0022, all -> 0x001d }
            goto L_0x000d
        L_0x0019:
            r1.close()     // Catch:{ IOException -> 0x0032 }
            goto L_0x0032
        L_0x001d:
            r0 = move-exception
            r4 = r1
            r1 = r0
            r0 = r4
            goto L_0x0033
        L_0x0022:
            r0 = r1
            goto L_0x0026
        L_0x0024:
            r1 = move-exception
            goto L_0x0033
        L_0x0026:
            java.lang.String r1 = "Dexposed"
            java.lang.String r2 = "dumpMaps error"
            android.util.Log.e(r1, r2)     // Catch:{ all -> 0x0024 }
            if (r0 == 0) goto L_0x0032
            r0.close()     // Catch:{ IOException -> 0x0032 }
        L_0x0032:
            return
        L_0x0033:
            if (r0 == 0) goto L_0x0038
            r0.close()     // Catch:{ IOException -> 0x0038 }
        L_0x0038:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.taobao.android.dexposed.utility.Debug.dumpMaps():void");
    }
}
