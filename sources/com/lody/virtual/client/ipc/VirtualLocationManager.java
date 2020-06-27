package com.lody.virtual.client.ipc;

import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.remote.vloc.VCell;
import com.lody.virtual.remote.vloc.VLocation;
import com.lody.virtual.server.IVirtualLocationManager;
import com.lody.virtual.server.IVirtualLocationManager.Stub;
import java.util.List;

public class VirtualLocationManager {
    public static final int MODE_CLOSE = 0;
    public static final int MODE_USE_GLOBAL = 1;
    public static final int MODE_USE_SELF = 2;
    private static final VirtualLocationManager sInstance = new VirtualLocationManager();
    private IVirtualLocationManager mRemote;

    public static VirtualLocationManager get() {
        return sInstance;
    }

    public IVirtualLocationManager getRemote() {
        if (this.mRemote == null || (!this.mRemote.asBinder().pingBinder() && !VirtualCore.get().isVAppProcess())) {
            synchronized (this) {
                this.mRemote = (IVirtualLocationManager) LocalProxyUtils.genProxy(IVirtualLocationManager.class, getRemoteInterface());
            }
        }
        return this.mRemote;
    }

    private Object getRemoteInterface() {
        return Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.VIRTUAL_LOC));
    }

    public int getMode(int i, String str) {
        try {
            return getRemote().getMode(i, str);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public int getMode() {
        return getMode(MethodProxy.getAppUserId(), MethodProxy.getAppPkg());
    }

    public void setMode(int i, String str, int i2) {
        try {
            getRemote().setMode(i, str, i2);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void setCell(int i, String str, VCell vCell) {
        try {
            getRemote().setCell(i, str, vCell);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void setAllCell(int i, String str, List<VCell> list) {
        try {
            getRemote().setAllCell(i, str, list);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void setNeighboringCell(int i, String str, List<VCell> list) {
        try {
            getRemote().setNeighboringCell(i, str, list);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public VCell getCell(int i, String str) {
        try {
            return getRemote().getCell(i, str);
        } catch (RemoteException e) {
            return (VCell) VirtualRuntime.crash(e);
        }
    }

    public List<VCell> getAllCell(int i, String str) {
        try {
            return getRemote().getAllCell(i, str);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public List<VCell> getNeighboringCell(int i, String str) {
        try {
            return getRemote().getNeighboringCell(i, str);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public void setGlobalCell(VCell vCell) {
        try {
            getRemote().setGlobalCell(vCell);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void setGlobalAllCell(List<VCell> list) {
        try {
            getRemote().setGlobalAllCell(list);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void setGlobalNeighboringCell(List<VCell> list) {
        try {
            getRemote().setGlobalNeighboringCell(list);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void setLocation(int i, String str, VLocation vLocation) {
        try {
            getRemote().setLocation(i, str, vLocation);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public VLocation getLocation(int i, String str) {
        try {
            return getRemote().getLocation(i, str);
        } catch (RemoteException e) {
            return (VLocation) VirtualRuntime.crash(e);
        }
    }

    public VLocation getLocation() {
        return getLocation(MethodProxy.getAppUserId(), MethodProxy.getAppPkg());
    }

    public void setGlobalLocation(VLocation vLocation) {
        try {
            getRemote().setGlobalLocation(vLocation);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public VLocation getGlobalLocation() {
        try {
            return getRemote().getGlobalLocation();
        } catch (RemoteException e) {
            return (VLocation) VirtualRuntime.crash(e);
        }
    }
}
