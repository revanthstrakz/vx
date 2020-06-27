package p015me.weishu.exposed;

import android.content.pm.ApplicationInfo;

/* renamed from: me.weishu.exposed.ModuleLoadListener */
public interface ModuleLoadListener {
    void onLoadingModule(String str, ApplicationInfo applicationInfo, ClassLoader classLoader);

    void onModuleLoaded(String str, ApplicationInfo applicationInfo, ClassLoader classLoader);
}
