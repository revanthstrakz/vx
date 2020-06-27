package p013io.virtualapp.dev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.widget.Toast;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstallResult;
import p013io.virtualapp.home.LoadingActivity;

/* renamed from: io.virtualapp.dev.CmdReceiver */
public class CmdReceiver extends BroadcastReceiver {
    private static final String ACTION = "io.va.exposed.CMD";
    private static final String CMD_LAUNCH = "launch";
    private static final String CMD_REBOOT = "reboot";
    private static final String CMD_UPDATE = "update";
    private static final String KEY_CMD = "cmd";
    private static final String KEY_PKG = "pkg";
    private static final String KEY_UID = "uid";

    public void onReceive(Context context, Intent intent) {
        int i;
        if (ACTION.equalsIgnoreCase(intent.getAction())) {
            String stringExtra = intent.getStringExtra(KEY_CMD);
            if (TextUtils.isEmpty(stringExtra)) {
                showTips(context, "No cmd found!");
            } else if (CMD_REBOOT.equalsIgnoreCase(stringExtra)) {
                VirtualCore.get().killAllApps();
                showTips(context, "Reboot Success!!");
            } else {
                if (CMD_UPDATE.equalsIgnoreCase(stringExtra)) {
                    String stringExtra2 = intent.getStringExtra(KEY_PKG);
                    if (TextUtils.isEmpty(stringExtra2)) {
                        showTips(context, "Please tell me the update package!!");
                        return;
                    }
                    PackageManager packageManager = context.getPackageManager();
                    if (packageManager == null) {
                        showTips(context, "system error, update failed!");
                        return;
                    }
                    try {
                        InstallResult installPackage = VirtualCore.get().installPackage(packageManager.getApplicationInfo(stringExtra2, 0).sourceDir, 4);
                        if (!installPackage.isSuccess) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Update ");
                            sb.append(stringExtra2);
                            sb.append(" failed: ");
                            sb.append(installPackage.error);
                            showTips(context, sb.toString());
                        } else if (installPackage.isUpdate) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Update ");
                            sb2.append(stringExtra2);
                            sb2.append(" Success!!");
                            showTips(context, sb2.toString());
                        }
                    } catch (NameNotFoundException unused) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Can not found ");
                        sb3.append(stringExtra2);
                        sb3.append(" outside!");
                        showTips(context, sb3.toString());
                    }
                } else if (CMD_LAUNCH.equalsIgnoreCase(stringExtra)) {
                    String stringExtra3 = intent.getStringExtra(KEY_PKG);
                    if (TextUtils.isEmpty(stringExtra3)) {
                        showTips(context, "Please tell me the launch package!!");
                        return;
                    }
                    String stringExtra4 = intent.getStringExtra(KEY_UID);
                    if (!TextUtils.isEmpty(stringExtra4)) {
                        try {
                            i = Integer.parseInt(stringExtra4);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        LoadingActivity.launch(context, stringExtra3, i);
                    }
                    i = 0;
                    LoadingActivity.launch(context, stringExtra3, i);
                }
            }
        }
    }

    private void showTips(Context context, String str) {
        Toast.makeText(context, str, 0).show();
    }
}
