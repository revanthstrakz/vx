package com.allenliu.versionchecklib.utils;

import android.util.Log;
import com.allenliu.versionchecklib.core.AllenChecker;

public class ALog {
    /* renamed from: e */
    public static void m10e(String str) {
        if (AllenChecker.isDebug() && str != null && !str.isEmpty()) {
            Log.e("Allen Checker", str);
        }
    }
}
