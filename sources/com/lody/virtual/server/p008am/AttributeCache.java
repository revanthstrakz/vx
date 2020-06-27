package com.lody.virtual.server.p008am;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.util.SparseArray;
import java.util.HashMap;
import java.util.WeakHashMap;

/* renamed from: com.lody.virtual.server.am.AttributeCache */
public final class AttributeCache {
    private static AttributeCache sInstance;
    private final Configuration mConfiguration = new Configuration();
    private final Context mContext;
    private final WeakHashMap<String, Package> mPackages = new WeakHashMap<>();

    /* renamed from: com.lody.virtual.server.am.AttributeCache$Entry */
    public static final class Entry {
        public final TypedArray array;
        public final Context context;

        public Entry(Context context2, TypedArray typedArray) {
            this.context = context2;
            this.array = typedArray;
        }
    }

    /* renamed from: com.lody.virtual.server.am.AttributeCache$Package */
    public static final class Package {
        public final Context context;
        /* access modifiers changed from: private */
        public final SparseArray<HashMap<int[], Entry>> mMap = new SparseArray<>();

        public Package(Context context2) {
            this.context = context2;
        }
    }

    public AttributeCache(Context context) {
        this.mContext = context;
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new AttributeCache(context);
        }
    }

    public static AttributeCache instance() {
        return sInstance;
    }

    public void removePackage(String str) {
        synchronized (this) {
            this.mPackages.remove(str);
        }
    }

    public void updateConfiguration(Configuration configuration) {
        synchronized (this) {
            if ((this.mConfiguration.updateFrom(configuration) & -1073741985) != 0) {
                this.mPackages.clear();
            }
        }
    }

    public Entry get(String str, int i, int[] iArr) {
        HashMap hashMap;
        synchronized (this) {
            Package packageR = (Package) this.mPackages.get(str);
            if (packageR != null) {
                hashMap = (HashMap) packageR.mMap.get(i);
                if (hashMap != null) {
                    Entry entry = (Entry) hashMap.get(iArr);
                    if (entry != null) {
                        return entry;
                    }
                }
            } else {
                try {
                    Context createPackageContext = this.mContext.createPackageContext(str, 3);
                    if (createPackageContext == null) {
                        return null;
                    }
                    Package packageR2 = new Package(createPackageContext);
                    this.mPackages.put(str, packageR2);
                    hashMap = null;
                    packageR = packageR2;
                } catch (NameNotFoundException unused) {
                    return null;
                }
            }
            if (hashMap == null) {
                hashMap = new HashMap();
                packageR.mMap.put(i, hashMap);
            }
            try {
                Entry entry2 = new Entry(packageR.context, packageR.context.obtainStyledAttributes(i, iArr));
                hashMap.put(iArr, entry2);
                return entry2;
            } catch (NotFoundException unused2) {
                return null;
            }
        }
    }
}
