package com.lody.virtual.client.stub;

import android.app.Activity;

public class ShortcutHandleActivity extends Activity {
    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x002f A[SYNTHETIC, Splitter:B:11:0x002f] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x003b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x003c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r8) {
        /*
            r7 = this;
            super.onCreate(r8)
            r7.finish()
            android.content.Intent r8 = r7.getIntent()
            if (r8 != 0) goto L_0x000d
            return
        L_0x000d:
            java.lang.String r0 = "_VA_|_user_id_"
            r1 = 0
            int r0 = r8.getIntExtra(r0, r1)
            java.lang.String r2 = "_VA_|_splash_"
            java.lang.String r2 = r8.getStringExtra(r2)
            java.lang.String r3 = "_VA_|_uri_"
            java.lang.String r3 = r8.getStringExtra(r3)
            r4 = 0
            if (r2 == 0) goto L_0x002c
            android.content.Intent r2 = android.content.Intent.parseUri(r2, r1)     // Catch:{ URISyntaxException -> 0x0028 }
            goto L_0x002d
        L_0x0028:
            r2 = move-exception
            r2.printStackTrace()
        L_0x002c:
            r2 = r4
        L_0x002d:
            if (r3 == 0) goto L_0x0038
            android.content.Intent r1 = android.content.Intent.parseUri(r3, r1)     // Catch:{ URISyntaxException -> 0x0034 }
            goto L_0x0039
        L_0x0034:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0038:
            r1 = r4
        L_0x0039:
            if (r1 != 0) goto L_0x003c
            return
        L_0x003c:
            android.os.Bundle r8 = r8.getExtras()
            if (r8 == 0) goto L_0x006a
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>(r8)
            java.util.Set r8 = r8.keySet()
            java.util.Iterator r8 = r8.iterator()
        L_0x004f:
            boolean r5 = r8.hasNext()
            if (r5 == 0) goto L_0x0067
            java.lang.Object r5 = r8.next()
            java.lang.String r5 = (java.lang.String) r5
            java.lang.String r6 = "_VA_"
            boolean r6 = r5.startsWith(r6)
            if (r6 == 0) goto L_0x004f
            r3.remove(r5)
            goto L_0x004f
        L_0x0067:
            r1.putExtras(r3)
        L_0x006a:
            int r8 = android.os.Build.VERSION.SDK_INT
            r3 = 15
            if (r8 < r3) goto L_0x0073
            r1.setSelector(r4)
        L_0x0073:
            if (r2 != 0) goto L_0x0082
            com.lody.virtual.client.ipc.VActivityManager r8 = com.lody.virtual.client.ipc.VActivityManager.get()     // Catch:{ Throwable -> 0x007d }
            r8.startActivity(r1, r0)     // Catch:{ Throwable -> 0x007d }
            goto L_0x00a4
        L_0x007d:
            r8 = move-exception
            r8.printStackTrace()
            goto L_0x00a4
        L_0x0082:
            java.lang.String r8 = "KEY_INTENT"
            r2.putExtra(r8, r1)
            java.lang.String r8 = "KEY_USER"
            r2.putExtra(r8, r0)
            java.lang.String r8 = r1.getPackage()
            if (r8 != 0) goto L_0x009c
            android.content.ComponentName r0 = r1.getComponent()
            if (r0 == 0) goto L_0x009c
            java.lang.String r8 = r0.getPackageName()
        L_0x009c:
            java.lang.String r0 = "MODEL_ARGUMENT"
            r2.putExtra(r0, r8)
            r7.startActivity(r2)
        L_0x00a4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.stub.ShortcutHandleActivity.onCreate(android.os.Bundle):void");
    }
}
