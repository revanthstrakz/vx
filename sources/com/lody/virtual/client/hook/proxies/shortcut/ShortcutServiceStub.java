package com.lody.virtual.client.hook.proxies.shortcut;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.PersistableBundle;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.helper.compat.ParceledListSliceCompat;
import java.lang.reflect.Method;
import java.util.List;
import mirror.android.content.p016pm.IShortcutService.Stub;
import mirror.android.content.p016pm.ParceledListSlice;

public class ShortcutServiceStub extends BinderInvocationProxy {

    private static class ReplacePkgAndShortcutListMethodProxy extends ReplaceCallingPkgMethodProxy {
        ReplacePkgAndShortcutListMethodProxy(String str) {
            super(str);
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            List<ShortcutInfo> findFirstShortcutList = findFirstShortcutList(objArr);
            if (findFirstShortcutList != null) {
                String hostPkg = getHostPkg();
                for (ShortcutInfo access$000 : findFirstShortcutList) {
                    ShortcutServiceStub.replaceShortcutInfo(access$000, hostPkg, getPM());
                }
            }
            return super.beforeCall(obj, method, objArr);
        }

        @TargetApi(25)
        private List<ShortcutInfo> findFirstShortcutList(Object... objArr) {
            if (objArr == null) {
                return null;
            }
            for (Object obj : objArr) {
                if (obj.getClass().isAssignableFrom(ParceledListSlice.TYPE)) {
                    return ParceledListSliceCompat.getList(obj);
                }
            }
            return null;
        }
    }

    private static class ReplacePkgAndShortcutMethodProxy extends ReplaceCallingPkgMethodProxy {
        ReplacePkgAndShortcutMethodProxy(String str) {
            super(str);
        }

        @TargetApi(21)
        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            ShortcutServiceStub.replaceShortcutInfo(findFirstShortcutInfo(objArr), getHostPkg(), getPM());
            return super.beforeCall(obj, method, objArr);
        }

        @TargetApi(25)
        private ShortcutInfo findFirstShortcutInfo(Object[] objArr) {
            if (objArr == null) {
                return null;
            }
            for (ShortcutInfo shortcutInfo : objArr) {
                if (shortcutInfo.getClass() == mirror.android.content.p016pm.ShortcutInfo.TYPE) {
                    return shortcutInfo;
                }
            }
            return null;
        }
    }

    public ShortcutServiceStub() {
        super(Stub.asInterface, "shortcut");
    }

    public void inject() throws Throwable {
        super.inject();
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getManifestShortcuts"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getDynamicShortcuts"));
        addMethodProxy((MethodProxy) new ReplacePkgAndShortcutListMethodProxy("setDynamicShortcuts"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("addDynamicShortcuts"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("createShortcutResultIntent"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("disableShortcuts"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("enableShortcuts"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getRemainingCallCount"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getRateLimitResetTime"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getIconMaxDimensions"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getMaxShortcutCountPerActivity"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("reportShortcutUsed"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("onApplicationActive"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getPinnedShortcuts"));
        addMethodProxy((MethodProxy) new ReplacePkgAndShortcutMethodProxy("requestPinShortcut"));
    }

    /* access modifiers changed from: private */
    @TargetApi(23)
    public static void replaceShortcutInfo(ShortcutInfo shortcutInfo, String str, PackageManager packageManager) {
        if (shortcutInfo != null) {
            mirror.android.content.p016pm.ShortcutInfo.mPackageName.set(shortcutInfo, str);
            try {
                mirror.android.content.p016pm.ShortcutInfo.mIcon.set(shortcutInfo, Icon.createWithBitmap(((BitmapDrawable) packageManager.getApplicationIcon(str)).getBitmap()));
            } catch (Throwable unused) {
            }
            Intent[] intentArr = (Intent[]) mirror.android.content.p016pm.ShortcutInfo.mIntents.get(shortcutInfo);
            if (intentArr != null) {
                int length = intentArr.length;
                Intent[] intentArr2 = new Intent[length];
                PersistableBundle[] persistableBundleArr = (PersistableBundle[]) mirror.android.content.p016pm.ShortcutInfo.mIntentPersistableExtrases.get(shortcutInfo);
                if (persistableBundleArr == null) {
                    persistableBundleArr = new PersistableBundle[length];
                }
                for (int i = 0; i < length; i++) {
                    Intent intent = intentArr[i];
                    PersistableBundle persistableBundle = persistableBundleArr[i];
                    if (persistableBundle == null) {
                        persistableBundle = new PersistableBundle();
                    }
                    Intent intent2 = new Intent();
                    intent2.setClassName(str, Constants.SHORTCUT_PROXY_ACTIVITY_NAME);
                    intent2.addCategory("android.intent.category.DEFAULT");
                    persistableBundle.putString("_VA_|_uri_", intent.toUri(0));
                    persistableBundle.putInt("_VA_|_user_id_", 0);
                    intentArr2[i] = intent2;
                }
                System.arraycopy(intentArr2, 0, intentArr, 0, length);
                mirror.android.content.p016pm.ShortcutInfo.mIntentPersistableExtrases.set(shortcutInfo, persistableBundleArr);
            }
        }
    }
}
