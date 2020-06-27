package p013io.virtualapp.delegate;

import android.app.Application;
import com.lody.virtual.client.core.VirtualCore;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

/* renamed from: io.virtualapp.delegate.MyVirtualInitializer */
public class MyVirtualInitializer extends BaseVirtualInitializer {
    public MyVirtualInitializer(Application application, VirtualCore virtualCore) {
        super(application, virtualCore);
    }

    public void onMainProcess() {
        AppCenter.start(this.application, "bf5e74bd-3795-49bd-95c8-327db494dd11", Analytics.class, Crashes.class);
        super.onMainProcess();
    }

    public void onVirtualProcess() {
        AppCenter.start(this.application, "bf5e74bd-3795-49bd-95c8-327db494dd11", Analytics.class, Crashes.class);
        super.onVirtualProcess();
        this.virtualCore.setCrashHandler(new MyCrashHandler());
    }
}
