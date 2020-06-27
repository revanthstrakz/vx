package com.lody.virtual.server.p008am;

import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.SpecialComponentList;
import com.lody.virtual.helper.collection.ArrayMap;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.remote.PendingResultData;
import com.lody.virtual.server.p009pm.PackageSetting;
import com.lody.virtual.server.p009pm.VAppManagerService;
import com.lody.virtual.server.p009pm.parser.VPackage;
import com.lody.virtual.server.p009pm.parser.VPackage.ActivityComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ActivityIntentInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mirror.android.app.ContextImpl;
import mirror.android.app.LoadedApkHuaWei;
import mirror.android.rms.resource.ReceiverResourceLP;
import mirror.android.rms.resource.ReceiverResourceM;
import mirror.android.rms.resource.ReceiverResourceN;

/* renamed from: com.lody.virtual.server.am.BroadcastSystem */
public class BroadcastSystem {
    private static final int BROADCAST_TIME_OUT = 8500;
    /* access modifiers changed from: private */
    public static final String TAG = "BroadcastSystem";
    private static BroadcastSystem gDefault;
    /* access modifiers changed from: private */
    public final VActivityManagerService mAMS;
    /* access modifiers changed from: private */
    public final VAppManagerService mApp;
    /* access modifiers changed from: private */
    public final Map<IBinder, BroadcastRecord> mBroadcastRecords = new HashMap();
    private final Context mContext;
    private final ArrayMap<String, List<BroadcastReceiver>> mReceivers = new ArrayMap<>();
    private final StaticScheduler mScheduler;
    private final TimeoutHandler mTimeoutHandler;

    /* renamed from: com.lody.virtual.server.am.BroadcastSystem$BroadcastRecord */
    private static final class BroadcastRecord {
        PendingResultData pendingResult;
        ActivityInfo receiverInfo;
        int vuid;

        BroadcastRecord(int i, ActivityInfo activityInfo, PendingResultData pendingResultData) {
            this.vuid = i;
            this.receiverInfo = activityInfo;
            this.pendingResult = pendingResultData;
        }
    }

    /* renamed from: com.lody.virtual.server.am.BroadcastSystem$StaticBroadcastReceiver */
    private final class StaticBroadcastReceiver extends BroadcastReceiver {
        private int appId;
        private IntentFilter filter;
        private ActivityInfo info;

        private StaticBroadcastReceiver(int i, ActivityInfo activityInfo, IntentFilter intentFilter) {
            this.appId = i;
            this.info = activityInfo;
            this.filter = intentFilter;
        }

        public void onReceive(Context context, Intent intent) {
            if (!BroadcastSystem.this.mApp.isBooting() && (intent.getFlags() & 1073741824) == 0 && !isInitialStickyBroadcast()) {
                String stringExtra = intent.getStringExtra("_VA_|_privilege_pkg_");
                if (stringExtra == null || this.info.packageName.equals(stringExtra)) {
                    PendingResult goAsync = goAsync();
                    if (!BroadcastSystem.this.mAMS.handleStaticBroadcast(this.appId, this.info, intent, new PendingResultData(goAsync))) {
                        goAsync.finish();
                    }
                }
            }
        }
    }

    /* renamed from: com.lody.virtual.server.am.BroadcastSystem$StaticScheduler */
    private static final class StaticScheduler extends Handler {
        private StaticScheduler() {
        }
    }

    /* renamed from: com.lody.virtual.server.am.BroadcastSystem$TimeoutHandler */
    private final class TimeoutHandler extends Handler {
        private TimeoutHandler() {
        }

        public void handleMessage(Message message) {
            BroadcastRecord broadcastRecord = (BroadcastRecord) BroadcastSystem.this.mBroadcastRecords.remove((IBinder) message.obj);
            if (broadcastRecord != null) {
                VLog.m91w(BroadcastSystem.TAG, "Broadcast timeout, cancel to dispatch it.", new Object[0]);
                broadcastRecord.pendingResult.finish();
            }
        }
    }

    private BroadcastSystem(Context context, VActivityManagerService vActivityManagerService, VAppManagerService vAppManagerService) {
        this.mContext = context;
        this.mApp = vAppManagerService;
        this.mAMS = vActivityManagerService;
        this.mScheduler = new StaticScheduler();
        this.mTimeoutHandler = new TimeoutHandler();
        fuckHuaWeiVerifier();
    }

    public static void attach(VActivityManagerService vActivityManagerService, VAppManagerService vAppManagerService) {
        if (gDefault == null) {
            gDefault = new BroadcastSystem(VirtualCore.get().getContext(), vActivityManagerService, vAppManagerService);
            return;
        }
        throw new IllegalStateException();
    }

    public static BroadcastSystem get() {
        return gDefault;
    }

