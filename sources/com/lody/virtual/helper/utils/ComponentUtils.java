package com.lody.virtual.helper.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.os.IBinder;
import com.lody.virtual.GmsSupport;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.SpecialComponentList;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.stub.StubPendingActivity;
import com.lody.virtual.client.stub.StubPendingReceiver;
import com.lody.virtual.client.stub.StubPendingService;
import com.lody.virtual.helper.compat.ObjectsCompat;
import com.lody.virtual.p007os.VUserHandle;

public class ComponentUtils {
    public static String getTaskAffinity(ActivityInfo activityInfo) {
        if (activityInfo.launchMode == 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("-SingleInstance-");
            sb.append(activityInfo.packageName);
            sb.append("/");
            sb.append(activityInfo.name);
            return sb.toString();
        } else if (activityInfo.taskAffinity == null && activityInfo.applicationInfo.taskAffinity == null) {
            return activityInfo.packageName;
        } else {
            if (activityInfo.taskAffinity != null) {
                return activityInfo.taskAffinity;
            }
            return activityInfo.applicationInfo.taskAffinity;
        }
    }

    public static boolean isSameIntent(Intent intent, Intent intent2) {
        if (!(intent == null || intent2 == null)) {
            if (!ObjectsCompat.equals(intent.getAction(), intent2.getAction()) || !ObjectsCompat.equals(intent.getData(), intent2.getData()) || !ObjectsCompat.equals(intent.getType(), intent2.getType())) {
                return false;
            }
            String str = intent.getPackage();
            if (str == null && intent.getComponent() != null) {
                str = intent.getComponent().getPackageName();
            }
            String str2 = intent2.getPackage();
            if (str2 == null && intent2.getComponent() != null) {
                str2 = intent2.getComponent().getPackageName();
            }
            if (!ObjectsCompat.equals(str, str2) || !ObjectsCompat.equals(intent.getComponent(), intent2.getComponent()) || !ObjectsCompat.equals(intent.getCategories(), intent2.getCategories())) {
                return false;
            }
        }
        return true;
    }

    public static String getProcessName(ComponentInfo componentInfo) {
        String str = componentInfo.processName;
        if (str != null) {
            return str;
        }
        String str2 = componentInfo.packageName;
        componentInfo.processName = str2;
        return str2;
    }

    public static boolean isSameComponent(ComponentInfo componentInfo, ComponentInfo componentInfo2) {
        boolean z = false;
        if (componentInfo == null || componentInfo2 == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(componentInfo.packageName);
        sb.append("");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(componentInfo2.packageName);
        sb3.append("");
        String sb4 = sb3.toString();
        StringBuilder sb5 = new StringBuilder();
        sb5.append(componentInfo.name);
        sb5.append("");
        String sb6 = sb5.toString();
        StringBuilder sb7 = new StringBuilder();
        sb7.append(componentInfo2.name);
        sb7.append("");
        String sb8 = sb7.toString();
        if (sb2.equals(sb4) && sb6.equals(sb8)) {
            z = true;
        }
        return z;
    }

    public static ComponentName toComponentName(ComponentInfo componentInfo) {
        return new ComponentName(componentInfo.packageName, componentInfo.name);
    }

    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        if (GmsSupport.isGmsFamilyPackage(applicationInfo.packageName) || ((applicationInfo.flags & 1) == 0 && !SpecialComponentList.isSpecSystemPackage(applicationInfo.packageName))) {
            return false;
        }
        return true;
    }

    public static boolean isStubComponent(Intent intent) {
        return (intent == null || intent.getComponent() == null || !VirtualCore.get().getHostPkg().equals(intent.getComponent().getPackageName())) ? false : true;
    }

    public static Intent redirectBroadcastIntent(Intent intent, int i) {
        Intent cloneFilter = intent.cloneFilter();
        cloneFilter.setComponent(null);
        cloneFilter.setPackage(null);
        ComponentName component = intent.getComponent();
        String str = intent.getPackage();
        if (component != null) {
            cloneFilter.putExtra("_VA_|_user_id_", i);
            cloneFilter.setAction(String.format("_VA_%s_%s", new Object[]{component.getPackageName(), component.getClassName()}));
            cloneFilter.putExtra("_VA_|_component_", component);
            cloneFilter.putExtra("_VA_|_intent_", new Intent(intent));
        } else if (str != null) {
            cloneFilter.putExtra("_VA_|_user_id_", i);
            cloneFilter.putExtra("_VA_|_creator_", str);
            cloneFilter.putExtra("_VA_|_intent_", new Intent(intent));
            String protectAction = SpecialComponentList.protectAction(intent.getAction());
            if (protectAction != null) {
                cloneFilter.setAction(protectAction);
            }
        } else {
            cloneFilter.putExtra("_VA_|_user_id_", i);
            cloneFilter.putExtra("_VA_|_intent_", new Intent(intent));
            String protectAction2 = SpecialComponentList.protectAction(intent.getAction());
            if (protectAction2 != null) {
                cloneFilter.setAction(protectAction2);
            }
        }
        return cloneFilter;
    }

    public static Intent redirectIntentSender(int i, String str, Intent intent, IBinder iBinder) {
        Intent cloneFilter = intent.cloneFilter();
        if (i != 4) {
            switch (i) {
                case 1:
                    cloneFilter.setClass(VirtualCore.get().getContext(), StubPendingReceiver.class);
                    break;
                case 2:
                    if (VirtualCore.get().resolveActivityInfo(intent, VUserHandle.myUserId()) != null) {
                        cloneFilter.setClass(VirtualCore.get().getContext(), StubPendingActivity.class);
                        cloneFilter.setFlags(intent.getFlags());
                        if (iBinder != null) {
                            try {
                                ComponentName activityForToken = VActivityManager.get().getActivityForToken(iBinder);
                                if (activityForToken != null) {
                                    cloneFilter.putExtra("_VA_|_caller_", activityForToken);
                                    break;
                                }
                            } catch (Throwable unused) {
                                break;
                            }
                        }
                    }
                    break;
                default:
                    return null;
            }
        } else if (VirtualCore.get().resolveServiceInfo(intent, VUserHandle.myUserId()) != null) {
            cloneFilter.setClass(VirtualCore.get().getContext(), StubPendingService.class);
        }
        cloneFilter.putExtra("_VA_|_user_id_", VUserHandle.myUserId());
        cloneFilter.putExtra("_VA_|_intent_", intent);
        cloneFilter.putExtra("_VA_|_creator_", str);
        cloneFilter.putExtra("_VA_|_from_inner_", true);
        return cloneFilter;
    }
}
