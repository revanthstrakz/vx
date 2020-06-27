package com.android.launcher3.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.LauncherSettings.WorkspaceScreens;
import java.util.ArrayList;
import java.util.Collection;

public class LauncherDbUtils {
    private static final String TAG = "LauncherDbUtils";

    public static class SQLiteTransaction implements AutoCloseable {
        private final SQLiteDatabase mDb;

        public SQLiteTransaction(SQLiteDatabase sQLiteDatabase) {
            this.mDb = sQLiteDatabase;
            sQLiteDatabase.beginTransaction();
        }

        public void commit() {
            this.mDb.setTransactionSuccessful();
        }

        public void close() {
            this.mDb.endTransaction();
        }
    }

    public static boolean prepareScreenZeroToHostQsb(Context context, SQLiteDatabase sQLiteDatabase) {
        SQLiteTransaction sQLiteTransaction;
        Throwable th;
        try {
            sQLiteTransaction = new SQLiteTransaction(sQLiteDatabase);
            ArrayList screenIdsFromCursor = getScreenIdsFromCursor(sQLiteDatabase.query(WorkspaceScreens.TABLE_NAME, null, null, null, null, null, WorkspaceScreens.SCREEN_RANK));
            if (screenIdsFromCursor.isEmpty()) {
                sQLiteTransaction.commit();
                sQLiteTransaction.close();
                return true;
            }
            if (((Long) screenIdsFromCursor.get(0)).longValue() != 0) {
                if (screenIdsFromCursor.indexOf(Long.valueOf(0)) > -1) {
                    long j = 1;
                    while (screenIdsFromCursor.indexOf(Long.valueOf(j)) > -1) {
                        j++;
                    }
                    renameScreen(sQLiteDatabase, 0, j);
                }
                renameScreen(sQLiteDatabase, ((Long) screenIdsFromCursor.get(0)).longValue(), 0);
            }
            if (DatabaseUtils.queryNumEntries(sQLiteDatabase, Favorites.TABLE_NAME, "container = -100 and screen = 0 and cellY = 0") == 0) {
                sQLiteTransaction.commit();
                sQLiteTransaction.close();
                return true;
            }
            new LossyScreenMigrationTask(context, LauncherAppState.getIDP(context), sQLiteDatabase).migrateScreen0();
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to update workspace size", e);
            return false;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    private static void renameScreen(SQLiteDatabase sQLiteDatabase, long j, long j2) {
        String[] strArr = {Long.toString(j)};
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", Long.valueOf(j2));
        sQLiteDatabase.update(WorkspaceScreens.TABLE_NAME, contentValues, "_id = ?", strArr);
        contentValues.clear();
        contentValues.put(Favorites.SCREEN, Long.valueOf(j2));
        sQLiteDatabase.update(Favorites.TABLE_NAME, contentValues, "container = -100 and screen = ?", strArr);
    }

    public static ArrayList<Long> getScreenIdsFromCursor(Cursor cursor) {
        try {
            return (ArrayList) iterateCursor(cursor, cursor.getColumnIndexOrThrow("_id"), new ArrayList());
        } finally {
            cursor.close();
        }
    }

    public static <T extends Collection<Long>> T iterateCursor(Cursor cursor, int i, T t) {
        while (cursor.moveToNext()) {
            t.add(Long.valueOf(cursor.getLong(i)));
        }
        return t;
    }
}
