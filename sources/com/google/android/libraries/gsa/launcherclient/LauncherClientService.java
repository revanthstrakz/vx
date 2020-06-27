package com.google.android.libraries.gsa.launcherclient;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import com.google.android.libraries.launcherclient.ILauncherOverlay;
import com.google.android.libraries.launcherclient.ILauncherOverlay.Stub;
import java.lang.ref.WeakReference;

public class LauncherClientService extends BaseClientService {
    public static LauncherClientService sInstance;
    public WeakReference<LauncherClient> mClient;
    public ILauncherOverlay mOverlay;
    private boolean mStopped;

    static LauncherClientService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LauncherClientService(context.getApplicationContext());
        }
        return sInstance;
    }

    private LauncherClientService(Context context) {
        super(context, 33);
    }

    public final void setStopped(boolean z) {
        this.mStopped = z;
        cleanUp();
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        setClient(Stub.asInterface(iBinder));
    }

    public void onServiceDisconnected(ComponentName componentName) {
        setClient(null);
        cleanUp();
    }

    private void cleanUp() {
        if (this.mStopped && this.mOverlay == null) {
            disconnect();
        }
    }

    private void setClient(ILauncherOverlay iLauncherOverlay) {
        this.mOverlay = iLauncherOverlay;
        LauncherClient client = getClient();
        if (client != null) {
            client.setOverlay(this.mOverlay);
        }
    }

    public final LauncherClient getClient() {
        if (this.mClient != null) {
            return (LauncherClient) this.mClient.get();
        }
        return null;
    }
}
