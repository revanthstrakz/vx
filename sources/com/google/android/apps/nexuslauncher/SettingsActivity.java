package com.google.android.apps.nexuslauncher;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.android.launcher3.C0622R;
import com.android.launcher3.SettingsActivity.LauncherSettingsFragment;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;

public class SettingsActivity extends com.android.launcher3.SettingsActivity implements OnPreferenceStartFragmentCallback {
    public static final String APP_VERSION_PREF = "about_app_version";
    private static final String BRIDGE_TAG = "tag_bridge";
    public static final String ENABLE_MINUS_ONE_PREF = "pref_enable_minus_one";
    private static final String GOOGLE_APP = "com.google.android.googlequicksearchbox";
    public static final String ICON_PACK_PREF = "pref_icon_pack";
    public static final String SHOW_PREDICTIONS_PREF = "pref_show_predictions";
    public static final String SMARTSPACE_PREF = "pref_smartspace";

    public static class InstallFragment extends DialogFragment {
        public Dialog onCreateDialog(Bundle bundle) {
            return new Builder(getActivity()).setTitle(C0622R.string.bridge_missing_title).setMessage(C0622R.string.bridge_missing_message).setNegativeButton(17039360, null).create();
        }
    }

    public static class MySettingsFragment extends LauncherSettingsFragment implements OnPreferenceChangeListener {
        private Context mContext;
        private CustomIconPreference mIconPackPref;

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mContext = getActivity();
            findPreference(SettingsActivity.SHOW_PREDICTIONS_PREF).setOnPreferenceChangeListener(this);
            findPreference(SettingsActivity.ENABLE_MINUS_ONE_PREF).setOnPreferenceChangeListener(this);
            findPreference(SettingsActivity.ENABLE_MINUS_ONE_PREF).setTitle(getDisplayGoogleTitle());
            try {
                if (this.mContext.getPackageManager().getApplicationInfo(SettingsActivity.GOOGLE_APP, 0).enabled) {
                    this.mIconPackPref = (CustomIconPreference) findPreference(SettingsActivity.ICON_PACK_PREF);
                    this.mIconPackPref.setOnPreferenceChangeListener(this);
                    findPreference(SettingsActivity.SHOW_PREDICTIONS_PREF).setOnPreferenceChangeListener(this);
                    return;
                }
                throw new NameNotFoundException();
            } catch (NameNotFoundException unused) {
                getPreferenceScreen().removePreference(findPreference(SettingsActivity.ENABLE_MINUS_ONE_PREF));
            }
        }

        private String getDisplayGoogleTitle() {
            String str = null;
            try {
                Resources resourcesForApplication = this.mContext.getPackageManager().getResourcesForApplication(SettingsActivity.GOOGLE_APP);
                int identifier = resourcesForApplication.getIdentifier("title_google_home_screen", StringTypedProperty.TYPE, SettingsActivity.GOOGLE_APP);
                if (identifier != 0) {
                    str = resourcesForApplication.getString(identifier);
                }
            } catch (NameNotFoundException unused) {
            }
            if (TextUtils.isEmpty(str)) {
                str = this.mContext.getString(C0622R.string.title_google_app);
            }
            return this.mContext.getString(C0622R.string.title_show_google_app, new Object[]{str});
        }

