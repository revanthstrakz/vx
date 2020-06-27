package com.lody.virtual.helper.compat;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.VLog;
import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import mirror.com.android.internal.content.NativeLibraryHelper;
import mirror.com.android.internal.content.NativeLibraryHelper.Handle;
import mirror.dalvik.system.VMRuntime;

public class NativeLibraryHelperCompat {
    private static String TAG = "NativeLibraryHelperCompat";

    public static int copyNativeBinaries(File file, File file2) {
        if (VERSION.SDK_INT >= 21) {
            return copyNativeBinariesAfterL(file, file2);
        }
        return copyNativeBinariesBeforeL(file, file2);
    }

    private static int copyNativeBinariesBeforeL(File file, File file2) {
        try {
            return ((Integer) Reflect.m79on(NativeLibraryHelper.TYPE).call("copyNativeBinariesIfNeededLI", file, file2).get()).intValue();
        } catch (Throwable th) {
            th.printStackTrace();
            return -1;
        }
    }

    @TargetApi(21)
    private static int copyNativeBinariesAfterL(File file, File file2) {
        try {
            Object call = Handle.create.call(file);
            if (call == null) {
                return -1;
            }
            String str = null;
            Set aBIsFromApk = getABIsFromApk(file.getAbsolutePath());
            if (aBIsFromApk != null) {
                if (!aBIsFromApk.isEmpty()) {
                    if (!((Boolean) VMRuntime.is64Bit.call(VMRuntime.getRuntime.call(new Object[0]), new Object[0])).booleanValue() || !isVM64(aBIsFromApk)) {
                        if (Build.SUPPORTED_32_BIT_ABIS.length > 0) {
                            int intValue = ((Integer) NativeLibraryHelper.findSupportedAbi.call(call, Build.SUPPORTED_32_BIT_ABIS)).intValue();
                            if (intValue >= 0) {
                                str = Build.SUPPORTED_32_BIT_ABIS[intValue];
                            }
                        }
                    } else if (Build.SUPPORTED_64_BIT_ABIS.length > 0) {
                        int intValue2 = ((Integer) NativeLibraryHelper.findSupportedAbi.call(call, Build.SUPPORTED_64_BIT_ABIS)).intValue();
                        if (intValue2 >= 0) {
                            str = Build.SUPPORTED_64_BIT_ABIS[intValue2];
                        }
                    }
                    if (str == null) {
                        VLog.m87e(TAG, "Not match any abi [%s].", file.getPath());
                        return -1;
                    }
                    return ((Integer) NativeLibraryHelper.copyNativeBinaries.call(call, file2, str)).intValue();
                }
            }
            return 0;
        } catch (Throwable th) {
            VLog.m86d(TAG, "copyNativeBinaries with error : %s", th.getLocalizedMessage());
            th.printStackTrace();
            return -1;
        }
    }

    @TargetApi(21)
    private static boolean isVM64(Set<String> set) {
        if (Build.SUPPORTED_64_BIT_ABIS.length == 0) {
            return false;
        }
        if (set == null || set.isEmpty()) {
            return true;
        }
        for (String str : set) {
            if ("arm64-v8a".endsWith(str) || "x86_64".equals(str)) {
                return true;
            }
            if ("mips64".equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> getABIsFromApk(String str) {
        try {
            Enumeration entries = new ZipFile(str).entries();
            HashSet hashSet = new HashSet();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                String name = zipEntry.getName();
                if (!name.contains("../")) {
                    if (name.startsWith("lib/") && !zipEntry.isDirectory() && name.endsWith(".so")) {
                        hashSet.add(name.substring(name.indexOf("/") + 1, name.lastIndexOf("/")));
                    }
                }
            }
            return hashSet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
