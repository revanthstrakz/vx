package com.lody.virtual.server.p009pm;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* renamed from: com.lody.virtual.server.pm.IntentResolver */
public abstract class IntentResolver<F extends IntentInfo, R> {
    private static final String TAG = "IntentResolver";
    private static final Comparator sResolvePrioritySorter = new Comparator() {
        public int compare(Object obj, Object obj2) {
            int i;
            int i2;
            int i3 = 0;
            if (obj instanceof IntentFilter) {
                i2 = ((IntentFilter) obj).getPriority();
                i = ((IntentFilter) obj2).getPriority();
            } else if (!(obj instanceof ResolveInfo)) {
                return 0;
            } else {
                ResolveInfo resolveInfo = (ResolveInfo) obj;
                ResolveInfo resolveInfo2 = (ResolveInfo) obj2;
                i2 = resolveInfo.filter == null ? 0 : resolveInfo.filter.getPriority();
                i = resolveInfo2.filter == null ? 0 : resolveInfo2.filter.getPriority();
            }
            if (i2 > i) {
                i3 = -1;
            } else if (i2 < i) {
                i3 = 1;
            }
            return i3;
        }
    };
    private HashMap<String, F[]> mActionToFilter = new HashMap<>();
    private HashMap<String, F[]> mBaseTypeToFilter = new HashMap<>();
    private HashSet<F> mFilters = new HashSet<>();
    private HashMap<String, F[]> mSchemeToFilter = new HashMap<>();
    private HashMap<String, F[]> mTypeToFilter = new HashMap<>();
    private HashMap<String, F[]> mTypedActionToFilter = new HashMap<>();
    private HashMap<String, F[]> mWildTypeToFilter = new HashMap<>();

    /* renamed from: com.lody.virtual.server.pm.IntentResolver$IteratorWrapper */
    private class IteratorWrapper implements Iterator<F> {
        private F mCur;

        /* renamed from: mI */
        private Iterator<F> f185mI;

        IteratorWrapper(Iterator<F> it) {
            this.f185mI = it;
        }

        public boolean hasNext() {
            return this.f185mI.hasNext();
        }

        public F next() {
            F f = (IntentInfo) this.f185mI.next();
            this.mCur = f;
            return f;
        }

        public void remove() {
            if (this.mCur != null) {
                IntentResolver.this.removeFilterInternal(this.mCur);
            }
            this.f185mI.remove();
        }
    }

    /* access modifiers changed from: protected */
    public boolean allowFilterResult(F f, List<R> list) {
        return true;
    }

    /* access modifiers changed from: protected */
    public Object filterToLabel(F f) {
        return "IntentFilter";
    }

