package p013io.virtualapp.home;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import com.android.launcher3.LauncherFiles;
import com.google.android.apps.nexuslauncher.NexusLauncherActivity;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.DeviceUtil;
import io.va.exposed.R;
import java.io.File;
import java.lang.reflect.Method;
import jonathanfinerty.once.Once;
import p013io.virtualapp.XApp;
import p013io.virtualapp.settings.SettingsActivity;
import p013io.virtualapp.update.VAVersionService;
import p013io.virtualapp.utils.Misc;

/* renamed from: io.virtualapp.home.NewHomeActivity */
public class NewHomeActivity extends NexusLauncherActivity {
    private static final String SHOW_DOZE_ALERT_KEY = "SHOW_DOZE_ALERT_KEY";
    private static final String WALLPAPER_FILE_NAME = "wallpaper.png";
    private boolean checkXposedInstaller = true;
    private boolean mDirectlyBack = false;
    private Handler mUiHandler;

    static /* synthetic */ void lambda$null$73(DialogInterface dialogInterface, int i) {
    }

    public Activity getActivity() {
        return this;
    }

    public Context getContext() {
        return this;
    }

    public static void goHome(Context context) {
        Intent intent = new Intent(context, NewHomeActivity.class);
        intent.addFlags(131072);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public void onCreate(Bundle bundle) {
        SharedPreferences sharedPreferences = getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, 0);
        super.onCreate(bundle);
        showMenuKey();
        this.mUiHandler = new Handler(getMainLooper());
        alertForMeizu();
        alertForDonate();
        this.mDirectlyBack = sharedPreferences.getBoolean(SettingsActivity.DIRECTLY_BACK_KEY, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:19:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void installXposed() {
        /*
            r6 = this;
            r0 = 0
            com.lody.virtual.client.core.VirtualCore r1 = com.lody.virtual.client.core.VirtualCore.get()     // Catch:{ Throwable -> 0x002e }
            java.lang.String r2 = "de.robv.android.xposed.installer"
            boolean r1 = r1.isAppInstalled(r2)     // Catch:{ Throwable -> 0x002e }
            java.lang.String r2 = "XposedInstaller_1_31.apk"
            java.io.File r2 = r6.getFileStreamPath(r2)     // Catch:{ Throwable -> 0x002c }
            boolean r3 = r2.exists()     // Catch:{ Throwable -> 0x002c }
            if (r3 == 0) goto L_0x003d
            com.lody.virtual.client.core.VirtualCore r3 = com.lody.virtual.client.core.VirtualCore.get()     // Catch:{ Throwable -> 0x002c }
            java.lang.String r4 = "de.robv.android.xposed.installer"
            r3.uninstallPackage(r4)     // Catch:{ Throwable -> 0x002c }
            r2.delete()     // Catch:{ Throwable -> 0x002c }
            java.lang.String r1 = "Launcher"
            java.lang.String r2 = "remove xposed installer success!"
            android.util.Log.d(r1, r2)     // Catch:{ Throwable -> 0x002e }
            r1 = 0
            goto L_0x003d
        L_0x002c:
            r2 = move-exception
            goto L_0x0031
        L_0x002e:
            r1 = move-exception
            r2 = r1
            r1 = 0
        L_0x0031:
            java.lang.String r3 = "Launcher"
            java.lang.String r4 = "remove xposed install failed."
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r0] = r2
            com.lody.virtual.helper.utils.VLog.m86d(r3, r4, r5)
        L_0x003d:
            if (r1 != 0) goto L_0x0076
            android.app.ProgressDialog r1 = new android.app.ProgressDialog
            r1.<init>(r6)
            r1.setCancelable(r0)
            android.content.res.Resources r0 = r6.getResources()
            r2 = 2131886394(0x7f12013a, float:1.9407366E38)
            java.lang.String r0 = r0.getString(r2)
            r1.setMessage(r0)
            r1.show()
            org.jdeferred.android.AndroidDeferredManager r0 = p013io.virtualapp.abs.p014ui.VUiKit.defer()
            io.virtualapp.home.-$$Lambda$NewHomeActivity$typsp6oLQHegMnrqiJ1k29o_OjM r2 = new io.virtualapp.home.-$$Lambda$NewHomeActivity$typsp6oLQHegMnrqiJ1k29o_OjM
            r2.<init>()
            org.jdeferred.Promise r0 = r0.when(r2)
            io.virtualapp.home.-$$Lambda$NewHomeActivity$LzGRbCzZpYOEIvwaQTFb8ONdwtY r2 = new io.virtualapp.home.-$$Lambda$NewHomeActivity$LzGRbCzZpYOEIvwaQTFb8ONdwtY
            r2.<init>(r1)
            org.jdeferred.Promise r0 = r0.then(r2)
            io.virtualapp.home.-$$Lambda$NewHomeActivity$wjCcQlyLmMIdjMYX11n56cw1aB4 r2 = new io.virtualapp.home.-$$Lambda$NewHomeActivity$wjCcQlyLmMIdjMYX11n56cw1aB4
            r2.<init>(r1)
            r0.fail(r2)
        L_0x0076:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.home.NewHomeActivity.installXposed():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:48:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$installXposed$68(p013io.virtualapp.home.NewHomeActivity r9) {
        /*
            java.lang.String r0 = "XposedInstaller_5_8.apk"
            java.io.File r0 = r9.getFileStreamPath(r0)
            boolean r1 = r0.exists()
            r2 = 0
            if (r1 != 0) goto L_0x0064
            r1 = 0
            android.content.Context r3 = r9.getApplicationContext()     // Catch:{ Throwable -> 0x0047, all -> 0x0044 }
            android.content.res.AssetManager r3 = r3.getAssets()     // Catch:{ Throwable -> 0x0047, all -> 0x0044 }
            java.lang.String r4 = "XposedInstaller_3.1.5.apk_"
            java.io.InputStream r3 = r3.open(r4)     // Catch:{ Throwable -> 0x0047, all -> 0x0044 }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Throwable -> 0x003e, all -> 0x003a }
            r4.<init>(r0)     // Catch:{ Throwable -> 0x003e, all -> 0x003a }
            r1 = 1024(0x400, float:1.435E-42)
            byte[] r1 = new byte[r1]     // Catch:{ Throwable -> 0x0035, all -> 0x0033 }
        L_0x0025:
            int r5 = r3.read(r1)     // Catch:{ Throwable -> 0x0035, all -> 0x0033 }
            if (r5 <= 0) goto L_0x002f
            r4.write(r1, r2, r5)     // Catch:{ Throwable -> 0x0035, all -> 0x0033 }
            goto L_0x0025
        L_0x002f:
            com.lody.virtual.helper.utils.FileUtils.closeQuietly(r3)
            goto L_0x0058
        L_0x0033:
            r0 = move-exception
            goto L_0x003c
        L_0x0035:
            r1 = move-exception
            r8 = r3
            r3 = r1
            r1 = r8
            goto L_0x0049
        L_0x003a:
            r0 = move-exception
            r4 = r1
        L_0x003c:
            r1 = r3
            goto L_0x005d
        L_0x003e:
            r4 = move-exception
            r8 = r4
            r4 = r1
            r1 = r3
            r3 = r8
            goto L_0x0049
        L_0x0044:
            r0 = move-exception
            r4 = r1
            goto L_0x005d
        L_0x0047:
            r3 = move-exception
            r4 = r1
        L_0x0049:
            java.lang.String r5 = "Launcher"
            java.lang.String r6 = "copy file error"
            r7 = 1
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x005c }
            r7[r2] = r3     // Catch:{ all -> 0x005c }
            com.lody.virtual.helper.utils.VLog.m87e(r5, r6, r7)     // Catch:{ all -> 0x005c }
            com.lody.virtual.helper.utils.FileUtils.closeQuietly(r1)
        L_0x0058:
            com.lody.virtual.helper.utils.FileUtils.closeQuietly(r4)
            goto L_0x0064
        L_0x005c:
            r0 = move-exception
        L_0x005d:
            com.lody.virtual.helper.utils.FileUtils.closeQuietly(r1)
            com.lody.virtual.helper.utils.FileUtils.closeQuietly(r4)
            throw r0
        L_0x0064:
            boolean r1 = r0.isFile()
            if (r1 == 0) goto L_0x0092
            boolean r1 = com.lody.virtual.helper.utils.DeviceUtil.isMeizuBelowN()
            if (r1 != 0) goto L_0x0092
            java.lang.String r1 = "8537fb219128ead3436cc19ff35cfb2e"
            java.lang.String r3 = com.lody.virtual.helper.utils.MD5Utils.getFileMD5String(r0)     // Catch:{ Throwable -> 0x0092 }
            boolean r1 = r1.equals(r3)     // Catch:{ Throwable -> 0x0092 }
            if (r1 == 0) goto L_0x0089
            com.lody.virtual.client.core.VirtualCore r1 = com.lody.virtual.client.core.VirtualCore.get()     // Catch:{ Throwable -> 0x0092 }
            java.lang.String r0 = r0.getPath()     // Catch:{ Throwable -> 0x0092 }
            r2 = 2
            r1.installPackage(r0, r2)     // Catch:{ Throwable -> 0x0092 }
            goto L_0x0092
        L_0x0089:
            java.lang.String r0 = "Launcher"
            java.lang.String r1 = "unknown Xposed installer, ignore!"
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ Throwable -> 0x0092 }
            com.lody.virtual.helper.utils.VLog.m91w(r0, r1, r2)     // Catch:{ Throwable -> 0x0092 }
        L_0x0092:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.home.NewHomeActivity.lambda$installXposed$68(io.virtualapp.home.NewHomeActivity):void");
    }

    /* access modifiers changed from: private */
    public static void dismissDialog(ProgressDialog progressDialog) {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.checkXposedInstaller) {
            this.checkXposedInstaller = false;
            installXposed();
        }
        new Handler().postDelayed(new Runnable() {
            public final void run() {
                VAVersionService.checkUpdate(NewHomeActivity.this.getApplicationContext(), false);
            }
        }, 1000);
        setWallpaper();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 82) {
            return super.onKeyDown(i, keyEvent);
        }
        onSettingsClicked();
        return true;
    }

    public void onClickAddWidgetButton(View view) {
        onAddAppClicked();
    }

    private void onAddAppClicked() {
        ListAppActivity.gotoListApp(this);
    }

    private void onSettingsClicked() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onClickSettingsButton(View view) {
        onSettingsClicked();
    }

    /* access modifiers changed from: protected */
    public void onClickAllAppsButton(View view) {
        onSettingsClicked();
    }

    public void startVirtualActivity(Intent intent, Bundle bundle, int i) {
        String str = intent.getPackage();
        if (TextUtils.isEmpty(str)) {
            ComponentName component = intent.getComponent();
            if (component != null) {
                str = component.getPackageName();
            }
        }
        if (str == null) {
            try {
                startActivity(intent);
                return;
            } catch (Throwable unused) {
            }
        }
        if (LoadingActivity.launch(this, str, i)) {
            if (this.mDirectlyBack) {
                finish();
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("can not launch activity for :");
        sb.append(intent);
        throw new ActivityNotFoundException(sb.toString());
    }

    private void alertForDonate() {
        if (Once.beenDone(1, "show_donate")) {
            alertForDoze();
        } else {
            try {
                new Builder(getContext()).setTitle(R.string.about_donate).setMessage(R.string.donate_dialog_content).setPositiveButton(17039379, new OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        NewHomeActivity.lambda$alertForDonate$72(NewHomeActivity.this, dialogInterface, i);
                    }
                }).create().show();
            } catch (Throwable unused) {
            }
        }
    }

    public static /* synthetic */ void lambda$alertForDonate$72(NewHomeActivity newHomeActivity, DialogInterface dialogInterface, int i) {
        Misc.showDonate(newHomeActivity);
        Once.markDone("show_donate");
    }

    private void alertForMeizu() {
        if (DeviceUtil.isMeizuBelowN() && !VirtualCore.get().isAppInstalled(XApp.XPOSED_INSTALLER_PACKAGE)) {
            this.mUiHandler.postDelayed(new Runnable() {
                public final void run() {
                    NewHomeActivity.lambda$alertForMeizu$74(NewHomeActivity.this);
                }
            }, 2000);
        }
    }

    public static /* synthetic */ void lambda$alertForMeizu$74(NewHomeActivity newHomeActivity) {
        try {
            new Builder(newHomeActivity.getContext()).setTitle(R.string.meizu_device_tips_title).setMessage(R.string.meizu_device_tips_content).setPositiveButton(17039379, $$Lambda$NewHomeActivity$gC1tDm_d3TDYA10oC8HZTSw8RGA.INSTANCE).create().show();
        } catch (Throwable unused) {
        }
    }

    private void alertForDoze() {
        if (VERSION.SDK_INT >= 23) {
            PowerManager powerManager = (PowerManager) getSystemService("power");
            if (powerManager != null && PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SHOW_DOZE_ALERT_KEY, true) && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                this.mUiHandler.postDelayed(new Runnable() {
                    public final void run() {
                        NewHomeActivity.lambda$alertForDoze$77(NewHomeActivity.this);
                    }
                }, 1000);
            }
        }
    }

