package p013io.virtualapp.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.support.p004v7.app.AlertDialog.Builder;
import android.widget.Toast;
import io.va.exposed.R;
import moe.feng.alipay.zerosdk.AlipayZeroSdk;

/* renamed from: io.virtualapp.utils.Misc */
public class Misc {
    public static void showDonate(Activity activity) {
        new Builder(activity, 2131951907).setTitle((int) R.string.donate_choose_title).setItems((CharSequence[]) new String[]{activity.getResources().getString(R.string.donate_alipay), "PayPal", "Bitcoin"}, (OnClickListener) new OnClickListener(activity) {
            private final /* synthetic */ Activity f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                Misc.lambda$showDonate$86(this.f$0, dialogInterface, i);
            }
        }).create().show();
    }

    static /* synthetic */ void lambda$showDonate$86(Activity activity, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        if (i == 0) {
            if (!AlipayZeroSdk.hasInstalledAlipayClient(activity)) {
                Toast.makeText(activity, R.string.prompt_alipay_not_found, 0).show();
                return;
            }
            AlipayZeroSdk.startAlipayClient(activity, "FKX016770URBZGZSR37U37");
        } else if (i == 1) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://paypal.me/virtualxposed"));
                activity.startActivity(intent);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        } else if (i == 2) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService("clipboard");
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "39Wst8oL74pRP2vKPkPihH6RFQF4hWoBqU"));
                }
                Toast.makeText(activity, activity.getResources().getString(R.string.donate_bitconins_tips), 0).show();
            } catch (Throwable th2) {
                th2.printStackTrace();
            }
        }
    }
}
