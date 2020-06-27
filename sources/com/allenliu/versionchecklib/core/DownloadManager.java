package com.allenliu.versionchecklib.core;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.p001v4.app.NotificationCompat.Builder;
import com.allenliu.versionchecklib.C0494R;
import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.core.http.FileCallBack;
import com.allenliu.versionchecklib.utils.ALog;
import com.allenliu.versionchecklib.utils.AppUtils;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import java.io.File;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManager {
    /* access modifiers changed from: private */
    public static int lastProgress;

    public static void downloadAPK(Context context, String str, VersionParams versionParams, DownloadListener downloadListener) {
        final NotificationManager notificationManager;
        final Builder builder;
        lastProgress = 0;
        if (str != null && !str.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(versionParams.getDownloadAPKPath());
            sb.append(context.getString(C0494R.string.versionchecklib_download_apkname, new Object[]{context.getPackageName()}));
            String sb2 = sb.toString();
            if (versionParams.isSilentDownload()) {
                if (versionParams.isForceRedownload()) {
                    silentDownloadAPK(context, str, versionParams, downloadListener);
                } else if (checkAPKIsExists(context, sb2)) {
                    if (downloadListener != null) {
                        downloadListener.onCheckerDownloadSuccess(new File(sb2));
                    }
                } else {
                    silentDownloadAPK(context, str, versionParams, downloadListener);
                }
            } else if (versionParams.isForceRedownload() || !checkAPKIsExists(context, sb2)) {
                if (downloadListener != null) {
                    downloadListener.onCheckerStartDownload();
                }
                if (versionParams.isShowNotification()) {
                    NotificationManager notificationManager2 = (NotificationManager) context.getSystemService(ServiceManagerNative.NOTIFICATION);
                    Builder builder2 = new Builder(context);
                    builder2.setAutoCancel(true);
                    builder2.setSmallIcon(C0494R.mipmap.ic_launcher);
                    builder2.setContentTitle(context.getString(C0494R.string.app_name));
                    builder2.setTicker(context.getString(C0494R.string.versionchecklib_downloading));
                    builder2.setContentText(String.format(context.getString(C0494R.string.versionchecklib_download_progress), new Object[]{Integer.valueOf(0)}));
                    Notification build = builder2.build();
                    build.vibrate = new long[]{500, 500};
                    build.defaults = 3;
                    notificationManager2.notify(0, build);
                    notificationManager = notificationManager2;
                    builder = builder2;
                } else {
                    builder = null;
                    notificationManager = null;
                }
                Call newCall = AllenHttp.getHttpClient().newCall(new Request.Builder().url(str).build());
                String downloadAPKPath = versionParams.getDownloadAPKPath();
                String string = context.getString(C0494R.string.versionchecklib_download_apkname, new Object[]{context.getPackageName()});
                final DownloadListener downloadListener2 = downloadListener;
                final VersionParams versionParams2 = versionParams;
                final Context context2 = context;
                final String str2 = str;
                C05021 r0 = new FileCallBack(downloadAPKPath, string) {
                    public void onSuccess(File file, Call call, Response response) {
                        Uri uri;
                        downloadListener2.onCheckerDownloadSuccess(file);
                        if (versionParams2.isShowNotification()) {
                            Intent intent = new Intent("android.intent.action.VIEW");
                            if (VERSION.SDK_INT >= 24) {
                                Context context = context2;
                                StringBuilder sb = new StringBuilder();
                                sb.append(context2.getPackageName());
                                sb.append(".versionProvider");
                                uri = VersionFileProvider.getUriForFile(context, sb.toString(), file);
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(context2.getPackageName());
                                sb2.append("");
                                ALog.m10e(sb2.toString());
                                intent.addFlags(1);
                            } else {
                                uri = Uri.fromFile(file);
                            }
                            ALog.m10e("APK download Success");
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                            builder.setContentIntent(PendingIntent.getActivity(context2, 0, intent, 0));
                            builder.setContentText(context2.getString(C0494R.string.versionchecklib_download_finish));
                            builder.setProgress(100, 100, false);
                            notificationManager.cancelAll();
                            notificationManager.notify(0, builder.build());
                        }
                        AppUtils.installApk(context2, file);
                        if (context2 instanceof Activity) {
                            ((Activity) context2).finish();
                        }
                    }

                    public void onDownloading(int i) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("downloadProgress:");
                        sb.append(i);
                        sb.append("");
                        ALog.m10e(sb.toString());
                        downloadListener2.onCheckerDownloading(i);
                        if (i - DownloadManager.lastProgress >= 5) {
                            DownloadManager.lastProgress = i;
                            if (versionParams2.isShowNotification()) {
                                builder.setContentIntent(null);
                                builder.setContentText(String.format(context2.getString(C0494R.string.versionchecklib_download_progress), new Object[]{Integer.valueOf(DownloadManager.lastProgress)}));
                                builder.setProgress(100, DownloadManager.lastProgress, false);
                                notificationManager.notify(0, builder.build());
                            }
                        }
                    }

                    public void onDownloadFailed() {
                        if (versionParams2.isShowNotification()) {
                            Intent intent = new Intent(context2, versionParams2.getCustomDownloadActivityClass());
                            intent.putExtra("isRetry", true);
                            intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams2);
                            intent.putExtra("downloadUrl", str2);
                            builder.setContentIntent(PendingIntent.getActivity(context2, 0, intent, 134217728));
                            builder.setContentText(context2.getString(C0494R.string.versionchecklib_download_fail));
                            builder.setProgress(100, 0, false);
                            notificationManager.notify(0, builder.build());
                        }
                        ALog.m10e("file download failed");
                        downloadListener2.onCheckerDownloadFail();
                    }
                };
                newCall.enqueue(r0);
            } else {
                if (downloadListener != null) {
                    downloadListener.onCheckerDownloadSuccess(new File(sb2));
                }
                AppUtils.installApk(context, new File(sb2));
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        }
    }

    private static void silentDownloadAPK(Context context, String str, VersionParams versionParams, final DownloadListener downloadListener) {
        Request build = new Request.Builder().url(str).build();
        if (downloadListener != null) {
            downloadListener.onCheckerStartDownload();
        }
        AllenHttp.getHttpClient().newCall(build).enqueue(new FileCallBack(versionParams.getDownloadAPKPath(), context.getString(C0494R.string.versionchecklib_download_apkname, new Object[]{context.getPackageName()})) {
            public void onSuccess(File file, Call call, Response response) {
                downloadListener.onCheckerDownloadSuccess(file);
            }

            public void onDownloading(int i) {
                StringBuilder sb = new StringBuilder();
                sb.append("silent downloadProgress:");
                sb.append(i);
                sb.append("");
                ALog.m10e(sb.toString());
                if (i - DownloadManager.lastProgress >= 5) {
                    DownloadManager.lastProgress = i;
                }
                downloadListener.onCheckerDownloading(i);
            }

            public void onDownloadFailed() {
                ALog.m10e("file silent download failed");
                downloadListener.onCheckerDownloadFail();
            }
        });
    }

    public static boolean checkAPKIsExists(Context context, String str) {
        if (!new File(str).exists()) {
            return false;
        }
        try {
            PackageInfo packageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(str, 1);
            StringBuilder sb = new StringBuilder();
            sb.append("本地安装包版本号：");
            sb.append(packageArchiveInfo.versionCode);
            sb.append("\n 当前app版本号：");
            sb.append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            ALog.m10e(sb.toString());
            if (packageArchiveInfo == null || !context.getPackageName().equalsIgnoreCase(packageArchiveInfo.packageName) || context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode == packageArchiveInfo.versionCode) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }
}
