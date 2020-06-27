package com.android.launcher3.shortcuts;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.C0622R;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.utils.PrefStorageConstants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParserException;

public class ShortcutInfoCompatBackport extends ShortcutInfoCompat {
    private static final String USE_PACKAGE = "shortcut_backport_use_package";
    private final ComponentName mActivity;
    private final Context mContext;
    private final String mDisabledMessage;
    private final boolean mEnabled;
    private final Integer mIcon;
    private final String mId;
    private final Intent mIntent;
    private final String mLongLabel;
    private final String mPackageName;
    private final String mShortLabel;

    public int getRank() {
        return 1;
    }

    public boolean isDeclaredInManifest() {
        return true;
    }

    public boolean isDynamic() {
        return false;
    }

    public boolean isPinned() {
        return false;
    }

    public String toString() {
        return "";
    }

    static Intent stripPackage(Intent intent) {
        Intent intent2 = new Intent(intent);
        if (!intent2.getBooleanExtra(USE_PACKAGE, true)) {
            intent2.setPackage(null);
        }
        intent2.removeExtra(USE_PACKAGE);
        return intent2;
    }

    public ShortcutInfoCompatBackport(Context context, Resources resources, String str, ComponentName componentName, XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
        super(null);
        this.mContext = context;
        this.mPackageName = str;
        this.mActivity = componentName;
        HashMap hashMap = new HashMap();
        for (int i = 0; i < xmlResourceParser.getAttributeCount(); i++) {
            hashMap.put(xmlResourceParser.getAttributeName(i), xmlResourceParser.getAttributeValue(i));
        }
        this.mId = (String) hashMap.get("shortcutId");
        this.mEnabled = !hashMap.containsKey(PrefStorageConstants.KEY_ENABLED) || ((String) hashMap.get(PrefStorageConstants.KEY_ENABLED)).toLowerCase().equals("true");
        if (hashMap.containsKey(BaseLauncherColumns.ICON)) {
            String str2 = (String) hashMap.get(BaseLauncherColumns.ICON);
            int identifier = resources.getIdentifier(str2, null, str);
            if (identifier == 0) {
                identifier = Integer.parseInt(str2.substring(1));
            }
            this.mIcon = Integer.valueOf(identifier);
        } else {
            this.mIcon = Integer.valueOf(0);
        }
        this.mShortLabel = hashMap.containsKey("shortcutShortLabel") ? resources.getString(Integer.valueOf(((String) hashMap.get("shortcutShortLabel")).substring(1)).intValue()) : "";
        this.mLongLabel = hashMap.containsKey("shortcutLongLabel") ? resources.getString(Integer.valueOf(((String) hashMap.get("shortcutLongLabel")).substring(1)).intValue()) : this.mShortLabel;
        this.mDisabledMessage = hashMap.containsKey("shortcutDisabledMessage") ? resources.getString(Integer.valueOf(((String) hashMap.get("shortcutDisabledMessage")).substring(1)).intValue()) : "";
        HashMap hashMap2 = new HashMap();
        HashMap hashMap3 = new HashMap();
        HashMap hashMap4 = new HashMap();
        int depth = xmlResourceParser.getDepth();
        do {
            if (xmlResourceParser.nextToken() == 2) {
                String name = xmlResourceParser.getName();
                if (name.equals(BaseLauncherColumns.INTENT)) {
                    hashMap2.clear();
                    hashMap4.clear();
                    for (int i2 = 0; i2 < xmlResourceParser.getAttributeCount(); i2++) {
                        hashMap2.put(xmlResourceParser.getAttributeName(i2), xmlResourceParser.getAttributeValue(i2));
                    }
                } else if (name.equals("extra")) {
                    hashMap3.clear();
                    for (int i3 = 0; i3 < xmlResourceParser.getAttributeCount(); i3++) {
                        hashMap3.put(xmlResourceParser.getAttributeName(i3), xmlResourceParser.getAttributeValue(i3));
                    }
                    if (hashMap3.containsKey(CommonProperties.NAME) && hashMap3.containsKey("value")) {
                        hashMap4.put(hashMap3.get(CommonProperties.NAME), hashMap3.get("value"));
                    }
                }
            }
        } while (xmlResourceParser.getDepth() > depth);
        String str3 = hashMap2.containsKey("action") ? (String) hashMap2.get("action") : "android.intent.action.MAIN";
        boolean containsKey = hashMap2.containsKey("targetPackage");
        String str4 = containsKey ? (String) hashMap2.get("targetPackage") : this.mPackageName;
        this.mIntent = new Intent(str3).setPackage(str4).setFlags(268484608).putExtra(ShortcutInfoCompat.EXTRA_SHORTCUT_ID, this.mId);
        if (hashMap2.containsKey("targetClass")) {
            this.mIntent.setComponent(new ComponentName(str4, (String) hashMap2.get("targetClass")));
        }
        if (hashMap2.containsKey("data")) {
            this.mIntent.setData(Uri.parse((String) hashMap2.get("data")));
        }
        for (Entry entry : hashMap4.entrySet()) {
            this.mIntent.putExtra((String) entry.getKey(), (String) entry.getValue());
        }
        this.mIntent.putExtra(USE_PACKAGE, containsKey);
    }

    public Drawable getIcon(int i) {
        try {
            return this.mContext.getPackageManager().getResourcesForApplication(this.mPackageName).getDrawableForDensity(this.mIcon.intValue(), i);
        } catch (NameNotFoundException | NotFoundException unused) {
            return this.mContext.getResources().getDrawableForDensity(C0622R.C0624drawable.ic_default_shortcut, i);
        }
    }

    public Intent makeIntent() {
        return this.mIntent;
    }

    public String getPackage() {
        return this.mIntent.getPackage();
    }

    public String getId() {
        return this.mId;
    }

    public CharSequence getShortLabel() {
        return this.mShortLabel;
    }

    public CharSequence getLongLabel() {
        return this.mLongLabel;
    }

    public ComponentName getActivity() {
        return this.mIntent.getComponent() == null ? this.mActivity : this.mIntent.getComponent();
    }

    public UserHandle getUserHandle() {
        return Process.myUserHandle();
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public CharSequence getDisabledMessage() {
        return this.mDisabledMessage;
    }
}
