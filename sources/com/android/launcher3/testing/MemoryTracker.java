package com.android.launcher3.testing;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Debug.MemoryInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.LongSparseArray;
import com.android.launcher3.util.TestingUtils;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import p013io.virtualapp.utils.HanziToPinyin.Token;

public class MemoryTracker extends Service {
    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_UPDATE = 3;
    public static final String TAG = "MemoryTracker";
    private static final long UPDATE_RATE = 5000;
    ActivityManager mAm;
    private final IBinder mBinder = new MemoryTrackerInterface();
    public final LongSparseArray<ProcessMemInfo> mData = new LongSparseArray<>();
    Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    MemoryTracker.this.mHandler.removeMessages(3);
                    MemoryTracker.this.mHandler.sendEmptyMessage(3);
                    return;
                case 2:
                    MemoryTracker.this.mHandler.removeMessages(3);
                    return;
                case 3:
                    MemoryTracker.this.update();
                    MemoryTracker.this.mHandler.removeMessages(3);
                    MemoryTracker.this.mHandler.sendEmptyMessageDelayed(3, MemoryTracker.UPDATE_RATE);
                    return;
                default:
                    return;
            }
        }
    };
    private final Object mLock = new Object();
    public final ArrayList<Long> mPids = new ArrayList<>();
    private int[] mPidsArray = new int[0];

    public class MemoryTrackerInterface extends Binder {
        public MemoryTrackerInterface() {
        }

        /* access modifiers changed from: 0000 */
        public MemoryTracker getService() {
            return MemoryTracker.this;
        }
    }

    public static class ProcessMemInfo {
        public long currentPss;
        public long currentUss;
        public int head = 0;
        public long max = 1;
        public String name;
        public int pid;
        public long[] pss = new long[256];
        public long startTime;
        public long[] uss = new long[256];

        public ProcessMemInfo(int i, String str, long j) {
            this.pid = i;
            this.name = str;
            this.startTime = j;
        }

        public long getUptime() {
            return System.currentTimeMillis() - this.startTime;
        }
    }

    public ProcessMemInfo getMemInfo(int i) {
        return (ProcessMemInfo) this.mData.get((long) i);
    }

    public int[] getTrackedProcesses() {
        return this.mPidsArray;
    }

    public void startTrackingProcess(int i, String str, long j) {
        synchronized (this.mLock) {
            long j2 = (long) i;
            Long valueOf = Long.valueOf(j2);
            if (!this.mPids.contains(valueOf)) {
                this.mPids.add(valueOf);
                updatePidsArrayL();
                this.mData.put(j2, new ProcessMemInfo(i, str, j));
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updatePidsArrayL() {
        int size = this.mPids.size();
        this.mPidsArray = new int[size];
        StringBuffer stringBuffer = new StringBuffer("Now tracking processes: ");
        for (int i = 0; i < size; i++) {
            int intValue = ((Long) this.mPids.get(i)).intValue();
            this.mPidsArray[i] = intValue;
            stringBuffer.append(intValue);
            stringBuffer.append(Token.SEPARATOR);
        }
        Log.v(TAG, stringBuffer.toString());
    }

    /* access modifiers changed from: 0000 */
    public void update() {
        synchronized (this.mLock) {
            MemoryInfo[] processMemoryInfo = this.mAm.getProcessMemoryInfo(this.mPidsArray);
            int i = 0;
            while (true) {
                if (i >= processMemoryInfo.length) {
                    break;
                }
                MemoryInfo memoryInfo = processMemoryInfo[i];
                if (i > this.mPids.size()) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("update: unknown process info received: ");
                    sb.append(memoryInfo);
                    Log.e(str, sb.toString());
                    break;
                }
                long intValue = (long) ((Long) this.mPids.get(i)).intValue();
                ProcessMemInfo processMemInfo = (ProcessMemInfo) this.mData.get(intValue);
                processMemInfo.head = (processMemInfo.head + 1) % processMemInfo.pss.length;
                long[] jArr = processMemInfo.pss;
                int i2 = processMemInfo.head;
                long totalPss = (long) memoryInfo.getTotalPss();
                processMemInfo.currentPss = totalPss;
                jArr[i2] = totalPss;
                long[] jArr2 = processMemInfo.uss;
                int i3 = processMemInfo.head;
                long totalPrivateDirty = (long) memoryInfo.getTotalPrivateDirty();
                processMemInfo.currentUss = totalPrivateDirty;
                jArr2[i3] = totalPrivateDirty;
                if (processMemInfo.currentPss > processMemInfo.max) {
                    processMemInfo.max = processMemInfo.currentPss;
                }
                if (processMemInfo.currentUss > processMemInfo.max) {
                    processMemInfo.max = processMemInfo.currentUss;
                }
                if (processMemInfo.currentPss == 0) {
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("update: pid ");
                    sb2.append(intValue);
                    sb2.append(" has pss=0, it probably died");
                    Log.v(str2, sb2.toString());
                    this.mData.remove(intValue);
                }
                i++;
            }
            for (int size = this.mPids.size() - 1; size >= 0; size--) {
                if (this.mData.get((long) ((Long) this.mPids.get(size)).intValue()) == null) {
                    this.mPids.remove(size);
                    updatePidsArrayL();
                }
            }
        }
    }

    public void onCreate() {
        this.mAm = (ActivityManager) getSystemService(ServiceManagerNative.ACTIVITY);
        for (RunningServiceInfo runningServiceInfo : this.mAm.getRunningServices(256)) {
            if (runningServiceInfo.service.getPackageName().equals(getPackageName())) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("discovered running service: ");
                sb.append(runningServiceInfo.process);
                sb.append(" (");
                sb.append(runningServiceInfo.pid);
                sb.append(")");
                Log.v(str, sb.toString());
                startTrackingProcess(runningServiceInfo.pid, runningServiceInfo.process, System.currentTimeMillis() - (SystemClock.elapsedRealtime() - runningServiceInfo.activeSince));
            }
        }
        for (RunningAppProcessInfo runningAppProcessInfo : this.mAm.getRunningAppProcesses()) {
            String str2 = runningAppProcessInfo.processName;
            if (str2.startsWith(getPackageName())) {
                String str3 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("discovered other running process: ");
                sb2.append(str2);
                sb2.append(" (");
                sb2.append(runningAppProcessInfo.pid);
                sb2.append(")");
                Log.v(str3, sb2.toString());
                startTrackingProcess(runningAppProcessInfo.pid, str2, System.currentTimeMillis());
            }
        }
    }

    public void onDestroy() {
        this.mHandler.sendEmptyMessage(2);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Received start id ");
        sb.append(i2);
        sb.append(": ");
        sb.append(intent);
        Log.v(str, sb.toString());
        if (intent != null && TestingUtils.ACTION_START_TRACKING.equals(intent.getAction())) {
            startTrackingProcess(intent.getIntExtra("pid", -1), intent.getStringExtra(CommonProperties.NAME), intent.getLongExtra("start", System.currentTimeMillis()));
        }
        this.mHandler.sendEmptyMessage(1);
        return 1;
    }

    public IBinder onBind(Intent intent) {
        this.mHandler.sendEmptyMessage(1);
        return this.mBinder;
    }
}
