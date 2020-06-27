package com.android.launcher3.testing;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import com.android.launcher3.testing.MemoryTracker.MemoryTrackerInterface;
import com.android.launcher3.testing.MemoryTracker.ProcessMemInfo;
import com.microsoft.appcenter.Constants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MemoryDumpActivity extends Activity {
    private static final String TAG = "MemoryDumpActivity";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x007e A[SYNTHETIC, Splitter:B:37:0x007e] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0085 A[SYNTHETIC, Splitter:B:43:0x0085] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String zipUp(java.util.ArrayList<java.lang.String> r9) {
        /*
            r0 = 262144(0x40000, float:3.67342E-40)
            byte[] r1 = new byte[r0]
            java.lang.String r2 = "%s/hprof-%d.zip"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.io.File r4 = android.os.Environment.getExternalStorageDirectory()
            r5 = 0
            r3[r5] = r4
            long r6 = java.lang.System.currentTimeMillis()
            java.lang.Long r4 = java.lang.Long.valueOf(r6)
            r6 = 1
            r3[r6] = r4
            java.lang.String r2 = java.lang.String.format(r2, r3)
            r3 = 0
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0073, all -> 0x0070 }
            r4.<init>(r2)     // Catch:{ IOException -> 0x0073, all -> 0x0070 }
            java.util.zip.ZipOutputStream r6 = new java.util.zip.ZipOutputStream     // Catch:{ IOException -> 0x0073, all -> 0x0070 }
            java.io.BufferedOutputStream r7 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x0073, all -> 0x0070 }
            r7.<init>(r4)     // Catch:{ IOException -> 0x0073, all -> 0x0070 }
            r6.<init>(r7)     // Catch:{ IOException -> 0x0073, all -> 0x0070 }
            java.util.Iterator r9 = r9.iterator()     // Catch:{ IOException -> 0x006e }
        L_0x0033:
            boolean r4 = r9.hasNext()     // Catch:{ IOException -> 0x006e }
            if (r4 == 0) goto L_0x006a
            java.lang.Object r4 = r9.next()     // Catch:{ IOException -> 0x006e }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ IOException -> 0x006e }
            java.io.BufferedInputStream r7 = new java.io.BufferedInputStream     // Catch:{ all -> 0x0064 }
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ all -> 0x0064 }
            r8.<init>(r4)     // Catch:{ all -> 0x0064 }
            r7.<init>(r8)     // Catch:{ all -> 0x0064 }
            java.util.zip.ZipEntry r8 = new java.util.zip.ZipEntry     // Catch:{ all -> 0x0062 }
            r8.<init>(r4)     // Catch:{ all -> 0x0062 }
            r6.putNextEntry(r8)     // Catch:{ all -> 0x0062 }
        L_0x0051:
            int r4 = r7.read(r1, r5, r0)     // Catch:{ all -> 0x0062 }
            if (r4 <= 0) goto L_0x005b
            r6.write(r1, r5, r4)     // Catch:{ all -> 0x0062 }
            goto L_0x0051
        L_0x005b:
            r6.closeEntry()     // Catch:{ all -> 0x0062 }
            r7.close()     // Catch:{ IOException -> 0x006e }
            goto L_0x0033
        L_0x0062:
            r9 = move-exception
            goto L_0x0066
        L_0x0064:
            r9 = move-exception
            r7 = r3
        L_0x0066:
            r7.close()     // Catch:{ IOException -> 0x006e }
            throw r9     // Catch:{ IOException -> 0x006e }
        L_0x006a:
            r6.close()     // Catch:{ IOException -> 0x006d }
        L_0x006d:
            return r2
        L_0x006e:
            r9 = move-exception
            goto L_0x0075
        L_0x0070:
            r9 = move-exception
            r6 = r3
            goto L_0x0083
        L_0x0073:
            r9 = move-exception
            r6 = r3
        L_0x0075:
            java.lang.String r0 = "MemoryDumpActivity"
            java.lang.String r1 = "error zipping up profile data"
            android.util.Log.e(r0, r1, r9)     // Catch:{ all -> 0x0082 }
            if (r6 == 0) goto L_0x0081
            r6.close()     // Catch:{ IOException -> 0x0081 }
        L_0x0081:
            return r3
        L_0x0082:
            r9 = move-exception
        L_0x0083:
            if (r6 == 0) goto L_0x0088
            r6.close()     // Catch:{ IOException -> 0x0088 }
        L_0x0088:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.testing.MemoryDumpActivity.zipUp(java.util.ArrayList):java.lang.String");
    }

    public static void dumpHprofAndShare(Context context, MemoryTracker memoryTracker) {
        int[] copyOf;
        String str;
        StringBuilder sb = new StringBuilder();
        ArrayList arrayList = new ArrayList();
        int myPid = Process.myPid();
        int[] trackedProcesses = memoryTracker.getTrackedProcesses();
        for (int i : Arrays.copyOf(trackedProcesses, trackedProcesses.length)) {
            ProcessMemInfo memInfo = memoryTracker.getMemInfo(i);
            if (memInfo != null) {
                sb.append("pid ");
                sb.append(i);
                sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
                sb.append(" up=");
                sb.append(memInfo.getUptime());
                sb.append(" pss=");
                sb.append(memInfo.currentPss);
                sb.append(" uss=");
                sb.append(memInfo.currentUss);
                sb.append("\n");
            }
            if (i == myPid) {
                String format = String.format("%s/launcher-memory-%d.ahprof", new Object[]{Environment.getExternalStorageDirectory(), Integer.valueOf(i)});
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Dumping memory info for process ");
                sb2.append(i);
                sb2.append(" to ");
                sb2.append(format);
                Log.v(str2, sb2.toString());
                try {
                    Debug.dumpHprofData(format);
                } catch (IOException e) {
                    Log.e(TAG, "error dumping memory:", e);
                }
                arrayList.add(format);
            }
        }
        String zipUp = zipUp(arrayList);
        if (zipUp != null) {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("application/zip");
            PackageManager packageManager = context.getPackageManager();
            intent.putExtra("android.intent.extra.SUBJECT", String.format("Launcher memory dump (%d)", new Object[]{Integer.valueOf(myPid)}));
            try {
                str = packageManager.getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (NameNotFoundException unused) {
                str = "?";
            }
            sb.append("\nApp version: ");
            sb.append(str);
            sb.append("\nBuild: ");
            sb.append(Build.DISPLAY);
            sb.append("\n");
            intent.putExtra("android.intent.extra.TEXT", sb.toString());
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(zipUp)));
            context.startActivity(intent);
        }
    }

    public void onStart() {
        super.onStart();
        startDump(this, new Runnable() {
            public void run() {
                MemoryDumpActivity.this.finish();
            }
        });
    }

    public static void startDump(Context context) {
        startDump(context, null);
    }

    public static void startDump(final Context context, final Runnable runnable) {
        C08232 r0 = new ServiceConnection() {
            public void onServiceDisconnected(ComponentName componentName) {
            }

            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.v(MemoryDumpActivity.TAG, "service connected, dumping...");
                MemoryDumpActivity.dumpHprofAndShare(context, ((MemoryTrackerInterface) iBinder).getService());
                context.unbindService(this);
                if (runnable != null) {
                    runnable.run();
                }
            }
        };
        Log.v(TAG, "attempting to bind to memory tracker");
        context.bindService(new Intent(context, MemoryTracker.class), r0, 1);
    }
}
