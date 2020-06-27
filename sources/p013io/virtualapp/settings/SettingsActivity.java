package p013io.virtualapp.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.Toast;
import com.android.launcher3.LauncherFiles;
import com.google.android.apps.nexuslauncher.SettingsActivity;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.ipc.VActivityManager;
import io.va.exposed.R;
import java.io.File;
import java.io.IOException;
import p013io.virtualapp.XApp;
import p013io.virtualapp.gms.FakeGms;
import p013io.virtualapp.home.ListAppActivity;
import p013io.virtualapp.settings.SettingsActivity.SettingsFragment;
import p013io.virtualapp.utils.Misc;

/* renamed from: io.virtualapp.settings.SettingsActivity */
public class SettingsActivity extends Activity {
    private static final String ABOUT_KEY = "settings_about";
    private static final String ADD_APP_KEY = "settings_add_app";
    private static final String ADVANCE_SETTINGS_KEY = "settings_advance";
    private static final String ALLOW_FAKE_SIGNATURE = "advance_settings_allow_fake_signature";
    private static final String APP_MANAGE_KEY = "settings_app_manage";
    private static final String DESKTOP_SETTINGS_KEY = "settings_desktop";
    public static final String DIRECTLY_BACK_KEY = "advance_settings_directly_back";
    private static final String DISABLE_INSTALLER_KEY = "advance_settings_disable_installer";
    private static final String DISABLE_RESIDENT_NOTIFICATION = "advance_settings_disable_resident_notification";
    private static final String DISABLE_XPOSED = "advance_settings_disable_xposed";
    private static final String DONATE_KEY = "settings_donate";
    public static final String ENABLE_LAUNCHER = "advance_settings_enable_launcher";
    private static final String FAQ_SETTINGS_KEY = "settings_faq";
    private static final String FILE_MANAGE = "settings_file_manage";
    private static final String HIDE_SETTINGS_KEY = "advance_settings_hide_settings";
    private static final String INSTALL_GMS_KEY = "advance_settings_install_gms";
    private static final String MODULE_MANAGE_KEY = "settings_module_manage";
    private static final String PERMISSION_MANAGE = "settings_permission_manage";
    private static final String REBOOT_KEY = "settings_reboot";
    private static final String RECOMMEND_PLUGIN = "settings_plugin_recommend";
    private static final String TASK_MANAGE_KEY = "settings_task_manage";

