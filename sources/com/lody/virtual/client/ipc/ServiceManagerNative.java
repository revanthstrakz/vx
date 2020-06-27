package com.lody.virtual.client.ipc;

import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ProviderCall.Builder;
import com.lody.virtual.helper.compat.BundleCompat;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.server.ServiceCache;
import com.lody.virtual.server.interfaces.IServiceFetcher;
import com.lody.virtual.server.interfaces.IServiceFetcher.Stub;

public class ServiceManagerNative {
    public static final String ACCOUNT = "account";
    public static final String ACTIVITY = "activity";
    public static final String APP = "app";
    public static final String DEVICE = "device";
    public static final String JOB = "job";
    public static final String NOTIFICATION = "notification";
    public static final String PACKAGE = "package";
    public static String SERVICE_CP_AUTH = "virtual.service.BinderProvider";
    public static final String SERVICE_DEF_AUTH = "virtual.service.BinderProvider";
    private static final String TAG = "ServiceManagerNative";
    public static final String USER = "user";
    public static final String VIRTUAL_LOC = "virtual-loc";

    /* renamed from: VS */
    public static final String f178VS = "vs";
    private static IServiceFetcher sFetcher;

    private static IServiceFetcher getServiceFetcher() {
        if (sFetcher == null || !sFetcher.asBinder().isBinderAlive()) {
            synchronized (ServiceManagerNative.class) {
                Bundle call = new Builder(VirtualCore.get().getContext(), SERVICE_CP_AUTH).methodName("@").call();
                if (call != null) {
                    IBinder binder = BundleCompat.getBinder(call, "_VA_|_binder_");
                    linkBinderDied(binder);
                    sFetcher = Stub.asInterface(binder);
                }
            }
        }
        return sFetcher;
    }

    public static void ensureServerStarted() {
        new Builder(VirtualCore.get().getContext(), SERVICE_CP_AUTH).methodName("ensure_created").call();
    }

    public static void clearServerFetcher() {
        sFetcher = null;
    }

    private static void linkBinderDied(final IBinder iBinder) {
        try {
            iBinder.linkToDeath(new DeathRecipient() {
                public void binderDied() {
                    iBinder.unlinkToDeath(this, 0);
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static IBinder getService(String str) {
        if (VirtualCore.get().isServerProcess()) {
            return ServiceCache.getService(str);
        }
        IServiceFetcher serviceFetcher = getServiceFetcher();
        if (serviceFetcher != null) {
            try {
                return serviceFetcher.getService(str);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        VLog.m87e(TAG, "GetService(%s) return null.", str);
        return null;
    }

    public static void addService(String str, IBinder iBinder) {
        IServiceFetcher serviceFetcher = getServiceFetcher();
        if (serviceFetcher != null) {
            try {
                serviceFetcher.addService(str, iBinder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeService(String str) {
        IServiceFetcher serviceFetcher = getServiceFetcher();
        if (serviceFetcher != null) {
            try {
                serviceFetcher.removeService(str);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
