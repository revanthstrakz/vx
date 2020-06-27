package com.lody.virtual.client.hook.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import com.lody.virtual.client.core.VirtualCore;
import java.io.FileDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import mirror.RefStaticMethod;
import mirror.android.p017os.ServiceManager;

public class BinderInvocationStub extends MethodInvocationStub<IInterface> implements IBinder {
    private static final String TAG = "BinderInvocationStub";
    private IBinder mBaseBinder;

    private final class AsBinder extends MethodProxy {
        public String getMethodName() {
            return "asBinder";
        }

        private AsBinder() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return BinderInvocationStub.this;
        }
    }

    public BinderInvocationStub(RefStaticMethod<IInterface> refStaticMethod, IBinder iBinder) {
        this(asInterface(refStaticMethod, iBinder));
    }

    public BinderInvocationStub(Class<?> cls, IBinder iBinder) {
        this(asInterface(cls, iBinder));
    }

    public BinderInvocationStub(IInterface iInterface) {
        super(iInterface);
        this.mBaseBinder = getBaseInterface() != null ? ((IInterface) getBaseInterface()).asBinder() : null;
        addMethodProxy(new AsBinder());
    }

    private static IInterface asInterface(RefStaticMethod<IInterface> refStaticMethod, IBinder iBinder) {
        if (refStaticMethod == null || iBinder == null) {
            return null;
        }
        return (IInterface) refStaticMethod.call(iBinder);
    }

    private static IInterface asInterface(Class<?> cls, IBinder iBinder) {
        if (cls == null || iBinder == null) {
            return null;
        }
        try {
            return (IInterface) cls.getMethod("asInterface", new Class[]{IBinder.class}).invoke(null, new Object[]{iBinder});
        } catch (Exception e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not create stub ");
            sb.append(cls.getName());
            sb.append(". Cause: ");
            sb.append(e);
            Log.d(str, sb.toString());
            return null;
        }
    }

    public void replaceService(String str) {
        if (this.mBaseBinder != null) {
            ((Map) ServiceManager.sCache.get()).put(str, this);
        }
    }

    public String getInterfaceDescriptor() throws RemoteException {
        return this.mBaseBinder.getInterfaceDescriptor();
    }

    public Context getContext() {
        return VirtualCore.get().getContext();
    }

    public boolean pingBinder() {
        return this.mBaseBinder.pingBinder();
    }

    public boolean isBinderAlive() {
        return this.mBaseBinder.isBinderAlive();
    }

    public IInterface queryLocalInterface(String str) {
        return (IInterface) getProxyInterface();
    }

    public void dump(FileDescriptor fileDescriptor, String[] strArr) throws RemoteException {
        this.mBaseBinder.dump(fileDescriptor, strArr);
    }

    @TargetApi(13)
    public void dumpAsync(FileDescriptor fileDescriptor, String[] strArr) throws RemoteException {
        this.mBaseBinder.dumpAsync(fileDescriptor, strArr);
    }

    public boolean transact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        return this.mBaseBinder.transact(i, parcel, parcel2, i2);
    }

    public void linkToDeath(DeathRecipient deathRecipient, int i) throws RemoteException {
        this.mBaseBinder.linkToDeath(deathRecipient, i);
    }

    public boolean unlinkToDeath(DeathRecipient deathRecipient, int i) {
        return this.mBaseBinder.unlinkToDeath(deathRecipient, i);
    }

    public IBinder getBaseBinder() {
        return this.mBaseBinder;
    }
}
