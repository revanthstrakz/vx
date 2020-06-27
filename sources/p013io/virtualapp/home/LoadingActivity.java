package p013io.virtualapp.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.p001v4.app.ActivityCompat;
import android.support.p004v7.app.AlertDialog.Builder;
import android.util.Log;
import android.widget.Toast;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.core.VirtualCore.UiCallback;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.utils.VLog;
import io.va.exposed.R;
import java.util.Set;
import jonathanfinerty.once.Once;
import org.jdeferred.DoneCallback;
import p013io.virtualapp.abs.p014ui.VActivity;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.home.models.PackageAppData;
import p013io.virtualapp.widgets.EatBeansView;

/* renamed from: io.virtualapp.home.LoadingActivity */
public class LoadingActivity extends VActivity {
    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final String TAG = "LoadingActivity";
    private PackageAppData appModel;
    private Intent intentToLaunch;
    private EatBeansView loadingView;
    private final UiCallback mUiCallback = new UiCallback() {
        static /* synthetic */ void lambda$onOpenFailed$53() {
        }

        public void onAppOpened(String str, int i) throws RemoteException {
            LoadingActivity.this.finish();
        }

        public void onOpenFailed(String str, int i) throws RemoteException {
            VUiKit.defer().when((Runnable) $$Lambda$LoadingActivity$1$PsTpepIbCMlTopsTW6bP4aD0Y.INSTANCE).done(new DoneCallback(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onDone(Object obj) {
                    C12561.lambda$onOpenFailed$54(C12561.this, this.f$1, (Void) obj);
                }
            });
            LoadingActivity.this.finish();
        }

        public static /* synthetic */ void lambda$onOpenFailed$54(C12561 r4, String str, Void voidR) {
            if (!LoadingActivity.this.isFinishing()) {
                Toast.makeText(LoadingActivity.this.getApplicationContext(), LoadingActivity.this.getResources().getString(R.string.start_app_failed, new Object[]{str}), 0).show();
            }
        }
    };
    private long start;
    private int userToLaunch;

