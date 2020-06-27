package p011de.robv.android.xposed.callbacks;

import android.content.pm.ApplicationInfo;
import p011de.robv.android.xposed.IXposedHookLoadPackage;
import p011de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet;
import p011de.robv.android.xposed.callbacks.XCallback.Param;

/* renamed from: de.robv.android.xposed.callbacks.XC_LoadPackage */
public abstract class XC_LoadPackage extends XCallback implements IXposedHookLoadPackage {

    /* renamed from: de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam */
    public static final class LoadPackageParam extends Param {
        public ApplicationInfo appInfo;
        public ClassLoader classLoader;
        public boolean isFirstApplication;
        public String packageName;
        public String processName;

        public LoadPackageParam(CopyOnWriteSortedSet<XC_LoadPackage> copyOnWriteSortedSet) {
            super(copyOnWriteSortedSet);
        }
    }

    public XC_LoadPackage() {
    }

    public XC_LoadPackage(int i) {
        super(i);
    }

    /* access modifiers changed from: protected */
    public void call(Param param) throws Throwable {
        if (param instanceof LoadPackageParam) {
            handleLoadPackage((LoadPackageParam) param);
        }
    }
}
