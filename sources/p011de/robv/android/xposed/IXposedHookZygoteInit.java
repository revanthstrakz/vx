package p011de.robv.android.xposed;

/* renamed from: de.robv.android.xposed.IXposedHookZygoteInit */
public interface IXposedHookZygoteInit extends IXposedMod {

    /* renamed from: de.robv.android.xposed.IXposedHookZygoteInit$StartupParam */
    public static final class StartupParam {
        public String modulePath;
        public boolean startsSystemServer;

        StartupParam() {
        }
    }

    void initZygote(StartupParam startupParam) throws Throwable;
}
