package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class BaseClientService implements ServiceConnection {
    private final ServiceConnection mBridge = new LauncherClientBridge(this);
    private boolean mConnected;
    private final Context mContext;
    private final int mFlags;

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
    }

    public void onServiceDisconnected(ComponentName componentName) {
    }

    BaseClientService(Context context, int i) {
        this.mContext = context;
        this.mFlags = i;
    }

    public final boolean connect() {
        if (!this.mConnected) {
            try {
                this.mConnected = this.mContext.bindService(LauncherClient.getIntent(this.mContext, true), this.mBridge, this.mFlags);
            } catch (Throwable th) {
                Log.e("LauncherClient", "Unable to connect to overlay service", th);
            }
        }
        return this.mConnected;
    }

    public final void disconnect() {
        if (this.mConnected) {
            this.mContext.unbindService(this.mBridge);
            this.mConnected = false;
        }
    }
}
