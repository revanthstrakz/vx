package p015me.weishu.epic.art;

import android.util.Log;
import com.taobao.android.dexposed.DeviceCheck;
import com.taobao.android.dexposed.utility.Debug;
import com.taobao.android.dexposed.utility.Logger;
import com.taobao.android.dexposed.utility.Unsafe;
import java.lang.reflect.Member;
import p011de.robv.android.xposed.XposedHelpers;

/* renamed from: me.weishu.epic.art.EpicNative */
public final class EpicNative {
    private static final String TAG = "EpicNative";
    private static volatile boolean useUnsafe = false;

    static native boolean activateNative(long j, long j2, long j3, long j4, byte[] bArr);

    public static native boolean cacheflush(long j, long j2);

    public static native boolean compileMethod(Member member, long j);

    public static native void disableMovingGc(int i);

    public static native long getMethodAddress(Member member);

    public static native Object getObjectNative(long j, long j2);

    private static native boolean isGetObjectAvailable();

    public static native long malloc(int i);

    public static native void memcpy(long j, long j2, int i);

    public static native byte[] memget(long j, int i);

    public static native void memput(byte[] bArr, long j);

    public static native long mmap(int i);

    public static native boolean munmap(long j, int i);

    public static native boolean munprotect(long j, long j2);

    public static native void resumeAll(long j);

    public static native void startJit(long j);

    public static native long stopJit();

    public static native long suspendAll();

    static {
        boolean z;
        try {
            System.loadLibrary("epic");
            if (!DeviceCheck.isYunOS()) {
                if (isGetObjectAvailable()) {
                    z = false;
                    useUnsafe = z;
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("use unsafe ? ");
                    sb.append(useUnsafe);
                    Log.i(str, sb.toString());
                }
            }
            z = true;
            useUnsafe = z;
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("use unsafe ? ");
            sb2.append(useUnsafe);
            Log.i(str2, sb2.toString());
        } catch (Throwable th) {
            Log.e(TAG, "init EpicNative error", th);
        }
    }

    public static Object getObject(long j, long j2) {
        if (useUnsafe) {
            return Unsafe.getObject(j2);
        }
        return getObjectNative(j, j2);
    }

    private EpicNative() {
    }

    public static boolean compileMethod(Member member) {
        return compileMethod(member, XposedHelpers.getLongField(Thread.currentThread(), "nativePeer"));
    }

    public static Object getObject(long j) {
        return getObject(XposedHelpers.getLongField(Thread.currentThread(), "nativePeer"), j);
    }

    public static long map(int i) {
        long mmap = mmap(i);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Mapped memory of size ");
        sb.append(i);
        sb.append(" at ");
        sb.append(Debug.addrHex(mmap));
        Logger.m95i(str, sb.toString());
        return mmap;
    }

    public static boolean unmap(long j, int i) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Removing mapped memory of size ");
        sb.append(i);
        sb.append(" at ");
        sb.append(Debug.addrHex(j));
        Logger.m92d(str, sb.toString());
        return munmap(j, i);
    }

    public static void put(byte[] bArr, long j) {
        memput(bArr, j);
    }

    public static byte[] get(long j, int i) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Reading ");
        sb.append(i);
        sb.append(" bytes from: ");
        sb.append(Debug.addrHex(j));
        Logger.m92d(str, sb.toString());
        byte[] memget = memget(j, i);
        Logger.m92d(TAG, Debug.hexdump(memget, j));
        return memget;
    }

    public static boolean unprotect(long j, long j2) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Disabling mprotect from ");
        sb.append(Debug.addrHex(j));
        Logger.m92d(str, sb.toString());
        return munprotect(j, j2);
    }

    public static void copy(long j, long j2, int i) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Copy ");
        sb.append(i);
        sb.append(" bytes form ");
        sb.append(Debug.addrHex(j));
        sb.append(" to ");
        sb.append(Debug.addrHex(j2));
        Logger.m92d(str, sb.toString());
        memcpy(j, j2, i);
    }
}
