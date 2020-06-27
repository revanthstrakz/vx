package com.allenliu.versionchecklib.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.p001v4.app.ActivityCompat;
import android.support.p001v4.content.ContextCompat;
import android.support.p004v7.app.AppCompatActivity;
import android.widget.Toast;
import com.allenliu.versionchecklib.C0494R;

public class PermissionDialogActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            sendBroadcast(true);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, VersionDialogActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, VersionDialogActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void sendBroadcast(boolean z) {
        Intent intent = new Intent();
        intent.setAction(AVersionService.PERMISSION_ACTION);
        intent.putExtra("result", z);
        sendBroadcast(intent);
        finish();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 291) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                Toast.makeText(this, getString(C0494R.string.versionchecklib_write_permission_deny), 1).show();
                sendBroadcast(false);
            } else {
                sendBroadcast(true);
            }
        }
    }
}
