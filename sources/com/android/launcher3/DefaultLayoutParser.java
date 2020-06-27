package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.launcher3.AutoInstallsLayout.LayoutParserCallback;
import com.android.launcher3.LauncherSettings.Favorites;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DefaultLayoutParser extends AutoInstallsLayout {
    private static final String ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE = "com.android.launcher.action.APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE";
    private static final String ATTR_CONTAINER = "container";
    private static final String ATTR_FOLDER_ITEMS = "folderItems";
    private static final String ATTR_SCREEN = "screen";
    protected static final String ATTR_URI = "uri";
    private static final String TAG = "DefaultLayoutParser";
    private static final String TAG_APPWIDGET = "appwidget";
    protected static final String TAG_FAVORITE = "favorite";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_FOLDER = "folder";
    private static final String TAG_PARTNER_FOLDER = "partner-folder";
    protected static final String TAG_RESOLVE = "resolve";
    protected static final String TAG_SHORTCUT = "shortcut";

    public class AppShortcutWithUriParser extends AppShortcutParser {
        public AppShortcutWithUriParser() {
            super();
        }

        public /* bridge */ /* synthetic */ long parseAndAdd(XmlResourceParser xmlResourceParser) {
            return super.parseAndAdd(xmlResourceParser);
        }

        /* access modifiers changed from: protected */
        public long invalidPackageOrClass(XmlResourceParser xmlResourceParser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(xmlResourceParser, DefaultLayoutParser.ATTR_URI);
            if (TextUtils.isEmpty(attributeValue)) {
                Log.e(DefaultLayoutParser.TAG, "Skipping invalid <favorite> with no component or uri");
                return -1;
            }
            try {
                Intent parseUri = Intent.parseUri(attributeValue, 0);
                ResolveInfo resolveActivity = DefaultLayoutParser.this.mPackageManager.resolveActivity(parseUri, 65536);
                List queryIntentActivities = DefaultLayoutParser.this.mPackageManager.queryIntentActivities(parseUri, 65536);
                if (wouldLaunchResolverActivity(resolveActivity, queryIntentActivities)) {
                    resolveActivity = getSingleSystemActivity(queryIntentActivities);
                    if (resolveActivity == null) {
                        String str = DefaultLayoutParser.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("No preference or single system activity found for ");
                        sb.append(parseUri.toString());
                        Log.w(str, sb.toString());
                        return -1;
                    }
                }
                ActivityInfo activityInfo = resolveActivity.activityInfo;
                Intent launchIntentForPackage = DefaultLayoutParser.this.mPackageManager.getLaunchIntentForPackage(activityInfo.packageName);
                if (launchIntentForPackage == null) {
                    return -1;
                }
                launchIntentForPackage.setFlags(270532608);
                return DefaultLayoutParser.this.addShortcut(activityInfo.loadLabel(DefaultLayoutParser.this.mPackageManager).toString(), launchIntentForPackage, 0);
            } catch (URISyntaxException e) {
                String str2 = DefaultLayoutParser.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Unable to add meta-favorite: ");
                sb2.append(attributeValue);
                Log.e(str2, sb2.toString(), e);
                return -1;
            }
        }

        private ResolveInfo getSingleSystemActivity(List<ResolveInfo> list) {
            int size = list.size();
            ResolveInfo resolveInfo = null;
            int i = 0;
            while (i < size) {
                try {
                    if ((DefaultLayoutParser.this.mPackageManager.getApplicationInfo(((ResolveInfo) list.get(i)).activityInfo.packageName, 0).flags & 1) != 0) {
                        if (resolveInfo != null) {
                            return null;
                        }
                        resolveInfo = (ResolveInfo) list.get(i);
                    }
                    i++;
                } catch (NameNotFoundException e) {
                    Log.w(DefaultLayoutParser.TAG, "Unable to get info about resolve results", e);
                    return null;
                }
            }
            return resolveInfo;
        }

        private boolean wouldLaunchResolverActivity(ResolveInfo resolveInfo, List<ResolveInfo> list) {
            for (int i = 0; i < list.size(); i++) {
                ResolveInfo resolveInfo2 = (ResolveInfo) list.get(i);
                if (resolveInfo2.activityInfo.name.equals(resolveInfo.activityInfo.name) && resolveInfo2.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName)) {
                    return false;
                }
            }
            return true;
        }
    }

    protected class AppWidgetParser extends PendingWidgetParser {
        protected AppWidgetParser() {
            super();
        }

        /* access modifiers changed from: protected */
        public long verifyAndInsert(ComponentName componentName, Bundle bundle) {
            long j;
            try {
                DefaultLayoutParser.this.mPackageManager.getReceiverInfo(componentName, 0);
            } catch (Exception unused) {
                ComponentName componentName2 = new ComponentName(DefaultLayoutParser.this.mPackageManager.currentToCanonicalPackageNames(new String[]{componentName.getPackageName()})[0], componentName.getClassName());
                try {
                    DefaultLayoutParser.this.mPackageManager.getReceiverInfo(componentName2, 0);
                    componentName = componentName2;
                } catch (Exception unused2) {
                    String str = DefaultLayoutParser.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Can't find widget provider: ");
                    sb.append(componentName2.getClassName());
                    Log.d(str, sb.toString());
                    return -1;
                }
            }
            AppWidgetManager instance = AppWidgetManager.getInstance(DefaultLayoutParser.this.mContext);
            try {
                int allocateAppWidgetId = DefaultLayoutParser.this.mAppWidgetHost.allocateAppWidgetId();
                if (!instance.bindAppWidgetIdIfAllowed(allocateAppWidgetId, componentName)) {
                    String str2 = DefaultLayoutParser.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Unable to bind app widget id ");
                    sb2.append(componentName);
                    Log.e(str2, sb2.toString());
                    DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(allocateAppWidgetId);
                    return -1;
                }
                DefaultLayoutParser.this.mValues.put(Favorites.APPWIDGET_ID, Integer.valueOf(allocateAppWidgetId));
                DefaultLayoutParser.this.mValues.put(Favorites.APPWIDGET_PROVIDER, componentName.flattenToString());
                DefaultLayoutParser.this.mValues.put("_id", Long.valueOf(DefaultLayoutParser.this.mCallback.generateNewItemId()));
                j = DefaultLayoutParser.this.mCallback.insertAndCheck(DefaultLayoutParser.this.mDb, DefaultLayoutParser.this.mValues);
                if (j < 0) {
                    try {
                        DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(allocateAppWidgetId);
                        return j;
                    } catch (RuntimeException e) {
                        e = e;
                        Log.e(DefaultLayoutParser.TAG, "Problem allocating appWidgetId", e);
                        return j;
                    }
                } else {
                    if (!bundle.isEmpty()) {
                        Intent intent = new Intent(DefaultLayoutParser.ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE);
                        intent.setComponent(componentName);
                        intent.putExtras(bundle);
                        intent.putExtra(Favorites.APPWIDGET_ID, allocateAppWidgetId);
                        DefaultLayoutParser.this.mContext.sendBroadcast(intent);
                    }
                    return j;
                }
            } catch (RuntimeException e2) {
                e = e2;
                j = -1;
                Log.e(DefaultLayoutParser.TAG, "Problem allocating appWidgetId", e);
                return j;
            }
        }
    }

    class MyFolderParser extends FolderParser {
        MyFolderParser() {
            super(DefaultLayoutParser.this);
        }

        public long parseAndAdd(XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(xmlResourceParser, DefaultLayoutParser.ATTR_FOLDER_ITEMS, 0);
            if (attributeResourceValue != 0) {
                xmlResourceParser = DefaultLayoutParser.this.mSourceRes.getXml(attributeResourceValue);
                AutoInstallsLayout.beginDocument(xmlResourceParser, DefaultLayoutParser.TAG_FOLDER);
            }
            return super.parseAndAdd(xmlResourceParser);
        }
    }

    class PartnerFolderParser implements TagParser {
        PartnerFolderParser() {
        }

        public long parseAndAdd(XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
            Partner partner = Partner.get(DefaultLayoutParser.this.mPackageManager);
            if (partner != null) {
                Resources resources = partner.getResources();
                int identifier = resources.getIdentifier(Partner.RES_FOLDER, "xml", partner.getPackageName());
                if (identifier != 0) {
                    XmlResourceParser xml = resources.getXml(identifier);
                    AutoInstallsLayout.beginDocument(xml, DefaultLayoutParser.TAG_FOLDER);
                    return new FolderParser(DefaultLayoutParser.this.getFolderElementsMap(resources)).parseAndAdd(xml);
                }
            }
            return -1;
        }
    }

    public class ResolveParser implements TagParser {
        private final AppShortcutWithUriParser mChildParser = new AppShortcutWithUriParser();

        public ResolveParser() {
        }

        public long parseAndAdd(XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
            int depth = xmlResourceParser.getDepth();
            long j = -1;
            while (true) {
                int next = xmlResourceParser.next();
                if (next == 3 && xmlResourceParser.getDepth() <= depth) {
                    return j;
                }
                if (next == 2 && j <= -1) {
                    String name = xmlResourceParser.getName();
                    if (DefaultLayoutParser.TAG_FAVORITE.equals(name)) {
                        j = this.mChildParser.parseAndAdd(xmlResourceParser);
                    } else {
                        String str = DefaultLayoutParser.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Fallback groups can contain only favorites, found ");
                        sb.append(name);
                        Log.e(str, sb.toString());
                    }
                }
            }
        }
    }

    public class UriShortcutParser extends ShortcutParser {
        public /* bridge */ /* synthetic */ long parseAndAdd(XmlResourceParser xmlResourceParser) {
            return super.parseAndAdd(xmlResourceParser);
        }

        public UriShortcutParser(Resources resources) {
            super(resources);
        }

        /* access modifiers changed from: protected */
        public Intent parseIntent(XmlResourceParser xmlResourceParser) {
            String str;
            try {
                str = AutoInstallsLayout.getAttributeValue(xmlResourceParser, DefaultLayoutParser.ATTR_URI);
                try {
                    return Intent.parseUri(str, 0);
                } catch (URISyntaxException unused) {
                    String str2 = DefaultLayoutParser.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Shortcut has malformed uri: ");
                    sb.append(str);
                    Log.w(str2, sb.toString());
                    return null;
                }
            } catch (URISyntaxException unused2) {
                str = null;
                String str22 = DefaultLayoutParser.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Shortcut has malformed uri: ");
                sb2.append(str);
                Log.w(str22, sb2.toString());
                return null;
            }
        }
    }

    public DefaultLayoutParser(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback layoutParserCallback, Resources resources, int i) {
        super(context, appWidgetHost, layoutParserCallback, resources, i, "favorites");
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, TagParser> getFolderElementsMap() {
        return getFolderElementsMap(this.mSourceRes);
    }

    /* access modifiers changed from: 0000 */
    public ArrayMap<String, TagParser> getFolderElementsMap(Resources resources) {
        ArrayMap<String, TagParser> arrayMap = new ArrayMap<>();
        arrayMap.put(TAG_FAVORITE, new AppShortcutWithUriParser());
        arrayMap.put(TAG_SHORTCUT, new UriShortcutParser(resources));
        return arrayMap;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, TagParser> getLayoutElementsMap() {
        ArrayMap<String, TagParser> arrayMap = new ArrayMap<>();
        arrayMap.put(TAG_FAVORITE, new AppShortcutWithUriParser());
        arrayMap.put(TAG_APPWIDGET, new AppWidgetParser());
        arrayMap.put(TAG_SHORTCUT, new UriShortcutParser(this.mSourceRes));
        arrayMap.put(TAG_RESOLVE, new ResolveParser());
        arrayMap.put(TAG_FOLDER, new MyFolderParser());
        arrayMap.put(TAG_PARTNER_FOLDER, new PartnerFolderParser());
        return arrayMap;
    }

    /* access modifiers changed from: protected */
    public void parseContainerAndScreen(XmlResourceParser xmlResourceParser, long[] jArr) {
        jArr[0] = -100;
        String attributeValue = getAttributeValue(xmlResourceParser, "container");
        if (attributeValue != null) {
            jArr[0] = Long.valueOf(attributeValue).longValue();
        }
        jArr[1] = Long.parseLong(getAttributeValue(xmlResourceParser, "screen"));
    }
}
