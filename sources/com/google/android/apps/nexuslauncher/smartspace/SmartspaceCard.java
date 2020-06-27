package com.google.android.apps.nexuslauncher.smartspace;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.ShadowGenerator;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0948b;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0949c;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0950d;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0951e;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.C0955i;
import com.google.android.apps.nexuslauncher.utils.ColorManipulation;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;
import java.net.URISyntaxException;

public class SmartspaceCard {

    /* renamed from: dI */
    private final C0948b f119dI;

    /* renamed from: dJ */
    private final long f120dJ;

    /* renamed from: dK */
    private final int f121dK;

    /* renamed from: dL */
    private final boolean f122dL;

    /* renamed from: dM */
    private final boolean f123dM;

    /* renamed from: dN */
    private final long f124dN;
    private final Context mContext;
    private Bitmap mIcon;
    private final Intent mIntent;

    public SmartspaceCard(Context context, C0948b bVar, Intent intent, boolean z, Bitmap bitmap, boolean z2, long j, long j2, int i) {
        this.mContext = context.getApplicationContext();
        this.f119dI = bVar;
        this.f123dM = z;
        this.mIntent = intent;
        this.mIcon = bitmap;
        this.f124dN = j;
        this.f120dJ = j2;
        this.f121dK = i;
        this.f122dL = z2;
    }

    /* renamed from: cD */
    static SmartspaceCard m30cD(Context context, C0955i iVar, boolean z) {
        Intent intent;
        Bitmap bitmap;
        C0955i iVar2 = iVar;
        if (iVar2 != null) {
            try {
                if (TextUtils.isEmpty(iVar2.f171de.f146cG.f166cZ)) {
                    intent = null;
                } else {
                    intent = Intent.parseUri(iVar2.f171de.f146cG.f166cZ, 0);
                }
                if (iVar2.f170dd == null) {
                    bitmap = null;
                } else {
                    bitmap = BitmapFactory.decodeByteArray(iVar2.f170dd, 0, iVar2.f170dd.length, null);
                }
                if (bitmap != null) {
                    bitmap = ShadowGenerator.getInstance(context).recreateIcon(bitmap, false, new BlurMaskFilter((float) Utilities.pxFromDp(3.0f, context.getResources().getDisplayMetrics()), Blur.NORMAL), 20, 55);
                }
                SmartspaceCard smartspaceCard = new SmartspaceCard(context, iVar2.f171de, intent, z, bitmap, iVar2.f169dc, iVar2.f172df, iVar2.f174dh, iVar2.f173dg);
                return smartspaceCard;
            } catch (Throwable th) {
                Log.e("SmartspaceCard", "from proto", th);
            }
        }
        return null;
    }

    /* renamed from: cE */
    private String m31cE(C0951e eVar) {
        Resources resources = this.mContext.getResources();
        int cJ = m34cJ(eVar);
        if (cJ >= 60) {
            int i = cJ / 60;
            int i2 = cJ % 60;
            String quantityString = resources.getQuantityString(C0622R.plurals.smartspace_hours, i, new Object[]{Integer.valueOf(i)});
            if (i2 <= 0) {
                return quantityString;
            }
            String quantityString2 = resources.getQuantityString(C0622R.plurals.smartspace_minutes, i2, new Object[]{Integer.valueOf(i2)});
            return resources.getString(C0622R.string.smartspace_hours_mins, new Object[]{quantityString, quantityString2});
        }
        return resources.getQuantityString(C0622R.plurals.smartspace_minutes, cJ, new Object[]{Integer.valueOf(cJ)});
    }

    /* renamed from: cG */
    private C0950d m32cG(boolean z) {
        C0950d dVar;
        C0949c cH = m33cH();
        if (cH == null) {
            return null;
        }
        if (z) {
            dVar = cH.f153cL;
        } else {
            dVar = cH.f154cM;
        }
        return dVar;
    }

