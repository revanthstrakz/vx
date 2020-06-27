package com.microsoft.appcenter.utils.crypto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Base64;
import com.android.launcher3.IconCache;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.cert.CertificateExpiredException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class CryptoUtils {
    @VisibleForTesting
    static final ICryptoFactory DEFAULT_CRYPTO_FACTORY = new ICryptoFactory() {
        public IKeyGenerator getKeyGenerator(String str, String str2) throws Exception {
            final KeyGenerator instance = KeyGenerator.getInstance(str, str2);
            return new IKeyGenerator() {
                public void init(AlgorithmParameterSpec algorithmParameterSpec) throws Exception {
                    instance.init(algorithmParameterSpec);
                }

                public void generateKey() {
                    instance.generateKey();
                }
            };
        }

        public ICipher getCipher(String str, String str2) throws Exception {
            final Cipher instance = Cipher.getInstance(str, str2);
            return new ICipher() {
                public void init(int i, Key key) throws Exception {
                    instance.init(i, key);
                }

                public void init(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec) throws Exception {
                    instance.init(i, key, algorithmParameterSpec);
                }

                public byte[] doFinal(byte[] bArr) throws Exception {
                    return instance.doFinal(bArr);
                }

                public byte[] doFinal(byte[] bArr, int i, int i2) throws Exception {
                    return instance.doFinal(bArr, i, i2);
                }

                public byte[] getIV() {
                    return instance.getIV();
                }

                public int getBlockSize() {
                    return instance.getBlockSize();
                }

                public String getAlgorithm() {
                    return instance.getAlgorithm();
                }

                public String getProvider() {
                    return instance.getProvider().getName();
                }
            };
        }
    };
    private static final String M_KEY_EXPIRED_EXCEPTION = "android.security.keystore.KeyExpiredException";
    @SuppressLint({"StaticFieldLeak"})
    private static CryptoUtils sInstance;
    private final int mApiLevel;
    private final Context mContext;
    private final ICryptoFactory mCryptoFactory;
    private final Map<String, CryptoHandlerEntry> mCryptoHandlers;
    private final KeyStore mKeyStore;

    @VisibleForTesting
    static class CryptoHandlerEntry {
        int mAliasIndex;
        final CryptoHandler mCryptoHandler;

        CryptoHandlerEntry(int i, CryptoHandler cryptoHandler) {
            this.mAliasIndex = i;
            this.mCryptoHandler = cryptoHandler;
        }
    }

    public static class DecryptedData {
        final String mDecryptedData;
        final String mNewEncryptedData;

        @VisibleForTesting
        public DecryptedData(String str, String str2) {
            this.mDecryptedData = str;
            this.mNewEncryptedData = str2;
        }

        public String getDecryptedData() {
            return this.mDecryptedData;
        }

        public String getNewEncryptedData() {
            return this.mNewEncryptedData;
        }
    }

    interface ICipher {
        byte[] doFinal(byte[] bArr) throws Exception;

        byte[] doFinal(byte[] bArr, int i, int i2) throws Exception;

        @VisibleForTesting
        String getAlgorithm();

        int getBlockSize();

        byte[] getIV();

        @VisibleForTesting
        String getProvider();

        void init(int i, Key key) throws Exception;

        void init(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec) throws Exception;
    }

    interface ICryptoFactory {
        ICipher getCipher(String str, String str2) throws Exception;

        IKeyGenerator getKeyGenerator(String str, String str2) throws Exception;
    }

    interface IKeyGenerator {
        void generateKey();

        void init(AlgorithmParameterSpec algorithmParameterSpec) throws Exception;
    }

    private CryptoUtils(@NonNull Context context) {
        this(context, DEFAULT_CRYPTO_FACTORY, VERSION.SDK_INT);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0046 A[SYNTHETIC, Splitter:B:21:0x0046] */
    @android.support.annotation.VisibleForTesting
    @android.annotation.TargetApi(23)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    CryptoUtils(@android.support.annotation.NonNull android.content.Context r3, @android.support.annotation.NonNull com.microsoft.appcenter.utils.crypto.CryptoUtils.ICryptoFactory r4, int r5) {
        /*
            r2 = this;
            r2.<init>()
            java.util.LinkedHashMap r0 = new java.util.LinkedHashMap
            r0.<init>()
            r2.mCryptoHandlers = r0
            android.content.Context r3 = r3.getApplicationContext()
            r2.mContext = r3
            r2.mCryptoFactory = r4
            r2.mApiLevel = r5
            r3 = 0
            r4 = 19
            if (r5 < r4) goto L_0x002c
            java.lang.String r4 = "AndroidKeyStore"
            java.security.KeyStore r4 = java.security.KeyStore.getInstance(r4)     // Catch:{ Exception -> 0x0025 }
            r4.load(r3)     // Catch:{ Exception -> 0x0024 }
            r3 = r4
            goto L_0x002c
        L_0x0024:
            r3 = r4
        L_0x0025:
            java.lang.String r4 = "AppCenter"
            java.lang.String r0 = "Cannot use secure keystore on this device."
            com.microsoft.appcenter.utils.AppCenterLog.error(r4, r0)
        L_0x002c:
            r2.mKeyStore = r3
            if (r3 == 0) goto L_0x0044
            r4 = 23
            if (r5 < r4) goto L_0x0044
            com.microsoft.appcenter.utils.crypto.CryptoAesHandler r4 = new com.microsoft.appcenter.utils.crypto.CryptoAesHandler     // Catch:{ Exception -> 0x003d }
            r4.<init>()     // Catch:{ Exception -> 0x003d }
            r2.registerHandler(r4)     // Catch:{ Exception -> 0x003d }
            goto L_0x0044
        L_0x003d:
            java.lang.String r4 = "AppCenter"
            java.lang.String r5 = "Cannot use modern encryption on this device."
            com.microsoft.appcenter.utils.AppCenterLog.error(r4, r5)
        L_0x0044:
            if (r3 == 0) goto L_0x0056
            com.microsoft.appcenter.utils.crypto.CryptoRsaHandler r3 = new com.microsoft.appcenter.utils.crypto.CryptoRsaHandler     // Catch:{ Exception -> 0x004f }
            r3.<init>()     // Catch:{ Exception -> 0x004f }
            r2.registerHandler(r3)     // Catch:{ Exception -> 0x004f }
            goto L_0x0056
        L_0x004f:
            java.lang.String r3 = "AppCenter"
            java.lang.String r4 = "Cannot use old encryption on this device."
            com.microsoft.appcenter.utils.AppCenterLog.error(r3, r4)
        L_0x0056:
            com.microsoft.appcenter.utils.crypto.CryptoNoOpHandler r3 = new com.microsoft.appcenter.utils.crypto.CryptoNoOpHandler
            r3.<init>()
            java.util.Map<java.lang.String, com.microsoft.appcenter.utils.crypto.CryptoUtils$CryptoHandlerEntry> r4 = r2.mCryptoHandlers
            java.lang.String r5 = r3.getAlgorithm()
            com.microsoft.appcenter.utils.crypto.CryptoUtils$CryptoHandlerEntry r0 = new com.microsoft.appcenter.utils.crypto.CryptoUtils$CryptoHandlerEntry
            r1 = 0
            r0.<init>(r1, r3)
            r4.put(r5, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.utils.crypto.CryptoUtils.<init>(android.content.Context, com.microsoft.appcenter.utils.crypto.CryptoUtils$ICryptoFactory, int):void");
    }

    public static CryptoUtils getInstance(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new CryptoUtils(context);
        }
        return sInstance;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public ICryptoFactory getCryptoFactory() {
        return this.mCryptoFactory;
    }

    private void registerHandler(@NonNull CryptoHandler cryptoHandler) throws Exception {
        int i = 0;
        String alias = getAlias(cryptoHandler, 0);
        String alias2 = getAlias(cryptoHandler, 1);
        Date creationDate = this.mKeyStore.getCreationDate(alias);
        Date creationDate2 = this.mKeyStore.getCreationDate(alias2);
        if (creationDate2 != null && creationDate2.after(creationDate)) {
            alias = alias2;
            i = 1;
        }
        if (this.mCryptoHandlers.isEmpty() && !this.mKeyStore.containsAlias(alias)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Creating alias: ");
            sb.append(alias);
            AppCenterLog.debug("AppCenter", sb.toString());
            cryptoHandler.generateKey(this.mCryptoFactory, alias, this.mContext);
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Using ");
        sb2.append(alias);
        AppCenterLog.debug("AppCenter", sb2.toString());
        this.mCryptoHandlers.put(cryptoHandler.getAlgorithm(), new CryptoHandlerEntry(i, cryptoHandler));
    }

    @NonNull
    private String getAlias(@NonNull CryptoHandler cryptoHandler, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("appcenter.");
        sb.append(i);
        sb.append(IconCache.EMPTY_CLASS_NAME);
        sb.append(cryptoHandler.getAlgorithm());
        return sb.toString();
    }

    @Nullable
    private Entry getKeyStoreEntry(@NonNull CryptoHandlerEntry cryptoHandlerEntry) throws Exception {
        return getKeyStoreEntry(cryptoHandlerEntry.mCryptoHandler, cryptoHandlerEntry.mAliasIndex);
    }

    @Nullable
    private Entry getKeyStoreEntry(CryptoHandler cryptoHandler, int i) throws Exception {
        if (this.mKeyStore == null) {
            return null;
        }
        return this.mKeyStore.getEntry(getAlias(cryptoHandler, i), null);
    }

    @Nullable
    public String encrypt(@Nullable String str) {
        CryptoHandlerEntry cryptoHandlerEntry;
        CryptoHandler cryptoHandler;
        if (str == null) {
            return null;
        }
        try {
            cryptoHandlerEntry = (CryptoHandlerEntry) this.mCryptoHandlers.values().iterator().next();
            cryptoHandler = cryptoHandlerEntry.mCryptoHandler;
            String encodeToString = Base64.encodeToString(cryptoHandler.encrypt(this.mCryptoFactory, this.mApiLevel, getKeyStoreEntry(cryptoHandlerEntry), str.getBytes(com.bumptech.glide.load.Key.STRING_CHARSET_NAME)), 0);
            StringBuilder sb = new StringBuilder();
            sb.append(cryptoHandler.getAlgorithm());
            sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
            sb.append(encodeToString);
            return sb.toString();
        } catch (InvalidKeyException e) {
            if (!(e.getCause() instanceof CertificateExpiredException)) {
                if (!M_KEY_EXPIRED_EXCEPTION.equals(e.getClass().getName())) {
                    throw e;
                }
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Alias expired: ");
            sb2.append(cryptoHandlerEntry.mAliasIndex);
            AppCenterLog.debug("AppCenter", sb2.toString());
            cryptoHandlerEntry.mAliasIndex ^= 1;
            String alias = getAlias(cryptoHandler, cryptoHandlerEntry.mAliasIndex);
            if (this.mKeyStore.containsAlias(alias)) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Deleting alias: ");
                sb3.append(alias);
                AppCenterLog.debug("AppCenter", sb3.toString());
                this.mKeyStore.deleteEntry(alias);
            }
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Creating alias: ");
            sb4.append(alias);
            AppCenterLog.debug("AppCenter", sb4.toString());
            cryptoHandler.generateKey(this.mCryptoFactory, alias, this.mContext);
            return encrypt(str);
        } catch (Exception unused) {
            AppCenterLog.error("AppCenter", "Failed to encrypt data.");
            return str;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0048, code lost:
        return getDecryptedData(r3, r2.mAliasIndex ^ 1, r1[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0049, code lost:
        com.microsoft.appcenter.utils.AppCenterLog.error("AppCenter", "Failed to decrypt data.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0055, code lost:
        return new com.microsoft.appcenter.utils.crypto.CryptoUtils.DecryptedData(r8, null);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:18:0x003f */
    @android.support.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.microsoft.appcenter.utils.crypto.CryptoUtils.DecryptedData decrypt(@android.support.annotation.Nullable java.lang.String r8) {
        /*
            r7 = this;
            r0 = 0
            if (r8 != 0) goto L_0x0009
            com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData r8 = new com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData
            r8.<init>(r0, r0)
            return r8
        L_0x0009:
            java.lang.String r1 = ":"
            java.lang.String[] r1 = r8.split(r1)
            int r2 = r1.length
            r3 = 2
            if (r2 != r3) goto L_0x001f
            java.util.Map<java.lang.String, com.microsoft.appcenter.utils.crypto.CryptoUtils$CryptoHandlerEntry> r2 = r7.mCryptoHandlers
            r3 = 0
            r3 = r1[r3]
            java.lang.Object r2 = r2.get(r3)
            com.microsoft.appcenter.utils.crypto.CryptoUtils$CryptoHandlerEntry r2 = (com.microsoft.appcenter.utils.crypto.CryptoUtils.CryptoHandlerEntry) r2
            goto L_0x0020
        L_0x001f:
            r2 = r0
        L_0x0020:
            if (r2 != 0) goto L_0x0024
            r3 = r0
            goto L_0x0026
        L_0x0024:
            com.microsoft.appcenter.utils.crypto.CryptoHandler r3 = r2.mCryptoHandler
        L_0x0026:
            if (r3 != 0) goto L_0x0035
            java.lang.String r1 = "AppCenter"
            java.lang.String r2 = "Failed to decrypt data."
            com.microsoft.appcenter.utils.AppCenterLog.error(r1, r2)
            com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData r1 = new com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData
            r1.<init>(r8, r0)
            return r1
        L_0x0035:
            r4 = 1
            int r5 = r2.mAliasIndex     // Catch:{ Exception -> 0x003f }
            r6 = r1[r4]     // Catch:{ Exception -> 0x003f }
            com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData r5 = r7.getDecryptedData(r3, r5, r6)     // Catch:{ Exception -> 0x003f }
            return r5
        L_0x003f:
            int r2 = r2.mAliasIndex     // Catch:{ Exception -> 0x0049 }
            r2 = r2 ^ r4
            r1 = r1[r4]     // Catch:{ Exception -> 0x0049 }
            com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData r1 = r7.getDecryptedData(r3, r2, r1)     // Catch:{ Exception -> 0x0049 }
            return r1
        L_0x0049:
            java.lang.String r1 = "AppCenter"
            java.lang.String r2 = "Failed to decrypt data."
            com.microsoft.appcenter.utils.AppCenterLog.error(r1, r2)
            com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData r1 = new com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData
            r1.<init>(r8, r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.utils.crypto.CryptoUtils.decrypt(java.lang.String):com.microsoft.appcenter.utils.crypto.CryptoUtils$DecryptedData");
    }

    @NonNull
    private DecryptedData getDecryptedData(CryptoHandler cryptoHandler, int i, String str) throws Exception {
        String str2 = new String(cryptoHandler.decrypt(this.mCryptoFactory, this.mApiLevel, getKeyStoreEntry(cryptoHandler, i), Base64.decode(str, 0)), com.bumptech.glide.load.Key.STRING_CHARSET_NAME);
        return new DecryptedData(str2, cryptoHandler != ((CryptoHandlerEntry) this.mCryptoHandlers.values().iterator().next()).mCryptoHandler ? encrypt(str2) : null);
    }
}
