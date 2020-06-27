package p013io.virtualapp.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.p004v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.lody.virtual.client.core.VirtualCore;
import java.util.concurrent.Executors;
import p013io.virtualapp.home.LoadingActivity;
import p013io.virtualapp.utils.DialogUtil;

/* renamed from: io.virtualapp.settings.OnlinePlugin */
public class OnlinePlugin {
    public static final String FILE_MANAGE_PACKAGE = "com.amaze.filemanager";
    public static final String FILE_MANAGE_URL = "http://vaexposed.weishu.me/amaze.json";
    public static final String PERMISSION_MANAGE_PACKAGE = "eu.faircode.xlua";
    public static final String PERMISSION_MANAGE_URL = "http://vaexposed.weishu.me/xlua.json";
    private static final String TAG = "OnlinePlugin";

    public static void openOrDownload(Activity activity, String str, String str2, String str3) {
        if (activity != null && str != null) {
            if (VirtualCore.get().isAppInstalled(str)) {
                LoadingActivity.launch(activity, str, 0);
            } else {
                DialogUtil.showDialog(new Builder(activity, 2131951907).setTitle(17039380).setMessage((CharSequence) str3).setPositiveButton(17039370, (OnClickListener) new OnClickListener(activity, str2, str) {
                    private final /* synthetic */ Activity f$0;
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        OnlinePlugin.lambda$openOrDownload$7(this.f$0, this.f$1, this.f$2, dialogInterface, i);
                    }
                }).setNegativeButton(17039360, (OnClickListener) null).create());
            }
        }
    }

    static /* synthetic */ void lambda$openOrDownload$7(Activity activity, String str, String str2, DialogInterface dialogInterface, int i) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Executors.newSingleThreadExecutor().submit(new Runnable(activity, progressDialog, str, str2) {
            private final /* synthetic */ Activity f$0;
            private final /* synthetic */ ProgressDialog f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                OnlinePlugin.lambda$null$6(this.f$0, this.f$1, this.f$2, this.f$3);
            }
        });
    }

    static /* synthetic */ void lambda$null$6(Activity activity, ProgressDialog progressDialog, String str, String str2) {
        String downloadAndInstall = downloadAndInstall(activity, progressDialog, str, str2);
        try {
            progressDialog.dismiss();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        if (downloadAndInstall == null) {
            activity.runOnUiThread(new Runnable(activity, str2) {
                private final /* synthetic */ Activity f$0;
                private final /* synthetic */ String f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    LoadingActivity.launch(this.f$0, this.f$1, 0);
                }
            });
        } else {
            activity.runOnUiThread(new Runnable(activity, downloadAndInstall) {
                private final /* synthetic */ Activity f$0;
                private final /* synthetic */ String f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    Toast.makeText(this.f$0, this.f$1, 0).show();
                }
            });
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x014f A[SYNTHETIC, Splitter:B:43:0x014f] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x015b A[SYNTHETIC, Splitter:B:49:0x015b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String downloadAndInstall(android.app.Activity r6, android.app.ProgressDialog r7, java.lang.String r8, java.lang.String r9) {
        /*
            okhttp3.OkHttpClient$Builder r0 = new okhttp3.OkHttpClient$Builder
            r0.<init>()
            java.util.concurrent.TimeUnit r1 = java.util.concurrent.TimeUnit.SECONDS
            r2 = 30
            okhttp3.OkHttpClient$Builder r0 = r0.connectTimeout(r2, r1)
            java.util.concurrent.TimeUnit r1 = java.util.concurrent.TimeUnit.SECONDS
            okhttp3.OkHttpClient$Builder r0 = r0.readTimeout(r2, r1)
            java.util.concurrent.TimeUnit r1 = java.util.concurrent.TimeUnit.SECONDS
            okhttp3.OkHttpClient$Builder r0 = r0.writeTimeout(r2, r1)
            okhttp3.OkHttpClient r0 = r0.build()
            okhttp3.Request$Builder r1 = new okhttp3.Request$Builder
            r1.<init>()
            okhttp3.Request$Builder r8 = r1.url(r8)
            okhttp3.Request r8 = r8.build()
            java.lang.String r1 = "Prepare download..."
            updateMessage(r6, r7, r1)
            okhttp3.Call r8 = r0.newCall(r8)     // Catch:{ IOException -> 0x0178 }
            okhttp3.Response r8 = r8.execute()     // Catch:{ IOException -> 0x0178 }
            boolean r0 = r8.isSuccessful()
            if (r0 != 0) goto L_0x0040
            java.lang.String r6 = "Download failed, please check your network, error: 1"
            return r6
        L_0x0040:
            java.lang.String r0 = "OnlinePlugin"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "response success: "
            r1.append(r2)
            int r2 = r8.code()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Log.i(r0, r1)
            r0 = 200(0xc8, float:2.8E-43)
            int r1 = r8.code()
            if (r0 == r1) goto L_0x0065
            java.lang.String r6 = "Download failed, please check your network, error: 2"
            return r6
        L_0x0065:
            java.lang.String r0 = "Parsing config..."
            updateMessage(r6, r7, r0)
            okhttp3.ResponseBody r8 = r8.body()
            if (r8 != 0) goto L_0x0073
            java.lang.String r6 = "Download failed, please check your network, error: 3"
            return r6
        L_0x0073:
            java.lang.String r8 = r8.string()     // Catch:{ IOException -> 0x0175 }
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0172 }
            r0.<init>(r8)     // Catch:{ JSONException -> 0x0172 }
            java.lang.String r8 = "url"
            java.lang.String r8 = r0.getString(r8)     // Catch:{ JSONException -> 0x016f }
            java.lang.String r1 = "xposed"
            r2 = 0
            boolean r0 = r0.optBoolean(r1, r2)     // Catch:{ JSONException -> 0x016f }
            java.io.File r1 = new java.io.File
            java.io.File r3 = r6.getCacheDir()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r9)
            java.lang.String r5 = ".apk"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r1.<init>(r3, r4)
            io.virtualapp.settings.-$$Lambda$OnlinePlugin$D-Mh_LeTy79r55KHr5PLQfnwJbw r3 = new io.virtualapp.settings.-$$Lambda$OnlinePlugin$D-Mh_LeTy79r55KHr5PLQfnwJbw
            r3.<init>(r6, r7, r9)
            p013io.virtualapp.gms.FakeGms.downloadFile(r8, r1, r3)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r3 = "installing "
            r8.append(r3)
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            updateMessage(r6, r7, r8)
            com.lody.virtual.client.core.VirtualCore r8 = com.lody.virtual.client.core.VirtualCore.get()
            java.lang.String r1 = r1.getAbsolutePath()
            r3 = 4
            com.lody.virtual.remote.InstallResult r8 = r8.installPackage(r1, r3)
            boolean r1 = r8.isSuccess
            if (r1 != 0) goto L_0x00ec
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "install "
            r6.append(r7)
            r6.append(r9)
            java.lang.String r7 = " failed: "
            r6.append(r7)
            java.lang.String r7 = r8.error
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            return r6
        L_0x00ec:
            r8 = 0
            if (r0 == 0) goto L_0x0164
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "enable "
            r0.append(r1)
            r0.append(r9)
            java.lang.String r1 = " in Xposed Installer"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            updateMessage(r6, r7, r0)
            java.lang.String r0 = "de.robv.android.xposed.installer"
            java.io.File r0 = com.lody.virtual.p007os.VEnvironment.getDataUserPackageDirectory(r2, r0)
            java.io.File r9 = com.lody.virtual.p007os.VEnvironment.getPackageResourcePath(r9)
            java.io.File r1 = new java.io.File
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "exposed_conf"
            r2.append(r3)
            java.lang.String r3 = java.io.File.separator
            r2.append(r3)
            java.lang.String r3 = "modules.list"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r0, r2)
            java.io.FileWriter r0 = new java.io.FileWriter     // Catch:{ IOException -> 0x0148, all -> 0x0145 }
            r2 = 1
            r0.<init>(r1, r2)     // Catch:{ IOException -> 0x0148, all -> 0x0145 }
            java.lang.String r9 = r9.getAbsolutePath()     // Catch:{ IOException -> 0x0143 }
            r0.append(r9)     // Catch:{ IOException -> 0x0143 }
            r0.flush()     // Catch:{ IOException -> 0x0143 }
            r0.close()     // Catch:{ IOException -> 0x0153 }
            goto L_0x0164
        L_0x0143:
            r9 = move-exception
            goto L_0x014a
        L_0x0145:
            r6 = move-exception
            r0 = r8
            goto L_0x0159
        L_0x0148:
            r9 = move-exception
            r0 = r8
        L_0x014a:
            r9.printStackTrace()     // Catch:{ all -> 0x0158 }
            if (r0 == 0) goto L_0x0164
            r0.close()     // Catch:{ IOException -> 0x0153 }
            goto L_0x0164
        L_0x0153:
            r9 = move-exception
            r9.printStackTrace()
            goto L_0x0164
        L_0x0158:
            r6 = move-exception
        L_0x0159:
            if (r0 == 0) goto L_0x0163
            r0.close()     // Catch:{ IOException -> 0x015f }
            goto L_0x0163
        L_0x015f:
            r7 = move-exception
            r7.printStackTrace()
        L_0x0163:
            throw r6
        L_0x0164:
            java.lang.String r9 = " install success!!"
            updateMessage(r6, r7, r9)
            r6 = 300(0x12c, double:1.48E-321)
            android.os.SystemClock.sleep(r6)
            return r8
        L_0x016f:
            java.lang.String r6 = "Download failed, please check your network, error: 6"
            return r6
        L_0x0172:
            java.lang.String r6 = "Download failed, please check your network, error: 5"
            return r6
        L_0x0175:
            java.lang.String r6 = "Download failed, please check your network, error: 4"
            return r6
        L_0x0178:
            java.lang.String r6 = "Download failed, please check your network, error: 0"
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.settings.OnlinePlugin.downloadAndInstall(android.app.Activity, android.app.ProgressDialog, java.lang.String, java.lang.String):java.lang.String");
    }

    static /* synthetic */ void lambda$downloadAndInstall$8(Activity activity, ProgressDialog progressDialog, String str, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("download ");
        sb.append(str);
        sb.append("...");
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
}
