package com.lody.virtual.client.ipc;

import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.remote.VDeviceInfo;
import com.lody.virtual.server.IDeviceInfoManager;
import com.lody.virtual.server.IDeviceInfoManager.Stub;

public class VDeviceManager {
    private static final VDeviceManager sInstance = new VDeviceManager();
    private IDeviceInfoManager mRemote;

    public static VDeviceManager get() {
        return sInstance;
    }

    public IDeviceInfoManager getRemote() {
        if (this.mRemote == null || (!this.mRemote.asBinder().pingBinder() && !VirtualCore.get().isVAppProcess())) {
            synchronized (this) {
                this.mRemote = (IDeviceInfoManager) LocalProxyUtils.genProxy(IDeviceInfoManager.class, getRemoteInterface());
            }
        }
        return this.mRemote;
    }

    private Object getRemoteInterface() {
        return Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.DEVICE));
    }

    public VDeviceInfo getDeviceInfo(int i) {
        try {
            return getRemote().getDeviceInfo(i);
        } catch (RemoteException e) {
            return (VDeviceInfo) VirtualRuntime.crash(e);
        }
    }
}
