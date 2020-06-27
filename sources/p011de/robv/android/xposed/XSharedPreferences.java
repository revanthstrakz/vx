package p011de.robv.android.xposed;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import p011de.robv.android.xposed.services.FileResult;

/* renamed from: de.robv.android.xposed.XSharedPreferences */
public final class XSharedPreferences implements SharedPreferences {
    private static final String TAG = "XSharedPreferences";
    private static File sPackageBaseDirectory = new File(System.getProperty("vxp_user_dir"));
    private final File mFile;
    private long mFileSize;
    private final String mFilename;
    private long mLastModified;
    private boolean mLoaded;
    private Map<String, Object> mMap;

    static {
        if (System.getProperty("vxp") != null) {
        }
    }

    public static void setPackageBaseDirectory(File file) {
        if (sPackageBaseDirectory == null || sPackageBaseDirectory.equals(file)) {
            sPackageBaseDirectory = file;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("you can only set package base dir once, p: ");
        sb.append(sPackageBaseDirectory);
        sb.append(" n:");
        sb.append(file);
        throw new IllegalStateException(sb.toString());
    }

    public XSharedPreferences(File file) {
        this.mLoaded = false;
        this.mFile = file;
        this.mFilename = this.mFile.getAbsolutePath();
        startLoadFromDisk();
    }

    public XSharedPreferences(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("_preferences");
        this(str, sb.toString());
    }

    public XSharedPreferences(String str, String str2) {
        this.mLoaded = false;
        if (sPackageBaseDirectory == null) {
            File dataDirectory = Environment.getDataDirectory();
            StringBuilder sb = new StringBuilder();
            sb.append("data/");
            sb.append(str);
            sb.append("/shared_prefs/");
            sb.append(str2);
            sb.append(".xml");
            this.mFile = new File(dataDirectory, sb.toString());
        } else {
            File file = sPackageBaseDirectory;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append("/shared_prefs/");
            sb2.append(str2);
            sb2.append(".xml");
            this.mFile = new File(file, sb2.toString());
        }
        this.mFilename = this.mFile.getAbsolutePath();
        startLoadFromDisk();
    }

    @SuppressLint({"SetWorldReadable"})
    public boolean makeWorldReadable() {
        if (SELinuxHelper.getAppDataFileService().hasDirectFileAccess() && this.mFile.exists()) {
            return this.mFile.setReadable(true, false);
        }
        return false;
    }

    public File getFile() {
        return this.mFile;
    }

    private void startLoadFromDisk() {
        synchronized (this) {
            this.mLoaded = false;
        }
        new Thread("XSharedPreferences-load") {
            public void run() {
                synchronized (XSharedPreferences.this) {
                    XSharedPreferences.this.loadFromDiskLocked();
                }
            }
        }.start();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0042, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0043, code lost:
        r7 = r1;
        r1 = r0;
        r0 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0048, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0049, code lost:
        r7 = r2;
        r2 = null;
        r0 = r1;
        r1 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x004e, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0050, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0051, code lost:
        r7 = r2;
        r2 = null;
        r0 = r1;
        r1 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x007f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0080, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0098, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0099, code lost:
        throw r0;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:22:0x003a, B:53:0x0079] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:43:0x0067, B:67:0x0092] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0042 A[ExcHandler: all (r0v20 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:6:0x0014] */
    /* JADX WARNING: Removed duplicated region for block: B:45:? A[ExcHandler: Exception (unused java.lang.Exception), PHI: r0 r2 
      PHI: (r0v14 de.robv.android.xposed.services.FileResult) = (r0v8 de.robv.android.xposed.services.FileResult), (r0v8 de.robv.android.xposed.services.FileResult), (r0v15 de.robv.android.xposed.services.FileResult), (r0v15 de.robv.android.xposed.services.FileResult) binds: [B:67:0x0092, B:68:?, B:43:0x0067, B:44:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r2v8 java.util.Map<java.lang.String, java.lang.Object>) = (r2v2 java.util.Map<java.lang.String, java.lang.Object>), (r2v2 java.util.Map<java.lang.String, java.lang.Object>), (r2v9 java.util.Map<java.lang.String, java.lang.Object>), (r2v9 java.util.Map<java.lang.String, java.lang.Object>) binds: [B:67:0x0092, B:68:?, B:43:0x0067, B:44:?] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC, Splitter:B:43:0x0067] */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A[ExcHandler: Exception (unused java.lang.Exception), PHI: r1 r2 
      PHI: (r1v7 de.robv.android.xposed.services.FileResult) = (r1v9 de.robv.android.xposed.services.FileResult), (r1v9 de.robv.android.xposed.services.FileResult), (r1v17 de.robv.android.xposed.services.FileResult), (r1v17 de.robv.android.xposed.services.FileResult) binds: [B:53:0x0079, B:54:?, B:22:0x003a, B:23:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r2v4 java.util.Map<java.lang.String, java.lang.Object>) = (r2v6 java.util.Map<java.lang.String, java.lang.Object>), (r2v6 java.util.Map<java.lang.String, java.lang.Object>), (r2v18 java.util.Map<java.lang.String, java.lang.Object>), (r2v18 java.util.Map<java.lang.String, java.lang.Object>) binds: [B:53:0x0079, B:54:?, B:22:0x003a, B:23:?] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC, Splitter:B:22:0x003a] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00aa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadFromDiskLocked() {
        /*
            r8 = this;
            boolean r0 = r8.mLoaded
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            r0 = 0
            de.robv.android.xposed.services.BaseService r1 = p011de.robv.android.xposed.SELinuxHelper.getAppDataFileService()     // Catch:{ XmlPullParserException -> 0x0083, FileNotFoundException -> 0x0071, IOException -> 0x0058 }
            java.lang.String r2 = r8.mFilename     // Catch:{ XmlPullParserException -> 0x0083, FileNotFoundException -> 0x0071, IOException -> 0x0058 }
            long r3 = r8.mFileSize     // Catch:{ XmlPullParserException -> 0x0083, FileNotFoundException -> 0x0071, IOException -> 0x0058 }
            long r5 = r8.mLastModified     // Catch:{ XmlPullParserException -> 0x0083, FileNotFoundException -> 0x0071, IOException -> 0x0058 }
            de.robv.android.xposed.services.FileResult r1 = r1.getFileInputStream(r2, r3, r5)     // Catch:{ XmlPullParserException -> 0x0083, FileNotFoundException -> 0x0071, IOException -> 0x0058 }
            java.io.InputStream r2 = r1.stream     // Catch:{ XmlPullParserException -> 0x0050, FileNotFoundException -> 0x004e, IOException -> 0x0048, all -> 0x0042 }
            if (r2 == 0) goto L_0x0032
            java.io.InputStream r2 = r1.stream     // Catch:{ XmlPullParserException -> 0x0050, FileNotFoundException -> 0x004e, IOException -> 0x0048, all -> 0x0042 }
            java.util.HashMap r2 = com.android.internal.util.XmlUtils.readMapXml(r2)     // Catch:{ XmlPullParserException -> 0x0050, FileNotFoundException -> 0x004e, IOException -> 0x0048, all -> 0x0042 }
            java.io.InputStream r0 = r1.stream     // Catch:{ XmlPullParserException -> 0x002c, FileNotFoundException -> 0x0029, IOException -> 0x0024, all -> 0x0042 }
            r0.close()     // Catch:{ XmlPullParserException -> 0x002c, FileNotFoundException -> 0x0029, IOException -> 0x0024, all -> 0x0042 }
            goto L_0x0034
        L_0x0024:
            r0 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
            goto L_0x005a
        L_0x0029:
            goto L_0x0073
        L_0x002c:
            r0 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
            goto L_0x0085
        L_0x0032:
            java.util.Map<java.lang.String, java.lang.Object> r2 = r8.mMap     // Catch:{ XmlPullParserException -> 0x0050, FileNotFoundException -> 0x004e, IOException -> 0x0048, all -> 0x0042 }
        L_0x0034:
            if (r1 == 0) goto L_0x0081
            java.io.InputStream r0 = r1.stream
            if (r0 == 0) goto L_0x0081
            java.io.InputStream r0 = r1.stream     // Catch:{ RuntimeException -> 0x0040, Exception -> 0x0081 }
            r0.close()     // Catch:{ RuntimeException -> 0x0040, Exception -> 0x0081 }
            goto L_0x0081
        L_0x0040:
            r0 = move-exception
            throw r0
        L_0x0042:
            r0 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
            goto L_0x00b5
        L_0x0048:
            r2 = move-exception
            r7 = r2
            r2 = r0
            r0 = r1
            r1 = r7
            goto L_0x005a
        L_0x004e:
            r2 = r0
            goto L_0x0073
        L_0x0050:
            r2 = move-exception
            r7 = r2
            r2 = r0
            r0 = r1
            r1 = r7
            goto L_0x0085
        L_0x0056:
            r1 = move-exception
            goto L_0x00b5
        L_0x0058:
            r1 = move-exception
            r2 = r0
        L_0x005a:
            java.lang.String r3 = "XSharedPreferences"
            java.lang.String r4 = "getSharedPreferences"
            android.util.Log.w(r3, r4, r1)     // Catch:{ all -> 0x0056 }
            if (r0 == 0) goto L_0x009a
            java.io.InputStream r1 = r0.stream
            if (r1 == 0) goto L_0x009a
            java.io.InputStream r1 = r0.stream     // Catch:{ RuntimeException -> 0x006f, Exception -> 0x006d }
            r1.close()     // Catch:{ RuntimeException -> 0x006f, Exception -> 0x006d }
            goto L_0x009a
        L_0x006d:
            goto L_0x009a
        L_0x006f:
            r0 = move-exception
            throw r0
        L_0x0071:
            r1 = r0
            r2 = r1
        L_0x0073:
            if (r1 == 0) goto L_0x0081
            java.io.InputStream r0 = r1.stream
            if (r0 == 0) goto L_0x0081
            java.io.InputStream r0 = r1.stream     // Catch:{ RuntimeException -> 0x007f, Exception -> 0x0081 }
            r0.close()     // Catch:{ RuntimeException -> 0x007f, Exception -> 0x0081 }
            goto L_0x0081
        L_0x007f:
            r0 = move-exception
            throw r0
        L_0x0081:
            r0 = r1
            goto L_0x009a
        L_0x0083:
            r1 = move-exception
            r2 = r0
        L_0x0085:
            java.lang.String r3 = "XSharedPreferences"
            java.lang.String r4 = "getSharedPreferences"
            android.util.Log.w(r3, r4, r1)     // Catch:{ all -> 0x0056 }
            if (r0 == 0) goto L_0x009a
            java.io.InputStream r1 = r0.stream
            if (r1 == 0) goto L_0x009a
            java.io.InputStream r1 = r0.stream     // Catch:{ RuntimeException -> 0x0098, Exception -> 0x006d }
            r1.close()     // Catch:{ RuntimeException -> 0x0098, Exception -> 0x006d }
            goto L_0x009a
        L_0x0098:
            r0 = move-exception
            throw r0
        L_0x009a:
            r1 = 1
            r8.mLoaded = r1
            if (r2 == 0) goto L_0x00aa
            r8.mMap = r2
            long r1 = r0.mtime
            r8.mLastModified = r1
            long r0 = r0.size
            r8.mFileSize = r0
            goto L_0x00b1
        L_0x00aa:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r8.mMap = r0
        L_0x00b1:
            r8.notifyAll()
            return
        L_0x00b5:
            if (r0 == 0) goto L_0x00c3
            java.io.InputStream r2 = r0.stream
            if (r2 == 0) goto L_0x00c3
            java.io.InputStream r0 = r0.stream     // Catch:{ RuntimeException -> 0x00c1, Exception -> 0x00c3 }
            r0.close()     // Catch:{ RuntimeException -> 0x00c1, Exception -> 0x00c3 }
            goto L_0x00c3
        L_0x00c1:
            r0 = move-exception
            throw r0
        L_0x00c3:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: p011de.robv.android.xposed.XSharedPreferences.loadFromDiskLocked():void");
    }

    public synchronized void reload() {
        if (hasFileChanged()) {
            startLoadFromDisk();
        }
    }

    public synchronized boolean hasFileChanged() {
        boolean z;
        z = true;
        try {
            FileResult statFile = SELinuxHelper.getAppDataFileService().statFile(this.mFilename);
            if (this.mLastModified == statFile.mtime && this.mFileSize == statFile.size) {
                z = false;
            }
        } catch (FileNotFoundException unused) {
            return true;
        } catch (IOException e) {
            Log.w(TAG, "hasFileChanged", e);
            return true;
        }
        return z;
    }

    private void awaitLoadedLocked() {
        while (!this.mLoaded) {
            try {
                wait();
            } catch (InterruptedException unused) {
            }
        }
    }

    public Map<String, ?> getAll() {
        HashMap hashMap;
        synchronized (this) {
            awaitLoadedLocked();
            hashMap = new HashMap(this.mMap);
        }
        return hashMap;
    }

    public String getString(String str, String str2) {
        String str3;
        synchronized (this) {
            awaitLoadedLocked();
            str3 = (String) this.mMap.get(str);
            if (str3 == null) {
                str3 = str2;
            }
        }
        return str3;
    }

    public Set<String> getStringSet(String str, Set<String> set) {
        Set<String> set2;
        synchronized (this) {
            awaitLoadedLocked();
            set2 = (Set) this.mMap.get(str);
            if (set2 == null) {
                set2 = set;
            }
        }
        return set2;
    }

    public int getInt(String str, int i) {
        synchronized (this) {
            awaitLoadedLocked();
            Integer num = (Integer) this.mMap.get(str);
            if (num != null) {
                i = num.intValue();
            }
        }
        return i;
    }

    public long getLong(String str, long j) {
        synchronized (this) {
            awaitLoadedLocked();
            Long l = (Long) this.mMap.get(str);
            if (l != null) {
                j = l.longValue();
            }
        }
        return j;
    }

    public float getFloat(String str, float f) {
        synchronized (this) {
            awaitLoadedLocked();
            Float f2 = (Float) this.mMap.get(str);
            if (f2 != null) {
                f = f2.floatValue();
            }
        }
        return f;
    }

    public boolean getBoolean(String str, boolean z) {
        synchronized (this) {
            awaitLoadedLocked();
            Boolean bool = (Boolean) this.mMap.get(str);
            if (bool != null) {
                z = bool.booleanValue();
            }
        }
        return z;
    }

    public boolean contains(String str) {
        boolean containsKey;
        synchronized (this) {
            awaitLoadedLocked();
            containsKey = this.mMap.containsKey(str);
        }
        return containsKey;
    }

    @Deprecated
    public Editor edit() {
        throw new UnsupportedOperationException("read-only implementation");
    }

    @Deprecated
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        throw new UnsupportedOperationException("listeners are not supported in this implementation");
    }

    @Deprecated
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        throw new UnsupportedOperationException("listeners are not supported in this implementation");
    }
}
