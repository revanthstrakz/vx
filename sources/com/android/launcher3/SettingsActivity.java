package com.android.launcher3;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import com.android.launcher3.SettingsActivity.LauncherSettingsFragment;
import com.android.launcher3.graphics.IconShapeOverride;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.util.SettingsObserver.Secure;
import com.android.launcher3.util.SettingsObserver.System;
import com.android.launcher3.views.ButtonPreference;
import java.lang.ref.WeakReference;

public class SettingsActivity extends Activity {
    public static final String NOTIFICATION_BADGING = "notification_badging";
    private static final String NOTIFICATION_ENABLED_LISTENERS = "enabled_notification_listeners";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String WALLPAPER_FILE = "wallpaper.png";

    private static class IconBadgingObserver extends Secure implements OnPreferenceClickListener {
        private final ButtonPreference mBadgingPref;
        private final FragmentManager mFragmentManager;
        private final ContentResolver mResolver;
        private boolean serviceEnabled = true;

        public IconBadgingObserver(ButtonPreference buttonPreference, ContentResolver contentResolver, FragmentManager fragmentManager) {
            super(contentResolver);
            this.mBadgingPref = buttonPreference;
            this.mResolver = contentResolver;
            this.mFragmentManager = fragmentManager;
        }

        public void onSettingChanged(boolean z) {
            int i = z ? C0622R.string.icon_badging_desc_on : C0622R.string.icon_badging_desc_off;
            if (z) {
                String string = Settings.Secure.getString(this.mResolver, SettingsActivity.NOTIFICATION_ENABLED_LISTENERS);
                ComponentName componentName = new ComponentName(this.mBadgingPref.getContext(), NotificationListener.class);
                this.serviceEnabled = string != null && (string.contains(componentName.flattenToString()) || string.contains(componentName.flattenToShortString()));
                if (!this.serviceEnabled) {
                    i = C0622R.string.title_missing_notification_access;
                }
            }
            this.mBadgingPref.setWidgetFrameVisible(true ^ this.serviceEnabled);
            this.mBadgingPref.setOnPreferenceClickListener((!this.serviceEnabled || !Utilities.ATLEAST_OREO) ? this : null);
            this.mBadgingPref.setSummary(i);
        }

