package com.lody.virtual.client.ipc;

import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.server.IVirtualStorageService;
import com.lody.virtual.server.IVirtualStorageService.Stub;

public class VirtualStorageManager {
    private static final VirtualStorageManager sInstance = new VirtualStorageManager();
    private IVirtualStorageService mRemote;

    public static VirtualStorageManager get() {
        return sInstance;
    }

    public IVirtualStorageService getRemote() {
        if (this.mRemote == null || (!this.mRemote.asBinder().pingBinder() && !VirtualCore.get().isVAppProcess())) {
            synchronized (this) {
                this.mRemote = (IVirtualStorageService) LocalProxyUtils.genProxy(IVirtualStorageService.class, getRemoteInterface());
            }
        }
        return this.mRemote;
    }

    private Object getRemoteInterface() {
        return Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.f178VS));
    }

    public void setVirtualStorage(String str, int i, String str2) {
        try {
            getRemote().setVirtualStorage(str, i, str2);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public String getVirtualStorage(String str, int i) {
        try {
            return getRemote().getVirtualStorage(str, i);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public void setVirtualStorageState(String str, int i, boolean z) {
        try {
            getRemote().setVirtualStorageState(str, i, z);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public boolean isVirtualStorageEnable(String str, int i) {
        try {
            return getRemote().isVirtualStorageEnable(str, i);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }
}
