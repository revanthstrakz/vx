package com.microsoft.appcenter.utils.crypto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.security.KeyPairGeneratorSpec.Builder;
import android.support.annotation.RequiresApi;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import javax.security.auth.x500.X500Principal;

@RequiresApi(19)
class CryptoRsaHandler implements CryptoHandler {
    public String getAlgorithm() {
        return "RSA/ECB/PKCS1Padding/2048";
    }

    CryptoRsaHandler() {
    }

    @SuppressLint({"InlinedApi", "TrulyRandom"})
    public void generateKey(ICryptoFactory iCryptoFactory, String str, Context context) throws Exception {
        Calendar instance = Calendar.getInstance();
        instance.add(1, 1);
        KeyPairGenerator instance2 = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        Builder alias = new Builder(context).setAlias(str);
        StringBuilder sb = new StringBuilder();
        sb.append("CN=");
        sb.append(str);
        instance2.initialize(alias.setSubject(new X500Principal(sb.toString())).setStartDate(new Date()).setEndDate(instance.getTime()).setSerialNumber(BigInteger.TEN).setKeySize(2048).build());
        instance2.generateKeyPair();
    }

    private ICipher getCipher(ICryptoFactory iCryptoFactory, int i) throws Exception {
        return iCryptoFactory.getCipher("RSA/ECB/PKCS1Padding", i >= 23 ? "AndroidKeyStoreBCWorkaround" : "AndroidOpenSSL");
    }

    public byte[] encrypt(ICryptoFactory iCryptoFactory, int i, Entry entry, byte[] bArr) throws Exception {
        ICipher cipher = getCipher(iCryptoFactory, i);
        X509Certificate x509Certificate = (X509Certificate) ((PrivateKeyEntry) entry).getCertificate();
        try {
            x509Certificate.checkValidity();
            cipher.init(1, x509Certificate.getPublicKey());
            return cipher.doFinal(bArr);
        } catch (CertificateExpiredException e) {
            throw new InvalidKeyException(e);
        }
    }

    public byte[] decrypt(ICryptoFactory iCryptoFactory, int i, Entry entry, byte[] bArr) throws Exception {
        ICipher cipher = getCipher(iCryptoFactory, i);
        cipher.init(2, ((PrivateKeyEntry) entry).getPrivateKey());
        return cipher.doFinal(bArr);
    }
}