        public boolean onPreferenceClick(Preference preference) {
            if (Utilities.ATLEAST_OREO || !this.serviceEnabled) {
                new NotificationAccessConfirmation().show(this.mFragmentManager, "notification_access");
            } else {
                preference.getContext().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(268435456).putExtra(":settings:fragment_args_key", new ComponentName(preference.getContext(), NotificationListener.class).flattenToString()));
            }
            return true;
        }
    }

    public static class LauncherSettingsFragment extends PreferenceFragment {
        private IconBadgingObserver mIconBadgingObserver;
        private SystemDisplayRotationLockObserver mRotationLockObserver;

        private static class WallpaperChooseTask extends AsyncTask<Void, Void, Void> {
            WeakReference<Context> mActivityRef;
            WeakReference<ProgressDialog> mRef;
            String picturePath;

            WallpaperChooseTask(ProgressDialog progressDialog, String str) {
                this.mRef = new WeakReference<>(progressDialog);
                this.mActivityRef = new WeakReference<>(progressDialog.getContext());
                this.picturePath = str;
            }

            /* access modifiers changed from: protected */
            public void onPreExecute() {
                super.onPreExecute();
                try {
                    ProgressDialog progressDialog = (ProgressDialog) this.mRef.get();
                    if (progressDialog != null) {
                        progressDialog.show();
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Code restructure failed: missing block: B:16:0x0032, code lost:
                if (r1.isRecycled() == false) goto L_0x0057;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:34:0x0055, code lost:
                if (r1.isRecycled() == false) goto L_0x0057;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:35:0x0057, code lost:
                r1.recycle();
             */
            /* JADX WARNING: Removed duplicated region for block: B:28:0x0047 A[SYNTHETIC, Splitter:B:28:0x0047] */
            /* JADX WARNING: Removed duplicated region for block: B:33:0x0051  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x005f A[SYNTHETIC, Splitter:B:40:0x005f] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public java.lang.Void doInBackground(java.lang.Void... r5) {
                /*
                    r4 = this;
                    java.lang.ref.WeakReference<android.content.Context> r5 = r4.mActivityRef
                    java.lang.Object r5 = r5.get()
                    android.content.Context r5 = (android.content.Context) r5
                    r0 = 0
                    if (r5 != 0) goto L_0x000c
                    return r0
                L_0x000c:
                    java.lang.String r1 = r4.picturePath     // Catch:{ Throwable -> 0x003f, all -> 0x003c }
                    android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r1)     // Catch:{ Throwable -> 0x003f, all -> 0x003c }
                    java.lang.String r2 = "wallpaper.png"
                    java.io.File r5 = r5.getFileStreamPath(r2)     // Catch:{ Throwable -> 0x0039, all -> 0x0037 }
                    java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ Throwable -> 0x0039, all -> 0x0037 }
                    r2.<init>(r5)     // Catch:{ Throwable -> 0x0039, all -> 0x0037 }
                    android.graphics.Bitmap$CompressFormat r5 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ Throwable -> 0x0035 }
                    r3 = 90
                    r1.compress(r5, r3, r2)     // Catch:{ Throwable -> 0x0035 }
                    r2.close()     // Catch:{ IOException -> 0x0028 }
                    goto L_0x002c
                L_0x0028:
                    r5 = move-exception
                    r5.printStackTrace()
                L_0x002c:
                    if (r1 == 0) goto L_0x005a
                    boolean r5 = r1.isRecycled()
                    if (r5 != 0) goto L_0x005a
                    goto L_0x0057
                L_0x0035:
                    r5 = move-exception
                    goto L_0x0042
                L_0x0037:
                    r5 = move-exception
                    goto L_0x005d
                L_0x0039:
                    r5 = move-exception
                    r2 = r0
                    goto L_0x0042
                L_0x003c:
                    r5 = move-exception
                    r1 = r0
                    goto L_0x005d
                L_0x003f:
                    r5 = move-exception
                    r1 = r0
                    r2 = r1
                L_0x0042:
                    r5.printStackTrace()     // Catch:{ all -> 0x005b }
                    if (r2 == 0) goto L_0x004f
                    r2.close()     // Catch:{ IOException -> 0x004b }
                    goto L_0x004f
                L_0x004b:
                    r5 = move-exception
                    r5.printStackTrace()
                L_0x004f:
                    if (r1 == 0) goto L_0x005a
                    boolean r5 = r1.isRecycled()
                    if (r5 != 0) goto L_0x005a
                L_0x0057:
                    r1.recycle()
                L_0x005a:
                    return r0
                L_0x005b:
                    r5 = move-exception
                    r0 = r2
                L_0x005d:
                    if (r0 == 0) goto L_0x0067
                    r0.close()     // Catch:{ IOException -> 0x0063 }
                    goto L_0x0067
                L_0x0063:
                    r0 = move-exception
                    r0.printStackTrace()
                L_0x0067:
                    if (r1 == 0) goto L_0x0072
                    boolean r0 = r1.isRecycled()
                    if (r0 != 0) goto L_0x0072
                    r1.recycle()
                L_0x0072:
                    throw r5
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.SettingsActivity.LauncherSettingsFragment.WallpaperChooseTask.doInBackground(java.lang.Void[]):java.lang.Void");
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void voidR) {
                super.onPostExecute(voidR);
                try {
                    ProgressDialog progressDialog = (ProgressDialog) this.mRef.get();
                    if (progressDialog != null) {
                        progressDialog.hide();
                    }
                } catch (Throwable unused) {
                }
            }
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            addPreferencesFromResource(C0622R.xml.launcher_preferences);
            ContentResolver contentResolver = getActivity().getContentResolver();
            Preference findPreference = findPreference(Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
            if (getResources().getBoolean(C0622R.bool.allow_rotation)) {
                getPreferenceScreen().removePreference(findPreference);
            } else {
                this.mRotationLockObserver = new SystemDisplayRotationLockObserver(findPreference, contentResolver);
                this.mRotationLockObserver.register("accelerometer_rotation", new String[0]);
                findPreference.setDefaultValue(Boolean.valueOf(Utilities.getAllowRotationDefaultValue(getActivity())));
            }
            Preference findPreference2 = findPreference(IconShapeOverride.KEY_PREFERENCE);
            if (findPreference2 != null) {
                if (IconShapeOverride.isSupported(getActivity())) {
                    IconShapeOverride.handlePreferenceUi((ListPreference) findPreference2);
                } else {
                    getPreferenceScreen().removePreference(findPreference2);
                }
            }
            ButtonPreference buttonPreference = (ButtonPreference) findPreference("change_wallpaper");
            if (buttonPreference != null) {
                buttonPreference.setOnPreferenceClickListener(new OnPreferenceClickListener(buttonPreference) {
                    private final /* synthetic */ ButtonPreference f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onPreferenceClick(Preference preference) {
                        return LauncherSettingsFragment.lambda$onCreate$4(LauncherSettingsFragment.this, this.f$1, preference);
                    }
                });
            }
        }

        public static /* synthetic */ boolean lambda$onCreate$4(LauncherSettingsFragment launcherSettingsFragment, ButtonPreference buttonPreference, Preference preference) {
            try {
                PopupMenu popupMenu = new PopupMenu(launcherSettingsFragment.getActivity(), buttonPreference.getView());
                popupMenu.inflate(C0622R.C0626menu.change_wallpaper);
                popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    public final boolean onMenuItemClick(MenuItem menuItem) {
                        return LauncherSettingsFragment.lambda$null$3(LauncherSettingsFragment.this, menuItem);
                    }
                });
                popupMenu.show();
            } catch (Throwable th) {
                th.printStackTrace();
            }
            return false;
        }

        public static /* synthetic */ boolean lambda$null$3(LauncherSettingsFragment launcherSettingsFragment, MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == C0622R.C0625id.action_wallpaper_restore) {
                Activity activity = launcherSettingsFragment.getActivity();
                if (activity != null) {
                    activity.getFileStreamPath(SettingsActivity.WALLPAPER_FILE).delete();
                }
            } else if (itemId == C0622R.C0625id.action_wallpaper_set) {
                Intent intent = new Intent("android.intent.action.PICK", null);
                intent.setDataAndType(Media.EXTERNAL_CONTENT_URI, "image/*");
                try {
                    launcherSettingsFragment.startActivityForResult(intent, 1);
                } catch (Throwable unused) {
                }
            }
            return false;
        }

        public void onDestroy() {
            if (this.mRotationLockObserver != null) {
                this.mRotationLockObserver.unregister();
                this.mRotationLockObserver = null;
            }
            if (this.mIconBadgingObserver != null) {
                this.mIconBadgingObserver.unregister();
                this.mIconBadgingObserver = null;
            }
            super.onDestroy();
        }

        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i2 == -1 && i == 1) {
                String[] strArr = {"_data"};
                try {
                    Cursor query = getActivity().getContentResolver().query(intent.getData(), strArr, null, null, null);
                    if (query != null) {
                        query.moveToFirst();
                        String string = query.getString(query.getColumnIndex(strArr[0]));
                        query.close();
                        ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setTitle(C0622R.string.wallpaper_changing);
                        progressDialog.setCancelable(false);
                        new WallpaperChooseTask(progressDialog, string).execute(new Void[0]);
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
    }

    public static class NotificationAccessConfirmation extends DialogFragment implements OnClickListener {
        public Dialog onCreateDialog(Bundle bundle) {
            Activity activity = getActivity();
            return new Builder(activity).setTitle(C0622R.string.title_missing_notification_access).setMessage(activity.getString(C0622R.string.msg_missing_notification_access, new Object[]{activity.getString(C0622R.string.derived_app_name)})).setNegativeButton(17039360, null).setPositiveButton(C0622R.string.title_change_settings, this).create();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            getActivity().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(268435456).putExtra(":settings:fragment_args_key", new ComponentName(getActivity(), NotificationListener.class).flattenToString()));
        }
    }

    private static class SystemDisplayRotationLockObserver extends System {
        private final Preference mRotationPref;

        public SystemDisplayRotationLockObserver(Preference preference, ContentResolver contentResolver) {
            super(contentResolver);
            this.mRotationPref = preference;
        }

        public void onSettingChanged(boolean z) {
            this.mRotationPref.setEnabled(z);
            this.mRotationPref.setSummary(z ? C0622R.string.allow_rotation_desc : C0622R.string.allow_rotation_blocked_desc);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, new LauncherSettingsFragment()).commit();
        }
    }
}