    /* renamed from: io.virtualapp.settings.SettingsActivity$SettingsFragment */
    public static class SettingsFragment extends PreferenceFragment {
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            addPreferencesFromResource(R.xml.settings_preferences);
            Preference findPreference = findPreference(SettingsActivity.ADD_APP_KEY);
            Preference findPreference2 = findPreference(SettingsActivity.MODULE_MANAGE_KEY);
            Preference findPreference3 = findPreference(SettingsActivity.RECOMMEND_PLUGIN);
            Preference findPreference4 = findPreference(SettingsActivity.APP_MANAGE_KEY);
            Preference findPreference5 = findPreference(SettingsActivity.TASK_MANAGE_KEY);
            Preference findPreference6 = findPreference(SettingsActivity.DESKTOP_SETTINGS_KEY);
            Preference findPreference7 = findPreference(SettingsActivity.FAQ_SETTINGS_KEY);
            Preference findPreference8 = findPreference(SettingsActivity.DONATE_KEY);
            Preference findPreference9 = findPreference(SettingsActivity.ABOUT_KEY);
            Preference findPreference10 = findPreference(SettingsActivity.REBOOT_KEY);
            Preference findPreference11 = findPreference(SettingsActivity.FILE_MANAGE);
            Preference findPreference12 = findPreference(SettingsActivity.PERMISSION_MANAGE);
            SwitchPreference switchPreference = (SwitchPreference) findPreference(SettingsActivity.DISABLE_INSTALLER_KEY);
            SwitchPreference switchPreference2 = (SwitchPreference) findPreference(SettingsActivity.ENABLE_LAUNCHER);
            SwitchPreference switchPreference3 = (SwitchPreference) findPreference(SettingsActivity.DISABLE_RESIDENT_NOTIFICATION);
            SwitchPreference switchPreference4 = (SwitchPreference) findPreference(SettingsActivity.ALLOW_FAKE_SIGNATURE);
            SwitchPreference switchPreference5 = (SwitchPreference) findPreference(SettingsActivity.DISABLE_XPOSED);
            findPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return ListAppActivity.gotoListApp(SettingsFragment.this.getActivity());
                }
            });
            findPreference2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.lambda$onCreate$31(SettingsFragment.this, preference);
                }
            });
            findPreference3.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.this.startActivity(new Intent(SettingsFragment.this.getActivity(), RecommendPluginActivity.class));
                }
            });
            if (!VirtualCore.get().isXposedEnabled()) {
                getPreferenceScreen().removePreference(findPreference2);
                getPreferenceScreen().removePreference(findPreference3);
            }
            findPreference4.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.this.startActivity(new Intent(SettingsFragment.this.getActivity(), AppManageActivity.class));
                }
            });
            findPreference5.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.this.startActivity(new Intent(SettingsFragment.this.getActivity(), TaskManageActivity.class));
                }
            });
            findPreference7.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/android-hacker/VAExposed/wiki/FAQ")));
                }
            });
            findPreference6.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.this.startActivity(new Intent(SettingsFragment.this.getActivity(), SettingsActivity.class));
                }
            });
            findPreference8.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return Misc.showDonate(SettingsFragment.this.getActivity());
                }
            });
            findPreference9.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.this.startActivity(new Intent(SettingsFragment.this.getActivity(), AboutActivity.class));
                }
            });
            findPreference10.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.lambda$onCreate$40(SettingsFragment.this, preference);
                }
            });
            switchPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return SettingsFragment.lambda$onCreate$41(SettingsFragment.this, preference, obj);
                }
            });
            switchPreference2.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return SettingsFragment.lambda$onCreate$42(SettingsFragment.this, preference, obj);
                }
            });
            findPreference(SettingsActivity.INSTALL_GMS_KEY).setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return SettingsFragment.lambda$onCreate$43(SettingsFragment.this, preference);
                }
            });
            findPreference11.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return OnlinePlugin.openOrDownload(SettingsFragment.this.getActivity(), OnlinePlugin.FILE_MANAGE_PACKAGE, OnlinePlugin.FILE_MANAGE_URL, SettingsFragment.this.getString(R.string.install_file_manager_tips));
                }
            });
            findPreference12.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public final boolean onPreferenceClick(Preference preference) {
                    return OnlinePlugin.openOrDownload(SettingsFragment.this.getActivity(), OnlinePlugin.PERMISSION_MANAGE_PACKAGE, OnlinePlugin.PERMISSION_MANAGE_URL, SettingsFragment.this.getString(R.string.install_permission_manager_tips));
                }
            });
            switchPreference5.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return SettingsFragment.lambda$onCreate$46(SettingsFragment.this, preference, obj);
                }
            });
            SwitchPreference switchPreference6 = switchPreference3;
            switchPreference6.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return SettingsFragment.lambda$onCreate$47(SettingsFragment.this, preference, obj);
                }
            });
            if (VERSION.SDK_INT < 25) {
                ((PreferenceScreen) findPreference(SettingsActivity.ADVANCE_SETTINGS_KEY)).removePreference(switchPreference6);
            }
            switchPreference4.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return SettingsFragment.lambda$onCreate$48(SettingsFragment.this, preference, obj);
                }
            });
        }

        public static /* synthetic */ boolean lambda$onCreate$31(SettingsFragment settingsFragment, Preference preference) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(XApp.XPOSED_INSTALLER_PACKAGE, "de.robv.android.xposed.installer.WelcomeActivity"));
                intent.putExtra("fragment", 1);
                if (VActivityManager.get().startActivity(intent, 0) < 0) {
                    Toast.makeText(settingsFragment.getActivity(), R.string.xposed_installer_not_found, 0).show();
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
            return false;
        }

        public static /* synthetic */ boolean lambda$onCreate$40(SettingsFragment settingsFragment, Preference preference) {
            try {
                new Builder(settingsFragment.getActivity()).setTitle(R.string.settings_reboot_title).setMessage(settingsFragment.getResources().getString(R.string.settings_reboot_content)).setPositiveButton(17039379, new OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SettingsFragment.lambda$null$39(SettingsFragment.this, dialogInterface, i);
                    }
                }).setNegativeButton(17039369, null).create().show();
            } catch (Throwable unused) {
            }
            return false;
        }

        public static /* synthetic */ void lambda$null$39(SettingsFragment settingsFragment, DialogInterface dialogInterface, int i) {
            VirtualCore.get().killAllApps();
            Toast.makeText(settingsFragment.getActivity(), R.string.reboot_tips_1, 0).show();
        }

        public static /* synthetic */ boolean lambda$onCreate$41(SettingsFragment settingsFragment, Preference preference, Object obj) {
            if (!(obj instanceof Boolean)) {
                return false;
            }
            try {
                settingsFragment.getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(settingsFragment.getActivity().getPackageName(), "vxp.installer"), !((Boolean) obj).booleanValue() ? 1 : 2, 1);
                return true;
            } catch (Throwable unused) {
                return false;
            }
        }

        public static /* synthetic */ boolean lambda$onCreate$42(SettingsFragment settingsFragment, Preference preference, Object obj) {
            if (!(obj instanceof Boolean)) {
                return false;
            }
            try {
                settingsFragment.getActivity().getPackageManager().setComponentEnabledSetting(new ComponentName(settingsFragment.getActivity().getPackageName(), "vxp.launcher"), ((Boolean) obj).booleanValue() ? 1 : 2, 1);
                return true;
            } catch (Throwable unused) {
                return false;
            }
        }

        public static /* synthetic */ boolean lambda$onCreate$43(SettingsFragment settingsFragment, Preference preference) {
            if (FakeGms.isAlreadyInstalled(settingsFragment.getActivity())) {
                FakeGms.uninstallGms(settingsFragment.getActivity());
            } else {
                FakeGms.installGms(settingsFragment.getActivity());
            }
            return true;
        }

        public static /* synthetic */ boolean lambda$onCreate$46(SettingsFragment settingsFragment, Preference preference, Object obj) {
            boolean z;
            boolean z2 = false;
            if (!(obj instanceof Boolean)) {
                return false;
            }
            boolean booleanValue = ((Boolean) obj).booleanValue();
            File fileStreamPath = settingsFragment.getActivity().getFileStreamPath(".disable_xposed");
            if (booleanValue) {
                try {
                    z = fileStreamPath.createNewFile();
                } catch (IOException unused) {
                    z = false;
                }
                return z;
            }
            if (!fileStreamPath.exists() || fileStreamPath.delete()) {
                z2 = true;
            }
            return z2;
        }

        public static /* synthetic */ boolean lambda$onCreate$47(SettingsFragment settingsFragment, Preference preference, Object obj) {
            boolean z;
            boolean z2 = false;
            if (!(obj instanceof Boolean)) {
                return false;
            }
            boolean booleanValue = ((Boolean) obj).booleanValue();
            File fileStreamPath = settingsFragment.getActivity().getFileStreamPath(Constants.NO_NOTIFICATION_FLAG);
            if (booleanValue) {
                try {
                    z = fileStreamPath.createNewFile();
                } catch (IOException unused) {
                    z = false;
                }
                return z;
            }
            if (!fileStreamPath.exists() || fileStreamPath.delete()) {
                z2 = true;
            }
            return z2;
        }

        public static /* synthetic */ boolean lambda$onCreate$48(SettingsFragment settingsFragment, Preference preference, Object obj) {
            boolean z;
            boolean z2 = false;
            if (!(obj instanceof Boolean)) {
                return false;
            }
            boolean booleanValue = ((Boolean) obj).booleanValue();
            File fileStreamPath = settingsFragment.getActivity().getFileStreamPath(Constants.FAKE_SIGNATURE_FLAG);
            if (booleanValue) {
                try {
                    z = fileStreamPath.createNewFile();
                } catch (IOException unused) {
                    z = false;
                }
                return z;
            }
            if (!fileStreamPath.exists() || fileStreamPath.delete()) {
                z2 = true;
            }
            return z2;
        }

        private static void dismiss(ProgressDialog progressDialog) {
            try {
                progressDialog.dismiss();
            } catch (Throwable unused) {
            }
        }

        /* access modifiers changed from: protected */
        public int dp2px(float f) {
            return (int) ((f * getResources().getDisplayMetrics().density) + 0.5f);
        }

        public void startActivity(Intent intent) {
            try {
                super.startActivity(intent);
            } catch (Throwable th) {
                Toast.makeText(getActivity(), "startActivity failed.", 0).show();
                th.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, new SettingsFragment()).commit();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 5 && i2 == -1) {
            finish();
        }
    }
}
