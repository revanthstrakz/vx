package com.lody.virtual.p007os;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Environment;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.helper.utils.EncodeUtils;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.FileUtils.FileMode;
import com.lody.virtual.helper.utils.VLog;
import java.io.File;
import java.util.Locale;
import mirror.dalvik.system.VMRuntime;

/* renamed from: com.lody.virtual.os.VEnvironment */
public class VEnvironment {
    private static final File DALVIK_CACHE_DIRECTORY = ensureCreated(new File(ROOT, "opt"));
    private static final File DATA_DIRECTORY = ensureCreated(new File(ROOT, "data"));
    private static final File ROOT = ensureCreated(new File(new File(getContext().getApplicationInfo().dataDir), "virtual"));
    private static final String TAG = "VEnvironment";
    private static final File USER_DIRECTORY = ensureCreated(new File(DATA_DIRECTORY, ServiceManagerNative.USER));

    public static void systemReady() {
        if (VERSION.SDK_INT >= 21) {
            try {
                FileUtils.chmod(ROOT.getAbsolutePath(), FileMode.MODE_755);
                FileUtils.chmod(DATA_DIRECTORY.getAbsolutePath(), FileMode.MODE_755);
                FileUtils.chmod(getDataAppDirectory().getAbsolutePath(), FileMode.MODE_755);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Context getContext() {
        return VirtualCore.get().getContext();
    }

    private static File ensureCreated(File file) {
        if (!file.exists() && !file.mkdirs()) {
            VLog.m91w(TAG, "Unable to create the directory: %s.", file.getPath());
        }
        return file;
    }

    public static File getDataUserPackageDirectory(int i, String str) {
        return ensureCreated(new File(getUserSystemDirectory(i), str));
    }

    public static File getPackageResourcePath(String str) {
        return new File(getDataAppPackageDirectory(str), EncodeUtils.decode("YmFzZS5hcGs="));
    }

    public static File getDataAppDirectory() {
        return ensureCreated(new File(getDataDirectory(), ServiceManagerNative.APP));
    }

    public static File getUidListFile() {
        return new File(getSystemSecureDirectory(), "uid-list.ini");
    }

    public static File getBakUidListFile() {
        return new File(getSystemSecureDirectory(), "uid-list.ini.bak");
    }

    public static File getAccountConfigFile() {
        return new File(getSystemSecureDirectory(), "account-list.ini");
    }

    public static File getVirtualLocationFile() {
        return new File(getSystemSecureDirectory(), "virtual-loc.ini");
    }

    public static File getDeviceInfoFile() {
        return new File(getSystemSecureDirectory(), "device-info.ini");
    }

    public static File getPackageListFile() {
        return new File(getSystemSecureDirectory(), "packages.ini");
    }

    public static File getVSConfigFile() {
        return new File(getSystemSecureDirectory(), "vss.ini");
    }

    public static File getBakPackageListFile() {
        return new File(getSystemSecureDirectory(), "packages.ini.bak");
    }

    public static File getJobConfigFile() {
        return new File(getSystemSecureDirectory(), "job-list.ini");
    }

    public static File getDalvikCacheDirectory() {
        return DALVIK_CACHE_DIRECTORY;
    }

    public static File getOdexFile(String str) {
        if (isAndroidO()) {
            String str2 = (String) VMRuntime.getCurrentInstructionSet.call(new Object[0]);
            File dataAppPackageDirectory = getDataAppPackageDirectory(str);
            StringBuilder sb = new StringBuilder();
            sb.append("oat");
            sb.append(File.separator);
            sb.append(str2);
            return new File(ensureCreated(new File(dataAppPackageDirectory, sb.toString())), EncodeUtils.decode("YmFzZS5vZGV4"));
        }
        File file = DALVIK_CACHE_DIRECTORY;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(EncodeUtils.decode("ZGF0YUBhcHBA"));
        sb2.append(str);
        sb2.append(EncodeUtils.decode("LTFAYmFzZS5hcGtAY2xhc3Nlcy5kZXg="));
        return new File(file, sb2.toString());
    }

    public static File getDataAppPackageDirectory(String str) {
        return ensureCreated(new File(getDataAppDirectory(), str));
    }

    public static File getAppLibDirectory(String str) {
        return ensureCreated(new File(getDataAppPackageDirectory(str), "lib"));
    }

    public static File getPackageCacheFile(String str) {
        return new File(getDataAppPackageDirectory(str), "package.ini");
    }

    public static File getSignatureFile(String str) {
        return new File(getDataAppPackageDirectory(str), "signature.ini");
    }

    public static File getUserSystemDirectory() {
        return USER_DIRECTORY;
    }

    public static File getUserSystemDirectory(int i) {
        return new File(USER_DIRECTORY, String.valueOf(i));
    }

    public static File getVirtualStorageBaseDir() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (externalStorageDirectory != null) {
            return ensureCreated(new File(new File(externalStorageDirectory, "VirtualXposed"), "vsdcard"));
        }
        return null;
    }

    public static File getVirtualStorageDir(String str, int i) {
        File virtualStorageBaseDir = getVirtualStorageBaseDir();
        if (virtualStorageBaseDir == null) {
            return null;
        }
        return ensureCreated(new File(virtualStorageBaseDir, String.valueOf(i)));
    }

    public static File getVirtualPrivateStorageDir(int i) {
        return ensureCreated(new File(String.format(Locale.ENGLISH, "%s/Android/data/%s/%s/%d", new Object[]{Environment.getExternalStorageDirectory(), VirtualCore.get().getHostPkg(), "virtual", Integer.valueOf(i)})));
    }

    public static File getVirtualPrivateStorageDir(int i, String str) {
        return ensureCreated(new File(getVirtualPrivateStorageDir(i), str));
    }

    public static File getWifiMacFile(int i) {
        return new File(getUserSystemDirectory(i), EncodeUtils.decode("d2lmaU1hY0FkZHJlc3M="));
    }

    public static File getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public static File getSystemSecureDirectory() {
        return ensureCreated(new File(getDataAppDirectory(), "system"));
    }

    public static File getPackageInstallerStageDir() {
        return ensureCreated(new File(DATA_DIRECTORY, ".session_dir"));
    }

    public static boolean isAndroidO() {
        return VERSION.SDK_INT > 25;
    }
}
