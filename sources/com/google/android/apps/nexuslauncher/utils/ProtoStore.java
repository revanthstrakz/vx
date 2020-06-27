package com.google.android.apps.nexuslauncher.utils;

import android.content.Context;
import android.util.Log;
import com.android.launcher3.util.IOUtils;
import com.google.protobuf.nano.MessageNano;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ProtoStore {
    private final Context mContext;

    public ProtoStore(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /* renamed from: dw */
    public void mo13021dw(MessageNano messageNano, String str) {
        try {
            FileOutputStream openFileOutput = this.mContext.openFileOutput(str, 0);
            if (messageNano != null) {
                openFileOutput.write(MessageNano.toByteArray(messageNano));
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("deleting ");
            sb.append(str);
            Log.d("ProtoStore", sb.toString());
            this.mContext.deleteFile(str);
        } catch (FileNotFoundException unused) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("file does not exist ");
            sb2.append(str);
            Log.d("ProtoStore", sb2.toString());
        } catch (Exception e) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("unable to write file ");
            sb3.append(str);
            Log.e("ProtoStore", sb3.toString(), e);
        }
    }

    /* renamed from: dv */
    public boolean mo13020dv(String str, MessageNano messageNano) {
        try {
            MessageNano.mergeFrom(messageNano, IOUtils.toByteArray(this.mContext.getFileStreamPath(str)));
            return true;
        } catch (FileNotFoundException unused) {
            Log.d("ProtoStore", "no cached data");
            return false;
        } catch (Exception e) {
            Log.e("ProtoStore", "unable to load data", e);
            return false;
        }
    }
}
