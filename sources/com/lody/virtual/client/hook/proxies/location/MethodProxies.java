package com.lody.virtual.client.hook.proxies.location;

import android.location.LocationRequest;
import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import com.lody.virtual.client.ipc.VirtualLocationManager;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.remote.vloc.VLocation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import mirror.android.location.LocationManager.GnssStatusListenerTransport;
import mirror.android.location.LocationManager.GpsStatusListenerTransport;
import mirror.android.location.LocationManager.ListenerTransport;
import mirror.android.location.LocationRequestL;

public class MethodProxies {

    static class AddGpsStatusListener extends ReplaceLastPkgMethodProxy {
        public AddGpsStatusListener() {
            super("addGpsStatusListener");
        }

        public AddGpsStatusListener(String str) {
            super(str);
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (!isFakeLocationEnable()) {
                return super.call(obj, method, objArr);
            }
            Object first = ArrayUtils.getFirst(objArr, GpsStatusListenerTransport.TYPE);
            Object obj2 = GpsStatusListenerTransport.this$0.get(first);
            GpsStatusListenerTransport.onGpsStarted.call(first, new Object[0]);
            GpsStatusListenerTransport.onFirstFix.call(first, Integer.valueOf(0));
            if (GpsStatusListenerTransport.mListener.get(first) != null) {
                MockLocationHelper.invokeSvStatusChanged(first);
            } else {
                MockLocationHelper.invokeNmeaReceived(first);
            }
            GPSListenerThread.get().addListenerTransport(obj2);
            return Boolean.valueOf(true);
        }
    }

    static class GetBestProvider extends MethodProxy {
        public String getMethodName() {
            return "getBestProvider";
        }

        GetBestProvider() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                return "gps";
            }
            return super.call(obj, method, objArr);
        }
    }

    static class GetLastKnownLocation extends GetLastLocation {
        public String getMethodName() {
            return "getLastKnownLocation";
        }

        GetLastKnownLocation() {
        }
    }

    static class GetLastLocation extends ReplaceLastPkgMethodProxy {
        public GetLastLocation() {
            super("getLastLocation");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (!(objArr[0] instanceof String)) {
                MethodProxies.fixLocationRequest(objArr[0]);
            }
            if (!isFakeLocationEnable()) {
                return super.call(obj, method, objArr);
            }
            VLocation location = VirtualLocationManager.get().getLocation();
            if (location != null) {
                return location.toSysLocation();
            }
            return null;
        }
    }

    static class IsProviderEnabled extends MethodProxy {
        public String getMethodName() {
            return "isProviderEnabled";
        }

        IsProviderEnabled() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (!isFakeLocationEnable()) {
                return super.call(obj, method, objArr);
            }
            String str = objArr[0];
            if ("passive".equals(str)) {
                return Boolean.valueOf(true);
            }
            if ("gps".equals(str)) {
                return Boolean.valueOf(true);
            }
            if ("network".equals(str)) {
                return Boolean.valueOf(true);
            }
            return Boolean.valueOf(false);
        }
    }

    static class RegisterGnssStatusCallback extends MethodProxy {
        public String getMethodName() {
            return "registerGnssStatusCallback";
        }

        RegisterGnssStatusCallback() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (!isFakeLocationEnable()) {
                return super.call(obj, method, objArr);
            }
            Object first = ArrayUtils.getFirst(objArr, GnssStatusListenerTransport.TYPE);
            if (first != null) {
                GnssStatusListenerTransport.onGnssStarted.call(first, new Object[0]);
                if (GnssStatusListenerTransport.mGpsListener.get(first) != null) {
                    MockLocationHelper.invokeSvStatusChanged(first);
                } else {
                    MockLocationHelper.invokeNmeaReceived(first);
                }
                GnssStatusListenerTransport.onFirstFix.call(first, Integer.valueOf(0));
                GPSListenerThread.get().addListenerTransport(GnssStatusListenerTransport.this$0.get(first));
            }
            return Boolean.valueOf(true);
        }
    }

    static class RemoveGpsStatusListener extends ReplaceLastPkgMethodProxy {
        public RemoveGpsStatusListener() {
            super("removeGpsStatusListener");
        }

        public RemoveGpsStatusListener(String str) {
            super(str);
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                return Integer.valueOf(0);
            }
            return super.call(obj, method, objArr);
        }
    }

    static class RemoveUpdates extends ReplaceLastPkgMethodProxy {
        public RemoveUpdates() {
            super("removeUpdates");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                return Integer.valueOf(0);
            }
            return super.call(obj, method, objArr);
        }
    }

    static class RequestLocationUpdates extends ReplaceLastPkgMethodProxy {
        public RequestLocationUpdates() {
            super("requestLocationUpdates");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (VERSION.SDK_INT > 16) {
                MethodProxies.fixLocationRequest(objArr[0]);
            }
            if (!isFakeLocationEnable()) {
                return super.call(obj, method, objArr);
            }
            Object first = ArrayUtils.getFirst(objArr, ListenerTransport.TYPE);
            if (first != null) {
                Object obj2 = ListenerTransport.this$0.get(first);
                MockLocationHelper.setGpsStatus(obj2);
                GPSListenerThread.get().addListenerTransport(obj2);
            }
            return Integer.valueOf(0);
        }
    }

    static class UnregisterGnssStatusCallback extends RemoveGpsStatusListener {
        public UnregisterGnssStatusCallback() {
            super("unregisterGnssStatusCallback");
        }
    }

    static class getAllProviders extends getProviders {
        public String getMethodName() {
            return "getAllProviders";
        }

        getAllProviders() {
        }
    }

    static class getProviderProperties extends MethodProxy {
        public String getMethodName() {
            return "getProviderProperties";
        }

        getProviderProperties() {
        }

        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            if (isFakeLocationEnable()) {
                return super.afterCall(obj, method, objArr, obj2);
            }
            try {
                Reflect.m80on(obj2).set("mRequiresNetwork", Boolean.valueOf(false));
                Reflect.m80on(obj2).set("mRequiresCell", Boolean.valueOf(false));
            } catch (Throwable th) {
                th.printStackTrace();
            }
            return obj2;
        }
    }

    static class getProviders extends MethodProxy {
        static List PROVIDERS = Arrays.asList(new String[]{"gps", "passive", "network"});

        public String getMethodName() {
            return "getProviders";
        }

        getProviders() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return PROVIDERS;
        }
    }

    static class locationCallbackFinished extends MethodProxy {
        public String getMethodName() {
            return "locationCallbackFinished";
        }

        locationCallbackFinished() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                return Boolean.valueOf(true);
            }
            return super.call(obj, method, objArr);
        }
    }

    static class sendExtraCommand extends MethodProxy {
        public String getMethodName() {
            return "sendExtraCommand";
        }

        sendExtraCommand() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                return Boolean.valueOf(true);
            }
            return super.call(obj, method, objArr);
        }
    }

    /* access modifiers changed from: private */
    public static void fixLocationRequest(LocationRequest locationRequest) {
        if (locationRequest != null) {
            if (LocationRequestL.mHideFromAppOps != null) {
                LocationRequestL.mHideFromAppOps.set(locationRequest, false);
            }
            if (LocationRequestL.mWorkSource != null) {
                LocationRequestL.mWorkSource.set(locationRequest, null);
            }
        }
    }
}
