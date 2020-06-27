package com.microsoft.appcenter.utils.crypto;

import android.content.Context;
import java.security.KeyStore.Entry;

class CryptoNoOpHandler implements CryptoHandler {
    public byte[] decrypt(ICryptoFactory iCryptoFactory, int i, Entry entry, byte[] bArr) {
        return bArr;
    }

    public byte[] encrypt(ICryptoFactory iCryptoFactory, int i, Entry entry, byte[] bArr) {
        return bArr;
    }

    public void generateKey(ICryptoFactory iCryptoFactory, String str, Context context) {
    }

    public String getAlgorithm() {
        return "None";
    }

    CryptoNoOpHandler() {
    }
}
