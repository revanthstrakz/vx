package com.android.launcher3.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.GridSizeMigrationTask;
import com.android.launcher3.util.LongArrayMap;
import java.util.ArrayList;
import java.util.Iterator;

public class LossyScreenMigrationTask extends GridSizeMigrationTask {
    private final SQLiteDatabase mDb;
    private final LongArrayMap<DbEntry> mOriginalItems = new LongArrayMap<>();
    private final LongArrayMap<DbEntry> mUpdates = new LongArrayMap<>();

    protected LossyScreenMigrationTask(Context context, InvariantDeviceProfile invariantDeviceProfile, SQLiteDatabase sQLiteDatabase) {
        super(context, invariantDeviceProfile, getValidPackages(context), new Point(invariantDeviceProfile.numColumns, invariantDeviceProfile.numRows + 1), new Point(invariantDeviceProfile.numColumns, invariantDeviceProfile.numRows));
        this.mDb = sQLiteDatabase;
    }

    /* access modifiers changed from: protected */
    public Cursor queryWorkspace(String[] strArr, String str) {
        return this.mDb.query(Favorites.TABLE_NAME, strArr, str, null, null, null, null);
    }

    /* access modifiers changed from: protected */
    public void update(DbEntry dbEntry) {
        this.mUpdates.put(dbEntry.f52id, dbEntry.copy());
    }

    /* access modifiers changed from: protected */
    public ArrayList<DbEntry> loadWorkspaceEntries(long j) {
        ArrayList<DbEntry> loadWorkspaceEntries = super.loadWorkspaceEntries(j);
        Iterator it = loadWorkspaceEntries.iterator();
        while (it.hasNext()) {
            DbEntry dbEntry = (DbEntry) it.next();
            this.mOriginalItems.put(dbEntry.f52id, dbEntry.copy());
            dbEntry.cellY++;
            this.mUpdates.put(dbEntry.f52id, dbEntry.copy());
        }
        return loadWorkspaceEntries;
    }

    public void migrateScreen0() {
        migrateScreen(0);
        ContentValues contentValues = new ContentValues();
        Iterator it = this.mUpdates.iterator();
        while (it.hasNext()) {
            DbEntry dbEntry = (DbEntry) it.next();
            DbEntry dbEntry2 = (DbEntry) this.mOriginalItems.get(dbEntry.f52id);
            if (dbEntry2.cellX != dbEntry.cellX || dbEntry2.cellY != dbEntry.cellY || dbEntry2.spanX != dbEntry.spanX || dbEntry2.spanY != dbEntry.spanY) {
                contentValues.clear();
                dbEntry.addToContentValues(contentValues);
                this.mDb.update(Favorites.TABLE_NAME, contentValues, "_id = ?", new String[]{Long.toString(dbEntry.f52id)});
            }
        }
        Iterator it2 = this.mCarryOver.iterator();
        while (it2.hasNext()) {
            this.mEntryToRemove.add(Long.valueOf(((DbEntry) it2.next()).f52id));
        }
        if (!this.mEntryToRemove.isEmpty()) {
            this.mDb.delete(Favorites.TABLE_NAME, Utilities.createDbSelectionQuery("_id", this.mEntryToRemove), null);
        }
    }
}
