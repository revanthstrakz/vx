package com.android.launcher3.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.android.launcher3.LauncherProvider.DatabaseHelper;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.provider.LauncherDbUtils.SQLiteTransaction;
import com.android.launcher3.util.LogConfig;
import java.io.InvalidObjectException;

public class RestoreDbTask {
    private static final String INFO_COLUMN_DEFAULT_VALUE = "dflt_value";
    private static final String INFO_COLUMN_NAME = "name";
    private static final String RESTORE_TASK_PENDING = "restore_task_pending";
    private static final String TAG = "RestoreDbTask";

    public static boolean performRestore(DatabaseHelper databaseHelper) {
        SQLiteTransaction sQLiteTransaction;
        Throwable th;
        SQLiteDatabase writableDatabase = databaseHelper.getWritableDatabase();
        try {
            sQLiteTransaction = new SQLiteTransaction(writableDatabase);
            new RestoreDbTask().sanitizeDB(databaseHelper, writableDatabase);
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            return true;
        } catch (Exception e) {
            FileLog.m14e(TAG, "Failed to verify db", e);
            return false;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    private void sanitizeDB(DatabaseHelper databaseHelper, SQLiteDatabase sQLiteDatabase) throws Exception {
        long defaultProfileId = getDefaultProfileId(sQLiteDatabase);
        int delete = sQLiteDatabase.delete(Favorites.TABLE_NAME, "profileId != ?", new String[]{Long.toString(defaultProfileId)});
        if (delete > 0) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(delete);
            sb.append(" items belonging to a managed profile, were deleted");
            FileLog.m11d(str, sb.toString());
        }
        boolean isPropertyEnabled = Utilities.isPropertyEnabled(LogConfig.KEEP_ALL_ICONS);
        ContentValues contentValues = new ContentValues();
        int i = 8;
        contentValues.put(Favorites.RESTORED, Integer.valueOf((isPropertyEnabled ? 8 : 0) | 1));
        sQLiteDatabase.update(Favorites.TABLE_NAME, contentValues, null, null);
        String str2 = Favorites.RESTORED;
        if (!isPropertyEnabled) {
            i = 0;
        }
        contentValues.put(str2, Integer.valueOf(i | 7));
        sQLiteDatabase.update(Favorites.TABLE_NAME, contentValues, "itemType = ?", new String[]{Integer.toString(4)});
        long defaultUserSerial = databaseHelper.getDefaultUserSerial();
        if (Utilities.longCompare(defaultProfileId, defaultUserSerial) != 0) {
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Changing primary user id from ");
            sb2.append(defaultProfileId);
            sb2.append(" to ");
            sb2.append(defaultUserSerial);
            FileLog.m11d(str3, sb2.toString());
            migrateProfileId(sQLiteDatabase, defaultUserSerial);
        }
    }

    /* access modifiers changed from: protected */
    public void migrateProfileId(SQLiteDatabase sQLiteDatabase, long j) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Favorites.PROFILE_ID, Long.valueOf(j));
        sQLiteDatabase.update(Favorites.TABLE_NAME, contentValues, null, null);
        sQLiteDatabase.execSQL("ALTER TABLE favorites RENAME TO favorites_old;");
        Favorites.addTableToDb(sQLiteDatabase, j, false);
        sQLiteDatabase.execSQL("INSERT INTO favorites SELECT * FROM favorites_old;");
        sQLiteDatabase.execSQL("DROP TABLE favorites_old;");
    }

    /* access modifiers changed from: protected */
    public long getDefaultProfileId(SQLiteDatabase sQLiteDatabase) throws Exception {
        Throwable th;
        Cursor rawQuery = sQLiteDatabase.rawQuery("PRAGMA table_info (favorites)", null);
        try {
            int columnIndex = rawQuery.getColumnIndex("name");
            while (rawQuery.moveToNext()) {
                if (Favorites.PROFILE_ID.equals(rawQuery.getString(columnIndex))) {
                    long j = rawQuery.getLong(rawQuery.getColumnIndex(INFO_COLUMN_DEFAULT_VALUE));
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    return j;
                }
            }
            throw new InvalidObjectException("Table does not have a profile id column");
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    public static boolean isPending(Context context) {
        return Utilities.getPrefs(context).getBoolean(RESTORE_TASK_PENDING, false);
    }

    public static void setPending(Context context, boolean z) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Restore data received through full backup ");
        sb.append(z);
        FileLog.m11d(str, sb.toString());
        Utilities.getPrefs(context).edit().putBoolean(RESTORE_TASK_PENDING, z).commit();
    }
}
