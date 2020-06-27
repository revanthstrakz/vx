package com.microsoft.appcenter.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest.Builder;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import java.io.Closeable;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkStateHelper implements Closeable {
    @SuppressLint({"StaticFieldLeak"})
    private static NetworkStateHelper sSharedInstance;
    private final AtomicBoolean mConnected = new AtomicBoolean();
    private final ConnectivityManager mConnectivityManager;
    private ConnectivityReceiver mConnectivityReceiver;
    private final Context mContext;
    private final Set<Listener> mListeners = new CopyOnWriteArraySet();
    private NetworkCallback mNetworkCallback;

    private class ConnectivityReceiver extends BroadcastReceiver {
        private ConnectivityReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            NetworkStateHelper.this.handleNetworkStateUpdate();
        }
    }

    public interface Listener {
        void onNetworkStateUpdated(boolean z);
    }

    @VisibleForTesting
    public NetworkStateHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        reopen();
    }

    public static synchronized void unsetInstance() {
        synchronized (NetworkStateHelper.class) {
            sSharedInstance = null;
        }
    }

    public static synchronized NetworkStateHelper getSharedInstance(Context context) {
        NetworkStateHelper networkStateHelper;
        synchronized (NetworkStateHelper.class) {
            if (sSharedInstance == null) {
                sSharedInstance = new NetworkStateHelper(context);
            }
            networkStateHelper = sSharedInstance;
        }
        return networkStateHelper;
    }

    public void reopen() {
        try {
            if (VERSION.SDK_INT >= 21) {
                Builder builder = new Builder();
                builder.addCapability(12);
                this.mNetworkCallback = new NetworkCallback() {
                    public void onAvailable(Network network) {
                        NetworkStateHelper.this.onNetworkAvailable(network);
                    }

                    public void onLost(Network network) {
                        NetworkStateHelper.this.onNetworkLost(network);
                    }
                };
                this.mConnectivityManager.registerNetworkCallback(builder.build(), this.mNetworkCallback);
                return;
            }
            this.mConnectivityReceiver = new ConnectivityReceiver();
            this.mContext.registerReceiver(this.mConnectivityReceiver, getOldIntentFilter());
            handleNetworkStateUpdate();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Cannot access network state information.", e);
            this.mConnected.set(true);
        }
    }

    @NonNull
    private IntentFilter getOldIntentFilter() {
        return new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    }

    public boolean isNetworkConnected() {
        return this.mConnected.get() || isAnyNetworkConnected();
    }

    private boolean isAnyNetworkConnected() {
        if (VERSION.SDK_INT >= 21) {
            Network[] allNetworks = this.mConnectivityManager.getAllNetworks();
            if (allNetworks == null) {
                return false;
            }
            for (Network networkInfo : allNetworks) {
                NetworkInfo networkInfo2 = this.mConnectivityManager.getNetworkInfo(networkInfo);
                if (networkInfo2 != null && networkInfo2.isConnected()) {
                    return true;
                }
            }
        } else {
            NetworkInfo[] allNetworkInfo = this.mConnectivityManager.getAllNetworkInfo();
            if (allNetworkInfo == null) {
                return false;
            }
            for (NetworkInfo networkInfo3 : allNetworkInfo) {
                if (networkInfo3 != null && networkInfo3.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    @RequiresApi(api = 21)
    public void onNetworkAvailable(Network network) {
        StringBuilder sb = new StringBuilder();
        sb.append("Network ");
        sb.append(network);
        sb.append(" is available.");
        AppCenterLog.debug("AppCenter", sb.toString());
        if (this.mConnected.compareAndSet(false, true)) {
            notifyNetworkStateUpdated(true);
        }
    }

    /* access modifiers changed from: private */
    @RequiresApi(api = 21)
    public void onNetworkLost(Network network) {
        boolean z;
        StringBuilder sb = new StringBuilder();
        sb.append("Network ");
        sb.append(network);
        sb.append(" is lost.");
        AppCenterLog.debug("AppCenter", sb.toString());
        Network[] allNetworks = this.mConnectivityManager.getAllNetworks();
        if (!(allNetworks == null || allNetworks.length == 0)) {
            if (!Arrays.equals(allNetworks, new Network[]{network})) {
                z = false;
                if (z && this.mConnected.compareAndSet(true, false)) {
                    notifyNetworkStateUpdated(false);
                    return;
                }
            }
        }
        z = true;
        if (z) {
        }
    }

    /* access modifiers changed from: private */
    public void handleNetworkStateUpdate() {
        boolean isAnyNetworkConnected = isAnyNetworkConnected();
        if (this.mConnected.compareAndSet(!isAnyNetworkConnected, isAnyNetworkConnected)) {
            notifyNetworkStateUpdated(isAnyNetworkConnected);
        }
    }

    private void notifyNetworkStateUpdated(boolean z) {
        String str = "AppCenter";
        StringBuilder sb = new StringBuilder();
        sb.append("Network has been ");
        sb.append(z ? "connected." : "disconnected.");
        AppCenterLog.debug(str, sb.toString());
        for (Listener onNetworkStateUpdated : this.mListeners) {
            onNetworkStateUpdated.onNetworkStateUpdated(z);
        }
    }

    public void close() {
        this.mConnected.set(false);
        if (VERSION.SDK_INT >= 21) {
            this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
        } else {
            this.mContext.unregisterReceiver(this.mConnectivityReceiver);
        }
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.mListeners.remove(listener);
    }
}
