package com.google.android.apps.nexuslauncher;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.widget.WidgetsBottomSheet;

public class CustomBottomSheet extends WidgetsBottomSheet {
    private FragmentManager mFragmentManager;

    public static class PrefsFragment extends PreferenceFragment implements OnPreferenceChangeListener {
        private static final String PREF_HIDE = "pref_app_hide";
        private static final String PREF_PACK = "pref_app_icon_pack";
        private ComponentKey mKey;
        private SwitchPreference mPrefHide;
        private SwitchPreference mPrefPack;

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(C0622R.xml.app_edit_prefs);
        }

        public void loadForApp(ItemInfo itemInfo) {
            this.mKey = new ComponentKey(itemInfo.getTargetComponent(), itemInfo.user);
            this.mPrefPack = (SwitchPreference) findPreference(PREF_PACK);
            this.mPrefHide = (SwitchPreference) findPreference(PREF_HIDE);
            Activity activity = getActivity();
            CustomDrawableFactory customDrawableFactory = (CustomDrawableFactory) DrawableFactory.get(activity);
            ComponentName targetComponent = itemInfo.getTargetComponent();
            boolean z = true;
            boolean z2 = customDrawableFactory.packCalendars.containsKey(targetComponent) || customDrawableFactory.packComponents.containsKey(targetComponent);
            this.mPrefPack.setEnabled(z2);
            SwitchPreference switchPreference = this.mPrefPack;
            if (!z2 || !CustomIconProvider.isEnabledForApp(activity, this.mKey)) {
                z = false;
            }
            switchPreference.setChecked(z);
            if (z2) {
                PackageManager packageManager = activity.getPackageManager();
                try {
                    this.mPrefPack.setSummary(packageManager.getPackageInfo(customDrawableFactory.iconPack, 0).applicationInfo.loadLabel(packageManager));
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            this.mPrefHide.setChecked(CustomAppFilter.isHiddenApp(activity, this.mKey));
            this.mPrefPack.setOnPreferenceChangeListener(this);
            this.mPrefHide.setOnPreferenceChangeListener(this);
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x003b  */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x0041  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onPreferenceChange(android.preference.Preference r5, java.lang.Object r6) {
            /*
                r4 = this;
                java.lang.Boolean r6 = (java.lang.Boolean) r6
                boolean r6 = r6.booleanValue()
                android.app.Activity r0 = r4.getActivity()
                com.android.launcher3.Launcher r0 = com.android.launcher3.Launcher.getLauncher(r0)
                java.lang.String r5 = r5.getKey()
                int r1 = r5.hashCode()
                r2 = -1098302363(0xffffffffbe893c65, float:-0.2680389)
                r3 = 1
                if (r1 == r2) goto L_0x002c
                r2 = 619155260(0x24e78f3c, float:1.0042293E-16)
                if (r1 == r2) goto L_0x0022
                goto L_0x0036
            L_0x0022:
                java.lang.String r1 = "pref_app_hide"
                boolean r5 = r5.equals(r1)
                if (r5 == 0) goto L_0x0036
                r5 = 1
                goto L_0x0037
            L_0x002c:
                java.lang.String r1 = "pref_app_icon_pack"
                boolean r5 = r5.equals(r1)
                if (r5 == 0) goto L_0x0036
                r5 = 0
                goto L_0x0037
            L_0x0036:
                r5 = -1
            L_0x0037:
                switch(r5) {
                    case 0: goto L_0x0041;
                    case 1: goto L_0x003b;
                    default: goto L_0x003a;
                }
            L_0x003a:
                goto L_0x004b
            L_0x003b:
                com.android.launcher3.util.ComponentKey r5 = r4.mKey
                com.google.android.apps.nexuslauncher.CustomAppFilter.setComponentNameState(r0, r5, r6)
                goto L_0x004b
            L_0x0041:
                com.android.launcher3.util.ComponentKey r5 = r4.mKey
                com.google.android.apps.nexuslauncher.CustomIconProvider.setAppState(r0, r5, r6)
                com.android.launcher3.util.ComponentKey r5 = r4.mKey
                com.google.android.apps.nexuslauncher.CustomIconUtils.reloadIconByKey(r0, r5)
            L_0x004b:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.apps.nexuslauncher.CustomBottomSheet.PrefsFragment.onPreferenceChange(android.preference.Preference, java.lang.Object):boolean");
        }
    }

    /* access modifiers changed from: protected */
    public void onWidgetsBound() {
    }

    public CustomBottomSheet(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CustomBottomSheet(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFragmentManager = Launcher.getLauncher(context).getFragmentManager();
    }

    public void populateAndShow(ItemInfo itemInfo) {
        super.populateAndShow(itemInfo);
        ((TextView) findViewById(C0622R.C0625id.title)).setText(itemInfo.title);
        ((PrefsFragment) this.mFragmentManager.findFragmentById(C0622R.C0625id.sheet_prefs)).loadForApp(itemInfo);
    }

    public void onDetachedFromWindow() {
        Fragment findFragmentById = this.mFragmentManager.findFragmentById(C0622R.C0625id.sheet_prefs);
        if (findFragmentById != null) {
            this.mFragmentManager.beginTransaction().remove(findFragmentById).commitAllowingStateLoss();
        }
        super.onDetachedFromWindow();
    }
}
