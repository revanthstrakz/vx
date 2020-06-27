package com.google.android.libraries.gsa.launcherclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.google.android.libraries.launcherclient.ILauncherOverlay;
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback.Stub;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.microsoft.appcenter.Constants;
import java.lang.ref.WeakReference;

public class LauncherClient {
    public static final String BRIDGE_PACKAGE = "com.google.android.apps.nexuslauncher";
    public static final boolean BRIDGE_USE = true;
    private static int apiVersion = -1;
    public final BroadcastReceiver googleInstallListener = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            LauncherClient.this.mBaseService.disconnect();
            LauncherClient.this.mLauncherService.disconnect();
            LauncherClient.loadApiVersion(context);
            if ((LauncherClient.this.mActivityState & 2) != 0) {
                LauncherClient.this.reconnect();
            }
        }
    };
    public final Activity mActivity;
    /* access modifiers changed from: private */
    public int mActivityState = 0;
    public final BaseClientService mBaseService;
    public boolean mDestroyed = false;
    public int mFlags;
    public final LauncherClientService mLauncherService;
    private Bundle mLayoutBundle;
    public LayoutParams mLayoutParams;
    private ILauncherOverlay mOverlay;
    public OverlayCallback mOverlayCallback;
    /* access modifiers changed from: private */
    public final IScrollCallback mScrollCallback;
    /* access modifiers changed from: private */
    public int mServiceState = 0;

    public class OverlayCallback extends Stub implements Callback {
        public LauncherClient mClient;
        private final Handler mUIHandler = new Handler(Looper.getMainLooper(), this);
        public Window mWindow;
        private boolean mWindowHidden = false;
        public WindowManager mWindowManager;
        int mWindowShift;

        public OverlayCallback() {
        }

        public final void overlayScrollChanged(float f) {
            this.mUIHandler.removeMessages(2);
            Message.obtain(this.mUIHandler, 2, Float.valueOf(f)).sendToTarget();
            if (f > 0.0f && this.mWindowHidden) {
                this.mWindowHidden = false;
            }
        }

        public final void overlayStatusChanged(int i) {
            Message.obtain(this.mUIHandler, 4, i, 0).sendToTarget();
        }

        public boolean handleMessage(Message message) {
            if (this.mClient == null) {
                return true;
            }
            switch (message.what) {
                case 2:
                    if ((this.mClient.mServiceState & 1) != 0) {
                        this.mClient.mScrollCallback.onOverlayScrollChanged(((Float) message.obj).floatValue());
                    }
                    return true;
                case 3:
                    LayoutParams attributes = this.mWindow.getAttributes();
                    if (((Boolean) message.obj).booleanValue()) {
                        attributes.x = this.mWindowShift;
                        attributes.flags |= 512;
                    } else {
                        attributes.x = 0;
                        attributes.flags &= -513;
                    }
                    this.mWindowManager.updateViewLayout(this.mWindow.getDecorView(), attributes);
                    return true;
                case 4:
                    this.mClient.setServiceState(message.arg1);
                    if (this.mClient.mScrollCallback instanceof ISerializableScrollCallback) {
                        ((ISerializableScrollCallback) this.mClient.mScrollCallback).setPersistentFlags(message.arg1);
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    public LauncherClient(Activity activity, IScrollCallback iScrollCallback, StaticInteger staticInteger) {
        this.mActivity = activity;
        this.mScrollCallback = iScrollCallback;
        this.mBaseService = new BaseClientService(activity, 65);
        this.mFlags = staticInteger.mData;
        this.mLauncherService = LauncherClientService.getInstance(activity);
        this.mLauncherService.mClient = new WeakReference<>(this);
        this.mOverlay = this.mLauncherService.mOverlay;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme(ServiceManagerNative.PACKAGE);
        intentFilter.addDataSchemeSpecificPart("com.google.android.googlequicksearchbox", 0);
        this.mActivity.registerReceiver(this.googleInstallListener, intentFilter);
        if (apiVersion <= 0) {
            loadApiVersion(activity);
        }
        reconnect();
        if (this.mActivity.getWindow() != null && this.mActivity.getWindow().peekDecorView() != null && this.mActivity.getWindow().peekDecorView().isAttachedToWindow()) {
            onAttachedToWindow();
        }
    }

    public final void onAttachedToWindow() {
        if (!this.mDestroyed) {
            setLayoutParams(this.mActivity.getWindow().getAttributes());
        }
    }

    public final void onDetachedFromWindow() {
        if (!this.mDestroyed) {
            setLayoutParams(null);
        }
    }

    public final void onResume() {
        if (!this.mDestroyed) {
            this.mActivityState |= 2;
            if (this.mOverlay != null && this.mLayoutParams != null) {
                try {
                    if (apiVersion < 4) {
                        this.mOverlay.onResume();
                    } else {
                        this.mOverlay.setActivityState(this.mActivityState);
                    }
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public final void onPause() {
        if (!this.mDestroyed) {
            this.mActivityState &= -3;
            if (this.mOverlay != null && this.mLayoutParams != null) {
                try {
                    if (apiVersion < 4) {
                        this.mOverlay.onPause();
                    } else {
                        this.mOverlay.setActivityState(this.mActivityState);
                    }
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public final void onStart() {
        if (!this.mDestroyed) {
            this.mLauncherService.setStopped(false);
            reconnect();
            this.mActivityState |= 1;
            if (this.mOverlay != null && this.mLayoutParams != null) {
                try {
                    this.mOverlay.setActivityState(this.mActivityState);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public final void onStop() {
        if (!this.mDestroyed) {
            this.mLauncherService.setStopped(true);
            this.mBaseService.disconnect();
            this.mActivityState &= -2;
            if (this.mOverlay != null && this.mLayoutParams != null) {
                try {
                    this.mOverlay.setActivityState(this.mActivityState);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void reconnect() {
        if (this.mDestroyed) {
            return;
        }
        if (!this.mLauncherService.connect() || !this.mBaseService.connect()) {
            this.mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    LauncherClient.this.setServiceState(0);
                }
            });
        }
    }

    public final void setLayoutParams(LayoutParams layoutParams) {
        if (this.mLayoutParams != layoutParams) {
            this.mLayoutParams = layoutParams;
            if (this.mLayoutParams != null) {
                exchangeConfig();
            } else if (this.mOverlay != null) {
                try {
                    this.mOverlay.windowDetached(this.mActivity.isChangingConfigurations());
                } catch (RemoteException unused) {
                }
                this.mOverlay = null;
            }
        }
    }

    public final void exchangeConfig() {
        if (this.mOverlay != null) {
            try {
                if (this.mOverlayCallback == null) {
                    this.mOverlayCallback = new OverlayCallback();
                }
                OverlayCallback overlayCallback = this.mOverlayCallback;
                overlayCallback.mClient = this;
                overlayCallback.mWindowManager = this.mActivity.getWindowManager();
                Point point = new Point();
                overlayCallback.mWindowManager.getDefaultDisplay().getRealSize(point);
                overlayCallback.mWindowShift = -Math.max(point.x, point.y);
                overlayCallback.mWindow = this.mActivity.getWindow();
                if (apiVersion < 3) {
                    this.mOverlay.windowAttached(this.mLayoutParams, this.mOverlayCallback, this.mFlags);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("layout_params", this.mLayoutParams);
                    bundle.putParcelable("configuration", this.mActivity.getResources().getConfiguration());
                    bundle.putInt("client_options", this.mFlags);
                    if (this.mLayoutBundle != null) {
                        bundle.putAll(this.mLayoutBundle);
                    }
                    this.mOverlay.windowAttached2(bundle, this.mOverlayCallback);
                }
                if (apiVersion >= 4) {
                    this.mOverlay.setActivityState(this.mActivityState);
                } else if ((this.mActivityState & 2) != 0) {
                    this.mOverlay.onResume();
                } else {
                    this.mOverlay.onPause();
                }
            } catch (RemoteException unused) {
            }
        }
    }

    private boolean isConnected() {
        return this.mOverlay != null;
    }

    public final void startScroll() {
        if (isConnected()) {
            try {
                this.mOverlay.startScroll();
            } catch (RemoteException unused) {
            }
        }
    }

    public final void endScroll() {
        if (isConnected()) {
            try {
                this.mOverlay.endScroll();
            } catch (RemoteException unused) {
            }
        }
    }

    public final void setScroll(float f) {
        if (isConnected()) {
            try {
                this.mOverlay.onScroll(f);
            } catch (RemoteException unused) {
            }
        }
    }

    public final void hideOverlay(boolean z) {
        if (this.mOverlay != null) {
            try {
                this.mOverlay.closeOverlay(z ? 1 : 0);
            } catch (RemoteException unused) {
            }
        }
    }

    public final boolean startSearch(byte[] bArr, Bundle bundle) {
        if (apiVersion >= 6 && this.mOverlay != null) {
            try {
                return this.mOverlay.startSearch(bArr, bundle);
            } catch (Throwable th) {
                Log.e("DrawerOverlayClient", "Error starting session for search", th);
            }
        }
        return false;
    }

    public final void redraw(Bundle bundle) {
        this.mLayoutBundle = bundle;
        if (this.mLayoutParams != null && apiVersion >= 7) {
            exchangeConfig();
        }
    }

    /* access modifiers changed from: 0000 */
    public final void setOverlay(ILauncherOverlay iLauncherOverlay) {
        this.mOverlay = iLauncherOverlay;
        if (this.mOverlay == null) {
            setServiceState(0);
        } else if (this.mLayoutParams != null) {
            exchangeConfig();
        }
    }

    /* access modifiers changed from: private */
    public void setServiceState(int i) {
        if (this.mServiceState != i) {
            this.mServiceState = i;
            IScrollCallback iScrollCallback = this.mScrollCallback;
            boolean z = true;
            if ((i & 1) == 0) {
                z = false;
            }
            iScrollCallback.onServiceStateChanged(z);
        }
    }

    static Intent getIntent(Context context, boolean z) {
        String packageName = context.getPackageName();
        Intent intent = new Intent("com.android.launcher3.WINDOW_OVERLAY").setPackage(z ? BRIDGE_PACKAGE : "com.google.android.googlequicksearchbox");
        StringBuilder sb = new StringBuilder(packageName.length() + 18);
        sb.append("app://");
        sb.append(packageName);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(Process.myUid());
        return intent.setData(Uri.parse(sb.toString()).buildUpon().appendQueryParameter("v", Integer.toString(7)).appendQueryParameter("cv", Integer.toString(9)).build());
    }

    /* access modifiers changed from: private */
    public static void loadApiVersion(Context context) {
        ResolveInfo resolveService = context.getPackageManager().resolveService(getIntent(context, false), 128);
        int i = 1;
        if (!(resolveService == null || resolveService.serviceInfo.metaData == null)) {
            i = resolveService.serviceInfo.metaData.getInt("service.api.version", 1);
        }
        apiVersion = i;
    }
}
