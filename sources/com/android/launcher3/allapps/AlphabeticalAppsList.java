package com.android.launcher3.allapps;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.discovery.AppDiscoveryAppInfo;
import com.android.launcher3.discovery.AppDiscoveryItem;
import com.android.launcher3.discovery.AppDiscoveryUpdateState;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ComponentKeyMapper;
import com.android.launcher3.util.LabelComparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

public class AlphabeticalAppsList {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_PREDICTIONS = false;
    private static final int FAST_SCROLL_FRACTION_DISTRIBUTE_BY_NUM_SECTIONS = 1;
    private static final int FAST_SCROLL_FRACTION_DISTRIBUTE_BY_ROWS_FRACTION = 0;
    public static final String TAG = "AlphabeticalAppsList";
    private AllAppsGridAdapter mAdapter;
    private final ArrayList<AdapterItem> mAdapterItems = new ArrayList<>();
    private AppDiscoveryUpdateState mAppDiscoveryUpdateState;
    private AppInfoComparator mAppNameComparator;
    private final List<AppInfo> mApps = new ArrayList();
    private HashMap<CharSequence, String> mCachedSectionNames = new HashMap<>();
    private final HashMap<ComponentKey, AppInfo> mComponentToAppMap = new HashMap<>();
    private final List<AppDiscoveryAppInfo> mDiscoveredApps = new ArrayList();
    private final int mFastScrollDistributionMode = 1;
    private final List<FastScrollSectionInfo> mFastScrollerSections = new ArrayList();
    private final List<AppInfo> mFilteredApps = new ArrayList();
    private AlphabeticIndexCompat mIndexer;
    private final Launcher mLauncher;
    private int mNumAppRowsInAdapter;
    private int mNumAppsPerRow;
    private int mNumPredictedAppsPerRow;
    private final List<ComponentKeyMapper<AppInfo>> mPredictedAppComponents = new ArrayList();
    private final List<AppInfo> mPredictedApps = new ArrayList();
    private ArrayList<ComponentKey> mSearchResults;

    public static class AdapterItem {
        public int appIndex = -1;
        public AppInfo appInfo = null;
        public int position;
        public int rowAppIndex;
        public int rowIndex;
        public String sectionName = null;
        public int viewType;

        public static AdapterItem asPredictedApp(int i, String str, AppInfo appInfo2, int i2) {
            AdapterItem asApp = asApp(i, str, appInfo2, i2);
            asApp.viewType = 4;
            return asApp;
        }

        public static AdapterItem asApp(int i, String str, AppInfo appInfo2, int i2) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 2;
            adapterItem.position = i;
            adapterItem.sectionName = str;
            adapterItem.appInfo = appInfo2;
            adapterItem.appIndex = i2;
            return adapterItem;
        }