    public static /* synthetic */ void lambda$alertForDoze$77(NewHomeActivity newHomeActivity) {
        try {
            new Builder(newHomeActivity.getContext()).setTitle(R.string.alert_for_doze_mode_title).setMessage(R.string.alert_for_doze_mode_content).setPositiveButton(R.string.alert_for_doze_mode_yes, new OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    NewHomeActivity.lambda$null$75(NewHomeActivity.this, dialogInterface, i);
                }
            }).setNegativeButton(R.string.alert_for_doze_mode_no, new OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PreferenceManager.getDefaultSharedPreferences(NewHomeActivity.this.getActivity()).edit().putBoolean(NewHomeActivity.SHOW_DOZE_ALERT_KEY, false).apply();
                }
            }).create().show();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        return;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x003b */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$null$75(p013io.virtualapp.home.NewHomeActivity r3, android.content.DialogInterface r4, int r5) {
        /*
            r4 = 0
            android.content.Intent r5 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            java.lang.String r0 = "android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            r1.<init>()     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            java.lang.String r2 = "package:"
            r1.append(r2)     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            java.lang.String r2 = r3.getPackageName()     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            r1.append(r2)     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            java.lang.String r1 = r1.toString()     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            r5.<init>(r0, r1)     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            r3.startActivity(r5)     // Catch:{ ActivityNotFoundException -> 0x003b, Throwable -> 0x0025 }
            goto L_0x005b
        L_0x0025:
            android.app.Activity r5 = r3.getActivity()
            android.content.SharedPreferences r5 = android.preference.PreferenceManager.getDefaultSharedPreferences(r5)
            android.content.SharedPreferences$Editor r5 = r5.edit()
            java.lang.String r0 = "SHOW_DOZE_ALERT_KEY"
            android.content.SharedPreferences$Editor r4 = r5.putBoolean(r0, r4)
            r4.apply()
            goto L_0x005b
        L_0x003b:
            android.content.Intent r5 = new android.content.Intent     // Catch:{ Throwable -> 0x0046 }
            java.lang.String r0 = "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS"
            r5.<init>(r0)     // Catch:{ Throwable -> 0x0046 }
            r3.startActivity(r5)     // Catch:{ Throwable -> 0x0046 }
            goto L_0x005b
        L_0x0046:
            android.app.Activity r5 = r3.getActivity()
            android.content.SharedPreferences r5 = android.preference.PreferenceManager.getDefaultSharedPreferences(r5)
            android.content.SharedPreferences$Editor r5 = r5.edit()
            java.lang.String r0 = "SHOW_DOZE_ALERT_KEY"
            android.content.SharedPreferences$Editor r4 = r5.putBoolean(r0, r4)
            r4.apply()
        L_0x005b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.home.NewHomeActivity.lambda$null$75(io.virtualapp.home.NewHomeActivity, android.content.DialogInterface, int):void");
    }

    private void setWallpaper() {
        File fileStreamPath = getFileStreamPath(WALLPAPER_FILE_NAME);
        if (fileStreamPath == null || !fileStreamPath.exists() || fileStreamPath.isDirectory()) {
            setOurWallpaper(getResources().getDrawable(R.drawable.home_bg));
        } else {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            try {
                Drawable createFromPath = BitmapDrawable.createFromPath(fileStreamPath.getPath());
                if (SystemClock.elapsedRealtime() - elapsedRealtime > 200) {
                    Toast.makeText(getApplicationContext(), R.string.wallpaper_too_big_tips, 0).show();
                }
                if (createFromPath == null) {
                    setOurWallpaper(getResources().getDrawable(R.drawable.home_bg));
                } else {
                    setOurWallpaper(createFromPath);
                }
            } catch (Throwable unused) {
                Toast.makeText(getApplicationContext(), R.string.wallpaper_too_big_tips, 0).show();
            }
        }
    }

    private void showMenuKey() {
        try {
            Method declaredMethod = Window.class.getDeclaredMethod("setNeedsMenuKey", new Class[]{Integer.TYPE});
            declaredMethod.setAccessible(true);
            int i = LayoutParams.class.getField("NEEDS_MENU_SET_TRUE").getInt(null);
            declaredMethod.invoke(getWindow(), new Object[]{Integer.valueOf(i)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
