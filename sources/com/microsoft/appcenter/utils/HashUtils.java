package com.microsoft.appcenter.utils;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.load.Key;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    private static final char[] HEXADECIMAL_OUTPUT = "0123456789abcdef".toCharArray();

    @VisibleForTesting
    HashUtils() {
    }

    @NonNull
    public static String sha256(@NonNull String str) {
        return sha256(str, Key.STRING_CHARSET_NAME);
    }

    @VisibleForTesting
    @NonNull
    static String sha256(@NonNull String str, String str2) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.update(str.getBytes(str2));
            return encodeHex(instance.digest());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private static String encodeHex(@NonNull byte[] bArr) {
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i] & 255;
            int i2 = i * 2;
            cArr[i2] = HEXADECIMAL_OUTPUT[b >>> 4];
            cArr[i2 + 1] = HEXADECIMAL_OUTPUT[b & 15];
        }
        return new String(cArr);
    }
}
