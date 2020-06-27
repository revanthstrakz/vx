package com.lody.virtual.client.hook.secondary;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.FileDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

abstract class StubBinder implements IBinder {
    private IBinder mBase;
    private ClassLoader mClassLoader;
    private IInterface mInterface;

    public abstract InvocationHandler createHandler(Class<?> cls, IInterface iInterface);

    StubBinder(ClassLoader classLoader, IBinder iBinder) {
        this.mClassLoader = classLoader;
        this.mBase = iBinder;
    }

    public String getInterfaceDescriptor() throws RemoteException {
        return this.mBase.getInterfaceDescriptor();
    }

    public boolean pingBinder() {
        return this.mBase.pingBinder();
    }

    public boolean isBinderAlive() {
        return this.mBase.isBinderAlive();
    }

    public IInterface queryLocalInterface(String str) {
        if (this.mInterface == null) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace == null || stackTrace.length <= 1) {
                return null;
            }
            Class cls = null;
            IInterface iInterface = null;
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (!stackTraceElement.isNativeMethod()) {
                    try {
                        Method declaredMethod = this.mClassLoader.loadClass(stackTraceElement.getClassName()).getDeclaredMethod(stackTraceElement.getMethodName(), new Class[]{IBinder.class});
                        if ((declaredMethod.getModifiers() & 8) != 0) {
                            declaredMethod.setAccessible(true);
                            Class returnType = declaredMethod.getReturnType();
                            if (returnType.isInterface() && IInterface.class.isAssignableFrom(returnType)) {
                                try {
                                    iInterface = (IInterface) declaredMethod.invoke(null, new Object[]{this.mBase});
                                } catch (Exception unused) {
                                }
                                cls = returnType;
                            }
                        }
                    } catch (Exception unused2) {
                    }
                }
            }
            if (cls == null || iInterface == null) {
                return null;
            }
            this.mInterface = (IInterface) Proxy.newProxyInstance(this.mClassLoader, new Class[]{cls}, createHandler(cls, iInterface));
        }
        return this.mInterface;
    }

    public void dump(FileDescriptor fileDescriptor, String[] strArr) throws RemoteException {
        this.mBase.dump(fileDescriptor, strArr);
    }

    public void dumpAsync(FileDescriptor fileDescriptor, String[] strArr) throws RemoteException {
        this.mBase.dumpAsync(fileDescriptor, strArr);
    }

    public boolean transact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        return this.mBase.transact(i, parcel, parcel2, i2);
    }

    public void linkToDeath(DeathRecipient deathRecipient, int i) throws RemoteException {
        this.mBase.linkToDeath(deathRecipient, i);
    }

    public boolean unlinkToDeath(DeathRecipient deathRecipient, int i) {
        return this.mBase.unlinkToDeath(deathRecipient, i);
    }
}