        public void onResume() {
            super.onResume();
            this.mIconPackPref.reloadIconPacks();
            SwitchPreference switchPreference = (SwitchPreference) findPreference(SettingsActivity.ENABLE_MINUS_ONE_PREF);
            if (switchPreference != null && !PixelBridge.isInstalled(getActivity())) {
                switchPreference.setChecked(false);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x003e  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x005b  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0094  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onPreferenceChange(android.preference.Preference r6, java.lang.Object r7) {
            /*
                r5 = this;
                java.lang.String r0 = r6.getKey()
                int r1 = r0.hashCode()
                r2 = -1208660221(0xffffffffb7f54f03, float:-2.9243069E-5)
                r3 = 0
                r4 = 1
                if (r1 == r2) goto L_0x002e
                r2 = -290350562(0xffffffffeeb19a1e, float:-2.7482581E28)
                if (r1 == r2) goto L_0x0024
                r2 = 1986449655(0x7666d0f7, float:1.1703775E33)
                if (r1 == r2) goto L_0x001a
                goto L_0x0038
            L_0x001a:
                java.lang.String r1 = "pref_enable_minus_one"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0038
                r0 = 0
                goto L_0x0039
            L_0x0024:
                java.lang.String r1 = "pref_show_predictions"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0038
                r0 = 2
                goto L_0x0039
            L_0x002e:
                java.lang.String r1 = "pref_icon_pack"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0038
                r0 = 1
                goto L_0x0039
            L_0x0038:
                r0 = -1
            L_0x0039:
                switch(r0) {
                    case 0: goto L_0x0094;
                    case 1: goto L_0x005b;
                    case 2: goto L_0x003e;
                    default: goto L_0x003c;
                }
            L_0x003c:
                goto L_0x00b5
            L_0x003e:
                java.lang.Boolean r7 = (java.lang.Boolean) r7
                boolean r7 = r7.booleanValue()
                if (r7 == 0) goto L_0x0047
                return r4
            L_0x0047:
                com.google.android.apps.nexuslauncher.SettingsActivity$SuggestionConfirmationFragment r7 = new com.google.android.apps.nexuslauncher.SettingsActivity$SuggestionConfirmationFragment
                r7.<init>()
                r7.setTargetFragment(r5, r3)
                android.app.FragmentManager r0 = r5.getFragmentManager()
                java.lang.String r6 = r6.getKey()
                r7.show(r0, r6)
                goto L_0x00b5
            L_0x005b:
                android.content.Context r6 = r5.mContext
                java.lang.String r6 = com.google.android.apps.nexuslauncher.CustomIconUtils.getCurrentPack(r6)
                boolean r6 = r6.equals(r7)
                if (r6 != 0) goto L_0x0093
                android.content.Context r6 = r5.mContext
                r0 = 0
                android.content.Context r1 = r5.mContext
                int r2 = com.android.launcher3.C0622R.string.state_loading
                java.lang.String r1 = r1.getString(r2)
                android.app.ProgressDialog r6 = android.app.ProgressDialog.show(r6, r0, r1, r4, r3)
                android.app.Activity r0 = r5.getActivity()
                java.lang.String r7 = (java.lang.String) r7
                com.google.android.apps.nexuslauncher.CustomIconUtils.setCurrentPack(r0, r7)
                android.content.Context r7 = r5.mContext
                com.google.android.apps.nexuslauncher.CustomIconUtils.applyIconPackAsync(r7)
                android.os.Handler r7 = new android.os.Handler
                r7.<init>()
                com.google.android.apps.nexuslauncher.SettingsActivity$MySettingsFragment$1 r0 = new com.google.android.apps.nexuslauncher.SettingsActivity$MySettingsFragment$1
                r0.<init>(r6)
                r1 = 1000(0x3e8, double:4.94E-321)
                r7.postDelayed(r0, r1)
            L_0x0093:
                return r4
            L_0x0094:
                android.app.Activity r6 = r5.getActivity()
                boolean r6 = com.google.android.apps.nexuslauncher.PixelBridge.isInstalled(r6)
                if (r6 == 0) goto L_0x009f
                return r4
            L_0x009f:
                android.app.FragmentManager r6 = r5.getFragmentManager()
                java.lang.String r7 = "tag_bridge"
                android.app.Fragment r7 = r6.findFragmentByTag(r7)
                if (r7 != 0) goto L_0x00b5
                com.google.android.apps.nexuslauncher.SettingsActivity$InstallFragment r7 = new com.google.android.apps.nexuslauncher.SettingsActivity$InstallFragment
                r7.<init>()
                java.lang.String r0 = "tag_bridge"
                r7.show(r6, r0)
            L_0x00b5:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.apps.nexuslauncher.SettingsActivity.MySettingsFragment.onPreferenceChange(android.preference.Preference, java.lang.Object):boolean");
        }
    }

    public static class OpenSourceLicensesFragment extends DialogFragment {
        public Dialog onCreateDialog(Bundle bundle) {
            WebView webView = new WebView(getActivity());
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setBuiltInZoomControls(true);
            webView.loadUrl("file:///android_asset/license.html");
            return new Builder(getActivity()).setTitle(C0622R.string.pref_open_source_licenses_title).setView(webView).create();
        }
    }

    public static class SuggestionConfirmationFragment extends DialogFragment implements OnClickListener {
        public void onClick(DialogInterface dialogInterface, int i) {
            if (getTargetFragment() instanceof PreferenceFragment) {
                Preference findPreference = ((PreferenceFragment) getTargetFragment()).findPreference(SettingsActivity.SHOW_PREDICTIONS_PREF);
                if (findPreference instanceof TwoStatePreference) {
                    ((TwoStatePreference) findPreference).setChecked(false);
                }
            }
        }

        public Dialog onCreateDialog(Bundle bundle) {
            return new Builder(getActivity()).setTitle(C0622R.string.title_disable_suggestions_prompt).setMessage(C0622R.string.msg_disable_suggestions_prompt).setNegativeButton(17039360, null).setPositiveButton(C0622R.string.label_turn_off_suggestions, this).create();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, new MySettingsFragment()).commit();
        }
    }

    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment, Preference preference) {
        Fragment instantiate = Fragment.instantiate(this, preference.getFragment(), preference.getExtras());
        if (instantiate instanceof DialogFragment) {
            ((DialogFragment) instantiate).show(getFragmentManager(), preference.getKey());
        } else {
            getFragmentManager().beginTransaction().replace(16908290, instantiate).addToBackStack(preference.getKey()).commit();
        }
        return true;
    }
}
