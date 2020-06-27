package com.microsoft.appcenter.utils.crypto;

import android.content.Context;
import java.security.KeyStore.Entry;

interface CryptoHandler {
    byte[] decrypt(ICryptoFactory iCryptoFactory, int i, Entry entry, byte[] bArr) throws Exception;

    byte[] encrypt(ICryptoFactory iCryptoFactory, int i, Entry entry, byte[] bArr) throws Exception;

    void generateKey(ICryptoFactory iCryptoFactory, String str, Context context) throws Exception;

    String getAlgorithm();
}