    private void fuckHuaWeiVerifier() {
        if (LoadedApkHuaWei.mReceiverResource != null) {
            Object obj = ContextImpl.mPackageInfo.get(this.mContext);
            if (obj != null) {
                Object obj2 = LoadedApkHuaWei.mReceiverResource.get(obj);
                if (obj2 == null) {
                    return;
                }
                if (VERSION.SDK_INT >= 24) {
                    if (ReceiverResourceN.mWhiteList != null) {
                        List list = (List) ReceiverResourceN.mWhiteList.get(obj2);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(this.mContext.getPackageName());
                        if (list != null) {
                            arrayList.addAll(list);
                        }
                        ReceiverResourceN.mWhiteList.set(obj2, arrayList);
                    }
                } else if (ReceiverResourceM.mWhiteList != null) {
                    String[] strArr = (String[]) ReceiverResourceM.mWhiteList.get(obj2);
                    LinkedList linkedList = new LinkedList();
                    Collections.addAll(linkedList, strArr);
                    linkedList.add(this.mContext.getPackageName());
                    ReceiverResourceM.mWhiteList.set(obj2, linkedList.toArray(new String[linkedList.size()]));
                } else if (ReceiverResourceLP.mResourceConfig != null) {
                    ReceiverResourceLP.mResourceConfig.set(obj2, null);
                }
            }
        }
    }

    public void startApp(VPackage vPackage) {
        PackageSetting packageSetting = (PackageSetting) vPackage.mExtras;
        Iterator it = vPackage.receivers.iterator();
        while (it.hasNext()) {
            ActivityComponent activityComponent = (ActivityComponent) it.next();
            ActivityInfo activityInfo = activityComponent.info;
            List list = (List) this.mReceivers.get(vPackage.packageName);
            if (list == null) {
                list = new ArrayList();
                this.mReceivers.put(vPackage.packageName, list);
            }
            List list2 = list;
            IntentFilter intentFilter = new IntentFilter(String.format("_VA_%s_%s", new Object[]{activityInfo.packageName, activityInfo.name}));
            StaticBroadcastReceiver staticBroadcastReceiver = new StaticBroadcastReceiver(packageSetting.appId, activityInfo, intentFilter);
            this.mContext.registerReceiver(staticBroadcastReceiver, intentFilter, null, this.mScheduler);
            list2.add(staticBroadcastReceiver);
            Iterator it2 = activityComponent.intents.iterator();
            while (it2.hasNext()) {
                IntentFilter intentFilter2 = new IntentFilter(((ActivityIntentInfo) it2.next()).filter);
                SpecialComponentList.protectIntentFilter(intentFilter2);
                StaticBroadcastReceiver staticBroadcastReceiver2 = new StaticBroadcastReceiver(packageSetting.appId, activityInfo, intentFilter2);
                this.mContext.registerReceiver(staticBroadcastReceiver2, intentFilter2, null, this.mScheduler);
                list2.add(staticBroadcastReceiver2);
            }
        }
    }

    public void stopApp(String str) {
        synchronized (this.mBroadcastRecords) {
            Iterator it = this.mBroadcastRecords.entrySet().iterator();
            while (it.hasNext()) {
                BroadcastRecord broadcastRecord = (BroadcastRecord) ((Entry) it.next()).getValue();
                if (broadcastRecord.receiverInfo.packageName.equals(str)) {
                    broadcastRecord.pendingResult.finish();
                    it.remove();
                }
            }
        }
        synchronized (this.mReceivers) {
            List<BroadcastReceiver> list = (List) this.mReceivers.get(str);
            if (list != null) {
                for (BroadcastReceiver unregisterReceiver : list) {
                    this.mContext.unregisterReceiver(unregisterReceiver);
                }
            }
            this.mReceivers.remove(str);
        }
    }

    /* access modifiers changed from: 0000 */
    public void broadcastFinish(PendingResultData pendingResultData) {
        synchronized (this.mBroadcastRecords) {
            if (((BroadcastRecord) this.mBroadcastRecords.remove(pendingResultData.mToken)) == null) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Unable to find the BroadcastRecord by token: ");
                sb.append(pendingResultData.mToken);
                VLog.m87e(str, sb.toString(), new Object[0]);
            }
        }
        this.mTimeoutHandler.removeMessages(0, pendingResultData.mToken);
        pendingResultData.finish();
    }

    /* access modifiers changed from: 0000 */
    public void broadcastSent(int i, ActivityInfo activityInfo, PendingResultData pendingResultData) {
        BroadcastRecord broadcastRecord = new BroadcastRecord(i, activityInfo, pendingResultData);
        synchronized (this.mBroadcastRecords) {
            this.mBroadcastRecords.put(pendingResultData.mToken, broadcastRecord);
        }
        Message message = new Message();
        message.obj = pendingResultData.mToken;
        this.mTimeoutHandler.sendMessageDelayed(message, 8500);
    }
}
