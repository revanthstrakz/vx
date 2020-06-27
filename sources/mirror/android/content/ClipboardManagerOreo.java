package mirror.android.content;

import android.content.ClipboardManager;
import android.os.IInterface;
import mirror.RefClass;
import mirror.RefObject;

public class ClipboardManagerOreo {
    public static Class<?> TYPE = RefClass.load(ClipboardManagerOreo.class, ClipboardManager.class);
    public static RefObject<IInterface> mService;
}
