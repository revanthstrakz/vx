package p011de.robv.android.xposed;

import p011de.robv.android.xposed.callbacks.XC_InitPackageResources;
import p011de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

/* renamed from: de.robv.android.xposed.IXposedHookInitPackageResources */
public interface IXposedHookInitPackageResources extends IXposedMod {

    /* renamed from: de.robv.android.xposed.IXposedHookInitPackageResources$Wrapper */
    public static final class Wrapper extends XC_InitPackageResources {
        private final IXposedHookInitPackageResources instance;

        public Wrapper(IXposedHookInitPackageResources iXposedHookInitPackageResources) {
            this.instance = iXposedHookInitPackageResources;
        }

        public void handleInitPackageResources(InitPackageResourcesParam initPackageResourcesParam) throws Throwable {
            this.instance.handleInitPackageResources(initPackageResourcesParam);
        }
    }

    void handleInitPackageResources(InitPackageResourcesParam initPackageResourcesParam) throws Throwable;
}
