package com.lody.virtual.client.hook.proxies.location;

import android.location.Location;
import android.os.Build.VERSION;
import android.os.Handler;
import com.lody.virtual.client.ipc.VirtualLocationManager;
import com.lody.virtual.remote.vloc.VLocation;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import mirror.android.location.LocationManager;
import mirror.android.location.LocationManager.ListenerTransport;

public class GPSListenerThread extends TimerTask {
    private static GPSListenerThread INSTANCE = new GPSListenerThread();
    private Handler handler = new Handler();
    private boolean isRunning = false;
    private HashMap<Object, Long> listeners = new HashMap<>();
    private Timer timer = new Timer();

    private void notifyGPSStatus(Map map) {
        if (map != null && !map.isEmpty()) {
            for (Entry value : map.entrySet()) {
                try {
                    Object value2 = value.getValue();
                    if (value2 != null) {
                        MockLocationHelper.invokeSvStatusChanged(value2);
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyLocation(Map map) {
        if (map != null) {
            try {
                if (!map.isEmpty()) {
                    VLocation location = VirtualLocationManager.get().getLocation();
                    if (location != null) {
                        Location sysLocation = location.toSysLocation();
                        for (Entry value : map.entrySet()) {
                            Object value2 = value.getValue();
                            if (value2 != null) {
                                ListenerTransport.onLocationChanged.call(value2, sysLocation);
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private void notifyMNmeaListener(Map map) {
        if (map != null && !map.isEmpty()) {
            for (Entry value : map.entrySet()) {
                try {
                    Object value2 = value.getValue();
                    if (value2 != null) {
                        MockLocationHelper.invokeNmeaReceived(value2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addListenerTransport(Object obj) {
        this.listeners.put(obj, Long.valueOf(System.currentTimeMillis()));
        if (!this.isRunning) {
            synchronized (this) {
                if (!this.isRunning) {
                    this.isRunning = true;
                    this.timer.schedule(this, 1000, 1000);
                }
            }
        }
    }

    public void removeListenerTransport(Object obj) {
        if (obj != null) {
            this.listeners.remove(obj);
        }
    }

    public void run() {
        Map map;
        if (!this.listeners.isEmpty()) {
            if (VirtualLocationManager.get().getMode() == 0) {
                this.listeners.clear();
                return;
            }
            for (Entry key : this.listeners.entrySet()) {
                try {
                    Object key2 = key.getKey();
                    if (VERSION.SDK_INT >= 24) {
                        Map map2 = (Map) LocationManager.mGnssNmeaListeners.get(key2);
                        notifyGPSStatus((Map) LocationManager.mGnssStatusListeners.get(key2));
                        notifyMNmeaListener(map2);
                        map = (Map) LocationManager.mGpsStatusListeners.get(key2);
                        notifyGPSStatus(map);
                        notifyMNmeaListener((Map) LocationManager.mGpsNmeaListeners.get(key2));
                    } else {
                        map = (Map) LocationManager.mGpsStatusListeners.get(key2);
                        notifyGPSStatus(map);
                        notifyMNmeaListener((Map) LocationManager.mNmeaListeners.get(key2));
                    }
                    final Map map3 = (Map) LocationManager.mListeners.get(key2);
                    if (map != null && !map.isEmpty()) {
                        if (map3 != null) {
                            if (!map3.isEmpty()) {
                                notifyLocation(map3);
                            }
                        }
                        this.handler.postDelayed(new Runnable() {
                            public void run() {
                                GPSListenerThread.this.notifyLocation(map3);
                            }
                        }, 100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        this.timer.cancel();
    }

    public static GPSListenerThread get() {
        return INSTANCE;
    }

    private GPSListenerThread() {
    }
}
