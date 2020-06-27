package p013io.virtualapp.abs.p014ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import org.jdeferred.android.AndroidDeferredManager;

/* renamed from: io.virtualapp.abs.ui.VUiKit */
public class VUiKit {
    private static final AndroidDeferredManager gDM = new AndroidDeferredManager();
    private static final Handler gUiHandler = new Handler(Looper.getMainLooper());

    public static AndroidDeferredManager defer() {
        return gDM;
    }

    public static int dpToPx(Context context, int i) {
        return (int) TypedValue.applyDimension(1, (float) i, context.getResources().getDisplayMetrics());
    }

    public static void post(Runnable runnable) {
        gUiHandler.post(runnable);
    }

    public static void postDelayed(long j, Runnable runnable) {
        gUiHandler.postDelayed(runnable, j);
    }

    public static void sleep(long j) {
        try {
            Thread.sleep(j);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
