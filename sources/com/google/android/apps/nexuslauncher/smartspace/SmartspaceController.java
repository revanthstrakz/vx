package com.google.android.apps.nexuslauncher.smartspace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import com.android.launcher3.Alarm;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.OnAlarmListener;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0955i;
import com.google.android.apps.nexuslauncher.utils.ActionIntentFilter;
import com.google.android.apps.nexuslauncher.utils.ProtoStore;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;
import java.io.PrintWriter;
import java.util.List;

public class SmartspaceController implements Callback {

    /* renamed from: dU */
    private static SmartspaceController f125dU;

    /* renamed from: dQ */
    private final SmartspaceDataContainer f126dQ;

    /* renamed from: dR */
    private final Alarm f127dR;

    /* renamed from: dS */
    private ISmartspace f128dS;

    /* renamed from: dT */
    private final ProtoStore f129dT;
    private final Context mAppContext;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper(), this);
    private final Handler mWorker = new Handler(LauncherModel.getWorkerLooper(), this);

    enum Store {
        WEATHER("smartspace_weather"),
        CURRENT("smartspace_current");
        
        final String filename;

        private Store(String str) {
            this.filename = str;
        }
    }

    public SmartspaceController(Context context) {
        this.mAppContext = context;
        this.f126dQ = new SmartspaceDataContainer();
        this.f129dT = new ProtoStore(context);
        Alarm alarm = new Alarm();
        this.f127dR = alarm;
        alarm.setOnAlarmListener(new OnAlarmListener() {
            public void onAlarm(Alarm alarm) {
                SmartspaceController.this.m53dc();
            }
        });
        m54dd();
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                SmartspaceController.this.m54dd();
            }
        }, ActionIntentFilter.googleInstance("android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED", "android.intent.action.PACKAGE_DATA_CLEARED"));
    }

    /* renamed from: db */
    private Intent m52db() {
        return new Intent("com.google.android.apps.gsa.smartspace.SETTINGS").setPackage(LauncherClient.BRIDGE_PACKAGE).addFlags(268435456);
    }

    /* access modifiers changed from: private */
    /* renamed from: dc */
    public void m53dc() {
        boolean isWeatherAvailable = this.f126dQ.isWeatherAvailable();
        boolean cS = this.f126dQ.mo12989cS();
        this.f126dQ.mo12991cU();
        if (isWeatherAvailable && !this.f126dQ.isWeatherAvailable()) {
            m56df(null, Store.WEATHER);
        }
        if (cS && !this.f126dQ.mo12989cS()) {
            m56df(null, Store.CURRENT);
            this.mAppContext.sendBroadcast(new Intent("com.google.android.apps.gsa.smartspace.EXPIRE_EVENT").setPackage(LauncherClient.BRIDGE_PACKAGE).addFlags(268435456));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: dd */
    public void m54dd() {
        if (this.f128dS != null) {
            this.f128dS.mo12961cq();
        }
        m55de();
    }

    /* renamed from: de */
    private void m55de() {
        this.mAppContext.sendBroadcast(new Intent("com.google.android.apps.gsa.smartspace.ENABLE_UPDATE").setPackage(LauncherClient.BRIDGE_PACKAGE).addFlags(268435456));
    }

    /* renamed from: df */
    private void m56df(NewCardInfo newCardInfo, Store store) {
        Message.obtain(this.mWorker, 2, store.ordinal(), 0, newCardInfo).sendToTarget();
    }

    public static SmartspaceController get(Context context) {
        if (f125dU == null) {
            f125dU = new SmartspaceController(context.getApplicationContext());
        }
        return f125dU;
    }

    private void update() {
        this.f127dR.cancelAlarm();
        long cT = this.f126dQ.mo12990cT();
        if (cT > 0) {
            this.f127dR.setAlarm(cT);
        }
        if (this.f128dS != null) {
            this.f128dS.mo12962cr(this.f126dQ);
        }
    }

    /* renamed from: cV */
    public void mo12981cV(NewCardInfo newCardInfo) {
        if (newCardInfo == null || newCardInfo.f116dj) {
            m56df(newCardInfo, Store.CURRENT);
        } else {
            m56df(newCardInfo, Store.WEATHER);
        }
    }

    /* renamed from: cW */
    public void mo12982cW() {
        Message.obtain(this.mWorker, 1).sendToTarget();
    }

    /* renamed from: cX */
    public void mo12983cX(String str, PrintWriter printWriter) {
        printWriter.println();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("SmartspaceController");
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("  weather ");
        sb2.append(this.f126dQ.f130dO);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append("  current ");
        sb3.append(this.f126dQ.f131dP);
        printWriter.println(sb3.toString());
    }

    /* renamed from: cY */
    public boolean mo12984cY() {
        List queryBroadcastReceivers = this.mAppContext.getPackageManager().queryBroadcastReceivers(m52db(), 0);
        if (queryBroadcastReceivers != null) {
            return !queryBroadcastReceivers.isEmpty();
        }
        return false;
    }

    /* renamed from: cZ */
    public void mo12985cZ() {
        this.mAppContext.sendBroadcast(m52db());
    }

    /* renamed from: da */
    public void mo12986da(ISmartspace iSmartspace) {
        this.f128dS = iSmartspace;
    }

    public boolean handleMessage(Message message) {
        int i = message.what;
        SmartspaceCard smartspaceCard = null;
        if (i != 101) {
            switch (i) {
                case 1:
                    C0955i iVar = new C0955i();
                    SmartspaceCard cD = this.f129dT.mo13020dv(Store.WEATHER.filename, iVar) ? SmartspaceCard.m30cD(this.mAppContext, iVar, true) : null;
                    C0955i iVar2 = new C0955i();
                    if (this.f129dT.mo13020dv(Store.CURRENT.filename, iVar2)) {
                        smartspaceCard = SmartspaceCard.m30cD(this.mAppContext, iVar2, false);
                    }
                    Message.obtain(this.mUiHandler, 101, new SmartspaceCard[]{cD, smartspaceCard}).sendToTarget();
                    break;
                case 2:
                    this.f129dT.mo13021dw(SmartspaceCard.m40cQ(this.mAppContext, (NewCardInfo) message.obj), Store.values()[message.arg1].filename);
                    Message.obtain(this.mUiHandler, 1).sendToTarget();
                    break;
            }
        } else {
            SmartspaceCard[] smartspaceCardArr = (SmartspaceCard[]) message.obj;
            if (smartspaceCardArr != null) {
                this.f126dQ.f130dO = smartspaceCardArr.length > 0 ? smartspaceCardArr[0] : null;
                SmartspaceDataContainer smartspaceDataContainer = this.f126dQ;
                if (smartspaceCardArr.length > 1) {
                    smartspaceCard = smartspaceCardArr[1];
                }
                smartspaceDataContainer.f131dP = smartspaceCard;
            }
            this.f126dQ.mo12991cU();
            update();
        }
        return true;
    }
}
