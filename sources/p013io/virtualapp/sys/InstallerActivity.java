package p013io.virtualapp.sys;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.p004v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.EncodeUtils;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import io.va.exposed.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import p013io.virtualapp.VCommends;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.home.LoadingActivity;
import p013io.virtualapp.home.models.AppData;
import p013io.virtualapp.home.models.AppInfoLite;
import p013io.virtualapp.sys.Installd.UpdateListener;

/* renamed from: io.virtualapp.sys.InstallerActivity */
public class InstallerActivity extends AppCompatActivity {
    /* access modifiers changed from: private */
    public int mInstallCount = 0;
    /* access modifiers changed from: private */
    public Button mLeft;
    /* access modifiers changed from: private */
    public ProgressBar mProgressBar;
    /* access modifiers changed from: private */
    public TextView mProgressText;
    /* access modifiers changed from: private */
    public Button mRight;
    private TextView mTips;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_install);
        this.mTips = (TextView) findViewById(R.id.installer_text);
        this.mLeft = (Button) findViewById(R.id.installer_left_button);
        this.mRight = (Button) findViewById(R.id.installer_right_button);
        this.mProgressBar = (ProgressBar) findViewById(R.id.installer_loading);
        this.mProgressText = (TextView) findViewById(R.id.installer_progress_text);
        handleIntent(getIntent());
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void onBackPressed() {
        int i = this.mInstallCount;
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            finish();
            return;
        }
        ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra(VCommends.EXTRA_APP_INFO_LIST);
        if (parcelableArrayListExtra == null) {
            handleSystemIntent(intent);
        } else {
            handleSelfIntent(parcelableArrayListExtra);
        }
    }

    private void handleSelfIntent(ArrayList<AppInfoLite> arrayList) {
        if (arrayList != null) {
            int size = arrayList.size();
            this.mInstallCount = size;
            if (!dealUpdate(arrayList)) {
                boolean z = false;
                for (int i = 0; i < size; i++) {
                    AppInfoLite appInfoLite = (AppInfoLite) arrayList.get(i);
                    if (new File(appInfoLite.path).length() > 25165824) {
                        z = true;
                    }
                    addApp(appInfoLite);
                }
                if (z) {
                    Toast.makeText(this, R.string.large_app_install_tips, 0).show();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void addApp(final AppInfoLite appInfoLite) {
        Installd.addApp(appInfoLite, new UpdateListener() {
            public void update(AppData appData) {
                InstallerActivity.this.runOnUiThread(new Runnable(appData, appInfoLite) {
                    private final /* synthetic */ AppData f$1;
                    private final /* synthetic */ AppInfoLite f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        C12801.lambda$update$112(C12801.this, this.f$1, this.f$2);
                    }
                });
            }

            public static /* synthetic */ void lambda$update$112(C12801 r5, AppData appData, AppInfoLite appInfoLite) {
                if (appData.isInstalling()) {
                    InstallerActivity.this.mProgressText.setVisibility(0);
                    InstallerActivity.this.mProgressBar.setVisibility(0);
                    InstallerActivity.this.mProgressText.setText(InstallerActivity.this.getResources().getString(R.string.add_app_installing_tips, new Object[]{appData.getName()}));
                } else if (appData.isLoading()) {
                    InstallerActivity.this.mProgressText.setVisibility(0);
                    InstallerActivity.this.mProgressBar.setVisibility(0);
                    InstallerActivity.this.mProgressText.setText(InstallerActivity.this.getResources().getString(R.string.add_app_loading_tips, new Object[]{appData.getName()}));
                } else {
                    InstallerActivity.this.mInstallCount = InstallerActivity.this.mInstallCount - 1;
                    if (InstallerActivity.this.mInstallCount <= 0) {
                        InstallerActivity.this.mInstallCount = 0;
                        InstallerActivity.this.mProgressText.setText(InstallerActivity.this.getResources().getString(R.string.add_app_loading_complete, new Object[]{appData.getName()}));
                        InstallerActivity.this.mProgressText.postDelayed(new Runnable(appInfoLite) {
                            private final /* synthetic */ AppInfoLite f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                C12801.lambda$null$111(C12801.this, this.f$1);
                            }
                        }, 500);
                    }
                }
            }

            public static /* synthetic */ void lambda$null$111(C12801 r3, AppInfoLite appInfoLite) {
                InstallerActivity.this.mProgressBar.setVisibility(8);
                InstallerActivity.this.mLeft.setVisibility(0);
                InstallerActivity.this.mLeft.setText(R.string.install_complete);
                InstallerActivity.this.mLeft.setOnClickListener(new OnClickListener() {
                    public final void onClick(View view) {
                        InstallerActivity.this.finish();
                    }
                });
                InstallerActivity.this.mRight.setVisibility(0);
                InstallerActivity.this.mRight.setText(R.string.install_complete_and_open);
                InstallerActivity.this.mRight.setOnClickListener(new OnClickListener(appInfoLite) {
                    private final /* synthetic */ AppInfoLite f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        C12801.lambda$null$110(C12801.this, this.f$1, view);
                    }
                });
            }

            public static /* synthetic */ void lambda$null$110(C12801 r1, AppInfoLite appInfoLite, View view) {
                LoadingActivity.launch(InstallerActivity.this.getApplicationContext(), appInfoLite.packageName, 0);
                InstallerActivity.this.finish();
            }

            public void fail(String str) {
                if (str == null) {
                    str = "Unknown";
                }
                InstallerActivity.this.mProgressText.setText(InstallerActivity.this.getResources().getString(R.string.install_fail, new Object[]{str}));
                InstallerActivity.this.mProgressText.postDelayed(new Runnable() {
                    public final void run() {
                        C12801.lambda$fail$114(C12801.this);
                    }
                }, 500);
            }

            public static /* synthetic */ void lambda$fail$114(C12801 r2) {
                InstallerActivity.this.mProgressBar.setVisibility(8);
                InstallerActivity.this.mRight.setVisibility(0);
                InstallerActivity.this.mRight.setText(R.string.install_complete);
                InstallerActivity.this.mRight.setOnClickListener(new OnClickListener() {
                    public final void onClick(View view) {
                        InstallerActivity.this.finish();
                    }
                });
            }
        });
    }

    private boolean dealUpdate(List<AppInfoLite> list) {
        if (list == null || list.size() != 1) {
            return false;
        }
        AppInfoLite appInfoLite = (AppInfoLite) list.get(0);
        if (appInfoLite == null) {
            return false;
        }
        if (Arrays.asList(new String[]{EncodeUtils.decode("Y29tLmxiZS5wYXJhbGxlbA=="), EncodeUtils.decode("aW8udmlydHVhbGFwcC5zYW5kdnhwb3NlZA=="), EncodeUtils.decode("Y29tLnNrLnNwYXRjaA=="), EncodeUtils.decode("Y29tLnFpaG9vLm1hZ2lj"), EncodeUtils.decode("Y29tLmRvdWJsZW9wZW4=")}).contains(appInfoLite.packageName)) {
            Toast.makeText(VirtualCore.get().getContext(), R.string.install_self_eggs, 0).show();
        }
        if (appInfoLite.disableMultiVersion) {
            return false;
        }
        InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(appInfoLite.packageName, 0);
        if (installedAppInfo == null) {
            return false;
        }
        PackageManager packageManager = getPackageManager();
        if (packageManager == null) {
            return false;
        }
        try {
            PackageInfo packageInfo = installedAppInfo.getPackageInfo(0);
            String str = packageInfo.versionName;
            int i = packageInfo.versionCode;
            PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(appInfoLite.path, 0);
            String str2 = packageArchiveInfo.versionName;
            int i2 = packageArchiveInfo.versionCode;
            Resources resources = getResources();
            int i3 = i == i2 ? R.string.multi_version_cover : i < i2 ? R.string.multi_version_upgrade : R.string.multi_version_downgrade;
            new Builder(this).setTitle(R.string.multi_version_tip_title).setMessage(getResources().getString(R.string.multi_version_tips_content, new Object[]{str, str2})).setPositiveButton(R.string.multi_version_multi, new DialogInterface.OnClickListener(appInfoLite) {
                private final /* synthetic */ AppInfoLite f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    InstallerActivity.this.addApp(this.f$1);
                }
            }).setNegativeButton(resources.getString(i3), new DialogInterface.OnClickListener(appInfoLite) {
                private final /* synthetic */ AppInfoLite f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    InstallerActivity.lambda$dealUpdate$116(InstallerActivity.this, this.f$1, dialogInterface, i);
                }
            }).create().show();
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    public static /* synthetic */ void lambda$dealUpdate$116(InstallerActivity installerActivity, AppInfoLite appInfoLite, DialogInterface dialogInterface, int i) {
        appInfoLite.disableMultiVersion = true;
        installerActivity.addApp(appInfoLite);
    }

    private void handleSystemIntent(Intent intent) {
        PackageInfo packageInfo;
        String str;
        CharSequence charSequence;
        String str2;
        Context context = VirtualCore.get().getContext();
        try {
            String fileFromUri = FileUtils.getFileFromUri(context, intent.getData());
            try {
                packageInfo = context.getPackageManager().getPackageArchiveInfo(fileFromUri, 128);
                try {
                    packageInfo.applicationInfo.sourceDir = fileFromUri;
                    packageInfo.applicationInfo.publicSourceDir = fileFromUri;
                } catch (Exception unused) {
                }
            } catch (Exception unused2) {
                packageInfo = null;
            }
            if (packageInfo == null) {
                finish();
                return;
            }
            if (packageInfo.applicationInfo.metaData != null) {
                boolean containsKey = packageInfo.applicationInfo.metaData.containsKey("xposedmodule");
            }
            InstalledAppInfo installedAppInfo = VirtualCore.get().getInstalledAppInfo(packageInfo.packageName, 0);
            String string = getResources().getString(17039360);
            PackageManager packageManager = getPackageManager();
            if (packageManager == null) {
                finish();
                return;
            }
            String str3 = packageInfo.packageName;
            String str4 = packageInfo.versionName;
            int i = packageInfo.versionCode;
            if (installedAppInfo != null) {
                PackageInfo packageInfo2 = installedAppInfo.getPackageInfo(0);
                if (packageInfo2 == null) {
                    finish();
                    return;
                }
                String str5 = packageInfo2.versionName;
                int i2 = packageInfo2.versionCode;
                CharSequence loadLabel = packageInfo2.applicationInfo.loadLabel(packageManager);
                Resources resources = getResources();
                int i3 = i2 == i ? R.string.multi_version_cover : i2 < i ? R.string.multi_version_upgrade : R.string.multi_version_downgrade;
                str2 = resources.getString(i3);
                str = getResources().getString(R.string.install_package_version_tips, new Object[]{str5, str4});
                charSequence = loadLabel;
            } else {
                str = getResources().getString(R.string.install_package, new Object[]{str3});
                str2 = getResources().getString(R.string.install);
                charSequence = str3;
            }
            this.mTips.setText(str);
            this.mLeft.setText(string);
            this.mRight.setText(str2);
            this.mLeft.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    InstallerActivity.this.finish();
                }
            });
            this.mRight.setOnClickListener(new OnClickListener(fileFromUri, charSequence, str3) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ CharSequence f$2;
                private final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    InstallerActivity.lambda$handleSystemIntent$124(InstallerActivity.this, this.f$1, this.f$2, this.f$3, view);
                }
            });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static /* synthetic */ void lambda$handleSystemIntent$124(InstallerActivity installerActivity, String str, CharSequence charSequence, String str2, View view) {
        installerActivity.mProgressBar.setVisibility(0);
        installerActivity.mTips.setVisibility(8);
        installerActivity.mLeft.setVisibility(8);
        installerActivity.mRight.setEnabled(false);
        VUiKit.defer().when((Callable) new Callable(str) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final Object call() {
                return VirtualCore.get().installPackage(this.f$0, 4);
            }
        }).done(new DoneCallback(charSequence, str2) {
            private final /* synthetic */ CharSequence f$1;
            private final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onDone(Object obj) {
                InstallerActivity.lambda$null$121(InstallerActivity.this, this.f$1, this.f$2, (InstallResult) obj);
            }
        }).fail(new FailCallback() {
            public final void onFail(Object obj) {
                InstallerActivity.lambda$null$123(InstallerActivity.this, (Throwable) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$null$121(InstallerActivity installerActivity, CharSequence charSequence, String str, InstallResult installResult) {
        String str2;
        installerActivity.mTips.setVisibility(8);
        installerActivity.mProgressText.setVisibility(0);
        installerActivity.mProgressText.setText(installerActivity.getResources().getString(R.string.add_app_loading_complete, new Object[]{charSequence}));
        installerActivity.mProgressBar.setVisibility(8);
        installerActivity.mRight.setVisibility(0);
        installerActivity.mRight.setEnabled(true);
        installerActivity.mRight.setText(R.string.install_complete_and_open);
        installerActivity.mRight.setOnClickListener(new OnClickListener(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                InstallerActivity.lambda$null$119(InstallerActivity.this, this.f$1, view);
            }
        });
        installerActivity.mLeft.setVisibility(0);
        installerActivity.mLeft.setEnabled(true);
        Button button = installerActivity.mLeft;
        if (installResult.isSuccess) {
            str2 = installerActivity.getResources().getString(R.string.install_complete);
        } else {
            str2 = installerActivity.getResources().getString(R.string.install_fail, new Object[]{installResult.error});
        }
        button.setText(str2);
        installerActivity.mLeft.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                InstallerActivity.this.finish();
            }
        });
    }

    public static /* synthetic */ void lambda$null$119(InstallerActivity installerActivity, String str, View view) {
        LoadingActivity.launch(installerActivity, str, 0);
        installerActivity.finish();
    }

    public static /* synthetic */ void lambda$null$123(InstallerActivity installerActivity, Throwable th) {
        String message = th.getMessage();
        if (message == null) {
            message = "Unknown";
        }
        installerActivity.mProgressText.setVisibility(0);
        installerActivity.mProgressText.setText(installerActivity.getResources().getString(R.string.install_fail, new Object[]{message}));
        installerActivity.mRight.setEnabled(true);
        installerActivity.mProgressBar.setVisibility(8);
        installerActivity.mRight.setText(17039370);
        installerActivity.mRight.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                InstallerActivity.this.finish();
            }
        });
    }
}
