package com.allenliu.versionchecklib.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.p001v4.app.ActivityCompat;
import android.support.p001v4.content.ContextCompat;
import android.support.p004v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.allenliu.versionchecklib.C0494R;
import com.allenliu.versionchecklib.callback.APKDownloadListener;
import com.allenliu.versionchecklib.callback.CommitClickListener;
import com.allenliu.versionchecklib.callback.DialogDismissListener;
import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.utils.ALog;
import com.allenliu.versionchecklib.utils.AppUtils;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import java.io.File;

public class VersionDialogActivity extends Activity implements DownloadListener, OnDismissListener {
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 291;
    public static VersionDialogActivity instance;
    private APKDownloadListener apkDownloadListener;
    private DialogDismissListener cancelListener;
    /* access modifiers changed from: private */
    public CommitClickListener commitListener;
    private String downloadUrl;
    protected Dialog failDialog;
    boolean isDestroy = false;
    protected Dialog loadingDialog;
    private View loadingView;
    private String title;
    private String updateMsg;
    protected Dialog versionDialog;
    private VersionParams versionParams;

    public static void setTransparent(Activity activity) {
        if (VERSION.SDK_INT >= 19) {
            transparentStatusBar(activity);
            setRootView(activity);
        }
    }

    @TargetApi(19)
    private static void transparentStatusBar(Activity activity) {
        if (VERSION.SDK_INT >= 21) {
            activity.getWindow().addFlags(Integer.MIN_VALUE);
            activity.getWindow().clearFlags(67108864);
            activity.getWindow().addFlags(134217728);
            activity.getWindow().setStatusBarColor(0);
            return;
        }
        activity.getWindow().addFlags(67108864);
    }

