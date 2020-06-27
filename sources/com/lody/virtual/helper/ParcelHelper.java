package com.lody.virtual.helper;

import android.os.Bundle;
import android.os.Parcel;
import java.util.HashMap;
import java.util.Map.Entry;

public class ParcelHelper {
    public static void writeMeta(Parcel parcel, Bundle bundle) {
        HashMap hashMap = new HashMap();
        if (bundle != null) {
            for (String str : bundle.keySet()) {
                hashMap.put(str, bundle.getString(str));
            }
        }
        parcel.writeMap(hashMap);
    }

    public static Bundle readMeta(Parcel parcel) {
        Bundle bundle = new Bundle();
        for (Entry entry : parcel.readHashMap(String.class.getClassLoader()).entrySet()) {
            bundle.putString((String) entry.getKey(), (String) entry.getValue());
        }
        return bundle;
    }
}
