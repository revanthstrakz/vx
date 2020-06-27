package com.lody.virtual.helper.utils;

import android.util.Base64;

public class EncodeUtils {
    public static String decode(String str) {
        return new String(Base64.decode(str, 0));
    }
}
