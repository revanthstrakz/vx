package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.text.AlphabeticIndex;
import android.icu.text.AlphabeticIndex.ImmutableIndex;
import android.os.LocaleList;
import com.android.launcher3.Utilities;
import java.lang.reflect.Method;
import java.util.Locale;

public class AlphabeticIndexCompat {
    private static final String MID_DOT = "∙";
    private static final String TAG = "AlphabeticIndexCompat";
    private final BaseIndex mBaseIndex;
    private final String mDefaultMiscLabel;

    private static class AlphabeticIndexV16 extends BaseIndex {
        private Object mAlphabeticIndex;
        private Method mGetBucketIndexMethod;
        private Method mGetBucketLabelMethod;

        public AlphabeticIndexV16(Context context) throws Exception {
            super();
            Locale locale = context.getResources().getConfiguration().locale;
            Class cls = Class.forName("libcore.icu.AlphabeticIndex");
            this.mGetBucketIndexMethod = cls.getDeclaredMethod("getBucketIndex", new Class[]{String.class});
            this.mGetBucketLabelMethod = cls.getDeclaredMethod("getBucketLabel", new Class[]{Integer.TYPE});
            this.mAlphabeticIndex = cls.getConstructor(new Class[]{Locale.class}).newInstance(new Object[]{locale});
            if (!locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                cls.getDeclaredMethod("addLabels", new Class[]{Locale.class}).invoke(this.mAlphabeticIndex, new Object[]{Locale.ENGLISH});
            }
        }

        /* access modifiers changed from: protected */
        public int getBucketIndex(String str) {
            try {
                return ((Integer) this.mGetBucketIndexMethod.invoke(this.mAlphabeticIndex, new Object[]{str})).intValue();
            } catch (Exception e) {
                e.printStackTrace();
                return super.getBucketIndex(str);
            }
        }

        /* access modifiers changed from: protected */
        public String getBucketLabel(int i) {
            try {
                return (String) this.mGetBucketLabelMethod.invoke(this.mAlphabeticIndex, new Object[]{Integer.valueOf(i)});
            } catch (Exception e) {
                e.printStackTrace();
                return super.getBucketLabel(i);
            }
        }
    }

    @TargetApi(24)
    private static class AlphabeticIndexVN extends BaseIndex {
        private final ImmutableIndex mAlphabeticIndex;

        public AlphabeticIndexVN(Context context) {
            super();
            LocaleList locales = context.getResources().getConfiguration().getLocales();
            int size = locales.size();
            AlphabeticIndex alphabeticIndex = new AlphabeticIndex(size == 0 ? Locale.ENGLISH : locales.get(0));
            for (int i = 1; i < size; i++) {
                alphabeticIndex.addLabels(new Locale[]{locales.get(i)});
            }
            alphabeticIndex.addLabels(new Locale[]{Locale.ENGLISH});
            this.mAlphabeticIndex = alphabeticIndex.buildImmutableIndex();
        }

        /* access modifiers changed from: protected */
        public int getBucketIndex(String str) {
            return this.mAlphabeticIndex.getBucketIndex(str);
        }

        /* access modifiers changed from: protected */
        public String getBucketLabel(int i) {
            return this.mAlphabeticIndex.getBucket(i).getLabel();
        }
    }

    private static class BaseIndex {
        private static final String BUCKETS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-";
        private static final int UNKNOWN_BUCKET_INDEX = (BUCKETS.length() - 1);

        private BaseIndex() {
        }

        /* access modifiers changed from: protected */
        public int getBucketIndex(String str) {
            if (str.isEmpty()) {
                return UNKNOWN_BUCKET_INDEX;
            }
            int indexOf = BUCKETS.indexOf(str.substring(0, 1).toUpperCase());
            if (indexOf != -1) {
                return indexOf;
            }
            return UNKNOWN_BUCKET_INDEX;
        }

        /* access modifiers changed from: protected */
        public String getBucketLabel(int i) {
            return BUCKETS.substring(i, i + 1);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x002a  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0019 A[SYNTHETIC, Splitter:B:9:0x0019] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AlphabeticIndexCompat(android.content.Context r6) {
        /*
            r5 = this;
            r5.<init>()
            r0 = 0
            boolean r1 = com.android.launcher3.Utilities.ATLEAST_NOUGAT     // Catch:{ Exception -> 0x000e }
            if (r1 == 0) goto L_0x0016
            com.android.launcher3.compat.AlphabeticIndexCompat$AlphabeticIndexVN r1 = new com.android.launcher3.compat.AlphabeticIndexCompat$AlphabeticIndexVN     // Catch:{ Exception -> 0x000e }
            r1.<init>(r6)     // Catch:{ Exception -> 0x000e }
            goto L_0x0017
        L_0x000e:
            r1 = move-exception
            java.lang.String r2 = "AlphabeticIndexCompat"
            java.lang.String r3 = "Unable to load the system index"
            android.util.Log.d(r2, r3, r1)
        L_0x0016:
            r1 = r0
        L_0x0017:
            if (r1 != 0) goto L_0x0028
            com.android.launcher3.compat.AlphabeticIndexCompat$AlphabeticIndexV16 r2 = new com.android.launcher3.compat.AlphabeticIndexCompat$AlphabeticIndexV16     // Catch:{ Exception -> 0x0020 }
            r2.<init>(r6)     // Catch:{ Exception -> 0x0020 }
            r1 = r2
            goto L_0x0028
        L_0x0020:
            r2 = move-exception
            java.lang.String r3 = "AlphabeticIndexCompat"
            java.lang.String r4 = "Unable to load the system index"
            android.util.Log.d(r3, r4, r2)
        L_0x0028:
            if (r1 != 0) goto L_0x002f
            com.android.launcher3.compat.AlphabeticIndexCompat$BaseIndex r1 = new com.android.launcher3.compat.AlphabeticIndexCompat$BaseIndex
            r1.<init>()
        L_0x002f:
            r5.mBaseIndex = r1
            android.content.res.Resources r6 = r6.getResources()
            android.content.res.Configuration r6 = r6.getConfiguration()
            java.util.Locale r6 = r6.locale
            java.lang.String r6 = r6.getLanguage()
            java.util.Locale r0 = java.util.Locale.JAPANESE
            java.lang.String r0 = r0.getLanguage()
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0050
            java.lang.String r6 = "他"
            r5.mDefaultMiscLabel = r6
            goto L_0x0054
        L_0x0050:
            java.lang.String r6 = "∙"
            r5.mDefaultMiscLabel = r6
        L_0x0054:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.compat.AlphabeticIndexCompat.<init>(android.content.Context):void");
    }

    public String computeSectionName(CharSequence charSequence) {
        String trim = Utilities.trim(charSequence);
        String bucketLabel = this.mBaseIndex.getBucketLabel(this.mBaseIndex.getBucketIndex(trim));
        if (!Utilities.trim(bucketLabel).isEmpty() || trim.length() <= 0) {
            return bucketLabel;
        }
        int codePointAt = trim.codePointAt(0);
        if (Character.isDigit(codePointAt)) {
            return "#";
        }
        return Character.isLetter(codePointAt) ? this.mDefaultMiscLabel : MID_DOT;
    }
}
