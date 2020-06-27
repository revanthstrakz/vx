package p013io.virtualapp.splash;

import android.os.Bundle;
import com.lody.virtual.client.core.VirtualCore;
import io.va.exposed.R;
import jonathanfinerty.once.Once;
import org.jdeferred.DoneCallback;
import p013io.virtualapp.VCommends;
import p013io.virtualapp.abs.p014ui.VActivity;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.home.NewHomeActivity;

/* renamed from: io.virtualapp.splash.SplashActivity */
public class SplashActivity extends VActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Once.beenDone(0, VCommends.TAG_NEW_VERSION);
        getWindow().setFlags(1024, 1024);
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_splash);
        VUiKit.defer().when((Runnable) new Runnable() {
            public final void run() {
                SplashActivity.lambda$onCreate$84(SplashActivity.this);
            }
        }).done(new DoneCallback() {
            public final void onDone(Object obj) {
                SplashActivity.lambda$onCreate$85(SplashActivity.this, (Void) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$onCreate$84(SplashActivity splashActivity) {
        long currentTimeMillis = System.currentTimeMillis();
        splashActivity.doActionInThread();
        long currentTimeMillis2 = 100 - (System.currentTimeMillis() - currentTimeMillis);
        if (currentTimeMillis2 > 0) {
            VUiKit.sleep(currentTimeMillis2);
        }
    }

    public static /* synthetic */ void lambda$onCreate$85(SplashActivity splashActivity, Void voidR) {
        NewHomeActivity.goHome(splashActivity);
        splashActivity.finish();
    }

    private void doActionInThread() {
        if (!VirtualCore.get().isEngineLaunched()) {
            VirtualCore.get().waitForEngine();
        }
    }
}
