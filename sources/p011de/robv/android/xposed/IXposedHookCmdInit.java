package p011de.robv.android.xposed;

/* renamed from: de.robv.android.xposed.IXposedHookCmdInit */
public interface IXposedHookCmdInit extends IXposedMod {

    /* renamed from: de.robv.android.xposed.IXposedHookCmdInit$StartupParam */
    public static final class StartupParam {
        public String modulePath;
        public String startClassName;

        StartupParam() {
        }
    }

    void initCmdApp(StartupParam startupParam) throws Throwable;
}
