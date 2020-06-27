package com.microsoft.appcenter.utils.storage;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;
import java.util.Map.Entry;

public class SQLiteUtils {
    @NonNull
    public static SQLiteQueryBuilder newSQLiteQueryBuilder() {
        return new SQLiteQueryBuilder();
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, String str, ContentValues contentValues) {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS `");
        sb.append(str);
        sb.append("` (oid INTEGER PRIMARY KEY AUTOINCREMENT");
        for (Entry entry : contentValues.valueSet()) {
            sb.append(", `");
            sb.append((String) entry.getKey());
            sb.append("` ");
            Object value = entry.getValue();
            if ((value instanceof Double) || (value instanceof Float)) {
                sb.append("REAL");
            } else if ((value instanceof Number) || (value instanceof Boolean)) {
                sb.append("INTEGER");
            } else if (value instanceof byte[]) {
                sb.append("BLOB");
            } else {
                sb.append("TEXT");
            }
        }
        sb.append(");");
        sQLiteDatabase.execSQL(sb.toString());
    }

    public static void dropTable(@NonNull SQLiteDatabase sQLiteDatabase, @NonNull String str) {
        sQLiteDatabase.execSQL(String.format("DROP TABLE `%s`", new Object[]{str}));
    }
}
