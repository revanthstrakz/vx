package p015me.weishu.exposed;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.lody.virtual.helper.utils.FileUtils.FileMode;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import p011de.robv.android.xposed.XposedBridge;
import p011de.robv.android.xposed.XposedHelpers;

/* renamed from: me.weishu.exposed.LogcatService */
public class LogcatService extends Service {
    private static final String PATH_KEY = "path";
    private static final String TAG = "LogcatService";
    /* access modifiers changed from: private */
    public volatile boolean mReading = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            String stringExtra = intent.getStringExtra(PATH_KEY);
            if (!TextUtils.isEmpty(stringExtra)) {
                startReadLogcat(stringExtra);
            }
        }
        return super.onStartCommand(intent, i, i2);
    }

    private void startReadLogcat(final String str) {
        if (!this.mReading) {
            C13041 r0 = new Thread("exposed-logcat") {
                public void run() {
                    super.run();
                    LogcatService.this.mReading = true;
                    try {
                        Log.i(LogcatService.TAG, "exposed logcat start..");
                        ArrayList arrayList = new ArrayList();
                        arrayList.add("sh");
                        arrayList.add("-c");
                        StringBuilder sb = new StringBuilder();
                        sb.append("logcat -v time -s XposedStartupMarker:D Xposed:I appproc:I XposedInstaller:I art:F DexposedBridge:I ExposedBridge:D Runtime:I EpicNative:D VClientImpl:D VApp:I  >> ");
                        sb.append(str);
                        arrayList.add(sb.toString());
                        new ProcessBuilder(arrayList).start().waitFor();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            };
            r0.setPriority(1);
            r0.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                public void uncaughtException(Thread thread, Throwable th) {
                    XposedBridge.log(th);
                    LogcatService.this.mReading = false;
                }
            });
            r0.start();
        }
    }

    public static void start(Context context, File file) {
        Intent intent = new Intent(context, LogcatService.class);
        File file2 = new File(file, "exposed_log");
        if (!file2.exists() || !file2.isDirectory()) {
            file2.mkdir();
            setWorldReadable(file2);
        }
        File file3 = new File(file2, "error.log");
        if (!file3.exists() || !file3.isFile()) {
            try {
                file3.createNewFile();
                setWorldReadable(file3);
            } catch (IOException unused) {
            }
        }
        intent.putExtra(PATH_KEY, file3.getPath());
        context.startService(intent);
    }

    private static void setWorldReadable(File file) {
        XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.os.FileUtils", ClassLoader.getSystemClassLoader()), "setPermissions", file.getPath(), Integer.valueOf(FileMode.MODE_755), Integer.valueOf(-1), Integer.valueOf(-1));
    }
}
