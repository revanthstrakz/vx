package p013io.virtualapp.utils;

import android.support.p004v7.app.AlertDialog;

/* renamed from: io.virtualapp.utils.DialogUtil */
public class DialogUtil {
    public static void showDialog(AlertDialog alertDialog) {
        if (alertDialog != null) {
            try {
                alertDialog.show();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }
}
