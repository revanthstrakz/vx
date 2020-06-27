package p013io.virtualapp.delegate;

import android.app.Application;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.core.VirtualCore.VirtualInitializer;
import com.lody.virtual.p007os.VEnvironment;
import jonathanfinerty.once.Once;
import p013io.virtualapp.XApp;
import p015me.weishu.exposed.LogcatService;

/* renamed from: io.virtualapp.delegate.BaseVirtualInitializer */
public class BaseVirtualInitializer extends VirtualInitializer {
    protected Application application;
    protected VirtualCore virtualCore;

    public BaseVirtualInitializer(Application application2, VirtualCore virtualCore2) {
        this.application = application2;
        this.virtualCore = virtualCore2;
    }

    public void onMainProcess() {
        Once.initialise(this.application);
    }

    public void onVirtualProcess() {
        this.virtualCore.setCrashHandler(new BaseCrashHandler());
        this.virtualCore.setComponentDelegate(new MyComponentDelegate());
        this.virtualCore.setPhoneInfoDelegate(new MyPhoneInfoDelegate());
        this.virtualCore.setTaskDescriptionDelegate(new MyTaskDescDelegate());
        LogcatService.start(this.application, VEnvironment.getDataUserPackageDirectory(0, XApp.XPOSED_INSTALLER_PACKAGE));
    }

    public void onServerProcess() {
        this.virtualCore.setAppRequestListener(new MyAppRequestListener(this.application));
        this.virtualCore.addVisibleOutsidePackage("com.tencent.mobileqq");
        this.virtualCore.addVisibleOutsidePackage("com.tencent.mobileqqi");
        this.virtualCore.addVisibleOutsidePackage("com.tencent.minihd.qq");
        this.virtualCore.addVisibleOutsidePackage("com.tencent.qqlite");
        this.virtualCore.addVisibleOutsidePackage("com.facebook.katana");
        this.virtualCore.addVisibleOutsidePackage("com.whatsapp");
        this.virtualCore.addVisibleOutsidePackage("com.tencent.mm");
        this.virtualCore.addVisibleOutsidePackage("com.immomo.momo");
    }
}
