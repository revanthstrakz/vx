package com.android.launcher3.model;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.SparseArray;
import com.android.launcher3.provider.LauncherDbUtils.SQLiteTransaction;
import com.android.launcher3.util.IOUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DbDowngradeHelper {
    private static final String KEY_DOWNGRADE_TO = "downgrade_to_";
    private static final String KEY_VERSION = "version";
    private static final String TAG = "DbDowngradeHelper";
    private final SparseArray<String[]> mStatements = new SparseArray<>();
    public final int version;

    private DbDowngradeHelper(int i) {
        this.version = i;
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Throwable th;
        ArrayList arrayList = new ArrayList();
        int i3 = i - 1;
        while (i3 >= i2) {
            String[] strArr = (String[]) this.mStatements.get(i3);
            if (strArr != null) {
                Collections.addAll(arrayList, strArr);
                i3--;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Downgrade path not supported to version ");
                sb.append(i3);
                throw new SQLiteException(sb.toString());
            }
        }
        SQLiteTransaction sQLiteTransaction = new SQLiteTransaction(sQLiteDatabase);
        try {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                sQLiteDatabase.execSQL((String) it.next());
            }
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            return;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    public static DbDowngradeHelper parse(File file) throws JSONException, IOException {
        JSONObject jSONObject = new JSONObject(new String(IOUtils.toByteArray(file)));
        DbDowngradeHelper dbDowngradeHelper = new DbDowngradeHelper(jSONObject.getInt(KEY_VERSION));
        for (int i = dbDowngradeHelper.version - 1; i > 0; i--) {
            StringBuilder sb = new StringBuilder();
            sb.append(KEY_DOWNGRADE_TO);
            sb.append(i);
            if (jSONObject.has(sb.toString())) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(KEY_DOWNGRADE_TO);
                sb2.append(i);
                JSONArray jSONArray = jSONObject.getJSONArray(sb2.toString());
                String[] strArr = new String[jSONArray.length()];
                for (int i2 = 0; i2 < strArr.length; i2++) {
                    strArr[i2] = jSONArray.getString(i2);
                }
                dbDowngradeHelper.mStatements.put(i, strArr);
            }
        }
        return dbDowngradeHelper;
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0009 */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x001c A[SYNTHETIC, Splitter:B:12:0x001c] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void updateSchemaFile(java.io.File r2, int r3, android.content.Context r4, int r5) {
        /*
            com.android.launcher3.model.DbDowngradeHelper r0 = parse(r2)     // Catch:{ Exception -> 0x0009 }
            int r0 = r0.version     // Catch:{ Exception -> 0x0009 }
            if (r0 < r3) goto L_0x0009
            return
        L_0x0009:
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0050 }
            r3.<init>(r2)     // Catch:{ IOException -> 0x0050 }
            r2 = 0
            android.content.res.Resources r4 = r4.getResources()     // Catch:{ Throwable -> 0x003f }
            java.io.InputStream r4 = r4.openRawResource(r5)     // Catch:{ Throwable -> 0x003f }
            com.android.launcher3.util.IOUtils.copy(r4, r3)     // Catch:{ Throwable -> 0x0026, all -> 0x0023 }
            if (r4 == 0) goto L_0x001f
            r4.close()     // Catch:{ Throwable -> 0x003f }
        L_0x001f:
            r3.close()     // Catch:{ IOException -> 0x0050 }
            goto L_0x0058
        L_0x0023:
            r5 = move-exception
            r0 = r2
            goto L_0x002c
        L_0x0026:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x0028 }
        L_0x0028:
            r0 = move-exception
            r1 = r0
            r0 = r5
            r5 = r1
        L_0x002c:
            if (r4 == 0) goto L_0x003c
            if (r0 == 0) goto L_0x0039
            r4.close()     // Catch:{ Throwable -> 0x0034 }
            goto L_0x003c
        L_0x0034:
            r4 = move-exception
            r0.addSuppressed(r4)     // Catch:{ Throwable -> 0x003f }
            goto L_0x003c
        L_0x0039:
            r4.close()     // Catch:{ Throwable -> 0x003f }
        L_0x003c:
            throw r5     // Catch:{ Throwable -> 0x003f }
        L_0x003d:
            r4 = move-exception
            goto L_0x0041
        L_0x003f:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003d }
        L_0x0041:
            if (r2 == 0) goto L_0x004c
            r3.close()     // Catch:{ Throwable -> 0x0047 }
            goto L_0x004f
        L_0x0047:
            r3 = move-exception
            r2.addSuppressed(r3)     // Catch:{ IOException -> 0x0050 }
            goto L_0x004f
        L_0x004c:
            r3.close()     // Catch:{ IOException -> 0x0050 }
        L_0x004f:
            throw r4     // Catch:{ IOException -> 0x0050 }
        L_0x0050:
            r2 = move-exception
            java.lang.String r3 = "DbDowngradeHelper"
            java.lang.String r4 = "Error writing schema file"
            android.util.Log.e(r3, r4, r2)
        L_0x0058:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.DbDowngradeHelper.updateSchemaFile(java.io.File, int, android.content.Context, int):void");
    }
}
