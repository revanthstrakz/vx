package moe.feng.alipay.zerosdk;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build.VERSION;
import android.service.quicksettings.TileService;
import java.net.URISyntaxException;

public class AlipayZeroSdk {
    private static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
    private static final String INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end";

    public static boolean startAlipayClient(Activity activity, String str) {
        return startIntentUrl(activity, INTENT_URL_FORMAT.replace("{urlCode}", str));
    }

    public static boolean startIntentUrl(Activity activity, String str) {
        try {
            activity.startActivity(Intent.parseUri(str, 1));
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (ActivityNotFoundException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static boolean hasInstalledAlipayClient(Context context) {
        boolean z = false;
        try {
            if (context.getPackageManager().getPackageInfo(ALIPAY_PACKAGE_NAME, 0) != null) {
                z = true;
            }
            return z;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getAlipayClientVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(ALIPAY_PACKAGE_NAME, 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean openAlipayScan(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("alipayqr://platformapi/startapp?saId=10000007"));
            if (!(context instanceof TileService)) {
                context.startActivity(intent);
            } else if (VERSION.SDK_INT >= 24) {
                ((TileService) context).startActivityAndCollapse(intent);
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean openAlipayBarcode(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("alipayqr://platformapi/startapp?saId=20000056"));
            if (!(context instanceof TileService)) {
                context.startActivity(intent);
            } else if (VERSION.SDK_INT >= 24) {
                ((TileService) context).startActivityAndCollapse(intent);
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }
}
