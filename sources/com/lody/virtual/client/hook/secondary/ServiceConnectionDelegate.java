package com.lody.virtual.client.hook.secondary;

import android.app.IServiceConnection;
import android.app.IServiceConnection.Stub;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.collection.ArrayMap;
import com.lody.virtual.server.IBinderDelegateService;
import mirror.android.app.ActivityThread;
import mirror.android.app.ContextImpl;
import mirror.android.app.IServiceConnectionO;
import mirror.android.app.LoadedApk;

public class ServiceConnectionDelegate extends Stub {
    private static final ArrayMap<IBinder, ServiceConnectionDelegate> DELEGATE_MAP = new ArrayMap<>();
    private IServiceConnection mConn;

    private ServiceConnectionDelegate(IServiceConnection iServiceConnection) {
        this.mConn = iServiceConnection;
    }

    public static IServiceConnection getDelegate(Context context, ServiceConnection serviceConnection, int i) {
        IServiceConnection iServiceConnection;
        if (serviceConnection != null) {
            try {
                Object call = ActivityThread.currentActivityThread.call(new Object[0]);
                Object obj = ContextImpl.mPackageInfo.get(VirtualCore.get().getContext());
                Handler handler = (Handler) ActivityThread.getHandler.call(call, new Object[0]);
                iServiceConnection = (IServiceConnection) LoadedApk.getServiceDispatcher.call(obj, serviceConnection, context, handler, Integer.valueOf(i));
            } catch (Exception e) {
                Log.e("ConnectionDelegate", "getServiceDispatcher", e);
                iServiceConnection = null;
            }
            if (iServiceConnection != null) {
                return getDelegate(iServiceConnection);
            }
            throw new RuntimeException("Not supported in system context");
        }
        throw new IllegalArgumentException("connection is null");
    }

    public static IServiceConnection removeDelegate(Context context, ServiceConnection serviceConnection) {
        IServiceConnection iServiceConnection;
        try {
            Object obj = ContextImpl.mPackageInfo.get(VirtualCore.get().getContext());
            iServiceConnection = (IServiceConnection) LoadedApk.forgetServiceDispatcher.call(obj, context, serviceConnection);
        } catch (Exception e) {
            Log.e("ConnectionDelegate", "forgetServiceDispatcher", e);
            iServiceConnection = null;
        }
        if (iServiceConnection == null) {
            return null;
        }
        return removeDelegate(iServiceConnection);
    }

    public static ServiceConnectionDelegate getDelegate(IServiceConnection iServiceConnection) {
        if (iServiceConnection instanceof ServiceConnectionDelegate) {
            return (ServiceConnectionDelegate) iServiceConnection;
        }
        IBinder asBinder = iServiceConnection.asBinder();
        ServiceConnectionDelegate serviceConnectionDelegate = (ServiceConnectionDelegate) DELEGATE_MAP.get(asBinder);
        if (serviceConnectionDelegate == null) {
            serviceConnectionDelegate = new ServiceConnectionDelegate(iServiceConnection);
            DELEGATE_MAP.put(asBinder, serviceConnectionDelegate);
        }
        return serviceConnectionDelegate;
    }

    public static ServiceConnectionDelegate removeDelegate(IServiceConnection iServiceConnection) {
        return (ServiceConnectionDelegate) DELEGATE_MAP.remove(iServiceConnection.asBinder());
    }

    public void connected(ComponentName componentName, IBinder iBinder) throws RemoteException {
        connected(componentName, iBinder, false);
    }

    public void connected(ComponentName componentName, IBinder iBinder, boolean z) throws RemoteException {
        IBinderDelegateService asInterface = IBinderDelegateService.Stub.asInterface(iBinder);
        if (asInterface != null) {
            componentName = asInterface.getComponent();
            iBinder = asInterface.getService();
            IBinder proxyService = ProxyServiceFactory.getProxyService(VClientImpl.get().getCurrentApplication(), componentName, iBinder);
            if (proxyService != null) {
                iBinder = proxyService;
            }
        }
        if (VERSION.SDK_INT >= 26) {
            IServiceConnectionO.connected.call(this.mConn, componentName, iBinder, Boolean.valueOf(z));
            return;
        }
        this.mConn.connected(componentName, iBinder);
    }
}
