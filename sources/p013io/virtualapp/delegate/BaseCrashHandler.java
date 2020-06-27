package p013io.virtualapp.delegate;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.util.Log;
import com.lody.virtual.client.core.CrashHandler;

/* renamed from: io.virtualapp.delegate.BaseCrashHandler */
public class BaseCrashHandler implements CrashHandler {
    protected static final String TAG = "XApp";

    @SuppressLint({"ApplySharedPref"})
    public void handleUncaughtException(Thread thread, Throwable th) {
        if (thread == Looper.getMainLooper().getThread()) {
            System.exit(0);
            return;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ignore uncaught exception of sub thread: ");
        sb.append(thread);
        Log.e(str, sb.toString());
    }
}
