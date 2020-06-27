package com.google.android.apps.nexuslauncher.smartspace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0947a;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0948b;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;

public class SmartspaceBroadcastReceiver extends BroadcastReceiver {
    /* renamed from: cg */
    private void m29cg(C0948b bVar, Context context, Intent intent, boolean z) throws NameNotFoundException {
        if (bVar.f151cy) {
            SmartspaceController.get(context).mo12981cV(null);
            return;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.google.android.googlequicksearchbox", 0);
            SmartspaceController smartspaceController = SmartspaceController.get(context);
            NewCardInfo newCardInfo = new NewCardInfo(bVar, intent, z, SystemClock.uptimeMillis(), packageInfo);
            smartspaceController.mo12981cV(newCardInfo);
        } catch (NameNotFoundException unused) {
        }
    }

    public void onReceive(Context context, Intent intent) {
        C0948b[] bVarArr;
        byte[] byteArrayExtra = intent.getByteArrayExtra("com.google.android.apps.nexuslauncher.extra.SMARTSPACE_CARD");
        if (byteArrayExtra != null) {
            C0947a aVar = new C0947a();
            try {
                MessageNano.mergeFrom(aVar, byteArrayExtra);
                for (C0948b bVar : aVar.f139cw) {
                    boolean z = true;
                    if (bVar.f152cz != 1) {
                        z = false;
                    }
                    if (!z) {
                        if (bVar.f152cz != 2) {
                            Log.w("SmartspaceReceiver", "unrecognized card priority");
                        }
                    }
                    m29cg(bVar, context, intent, z);
                }
            } catch (NameNotFoundException | InvalidProtocolBufferNanoException e) {
                Log.e("SmartspaceReceiver", "proto", e);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("receiving update with no proto: ");
            sb.append(intent.getExtras());
            Log.e("SmartspaceReceiver", sb.toString());
        }
    }
}
