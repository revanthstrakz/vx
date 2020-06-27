package com.taobao.android.dexposed.utility;

import android.util.Log;

public class NeverCalled {
    private void fake(int i) {
        String simpleName = getClass().getSimpleName();
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append("Do not inline me!!");
        Log.i(simpleName, sb.toString());
    }
}
