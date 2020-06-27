package com.lody.virtual.server.p009pm;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import com.lody.virtual.helper.compat.ObjectsCompat;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.server.p009pm.parser.PackageParserEx;
import com.lody.virtual.server.p009pm.parser.VPackage.ProviderComponent;
import com.lody.virtual.server.p009pm.parser.VPackage.ProviderIntentInfo;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/* renamed from: com.lody.virtual.server.pm.ProviderIntentResolver */
final class ProviderIntentResolver extends IntentResolver<ProviderIntentInfo, ResolveInfo> {
    private int mFlags;
    private final HashMap<ComponentName, ProviderComponent> mProviders = new HashMap<>();

    /* access modifiers changed from: protected */
    public void dumpFilter(PrintWriter printWriter, String str, ProviderIntentInfo providerIntentInfo) {
    }

    /* access modifiers changed from: protected */
    public void dumpFilterLabel(PrintWriter printWriter, String str, Object obj, int i) {
    }

    /* access modifiers changed from: protected */
    public boolean isFilterStopped(ProviderIntentInfo providerIntentInfo) {
        return false;
    }

    ProviderIntentResolver() {
    }

    public List<ResolveInfo> queryIntent(Intent intent, String str, boolean z, int i) {
        this.mFlags = z ? 65536 : 0;
        return super.queryIntent(intent, str, z, i);
    }

    public List<ResolveInfo> queryIntent(Intent intent, String str, int i, int i2) {
        this.mFlags = i;
        return super.queryIntent(intent, str, (i & 65536) != 0, i2);
    }

    public List<ResolveInfo> queryIntentForPackage(Intent intent, String str, int i, ArrayList<ProviderComponent> arrayList, int i2) {
        if (arrayList == null) {
            return null;
        }
        this.mFlags = i;
        boolean z = (i & 65536) != 0;
        int size = arrayList.size();
        ArrayList arrayList2 = new ArrayList(size);
        for (int i3 = 0; i3 < size; i3++) {
            ArrayList arrayList3 = ((ProviderComponent) arrayList.get(i3)).intents;
            if (arrayList3 != null && arrayList3.size() > 0) {
                ProviderIntentInfo[] providerIntentInfoArr = new ProviderIntentInfo[arrayList3.size()];
                arrayList3.toArray(providerIntentInfoArr);
                arrayList2.add(providerIntentInfoArr);
            }
        }
        return super.queryIntentFromList(intent, str, z, arrayList2, i2);
    }

    public final void addProvider(ProviderComponent providerComponent) {
        if (this.mProviders.containsKey(providerComponent.getComponentName())) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provider ");
            sb.append(providerComponent.getComponentName());
            sb.append(" already defined; ignoring");
            VLog.m91w("PackageManager", sb.toString(), new Object[0]);
            return;
        }
        this.mProviders.put(providerComponent.getComponentName(), providerComponent);
        int size = providerComponent.intents.size();
        for (int i = 0; i < size; i++) {
            addFilter((ProviderIntentInfo) providerComponent.intents.get(i));
        }
    }

    public final void removeProvider(ProviderComponent providerComponent) {
        this.mProviders.remove(providerComponent.getComponentName());
        int size = providerComponent.intents.size();
        for (int i = 0; i < size; i++) {
            removeFilter((ProviderIntentInfo) providerComponent.intents.get(i));
        }
    }

    /* access modifiers changed from: protected */
    @TargetApi(19)
    public boolean allowFilterResult(ProviderIntentInfo providerIntentInfo, List<ResolveInfo> list) {
        ProviderInfo providerInfo = providerIntentInfo.provider.info;
        for (int size = list.size() - 1; size >= 0; size--) {
            ProviderInfo providerInfo2 = ((ResolveInfo) list.get(size)).providerInfo;
            if (ObjectsCompat.equals(providerInfo2.name, providerInfo.name) && ObjectsCompat.equals(providerInfo2.packageName, providerInfo.packageName)) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public ProviderIntentInfo[] newArray(int i) {
        return new ProviderIntentInfo[i];
    }

    /* access modifiers changed from: protected */
    public boolean isPackageForFilter(String str, ProviderIntentInfo providerIntentInfo) {
        return str.equals(providerIntentInfo.provider.owner.packageName);
    }

    /* access modifiers changed from: protected */
    @TargetApi(19)
    public ResolveInfo newResult(ProviderIntentInfo providerIntentInfo, int i, int i2) {
        ProviderComponent providerComponent = providerIntentInfo.provider;
        ProviderInfo generateProviderInfo = PackageParserEx.generateProviderInfo(providerComponent, this.mFlags, ((PackageSetting) providerComponent.owner.mExtras).readUserState(i2), i2);
        if (generateProviderInfo == null) {
            return null;
        }
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.providerInfo = generateProviderInfo;
        if ((this.mFlags & 64) != 0) {
            resolveInfo.filter = providerIntentInfo.filter;
        }
        resolveInfo.priority = providerIntentInfo.filter.getPriority();
        resolveInfo.preferredOrder = providerComponent.owner.mPreferredOrder;
        resolveInfo.match = i;
        resolveInfo.isDefault = providerIntentInfo.hasDefault;
        resolveInfo.labelRes = providerIntentInfo.labelRes;
        resolveInfo.nonLocalizedLabel = providerIntentInfo.nonLocalizedLabel;
        resolveInfo.icon = providerIntentInfo.icon;
        return resolveInfo;
    }

    /* access modifiers changed from: protected */
    public void sortResults(List<ResolveInfo> list) {
        Collections.sort(list, VPackageManagerService.sResolvePrioritySorter);
    }

    /* access modifiers changed from: protected */
    public Object filterToLabel(ProviderIntentInfo providerIntentInfo) {
        return providerIntentInfo.provider;
    }
}
