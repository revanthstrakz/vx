package p015me.weishu.epic.art;

import com.taobao.android.dexposed.utility.Debug;
import com.taobao.android.dexposed.utility.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import p015me.weishu.epic.art.arch.ShellCode;
import p015me.weishu.epic.art.method.ArtMethod;

/* renamed from: me.weishu.epic.art.Epic */
public final class Epic {
    private static ShellCode ShellCode = null;
    private static final String TAG = "Epic";
    private static final Map<String, ArtMethod> backupMethodsMapping = new ConcurrentHashMap();
    private static final Map<Long, MethodInfo> originSigs = new ConcurrentHashMap();
    private static final Map<Long, Trampoline> scripts = new HashMap();

    /* renamed from: me.weishu.epic.art.Epic$EntryLock */
    private static class EntryLock {
        static Map<Long, EntryLock> sLockPool = new HashMap();

        private EntryLock() {
        }

        static synchronized EntryLock obtain(long j) {
            synchronized (EntryLock.class) {
                if (sLockPool.containsKey(Long.valueOf(j))) {
                    EntryLock entryLock = (EntryLock) sLockPool.get(Long.valueOf(j));
                    return entryLock;
                }
                EntryLock entryLock2 = new EntryLock();
                sLockPool.put(Long.valueOf(j), entryLock2);
                return entryLock2;
            }
        }
    }

    /* renamed from: me.weishu.epic.art.Epic$MethodInfo */
    public static class MethodInfo {
        public boolean isStatic;
        public ArtMethod method;
        public int paramNumber;
        public Class<?>[] paramTypes;
        public Class<?> returnType;

        public String toString() {
            return this.method.toGenericString();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0064  */
    static {
        /*
            java.util.concurrent.ConcurrentHashMap r0 = new java.util.concurrent.ConcurrentHashMap
            r0.<init>()
            backupMethodsMapping = r0
            java.util.concurrent.ConcurrentHashMap r0 = new java.util.concurrent.ConcurrentHashMap
            r0.<init>()
            originSigs = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            scripts = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            boolean r1 = com.taobao.android.dexposed.utility.Runtime.is64Bit()
            if (r1 == 0) goto L_0x0025
            me.weishu.epic.art.arch.Arm64 r1 = new me.weishu.epic.art.arch.Arm64
            r1.<init>()
            ShellCode = r1
            goto L_0x0032
        L_0x0025:
            boolean r1 = com.taobao.android.dexposed.utility.Runtime.isThumb2()
            if (r1 == 0) goto L_0x0034
            me.weishu.epic.art.arch.Thumb2 r1 = new me.weishu.epic.art.arch.Thumb2
            r1.<init>()
            ShellCode = r1
        L_0x0032:
            r1 = 1
            goto L_0x0043
        L_0x0034:
            r1 = 0
            me.weishu.epic.art.arch.Thumb2 r2 = new me.weishu.epic.art.arch.Thumb2
            r2.<init>()
            ShellCode = r2
            java.lang.String r2 = "Epic"
            java.lang.String r3 = "ARM32, not support now."
            com.taobao.android.dexposed.utility.Logger.m96w(r2, r3)
        L_0x0043:
            me.weishu.epic.art.arch.ShellCode r2 = ShellCode
            if (r2 == 0) goto L_0x0064
            java.lang.String r0 = "Epic"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Using: "
            r1.append(r2)
            me.weishu.epic.art.arch.ShellCode r2 = ShellCode
            java.lang.String r2 = r2.getName()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.taobao.android.dexposed.utility.Logger.m95i(r0, r1)
            return
        L_0x0064:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Do not support this ARCH now!! API LEVEL:"
            r3.append(r4)
            r3.append(r0)
            java.lang.String r0 = " thumb2 ? : "
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
            r2.<init>(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: p015me.weishu.epic.art.Epic.<clinit>():void");
    }

    public static boolean hookMethod(Constructor constructor) {
        return hookMethod(ArtMethod.m104of(constructor));
    }

    public static boolean hookMethod(Method method) {
        return hookMethod(ArtMethod.m105of(method));
    }

    private static boolean hookMethod(ArtMethod artMethod) {
        boolean install;
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.isStatic = Modifier.isStatic(artMethod.getModifiers());
        Class<?>[] parameterTypes = artMethod.getParameterTypes();
        if (parameterTypes != null) {
            methodInfo.paramNumber = parameterTypes.length;
            methodInfo.paramTypes = parameterTypes;
        } else {
            methodInfo.paramNumber = 0;
            methodInfo.paramTypes = new Class[0];
        }
        methodInfo.returnType = artMethod.getReturnType();
        methodInfo.method = artMethod;
        originSigs.put(Long.valueOf(artMethod.getAddress()), methodInfo);
        if (!artMethod.isAccessible()) {
            artMethod.setAccessible(true);
        }
        artMethod.ensureResolved();
        long entryPointFromQuickCompiledCode = artMethod.getEntryPointFromQuickCompiledCode();
        if (entryPointFromQuickCompiledCode == ArtMethod.getQuickToInterpreterBridge()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("this method is not compiled, compile it now. current entry: 0x");
            sb.append(Long.toHexString(entryPointFromQuickCompiledCode));
            Logger.m95i(str, sb.toString());
            if (artMethod.compile()) {
                entryPointFromQuickCompiledCode = artMethod.getEntryPointFromQuickCompiledCode();
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("compile method success, new entry: 0x");
                sb2.append(Long.toHexString(entryPointFromQuickCompiledCode));
                Logger.m95i(str2, sb2.toString());
            } else {
                Logger.m93e(TAG, "compile method failed...");
                return false;
            }
        }
        ArtMethod backup = artMethod.backup();
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("backup method address:");
        sb3.append(Debug.addrHex(backup.getAddress()));
        Logger.m95i(str3, sb3.toString());
        String str4 = TAG;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("backup method entry :");
        sb4.append(Debug.addrHex(backup.getEntryPointFromQuickCompiledCode()));
        Logger.m95i(str4, sb4.toString());
        if (getBackMethod(artMethod) == null) {
            setBackMethod(artMethod, backup);
        }
        synchronized (EntryLock.obtain(entryPointFromQuickCompiledCode)) {
            if (!scripts.containsKey(Long.valueOf(entryPointFromQuickCompiledCode))) {
                scripts.put(Long.valueOf(entryPointFromQuickCompiledCode), new Trampoline(ShellCode, entryPointFromQuickCompiledCode));
            }
            install = ((Trampoline) scripts.get(Long.valueOf(entryPointFromQuickCompiledCode))).install(artMethod);
        }
        return install;
    }

    public static synchronized ArtMethod getBackMethod(ArtMethod artMethod) {
        ArtMethod artMethod2;
        synchronized (Epic.class) {
            artMethod2 = (ArtMethod) backupMethodsMapping.get(artMethod.getIdentifier());
        }
        return artMethod2;
    }

    public static synchronized void setBackMethod(ArtMethod artMethod, ArtMethod artMethod2) {
        synchronized (Epic.class) {
            backupMethodsMapping.put(artMethod.getIdentifier(), artMethod2);
        }
    }

    public static MethodInfo getMethodInfo(long j) {
        return (MethodInfo) originSigs.get(Long.valueOf(j));
    }

    public static int getQuickCompiledCodeSize(ArtMethod artMethod) {
        int i = ByteBuffer.wrap(EpicNative.get(ShellCode.toMem(artMethod.getEntryPointFromQuickCompiledCode()) - 4, 4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getQuickCompiledCodeSize: ");
        sb.append(i);
        Logger.m92d(str, sb.toString());
        return i;
    }
}
