package com.google.android.apps.nexuslauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import com.android.launcher3.AppFilter;
import com.android.launcher3.AppInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ComponentKeyMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomAppPredictor extends UserEventDispatcher implements OnSharedPreferenceChangeListener {
    private static final int BOOST_ON_OPEN = 9;
    private static final Set<String> EMPTY_SET = new HashSet();
    private static final int MAX_PREDICTIONS = 10;
    private static final String[] PLACE_HOLDERS = {"com.google.android.apps.photos", "com.google.android.apps.maps", "com.google.android.gm", "com.google.android.deskclock", "com.android.settings", "com.whatsapp", "com.facebook.katana", "com.facebook.orca", "com.google.android.youtube", "com.yodo1.crossyroad", "com.spotify.music", "com.android.chrome", "com.instagram.android", "com.skype.raider", "com.snapchat.android", "com.viber.voip", "com.twitter.android", "com.android.phone", "com.google.android.music", DynamicIconProvider.GOOGLE_CALENDAR, "com.google.android.apps.genie.geniewidget", "com.netflix.mediaclient", "bbc.iplayer.android", "com.google.android.videos", "com.amazon.mShop.android.shopping", "com.microsoft.office.word", "com.google.android.apps.docs", "com.google.android.keep", "com.google.android.apps.plus", "com.google.android.talk"};
    private static final String PREDICTION_PREFIX = "pref_prediction_count_";
    private static final String PREDICTION_SET = "pref_prediction_set";
    private final AppFilter mAppFilter = AppFilter.newInstance(this.mContext);
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final SharedPreferences mPrefs;

    public CustomAppPredictor(Context context) {
        this.mContext = context;
        this.mPrefs = Utilities.getPrefs(context);
        this.mPrefs.registerOnSharedPreferenceChangeListener(this);
        this.mPackageManager = context.getPackageManager();
    }

    /* access modifiers changed from: 0000 */
    public List<ComponentKeyMapper<AppInfo>> getPredictions() {
        ArrayList arrayList = new ArrayList();
        if (isPredictorEnabled()) {
            clearNonExistentPackages();
            ArrayList<String> arrayList2 = new ArrayList<>(getStringSetCopy());
            Collections.sort(arrayList2, new Comparator<String>() {
                public int compare(String str, String str2) {
                    return Integer.compare(CustomAppPredictor.this.getLaunchCount(str2), CustomAppPredictor.this.getLaunchCount(str));
                }
            });
            for (String componentFromString : arrayList2) {
                arrayList.add(getComponentFromString(componentFromString));
            }
            for (int i = 0; i < PLACE_HOLDERS.length && arrayList.size() < 10; i++) {
                Intent launchIntentForPackage = this.mPackageManager.getLaunchIntentForPackage(PLACE_HOLDERS[i]);
                if (launchIntentForPackage != null) {
                    ComponentName component = launchIntentForPackage.getComponent();
                    if (component != null) {
                        ComponentKey componentKey = new ComponentKey(component, Process.myUserHandle());
                        if (!arrayList2.contains(componentKey.toString())) {
                            arrayList.add(new ComponentKeyMapper(componentKey));
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public void logAppLaunch(View view, Intent intent, UserHandle userHandle) {
        super.logAppLaunch(view, intent, userHandle);
        if (isPredictorEnabled() && recursiveIsDrawer(view)) {
            ComponentName component = intent.getComponent();
            if (component != null && this.mAppFilter.shouldShowApp(component, userHandle)) {
                clearNonExistentPackages();
                Set stringSetCopy = getStringSetCopy();
                Editor edit = this.mPrefs.edit();
                String componentKey = new ComponentKey(component, userHandle).toString();
                if (stringSetCopy.contains(componentKey)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(PREDICTION_PREFIX);
                    sb.append(componentKey);
                    edit.putInt(sb.toString(), getLaunchCount(componentKey) + 9);
                } else if (stringSetCopy.size() < 10 || decayHasSpotFree(stringSetCopy, edit)) {
                    stringSetCopy.add(componentKey);
                }
                edit.putStringSet(PREDICTION_SET, stringSetCopy);
                edit.apply();
            }
        }
    }

    private boolean decayHasSpotFree(Set<String> set, Editor editor) {
        HashSet<String> hashSet = new HashSet<>();
        boolean z = false;
        for (String str : set) {
            int launchCount = getLaunchCount(str);
            if (launchCount > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(PREDICTION_PREFIX);
                sb.append(str);
                editor.putInt(sb.toString(), launchCount - 1);
            } else if (!z) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(PREDICTION_PREFIX);
                sb2.append(str);
                editor.remove(sb2.toString());
                hashSet.add(str);
                z = true;
            }
        }
        for (String remove : hashSet) {
            set.remove(remove);
        }
        return z;
    }

    /* access modifiers changed from: private */
    public int getLaunchCount(String str) {
        SharedPreferences sharedPreferences = this.mPrefs;
        StringBuilder sb = new StringBuilder();
        sb.append(PREDICTION_PREFIX);
        sb.append(str);
        return sharedPreferences.getInt(sb.toString(), 0);
    }

    private boolean recursiveIsDrawer(View view) {
        if (view != null) {
            for (ViewParent parent = view.getParent(); parent != null; parent = parent.getParent()) {
                if (parent instanceof AllAppsContainerView) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPredictorEnabled() {
        return Utilities.getPrefs(this.mContext).getBoolean(SettingsActivity.SHOW_PREDICTIONS_PREF, false);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (str.equals(SettingsActivity.SHOW_PREDICTIONS_PREF) && !isPredictorEnabled()) {
            Set<String> stringSetCopy = getStringSetCopy();
            Editor edit = this.mPrefs.edit();
            for (String str2 : stringSetCopy) {
                StringBuilder sb = new StringBuilder();
                sb.append("Clearing ");
                sb.append(str2);
                sb.append(" at ");
                sb.append(getLaunchCount(str2));
                Log.i("Predictor", sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append(PREDICTION_PREFIX);
                sb2.append(str2);
                edit.remove(sb2.toString());
            }
            edit.putStringSet(PREDICTION_SET, EMPTY_SET);
            edit.apply();
        }
    }

    private ComponentKeyMapper<AppInfo> getComponentFromString(String str) {
        return new ComponentKeyMapper<>(new ComponentKey(this.mContext, str));
    }

    private void clearNonExistentPackages() {
        Set<String> stringSet = this.mPrefs.getStringSet(PREDICTION_SET, EMPTY_SET);
        HashSet hashSet = new HashSet(stringSet);
        Editor edit = this.mPrefs.edit();
        for (String str : stringSet) {
            try {
                this.mPackageManager.getPackageInfo(new ComponentKey(this.mContext, str).componentName.getPackageName(), 0);
            } catch (NameNotFoundException unused) {
                hashSet.remove(str);
                StringBuilder sb = new StringBuilder();
                sb.append(PREDICTION_PREFIX);
                sb.append(str);
                edit.remove(sb.toString());
            }
        }
        edit.putStringSet(PREDICTION_SET, hashSet);
        edit.apply();
    }

    private Set<String> getStringSetCopy() {
        HashSet hashSet = new HashSet();
        hashSet.addAll(this.mPrefs.getStringSet(PREDICTION_SET, EMPTY_SET));
        return hashSet;
    }
}
