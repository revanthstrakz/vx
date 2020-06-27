package com.android.launcher3.allapps.search;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.os.Handler;
import android.os.UserHandle;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.search.AllAppsSearchBarController.Callbacks;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.util.ComponentKey;
import java.text.Collator;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DefaultAppSearchAlgorithm implements SearchAlgorithm {
    public static final String SEARCH_HIDDEN_APPS = "pref_search_hidden_apps";
    private static final Pattern complementaryGlyphs = Pattern.compile("\\p{M}");
    private final List<AppInfo> mApps;
    private final Context mContext;
    protected final Handler mResultHandler = new Handler();

    public static class StringMatcher {
        private static final char MAX_UNICODE = '￿';
        private final Collator mCollator = Collator.getInstance();

        StringMatcher() {
            this.mCollator.setStrength(0);
            this.mCollator.setDecomposition(1);
        }

        public boolean matches(String str, String str2) {
            boolean z = true;
            switch (this.mCollator.compare(str, str2)) {
                case -1:
                    Collator collator = this.mCollator;
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(MAX_UNICODE);
                    if (collator.compare(sb.toString(), str2) <= -1) {
                        z = false;
                    }
                    return z;
                case 0:
                    return true;
                default:
                    return false;
            }
        }

        public static StringMatcher getInstance() {
            return new StringMatcher();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x002f, code lost:
        if (r3 == 1) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0032, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0033, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean isBreak(int r2, int r3, int r4) {
        /*
            r0 = 1
            if (r3 == 0) goto L_0x0035
            switch(r3) {
                case 12: goto L_0x0035;
                case 13: goto L_0x0035;
                case 14: goto L_0x0035;
                default: goto L_0x0006;
            }
        L_0x0006:
            r1 = 20
            if (r2 == r1) goto L_0x0034
            r1 = 0
            switch(r2) {
                case 1: goto L_0x002c;
                case 2: goto L_0x0024;
                case 3: goto L_0x002f;
                default: goto L_0x000e;
            }
        L_0x000e:
            switch(r2) {
                case 9: goto L_0x0015;
                case 10: goto L_0x0015;
                case 11: goto L_0x0015;
                default: goto L_0x0011;
            }
        L_0x0011:
            switch(r2) {
                case 24: goto L_0x0034;
                case 25: goto L_0x0034;
                case 26: goto L_0x0034;
                default: goto L_0x0014;
            }
        L_0x0014:
            return r1
        L_0x0015:
            r2 = 9
            if (r3 == r2) goto L_0x0022
            r2 = 10
            if (r3 == r2) goto L_0x0022
            r2 = 11
            if (r3 == r2) goto L_0x0022
            goto L_0x0023
        L_0x0022:
            r0 = 0
        L_0x0023:
            return r0
        L_0x0024:
            r2 = 5
            if (r3 > r2) goto L_0x002b
            if (r3 > 0) goto L_0x002a
            goto L_0x002b
        L_0x002a:
            r0 = 0
        L_0x002b:
            return r0
        L_0x002c:
            if (r4 != r0) goto L_0x002f
            return r0
        L_0x002f:
            if (r3 == r0) goto L_0x0032
            goto L_0x0033
        L_0x0032:
            r0 = 0
        L_0x0033:
            return r0
        L_0x0034:
            return r0
        L_0x0035:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.search.DefaultAppSearchAlgorithm.isBreak(int, int, int):boolean");
    }

    public DefaultAppSearchAlgorithm(Context context, List<AppInfo> list) {
        this.mContext = context;
        this.mApps = list;
    }

    public void cancel(boolean z) {
        if (z) {
            this.mResultHandler.removeCallbacksAndMessages(null);
        }
    }

    public void doSearch(final String str, final Callbacks callbacks) {
        final ArrayList titleMatchResult = getTitleMatchResult(str);
        this.mResultHandler.post(new Runnable() {
            public void run() {
                callbacks.onSearchResult(str, titleMatchResult);
            }
        });
    }

    private ArrayList<ComponentKey> getTitleMatchResult(String str) {
        String lowerCase = str.toLowerCase();
        ArrayList<ComponentKey> arrayList = new ArrayList<>();
        StringMatcher instance = StringMatcher.getInstance();
        for (AppInfo appInfo : getApps(this.mContext, this.mApps)) {
            if (matches(appInfo, lowerCase, instance)) {
                arrayList.add(appInfo.toComponentKey());
            }
        }
        return arrayList;
    }

    public static List<AppInfo> getApps(Context context, List<AppInfo> list) {
        if (!Utilities.getPrefs(context).getBoolean(SEARCH_HIDDEN_APPS, false)) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        IconCache iconCache = LauncherAppState.getInstance(context).getIconCache();
        for (UserHandle userHandle : UserManagerCompat.getInstance(context).getUserProfiles()) {
            ArrayList arrayList2 = new ArrayList();
            for (LauncherActivityInfo launcherActivityInfo : LauncherAppsCompat.getInstance(context).getActivityList(null, userHandle)) {
                if (!arrayList2.contains(launcherActivityInfo.getComponentName())) {
                    arrayList2.add(launcherActivityInfo.getComponentName());
                    AppInfo appInfo = new AppInfo(context, launcherActivityInfo, userHandle);
                    iconCache.getTitleAndIcon(appInfo, false);
                    arrayList.add(appInfo);
                }
            }
        }
        return arrayList;
    }

    public static boolean matches(AppInfo appInfo, String str, StringMatcher stringMatcher) {
        return matches(appInfo, str, stringMatcher, false) || matches(appInfo, str, stringMatcher, true);
    }

    private static boolean matches(AppInfo appInfo, String str, StringMatcher stringMatcher, boolean z) {
        int length = str.length();
        String charSequence = appInfo.title.toString();
        int length2 = charSequence.length();
        if (length2 < length || length <= 0) {
            return false;
        }
        if (z) {
            charSequence = normalize(charSequence);
            str = normalize(str);
        }
        int i = length2 - length;
        int type = Character.getType(charSequence.codePointAt(0));
        int i2 = 0;
        int i3 = 0;
        while (i2 <= i) {
            int type2 = i2 < length2 + -1 ? Character.getType(charSequence.codePointAt(i2 + 1)) : 0;
            if (isBreak(type, i3, type2) && stringMatcher.matches(str, charSequence.substring(i2, i2 + length))) {
                return true;
            }
            i2++;
            i3 = type;
            type = type2;
        }
        return false;
    }

    private static String normalize(String str) {
        return complementaryGlyphs.matcher(Normalizer.normalize(str, Form.NFKD)).replaceAll("");
    }
}
