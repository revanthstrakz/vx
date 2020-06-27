package com.lody.virtual.server.p010vs;

import android.os.RemoteException;
import android.util.SparseArray;
import com.lody.virtual.server.IVirtualStorageService.Stub;
import com.lody.virtual.server.p009pm.VUserManagerService;
import java.util.HashMap;

/* renamed from: com.lody.virtual.server.vs.VirtualStorageService */
public class VirtualStorageService extends Stub {
    private static final VirtualStorageService sService = new VirtualStorageService();
    private final SparseArray<HashMap<String, VSConfig>> mConfigs = new SparseArray<>();
    private final VSPersistenceLayer mLayer = new VSPersistenceLayer(this);

    public static VirtualStorageService get() {
        return sService;
    }

    private VirtualStorageService() {
        this.mLayer.read();
    }

    /* access modifiers changed from: 0000 */
    public SparseArray<HashMap<String, VSConfig>> getConfigs() {
        return this.mConfigs;
    }

    public void setVirtualStorage(String str, int i, String str2) throws RemoteException {
        checkUserId(i);
        synchronized (this.mConfigs) {
            getOrCreateVSConfigLocked(str, i).vsPath = str2;
            this.mLayer.save();
        }
    }

    private VSConfig getOrCreateVSConfigLocked(String str, int i) {
        HashMap hashMap = (HashMap) this.mConfigs.get(i);
        if (hashMap == null) {
            hashMap = new HashMap();
            this.mConfigs.put(i, hashMap);
        }
        VSConfig vSConfig = (VSConfig) hashMap.get(str);
        if (vSConfig != null) {
            return vSConfig;
        }
        VSConfig vSConfig2 = new VSConfig();
        vSConfig2.enable = false;
        hashMap.put(str, vSConfig2);
        return vSConfig2;
    }

    public String getVirtualStorage(String str, int i) throws RemoteException {
        String str2;
        checkUserId(i);
        synchronized (this.mConfigs) {
            str2 = getOrCreateVSConfigLocked(str, i).vsPath;
        }
        return str2;
    }

    public void setVirtualStorageState(String str, int i, boolean z) throws RemoteException {
        checkUserId(i);
        synchronized (this.mConfigs) {
            getOrCreateVSConfigLocked(str, i).enable = z;
            this.mLayer.save();
        }
    }

    public boolean isVirtualStorageEnable(String str, int i) throws RemoteException {
        boolean z;
        checkUserId(i);
        synchronized (this.mConfigs) {
            z = getOrCreateVSConfigLocked(str, i).enable;
        }
        return z;
    }

    private void checkUserId(int i) {
        if (!VUserManagerService.get().exists(i)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid userId ");
            sb.append(i);
            throw new IllegalStateException(sb.toString());
        }
    }
}
