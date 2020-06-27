package com.google.android.apps.nexuslauncher.smartspace;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.graphics.LauncherIcons;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0948b;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0952f;

public class NewCardInfo {

    /* renamed from: di */
    public final C0948b f115di;

    /* renamed from: dj */
    public final boolean f116dj;

    /* renamed from: dk */
    public final PackageInfo f117dk;

    /* renamed from: dl */
    public final long f118dl;
    public final Intent intent;

    public NewCardInfo(C0948b bVar, Intent intent2, boolean z, long j, PackageInfo packageInfo) {
        this.f115di = bVar;
        this.f116dj = z;
        this.intent = intent2;
        this.f118dl = j;
        this.f117dk = packageInfo;
    }

    private static Object getParcelableExtra(String str, Intent intent2) {
        if (!TextUtils.isEmpty(str)) {
            return intent2.getParcelableExtra(str);
        }
        return null;
    }

    public Bitmap getBitmap(Context context) {
        C0952f fVar = this.f115di.f150cx;
        if (fVar == null) {
            return null;
        }
        Bitmap bitmap = (Bitmap) getParcelableExtra(fVar.f162cV, this.intent);
        if (bitmap != null) {
            return bitmap;
        }
        try {
            if (!TextUtils.isEmpty(fVar.f163cW)) {
                return Media.getBitmap(context.getContentResolver(), Uri.parse(fVar.f163cW));
            }
            if (TextUtils.isEmpty(fVar.f164cX)) {
                return null;
            }
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication("com.google.android.googlequicksearchbox");
            return LauncherIcons.createIconBitmap(resourcesForApplication.getDrawableForDensity(resourcesForApplication.getIdentifier(fVar.f164cX, null, null), LauncherAppState.getIDP(context).fillResIconDpi), context);
        } catch (Exception unused) {
            StringBuilder sb = new StringBuilder();
            sb.append("retrieving bitmap uri=");
            sb.append(fVar.f163cW);
            sb.append(" gsaRes=");
            sb.append(fVar.f164cX);
            Log.e("NewCardInfo", sb.toString());
            return null;
        }
    }
}
