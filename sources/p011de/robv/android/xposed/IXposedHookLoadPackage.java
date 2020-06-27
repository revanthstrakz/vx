package p011de.robv.android.xposed;

import p011de.robv.android.xposed.callbacks.XC_LoadPackage;
import p011de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/* renamed from: de.robv.android.xposed.IXposedHookLoadPackage */
public interface IXposedHookLoadPackage extends IXposedMod {

    /* renamed from: de.robv.android.xposed.IXposedHookLoadPackage$Wrapper */
    public static final class Wrapper extends XC_LoadPackage {
        private final IXposedHookLoadPackage instance;

        public Wrapper(IXposedHookLoadPackage iXposedHookLoadPackage) {
            this.instance = iXposedHookLoadPackage;
        }

        public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {
            this.instance.handleLoadPackage(loadPackageParam);
        }
    }

    void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable;
}
