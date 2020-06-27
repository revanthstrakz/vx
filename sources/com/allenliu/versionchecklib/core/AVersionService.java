package com.allenliu.versionchecklib.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import com.allenliu.versionchecklib.C0494R;
import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.utils.ALog;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AVersionService extends Service implements DownloadListener {
    public static final String PERMISSION_ACTION = "com.allenliu.versionchecklib.filepermisssion.action";
    public static final String VERSION_PARAMS_EXTRA_KEY = "VERSION_PARAMS_EXTRA_KEY";
    public static final String VERSION_PARAMS_KEY = "VERSION_PARAMS_KEY";
    String downloadUrl;
    Bundle paramBundle;
    Callback stringCallback = new Callback() {
        public void onFailure(Call call, IOException iOException) {
            AVersionService.this.pauseRequest();
        }

        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                final String string = response.body().string();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        AVersionService.this.onResponses(AVersionService.this, string);
                    }
                });
                return;
            }
            AVersionService.this.pauseRequest();
        }
    };
    String title;
    String updateMsg;
    protected VersionParams versionParams;

    public class VersionBroadCastReceiver extends BroadcastReceiver {
        public VersionBroadCastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AVersionService.PERMISSION_ACTION)) {
                if (intent.getBooleanExtra("result", false)) {
                    AVersionService.this.silentDownload();
                }
                AVersionService.this.unregisterReceiver(this);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCheckerDownloading(int i) {
    }

    public void onCheckerStartDownload() {
    }

    public abstract void onResponses(AVersionService aVersionService, String str);

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            try {
                this.versionParams = (VersionParams) intent.getParcelableExtra(VERSION_PARAMS_KEY);
                verfiyAndDeleteAPK();
                if (this.versionParams.isOnlyDownload()) {
                    showVersionDialog(this.versionParams.getDownloadUrl(), this.versionParams.getTitle(), this.versionParams.getUpdateMsg(), this.versionParams.getParamBundle());
                } else {
                    requestVersionUrlSync();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, i, i2);
    }

    private void verfiyAndDeleteAPK() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(this.versionParams.getDownloadAPKPath());
            sb.append(getApplicationContext().getString(C0494R.string.versionchecklib_download_apkname, new Object[]{getApplicationContext().getPackageName()}));
            String sb2 = sb.toString();
            if (!DownloadManager.checkAPKIsExists(getApplicationContext(), sb2)) {
                ALog.m10e("删除本地apk");
                new File(sb2).delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void requestVersionUrlSync() {
        requestVersionUrl();
    }

    /* access modifiers changed from: private */
    public void pauseRequest() {
        long pauseRequestTime = this.versionParams.getPauseRequestTime();
        if (pauseRequestTime > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("请求版本接口失败，下次请求将在");
            sb.append(pauseRequestTime);
            sb.append("ms后开始");
            ALog.m10e(sb.toString());
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    AVersionService.this.requestVersionUrlSync();
                }
            }, pauseRequestTime);
        }
    }

    private void requestVersionUrl() {
        Request request;
        OkHttpClient httpClient = AllenHttp.getHttpClient();
        switch (this.versionParams.getRequestMethod()) {
            case GET:
                request = AllenHttp.get(this.versionParams).build();
                break;
            case POST:
                request = AllenHttp.post(this.versionParams).build();
                break;
            case POSTJSON:
                request = AllenHttp.postJson(this.versionParams).build();
                break;
            default:
                request = null;
                break;
        }
        httpClient.newCall(request).enqueue(this.stringCallback);
    }

    public void showVersionDialog(String str, String str2, String str3) {
        showVersionDialog(str, str2, str3, null);
    }

    public void showVersionDialog(String str, String str2, String str3, Bundle bundle) {
        this.downloadUrl = str;
        this.title = str2;
        this.updateMsg = str3;
        this.paramBundle = bundle;
        if (this.versionParams.isSilentDownload()) {
            registerReceiver(new VersionBroadCastReceiver(), new IntentFilter(PERMISSION_ACTION));
            Intent intent = new Intent(this, PermissionDialogActivity.class);
            intent.addFlags(268435456);
            startActivity(intent);
            return;
        }
        goToVersionDialog();
    }

    /* access modifiers changed from: private */
    public void silentDownload() {
        DownloadManager.downloadAPK(getApplicationContext(), this.downloadUrl, this.versionParams, this);
    }

    public void onCheckerDownloadSuccess(File file) {
        goToVersionDialog();
    }

    public void onCheckerDownloadFail() {
        stopSelf();
    }

    private void goToVersionDialog() {
        Intent intent = new Intent(getApplicationContext(), this.versionParams.getCustomDownloadActivityClass());
        if (this.updateMsg != null) {
            intent.putExtra("text", this.updateMsg);
        }
        if (this.downloadUrl != null) {
            intent.putExtra("downloadUrl", this.downloadUrl);
        }
        if (this.title != null) {
            intent.putExtra(BaseLauncherColumns.TITLE, this.title);
        }
        if (this.paramBundle != null) {
            this.versionParams.setParamBundle(this.paramBundle);
        }
        intent.putExtra(VERSION_PARAMS_KEY, this.versionParams);
        intent.addFlags(268435456);
        startActivity(intent);
        stopSelf();
    }

    public void setVersionParams(VersionParams versionParams2) {
        this.versionParams = versionParams2;
    }
}