        public static AdapterItem asDiscoveryItem(int i, String str, AppInfo appInfo2, int i2) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 256;
            adapterItem.position = i;
            adapterItem.sectionName = str;
            adapterItem.appInfo = appInfo2;
            adapterItem.appIndex = i2;
            return adapterItem;
        }

        public static AdapterItem asEmptySearch(int i) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 8;
            adapterItem.position = i;
            return adapterItem;
        }

        public static AdapterItem asPredictionDivider(int i) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 64;
            adapterItem.position = i;
            return adapterItem;
        }

        public static AdapterItem asMarketDivider(int i) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 32;
            adapterItem.position = i;
            return adapterItem;
        }

        public static AdapterItem asLoadingDivider(int i) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 128;
            adapterItem.position = i;
            return adapterItem;
        }

        public static AdapterItem asMarketSearch(int i) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 16;
            adapterItem.position = i;
            return adapterItem;
        }
    }

    public static class FastScrollSectionInfo {
        public AdapterItem fastScrollToItem;
        public String sectionName;
        public float touchFraction;

        public FastScrollSectionInfo(String str) {
            this.sectionName = str;
        }
    }

    public AlphabeticalAppsList(Context context) {
        this.mLauncher = Launcher.getLauncher(context);
        this.mIndexer = new AlphabeticIndexCompat(context);
        this.mAppNameComparator = new AppInfoComparator(context);
    }

    public void setNumAppsPerRow(int i, int i2) {
        this.mNumAppsPerRow = i;
        this.mNumPredictedAppsPerRow = i2;
        updateAdapterItems();
    }

    public void setAdapter(AllAppsGridAdapter allAppsGridAdapter) {
        this.mAdapter = allAppsGridAdapter;
    }

    public List<AppInfo> getApps() {
        return this.mApps;
    }

    public List<AppInfo> getPredictedApps() {
        return this.mPredictedApps;
    }

    public List<FastScrollSectionInfo> getFastScrollerSections() {
        return this.mFastScrollerSections;
    }

    public List<AdapterItem> getAdapterItems() {
        return this.mAdapterItems;
    }

    public int getNumAppRows() {
        return this.mNumAppRowsInAdapter;
    }

    public int getNumFilteredApps() {
        return this.mFilteredApps.size();
    }

    public boolean hasFilter() {
        return this.mSearchResults != null;
    }

    public boolean hasNoFilteredResults() {
        return this.mSearchResults != null && this.mFilteredApps.isEmpty();
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldShowEmptySearch() {
        return hasNoFilteredResults() && !isAppDiscoveryRunning() && this.mDiscoveredApps.isEmpty();
    }

    public boolean setOrderedFilter(ArrayList<ComponentKey> arrayList) {
        boolean z = false;
        if (this.mSearchResults == arrayList) {
            return false;
        }
        if (this.mSearchResults != null && this.mSearchResults.equals(arrayList)) {
            z = true;
        }
        this.mSearchResults = arrayList;
        updateAdapterItems();
        return !z;
    }

    public void onAppDiscoverySearchUpdate(@Nullable AppDiscoveryItem appDiscoveryItem, @NonNull AppDiscoveryUpdateState appDiscoveryUpdateState) {
        this.mAppDiscoveryUpdateState = appDiscoveryUpdateState;
        switch (appDiscoveryUpdateState) {
            case START:
                this.mDiscoveredApps.clear();
                break;
            case UPDATE:
                this.mDiscoveredApps.add(new AppDiscoveryAppInfo(appDiscoveryItem));
                break;
        }
        updateAdapterItems();
    }

    private List<AppInfo> processPredictedAppComponents(List<ComponentKeyMapper<AppInfo>> list) {
        if (this.mComponentToAppMap.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        for (ComponentKeyMapper item : list) {
            AppInfo appInfo = (AppInfo) item.getItem(this.mComponentToAppMap);
            if (appInfo != null) {
                arrayList.add(appInfo);
            }
            if (arrayList.size() == this.mNumPredictedAppsPerRow) {
                break;
            }
        }
        return arrayList;
    }

    public void setPredictedApps(List<ComponentKeyMapper<AppInfo>> list) {
        this.mPredictedAppComponents.clear();
        this.mPredictedAppComponents.addAll(list);
        List processPredictedAppComponents = processPredictedAppComponents(list);
        if (processPredictedAppComponents.equals(this.mPredictedApps)) {
            return;
        }
        if (processPredictedAppComponents.size() == this.mPredictedApps.size()) {
            swapInNewPredictedApps(processPredictedAppComponents);
        } else {
            onAppsUpdated();
        }
    }

    private void swapInNewPredictedApps(List<AppInfo> list) {
        this.mPredictedApps.clear();
        this.mPredictedApps.addAll(list);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AppInfo appInfo = (AppInfo) list.get(i);
            AdapterItem asPredictedApp = AdapterItem.asPredictedApp(i, "", appInfo, i);
            asPredictedApp.rowAppIndex = i;
            this.mAdapterItems.set(i, asPredictedApp);
            this.mFilteredApps.set(i, appInfo);
            this.mAdapter.notifyItemChanged(i);
        }
    }

    public void setApps(List<AppInfo> list) {
        this.mComponentToAppMap.clear();
        addOrUpdateApps(list);
    }

    public void addOrUpdateApps(List<AppInfo> list) {
        for (AppInfo appInfo : list) {
            this.mComponentToAppMap.put(appInfo.toComponentKey(), appInfo);
        }
        onAppsUpdated();
    }

    public void removeApps(List<AppInfo> list) {
        for (AppInfo componentKey : list) {
            this.mComponentToAppMap.remove(componentKey.toComponentKey());
        }
        onAppsUpdated();
    }

    private void onAppsUpdated() {
        this.mApps.clear();
        this.mApps.addAll(this.mComponentToAppMap.values());
        Collections.sort(this.mApps, this.mAppNameComparator);
        if (this.mLauncher.getResources().getConfiguration().locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            TreeMap treeMap = new TreeMap(new LabelComparator());
            for (AppInfo appInfo : this.mApps) {
                String andUpdateCachedSectionName = getAndUpdateCachedSectionName(appInfo.title);
                ArrayList arrayList = (ArrayList) treeMap.get(andUpdateCachedSectionName);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    treeMap.put(andUpdateCachedSectionName, arrayList);
                }
                arrayList.add(appInfo);
            }
            this.mApps.clear();
            for (Entry value : treeMap.entrySet()) {
                this.mApps.addAll((Collection) value.getValue());
            }
        } else {
            for (AppInfo appInfo2 : this.mApps) {
                getAndUpdateCachedSectionName(appInfo2.title);
            }
        }
        updateAdapterItems();
    }

    private void updateAdapterItems() {
        refillAdapterItems();
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0151  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void refillAdapterItems() {
        /*
            r10 = this;
            java.util.List<com.android.launcher3.AppInfo> r0 = r10.mFilteredApps
            r0.clear()
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r0 = r10.mFastScrollerSections
            r0.clear()
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r0 = r10.mAdapterItems
            r0.clear()
            java.util.List<com.android.launcher3.AppInfo> r0 = r10.mPredictedApps
            r0.clear()
            java.util.List<com.android.launcher3.util.ComponentKeyMapper<com.android.launcher3.AppInfo>> r0 = r10.mPredictedAppComponents
            r1 = 0
            r2 = 0
            if (r0 == 0) goto L_0x0084
            java.util.List<com.android.launcher3.util.ComponentKeyMapper<com.android.launcher3.AppInfo>> r0 = r10.mPredictedAppComponents
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0084
            boolean r0 = r10.hasFilter()
            if (r0 != 0) goto L_0x0084
            java.util.List<com.android.launcher3.AppInfo> r0 = r10.mPredictedApps
            java.util.List<com.android.launcher3.util.ComponentKeyMapper<com.android.launcher3.AppInfo>> r3 = r10.mPredictedAppComponents
            java.util.List r3 = r10.processPredictedAppComponents(r3)
            r0.addAll(r3)
            java.util.List<com.android.launcher3.AppInfo> r0 = r10.mPredictedApps
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0084
            com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo r0 = new com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo
            java.lang.String r3 = ""
            r0.<init>(r3)
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r3 = r10.mFastScrollerSections
            r3.add(r0)
            java.util.List<com.android.launcher3.AppInfo> r3 = r10.mPredictedApps
            java.util.Iterator r3 = r3.iterator()
            r4 = 0
            r5 = 0
        L_0x004f:
            boolean r6 = r3.hasNext()
            if (r6 == 0) goto L_0x0078
            java.lang.Object r6 = r3.next()
            com.android.launcher3.AppInfo r6 = (com.android.launcher3.AppInfo) r6
            int r7 = r4 + 1
            java.lang.String r8 = ""
            int r9 = r5 + 1
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r4 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asPredictedApp(r4, r8, r6, r5)
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r5 = r0.fastScrollToItem
            if (r5 != 0) goto L_0x006b
            r0.fastScrollToItem = r4
        L_0x006b:
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r5 = r10.mAdapterItems
            r5.add(r4)
            java.util.List<com.android.launcher3.AppInfo> r4 = r10.mFilteredApps
            r4.add(r6)
            r4 = r7
            r5 = r9
            goto L_0x004f
        L_0x0078:
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r3 = r10.mAdapterItems
            int r6 = r4 + 1
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r4 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asPredictionDivider(r4)
            r3.add(r4)
            goto L_0x0087
        L_0x0084:
            r0 = r1
            r5 = 0
            r6 = 0
        L_0x0087:
            java.util.List r3 = r10.getFiltersAppInfos()
            java.util.Iterator r3 = r3.iterator()
        L_0x008f:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x00cd
            java.lang.Object r4 = r3.next()
            com.android.launcher3.AppInfo r4 = (com.android.launcher3.AppInfo) r4
            java.lang.CharSequence r7 = r4.title
            java.lang.String r7 = r10.getAndUpdateCachedSectionName(r7)
            boolean r8 = r7.equals(r1)
            if (r8 != 0) goto L_0x00b2
            com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo r0 = new com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo
            r0.<init>(r7)
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r1 = r10.mFastScrollerSections
            r1.add(r0)
            r1 = r7
        L_0x00b2:
            int r8 = r6 + 1
            int r9 = r5 + 1
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r5 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asApp(r6, r7, r4, r5)
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r6 = r0.fastScrollToItem
            if (r6 != 0) goto L_0x00c0
            r0.fastScrollToItem = r5
        L_0x00c0:
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r6 = r10.mAdapterItems
            r6.add(r5)
            java.util.List<com.android.launcher3.AppInfo> r5 = r10.mFilteredApps
            r5.add(r4)
            r6 = r8
            r5 = r9
            goto L_0x008f
        L_0x00cd:
            boolean r0 = r10.hasFilter()
            if (r0 == 0) goto L_0x014d
            boolean r0 = r10.isAppDiscoveryRunning()
            if (r0 != 0) goto L_0x0109
            java.util.List<com.android.launcher3.discovery.AppDiscoveryAppInfo> r0 = r10.mDiscoveredApps
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x00e2
            goto L_0x0109
        L_0x00e2:
            boolean r0 = r10.hasNoFilteredResults()
            if (r0 == 0) goto L_0x00f4
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r0 = r10.mAdapterItems
            int r1 = r6 + 1
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r3 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asEmptySearch(r6)
            r0.add(r3)
            goto L_0x00ff
        L_0x00f4:
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r0 = r10.mAdapterItems
            int r1 = r6 + 1
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r3 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asMarketDivider(r6)
            r0.add(r3)
        L_0x00ff:
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r0 = r10.mAdapterItems
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r1 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asMarketSearch(r1)
            r0.add(r1)
            goto L_0x014d
        L_0x0109:
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r0 = r10.mAdapterItems
            int r1 = r6 + 1
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r3 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asLoadingDivider(r6)
            r0.add(r3)
            r0 = 0
        L_0x0115:
            java.util.List<com.android.launcher3.discovery.AppDiscoveryAppInfo> r3 = r10.mDiscoveredApps
            int r3 = r3.size()
            if (r0 >= r3) goto L_0x013e
            java.util.List<com.android.launcher3.discovery.AppDiscoveryAppInfo> r3 = r10.mDiscoveredApps
            java.lang.Object r3 = r3.get(r0)
            com.android.launcher3.discovery.AppDiscoveryAppInfo r3 = (com.android.launcher3.discovery.AppDiscoveryAppInfo) r3
            boolean r4 = r3.isRecent
            if (r4 == 0) goto L_0x012a
            goto L_0x013b
        L_0x012a:
            int r4 = r1 + 1
            java.lang.String r6 = ""
            int r7 = r5 + 1
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r1 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asDiscoveryItem(r1, r6, r3, r5)
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r3 = r10.mAdapterItems
            r3.add(r1)
            r1 = r4
            r5 = r7
        L_0x013b:
            int r0 = r0 + 1
            goto L_0x0115
        L_0x013e:
            boolean r0 = r10.isAppDiscoveryRunning()
            if (r0 != 0) goto L_0x014d
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r0 = r10.mAdapterItems
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r1 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asMarketSearch(r1)
            r0.add(r1)
        L_0x014d:
            int r0 = r10.mNumAppsPerRow
            if (r0 == 0) goto L_0x01f8
            r0 = -1
            java.util.ArrayList<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r1 = r10.mAdapterItems
            java.util.Iterator r1 = r1.iterator()
            r3 = 0
            r4 = 0
        L_0x015a:
            boolean r5 = r1.hasNext()
            r6 = 1
            if (r5 == 0) goto L_0x018c
            java.lang.Object r5 = r1.next()
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r5 = (com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem) r5
            r5.rowIndex = r2
            int r7 = r5.viewType
            boolean r7 = com.android.launcher3.allapps.AllAppsGridAdapter.isDividerViewType(r7)
            if (r7 == 0) goto L_0x0173
            r3 = 0
            goto L_0x015a
        L_0x0173:
            int r7 = r5.viewType
            boolean r7 = com.android.launcher3.allapps.AllAppsGridAdapter.isIconViewType(r7)
            if (r7 == 0) goto L_0x015a
            int r7 = r10.mNumAppsPerRow
            int r7 = r3 % r7
            if (r7 != 0) goto L_0x0184
            int r0 = r0 + 1
            r4 = 0
        L_0x0184:
            r5.rowIndex = r0
            r5.rowAppIndex = r4
            int r3 = r3 + 1
            int r4 = r4 + r6
            goto L_0x015a
        L_0x018c:
            int r0 = r0 + r6
            r10.mNumAppRowsInAdapter = r0
            r0 = 1065353216(0x3f800000, float:1.0)
            r1 = 0
            switch(r6) {
                case 0: goto L_0x01c2;
                case 1: goto L_0x0196;
                default: goto L_0x0195;
            }
        L_0x0195:
            goto L_0x01f8
        L_0x0196:
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r2 = r10.mFastScrollerSections
            int r2 = r2.size()
            float r2 = (float) r2
            float r0 = r0 / r2
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r2 = r10.mFastScrollerSections
            java.util.Iterator r2 = r2.iterator()
            r3 = 0
        L_0x01a5:
            boolean r4 = r2.hasNext()
            if (r4 == 0) goto L_0x01f8
            java.lang.Object r4 = r2.next()
            com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo r4 = (com.android.launcher3.allapps.AlphabeticalAppsList.FastScrollSectionInfo) r4
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r5 = r4.fastScrollToItem
            int r5 = r5.viewType
            boolean r5 = com.android.launcher3.allapps.AllAppsGridAdapter.isIconViewType(r5)
            if (r5 != 0) goto L_0x01be
            r4.touchFraction = r1
            goto L_0x01a5
        L_0x01be:
            r4.touchFraction = r3
            float r3 = r3 + r0
            goto L_0x01a5
        L_0x01c2:
            int r2 = r10.mNumAppRowsInAdapter
            float r2 = (float) r2
            float r0 = r0 / r2
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r2 = r10.mFastScrollerSections
            java.util.Iterator r2 = r2.iterator()
        L_0x01cc:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x01f8
            java.lang.Object r3 = r2.next()
            com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo r3 = (com.android.launcher3.allapps.AlphabeticalAppsList.FastScrollSectionInfo) r3
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r4 = r3.fastScrollToItem
            int r5 = r4.viewType
            boolean r5 = com.android.launcher3.allapps.AllAppsGridAdapter.isIconViewType(r5)
            if (r5 != 0) goto L_0x01e5
            r3.touchFraction = r1
            goto L_0x01cc
        L_0x01e5:
            int r5 = r4.rowAppIndex
            float r5 = (float) r5
            int r6 = r10.mNumAppsPerRow
            float r6 = (float) r6
            float r6 = r0 / r6
            float r5 = r5 * r6
            int r4 = r4.rowIndex
            float r4 = (float) r4
            float r4 = r4 * r0
            float r4 = r4 + r5
            r3.touchFraction = r4
            goto L_0x01cc
        L_0x01f8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AlphabeticalAppsList.refillAdapterItems():void");
    }

    public boolean isAppDiscoveryRunning() {
        return this.mAppDiscoveryUpdateState == AppDiscoveryUpdateState.START || this.mAppDiscoveryUpdateState == AppDiscoveryUpdateState.UPDATE;
    }

    private List<AppInfo> getFiltersAppInfos() {
        int i;
        if (this.mSearchResults == null) {
            return this.mApps;
        }
        LauncherAppsCompat instance = LauncherAppsCompat.getInstance(this.mLauncher);
        IconCache iconCache = LauncherAppState.getInstance(this.mLauncher).getIconCache();
        UserManagerCompat instance2 = UserManagerCompat.getInstance(this.mLauncher);
        ArrayList arrayList = new ArrayList();
        Iterator it = this.mSearchResults.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ComponentKey componentKey = (ComponentKey) it.next();
            AppInfo appInfo = (AppInfo) this.mComponentToAppMap.get(componentKey);
            if (appInfo == null) {
                Iterator it2 = instance.getActivityList(componentKey.componentName.getPackageName(), componentKey.user).iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    LauncherActivityInfo launcherActivityInfo = (LauncherActivityInfo) it2.next();
                    if (launcherActivityInfo.getComponentName().equals(componentKey.componentName)) {
                        AppInfo appInfo2 = new AppInfo(launcherActivityInfo, componentKey.user, instance2.isQuietModeEnabled(componentKey.user));
                        iconCache.getTitleAndIcon(appInfo2, false);
                        arrayList.add(appInfo2);
                        break;
                    }
                }
            } else {
                arrayList.add(appInfo);
            }
        }
        if (this.mDiscoveredApps.size() > 0) {
            for (i = 0; i < this.mDiscoveredApps.size(); i++) {
                AppDiscoveryAppInfo appDiscoveryAppInfo = (AppDiscoveryAppInfo) this.mDiscoveredApps.get(i);
                if (appDiscoveryAppInfo.isRecent) {
                    arrayList.add(appDiscoveryAppInfo);
                }
            }
            Collections.sort(arrayList, this.mAppNameComparator);
        }
        return arrayList;
    }

    public AppInfo findApp(ComponentKeyMapper<AppInfo> componentKeyMapper) {
        return (AppInfo) componentKeyMapper.getItem(this.mComponentToAppMap);
    }

    private String getAndUpdateCachedSectionName(CharSequence charSequence) {
        String str = (String) this.mCachedSectionNames.get(charSequence);
        if (str != null) {
            return str;
        }
        String computeSectionName = this.mIndexer.computeSectionName(charSequence);
        this.mCachedSectionNames.put(charSequence, computeSectionName);
        return computeSectionName;
    }
}