    private static void setRootView(Activity activity) {
        ViewGroup viewGroup = (ViewGroup) activity.findViewById(16908290);
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                childAt.setFitsSystemWindows(true);
                ((ViewGroup) childAt).setClipToPadding(true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        instance = this;
        setTransparent(this);
        boolean booleanExtra = getIntent().getBooleanExtra("isRetry", false);
        StringBuilder sb = new StringBuilder();
        sb.append(booleanExtra);
        sb.append("");
        Log.e("isRetry", sb.toString());
        if (booleanExtra) {
            retryDownload(getIntent());
        } else {
            initialize();
        }
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public VersionParams getVersionParams() {
        return this.versionParams;
    }

    public String getVersionTitle() {
        return this.title;
    }

    public String getVersionUpdateMsg() {
        return this.updateMsg;
    }

    public Bundle getVersionParamBundle() {
        return this.versionParams.getParamBundle();
    }

    private void initialize() {
        this.title = getIntent().getStringExtra(BaseLauncherColumns.TITLE);
        this.updateMsg = getIntent().getStringExtra("text");
        this.versionParams = (VersionParams) getIntent().getParcelableExtra(AVersionService.VERSION_PARAMS_KEY);
        this.downloadUrl = getIntent().getStringExtra("downloadUrl");
        if (this.title != null && this.updateMsg != null && this.downloadUrl != null && this.versionParams != null) {
            showVersionDialog();
        }
    }

    /* access modifiers changed from: protected */
    public void showVersionDialog() {
        if (!this.isDestroy) {
            this.versionDialog = new Builder(this).setTitle((CharSequence) this.title).setMessage((CharSequence) this.updateMsg).setPositiveButton((CharSequence) getString(C0494R.string.versionchecklib_confirm), (OnClickListener) new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (VersionDialogActivity.this.commitListener != null) {
                        VersionDialogActivity.this.commitListener.onCommitClick();
                    }
                    VersionDialogActivity.this.dealAPK();
                }
            }).setNegativeButton((CharSequence) getString(C0494R.string.versionchecklib_cancel), (OnClickListener) new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    VersionDialogActivity.this.finish();
                }
            }).create();
            this.versionDialog.setOnDismissListener(this);
            this.versionDialog.setCanceledOnTouchOutside(false);
            this.versionDialog.setCancelable(false);
            this.versionDialog.show();
        }
    }

    public void showLoadingDialog(int i) {
        ALog.m10e("show default downloading dialog");
        if (!this.isDestroy) {
            if (this.loadingDialog == null) {
                this.loadingView = LayoutInflater.from(this).inflate(C0494R.layout.downloading_layout, null);
                this.loadingDialog = new Builder(this).setTitle((CharSequence) "").setView(this.loadingView).create();
                this.loadingDialog.setCancelable(true);
                this.loadingDialog.setCanceledOnTouchOutside(false);
                this.loadingDialog.setOnCancelListener(new OnCancelListener() {
                    public void onCancel(DialogInterface dialogInterface) {
                        AllenHttp.getHttpClient().dispatcher().cancelAll();
                    }
                });
            }
            ProgressBar progressBar = (ProgressBar) this.loadingView.findViewById(C0494R.C0497id.f43pb);
            ((TextView) this.loadingView.findViewById(C0494R.C0497id.tv_progress)).setText(String.format(getString(C0494R.string.versionchecklib_progress), new Object[]{Integer.valueOf(i)}));
            progressBar.setProgress(i);
            this.loadingDialog.show();
        }
    }

    public void showFailDialog() {
        if (!this.isDestroy) {
            if (this.failDialog == null) {
                this.failDialog = new Builder(this).setMessage((CharSequence) getString(C0494R.string.versionchecklib_download_fail_retry)).setPositiveButton((CharSequence) getString(C0494R.string.versionchecklib_confirm), (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (VersionDialogActivity.this.commitListener != null) {
                            VersionDialogActivity.this.commitListener.onCommitClick();
                        }
                        VersionDialogActivity.this.dealAPK();
                    }
                }).setNegativeButton((CharSequence) getString(C0494R.string.versionchecklib_cancel), (OnClickListener) null).create();
                this.failDialog.setOnDismissListener(this);
                this.failDialog.setCanceledOnTouchOutside(false);
                this.failDialog.setCancelable(false);
            }
            this.failDialog.show();
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("isRetry", false);
        StringBuilder sb = new StringBuilder();
        sb.append(booleanExtra);
        sb.append("");
        Log.e("isRetry", sb.toString());
        if (booleanExtra) {
            retryDownload(intent);
        }
    }

    private void retryDownload(Intent intent) {
        dismissAllDialog();
        this.versionParams = (VersionParams) intent.getParcelableExtra(AVersionService.VERSION_PARAMS_KEY);
        this.downloadUrl = intent.getStringExtra("downloadUrl");
        requestPermissionAndDownloadFile();
    }

    public void setApkDownloadListener(APKDownloadListener aPKDownloadListener) {
        this.apkDownloadListener = aPKDownloadListener;
    }

    public void setCommitClickListener(CommitClickListener commitClickListener) {
        this.commitListener = commitClickListener;
    }

    public void setDialogDimissListener(DialogDismissListener dialogDismissListener) {
        this.cancelListener = dialogDismissListener;
    }

    public void dealAPK() {
        if (this.versionParams.isSilentDownload()) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.versionParams.getDownloadAPKPath());
            sb.append(getString(C0494R.string.versionchecklib_download_apkname, new Object[]{getPackageName()}));
            AppUtils.installApk(this, new File(sb.toString()));
            finish();
            return;
        }
        if (this.versionParams.isShowDownloadingDialog()) {
            showLoadingDialog(0);
        }
        requestPermissionAndDownloadFile();
    }

    /* access modifiers changed from: protected */
    public void downloadFile() {
        if (this.versionParams.isShowDownloadingDialog()) {
            showLoadingDialog(0);
        }
        DownloadManager.downloadAPK(this, this.downloadUrl, this.versionParams, this);
    }

    /* access modifiers changed from: protected */
    public void requestPermissionAndDownloadFile() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            downloadFile();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 291) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                Toast.makeText(this, getString(C0494R.string.versionchecklib_write_permission_deny), 1).show();
                finish();
            } else {
                downloadFile();
            }
        }
    }

    public void onCheckerDownloading(int i) {
        if (this.versionParams.isShowDownloadingDialog()) {
            showLoadingDialog(i);
        } else {
            if (this.loadingDialog != null) {
                this.loadingDialog.dismiss();
            }
            finish();
        }
        if (this.apkDownloadListener != null) {
            this.apkDownloadListener.onDownloading(i);
        }
    }

    public void onCheckerDownloadSuccess(File file) {
        if (this.apkDownloadListener != null) {
            this.apkDownloadListener.onDownloadSuccess(file);
        }
        dismissAllDialog();
    }

    public void onCheckerDownloadFail() {
        if (this.apkDownloadListener != null) {
            this.apkDownloadListener.onDownloadFail();
        }
        dismissAllDialog();
        showFailDialog();
    }

    public void onCheckerStartDownload() {
        if (!this.versionParams.isShowDownloadingDialog()) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.isDestroy = true;
        instance = null;
        super.onDestroy();
    }

    private void dismissAllDialog() {
        if (!this.isDestroy) {
            ALog.m10e("dismiss all dialog");
            if (this.loadingDialog != null && this.loadingDialog.isShowing()) {
                this.loadingDialog.dismiss();
            }
            if (this.versionDialog != null && this.versionDialog.isShowing()) {
                this.versionDialog.dismiss();
            }
            if (this.failDialog != null && this.failDialog.isShowing()) {
                this.failDialog.dismiss();
            }
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        if (this.versionParams.isSilentDownload() || ((!this.versionParams.isSilentDownload() && this.loadingDialog == null && this.versionParams.isShowDownloadingDialog()) || (!this.versionParams.isSilentDownload() && this.loadingDialog != null && !this.loadingDialog.isShowing() && this.versionParams.isShowDownloadingDialog()))) {
            if (this.cancelListener != null) {
                this.cancelListener.dialogDismiss(dialogInterface);
            }
            finish();
        }
    }
}
