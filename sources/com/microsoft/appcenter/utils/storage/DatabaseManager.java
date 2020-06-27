package com.microsoft.appcenter.utils.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.io.Closeable;
import java.util.Arrays;

public class DatabaseManager implements Closeable {
    public static final String PRIMARY_KEY = "oid";
    public static final String[] SELECT_PRIMARY_KEY = {PRIMARY_KEY};
    private final Context mContext;
    private final String mDatabase;
    /* access modifiers changed from: private */
    public final String mDefaultTable;
    /* access modifiers changed from: private */
    public final Listener mListener;
    private SQLiteOpenHelper mSQLiteOpenHelper;
    /* access modifiers changed from: private */
    public final ContentValues mSchema;

    public interface Listener {
        void onCreate(SQLiteDatabase sQLiteDatabase);

        void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2);
    }

    public DatabaseManager(Context context, String str, String str2, int i, ContentValues contentValues, @NonNull Listener listener) {
        this.mContext = context;
        this.mDatabase = str;
        this.mDefaultTable = str2;
        this.mSchema = contentValues;
        this.mListener = listener;
        C12291 r0 = new SQLiteOpenHelper(context, str, null, i) {
            public void onCreate(SQLiteDatabase sQLiteDatabase) {
                SQLiteUtils.createTable(sQLiteDatabase, DatabaseManager.this.mDefaultTable, DatabaseManager.this.mSchema);
                DatabaseManager.this.mListener.onCreate(sQLiteDatabase);
            }

            public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
                DatabaseManager.this.mListener.onUpgrade(sQLiteDatabase, i, i2);
            }
        };
        this.mSQLiteOpenHelper = r0;
    }

    private static ContentValues buildValues(Cursor cursor, ContentValues contentValues) {
        ContentValues contentValues2 = new ContentValues();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            if (!cursor.isNull(i)) {
                String columnName = cursor.getColumnName(i);
                if (columnName.equals(PRIMARY_KEY)) {
                    contentValues2.put(columnName, Long.valueOf(cursor.getLong(i)));
                } else {
                    Object obj = contentValues.get(columnName);
                    if (obj instanceof byte[]) {
                        contentValues2.put(columnName, cursor.getBlob(i));
                    } else if (obj instanceof Double) {
                        contentValues2.put(columnName, Double.valueOf(cursor.getDouble(i)));
                    } else if (obj instanceof Float) {
                        contentValues2.put(columnName, Float.valueOf(cursor.getFloat(i)));
                    } else if (obj instanceof Integer) {
                        contentValues2.put(columnName, Integer.valueOf(cursor.getInt(i)));
                    } else if (obj instanceof Long) {
                        contentValues2.put(columnName, Long.valueOf(cursor.getLong(i)));
                    } else if (obj instanceof Short) {
                        contentValues2.put(columnName, Short.valueOf(cursor.getShort(i)));
                    } else if (obj instanceof Boolean) {
                        boolean z = true;
                        if (cursor.getInt(i) != 1) {
                            z = false;
                        }
                        contentValues2.put(columnName, Boolean.valueOf(z));
                    } else {
                        contentValues2.put(columnName, cursor.getString(i));
                    }
                }
            }
        }
        return contentValues2;
    }

    public ContentValues buildValues(Cursor cursor) {
        return buildValues(cursor, this.mSchema);
    }

    @Nullable
    public ContentValues nextValues(Cursor cursor) {
        try {
            if (cursor.moveToNext()) {
                return buildValues(cursor);
            }
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get next cursor value: ", e);
        }
        return null;
    }

    public long put(@NonNull ContentValues contentValues, @NonNull String str) {
        Long l = null;
        Cursor cursor = null;
        while (l == null) {
            try {
                l = Long.valueOf(getDatabase().insertOrThrow(this.mDefaultTable, null, contentValues));
            } catch (SQLiteFullException e) {
                AppCenterLog.debug("AppCenter", "Storage is full, trying to delete the oldest log that has the lowest priority which is lower or equal priority than the new log");
                if (cursor == null) {
                    String asString = contentValues.getAsString(str);
                    SQLiteQueryBuilder newSQLiteQueryBuilder = SQLiteUtils.newSQLiteQueryBuilder();
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(" <= ?");
                    newSQLiteQueryBuilder.appendWhere(sb.toString());
                    String[] strArr = SELECT_PRIMARY_KEY;
                    String[] strArr2 = {asString};
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(" , ");
                    sb2.append(PRIMARY_KEY);
                    cursor = getCursor(newSQLiteQueryBuilder, strArr, strArr2, sb2.toString());
                }
                if (cursor.moveToNext()) {
                    long j = cursor.getLong(0);
                    delete(j);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Deleted log id=");
                    sb3.append(j);
                    AppCenterLog.debug("AppCenter", sb3.toString());
                } else {
                    throw e;
                }
            } catch (RuntimeException e2) {
                l = Long.valueOf(-1);
                AppCenterLog.error("AppCenter", String.format("Failed to insert values (%s) to database %s.", new Object[]{contentValues.toString(), this.mDatabase}), e2);
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (RuntimeException unused) {
            }
        }
        return l.longValue();
    }

    public void delete(@IntRange(from = 0) long j) {
        delete(this.mDefaultTable, PRIMARY_KEY, Long.valueOf(j));
    }

    public int delete(@NonNull String str, @Nullable Object obj) {
        return delete(this.mDefaultTable, str, obj);
    }

    private int delete(@NonNull String str, @NonNull String str2, @Nullable Object obj) {
        String[] strArr = {String.valueOf(obj)};
        try {
            SQLiteDatabase database = getDatabase();
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(" = ?");
            return database.delete(str, sb.toString(), strArr);
        } catch (RuntimeException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append(" = ?");
            AppCenterLog.error("AppCenter", String.format("Failed to delete values that match condition=\"%s\" and values=\"%s\" from database %s.", new Object[]{sb2.toString(), Arrays.toString(strArr), this.mDatabase}), e);
            return 0;
        }
    }

    public void clear() {
        try {
            getDatabase().delete(this.mDefaultTable, null, null);
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to clear the table.", e);
        }
    }

    public void close() {
        try {
            this.mSQLiteOpenHelper.close();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to close the database.", e);
        }
    }

    public final long getRowCount() {
        try {
            return DatabaseUtils.queryNumEntries(getDatabase(), this.mDefaultTable);
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Failed to get row count of database.", e);
            return -1;
        }
    }

    public Cursor getCursor(@Nullable SQLiteQueryBuilder sQLiteQueryBuilder, String[] strArr, @Nullable String[] strArr2, @Nullable String str) throws RuntimeException {
        return getCursor(this.mDefaultTable, sQLiteQueryBuilder, strArr, strArr2, str);
    }

    /* access modifiers changed from: 0000 */
    public Cursor getCursor(@NonNull String str, @Nullable SQLiteQueryBuilder sQLiteQueryBuilder, String[] strArr, @Nullable String[] strArr2, @Nullable String str2) throws RuntimeException {
        if (sQLiteQueryBuilder == null) {
            sQLiteQueryBuilder = SQLiteUtils.newSQLiteQueryBuilder();
        }
        SQLiteQueryBuilder sQLiteQueryBuilder2 = sQLiteQueryBuilder;
        sQLiteQueryBuilder2.setTables(str);
        return sQLiteQueryBuilder2.query(getDatabase(), strArr, null, strArr2, null, null, str2);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public SQLiteDatabase getDatabase() {
        try {
            return this.mSQLiteOpenHelper.getWritableDatabase();
        } catch (RuntimeException e) {
            AppCenterLog.warn("AppCenter", "Failed to open database. Trying to delete database (may be corrupted).", e);
            if (this.mContext.deleteDatabase(this.mDatabase)) {
                AppCenterLog.info("AppCenter", "The database was successfully deleted.");
            } else {
                AppCenterLog.warn("AppCenter", "Failed to delete database.");
            }
            return this.mSQLiteOpenHelper.getWritableDatabase();
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setSQLiteOpenHelper(@NonNull SQLiteOpenHelper sQLiteOpenHelper) {
        this.mSQLiteOpenHelper.close();
        this.mSQLiteOpenHelper = sQLiteOpenHelper;
    }

    public boolean setMaxSize(long j) {
        try {
            SQLiteDatabase database = getDatabase();
            long maximumSize = database.setMaximumSize(j);
            long pageSize = database.getPageSize();
            long j2 = j / pageSize;
            if (j % pageSize != 0) {
                j2++;
            }
            if (maximumSize != j2 * pageSize) {
                StringBuilder sb = new StringBuilder();
                sb.append("Could not change maximum database size to ");
                sb.append(j);
                sb.append(" bytes, current maximum size is ");
                sb.append(maximumSize);
                sb.append(" bytes.");
                AppCenterLog.error("AppCenter", sb.toString());
                return false;
            }
            if (j == maximumSize) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Changed maximum database size to ");
                sb2.append(maximumSize);
                sb2.append(" bytes.");
                AppCenterLog.info("AppCenter", sb2.toString());
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Changed maximum database size to ");
                sb3.append(maximumSize);
                sb3.append(" bytes (next multiple of page size).");
                AppCenterLog.info("AppCenter", sb3.toString());
            }
            return true;
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Could not change maximum database size.", e);
            return false;
        }
    }

    public long getMaxSize() {
        try {
            return getDatabase().getMaximumSize();
        } catch (RuntimeException e) {
            AppCenterLog.error("AppCenter", "Could not get maximum database size.", e);
            return -1;
        }
    }
}
