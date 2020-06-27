package com.android.launcher3;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.Provider;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class InstallShortcutReceiver extends BroadcastReceiver {
    private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String APPS_PENDING_INSTALL = "apps_to_install";
    private static final String APP_SHORTCUT_TYPE_KEY = "isAppShortcut";
    private static final String APP_WIDGET_TYPE_KEY = "isAppWidget";
    private static final boolean DBG = false;
    private static final String DEEPSHORTCUT_TYPE_KEY = "isDeepShortcut";
    public static final int FLAG_ACTIVITY_PAUSED = 1;
    public static final int FLAG_BULK_ADD = 4;
    public static final int FLAG_DRAG_AND_DROP = 4;
    public static final int FLAG_LOADER_RUNNING = 2;
    private static final String ICON_KEY = "icon";
    private static final String ICON_RESOURCE_NAME_KEY = "iconResource";
    private static final String ICON_RESOURCE_PACKAGE_NAME_KEY = "iconResourcePackage";
    private static final String LAUNCH_INTENT_KEY = "intent.launch";
    private static final String NAME_KEY = "name";
    public static final int NEW_SHORTCUT_BOUNCE_DURATION = 450;
    public static final int NEW_SHORTCUT_STAGGER_DELAY = 85;
    private static final String TAG = "InstallShortcutReceiver";
    private static final String USER_HANDLE_KEY = "userHandle";
    private static int sInstallQueueDisabledFlags;
    private static final Object sLock = new Object();

    private static class Decoder extends JSONObject {
        public final Intent launcherIntent;
        public final UserHandle user;

        private Decoder(String str, Context context) throws JSONException, URISyntaxException {
            UserHandle userHandle;
            super(str);
            this.launcherIntent = Intent.parseUri(getString(InstallShortcutReceiver.LAUNCH_INTENT_KEY), 0);
            if (has(InstallShortcutReceiver.USER_HANDLE_KEY)) {
                userHandle = UserManagerCompat.getInstance(context).getUserForSerialNumber(getLong(InstallShortcutReceiver.USER_HANDLE_KEY));
            } else {
                userHandle = Process.myUserHandle();
            }
            this.user = userHandle;
            if (this.user == null) {
                throw new JSONException("Invalid user");
            }
        }
    }

    private static class LazyShortcutsProvider extends Provider<List<Pair<ItemInfo, Object>>> {
        private final Context mContext;
        private final ArrayList<PendingInstallShortcutInfo> mPendingItems;

        public LazyShortcutsProvider(Context context, ArrayList<PendingInstallShortcutInfo> arrayList) {
            this.mContext = context;
            this.mPendingItems = arrayList;
        }

        public ArrayList<Pair<ItemInfo, Object>> get() {
            Preconditions.assertNonUiThread();
            ArrayList<Pair<ItemInfo, Object>> arrayList = new ArrayList<>();
            LauncherAppsCompat instance = LauncherAppsCompat.getInstance(this.mContext);
            Iterator it = this.mPendingItems.iterator();
            while (it.hasNext()) {
                PendingInstallShortcutInfo pendingInstallShortcutInfo = (PendingInstallShortcutInfo) it.next();
                String access$200 = InstallShortcutReceiver.getIntentPackage(pendingInstallShortcutInfo.launchIntent);
                if (TextUtils.isEmpty(access$200) || instance.isPackageEnabledForProfile(access$200, pendingInstallShortcutInfo.user)) {
                    arrayList.add(pendingInstallShortcutInfo.getItemInfo());
                }
            }
            return arrayList;
        }
    }

    private static class PendingInstallShortcutInfo {
        final LauncherActivityInfo activityInfo;
        final Intent data;
        final String label;
        final Intent launchIntent;
        final Context mContext;
        final AppWidgetProviderInfo providerInfo;
        final ShortcutInfoCompat shortcutInfo;
        final UserHandle user;

        public PendingInstallShortcutInfo(Intent intent, UserHandle userHandle, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = null;
            this.providerInfo = null;
            this.data = intent;
            this.user = userHandle;
            this.mContext = context;
            this.launchIntent = (Intent) intent.getParcelableExtra("android.intent.extra.shortcut.INTENT");
            this.label = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        }

        public PendingInstallShortcutInfo(LauncherActivityInfo launcherActivityInfo, Context context) {
            this.activityInfo = launcherActivityInfo;
            this.shortcutInfo = null;
            this.providerInfo = null;
            this.data = null;
            this.user = launcherActivityInfo.getUser();
            this.mContext = context;
            this.launchIntent = AppInfo.makeLaunchIntent(launcherActivityInfo);
            this.label = launcherActivityInfo.getLabel().toString();
        }

        public PendingInstallShortcutInfo(ShortcutInfoCompat shortcutInfoCompat, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = shortcutInfoCompat;
            this.providerInfo = null;
            this.data = null;
            this.mContext = context;
            this.user = shortcutInfoCompat.getUserHandle();
            this.launchIntent = shortcutInfoCompat.makeIntent();
            this.label = shortcutInfoCompat.getShortLabel().toString();
        }

        public PendingInstallShortcutInfo(AppWidgetProviderInfo appWidgetProviderInfo, int i, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = null;
            this.providerInfo = appWidgetProviderInfo;
            this.data = null;
            this.mContext = context;
            this.user = appWidgetProviderInfo.getProfile();
            this.launchIntent = new Intent().setComponent(appWidgetProviderInfo.provider).putExtra(Favorites.APPWIDGET_ID, i);
            this.label = appWidgetProviderInfo.label;
        }

        public String encodeToString() {
            try {
                if (this.activityInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.APP_SHORTCUT_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).endObject().toString();
                }
                if (this.shortcutInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.DEEPSHORTCUT_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).endObject().toString();
                }
                if (this.providerInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.APP_WIDGET_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).endObject().toString();
                }
                if (this.launchIntent.getAction() == null) {
                    this.launchIntent.setAction("android.intent.action.VIEW");
                } else if (this.launchIntent.getAction().equals("android.intent.action.MAIN") && this.launchIntent.getCategories() != null && this.launchIntent.getCategories().contains("android.intent.category.LAUNCHER")) {
                    this.launchIntent.addFlags(270532608);
                }
                Bitmap bitmap = (Bitmap) this.data.getParcelableExtra("android.intent.extra.shortcut.ICON");
                ShortcutIconResource shortcutIconResource = (ShortcutIconResource) this.data.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
                JSONStringer value = new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key("name").value(InstallShortcutReceiver.ensureValidName(this.mContext, this.launchIntent, this.label).toString());
                if (bitmap != null) {
                    byte[] flattenBitmap = Utilities.flattenBitmap(bitmap);
                    value = value.key("icon").value(Base64.encodeToString(flattenBitmap, 0, flattenBitmap.length, 0));
                }
                if (shortcutIconResource != null) {
                    value = value.key("iconResource").value(shortcutIconResource.resourceName).key(InstallShortcutReceiver.ICON_RESOURCE_PACKAGE_NAME_KEY).value(shortcutIconResource.packageName);
                }
                return value.endObject().toString();
            } catch (JSONException e) {
                String str = InstallShortcutReceiver.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Exception when adding shortcut: ");
                sb.append(e);
                Log.d(str, sb.toString());
                return null;
            }
        }

        public Pair<ItemInfo, Object> getItemInfo() {
            if (this.activityInfo != null) {
                AppInfo appInfo = new AppInfo(this.mContext, this.activityInfo, this.user);
                final LauncherAppState instance = LauncherAppState.getInstance(this.mContext);
                appInfo.title = "";
                appInfo.iconBitmap = instance.getIconCache().getDefaultIcon(this.user);
                final ShortcutInfo makeShortcut = appInfo.makeShortcut();
                if (Looper.myLooper() == LauncherModel.getWorkerLooper()) {
                    instance.getIconCache().getTitleAndIcon(makeShortcut, this.activityInfo, false);
                } else {
                    instance.getModel().updateAndBindShortcutInfo(new Provider<ShortcutInfo>() {
                        public ShortcutInfo get() {
                            instance.getIconCache().getTitleAndIcon(makeShortcut, PendingInstallShortcutInfo.this.activityInfo, false);
                            return makeShortcut;
                        }
                    });
                }
                return Pair.create(makeShortcut, this.activityInfo);
            } else if (this.shortcutInfo != null) {
                ShortcutInfo shortcutInfo2 = new ShortcutInfo(this.shortcutInfo, this.mContext);
                shortcutInfo2.iconBitmap = LauncherIcons.createShortcutIcon(this.shortcutInfo, this.mContext);
                return Pair.create(shortcutInfo2, this.shortcutInfo);
            } else if (this.providerInfo == null) {
                return Pair.create(InstallShortcutReceiver.createShortcutInfo(this.data, LauncherAppState.getInstance(this.mContext)), null);
            } else {
                LauncherAppWidgetProviderInfo fromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, this.providerInfo);
                LauncherAppWidgetInfo launcherAppWidgetInfo = new LauncherAppWidgetInfo(this.launchIntent.getIntExtra(Favorites.APPWIDGET_ID, 0), fromProviderInfo.provider);
                InvariantDeviceProfile idp = LauncherAppState.getIDP(this.mContext);
                launcherAppWidgetInfo.minSpanX = fromProviderInfo.minSpanX;
                launcherAppWidgetInfo.minSpanY = fromProviderInfo.minSpanY;
                launcherAppWidgetInfo.spanX = Math.min(fromProviderInfo.spanX, idp.numColumns);
                launcherAppWidgetInfo.spanY = Math.min(fromProviderInfo.spanY, idp.numRows);
                return Pair.create(launcherAppWidgetInfo, this.providerInfo);
            }
        }

        public boolean isLauncherActivity() {
            return this.activityInfo != null;
        }
    }

    private static void addToInstallQueue(SharedPreferences sharedPreferences, PendingInstallShortcutInfo pendingInstallShortcutInfo) {
        synchronized (sLock) {
            String encodeToString = pendingInstallShortcutInfo.encodeToString();
            if (encodeToString != null) {
                Set stringSet = sharedPreferences.getStringSet(APPS_PENDING_INSTALL, null);
                HashSet hashSet = stringSet != null ? new HashSet(stringSet) : new HashSet(1);
                hashSet.add(encodeToString);
                sharedPreferences.edit().putStringSet(APPS_PENDING_INSTALL, hashSet).apply();
            }
        }
    }

    public static void removeFromInstallQueue(Context context, HashSet<String> hashSet, UserHandle userHandle) {
        if (!hashSet.isEmpty()) {
            SharedPreferences prefs = Utilities.getPrefs(context);
            synchronized (sLock) {
                Set stringSet = prefs.getStringSet(APPS_PENDING_INSTALL, null);
                if (!Utilities.isEmpty(stringSet)) {
                    HashSet hashSet2 = new HashSet(stringSet);
                    Iterator it = hashSet2.iterator();
                    while (it.hasNext()) {
                        try {
                            Decoder decoder = new Decoder((String) it.next(), context);
                            if (hashSet.contains(getIntentPackage(decoder.launcherIntent)) && userHandle.equals(decoder.user)) {
                                it.remove();
                            }
                        } catch (URISyntaxException | JSONException e) {
                            String str = TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Exception reading shortcut to add: ");
                            sb.append(e);
                            Log.d(str, sb.toString());
                            it.remove();
                        }
                    }
                    prefs.edit().putStringSet(APPS_PENDING_INSTALL, hashSet2).apply();
                }
            }
        }
    }

    private static ArrayList<PendingInstallShortcutInfo> getAndClearInstallQueue(Context context) {
        SharedPreferences prefs = Utilities.getPrefs(context);
        synchronized (sLock) {
            ArrayList<PendingInstallShortcutInfo> arrayList = new ArrayList<>();
            Set<String> stringSet = prefs.getStringSet(APPS_PENDING_INSTALL, null);
            if (stringSet == null) {
                return arrayList;
            }
            for (String decode : stringSet) {
                PendingInstallShortcutInfo decode2 = decode(decode, context);
                if (decode2 != null) {
                    arrayList.add(decode2);
                }
            }
            prefs.edit().putStringSet(APPS_PENDING_INSTALL, new HashSet()).apply();
            return arrayList;
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (ACTION_INSTALL_SHORTCUT.equals(intent.getAction())) {
            PendingInstallShortcutInfo createPendingInfo = createPendingInfo(context, intent);
            if (createPendingInfo != null) {
                if (createPendingInfo.isLauncherActivity() || new PackageManagerHelper(context).hasPermissionForActivity(createPendingInfo.launchIntent, null)) {
                    queuePendingShortcutInfo(createPendingInfo, context);
                } else {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ignoring malicious intent ");
                    sb.append(createPendingInfo.launchIntent.toUri(0));
                    Log.e(str, sb.toString());
                }
            }
        }
    }

    private static boolean isValidExtraType(Intent intent, String str, Class cls) {
        Parcelable parcelableExtra = intent.getParcelableExtra(str);
        return parcelableExtra == null || cls.isInstance(parcelableExtra);
    }

    private static PendingInstallShortcutInfo createPendingInfo(Context context, Intent intent) {
        if (!isValidExtraType(intent, "android.intent.extra.shortcut.INTENT", Intent.class) || !isValidExtraType(intent, "android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.class) || !isValidExtraType(intent, "android.intent.extra.shortcut.ICON", Bitmap.class)) {
            return null;
        }
        PendingInstallShortcutInfo pendingInstallShortcutInfo = new PendingInstallShortcutInfo(intent, Process.myUserHandle(), context);
        if (pendingInstallShortcutInfo.launchIntent == null || pendingInstallShortcutInfo.label == null) {
            return null;
        }
        return convertToLauncherActivityIfPossible(pendingInstallShortcutInfo);
    }

    public static ShortcutInfo fromShortcutIntent(Context context, Intent intent) {
        PendingInstallShortcutInfo createPendingInfo = createPendingInfo(context, intent);
        if (createPendingInfo == null) {
            return null;
        }
        return (ShortcutInfo) createPendingInfo.getItemInfo().first;
    }

    public static ShortcutInfo fromActivityInfo(LauncherActivityInfo launcherActivityInfo, Context context) {
        return (ShortcutInfo) new PendingInstallShortcutInfo(launcherActivityInfo, context).getItemInfo().first;
    }

    public static void queueShortcut(ShortcutInfoCompat shortcutInfoCompat, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(shortcutInfoCompat, context), context);
    }

    public static void queueWidget(AppWidgetProviderInfo appWidgetProviderInfo, int i, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(appWidgetProviderInfo, i, context), context);
    }

    public static void queueActivityInfo(LauncherActivityInfo launcherActivityInfo, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(launcherActivityInfo, context), context);
    }

    public static HashSet<ShortcutKey> getPendingShortcuts(Context context) {
        HashSet<ShortcutKey> hashSet = new HashSet<>();
        Set<String> stringSet = Utilities.getPrefs(context).getStringSet(APPS_PENDING_INSTALL, null);
        if (Utilities.isEmpty(stringSet)) {
            return hashSet;
        }
        for (String decoder : stringSet) {
            try {
                Decoder decoder2 = new Decoder(decoder, context);
                if (decoder2.optBoolean(DEEPSHORTCUT_TYPE_KEY)) {
                    hashSet.add(ShortcutKey.fromIntent(decoder2.launcherIntent, decoder2.user));
                }
            } catch (URISyntaxException | JSONException e) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Exception reading shortcut to add: ");
                sb.append(e);
                Log.d(str, sb.toString());
            }
        }
        return hashSet;
    }

    private static void queuePendingShortcutInfo(PendingInstallShortcutInfo pendingInstallShortcutInfo, Context context) {
        addToInstallQueue(Utilities.getPrefs(context), pendingInstallShortcutInfo);
        flushInstallQueue(context);
    }

    public static void enableInstallQueue(int i) {
        sInstallQueueDisabledFlags = i | sInstallQueueDisabledFlags;
    }

    public static void disableAndFlushInstallQueue(int i, Context context) {
        sInstallQueueDisabledFlags = (~i) & sInstallQueueDisabledFlags;
        flushInstallQueue(context);
    }

    static void flushInstallQueue(Context context) {
        LauncherModel model = LauncherAppState.getInstance(context).getModel();
        boolean z = model.getCallback() == null;
        if (sInstallQueueDisabledFlags == 0 && !z) {
            ArrayList andClearInstallQueue = getAndClearInstallQueue(context);
            if (!andClearInstallQueue.isEmpty()) {
                model.addAndBindAddedWorkspaceItems(new LazyShortcutsProvider(context.getApplicationContext(), andClearInstallQueue));
            }
        }
    }

    static CharSequence ensureValidName(Context context, Intent intent, CharSequence charSequence) {
        if (charSequence == null) {
            try {
                PackageManager packageManager = context.getPackageManager();
                charSequence = packageManager.getActivityInfo(intent.getComponent(), 0).loadLabel(packageManager);
            } catch (NameNotFoundException unused) {
                return "";
            }
        }
        return charSequence;
    }

    /* access modifiers changed from: private */
    public static String getIntentPackage(Intent intent) {
        return intent.getComponent() == null ? intent.getPackage() : intent.getComponent().getPackageName();
    }

    private static PendingInstallShortcutInfo decode(String str, Context context) {
        PendingInstallShortcutInfo pendingInstallShortcutInfo = null;
        try {
            Decoder decoder = new Decoder(str, context);
            if (decoder.optBoolean(APP_SHORTCUT_TYPE_KEY)) {
                LauncherActivityInfo resolveActivity = LauncherAppsCompat.getInstance(context).resolveActivity(decoder.launcherIntent, decoder.user);
                if (resolveActivity != null) {
                    pendingInstallShortcutInfo = new PendingInstallShortcutInfo(resolveActivity, context);
                }
                return pendingInstallShortcutInfo;
            } else if (decoder.optBoolean(DEEPSHORTCUT_TYPE_KEY)) {
                List queryForFullDetails = DeepShortcutManager.getInstance(context).queryForFullDetails(decoder.launcherIntent.getPackage(), Arrays.asList(new String[]{decoder.launcherIntent.getStringExtra(ShortcutInfoCompat.EXTRA_SHORTCUT_ID)}), decoder.user);
                if (queryForFullDetails.isEmpty()) {
                    return null;
                }
                return new PendingInstallShortcutInfo((ShortcutInfoCompat) queryForFullDetails.get(0), context);
            } else if (decoder.optBoolean(APP_WIDGET_TYPE_KEY)) {
                int intExtra = decoder.launcherIntent.getIntExtra(Favorites.APPWIDGET_ID, 0);
                AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(context).getAppWidgetInfo(intExtra);
                if (appWidgetInfo != null && appWidgetInfo.provider.equals(decoder.launcherIntent.getComponent())) {
                    if (appWidgetInfo.getProfile().equals(decoder.user)) {
                        return new PendingInstallShortcutInfo(appWidgetInfo, intExtra, context);
                    }
                }
                return null;
            } else {
                Intent intent = new Intent();
                intent.putExtra("android.intent.extra.shortcut.INTENT", decoder.launcherIntent);
                intent.putExtra("android.intent.extra.shortcut.NAME", decoder.getString("name"));
                String optString = decoder.optString("icon");
                String optString2 = decoder.optString("iconResource");
                String optString3 = decoder.optString(ICON_RESOURCE_PACKAGE_NAME_KEY);
                if (optString != null && !optString.isEmpty()) {
                    byte[] decode = Base64.decode(optString, 0);
                    intent.putExtra("android.intent.extra.shortcut.ICON", BitmapFactory.decodeByteArray(decode, 0, decode.length));
                } else if (optString2 != null && !optString2.isEmpty()) {
                    ShortcutIconResource shortcutIconResource = new ShortcutIconResource();
                    shortcutIconResource.resourceName = optString2;
                    shortcutIconResource.packageName = optString3;
                    intent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", shortcutIconResource);
                }
                return new PendingInstallShortcutInfo(intent, decoder.user, context);
            }
        } catch (URISyntaxException | JSONException e) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Exception reading shortcut to add: ");
            sb.append(e);
            Log.d(str2, sb.toString());
            return null;
        }
    }

    private static PendingInstallShortcutInfo convertToLauncherActivityIfPossible(PendingInstallShortcutInfo pendingInstallShortcutInfo) {
        if (pendingInstallShortcutInfo.isLauncherActivity() || !Utilities.isLauncherAppTarget(pendingInstallShortcutInfo.launchIntent)) {
            return pendingInstallShortcutInfo;
        }
        LauncherActivityInfo resolveActivity = LauncherAppsCompat.getInstance(pendingInstallShortcutInfo.mContext).resolveActivity(pendingInstallShortcutInfo.launchIntent, pendingInstallShortcutInfo.user);
        if (resolveActivity == null) {
            return pendingInstallShortcutInfo;
        }
        return new PendingInstallShortcutInfo(resolveActivity, pendingInstallShortcutInfo.mContext);
    }

    /* access modifiers changed from: private */
    public static ShortcutInfo createShortcutInfo(Intent intent, LauncherAppState launcherAppState) {
        Intent intent2 = (Intent) intent.getParcelableExtra("android.intent.extra.shortcut.INTENT");
        String stringExtra = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        Parcelable parcelableExtra = intent.getParcelableExtra("android.intent.extra.shortcut.ICON");
        if (intent2 == null) {
            Log.e(TAG, "Can't construct ShorcutInfo with null intent");
            return null;
        }
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.user = Process.myUserHandle();
        if (parcelableExtra instanceof Bitmap) {
            shortcutInfo.iconBitmap = LauncherIcons.createIconBitmap((Bitmap) parcelableExtra, launcherAppState.getContext());
        } else {
            Parcelable parcelableExtra2 = intent.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
            if (parcelableExtra2 instanceof ShortcutIconResource) {
                shortcutInfo.iconResource = (ShortcutIconResource) parcelableExtra2;
                shortcutInfo.iconBitmap = LauncherIcons.createIconBitmap(shortcutInfo.iconResource, launcherAppState.getContext());
            }
        }
        if (shortcutInfo.iconBitmap == null) {
            shortcutInfo.iconBitmap = launcherAppState.getIconCache().getDefaultIcon(shortcutInfo.user);
        }
        shortcutInfo.title = Utilities.trim(stringExtra);
        shortcutInfo.contentDescription = UserManagerCompat.getInstance(launcherAppState.getContext()).getBadgedLabelForUser(shortcutInfo.title, shortcutInfo.user);
        shortcutInfo.intent = intent2;
        return shortcutInfo;
    }
}
