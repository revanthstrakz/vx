package com.lody.virtual.client.hook.secondary;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProxyServiceFactory {
    private static final String TAG = "ProxyServiceFactory";
    private static Map<String, ServiceFetcher> sHookSecondaryServiceMap = new HashMap();

    private interface ServiceFetcher {
        IBinder getService(Context context, ClassLoader classLoader, IBinder iBinder);
    }

    static {
        sHookSecondaryServiceMap.put("com.google.android.auth.IAuthManagerService", new ServiceFetcher() {
            public IBinder getService(Context context, ClassLoader classLoader, IBinder iBinder) {
                return new StubBinder(classLoader, iBinder) {
                    public InvocationHandler createHandler(Class<?> cls, final IInterface iInterface) {
                        return new InvocationHandler() {
                            public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                                try {
                                    return method.invoke(iInterface, objArr);
                                } catch (InvocationTargetException e) {
                                    if (e.getCause() != null) {
                                        throw e.getCause();
                                    }
                                    throw e;
                                }
                            }
                        };
                    }
                };
            }
        });
        sHookSecondaryServiceMap.put("com.android.vending.billing.IInAppBillingService", new ServiceFetcher() {
            public IBinder getService(Context context, ClassLoader classLoader, IBinder iBinder) {
                return new StubBinder(classLoader, iBinder) {
                    public InvocationHandler createHandler(Class<?> cls, final IInterface iInterface) {
                        return new InvocationHandler() {
                            public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                                try {
                                    return method.invoke(iInterface, objArr);
                                } catch (InvocationTargetException e) {
                                    if (e.getCause() != null) {
                                        throw e.getCause();
                                    }
                                    throw e;
                                }
                            }
                        };
                    }
                };
            }
        });
        sHookSecondaryServiceMap.put("com.google.android.gms.common.internal.IGmsServiceBroker", new ServiceFetcher() {
            public IBinder getService(Context context, ClassLoader classLoader, IBinder iBinder) {
                return new StubBinder(classLoader, iBinder) {
                    public InvocationHandler createHandler(Class<?> cls, final IInterface iInterface) {
                        return new InvocationHandler() {
                            public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
                                try {
                                    return method.invoke(iInterface, objArr);
                                } catch (InvocationTargetException e) {
                                    if (e.getCause() != null) {
                                        throw e.getCause();
                                    }
                                    throw e;
                                }
                            }
                        };
                    }
                };
            }
        });
    }

    public static IBinder getProxyService(Context context, ComponentName componentName, IBinder iBinder) {
        if (context == null || iBinder == null) {
            return null;
        }
        try {
            ServiceFetcher serviceFetcher = (ServiceFetcher) sHookSecondaryServiceMap.get(iBinder.getInterfaceDescriptor());
            if (serviceFetcher != null) {
                IBinder service = serviceFetcher.getService(context, context.getClassLoader(), iBinder);
                if (service != null) {
                    return service;
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }
}
