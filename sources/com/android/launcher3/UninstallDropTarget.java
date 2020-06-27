package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AttributeSet;
import android.widget.Toast;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.p007os.VUserManager;

public class UninstallDropTarget extends ButtonDropTarget {
    private static final String TAG = "UninstallDropTarget";
    private static Boolean sUninstallDisabled;

    public interface DropTargetResultCallback {
        void onDragObjectRemoved(boolean z);
    }

    public interface DropTargetSource extends DropTargetResultCallback {
        void deferCompleteDropAfterUninstallActivity();
    }

    public UninstallDropTarget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public UninstallDropTarget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setupUi();
    }

    /* access modifiers changed from: protected */
    public void setupUi() {
        this.mHoverColor = getResources().getColor(C0622R.color.uninstall_target_hover_tint);
        setDrawable(C0622R.C0624drawable.ic_uninstall_shadow);
    }

    /* access modifiers changed from: protected */
    public boolean supportsDrop(DragSource dragSource, ItemInfo itemInfo) {
        return supportsDrop(getContext(), itemInfo);
    }

    public static boolean supportsDrop(Context context, ItemInfo itemInfo) {
        boolean z = true;
        if (sUninstallDisabled == null) {
            Bundle userRestrictions = ((UserManager) context.getSystemService(ServiceManagerNative.USER)).getUserRestrictions();
            sUninstallDisabled = Boolean.valueOf(userRestrictions.getBoolean("no_control_apps", false) || userRestrictions.getBoolean(VUserManager.DISALLOW_UNINSTALL_APPS, false));
        }
        if (sUninstallDisabled.booleanValue()) {
            return false;
        }
        if (itemInfo instanceof AppInfo) {
            AppInfo appInfo = (AppInfo) itemInfo;
            if (appInfo.isSystemApp != 0) {
                if ((appInfo.isSystemApp & 2) == 0) {
                    z = false;
                }
                return z;
            }
        }
        if (getUninstallTarget(context, itemInfo) == null) {
            z = false;
        }
        return z;
    }

    private static ComponentName getUninstallTarget(Context context, ItemInfo itemInfo) {
        UserHandle userHandle;
        Intent intent;
        if (itemInfo == null || itemInfo.itemType != 0) {
            userHandle = null;
            intent = null;
        } else {
            intent = itemInfo.getIntent();
            userHandle = itemInfo.user;
        }
        if (intent != null) {
            LauncherActivityInfo resolveActivity = LauncherAppsCompat.getInstance(context).resolveActivity(intent, userHandle);
            if (resolveActivity != null && (resolveActivity.getApplicationInfo().flags & 1) == 0) {
                return resolveActivity.getComponentName();
            }
        }
        return null;
    }

    public void onDrop(DragObject dragObject) {
        if (dragObject.dragSource instanceof DropTargetSource) {
            ((DropTargetSource) dragObject.dragSource).deferCompleteDropAfterUninstallActivity();
        }
        super.onDrop(dragObject);
    }

    public void completeDrop(DragObject dragObject) {
        startUninstallActivity(this.mLauncher, dragObject.dragInfo, dragObject.dragSource instanceof DropTargetResultCallback ? (DropTargetResultCallback) dragObject.dragSource : null);
    }

    public static boolean startUninstallActivity(Launcher launcher, ItemInfo itemInfo) {
        return startUninstallActivity(launcher, itemInfo, null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x009f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean startUninstallActivity(com.android.launcher3.Launcher r9, com.android.launcher3.ItemInfo r10, com.android.launcher3.UninstallDropTarget.DropTargetResultCallback r11) {
        /*
            android.content.ComponentName r0 = getUninstallTarget(r9, r10)
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x0014
            int r1 = com.android.launcher3.C0622R.string.uninstall_system_app_text
            android.widget.Toast r1 = android.widget.Toast.makeText(r9, r1, r2)
            r1.show()
        L_0x0011:
            r1 = 0
            goto L_0x009d
        L_0x0014:
            java.lang.String r3 = r0.getPackageName()
            com.lody.virtual.client.core.VirtualCore r4 = com.lody.virtual.client.core.VirtualCore.get()
            boolean r4 = r4.isAppInstalled(r3)
            if (r4 == 0) goto L_0x005d
            android.app.AlertDialog$Builder r4 = new android.app.AlertDialog$Builder
            r4.<init>(r9)
            int r5 = com.android.launcher3.C0622R.string.home_menu_delete_title
            android.app.AlertDialog$Builder r4 = r4.setTitle(r5)
            android.content.res.Resources r5 = r9.getResources()
            int r6 = com.android.launcher3.C0622R.string.home_menu_delete_content
            java.lang.Object[] r7 = new java.lang.Object[r1]
            java.lang.CharSequence r8 = r10.title
            r7[r2] = r8
            java.lang.String r5 = r5.getString(r6, r7)
            android.app.AlertDialog$Builder r4 = r4.setMessage(r5)
            r5 = 17039379(0x1040013, float:2.4244624E-38)
            com.android.launcher3.-$$Lambda$UninstallDropTarget$VvU4lwpJhExlLIws_Wpp2CHHLgw r6 = new com.android.launcher3.-$$Lambda$UninstallDropTarget$VvU4lwpJhExlLIws_Wpp2CHHLgw
            r6.<init>(r3, r9)
            android.app.AlertDialog$Builder r3 = r4.setPositiveButton(r5, r6)
            r4 = 17039369(0x1040009, float:2.4244596E-38)
            r5 = 0
            android.app.AlertDialog$Builder r3 = r3.setNegativeButton(r4, r5)
            android.app.AlertDialog r3 = r3.create()
            r3.show()     // Catch:{ Throwable -> 0x0011 }
            goto L_0x009d
        L_0x005d:
            int r3 = com.android.launcher3.C0622R.string.delete_package_intent     // Catch:{ URISyntaxException -> 0x0085 }
            java.lang.String r3 = r9.getString(r3)     // Catch:{ URISyntaxException -> 0x0085 }
            android.content.Intent r3 = android.content.Intent.parseUri(r3, r2)     // Catch:{ URISyntaxException -> 0x0085 }
            java.lang.String r4 = "package"
            java.lang.String r5 = r0.getPackageName()     // Catch:{ URISyntaxException -> 0x0085 }
            java.lang.String r6 = r0.getClassName()     // Catch:{ URISyntaxException -> 0x0085 }
            android.net.Uri r4 = android.net.Uri.fromParts(r4, r5, r6)     // Catch:{ URISyntaxException -> 0x0085 }
            android.content.Intent r3 = r3.setData(r4)     // Catch:{ URISyntaxException -> 0x0085 }
            java.lang.String r4 = "android.intent.extra.USER"
            android.os.UserHandle r5 = r10.user     // Catch:{ URISyntaxException -> 0x0085 }
            android.content.Intent r3 = r3.putExtra(r4, r5)     // Catch:{ URISyntaxException -> 0x0085 }
            r9.startActivity(r3)     // Catch:{ URISyntaxException -> 0x0085 }
            goto L_0x009d
        L_0x0085:
            java.lang.String r1 = "UninstallDropTarget"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Failed to parse intent to start uninstall activity for item="
            r3.append(r4)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            android.util.Log.e(r1, r3)
            goto L_0x0011
        L_0x009d:
            if (r11 == 0) goto L_0x00a4
            android.os.UserHandle r10 = r10.user
            sendUninstallResult(r9, r1, r0, r10, r11)
        L_0x00a4:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.UninstallDropTarget.startUninstallActivity(com.android.launcher3.Launcher, com.android.launcher3.ItemInfo, com.android.launcher3.UninstallDropTarget$DropTargetResultCallback):boolean");
    }

    static /* synthetic */ void lambda$startUninstallActivity$0(ItemInfo itemInfo, String str, Launcher launcher, DialogInterface dialogInterface, int i) {
        try {
            VirtualCore.get().uninstallPackageAsUser(str, UserManagerCompat.toUserId(itemInfo.user));
        } catch (Throwable unused) {
            Toast.makeText(launcher, "Uninstall failed, please try again.", 0).show();
        }
    }

    protected static void sendUninstallResult(final Launcher launcher, boolean z, final ComponentName componentName, final UserHandle userHandle, final DropTargetResultCallback dropTargetResultCallback) {
        if (z) {
            launcher.addOnResumeCallback(new Runnable() {
                public void run() {
                    dropTargetResultCallback.onDragObjectRemoved(LauncherAppsCompat.getInstance(launcher).getApplicationInfo(componentName.getPackageName(), 8192, userHandle) == null);
                }
            });
        } else {
            dropTargetResultCallback.onDragObjectRemoved(false);
        }
    }
}