    /* renamed from: cH */
    private C0949c m33cH() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.f119dI.f143cD + this.f119dI.f144cE;
        if (currentTimeMillis < this.f119dI.f143cD && this.f119dI.f141cB != null) {
            return this.f119dI.f141cB;
        }
        if (currentTimeMillis > j && this.f119dI.f147cH != null) {
            return this.f119dI.f147cH;
        }
        if (this.f119dI.f142cC != null) {
            return this.f119dI.f142cC;
        }
        return null;
    }

    /* renamed from: cJ */
    private int m34cJ(C0951e eVar) {
        return (int) Math.ceil(((double) mo12969cI(eVar)) / 60000.0d);
    }

    /* renamed from: cK */
    private String[] m35cK(C0951e[] eVarArr, String str) {
        String[] strArr = new String[eVarArr.length];
        for (int i = 0; i < strArr.length; i++) {
            switch (eVarArr[i].f158cQ) {
                case 1:
                case 2:
                    strArr[i] = m31cE(eVarArr[i]);
                    break;
                case 3:
                    if (str != null && eVarArr[i].f160cS != 0) {
                        strArr[i] = str;
                        break;
                    } else {
                        strArr[i] = eVarArr[i].f159cR != null ? eVarArr[i].f159cR : "";
                        break;
                    }
                    break;
                default:
                    strArr[i] = "";
                    break;
            }
        }
        return strArr;
    }

    /* renamed from: cL */
    private boolean m36cL(C0950d dVar) {
        return (dVar == null || dVar.f155cN == null || dVar.f156cO == null || dVar.f156cO.length <= 0) ? false : true;
    }

    /* renamed from: cN */
    private String m37cN(boolean z) {
        return m38cO(z, null);
    }

    /* renamed from: cO */
    private String m38cO(boolean z, String str) {
        C0950d cG = m32cG(z);
        if (cG == null || cG.f155cN == null) {
            return "";
        }
        String str2 = cG.f155cN;
        if (m36cL(cG)) {
            return String.format(str2, (Object[]) m35cK(cG.f156cO, str));
        }
        if (str2 == null) {
            str2 = "";
        }
        return str2;
    }

    /* renamed from: cP */
    private static Bitmap m39cP(Bitmap bitmap, int i) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(i, Mode.SRC_IN));
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        new Canvas(createBitmap).drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }

    /* renamed from: cQ */
    static C0955i m40cQ(Context context, NewCardInfo newCardInfo) {
        byte[] bArr;
        if (newCardInfo == null) {
            return null;
        }
        C0955i iVar = new C0955i();
        Bitmap bitmap = newCardInfo.getBitmap(context);
        if (bitmap != null && iVar.f169dc && newCardInfo.f116dj) {
            bitmap = m39cP(bitmap, -1);
        }
        boolean z = false;
        if (bitmap != null) {
            bArr = Utilities.flattenBitmap(bitmap);
        } else {
            bArr = new byte[0];
        }
        iVar.f170dd = bArr;
        if (bitmap != null && new ColorManipulation().mo13019dB(bitmap)) {
            z = true;
        }
        iVar.f169dc = z;
        iVar.f171de = newCardInfo.f115di;
        iVar.f172df = newCardInfo.f118dl;
        if (newCardInfo.f117dk != null) {
            iVar.f173dg = newCardInfo.f117dk.versionCode;
            iVar.f174dh = newCardInfo.f117dk.lastUpdateTime;
        }
        return iVar;
    }

    /* renamed from: cA */
    public String mo12965cA(boolean z) {
        return m38cO(z, "");
    }

    /* renamed from: cB */
    public String mo12966cB(boolean z) {
        C0951e[] eVarArr = m32cG(z).f156cO;
        if (eVarArr != null) {
            for (int i = 0; i < eVarArr.length; i++) {
                if (eVarArr[i].f160cS != 0) {
                    return eVarArr[i].f159cR;
                }
            }
        }
        return "";
    }

    /* renamed from: cC */
    public String mo12967cC(String str) {
        return m38cO(true, str);
    }

    /* renamed from: cF */
    public long mo12968cF() {
        return this.f119dI.f145cF.f167da;
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: cI */
    public long mo12969cI(C0951e eVar) {
        long j;
        if (eVar.f158cQ == 2) {
            j = this.f119dI.f143cD + this.f119dI.f144cE;
        } else {
            j = this.f119dI.f143cD;
        }
        return Math.abs(System.currentTimeMillis() - j);
    }

    /* renamed from: cM */
    public boolean mo12970cM() {
        return System.currentTimeMillis() > mo12968cF();
    }

    /* access modifiers changed from: 0000 */
    public void click(View view) {
        if (this.f119dI.f146cG == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("no tap action available: ");
            sb.append(this);
            Log.e("SmartspaceCard", sb.toString());
            return;
        }
        Intent intent = new Intent(getIntent());
        Launcher launcher = Launcher.getLauncher(view.getContext());
        switch (this.f119dI.f146cG.f165cY) {
            case 1:
                if (!Utilities.ATLEAST_NOUGAT) {
                    try {
                        launcher.startActivity(Intent.parseUri(intent.getExtras().getString("com.google.android.apps.gsa.smartspace.extra.SMARTSPACE_INTENT"), 1));
                        return;
                    } catch (NullPointerException | SecurityException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                intent.addFlags(268435456);
                intent.setSourceBounds(launcher.getViewBounds(view));
                intent.setPackage(LauncherClient.BRIDGE_PACKAGE);
                view.getContext().sendBroadcast(intent);
                break;
            case 2:
                launcher.startActivitySafely(view, intent, null);
                break;
            default:
                StringBuilder sb2 = new StringBuilder();
                sb2.append("unrecognized tap action: ");
                sb2.append(this);
                Log.w("SmartspaceCard", sb2.toString());
                break;
        }
    }

    /* renamed from: cv */
    public boolean mo12972cv() {
        C0949c cH = m33cH();
        return cH != null && (m36cL(cH.f153cL) || m36cL(cH.f154cM));
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: cw */
    public long mo12973cw() {
        C0949c cH = m33cH();
        if (cH != null && m36cL(cH.f153cL)) {
            C0951e[] eVarArr = cH.f153cL.f156cO;
            for (C0951e eVar : eVarArr) {
                if (eVar.f158cQ == 1 || eVar.f158cQ == 2) {
                    return mo12969cI(eVar);
                }
            }
        }
        return 0;
    }

    /* renamed from: cx */
    public TruncateAt mo12974cx(boolean z) {
        C0949c cH = m33cH();
        if (cH != null) {
            int i = 0;
            if (z && cH.f153cL != null) {
                i = cH.f153cL.f157cP;
            } else if (!z && cH.f154cM != null) {
                i = cH.f154cM.f157cP;
            }
            switch (i) {
                case 1:
                    return TruncateAt.START;
                case 2:
                    return TruncateAt.MIDDLE;
            }
        }
        return TruncateAt.END;
    }

    /* renamed from: cy */
    public String mo12975cy() {
        return m37cN(false);
    }

    /* renamed from: cz */
    public boolean mo12976cz() {
        return this.f122dL;
    }

    public Bitmap getIcon() {
        return this.mIcon;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public String getTitle() {
        return m37cN(true);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("title:");
        sb.append(getTitle());
        sb.append(" expires:");
        sb.append(mo12968cF());
        sb.append(" published:");
        sb.append(this.f124dN);
        sb.append(" gsaVersion:");
        sb.append(this.f121dK);
        sb.append(" gsaUpdateTime: ");
        sb.append(this.f120dJ);
        return sb.toString();
    }
}
