package p013io.virtualapp.update;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import com.allenliu.versionchecklib.core.AVersionService;
import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionParams.Builder;
import io.va.exposed.R;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: io.virtualapp.update.VAVersionService */
public class VAVersionService extends AVersionService {
    private static final long CHECK_INTERVAL = TimeUnit.HOURS.toMillis(1);
    public static final String CHECK_VERION_URL = "http://vaexposed.weishu.me/update.json";
    private static final String KEY_SHOW_TIP = "show_tips";
    private static final String TAG = "VAVersionService";
    private static long sLastCheckTime;

    static {
        AllenChecker.init(false);
    }

    public void onResponses(AVersionService aVersionService, String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getString("url");
            int i = jSONObject.getInt("versionCode");
            String string2 = jSONObject.getString("updateMessage");
            if (getCurrentVersionCode(this) < i) {
                showVersionDialog(string, getResources().getString(R.string.new_version_detected), string2);
            } else {
                if ((this.versionParams == null || this.versionParams.getParamBundle() == null || !this.versionParams.getParamBundle().getBoolean(KEY_SHOW_TIP, false)) ? false : true) {
                    Toast.makeText(getApplicationContext(), R.string.version_is_latest, 0).show();
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "version info parse error!!", e);
        } catch (Throwable th) {
            stopSelf();
            throw th;
        }
        stopSelf();
    }

    public static void checkUpdateImmediately(Context context, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_SHOW_TIP, z);
        AllenChecker.startVersionCheck(context, new Builder().setRequestUrl(CHECK_VERION_URL).setShowDownloadingDialog(false).setParamBundle(bundle).setService(VAVersionService.class).build());
    }

    public static void checkUpdate(Context context, boolean z) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime - sLastCheckTime > CHECK_INTERVAL) {
            checkUpdateImmediately(context, z);
            sLastCheckTime = elapsedRealtime;
        }
    }

    private static int getCurrentVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
            return -1;
        }
    }
}
