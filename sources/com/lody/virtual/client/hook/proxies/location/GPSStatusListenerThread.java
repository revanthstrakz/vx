package com.lody.virtual.client.hook.proxies.location;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class GPSStatusListenerThread extends TimerTask {
    private static GPSStatusListenerThread INSTANCE = new GPSStatusListenerThread();
    private boolean isRunning = false;
    private Map<Object, Long> listeners = new HashMap();
    private Timer timer = new Timer();

    public void addListenerTransport(Object obj) {
        if (!this.isRunning) {
            synchronized (this) {
                if (!this.isRunning) {
                    this.isRunning = true;
                    this.timer.schedule(this, 100, 800);
                }
            }
        }
        this.listeners.put(obj, Long.valueOf(System.currentTimeMillis()));
    }

    public void removeListenerTransport(Object obj) {
        if (obj != null) {
            this.listeners.remove(obj);
        }
    }

    public void run() {
        if (!this.listeners.isEmpty()) {
            for (Entry key : this.listeners.entrySet()) {
                try {
                    Object key2 = key.getKey();
                    MockLocationHelper.invokeSvStatusChanged(key2);
                    MockLocationHelper.invokeNmeaReceived(key2);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        this.timer.cancel();
    }

    public static GPSStatusListenerThread get() {
        return INSTANCE;
    }

    private GPSStatusListenerThread() {
    }
}
