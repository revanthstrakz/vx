package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.graphics.LauncherIcons;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AutoInstallsLayout {
    private static final String ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE = "com.android.launcher.action.APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE";
    static final String ACTION_LAUNCHER_CUSTOMIZATION = "android.autoinstalls.config.action.PLAY_AUTO_INSTALL";
    private static final String ATTR_CLASS_NAME = "className";
    private static final String ATTR_CONTAINER = "container";
    private static final String ATTR_ICON = "icon";
    private static final String ATTR_KEY = "key";
    private static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_RANK = "rank";
    private static final String ATTR_SCREEN = "screen";
    private static final String ATTR_SPAN_X = "spanX";
    private static final String ATTR_SPAN_Y = "spanY";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_URL = "url";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_WORKSPACE = "workspace";
    private static final String ATTR_X = "x";
    private static final String ATTR_Y = "y";
    private static final String FORMATTED_LAYOUT_RES = "default_layout_%dx%d";
    private static final String FORMATTED_LAYOUT_RES_WITH_HOSTEAT = "default_layout_%dx%d_h%s";
    private static final String HOTSEAT_CONTAINER_NAME = Favorites.containerToString(-101);
    private static final String LAYOUT_RES = "default_layout";
    private static final boolean LOGD = false;
    private static final String TAG = "AutoInstalls";
    private static final String TAG_APPWIDGET = "appwidget";
    private static final String TAG_APP_ICON = "appicon";
    private static final String TAG_AUTO_INSTALL = "autoinstall";
    private static final String TAG_EXTRA = "extra";
    private static final String TAG_FOLDER = "folder";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_SHORTCUT = "shortcut";
    private static final String TAG_WORKSPACE = "workspace";
    final AppWidgetHost mAppWidgetHost;
    protected final LayoutParserCallback mCallback;
    private final int mColumnCount;
    final Context mContext;
    protected SQLiteDatabase mDb;
    private final InvariantDeviceProfile mIdp;
    protected final int mLayoutId;
    protected final PackageManager mPackageManager;
    protected final String mRootTag;
    private final int mRowCount;
    protected final Resources mSourceRes;
    private final long[] mTemp = new long[2];
    final ContentValues mValues;

    protected class AppShortcutParser implements TagParser {
        protected AppShortcutParser() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0071, code lost:
            r9 = com.android.launcher3.AutoInstallsLayout.TAG;
            r2 = new java.lang.StringBuilder();
            r2.append("Favorite not found: ");
            r2.append(r0);
            r2.append("/");
            r2.append(r1);
            android.util.Log.e(r9, r2.toString());
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0091, code lost:
            return -1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:?, code lost:
            r3 = new android.content.ComponentName(r8.this$0.mPackageManager.currentToCanonicalPackageNames(new java.lang.String[]{r0})[0], r1);
            r7 = r3;
            r3 = r8.this$0.mPackageManager.getActivityInfo(r3, 0);
            r2 = r7;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0027 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public long parseAndAdd(android.content.res.XmlResourceParser r9) {
            /*
                r8 = this;
                java.lang.String r0 = "packageName"
                java.lang.String r0 = com.android.launcher3.AutoInstallsLayout.getAttributeValue(r9, r0)
                java.lang.String r1 = "className"
                java.lang.String r1 = com.android.launcher3.AutoInstallsLayout.getAttributeValue(r9, r1)
                boolean r2 = android.text.TextUtils.isEmpty(r0)
                if (r2 != 0) goto L_0x0092
                boolean r2 = android.text.TextUtils.isEmpty(r1)
                if (r2 != 0) goto L_0x0092
                r9 = 0
                android.content.ComponentName r2 = new android.content.ComponentName     // Catch:{ NameNotFoundException -> 0x0027 }
                r2.<init>(r0, r1)     // Catch:{ NameNotFoundException -> 0x0027 }
                com.android.launcher3.AutoInstallsLayout r3 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x0027 }
                android.content.pm.PackageManager r3 = r3.mPackageManager     // Catch:{ NameNotFoundException -> 0x0027 }
                android.content.pm.ActivityInfo r3 = r3.getActivityInfo(r2, r9)     // Catch:{ NameNotFoundException -> 0x0027 }
                goto L_0x0046
            L_0x0027:
                com.android.launcher3.AutoInstallsLayout r2 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x0071 }
                android.content.pm.PackageManager r2 = r2.mPackageManager     // Catch:{ NameNotFoundException -> 0x0071 }
                r3 = 1
                java.lang.String[] r3 = new java.lang.String[r3]     // Catch:{ NameNotFoundException -> 0x0071 }
                r3[r9] = r0     // Catch:{ NameNotFoundException -> 0x0071 }
                java.lang.String[] r2 = r2.currentToCanonicalPackageNames(r3)     // Catch:{ NameNotFoundException -> 0x0071 }
                android.content.ComponentName r3 = new android.content.ComponentName     // Catch:{ NameNotFoundException -> 0x0071 }
                r2 = r2[r9]     // Catch:{ NameNotFoundException -> 0x0071 }
                r3.<init>(r2, r1)     // Catch:{ NameNotFoundException -> 0x0071 }
                com.android.launcher3.AutoInstallsLayout r2 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x0071 }
                android.content.pm.PackageManager r2 = r2.mPackageManager     // Catch:{ NameNotFoundException -> 0x0071 }
                android.content.pm.ActivityInfo r2 = r2.getActivityInfo(r3, r9)     // Catch:{ NameNotFoundException -> 0x0071 }
                r7 = r3
                r3 = r2
                r2 = r7
            L_0x0046:
                android.content.Intent r4 = new android.content.Intent     // Catch:{ NameNotFoundException -> 0x0071 }
                java.lang.String r5 = "android.intent.action.MAIN"
                r6 = 0
                r4.<init>(r5, r6)     // Catch:{ NameNotFoundException -> 0x0071 }
                java.lang.String r5 = "android.intent.category.LAUNCHER"
                android.content.Intent r4 = r4.addCategory(r5)     // Catch:{ NameNotFoundException -> 0x0071 }
                android.content.Intent r2 = r4.setComponent(r2)     // Catch:{ NameNotFoundException -> 0x0071 }
                r4 = 270532608(0x10200000, float:3.1554436E-29)
                android.content.Intent r2 = r2.setFlags(r4)     // Catch:{ NameNotFoundException -> 0x0071 }
                com.android.launcher3.AutoInstallsLayout r4 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x0071 }
                com.android.launcher3.AutoInstallsLayout r5 = com.android.launcher3.AutoInstallsLayout.this     // Catch:{ NameNotFoundException -> 0x0071 }
                android.content.pm.PackageManager r5 = r5.mPackageManager     // Catch:{ NameNotFoundException -> 0x0071 }
                java.lang.CharSequence r3 = r3.loadLabel(r5)     // Catch:{ NameNotFoundException -> 0x0071 }
                java.lang.String r3 = r3.toString()     // Catch:{ NameNotFoundException -> 0x0071 }
                long r2 = r4.addShortcut(r3, r2, r9)     // Catch:{ NameNotFoundException -> 0x0071 }
                return r2
            L_0x0071:
                java.lang.String r9 = "AutoInstalls"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Favorite not found: "
                r2.append(r3)
                r2.append(r0)
                java.lang.String r0 = "/"
                r2.append(r0)
                r2.append(r1)
                java.lang.String r0 = r2.toString()
                android.util.Log.e(r9, r0)
                r0 = -1
                return r0
            L_0x0092:
                long r0 = r8.invalidPackageOrClass(r9)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AutoInstallsLayout.AppShortcutParser.parseAndAdd(android.content.res.XmlResourceParser):long");
        }

        /* access modifiers changed from: protected */
        public long invalidPackageOrClass(XmlResourceParser xmlResourceParser) {
            Log.w(AutoInstallsLayout.TAG, "Skipping invalid <favorite> with no component");
            return -1;
        }
    }

    protected class AutoInstallParser implements TagParser {
        protected AutoInstallParser() {
        }

        public long parseAndAdd(XmlResourceParser xmlResourceParser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlResourceParser, AutoInstallsLayout.ATTR_PACKAGE_NAME);
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(xmlResourceParser, AutoInstallsLayout.ATTR_CLASS_NAME);
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                return -1;
            }
            AutoInstallsLayout.this.mValues.put(Favorites.RESTORED, Integer.valueOf(2));
            return AutoInstallsLayout.this.addShortcut(AutoInstallsLayout.this.mContext.getString(C0622R.string.package_state_unknown), new Intent("android.intent.action.MAIN", null).addCategory("android.intent.category.LAUNCHER").setComponent(new ComponentName(attributeValue, attributeValue2)).setFlags(270532608), 0);
        }
    }

    protected class FolderParser implements TagParser {
        private final ArrayMap<String, TagParser> mFolderElements;

        public FolderParser(AutoInstallsLayout autoInstallsLayout) {
            this(autoInstallsLayout.getFolderElementsMap());
        }

        public FolderParser(ArrayMap<String, TagParser> arrayMap) {
            this.mFolderElements = arrayMap;
        }

        public long parseAndAdd(XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
            String str;
            long j;
            XmlResourceParser xmlResourceParser2 = xmlResourceParser;
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(xmlResourceParser2, "title", 0);
            if (attributeResourceValue != 0) {
                str = AutoInstallsLayout.this.mSourceRes.getString(attributeResourceValue);
            } else {
                str = AutoInstallsLayout.this.mContext.getResources().getString(C0622R.string.folder_name);
            }
            AutoInstallsLayout.this.mValues.put("title", str);
            AutoInstallsLayout.this.mValues.put(BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(2));
            AutoInstallsLayout.this.mValues.put("spanX", Integer.valueOf(1));
            AutoInstallsLayout.this.mValues.put("spanY", Integer.valueOf(1));
            AutoInstallsLayout.this.mValues.put("_id", Long.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            long insertAndCheck = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (insertAndCheck < 0) {
                return -1;
            }
            ContentValues contentValues = new ContentValues(AutoInstallsLayout.this.mValues);
            ArrayList arrayList = new ArrayList();
            int depth = xmlResourceParser.getDepth();
            int i = 0;
            while (true) {
                int next = xmlResourceParser.next();
                if (next == 3 && xmlResourceParser.getDepth() <= depth) {
                    if (arrayList.size() < 2) {
                        SqlArguments sqlArguments = new SqlArguments(Favorites.getContentUri(insertAndCheck), null, null);
                        AutoInstallsLayout.this.mDb.delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
                        if (arrayList.size() == 1) {
                            ContentValues contentValues2 = new ContentValues();
                            AutoInstallsLayout.copyInteger(contentValues, contentValues2, "container");
                            AutoInstallsLayout.copyInteger(contentValues, contentValues2, "screen");
                            AutoInstallsLayout.copyInteger(contentValues, contentValues2, Favorites.CELLX);
                            AutoInstallsLayout.copyInteger(contentValues, contentValues2, Favorites.CELLY);
                            long longValue = ((Long) arrayList.get(0)).longValue();
                            SQLiteDatabase sQLiteDatabase = AutoInstallsLayout.this.mDb;
                            String str2 = Favorites.TABLE_NAME;
                            StringBuilder sb = new StringBuilder();
                            sb.append("_id=");
                            sb.append(longValue);
                            sQLiteDatabase.update(str2, contentValues2, sb.toString(), null);
                            j = longValue;
                        } else {
                            j = -1;
                        }
                    } else {
                        j = insertAndCheck;
                    }
                    return j;
                } else if (next == 2) {
                    AutoInstallsLayout.this.mValues.clear();
                    AutoInstallsLayout.this.mValues.put("container", Long.valueOf(insertAndCheck));
                    AutoInstallsLayout.this.mValues.put("rank", Integer.valueOf(i));
                    TagParser tagParser = (TagParser) this.mFolderElements.get(xmlResourceParser.getName());
                    if (tagParser != null) {
                        long parseAndAdd = tagParser.parseAndAdd(xmlResourceParser2);
                        if (parseAndAdd >= 0) {
                            arrayList.add(Long.valueOf(parseAndAdd));
                            i++;
                        }
                    } else {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Invalid folder item ");
                        sb2.append(xmlResourceParser.getName());
                        throw new RuntimeException(sb2.toString());
                    }
                }
            }
        }
    }

    public interface LayoutParserCallback {
        long generateNewItemId();

        long insertAndCheck(SQLiteDatabase sQLiteDatabase, ContentValues contentValues);
    }

    protected class PendingWidgetParser implements TagParser {
        protected PendingWidgetParser() {
        }

        public long parseAndAdd(XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlResourceParser, AutoInstallsLayout.ATTR_PACKAGE_NAME);
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(xmlResourceParser, AutoInstallsLayout.ATTR_CLASS_NAME);
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                return -1;
            }
            AutoInstallsLayout.this.mValues.put("spanX", AutoInstallsLayout.getAttributeValue(xmlResourceParser, "spanX"));
            AutoInstallsLayout.this.mValues.put("spanY", AutoInstallsLayout.getAttributeValue(xmlResourceParser, "spanY"));
            AutoInstallsLayout.this.mValues.put(BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(4));
            Bundle bundle = new Bundle();
            int depth = xmlResourceParser.getDepth();
            while (true) {
                int next = xmlResourceParser.next();
                if (next == 3 && xmlResourceParser.getDepth() <= depth) {
                    return verifyAndInsert(new ComponentName(attributeValue, attributeValue2), bundle);
                }
                if (next == 2) {
                    if (AutoInstallsLayout.TAG_EXTRA.equals(xmlResourceParser.getName())) {
                        String attributeValue3 = AutoInstallsLayout.getAttributeValue(xmlResourceParser, AutoInstallsLayout.ATTR_KEY);
                        String attributeValue4 = AutoInstallsLayout.getAttributeValue(xmlResourceParser, "value");
                        if (attributeValue3 != null && attributeValue4 != null) {
                            bundle.putString(attributeValue3, attributeValue4);
                        }
                    } else {
                        throw new RuntimeException("Widgets can contain only extras");
                    }
                }
            }
            throw new RuntimeException("Widget extras must have a key and value");
        }

        /* access modifiers changed from: protected */
        public long verifyAndInsert(ComponentName componentName, Bundle bundle) {
            AutoInstallsLayout.this.mValues.put(Favorites.APPWIDGET_PROVIDER, componentName.flattenToString());
            AutoInstallsLayout.this.mValues.put(Favorites.RESTORED, Integer.valueOf(35));
            AutoInstallsLayout.this.mValues.put("_id", Long.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            if (!bundle.isEmpty()) {
                AutoInstallsLayout.this.mValues.put(BaseLauncherColumns.INTENT, new Intent().putExtras(bundle).toUri(0));
            }
            long insertAndCheck = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (insertAndCheck < 0) {
                return -1;
            }
            return insertAndCheck;
        }
    }

    protected class ShortcutParser implements TagParser {
        private final Resources mIconRes;

        public ShortcutParser(Resources resources) {
            this.mIconRes = resources;
        }

        public long parseAndAdd(XmlResourceParser xmlResourceParser) {
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(xmlResourceParser, "title", 0);
            int attributeResourceValue2 = AutoInstallsLayout.getAttributeResourceValue(xmlResourceParser, "icon", 0);
            if (attributeResourceValue == 0 || attributeResourceValue2 == 0) {
                return -1;
            }
            Intent parseIntent = parseIntent(xmlResourceParser);
            if (parseIntent == null) {
                return -1;
            }
            Drawable drawable = this.mIconRes.getDrawable(attributeResourceValue2);
            if (drawable == null) {
                return -1;
            }
            AutoInstallsLayout.this.mValues.put("icon", Utilities.flattenBitmap(LauncherIcons.createIconBitmap(drawable, AutoInstallsLayout.this.mContext)));
            AutoInstallsLayout.this.mValues.put(BaseLauncherColumns.ICON_PACKAGE, this.mIconRes.getResourcePackageName(attributeResourceValue2));
            AutoInstallsLayout.this.mValues.put(BaseLauncherColumns.ICON_RESOURCE, this.mIconRes.getResourceName(attributeResourceValue2));
            parseIntent.setFlags(270532608);
            return AutoInstallsLayout.this.addShortcut(AutoInstallsLayout.this.mSourceRes.getString(attributeResourceValue), parseIntent, 1);
        }

        /* access modifiers changed from: protected */
        public Intent parseIntent(XmlResourceParser xmlResourceParser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlResourceParser, AutoInstallsLayout.ATTR_URL);
            if (TextUtils.isEmpty(attributeValue) || !Patterns.WEB_URL.matcher(attributeValue).matches()) {
                return null;
            }
            return new Intent("android.intent.action.VIEW", null).setData(Uri.parse(attributeValue));
        }
    }

    protected interface TagParser {
        long parseAndAdd(XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException;
    }

    static AutoInstallsLayout get(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback layoutParserCallback) {
        Pair findSystemApk = Utilities.findSystemApk(ACTION_LAUNCHER_CUSTOMIZATION, context.getPackageManager());
        if (findSystemApk == null) {
            return null;
        }
        return get(context, (String) findSystemApk.first, (Resources) findSystemApk.second, appWidgetHost, layoutParserCallback);
    }

    static AutoInstallsLayout get(Context context, String str, Resources resources, AppWidgetHost appWidgetHost, LayoutParserCallback layoutParserCallback) {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        String format = String.format(Locale.ENGLISH, FORMATTED_LAYOUT_RES_WITH_HOSTEAT, new Object[]{Integer.valueOf(idp.numColumns), Integer.valueOf(idp.numRows), Integer.valueOf(idp.numHotseatIcons)});
        int identifier = resources.getIdentifier(format, "xml", str);
        if (identifier == 0) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Formatted layout: ");
            sb.append(format);
            sb.append(" not found. Trying layout without hosteat");
            Log.d(str2, sb.toString());
            format = String.format(Locale.ENGLISH, FORMATTED_LAYOUT_RES, new Object[]{Integer.valueOf(idp.numColumns), Integer.valueOf(idp.numRows)});
            identifier = resources.getIdentifier(format, "xml", str);
        }
        if (identifier == 0) {
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Formatted layout: ");
            sb2.append(format);
            sb2.append(" not found. Trying the default layout");
            Log.d(str3, sb2.toString());
            identifier = resources.getIdentifier(LAYOUT_RES, "xml", str);
        }
        int i = identifier;
        if (i == 0) {
            String str4 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Layout definition not found in package: ");
            sb3.append(str);
            Log.e(str4, sb3.toString());
            return null;
        }
        AutoInstallsLayout autoInstallsLayout = new AutoInstallsLayout(context, appWidgetHost, layoutParserCallback, resources, i, "workspace");
        return autoInstallsLayout;
    }

    public AutoInstallsLayout(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback layoutParserCallback, Resources resources, int i, String str) {
        this.mContext = context;
        this.mAppWidgetHost = appWidgetHost;
        this.mCallback = layoutParserCallback;
        this.mPackageManager = context.getPackageManager();
        this.mValues = new ContentValues();
        this.mRootTag = str;
        this.mSourceRes = resources;
        this.mLayoutId = i;
        this.mIdp = LauncherAppState.getIDP(context);
        this.mRowCount = this.mIdp.numRows;
        this.mColumnCount = this.mIdp.numColumns;
    }

    public int loadLayout(SQLiteDatabase sQLiteDatabase, ArrayList<Long> arrayList) {
        this.mDb = sQLiteDatabase;
        try {
            return parseLayout(this.mLayoutId, arrayList);
        } catch (Exception e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Error parsing layout: ");
            sb.append(e);
            Log.e(str, sb.toString());
            return -1;
        }
    }

    /* access modifiers changed from: protected */
    public int parseLayout(int i, ArrayList<Long> arrayList) throws XmlPullParserException, IOException {
        XmlResourceParser xml = this.mSourceRes.getXml(i);
        beginDocument(xml, this.mRootTag);
        int depth = xml.getDepth();
        ArrayMap layoutElementsMap = getLayoutElementsMap();
        int i2 = 0;
        while (true) {
            int next = xml.next();
            if ((next != 3 || xml.getDepth() > depth) && next != 1) {
                if (next == 2) {
                    i2 += parseAndAddNode(xml, layoutElementsMap, arrayList);
                }
            }
        }
        return i2;
    }

    /* access modifiers changed from: protected */
    public void parseContainerAndScreen(XmlResourceParser xmlResourceParser, long[] jArr) {
        if (HOTSEAT_CONTAINER_NAME.equals(getAttributeValue(xmlResourceParser, "container"))) {
            jArr[0] = -101;
            jArr[1] = Long.parseLong(getAttributeValue(xmlResourceParser, "rank"));
            return;
        }
        jArr[0] = -100;
        jArr[1] = Long.parseLong(getAttributeValue(xmlResourceParser, "screen"));
    }

    /* access modifiers changed from: protected */
    public int parseAndAddNode(XmlResourceParser xmlResourceParser, ArrayMap<String, TagParser> arrayMap, ArrayList<Long> arrayList) throws XmlPullParserException, IOException {
        if (TAG_INCLUDE.equals(xmlResourceParser.getName())) {
            int attributeResourceValue = getAttributeResourceValue(xmlResourceParser, "workspace", 0);
            if (attributeResourceValue != 0) {
                return parseLayout(attributeResourceValue, arrayList);
            }
            return 0;
        }
        this.mValues.clear();
        parseContainerAndScreen(xmlResourceParser, this.mTemp);
        long j = this.mTemp[0];
        long j2 = this.mTemp[1];
        this.mValues.put("container", Long.valueOf(j));
        this.mValues.put("screen", Long.valueOf(j2));
        this.mValues.put(Favorites.CELLX, convertToDistanceFromEnd(getAttributeValue(xmlResourceParser, ATTR_X), this.mColumnCount));
        this.mValues.put(Favorites.CELLY, convertToDistanceFromEnd(getAttributeValue(xmlResourceParser, ATTR_Y), this.mRowCount));
        TagParser tagParser = (TagParser) arrayMap.get(xmlResourceParser.getName());
        if (tagParser == null || tagParser.parseAndAdd(xmlResourceParser) < 0) {
            return 0;
        }
        if (!arrayList.contains(Long.valueOf(j2)) && j == -100) {
            arrayList.add(Long.valueOf(j2));
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    public long addShortcut(String str, Intent intent, int i) {
        long generateNewItemId = this.mCallback.generateNewItemId();
        this.mValues.put(BaseLauncherColumns.INTENT, intent.toUri(0));
        this.mValues.put("title", str);
        this.mValues.put(BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(i));
        this.mValues.put("spanX", Integer.valueOf(1));
        this.mValues.put("spanY", Integer.valueOf(1));
        this.mValues.put("_id", Long.valueOf(generateNewItemId));
        if (this.mCallback.insertAndCheck(this.mDb, this.mValues) < 0) {
            return -1;
        }
        return generateNewItemId;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, TagParser> getFolderElementsMap() {
        ArrayMap<String, TagParser> arrayMap = new ArrayMap<>();
        arrayMap.put(TAG_APP_ICON, new AppShortcutParser());
        arrayMap.put(TAG_AUTO_INSTALL, new AutoInstallParser());
        arrayMap.put(TAG_SHORTCUT, new ShortcutParser(this.mSourceRes));
        return arrayMap;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, TagParser> getLayoutElementsMap() {
        ArrayMap<String, TagParser> arrayMap = new ArrayMap<>();
        arrayMap.put(TAG_APP_ICON, new AppShortcutParser());
        arrayMap.put(TAG_AUTO_INSTALL, new AutoInstallParser());
        arrayMap.put(TAG_FOLDER, new FolderParser(this));
        arrayMap.put(TAG_APPWIDGET, new PendingWidgetParser());
        arrayMap.put(TAG_SHORTCUT, new ShortcutParser(this.mSourceRes));
        return arrayMap;
    }

    protected static void beginDocument(XmlPullParser xmlPullParser, String str) throws XmlPullParserException, IOException {
        int next;
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            throw new XmlPullParserException("No start tag found");
        } else if (!xmlPullParser.getName().equals(str)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unexpected start tag: found ");
            sb.append(xmlPullParser.getName());
            sb.append(", expected ");
            sb.append(str);
            throw new XmlPullParserException(sb.toString());
        }
    }

    private static String convertToDistanceFromEnd(String str, int i) {
        if (!TextUtils.isEmpty(str)) {
            int parseInt = Integer.parseInt(str);
            if (parseInt < 0) {
                return Integer.toString(i + parseInt);
            }
        }
        return str;
    }

    protected static String getAttributeValue(XmlResourceParser xmlResourceParser, String str) {
        String attributeValue = xmlResourceParser.getAttributeValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", str);
        return attributeValue == null ? xmlResourceParser.getAttributeValue(null, str) : attributeValue;
    }

    protected static int getAttributeResourceValue(XmlResourceParser xmlResourceParser, String str, int i) {
        int attributeResourceValue = xmlResourceParser.getAttributeResourceValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", str, i);
        return attributeResourceValue == i ? xmlResourceParser.getAttributeResourceValue(null, str, i) : attributeResourceValue;
    }

    static void copyInteger(ContentValues contentValues, ContentValues contentValues2, String str) {
        contentValues2.put(str, contentValues.getAsInteger(str));
    }
}
