package mirror.android.p017os;

import android.os.IBinder;
import android.os.IInterface;
import java.util.Map;
import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;
import mirror.RefStaticObject;

/* renamed from: mirror.android.os.ServiceManager */
public class ServiceManager {
    public static Class<?> TYPE = RefClass.load(ServiceManager.class, "android.os.ServiceManager");
    @MethodParams({String.class, IBinder.class})
    public static RefStaticMethod<Void> addService;
    public static RefStaticMethod<IBinder> checkService;
    public static RefStaticMethod<IInterface> getIServiceManager;
    public static RefStaticMethod<IBinder> getService;
    public static RefStaticMethod<String[]> listServices;
    public static RefStaticObject<Map<String, IBinder>> sCache;
}
