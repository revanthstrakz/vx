package p013io.virtualapp.delegate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.lody.virtual.client.core.VirtualCore.AppRequestListener;
import java.io.File;
import p013io.virtualapp.sys.InstallerActivity;

/* renamed from: io.virtualapp.delegate.MyAppRequestListener */
public class MyAppRequestListener implements AppRequestListener {
    private final Context context;

    public MyAppRequestListener(Context context2) {
        this.context = context2;
    }

    public void onRequestInstall(String str) {
        try {
            Intent intent = new Intent(this.context, InstallerActivity.class);
            intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.android.package-archive");
            intent.addFlags(268435456);
            this.context.startActivity(intent);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void onRequestUninstall(String str) {
        Context context2 = this.context;
        StringBuilder sb = new StringBuilder();
        sb.append("Uninstall: ");
        sb.append(str);
        Toast.makeText(context2, sb.toString(), 0).show();
    }
}
