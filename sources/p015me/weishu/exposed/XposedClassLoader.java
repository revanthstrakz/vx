package p015me.weishu.exposed;

import p011de.robv.android.xposed.BuildConfig;

/* renamed from: me.weishu.exposed.XposedClassLoader */
class XposedClassLoader extends ClassLoader {
    private ClassLoader mHostClassLoader;

    XposedClassLoader(ClassLoader classLoader) {
        super(ClassLoader.getSystemClassLoader().getParent());
        this.mHostClassLoader = classLoader;
    }

    /* access modifiers changed from: protected */
    public Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
        if (str.startsWith(BuildConfig.APPLICATION_ID) || str.startsWith("android") || str.startsWith("external") || str.startsWith("me.weishu.epic.art") || str.startsWith("com.taobao.android.dexposed")) {
            return this.mHostClassLoader.loadClass(str);
        }
        return super.loadClass(str, z);
    }
}
