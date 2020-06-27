package com.lody.virtual.server.accounts;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.SyncRequest;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.Xml;
import com.lody.virtual.server.accounts.VSyncRecord.SyncExtras;
import com.lody.virtual.server.accounts.VSyncRecord.SyncRecordKey;
import com.lody.virtual.server.p009pm.VAppManagerService;
import com.lody.virtual.server.p009pm.VPackageManagerService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mirror.android.content.SyncAdapterTypeN;
import mirror.com.android.internal.R_Hide.styleable;

public class VContentService {
    private final Map<String, SyncAdapterInfo> mAppSyncAdapterInfos = new HashMap();
    private Context mContext;
    private final SparseArray<Map<SyncRecordKey, VSyncRecord>> mRecords = new SparseArray<>();

    private class SyncAdapterInfo {
        SyncAdapterType adapterType;
        ServiceInfo serviceInfo;

        SyncAdapterInfo(SyncAdapterType syncAdapterType, ServiceInfo serviceInfo2) {
            this.adapterType = syncAdapterType;
            this.serviceInfo = serviceInfo2;
        }
    }

    public void refreshServiceCache(String str) {
        Intent intent = new Intent("android.content.SyncAdapter");
        if (str != null) {
            intent.setPackage(str);
        }
        generateServicesMap(VPackageManagerService.get().queryIntentServices(intent, null, 128, 0), this.mAppSyncAdapterInfos, new RegisteredServicesParser());
    }

    public void syncAsUser(SyncRequest syncRequest, int i) {
        Account account = (Account) mirror.android.content.SyncRequest.mAccountToSync.get(syncRequest);
        String str = (String) mirror.android.content.SyncRequest.mAuthority.get(syncRequest);
        Bundle bundle = (Bundle) mirror.android.content.SyncRequest.mExtras.get(syncRequest);
        boolean z = mirror.android.content.SyncRequest.mIsPeriodic.get(syncRequest);
        long j = mirror.android.content.SyncRequest.mSyncRunTimeSecs.get(syncRequest);
        if (isAccountExist(i, account, str)) {
            SyncRecordKey syncRecordKey = new SyncRecordKey(account, str);
            SyncExtras syncExtras = new SyncExtras(bundle);
            int isSyncableAsUser = getIsSyncableAsUser(account, str, i);
            synchronized (this.mRecords) {
                Map map = (Map) this.mRecords.get(i);
                if (map == null) {
                    map = new HashMap();
                    this.mRecords.put(i, map);
                }
                VSyncRecord vSyncRecord = (VSyncRecord) map.get(syncRecordKey);
                if (vSyncRecord == null) {
                    vSyncRecord = new VSyncRecord(i, account, str);
                    map.put(syncRecordKey, vSyncRecord);
                }
                if (isSyncableAsUser < 0) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putBoolean("initialize", true);
                    vSyncRecord.extras.add(new SyncExtras(bundle2));
                }
                if (z) {
                    vSyncRecord.configs.put(syncExtras, new PeriodicSyncConfig(j));
                } else {
                    vSyncRecord.extras.add(syncExtras);
                }
            }
        }
    }

    private boolean isAccountExist(int i, Account account, String str) {
        boolean z;
        synchronized (this.mAppSyncAdapterInfos) {
            Map<String, SyncAdapterInfo> map = this.mAppSyncAdapterInfos;
            StringBuilder sb = new StringBuilder();
            sb.append(account.type);
            sb.append("/");
            sb.append(str);
            SyncAdapterInfo syncAdapterInfo = (SyncAdapterInfo) map.get(sb.toString());
            z = syncAdapterInfo != null && VAppManagerService.get().isAppInstalled(syncAdapterInfo.serviceInfo.packageName);
        }
        return z;
    }

    public int getIsSyncableAsUser(Account account, String str, int i) {
        SyncRecordKey syncRecordKey = new SyncRecordKey(account, str);
        synchronized (this.mRecords) {
            Map map = (Map) this.mRecords.get(i);
            if (map == null) {
                return -1;
            }
            VSyncRecord vSyncRecord = (VSyncRecord) map.get(syncRecordKey);
            if (vSyncRecord == null) {
                return -1;
            }
            int i2 = vSyncRecord.syncable;
            return i2;
        }
    }

    private void generateServicesMap(List<ResolveInfo> list, Map<String, SyncAdapterInfo> map, RegisteredServicesParser registeredServicesParser) {
        for (ResolveInfo resolveInfo : list) {
            XmlResourceParser parser = registeredServicesParser.getParser(this.mContext, resolveInfo.serviceInfo, "android.content.SyncAdapter");
            if (parser != null) {
                try {
                    AttributeSet asAttributeSet = Xml.asAttributeSet(parser);
                    while (true) {
                        int next = parser.next();
                        if (next == 1 || next == 2) {
                        }
                    }
                    if ("sync-adapter".equals(parser.getName())) {
                        SyncAdapterType parseSyncAdapterType = parseSyncAdapterType(registeredServicesParser.getResources(this.mContext, resolveInfo.serviceInfo.applicationInfo), asAttributeSet);
                        if (parseSyncAdapterType != null) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(parseSyncAdapterType.accountType);
                            sb.append("/");
                            sb.append(parseSyncAdapterType.authority);
                            map.put(sb.toString(), new SyncAdapterInfo(parseSyncAdapterType, resolveInfo.serviceInfo));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private SyncAdapterType parseSyncAdapterType(Resources resources, AttributeSet attributeSet) {
        TypedArray obtainAttributes = resources.obtainAttributes(attributeSet, (int[]) styleable.SyncAdapter.get());
        try {
            String string = obtainAttributes.getString(styleable.SyncAdapter_contentAuthority.get());
            String string2 = obtainAttributes.getString(styleable.SyncAdapter_accountType.get());
            if (string != null) {
                if (string2 != null) {
                    boolean z = obtainAttributes.getBoolean(styleable.SyncAdapter_userVisible.get(), true);
                    boolean z2 = obtainAttributes.getBoolean(styleable.SyncAdapter_supportsUploading.get(), true);
                    boolean z3 = obtainAttributes.getBoolean(styleable.SyncAdapter_isAlwaysSyncable.get(), true);
                    boolean z4 = obtainAttributes.getBoolean(styleable.SyncAdapter_allowParallelSyncs.get(), true);
                    String string3 = obtainAttributes.getString(styleable.SyncAdapter_settingsActivity.get());
                    if (SyncAdapterTypeN.ctor != null) {
                        SyncAdapterType syncAdapterType = (SyncAdapterType) SyncAdapterTypeN.ctor.newInstance(string, string2, Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(z4), string3, null);
                        obtainAttributes.recycle();
                        return syncAdapterType;
                    }
                    SyncAdapterType syncAdapterType2 = (SyncAdapterType) mirror.android.content.SyncAdapterType.ctor.newInstance(string, string2, Boolean.valueOf(z), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(z4), string3);
                    obtainAttributes.recycle();
                    return syncAdapterType2;
                }
            }
            obtainAttributes.recycle();
            return null;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }
}
