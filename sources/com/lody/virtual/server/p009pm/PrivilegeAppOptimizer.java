package com.lody.virtual.server.p009pm;

import android.content.Intent;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.server.p008am.VActivityManagerService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* renamed from: com.lody.virtual.server.pm.PrivilegeAppOptimizer */
public class PrivilegeAppOptimizer {
    private static final PrivilegeAppOptimizer sInstance = new PrivilegeAppOptimizer();
    private final List<String> privilegeApps = new ArrayList();

    private PrivilegeAppOptimizer() {
        Collections.addAll(this.privilegeApps, VASettings.PRIVILEGE_APPS);
    }

    public static PrivilegeAppOptimizer get() {
        return sInstance;
    }

    public List<String> getPrivilegeApps() {
        return Collections.unmodifiableList(this.privilegeApps);
    }

    public void addPrivilegeApp(String str) {
        this.privilegeApps.add(str);
    }

    public void removePrivilegeApp(String str) {
        this.privilegeApps.remove(str);
    }

    public boolean isPrivilegeApp(String str) {
        return this.privilegeApps.contains(str);
    }

    public void performOptimizeAllApps() {
        for (String performOptimize : this.privilegeApps) {
            performOptimize(performOptimize, -1);
        }
    }

    public boolean performOptimize(String str, int i) {
        VActivityManagerService.get().sendBroadcastAsUser(specifyApp(new Intent("android.intent.action.BOOT_COMPLETED"), str, i), new VUserHandle(i));
        return true;
    }

    public static void notifyBootFinish() {
        for (String performOptimize : Constants.PRIVILEGE_APP) {
            try {
                get().performOptimize(performOptimize, 0);
            } catch (Throwable unused) {
            }
        }
    }

    private Intent specifyApp(Intent intent, String str, int i) {
        intent.putExtra("_VA_|_privilege_pkg_", str);
        intent.putExtra("_VA_|_user_id_", i);
        intent.putExtra("_VA_|_intent_", new Intent("android.intent.action.BOOT_COMPLETED"));
        return intent;
    }
}