    public static boolean launch(Context context, String str, int i) {
        Intent launchIntent = VirtualCore.get().getLaunchIntent(str, i);
        if (launchIntent == null) {
            return false;
        }
        Intent intent = new Intent(context, LoadingActivity.class);
        intent.putExtra(Constants.PASS_PKG_NAME_ARGUMENT, str);
        intent.addFlags(268435456);
        intent.putExtra(Constants.PASS_KEY_INTENT, launchIntent);
        intent.putExtra(Constants.PASS_KEY_USER, i);
        context.startActivity(intent);
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00f8 A[Catch:{ Throwable -> 0x00fc }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r9) {
        /*
            r8 = this;
            super.onCreate(r9)
            long r0 = android.os.SystemClock.elapsedRealtime()
            r8.start = r0
            r9 = 2131558432(0x7f0d0020, float:1.874218E38)
            r8.setContentView(r9)
            r9 = 2131362011(0x7f0a00db, float:1.834379E38)
            android.view.View r9 = r8.findViewById(r9)
            io.virtualapp.widgets.EatBeansView r9 = (p013io.virtualapp.widgets.EatBeansView) r9
            r8.loadingView = r9
            android.content.Intent r9 = r8.getIntent()
            java.lang.String r0 = "KEY_USER"
            r1 = -1
            int r9 = r9.getIntExtra(r0, r1)
            android.content.Intent r0 = r8.getIntent()
            java.lang.String r1 = "MODEL_ARGUMENT"
            java.lang.String r0 = r0.getStringExtra(r1)
            io.virtualapp.home.repo.PackageAppDataStorage r1 = p013io.virtualapp.home.repo.PackageAppDataStorage.get()
            io.virtualapp.home.models.PackageAppData r1 = r1.acquire(r0)
            r8.appModel = r1
            io.virtualapp.home.models.PackageAppData r1 = r8.appModel
            r2 = 0
            if (r1 != 0) goto L_0x0063
            android.content.Context r9 = r8.getApplicationContext()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Open App:"
            r1.append(r3)
            r1.append(r0)
            java.lang.String r0 = " failed."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            android.widget.Toast r9 = android.widget.Toast.makeText(r9, r0, r2)
            r9.show()
            r8.finish()
            return
        L_0x0063:
            r1 = 2131361851(0x7f0a003b, float:1.8343466E38)
            android.view.View r1 = r8.findViewById(r1)
            android.widget.ImageView r1 = (android.widget.ImageView) r1
            io.virtualapp.home.models.PackageAppData r3 = r8.appModel
            android.graphics.drawable.Drawable r3 = r3.icon
            r1.setImageDrawable(r3)
            r1 = 2131361852(0x7f0a003c, float:1.8343468E38)
            android.view.View r1 = r8.findViewById(r1)
            android.widget.TextView r1 = (android.widget.TextView) r1
            java.util.Locale r3 = java.util.Locale.ENGLISH
            java.lang.String r4 = "Opening %s..."
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            io.virtualapp.home.models.PackageAppData r7 = r8.appModel
            java.lang.String r7 = r7.name
            r6[r2] = r7
            java.lang.String r3 = java.lang.String.format(r3, r4, r6)
            r1.setText(r3)
            android.content.Intent r1 = r8.getIntent()
            java.lang.String r3 = "KEY_INTENT"
            android.os.Parcelable r1 = r1.getParcelableExtra(r3)
            android.content.Intent r1 = (android.content.Intent) r1
            if (r1 != 0) goto L_0x00a2
            r8.finish()
            return
        L_0x00a2:
            com.lody.virtual.client.core.VirtualCore r3 = com.lody.virtual.client.core.VirtualCore.get()
            com.lody.virtual.client.core.VirtualCore$UiCallback r4 = r8.mUiCallback
            r3.setUiCallback(r1, r4)
            java.lang.String r3 = "activity"
            java.lang.Object r3 = r8.getSystemService(r3)     // Catch:{ Throwable -> 0x00fc }
            android.app.ActivityManager r3 = (android.app.ActivityManager) r3     // Catch:{ Throwable -> 0x00fc }
            if (r3 == 0) goto L_0x00da
            java.util.List r3 = r3.getRunningAppProcesses()     // Catch:{ Throwable -> 0x00fc }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ Throwable -> 0x00fc }
        L_0x00bd:
            boolean r4 = r3.hasNext()     // Catch:{ Throwable -> 0x00fc }
            if (r4 == 0) goto L_0x00da
            java.lang.Object r4 = r3.next()     // Catch:{ Throwable -> 0x00fc }
            android.app.ActivityManager$RunningAppProcessInfo r4 = (android.app.ActivityManager.RunningAppProcessInfo) r4     // Catch:{ Throwable -> 0x00fc }
            com.lody.virtual.client.ipc.VActivityManager r6 = com.lody.virtual.client.ipc.VActivityManager.get()     // Catch:{ Throwable -> 0x00fc }
            int r4 = r4.pid     // Catch:{ Throwable -> 0x00fc }
            java.lang.String r4 = r6.getAppProcessName(r4)     // Catch:{ Throwable -> 0x00fc }
            boolean r4 = android.text.TextUtils.equals(r4, r0)     // Catch:{ Throwable -> 0x00fc }
            if (r4 == 0) goto L_0x00bd
            goto L_0x00db
        L_0x00da:
            r5 = 0
        L_0x00db:
            java.lang.String r3 = "LoadingActivity"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x00fc }
            r4.<init>()     // Catch:{ Throwable -> 0x00fc }
            r4.append(r0)     // Catch:{ Throwable -> 0x00fc }
            java.lang.String r0 = "is running: "
            r4.append(r0)     // Catch:{ Throwable -> 0x00fc }
            r4.append(r5)     // Catch:{ Throwable -> 0x00fc }
            java.lang.String r0 = r4.toString()     // Catch:{ Throwable -> 0x00fc }
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ Throwable -> 0x00fc }
            com.lody.virtual.helper.utils.VLog.m89i(r3, r0, r2)     // Catch:{ Throwable -> 0x00fc }
            if (r5 == 0) goto L_0x0100
            r8.launchActivity(r1, r9)     // Catch:{ Throwable -> 0x00fc }
            return
        L_0x00fc:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0100:
            r8.checkAndLaunch(r1, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.home.LoadingActivity.onCreate(android.os.Bundle):void");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(5:21|22|23|24|25) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x00f9 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkAndLaunch(android.content.Intent r11, int r12) {
        /*
            r10 = this;
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 23
            if (r0 >= r1) goto L_0x0011
            java.lang.String r0 = "LoadingActivity"
            java.lang.String r1 = "device's api level below Android M, do not need runtime permission."
            android.util.Log.i(r0, r1)
            r10.launchActivityWithDelay(r11, r12)
            return
        L_0x0011:
            io.virtualapp.home.models.PackageAppData r0 = r10.appModel
            java.lang.String r0 = r0.packageName
            io.virtualapp.home.models.PackageAppData r2 = r10.appModel
            java.lang.String r2 = r2.name
            com.lody.virtual.client.ipc.VPackageManager r3 = com.lody.virtual.client.ipc.VPackageManager.get()     // Catch:{ Throwable -> 0x0117 }
            r4 = 0
            android.content.pm.ApplicationInfo r3 = r3.getApplicationInfo(r0, r4, r4)     // Catch:{ Throwable -> 0x0117 }
            int r3 = r3.targetSdkVersion     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r5 = "LoadingActivity"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0117 }
            r6.<init>()     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r7 = "target package: "
            r6.append(r7)     // Catch:{ Throwable -> 0x0117 }
            r6.append(r0)     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r7 = " targetSdkVersion: "
            r6.append(r7)     // Catch:{ Throwable -> 0x0117 }
            r6.append(r3)     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r6 = r6.toString()     // Catch:{ Throwable -> 0x0117 }
            android.util.Log.i(r5, r6)     // Catch:{ Throwable -> 0x0117 }
            if (r3 < r1) goto L_0x0050
            java.lang.String r0 = "LoadingActivity"
            java.lang.String r1 = "target package support runtime permission, launch directly."
            android.util.Log.i(r0, r1)     // Catch:{ Throwable -> 0x0117 }
            r10.launchActivityWithDelay(r11, r12)     // Catch:{ Throwable -> 0x0117 }
            goto L_0x0122
        L_0x0050:
            r10.intentToLaunch = r11     // Catch:{ Throwable -> 0x0117 }
            r10.userToLaunch = r12     // Catch:{ Throwable -> 0x0117 }
            com.lody.virtual.client.ipc.VPackageManager r1 = com.lody.virtual.client.ipc.VPackageManager.get()     // Catch:{ Throwable -> 0x0117 }
            r3 = 4096(0x1000, float:5.74E-42)
            android.content.pm.PackageInfo r0 = r1.getPackageInfo(r0, r3, r4)     // Catch:{ Throwable -> 0x0117 }
            java.lang.String[] r0 = r0.requestedPermissions     // Catch:{ Throwable -> 0x0117 }
            java.util.HashSet r1 = new java.util.HashSet     // Catch:{ Throwable -> 0x0117 }
            r1.<init>()     // Catch:{ Throwable -> 0x0117 }
            int r3 = r0.length     // Catch:{ Throwable -> 0x0117 }
            r5 = 0
        L_0x0067:
            if (r5 >= r3) goto L_0x009b
            r6 = r0[r5]     // Catch:{ Throwable -> 0x0117 }
            java.util.Set<java.lang.String> r7 = com.lody.virtual.server.p009pm.parser.VPackage.PermissionComponent.DANGEROUS_PERMISSION     // Catch:{ Throwable -> 0x0117 }
            boolean r7 = r7.contains(r6)     // Catch:{ Throwable -> 0x0117 }
            if (r7 == 0) goto L_0x0098
            int r7 = android.support.p001v4.content.ContextCompat.checkSelfPermission(r10, r6)     // Catch:{ Throwable -> 0x0117 }
            if (r7 == 0) goto L_0x007d
            r1.add(r6)     // Catch:{ Throwable -> 0x0117 }
            goto L_0x0098
        L_0x007d:
            java.lang.String r7 = "LoadingActivity"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0117 }
            r8.<init>()     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r9 = "permission: "
            r8.append(r9)     // Catch:{ Throwable -> 0x0117 }
            r8.append(r6)     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r6 = " is granted, ignore."
            r8.append(r6)     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r6 = r8.toString()     // Catch:{ Throwable -> 0x0117 }
            android.util.Log.i(r7, r6)     // Catch:{ Throwable -> 0x0117 }
        L_0x0098:
            int r5 = r5 + 1
            goto L_0x0067
        L_0x009b:
            boolean r0 = r1.isEmpty()     // Catch:{ Throwable -> 0x0117 }
            if (r0 == 0) goto L_0x00ac
            java.lang.String r0 = "LoadingActivity"
            java.lang.String r1 = "all permission are granted, launch directly."
            android.util.Log.i(r0, r1)     // Catch:{ Throwable -> 0x0117 }
            r10.launchActivityWithDelay(r11, r12)     // Catch:{ Throwable -> 0x0117 }
            goto L_0x0122
        L_0x00ac:
            java.lang.String r0 = "LoadingActivity"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0117 }
            r3.<init>()     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r5 = "request permission: "
            r3.append(r5)     // Catch:{ Throwable -> 0x0117 }
            r3.append(r1)     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r3 = r3.toString()     // Catch:{ Throwable -> 0x0117 }
            android.util.Log.i(r0, r3)     // Catch:{ Throwable -> 0x0117 }
            android.support.v7.app.AlertDialog$Builder r0 = new android.support.v7.app.AlertDialog$Builder     // Catch:{ Throwable -> 0x0117 }
            r3 = 2131951907(0x7f130123, float:1.9540242E38)
            r0.<init>(r10, r3)     // Catch:{ Throwable -> 0x0117 }
            r3 = 2131886384(0x7f120130, float:1.9407345E38)
            android.support.v7.app.AlertDialog$Builder r0 = r0.setTitle(r3)     // Catch:{ Throwable -> 0x0117 }
            android.content.res.Resources r3 = r10.getResources()     // Catch:{ Throwable -> 0x0117 }
            r5 = 2131886386(0x7f120132, float:1.940735E38)
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Throwable -> 0x0117 }
            r7[r4] = r2     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r2 = r3.getString(r5, r7)     // Catch:{ Throwable -> 0x0117 }
            android.support.v7.app.AlertDialog$Builder r0 = r0.setMessage(r2)     // Catch:{ Throwable -> 0x0117 }
            r2 = 2131886385(0x7f120131, float:1.9407347E38)
            io.virtualapp.home.-$$Lambda$LoadingActivity$FZIVVHNwRNMYSgOXJDNueS35PUQ r3 = new io.virtualapp.home.-$$Lambda$LoadingActivity$FZIVVHNwRNMYSgOXJDNueS35PUQ     // Catch:{ Throwable -> 0x0117 }
            r3.<init>(r1)     // Catch:{ Throwable -> 0x0117 }
            android.support.v7.app.AlertDialog$Builder r0 = r0.setPositiveButton(r2, r3)     // Catch:{ Throwable -> 0x0117 }
            android.support.v7.app.AlertDialog r0 = r0.create()     // Catch:{ Throwable -> 0x0117 }
            r0.show()     // Catch:{ Throwable -> 0x00f9 }
            goto L_0x0122
        L_0x00f9:
            r10.finish()     // Catch:{ Throwable -> 0x0117 }
            android.content.res.Resources r0 = r10.getResources()     // Catch:{ Throwable -> 0x0117 }
            r1 = 2131886432(0x7f120160, float:1.9407443E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]     // Catch:{ Throwable -> 0x0117 }
            io.virtualapp.home.models.PackageAppData r3 = r10.appModel     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r3 = r3.name     // Catch:{ Throwable -> 0x0117 }
            r2[r4] = r3     // Catch:{ Throwable -> 0x0117 }
            java.lang.String r0 = r0.getString(r1, r2)     // Catch:{ Throwable -> 0x0117 }
            android.widget.Toast r0 = android.widget.Toast.makeText(r10, r0, r4)     // Catch:{ Throwable -> 0x0117 }
            r0.show()     // Catch:{ Throwable -> 0x0117 }
            goto L_0x0122
        L_0x0117:
            r0 = move-exception
            java.lang.String r1 = "LoadingActivity"
            java.lang.String r2 = "check permission failed: "
            android.util.Log.e(r1, r2, r0)
            r10.launchActivityWithDelay(r11, r12)
        L_0x0122:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.home.LoadingActivity.checkAndLaunch(android.content.Intent, int):void");
    }

    public static /* synthetic */ void lambda$checkAndLaunch$50(LoadingActivity loadingActivity, Set set, DialogInterface dialogInterface, int i) {
        try {
            ActivityCompat.requestPermissions(loadingActivity, (String[]) set.toArray(new String[set.size()]), 100);
        } catch (Throwable unused) {
        }
    }

    private void launchActivityWithDelay(Intent intent, int i) {
        long elapsedRealtime = 1000 - (SystemClock.elapsedRealtime() - this.start);
        if (elapsedRealtime <= 0) {
            launchActivity(intent, i);
        } else {
            this.loadingView.postDelayed(new Runnable(intent, i) {
                private final /* synthetic */ Intent f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    LoadingActivity.this.launchActivity(this.f$1, this.f$2);
                }
            }, elapsedRealtime);
        }
    }

    /* access modifiers changed from: private */
    public void launchActivity(Intent intent, int i) {
        try {
            VActivityManager.get().startActivity(intent, i);
        } catch (Throwable th) {
            VLog.m87e(TAG, "start activity failed:", th);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.start_app_failed, new Object[]{this.appModel.name}), 0).show();
            finish();
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        boolean z;
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 100) {
            int length = iArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    z = true;
                    break;
                } else if (iArr[i2] == -1) {
                    z = false;
                    break;
                } else {
                    i2++;
                }
            }
            if (!z) {
                Log.i(TAG, "can not use runtime permission, you must grant all permission, otherwise the app may not work!");
                StringBuilder sb = new StringBuilder();
                sb.append("permission_tips_");
                sb.append(this.appModel.packageName.replaceAll("\\.", "_"));
                String sb2 = sb.toString();
                if (!Once.beenDone(sb2)) {
                    try {
                        new Builder(this, 2131951907).setTitle(17039380).setMessage((CharSequence) getResources().getString(R.string.permission_denied_tips_content, new Object[]{this.appModel.name})).setPositiveButton((int) R.string.permission_tips_confirm, (OnClickListener) new OnClickListener(sb2) {
                            private final /* synthetic */ String f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                LoadingActivity.lambda$onRequestPermissionsResult$52(LoadingActivity.this, this.f$1, dialogInterface, i);
                            }
                        }).create().show();
                    } catch (Throwable unused) {
                        Toast.makeText(this, getResources().getString(R.string.start_app_failed, new Object[]{this.appModel.name}), 0).show();
                    }
                } else {
                    launchActivityWithDelay(this.intentToLaunch, this.userToLaunch);
                    finish();
                }
            } else if (this.intentToLaunch == null) {
                Toast.makeText(this, getResources().getString(R.string.start_app_failed, new Object[]{this.appModel.name}), 0).show();
                finish();
            } else {
                launchActivityWithDelay(this.intentToLaunch, this.userToLaunch);
            }
        }
    }

    public static /* synthetic */ void lambda$onRequestPermissionsResult$52(LoadingActivity loadingActivity, String str, DialogInterface dialogInterface, int i) {
        loadingActivity.finish();
        Once.markDone(str);
        loadingActivity.launchActivityWithDelay(loadingActivity.intentToLaunch, loadingActivity.userToLaunch);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        startAnim();
    }

    private void startAnim() {
        if (this.loadingView != null) {
            this.loadingView.startAnim();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.loadingView != null) {
            this.loadingView.stopAnim();
        }
    }
}
