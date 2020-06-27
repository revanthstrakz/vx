package mehdi.sakout.aboutpage;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.util.TypedValue;

class AboutPageUtils {
    AboutPageUtils() {
    }

    static Boolean isAppInstalled(Context context, String str) {
        boolean z = true;
        try {
            context.getPackageManager().getPackageInfo(str, 1);
        } catch (NameNotFoundException unused) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    static int getThemeAccentColor(Context context) {
        int i;
        if (VERSION.SDK_INT >= 21) {
            i = 16843829;
        } else {
            i = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.data;
    }
}
