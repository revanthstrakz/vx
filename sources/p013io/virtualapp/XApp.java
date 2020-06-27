package p013io.virtualapp;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION;
import com.lody.virtual.client.NativeEngine;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.stub.VASettings;
import p013io.virtualapp.delegate.MyVirtualInitializer;

/* renamed from: io.virtualapp.XApp */
public class XApp extends Application {
    private static final String TAG = "XApp";
    public static final String XPOSED_INSTALLER_PACKAGE = "de.robv.android.xposed.installer";
    private static XApp gApp;

    public static XApp getApp() {
        return gApp;
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        gApp = this;
        super.attachBaseContext(context);
        if (VERSION.SDK_INT >= 24) {
            NativeEngine.disableJit(VERSION.SDK_INT);
        }
        VASettings.ENABLE_IO_REDIRECT = true;
        VASettings.ENABLE_INNER_SHORTCUT = false;
        try {
            VirtualCore.get().startup(context);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void onCreate() {
        super.onCreate();
        VirtualCore virtualCore = VirtualCore.get();
        virtualCore.initialize(new MyVirtualInitializer(this, virtualCore));
    }
}
