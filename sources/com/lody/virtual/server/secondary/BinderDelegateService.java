package com.lody.virtual.server.secondary;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import com.lody.virtual.server.IBinderDelegateService.Stub;
import java.util.HashMap;
import java.util.Map;

public class BinderDelegateService extends Stub {
    private static final Map<String, ProxyBinderFactory> mFactories = new HashMap();
    private ComponentName name;
    private IBinder service;

    private interface ProxyBinderFactory {
        IBinder create(Binder binder);
    }

    static {
        mFactories.put("android.accounts.IAccountAuthenticator", new ProxyBinderFactory() {
            public IBinder create(Binder binder) {
                return new FakeIdentityBinder(binder);
            }
        });
    }

    public BinderDelegateService(ComponentName componentName, IBinder iBinder) {
        this.name = componentName;
        if (iBinder instanceof Binder) {
            Binder binder = (Binder) iBinder;
            ProxyBinderFactory proxyBinderFactory = (ProxyBinderFactory) mFactories.get(binder.getInterfaceDescriptor());
            if (proxyBinderFactory != null) {
                iBinder = proxyBinderFactory.create(binder);
            }
        }
        this.service = iBinder;
    }

    public ComponentName getComponent() throws RemoteException {
        return this.name;
    }

    public IBinder getService() throws RemoteException {
        return this.service;
    }
}
