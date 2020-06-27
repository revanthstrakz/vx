package mirror.android.p017os;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/* renamed from: mirror.android.os.Message */
public class Message {
    public static Class<?> TYPE = RefClass.load(Message.class, android.os.Message.class);
    @MethodParams({int.class})
    public static RefStaticMethod<Void> updateCheckRecycle;
}
