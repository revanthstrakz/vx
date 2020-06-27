package p013io.virtualapp.gms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.support.p004v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Log;
import com.lody.virtual.client.core.VirtualCore;
import io.va.exposed.R;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.utils.DialogUtil;

/* renamed from: io.virtualapp.gms.FakeGms */
public class FakeGms {
    private static final String FAKE_GAPPS_PKG = "com.thermatk.android.xf.fakegapps";
    private static final String GMS_CONFIG_URL = "http://vaexposed.weishu.me/gms.json";
    private static final String GMS_PKG = "com.google.android.gms";
    private static final String GSF_PKG = "com.google.android.gsf";
    private static final String STORE_PKG = "com.android.vending";
    private static final String TAG = "FakeGms";
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    /* renamed from: io.virtualapp.gms.FakeGms$DownloadListener */
    public interface DownloadListener {
        void onProgress(int i);
    }

    public static void uninstallGms(Activity activity) {
        if (activity != null) {
            DialogUtil.showDialog(new Builder(activity, 2131951907).setTitle((int) R.string.uninstall_gms_title).setMessage((int) R.string.uninstall_gms_content).setPositiveButton((int) R.string.uninstall_gms_ok, (OnClickListener) new OnClickListener(activity) {
                private final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    FakeGms.lambda$uninstallGms$90(this.f$0, dialogInterface, i);
                }
            }).setNegativeButton(17039360, (OnClickListener) null).create());
        }
    }

    static /* synthetic */ void lambda$uninstallGms$90(Activity activity, DialogInterface dialogInterface, int i) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        VUiKit.defer().when((Runnable) $$Lambda$FakeGms$MDs_qLBgZHWZCsTpYenXVPAV_g.INSTANCE).then((DoneCallback<D>) new DoneCallback(progressDialog, activity) {
            private final /* synthetic */ ProgressDialog f$0;
            private final /* synthetic */ Activity f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onDone(Object obj) {
                FakeGms.lambda$null$88(this.f$0, this.f$1, (Void) obj);
            }
        }).fail(new FailCallback(progressDialog) {
            private final /* synthetic */ ProgressDialog f$0;

            {
                this.f$0 = r1;
            }

            public final void onFail(Object obj) {
                this.f$0.dismiss();
            }
        });
    }

    static /* synthetic */ void lambda$null$87() {
        VirtualCore.get().uninstallPackage(GMS_PKG);
        VirtualCore.get().uninstallPackage(GSF_PKG);
        VirtualCore.get().uninstallPackage(STORE_PKG);
        VirtualCore.get().uninstallPackage(FAKE_GAPPS_PKG);
    }

    static /* synthetic */ void lambda$null$88(ProgressDialog progressDialog, Activity activity, Void voidR) {
        progressDialog.dismiss();
        DialogUtil.showDialog(new Builder(activity, 2131951907).setTitle((int) R.string.uninstall_gms_title).setMessage((int) R.string.uninstall_gms_success).setPositiveButton(17039370, (OnClickListener) null).create());
    }

    public static boolean isAlreadyInstalled(Context context) {
        if (context == null) {
            return false;
        }
        boolean isAppInstalled = VirtualCore.get().isAppInstalled(GMS_PKG);
        if (!VirtualCore.get().isAppInstalled(GSF_PKG)) {
            isAppInstalled = false;
        }
        if (!VirtualCore.get().isAppInstalled(STORE_PKG)) {
            isAppInstalled = false;
        }
        if (!VirtualCore.get().isAppInstalled(FAKE_GAPPS_PKG)) {
            isAppInstalled = false;
        }
        return isAppInstalled;
    }

    public static void installGms(Activity activity) {
        if (activity != null) {
            DialogUtil.showDialog(new Builder(activity, 2131951907).setTitle((int) R.string.install_gms_title).setMessage((int) R.string.install_gms_content).setPositiveButton(17039370, (OnClickListener) new OnClickListener(activity) {
                private final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    FakeGms.lambda$installGms$95(this.f$0, dialogInterface, i);
                }
            }).setNegativeButton(17039360, (OnClickListener) null).create());
        }
    }

    static /* synthetic */ void lambda$installGms$95(Activity activity, DialogInterface dialogInterface, int i) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.show();
        executorService.submit(new Runnable(activity, progressDialog) {
            private final /* synthetic */ Activity f$0;
            private final /* synthetic */ ProgressDialog f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                FakeGms.lambda$null$94(this.f$0, this.f$1);
            }
        });
    }

    static /* synthetic */ void lambda$null$94(Activity activity, ProgressDialog progressDialog) {
        String installGmsInternal = installGmsInternal(activity, progressDialog);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("install gms result: ");
        sb.append(installGmsInternal);
        Log.i(str, sb.toString());
        try {
            progressDialog.dismiss();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (installGmsInternal == null) {
            activity.runOnUiThread(new Runnable(activity) {
                private final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    DialogUtil.showDialog(new Builder(this.f$0, 2131951907).setTitle((int) R.string.install_gms_title).setMessage((int) R.string.install_gms_success).setPositiveButton(17039370, (OnClickListener) null).create());
                }
            });
        } else {
            activity.runOnUiThread(new Runnable(activity) {
                private final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    DialogUtil.showDialog(new Builder(this.f$0, 2131951907).setTitle((int) R.string.install_gms_fail_title).setMessage((int) R.string.install_gms_fail_content).setPositiveButton((int) R.string.install_gms_fail_ok, (OnClickListener) new OnClickListener(this.f$0) {
                        private final /* synthetic */ Activity f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            FakeGms.lambda$null$92(this.f$0, dialogInterface, i);
                        }
                    }).setNegativeButton(17039360, (OnClickListener) null).create());
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$92(Activity activity, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://github.com/android-hacker/VirtualXposed/wiki/Google-service-support"));
            activity.startActivity(intent);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x0242 A[SYNTHETIC, Splitter:B:100:0x0242] */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x024e A[SYNTHETIC, Splitter:B:107:0x024e] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String installGmsInternal(android.app.Activity r13, android.app.ProgressDialog r14) {
        /*
            java.io.File r0 = r13.getCacheDir()
            okhttp3.OkHttpClient$Builder r1 = new okhttp3.OkHttpClient$Builder
            r1.<init>()
            java.util.concurrent.TimeUnit r2 = java.util.concurrent.TimeUnit.SECONDS
            r3 = 30
            okhttp3.OkHttpClient$Builder r1 = r1.connectTimeout(r3, r2)
            java.util.concurrent.TimeUnit r2 = java.util.concurrent.TimeUnit.SECONDS
            okhttp3.OkHttpClient$Builder r1 = r1.readTimeout(r3, r2)
            java.util.concurrent.TimeUnit r2 = java.util.concurrent.TimeUnit.SECONDS
            okhttp3.OkHttpClient$Builder r1 = r1.writeTimeout(r3, r2)
            okhttp3.OkHttpClient r1 = r1.build()
            okhttp3.Request$Builder r2 = new okhttp3.Request$Builder
            r2.<init>()
            java.lang.String r3 = "http://vaexposed.weishu.me/gms.json"
            okhttp3.Request$Builder r2 = r2.url(r3)
            okhttp3.Request r2 = r2.build()
            java.lang.String r3 = "Fetching gms config..."
            updateMessage(r13, r14, r3)
            okhttp3.Call r1 = r1.newCall(r2)     // Catch:{ IOException -> 0x0269 }
            okhttp3.Response r1 = r1.execute()     // Catch:{ IOException -> 0x0269 }
            boolean r2 = r1.isSuccessful()
            if (r2 != 0) goto L_0x0046
            java.lang.String r13 = "Download gms config failed, please check your network, error: 1"
            return r13
        L_0x0046:
            java.lang.String r2 = "FakeGms"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "response success: "
            r3.append(r4)
            int r4 = r1.code()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Log.i(r2, r3)
            r2 = 200(0xc8, float:2.8E-43)
            int r3 = r1.code()
            if (r2 == r3) goto L_0x006b
            java.lang.String r13 = "Download gms config failed, please check your network, error: 2"
            return r13
        L_0x006b:
            java.lang.String r2 = "Parsing gms config..."
            updateMessage(r13, r14, r2)
            okhttp3.ResponseBody r1 = r1.body()
            if (r1 != 0) goto L_0x0079
            java.lang.String r13 = "Download gms config failed, please check your network, error: 3"
            return r13
        L_0x0079:
            java.lang.String r1 = r1.string()     // Catch:{ IOException -> 0x0266 }
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0263 }
            r2.<init>(r1)     // Catch:{ JSONException -> 0x0263 }
            java.lang.String r1 = "gms"
            java.lang.String r1 = r2.getString(r1)     // Catch:{ JSONException -> 0x0260 }
            java.lang.String r3 = "gsf"
            java.lang.String r3 = r2.getString(r3)     // Catch:{ JSONException -> 0x025d }
            java.lang.String r4 = "store"
            java.lang.String r4 = r2.getString(r4)     // Catch:{ JSONException -> 0x025a }
            java.lang.String r5 = "fakegapps"
            java.lang.String r5 = r2.getString(r5)     // Catch:{ JSONException -> 0x0257 }
            r6 = 0
            java.lang.String r7 = "yalp"
            java.lang.String r2 = r2.getString(r7)     // Catch:{ JSONException -> 0x00a2 }
            goto L_0x00aa
        L_0x00a2:
            java.lang.String r2 = "FakeGms"
            java.lang.String r7 = "Download gms config failed, please check your network"
            android.util.Log.i(r2, r7)
            r2 = r6
        L_0x00aa:
            java.lang.String r7 = "config parse success!"
            updateMessage(r13, r14, r7)
            java.io.File r7 = new java.io.File
            java.lang.String r8 = "gms.apk"
            r7.<init>(r0, r8)
            java.io.File r8 = new java.io.File
            java.lang.String r9 = "gsf.apk"
            r8.<init>(r0, r9)
            java.io.File r9 = new java.io.File
            java.lang.String r10 = "store.apk"
            r9.<init>(r0, r10)
            java.io.File r10 = new java.io.File
            java.lang.String r11 = "fakegapps.apk"
            r10.<init>(r0, r11)
            java.io.File r11 = new java.io.File
            java.lang.String r12 = "yalpStore.apk"
            r11.<init>(r0, r12)
            boolean r0 = r7.exists()
            if (r0 == 0) goto L_0x00db
            r7.delete()
        L_0x00db:
            boolean r0 = r8.exists()
            if (r0 == 0) goto L_0x00e4
            r8.delete()
        L_0x00e4:
            boolean r0 = r9.exists()
            if (r0 == 0) goto L_0x00ed
            r9.delete()
        L_0x00ed:
            boolean r0 = r10.exists()
            if (r0 == 0) goto L_0x00f6
            r10.delete()
        L_0x00f6:
            io.virtualapp.gms.-$$Lambda$FakeGms$x9-eHO7X0KgEeYr-CDKnqkCii9g r0 = new io.virtualapp.gms.-$$Lambda$FakeGms$x9-eHO7X0KgEeYr-CDKnqkCii9g
            r0.<init>(r13, r14)
            boolean r0 = downloadFile(r1, r7, r0)
            if (r0 != 0) goto L_0x0104
            java.lang.String r13 = "Download gms config failed, please check your network, error: 10"
            return r13
        L_0x0104:
            io.virtualapp.gms.-$$Lambda$FakeGms$FRvHhcZFMVc7tmcd44zXlHAZI90 r0 = new io.virtualapp.gms.-$$Lambda$FakeGms$FRvHhcZFMVc7tmcd44zXlHAZI90
            r0.<init>(r13, r14)
            boolean r0 = downloadFile(r3, r8, r0)
            if (r0 != 0) goto L_0x0112
            java.lang.String r13 = "Download gms config failed, please check your network, error: 11"
            return r13
        L_0x0112:
            java.lang.String r0 = "download gms store..."
            updateMessage(r13, r14, r0)
            io.virtualapp.gms.-$$Lambda$FakeGms$aVDwPahNAGC0W-tHD-H76jp4FKs r0 = new io.virtualapp.gms.-$$Lambda$FakeGms$aVDwPahNAGC0W-tHD-H76jp4FKs
            r0.<init>(r13, r14)
            boolean r0 = downloadFile(r4, r9, r0)
            if (r0 != 0) goto L_0x0125
            java.lang.String r13 = "Download gms config failed, please check your network, error: 12"
            return r13
        L_0x0125:
            io.virtualapp.gms.-$$Lambda$FakeGms$we-x4QxGanOClxoVRbr3xplbCLs r0 = new io.virtualapp.gms.-$$Lambda$FakeGms$we-x4QxGanOClxoVRbr3xplbCLs
            r0.<init>(r13, r14)
            boolean r0 = downloadFile(r5, r10, r0)
            if (r0 != 0) goto L_0x0133
            java.lang.String r13 = "Download gms config failed, please check your network, error: 13"
            return r13
        L_0x0133:
            if (r2 == 0) goto L_0x013d
            io.virtualapp.gms.-$$Lambda$FakeGms$QpoHDW0c6BJukYNQ5uhHHwy2bRA r0 = new io.virtualapp.gms.-$$Lambda$FakeGms$QpoHDW0c6BJukYNQ5uhHHwy2bRA
            r0.<init>(r13, r14)
            downloadFile(r2, r11, r0)
        L_0x013d:
            java.lang.String r0 = "installing gms core"
            updateMessage(r13, r14, r0)
            com.lody.virtual.client.core.VirtualCore r0 = com.lody.virtual.client.core.VirtualCore.get()
            java.lang.String r1 = r7.getAbsolutePath()
            r2 = 4
            com.lody.virtual.remote.InstallResult r0 = r0.installPackage(r1, r2)
            boolean r1 = r0.isSuccess
            if (r1 != 0) goto L_0x0167
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "install gms core failed: "
            r13.append(r14)
            java.lang.String r14 = r0.error
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            return r13
        L_0x0167:
            java.lang.String r0 = "installing gms service framework..."
            updateMessage(r13, r14, r0)
            com.lody.virtual.client.core.VirtualCore r0 = com.lody.virtual.client.core.VirtualCore.get()
            java.lang.String r1 = r8.getAbsolutePath()
            com.lody.virtual.remote.InstallResult r0 = r0.installPackage(r1, r2)
            boolean r1 = r0.isSuccess
            if (r1 != 0) goto L_0x0190
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "install gms service framework failed: "
            r13.append(r14)
            java.lang.String r14 = r0.error
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            return r13
        L_0x0190:
            java.lang.String r0 = "installing gms store..."
            updateMessage(r13, r14, r0)
            com.lody.virtual.client.core.VirtualCore r0 = com.lody.virtual.client.core.VirtualCore.get()
            java.lang.String r1 = r9.getAbsolutePath()
            com.lody.virtual.remote.InstallResult r0 = r0.installPackage(r1, r2)
            boolean r1 = r0.isSuccess
            if (r1 != 0) goto L_0x01b9
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "install gms store failed: "
            r13.append(r14)
            java.lang.String r14 = r0.error
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            return r13
        L_0x01b9:
            java.lang.String r0 = "installing gms Xposed module..."
            updateMessage(r13, r14, r0)
            com.lody.virtual.client.core.VirtualCore r0 = com.lody.virtual.client.core.VirtualCore.get()
            java.lang.String r1 = r10.getAbsolutePath()
            com.lody.virtual.remote.InstallResult r0 = r0.installPackage(r1, r2)
            boolean r1 = r0.isSuccess
            if (r1 != 0) goto L_0x01e2
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "install gms xposed module failed: "
            r13.append(r14)
            java.lang.String r14 = r0.error
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            return r13
        L_0x01e2:
            boolean r0 = r11.exists()
            if (r0 == 0) goto L_0x01f8
            java.lang.String r0 = "installing yalp store..."
            updateMessage(r13, r14, r0)
            com.lody.virtual.client.core.VirtualCore r13 = com.lody.virtual.client.core.VirtualCore.get()
            java.lang.String r14 = r11.getAbsolutePath()
            r13.installPackage(r14, r2)
        L_0x01f8:
            r13 = 0
            java.lang.String r14 = "de.robv.android.xposed.installer"
            java.io.File r13 = com.lody.virtual.p007os.VEnvironment.getDataUserPackageDirectory(r13, r14)
            java.lang.String r14 = "com.thermatk.android.xf.fakegapps"
            java.io.File r14 = com.lody.virtual.p007os.VEnvironment.getPackageResourcePath(r14)
            java.io.File r0 = new java.io.File
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "exposed_conf"
            r1.append(r2)
            java.lang.String r2 = java.io.File.separator
            r1.append(r2)
            java.lang.String r2 = "modules.list"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r13, r1)
            java.io.FileWriter r13 = new java.io.FileWriter     // Catch:{ IOException -> 0x023b, all -> 0x0238 }
            r1 = 1
            r13.<init>(r0, r1)     // Catch:{ IOException -> 0x023b, all -> 0x0238 }
            java.lang.String r14 = r14.getAbsolutePath()     // Catch:{ IOException -> 0x0236 }
            r13.append(r14)     // Catch:{ IOException -> 0x0236 }
            r13.flush()     // Catch:{ IOException -> 0x0236 }
            r13.close()     // Catch:{ IOException -> 0x0246 }
            goto L_0x024a
        L_0x0236:
            r14 = move-exception
            goto L_0x023d
        L_0x0238:
            r14 = move-exception
            r13 = r6
            goto L_0x024c
        L_0x023b:
            r14 = move-exception
            r13 = r6
        L_0x023d:
            r14.printStackTrace()     // Catch:{ all -> 0x024b }
            if (r13 == 0) goto L_0x024a
            r13.close()     // Catch:{ IOException -> 0x0246 }
            goto L_0x024a
        L_0x0246:
            r13 = move-exception
            r13.printStackTrace()
        L_0x024a:
            return r6
        L_0x024b:
            r14 = move-exception
        L_0x024c:
            if (r13 == 0) goto L_0x0256
            r13.close()     // Catch:{ IOException -> 0x0252 }
            goto L_0x0256
        L_0x0252:
            r13 = move-exception
            r13.printStackTrace()
        L_0x0256:
            throw r14
        L_0x0257:
            java.lang.String r13 = "Download gms config failed, please check your network, error: 9"
            return r13
        L_0x025a:
            java.lang.String r13 = "Download gms config failed, please check your network, error: 8"
            return r13
        L_0x025d:
            java.lang.String r13 = "Download gms config failed, please check your network, error: 7"
            return r13
        L_0x0260:
            java.lang.String r13 = "Download gms config failed, please check your network, error: 6"
            return r13
        L_0x0263:
            java.lang.String r13 = "Download gms config failed, please check your network, error: 5"
            return r13
        L_0x0266:
            java.lang.String r13 = "Download gms config failed, please check your network, error: 4"
            return r13
        L_0x0269:
            java.lang.String r13 = "Download gms config failed, please check your network, error: 0"
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.gms.FakeGms.installGmsInternal(android.app.Activity, android.app.ProgressDialog):java.lang.String");
    }

    static /* synthetic */ void lambda$installGmsInternal$96(Activity activity, ProgressDialog progressDialog, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("download gms core...");
        sb.append(i);
        sb.append("%");
        updateMessage(activity, progressDialog, sb.toString());
    }

    static /* synthetic */ void lambda$installGmsInternal$97(Activity activity, ProgressDialog progressDialog, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("download gms service framework proxy..");
        sb.append(i);
        sb.append("%");
        updateMessage(activity, progressDialog, sb.toString());
    }

    static /* synthetic */ void lambda$installGmsInternal$98(Activity activity, ProgressDialog progressDialog, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("download gms store..");
        sb.append(i);
        sb.append("%");
        updateMessage(activity, progressDialog, sb.toString());
    }

    static /* synthetic */ void lambda$installGmsInternal$99(Activity activity, ProgressDialog progressDialog, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("download gms Xposed module..");
        sb.append(i);
        sb.append("%");
        updateMessage(activity, progressDialog, sb.toString());
    }

    static /* synthetic */ void lambda$installGmsInternal$100(Activity activity, ProgressDialog progressDialog, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("download yalp store..");
        sb.append(i);
        sb.append("%");
        updateMessage(activity, progressDialog, sb.toString());
    }

    private static void updateMessage(Activity activity, ProgressDialog progressDialog, String str) {
        if (activity != null && progressDialog != null && !TextUtils.isEmpty(str)) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("update dialog message: ");
            sb.append(str);
            Log.i(str2, sb.toString());
            activity.runOnUiThread(new Runnable(progressDialog, str) {
                private final /* synthetic */ ProgressDialog f$0;
                private final /* synthetic */ String f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    this.f$0.setMessage(this.f$1);
                }
            });
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0071 A[SYNTHETIC, Splitter:B:30:0x0071] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x007d A[SYNTHETIC, Splitter:B:37:0x007d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean downloadFile(java.lang.String r11, java.io.File r12, p013io.virtualapp.gms.FakeGms.DownloadListener r13) {
        /*
            okhttp3.OkHttpClient r0 = new okhttp3.OkHttpClient
            r0.<init>()
            okhttp3.Request$Builder r1 = new okhttp3.Request$Builder
            r1.<init>()
            okhttp3.Request$Builder r11 = r1.url(r11)
            okhttp3.Request r11 = r11.build()
            r1 = 0
            r2 = 0
            okhttp3.Call r11 = r0.newCall(r11)     // Catch:{ IOException -> 0x007a, all -> 0x006e }
            okhttp3.Response r11 = r11.execute()     // Catch:{ IOException -> 0x007a, all -> 0x006e }
            int r0 = r11.code()     // Catch:{ IOException -> 0x007a, all -> 0x006e }
            r3 = 200(0xc8, float:2.8E-43)
            if (r0 == r3) goto L_0x0025
            return r1
        L_0x0025:
            okhttp3.ResponseBody r11 = r11.body()     // Catch:{ IOException -> 0x007a, all -> 0x006e }
            if (r11 != 0) goto L_0x002c
            return r1
        L_0x002c:
            long r3 = r11.contentLength()     // Catch:{ IOException -> 0x007a, all -> 0x006e }
            r5 = 0
            java.io.InputStream r11 = r11.byteStream()     // Catch:{ IOException -> 0x007a, all -> 0x006e }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x007a, all -> 0x006e }
            r0.<init>(r12)     // Catch:{ IOException -> 0x007a, all -> 0x006e }
            r12 = 1024(0x400, float:1.435E-42)
            byte[] r12 = new byte[r12]     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
        L_0x003f:
            int r2 = r11.read(r12)     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
            if (r2 < 0) goto L_0x005c
            r0.write(r12, r1, r2)     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
            long r7 = (long) r2     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
            long r5 = r5 + r7
            double r7 = (double) r5     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
            r9 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r7 = r7 * r9
            double r9 = (double) r3     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
            double r7 = r7 / r9
            r9 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r7 = r7 * r9
            int r2 = (int) r7     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
            if (r13 == 0) goto L_0x003f
            r13.onProgress(r2)     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
            goto L_0x003f
        L_0x005c:
            r0.flush()     // Catch:{ IOException -> 0x006c, all -> 0x0069 }
            r11 = 1
            r0.close()     // Catch:{ IOException -> 0x0064 }
            goto L_0x0068
        L_0x0064:
            r12 = move-exception
            r12.printStackTrace()
        L_0x0068:
            return r11
        L_0x0069:
            r11 = move-exception
            r2 = r0
            goto L_0x006f
        L_0x006c:
            r2 = r0
            goto L_0x007b
        L_0x006e:
            r11 = move-exception
        L_0x006f:
            if (r2 == 0) goto L_0x0079
            r2.close()     // Catch:{ IOException -> 0x0075 }
            goto L_0x0079
        L_0x0075:
            r12 = move-exception
            r12.printStackTrace()
        L_0x0079:
            throw r11
        L_0x007a:
        L_0x007b:
            if (r2 == 0) goto L_0x0085
            r2.close()     // Catch:{ IOException -> 0x0081 }
            goto L_0x0085
        L_0x0081:
            r11 = move-exception
            r11.printStackTrace()
        L_0x0085:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.gms.FakeGms.downloadFile(java.lang.String, java.io.File, io.virtualapp.gms.FakeGms$DownloadListener):boolean");
    }
}
