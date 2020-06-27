package mirror.android.content.p016pm;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.PersistableBundle;
import mirror.RefClass;
import mirror.RefObject;

/* renamed from: mirror.android.content.pm.ShortcutInfo */
public class ShortcutInfo {
    public static Class<?> TYPE = RefClass.load(ShortcutInfo.class, "android.content.pm.ShortcutInfo");
    public static RefObject<Icon> mIcon;
    public static RefObject<PersistableBundle[]> mIntentPersistableExtrases;
    public static RefObject<Intent[]> mIntents;
    public static RefObject<String> mPackageName;
}