    /* access modifiers changed from: protected */
    public boolean isFilterStopped(F f) {
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract boolean isPackageForFilter(String str, F f);

    /* access modifiers changed from: protected */
    public abstract F[] newArray(int i);

    /* JADX WARNING: type inference failed for: r1v0, types: [R, F] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Incorrect type for immutable var: ssa=F, code=null, for r1v0, types: [R, F] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public R newResult(F r1, int r2, int r3) {
        /*
            r0 = this;
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.IntentResolver.newResult(com.lody.virtual.server.pm.parser.VPackage$IntentInfo, int, int):java.lang.Object");
    }

    private static FastImmutableArraySet<String> getFastIntentCategories(Intent intent) {
        Set categories = intent.getCategories();
        if (categories == null) {
            return null;
        }
        return new FastImmutableArraySet<>(categories.toArray(new String[categories.size()]));
    }

    public void addFilter(F f) {
        this.mFilters.add(f);
        int register_intent_filter = register_intent_filter(f, f.filter.schemesIterator(), this.mSchemeToFilter, "      Scheme: ");
        int register_mime_types = register_mime_types(f, "      Type: ");
        if (register_intent_filter == 0 && register_mime_types == 0) {
            register_intent_filter(f, f.filter.actionsIterator(), this.mActionToFilter, "      Action: ");
        }
        if (register_mime_types != 0) {
            register_intent_filter(f, f.filter.actionsIterator(), this.mTypedActionToFilter, "      TypedAction: ");
        }
    }

    private boolean filterEquals(IntentFilter intentFilter, IntentFilter intentFilter2) {
        int countActions = intentFilter.countActions();
        if (countActions != intentFilter2.countActions()) {
            return false;
        }
        for (int i = 0; i < countActions; i++) {
            if (!intentFilter2.hasAction(intentFilter.getAction(i))) {
                return false;
            }
        }
        int countCategories = intentFilter.countCategories();
        if (countCategories != intentFilter2.countCategories()) {
            return false;
        }
        for (int i2 = 0; i2 < countCategories; i2++) {
            if (!intentFilter2.hasCategory(intentFilter.getCategory(i2))) {
                return false;
            }
        }
        if (intentFilter.countDataTypes() != intentFilter2.countDataTypes()) {
            return false;
        }
        int countDataSchemes = intentFilter.countDataSchemes();
        if (countDataSchemes != intentFilter2.countDataSchemes()) {
            return false;
        }
        for (int i3 = 0; i3 < countDataSchemes; i3++) {
            if (!intentFilter2.hasDataScheme(intentFilter.getDataScheme(i3))) {
                return false;
            }
        }
        if (intentFilter.countDataAuthorities() != intentFilter2.countDataAuthorities() || intentFilter.countDataPaths() != intentFilter2.countDataPaths()) {
            return false;
        }
        if (VERSION.SDK_INT < 19 || intentFilter.countDataSchemeSpecificParts() == intentFilter2.countDataSchemeSpecificParts()) {
            return true;
        }
        return false;
    }

    private ArrayList<F> collectFilters(F[] fArr, IntentFilter intentFilter) {
        ArrayList<F> arrayList = null;
        if (fArr != null) {
            for (F f : fArr) {
                if (f == null) {
                    break;
                }
                if (filterEquals(f.filter, intentFilter)) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(f);
                }
            }
        }
        return arrayList;
    }

    public ArrayList<F> findFilters(IntentFilter intentFilter) {
        if (intentFilter.countDataSchemes() == 1) {
            return collectFilters((IntentInfo[]) this.mSchemeToFilter.get(intentFilter.getDataScheme(0)), intentFilter);
        }
        if (intentFilter.countDataTypes() != 0 && intentFilter.countActions() == 1) {
            return collectFilters((IntentInfo[]) this.mTypedActionToFilter.get(intentFilter.getAction(0)), intentFilter);
        }
        if (intentFilter.countDataTypes() == 0 && intentFilter.countDataSchemes() == 0 && intentFilter.countActions() == 1) {
            return collectFilters((IntentInfo[]) this.mActionToFilter.get(intentFilter.getAction(0)), intentFilter);
        }
        ArrayList<F> arrayList = null;
        Iterator it = this.mFilters.iterator();
        while (it.hasNext()) {
            IntentInfo intentInfo = (IntentInfo) it.next();
            if (filterEquals(intentInfo.filter, intentFilter)) {
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }
                arrayList.add(intentInfo);
            }
        }
        return arrayList;
    }

    public void removeFilter(F f) {
        removeFilterInternal(f);
        this.mFilters.remove(f);
    }

    /* access modifiers changed from: 0000 */
    public void removeFilterInternal(F f) {
        int unregister_intent_filter = unregister_intent_filter(f, f.filter.schemesIterator(), this.mSchemeToFilter, "      Scheme: ");
        int unregister_mime_types = unregister_mime_types(f, "      Type: ");
        if (unregister_intent_filter == 0 && unregister_mime_types == 0) {
            unregister_intent_filter(f, f.filter.actionsIterator(), this.mActionToFilter, "      Action: ");
        }
        if (unregister_mime_types != 0) {
            unregister_intent_filter(f, f.filter.actionsIterator(), this.mTypedActionToFilter, "      TypedAction: ");
        }
    }

    public Iterator<F> filterIterator() {
        return new IteratorWrapper(this.mFilters.iterator());
    }

    public Set<F> filterSet() {
        return Collections.unmodifiableSet(this.mFilters);
    }

    public List<R> queryIntentFromList(Intent intent, String str, boolean z, ArrayList<F[]> arrayList, int i) {
        ArrayList arrayList2 = new ArrayList();
        FastImmutableArraySet fastIntentCategories = getFastIntentCategories(intent);
        String scheme = intent.getScheme();
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            buildResolveList(intent, fastIntentCategories, z, str, scheme, (IntentInfo[]) arrayList.get(i2), arrayList2, i);
        }
        sortResults(arrayList2);
        return arrayList2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x008a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00ba  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00e2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<R> queryIntent(android.content.Intent r18, java.lang.String r19, boolean r20, int r21) {
        /*
            r17 = this;
            r9 = r17
            r10 = r19
            java.lang.String r11 = r18.getScheme()
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r0 = 0
            if (r10 == 0) goto L_0x007a
            r1 = 47
            int r1 = r10.indexOf(r1)
            if (r1 <= 0) goto L_0x007a
            r2 = 0
            java.lang.String r2 = r10.substring(r2, r1)
            java.lang.String r3 = "*"
            boolean r3 = r2.equals(r3)
            if (r3 != 0) goto L_0x0066
            int r3 = r19.length()
            int r4 = r1 + 2
            if (r3 != r4) goto L_0x0049
            int r1 = r1 + 1
            char r1 = r10.charAt(r1)
            r3 = 42
            if (r1 == r3) goto L_0x0038
            goto L_0x0049
        L_0x0038:
            java.util.HashMap<java.lang.String, F[]> r1 = r9.mBaseTypeToFilter
            java.lang.Object r1 = r1.get(r2)
            com.lody.virtual.server.pm.parser.VPackage$IntentInfo[] r1 = (com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo[]) r1
            java.util.HashMap<java.lang.String, F[]> r3 = r9.mWildTypeToFilter
            java.lang.Object r2 = r3.get(r2)
            com.lody.virtual.server.pm.parser.VPackage$IntentInfo[] r2 = (com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo[]) r2
            goto L_0x0059
        L_0x0049:
            java.util.HashMap<java.lang.String, F[]> r1 = r9.mTypeToFilter
            java.lang.Object r1 = r1.get(r10)
            com.lody.virtual.server.pm.parser.VPackage$IntentInfo[] r1 = (com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo[]) r1
            java.util.HashMap<java.lang.String, F[]> r3 = r9.mWildTypeToFilter
            java.lang.Object r2 = r3.get(r2)
            com.lody.virtual.server.pm.parser.VPackage$IntentInfo[] r2 = (com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo[]) r2
        L_0x0059:
            java.util.HashMap<java.lang.String, F[]> r3 = r9.mWildTypeToFilter
            java.lang.String r4 = "*"
            java.lang.Object r3 = r3.get(r4)
            com.lody.virtual.server.pm.parser.VPackage$IntentInfo[] r3 = (com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo[]) r3
            r13 = r2
            r14 = r3
            goto L_0x007d
        L_0x0066:
            java.lang.String r1 = r18.getAction()
            if (r1 == 0) goto L_0x007a
            java.util.HashMap<java.lang.String, F[]> r1 = r9.mTypedActionToFilter
            java.lang.String r2 = r18.getAction()
            java.lang.Object r1 = r1.get(r2)
            com.lody.virtual.server.pm.parser.VPackage$IntentInfo[] r1 = (com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo[]) r1
            r13 = r0
            goto L_0x007c
        L_0x007a:
            r1 = r0
            r13 = r1
        L_0x007c:
            r14 = r13
        L_0x007d:
            if (r11 == 0) goto L_0x0087
            java.util.HashMap<java.lang.String, F[]> r0 = r9.mSchemeToFilter
            java.lang.Object r0 = r0.get(r11)
            com.lody.virtual.server.pm.parser.VPackage$IntentInfo[] r0 = (com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo[]) r0
        L_0x0087:
            r15 = r0
            if (r10 != 0) goto L_0x00a0
            if (r11 != 0) goto L_0x00a0
            java.lang.String r0 = r18.getAction()
            if (r0 == 0) goto L_0x00a0
            java.util.HashMap<java.lang.String, F[]> r0 = r9.mActionToFilter
            java.lang.String r1 = r18.getAction()
            java.lang.Object r0 = r0.get(r1)
            com.lody.virtual.server.pm.parser.VPackage$IntentInfo[] r0 = (com.lody.virtual.server.p009pm.parser.VPackage.IntentInfo[]) r0
            r6 = r0
            goto L_0x00a1
        L_0x00a0:
            r6 = r1
        L_0x00a1:
            com.lody.virtual.server.pm.FastImmutableArraySet r16 = getFastIntentCategories(r18)
            if (r6 == 0) goto L_0x00b8
            r0 = r17
            r1 = r18
            r2 = r16
            r3 = r20
            r4 = r19
            r5 = r11
            r7 = r12
            r8 = r21
            r0.buildResolveList(r1, r2, r3, r4, r5, r6, r7, r8)
        L_0x00b8:
            if (r13 == 0) goto L_0x00cc
            r0 = r17
            r1 = r18
            r2 = r16
            r3 = r20
            r4 = r19
            r5 = r11
            r6 = r13
            r7 = r12
            r8 = r21
            r0.buildResolveList(r1, r2, r3, r4, r5, r6, r7, r8)
        L_0x00cc:
            if (r14 == 0) goto L_0x00e0
            r0 = r17
            r1 = r18
            r2 = r16
            r3 = r20
            r4 = r19
            r5 = r11
            r6 = r14
            r7 = r12
            r8 = r21
            r0.buildResolveList(r1, r2, r3, r4, r5, r6, r7, r8)
        L_0x00e0:
            if (r15 == 0) goto L_0x00f4
            r0 = r17
            r1 = r18
            r2 = r16
            r3 = r20
            r4 = r19
            r5 = r11
            r6 = r15
            r7 = r12
            r8 = r21
            r0.buildResolveList(r1, r2, r3, r4, r5, r6, r7, r8)
        L_0x00f4:
            r9.sortResults(r12)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.IntentResolver.queryIntent(android.content.Intent, java.lang.String, boolean, int):java.util.List");
    }

    /* access modifiers changed from: protected */
    public void sortResults(List<R> list) {
        Collections.sort(list, sResolvePrioritySorter);
    }

    /* access modifiers changed from: protected */
    public void dumpFilter(PrintWriter printWriter, String str, F f) {
        printWriter.print(str);
        printWriter.println(f);
    }

    /* access modifiers changed from: protected */
    public void dumpFilterLabel(PrintWriter printWriter, String str, Object obj, int i) {
        printWriter.print(str);
        printWriter.print(obj);
        printWriter.print(": ");
        printWriter.println(i);
    }

    private void addFilter(HashMap<String, F[]> hashMap, String str, F f) {
        IntentInfo[] intentInfoArr = (IntentInfo[]) hashMap.get(str);
        if (intentInfoArr == null) {
            IntentInfo[] newArray = newArray(2);
            hashMap.put(str, newArray);
            newArray[0] = f;
            return;
        }
        int length = intentInfoArr.length;
        int i = length;
        while (i > 0 && intentInfoArr[i - 1] == null) {
            i--;
        }
        if (i < length) {
            intentInfoArr[i] = f;
            return;
        }
        IntentInfo[] newArray2 = newArray((length * 3) / 2);
        System.arraycopy(intentInfoArr, 0, newArray2, 0, length);
        newArray2[length] = f;
        hashMap.put(str, newArray2);
    }

    private int register_mime_types(F f, String str) {
        String str2;
        Iterator typesIterator = f.filter.typesIterator();
        if (typesIterator == null) {
            return 0;
        }
        int i = 0;
        while (typesIterator.hasNext()) {
            String str3 = (String) typesIterator.next();
            i++;
            int indexOf = str3.indexOf(47);
            if (indexOf > 0) {
                str2 = str3.substring(0, indexOf).intern();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(str3);
                sb.append("/*");
                String sb2 = sb.toString();
                str2 = str3;
                str3 = sb2;
            }
            addFilter(this.mTypeToFilter, str3, f);
            if (indexOf > 0) {
                addFilter(this.mBaseTypeToFilter, str2, f);
            } else {
                addFilter(this.mWildTypeToFilter, str2, f);
            }
        }
        return i;
    }

    private int unregister_mime_types(F f, String str) {
        String str2;
        Iterator typesIterator = f.filter.typesIterator();
        if (typesIterator == null) {
            return 0;
        }
        int i = 0;
        while (typesIterator.hasNext()) {
            String str3 = (String) typesIterator.next();
            i++;
            int indexOf = str3.indexOf(47);
            if (indexOf > 0) {
                str2 = str3.substring(0, indexOf).intern();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(str3);
                sb.append("/*");
                String sb2 = sb.toString();
                str2 = str3;
                str3 = sb2;
            }
            remove_all_objects(this.mTypeToFilter, str3, f);
            if (indexOf > 0) {
                remove_all_objects(this.mBaseTypeToFilter, str2, f);
            } else {
                remove_all_objects(this.mWildTypeToFilter, str2, f);
            }
        }
        return i;
    }

    private int register_intent_filter(F f, Iterator<String> it, HashMap<String, F[]> hashMap, String str) {
        int i = 0;
        if (it == null) {
            return 0;
        }
        while (it.hasNext()) {
            i++;
            addFilter(hashMap, (String) it.next(), f);
        }
        return i;
    }

    private int unregister_intent_filter(F f, Iterator<String> it, HashMap<String, F[]> hashMap, String str) {
        int i = 0;
        if (it == null) {
            return 0;
        }
        while (it.hasNext()) {
            i++;
            remove_all_objects(hashMap, (String) it.next(), f);
        }
        return i;
    }

    private void remove_all_objects(HashMap<String, F[]> hashMap, String str, Object obj) {
        IntentInfo[] intentInfoArr = (IntentInfo[]) hashMap.get(str);
        if (intentInfoArr != null) {
            int length = intentInfoArr.length - 1;
            while (length >= 0 && intentInfoArr[length] == null) {
                length--;
            }
            int i = length;
            while (length >= 0) {
                if (intentInfoArr[length] == obj) {
                    int i2 = i - length;
                    if (i2 > 0) {
                        System.arraycopy(intentInfoArr, length + 1, intentInfoArr, length, i2);
                    }
                    intentInfoArr[i] = null;
                    i--;
                }
                length--;
            }
            if (i < 0) {
                hashMap.remove(str);
            } else if (i < intentInfoArr.length / 2) {
                IntentInfo[] newArray = newArray(i + 2);
                System.arraycopy(intentInfoArr, 0, newArray, 0, i + 1);
                hashMap.put(str, newArray);
            }
        }
    }

    private void buildResolveList(Intent intent, FastImmutableArraySet<String> fastImmutableArraySet, boolean z, String str, String str2, F[] fArr, List<R> list, int i) {
        F[] fArr2 = fArr;
        List<R> list2 = list;
        String action = intent.getAction();
        Uri data = intent.getData();
        String str3 = intent.getPackage();
        int length = fArr2 != null ? fArr2.length : 0;
        int i2 = 0;
        boolean z2 = false;
        while (i2 < length) {
            F f = fArr2[i2];
            if (f == null) {
                break;
            }
            if ((str3 == null || isPackageForFilter(str3, f)) && allowFilterResult(f, list2)) {
                F f2 = f;
                int match = f.filter.match(action, str, str2, data, fastImmutableArraySet, TAG);
                if (match >= 0) {
                    if (!z || f2.filter.hasCategory("android.intent.category.DEFAULT")) {
                        Object newResult = newResult(f2, match, i);
                        if (newResult != null) {
                            list2.add(newResult);
                        }
                        i2++;
                        fArr2 = fArr;
                    } else {
                        int i3 = i;
                        z2 = true;
                        i2++;
                        fArr2 = fArr;
                    }
                }
            }
            int i4 = i;
            i2++;
            fArr2 = fArr;
        }
        if (!z2) {
            return;
        }
        if (list.size() == 0) {
            VLog.m91w(TAG, "resolveIntent failed: found match, but none with CATEGORY_DEFAULT", new Object[0]);
        } else if (list.size() > 1) {
            VLog.m91w(TAG, "resolveIntent: multiple matches, only some with CATEGORY_DEFAULT", new Object[0]);
        }
    }
}
