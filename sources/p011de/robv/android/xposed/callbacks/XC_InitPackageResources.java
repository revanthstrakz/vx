package p011de.robv.android.xposed.callbacks;

import android.content.res.XResources;
import p011de.robv.android.xposed.IXposedHookInitPackageResources;
import p011de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet;
import p011de.robv.android.xposed.callbacks.XCallback.Param;

/* renamed from: de.robv.android.xposed.callbacks.XC_InitPackageResources */
public abstract class XC_InitPackageResources extends XCallback implements IXposedHookInitPackageResources {

    /* renamed from: de.robv.android.xposed.callbacks.XC_InitPackageResources$InitPackageResourcesParam */
    public static final class InitPackageResourcesParam extends Param {
        public String packageName;
        public XResources res;

        public InitPackageResourcesParam(CopyOnWriteSortedSet<XC_InitPackageResources> copyOnWriteSortedSet) {
            super(copyOnWriteSortedSet);
        }
    }

    public XC_InitPackageResources() {
    }

    public XC_InitPackageResources(int i) {
        super(i);
    }

    /* access modifiers changed from: protected */
    public void call(Param param) throws Throwable {
        if (param instanceof InitPackageResourcesParam) {
            handleInitPackageResources((InitPackageResourcesParam) param);
        }
    }
}
