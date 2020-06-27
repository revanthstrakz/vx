package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.LauncherSettings.Settings;
import com.android.launcher3.LauncherSettings.WorkspaceScreens;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.LongArrayMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

public class GridSizeMigrationTask {
    private static final boolean DEBUG = true;
    public static boolean ENABLED = Utilities.ATLEAST_NOUGAT;
    private static final String KEY_MIGRATION_SRC_HOTSEAT_COUNT = "migration_src_hotseat_count";
    private static final String KEY_MIGRATION_SRC_WORKSPACE_SIZE = "migration_src_workspace_size";
    private static final String TAG = "GridSizeMigrationTask";
    private static final float WT_APPLICATION = 0.8f;
    private static final float WT_FOLDER_FACTOR = 0.5f;
    private static final float WT_SHORTCUT = 1.0f;
    private static final float WT_WIDGET_FACTOR = 0.6f;
    private static final float WT_WIDGET_MIN = 2.0f;
    protected final ArrayList<DbEntry> mCarryOver = new ArrayList<>();
    private final Context mContext;
    private final int mDestHotseatSize;
    protected final ArrayList<Long> mEntryToRemove = new ArrayList<>();
    private final InvariantDeviceProfile mIdp;
    private final boolean mShouldRemoveX;
    private final boolean mShouldRemoveY;
    private final int mSrcHotseatSize;
    private final int mSrcX;
    private final int mSrcY;
    private final ContentValues mTempValues = new ContentValues();
    /* access modifiers changed from: private */
    public final int mTrgX;
    /* access modifiers changed from: private */
    public final int mTrgY;
    private final ArrayList<ContentProviderOperation> mUpdateOperations = new ArrayList<>();
    private final HashSet<String> mValidPackages;

    protected static class DbEntry extends ItemInfo implements Comparable<DbEntry> {
        public float weight;

        public DbEntry copy() {
            DbEntry dbEntry = new DbEntry();
            dbEntry.copyFrom(this);
            dbEntry.weight = this.weight;
            dbEntry.minSpanX = this.minSpanX;
            dbEntry.minSpanY = this.minSpanY;
            return dbEntry;
        }

        public int compareTo(DbEntry dbEntry) {
            if (this.itemType == 4) {
                if (dbEntry.itemType == 4) {
                    return (dbEntry.spanY * dbEntry.spanX) - (this.spanX * this.spanY);
                }
                return -1;
            } else if (dbEntry.itemType == 4) {
                return 1;
            } else {
                return Float.compare(dbEntry.weight, this.weight);
            }
        }

        public boolean columnsSame(DbEntry dbEntry) {
            return dbEntry.cellX == this.cellX && dbEntry.cellY == this.cellY && dbEntry.spanX == this.spanX && dbEntry.spanY == this.spanY && dbEntry.screenId == this.screenId;
        }

        public void addToContentValues(ContentValues contentValues) {
            contentValues.put(Favorites.SCREEN, Long.valueOf(this.screenId));
            contentValues.put(Favorites.CELLX, Integer.valueOf(this.cellX));
            contentValues.put(Favorites.CELLY, Integer.valueOf(this.cellY));
            contentValues.put(Favorites.SPANX, Integer.valueOf(this.spanX));
            contentValues.put(Favorites.SPANY, Integer.valueOf(this.spanY));
        }
    }

    protected static class MultiStepMigrationTask {
        private final Context mContext;
        private final HashSet<String> mValidPackages;

        public MultiStepMigrationTask(HashSet<String> hashSet, Context context) {
            this.mValidPackages = hashSet;
            this.mContext = context;
        }

        public boolean migrate(Point point, Point point2) throws Exception {
            boolean z = false;
            if (!point2.equals(point)) {
                if (point.x < point2.x) {
                    point.x = point2.x;
                }
                if (point.y < point2.y) {
                    point.y = point2.y;
                }
                while (!point2.equals(point)) {
                    Point point3 = new Point(point);
                    if (point2.x < point3.x) {
                        point3.x--;
                    }
                    if (point2.y < point3.y) {
                        point3.y--;
                    }
                    if (runStepTask(point, point3)) {
                        z = true;
                    }
                    point.set(point3.x, point3.y);
                }
            }
            return z;
        }

        /* access modifiers changed from: protected */
        public boolean runStepTask(Point point, Point point2) throws Exception {
            GridSizeMigrationTask gridSizeMigrationTask = new GridSizeMigrationTask(this.mContext, LauncherAppState.getIDP(this.mContext), this.mValidPackages, point, point2);
            return gridSizeMigrationTask.migrateWorkspace();
        }
    }

    private class OptimalPlacementSolution {
        ArrayList<DbEntry> finalPlacedItems;
        private final boolean ignoreMove;
        private final ArrayList<DbEntry> itemsToPlace;
        float lowestMoveCost;
        float lowestWeightLoss;
        private final GridOccupancy occupied;
        private final int startY;

        public OptimalPlacementSolution(GridSizeMigrationTask gridSizeMigrationTask, GridOccupancy gridOccupancy, ArrayList<DbEntry> arrayList, int i) {
            this(gridOccupancy, arrayList, i, false);
        }

        public OptimalPlacementSolution(GridOccupancy gridOccupancy, ArrayList<DbEntry> arrayList, int i, boolean z) {
            this.lowestWeightLoss = Float.MAX_VALUE;
            this.lowestMoveCost = Float.MAX_VALUE;
            this.occupied = gridOccupancy;
            this.itemsToPlace = arrayList;
            this.ignoreMove = z;
            this.startY = i;
            Collections.sort(this.itemsToPlace);
        }

        public void find() {
            find(0, 0.0f, 0.0f, new ArrayList());
        }

        public void find(int i, float f, float f2, ArrayList<DbEntry> arrayList) {
            float f3;
            float f4;
            int i2;
            float f5;
            float f6;
            int i3;
            int i4;
            int i5 = i;
            float f7 = f;
            float f8 = f2;
            ArrayList<DbEntry> arrayList2 = arrayList;
            if (f7 < this.lowestWeightLoss && (f7 != this.lowestWeightLoss || f8 < this.lowestMoveCost)) {
                if (i5 >= this.itemsToPlace.size()) {
                    this.lowestWeightLoss = f7;
                    this.lowestMoveCost = f8;
                    this.finalPlacedItems = GridSizeMigrationTask.deepCopy(arrayList);
                    return;
                }
                DbEntry dbEntry = (DbEntry) this.itemsToPlace.get(i5);
                int i6 = dbEntry.cellX;
                int i7 = dbEntry.cellY;
                ArrayList arrayList3 = new ArrayList(arrayList.size() + 1);
                arrayList3.addAll(arrayList2);
                arrayList3.add(dbEntry);
                if (dbEntry.spanX > 1 || dbEntry.spanY > 1) {
                    int i8 = dbEntry.spanX;
                    int i9 = dbEntry.spanY;
                    for (int i10 = this.startY; i10 < GridSizeMigrationTask.this.mTrgY; i10++) {
                        int i11 = 0;
                        while (i11 < GridSizeMigrationTask.this.mTrgX) {
                            if (i11 != i6) {
                                dbEntry.cellX = i11;
                                f4 = 1.0f;
                                f3 = f8 + 1.0f;
                            } else {
                                f4 = 1.0f;
                                f3 = f8;
                            }
                            if (i10 != i7) {
                                dbEntry.cellY = i10;
                                f3 += f4;
                            }
                            if (this.ignoreMove) {
                                f3 = f8;
                            }
                            if (this.occupied.isRegionVacant(i11, i10, i8, i9)) {
                                this.occupied.markCells((ItemInfo) dbEntry, true);
                                find(i5 + 1, f7, f3, arrayList3);
                                this.occupied.markCells((ItemInfo) dbEntry, false);
                            }
                            if (i8 > dbEntry.minSpanX && this.occupied.isRegionVacant(i11, i10, i8 - 1, i9)) {
                                dbEntry.spanX--;
                                this.occupied.markCells((ItemInfo) dbEntry, true);
                                find(i5 + 1, f7, f3 + 1.0f, arrayList3);
                                this.occupied.markCells((ItemInfo) dbEntry, false);
                                dbEntry.spanX++;
                            }
                            if (i9 > dbEntry.minSpanY && this.occupied.isRegionVacant(i11, i10, i8, i9 - 1)) {
                                dbEntry.spanY--;
                                this.occupied.markCells((ItemInfo) dbEntry, true);
                                find(i5 + 1, f7, f3 + 1.0f, arrayList3);
                                this.occupied.markCells((ItemInfo) dbEntry, false);
                                dbEntry.spanY++;
                            }
                            if (i9 <= dbEntry.minSpanY || i8 <= dbEntry.minSpanX) {
                                i2 = i8;
                            } else {
                                i2 = i8;
                                if (this.occupied.isRegionVacant(i11, i10, i8 - 1, i9 - 1)) {
                                    dbEntry.spanX--;
                                    dbEntry.spanY--;
                                    this.occupied.markCells((ItemInfo) dbEntry, true);
                                    find(i5 + 1, f7, f3 + GridSizeMigrationTask.WT_WIDGET_MIN, arrayList3);
                                    this.occupied.markCells((ItemInfo) dbEntry, false);
                                    dbEntry.spanX++;
                                    dbEntry.spanY++;
                                    dbEntry.cellX = i6;
                                    dbEntry.cellY = i7;
                                    i11++;
                                    i8 = i2;
                                }
                            }
                            dbEntry.cellX = i6;
                            dbEntry.cellY = i7;
                            i11++;
                            i8 = i2;
                        }
                        int i12 = i8;
                    }
                    try {
                        find(i5 + 1, f7 + dbEntry.weight, f8, arrayList2);
                    } catch (Throwable th) {
                        throw th;
                    }
                } else {
                    int i13 = Integer.MAX_VALUE;
                    int i14 = Integer.MAX_VALUE;
                    int i15 = Integer.MAX_VALUE;
                    for (int i16 = this.startY; i16 < GridSizeMigrationTask.this.mTrgY; i16++) {
                        for (int i17 = 0; i17 < GridSizeMigrationTask.this.mTrgX; i17++) {
                            if (!this.occupied.cells[i17][i16]) {
                                if (this.ignoreMove) {
                                    i3 = i13;
                                    i4 = 0;
                                } else {
                                    i3 = i13;
                                    i4 = ((dbEntry.cellX - i17) * (dbEntry.cellX - i17)) + ((dbEntry.cellY - i16) * (dbEntry.cellY - i16));
                                }
                                if (i4 < i15) {
                                    i14 = i16;
                                    i15 = i4;
                                    i13 = i17;
                                }
                            } else {
                                i3 = i13;
                            }
                            i13 = i3;
                        }
                        int i18 = i13;
                    }
                    if (i13 >= GridSizeMigrationTask.this.mTrgX || i14 >= GridSizeMigrationTask.this.mTrgY) {
                        for (int i19 = i5 + 1; i19 < this.itemsToPlace.size(); i19++) {
                            f7 += ((DbEntry) this.itemsToPlace.get(i19)).weight;
                        }
                        find(this.itemsToPlace.size(), f7 + dbEntry.weight, f8, arrayList2);
                    } else {
                        if (i13 != i6) {
                            dbEntry.cellX = i13;
                            f6 = 1.0f;
                            f5 = f8 + 1.0f;
                        } else {
                            f6 = 1.0f;
                            f5 = f8;
                        }
                        if (i14 != i7) {
                            dbEntry.cellY = i14;
                            f5 += f6;
                        }
                        if (this.ignoreMove) {
                            f5 = f8;
                        }
                        this.occupied.markCells((ItemInfo) dbEntry, true);
                        int i20 = i5 + 1;
                        find(i20, f7, f5, arrayList3);
                        this.occupied.markCells((ItemInfo) dbEntry, false);
                        dbEntry.cellX = i6;
                        dbEntry.cellY = i7;
                        if (i20 < this.itemsToPlace.size() && ((DbEntry) this.itemsToPlace.get(i20)).weight >= dbEntry.weight && !this.ignoreMove) {
                            find(i20, f7 + dbEntry.weight, f8, arrayList2);
                        }
                    }
                }
            }
        }
    }

    protected GridSizeMigrationTask(Context context, InvariantDeviceProfile invariantDeviceProfile, HashSet<String> hashSet, Point point, Point point2) {
        this.mContext = context;
        this.mValidPackages = hashSet;
        this.mIdp = invariantDeviceProfile;
        this.mSrcX = point.x;
        this.mSrcY = point.y;
        this.mTrgX = point2.x;
        this.mTrgY = point2.y;
        boolean z = false;
        this.mShouldRemoveX = this.mTrgX < this.mSrcX;
        if (this.mTrgY < this.mSrcY) {
            z = true;
        }
        this.mShouldRemoveY = z;
        this.mDestHotseatSize = -1;
        this.mSrcHotseatSize = -1;
    }

    protected GridSizeMigrationTask(Context context, InvariantDeviceProfile invariantDeviceProfile, HashSet<String> hashSet, int i, int i2) {
        this.mContext = context;
        this.mIdp = invariantDeviceProfile;
        this.mValidPackages = hashSet;
        this.mSrcHotseatSize = i;
        this.mDestHotseatSize = i2;
        this.mTrgY = -1;
        this.mTrgX = -1;
        this.mSrcY = -1;
        this.mSrcX = -1;
        this.mShouldRemoveY = false;
        this.mShouldRemoveX = false;
    }

    private boolean applyOperations() throws Exception {
        if (!this.mUpdateOperations.isEmpty()) {
            this.mContext.getContentResolver().applyBatch(LauncherProvider.AUTHORITY, this.mUpdateOperations);
        }
        if (!this.mEntryToRemove.isEmpty()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Removing items: ");
            sb.append(TextUtils.join(", ", this.mEntryToRemove));
            Log.d(str, sb.toString());
            this.mContext.getContentResolver().delete(Favorites.CONTENT_URI, Utilities.createDbSelectionQuery("_id", this.mEntryToRemove), null);
        }
        return !this.mUpdateOperations.isEmpty() || !this.mEntryToRemove.isEmpty();
    }

    /* access modifiers changed from: protected */
    public boolean migrateHotseat() throws Exception {
        ArrayList loadHotseatEntries = loadHotseatEntries();
        int i = this.mDestHotseatSize;
        while (loadHotseatEntries.size() > i) {
            DbEntry dbEntry = (DbEntry) loadHotseatEntries.get(loadHotseatEntries.size() / 2);
            Iterator it = loadHotseatEntries.iterator();
            while (it.hasNext()) {
                DbEntry dbEntry2 = (DbEntry) it.next();
                if (dbEntry2.weight < dbEntry.weight) {
                    dbEntry = dbEntry2;
                }
            }
            this.mEntryToRemove.add(Long.valueOf(dbEntry.f52id));
            loadHotseatEntries.remove(dbEntry);
        }
        Iterator it2 = loadHotseatEntries.iterator();
        int i2 = 0;
        while (it2.hasNext()) {
            DbEntry dbEntry3 = (DbEntry) it2.next();
            long j = (long) i2;
            if (dbEntry3.screenId != j) {
                dbEntry3.screenId = j;
                dbEntry3.cellX = i2;
                dbEntry3.cellY = 0;
                update(dbEntry3);
            }
            i2++;
        }
        return applyOperations();
    }

    /* access modifiers changed from: protected */
    public boolean migrateWorkspace() throws Exception {
        ArrayList loadWorkspaceScreensDb = LauncherModel.loadWorkspaceScreensDb(this.mContext);
        if (!loadWorkspaceScreensDb.isEmpty()) {
            Iterator it = loadWorkspaceScreensDb.iterator();
            while (it.hasNext()) {
                long longValue = ((Long) it.next()).longValue();
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Migrating ");
                sb.append(longValue);
                Log.d(str, sb.toString());
                migrateScreen(longValue);
            }
            if (!this.mCarryOver.isEmpty()) {
                LongArrayMap longArrayMap = new LongArrayMap();
                Iterator it2 = this.mCarryOver.iterator();
                while (it2.hasNext()) {
                    DbEntry dbEntry = (DbEntry) it2.next();
                    longArrayMap.put(dbEntry.f52id, dbEntry);
                }
                do {
                    OptimalPlacementSolution optimalPlacementSolution = new OptimalPlacementSolution(new GridOccupancy(this.mTrgX, this.mTrgY), deepCopy(this.mCarryOver), 0, true);
                    optimalPlacementSolution.find();
                    if (optimalPlacementSolution.finalPlacedItems.size() > 0) {
                        long j = Settings.call(this.mContext.getContentResolver(), Settings.METHOD_NEW_SCREEN_ID).getLong("value");
                        loadWorkspaceScreensDb.add(Long.valueOf(j));
                        Iterator it3 = optimalPlacementSolution.finalPlacedItems.iterator();
                        while (it3.hasNext()) {
                            DbEntry dbEntry2 = (DbEntry) it3.next();
                            if (this.mCarryOver.remove(longArrayMap.get(dbEntry2.f52id))) {
                                dbEntry2.screenId = j;
                                update(dbEntry2);
                            } else {
                                throw new Exception("Unable to find matching items");
                            }
                        }
                    } else {
                        throw new Exception("None of the items can be placed on an empty screen");
                    }
                } while (!this.mCarryOver.isEmpty());
                Uri uri = WorkspaceScreens.CONTENT_URI;
                this.mUpdateOperations.add(ContentProviderOperation.newDelete(uri).build());
                int size = loadWorkspaceScreensDb.size();
                for (int i = 0; i < size; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_id", Long.valueOf(((Long) loadWorkspaceScreensDb.get(i)).longValue()));
                    contentValues.put(WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                    this.mUpdateOperations.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
                }
            }
            return applyOperations();
        }
        throw new Exception("Unable to get workspace screens");
    }

    /* access modifiers changed from: protected */
    public void migrateScreen(long j) {
        long j2 = j;
        int i = (!FeatureFlags.QSB_ON_FIRST_SCREEN || j2 != 0) ? 0 : 1;
        ArrayList loadWorkspaceEntries = loadWorkspaceEntries(j);
        float[] fArr = new float[2];
        int i2 = Integer.MAX_VALUE;
        ArrayList arrayList = null;
        int i3 = Integer.MAX_VALUE;
        float f = Float.MAX_VALUE;
        float f2 = Float.MAX_VALUE;
        for (int i4 = 0; i4 < this.mSrcX; i4++) {
            int i5 = this.mSrcY - 1;
            int i6 = i3;
            ArrayList arrayList2 = arrayList;
            int i7 = i2;
            float f3 = f;
            float f4 = f2;
            while (i5 >= i) {
                int i8 = i5;
                ArrayList tryRemove = tryRemove(i4, i5, i, deepCopy(loadWorkspaceEntries), fArr);
                if (fArr[0] < f3 || (fArr[0] == f3 && fArr[1] < f4)) {
                    float f5 = fArr[0];
                    float f6 = fArr[1];
                    if (this.mShouldRemoveX) {
                        i7 = i4;
                    }
                    if (this.mShouldRemoveY) {
                        i6 = i8;
                    }
                    arrayList2 = tryRemove;
                    f3 = f5;
                    f4 = f6;
                }
                if (!this.mShouldRemoveY) {
                    break;
                }
                i5 = i8 - 1;
            }
            f = f3;
            f2 = f4;
            i2 = i7;
            i3 = i6;
            arrayList = arrayList2;
            if (!this.mShouldRemoveX) {
                break;
            }
        }
        Log.d(TAG, String.format("Removing row %d, column %d on screen %d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i2), Long.valueOf(j)}));
        LongArrayMap longArrayMap = new LongArrayMap();
        Iterator it = deepCopy(loadWorkspaceEntries).iterator();
        while (it.hasNext()) {
            DbEntry dbEntry = (DbEntry) it.next();
            longArrayMap.put(dbEntry.f52id, dbEntry);
        }
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            DbEntry dbEntry2 = (DbEntry) it2.next();
            DbEntry dbEntry3 = (DbEntry) longArrayMap.get(dbEntry2.f52id);
            longArrayMap.remove(dbEntry2.f52id);
            if (!dbEntry2.columnsSame(dbEntry3)) {
                update(dbEntry2);
            }
        }
        Iterator it3 = longArrayMap.iterator();
        while (it3.hasNext()) {
            this.mCarryOver.add((DbEntry) it3.next());
        }
        if (!this.mCarryOver.isEmpty() && f == 0.0f) {
            GridOccupancy gridOccupancy = new GridOccupancy(this.mTrgX, this.mTrgY);
            int i9 = i;
            gridOccupancy.markCells(0, 0, this.mTrgX, i, true);
            Iterator it4 = arrayList.iterator();
            while (it4.hasNext()) {
                gridOccupancy.markCells((ItemInfo) (DbEntry) it4.next(), true);
            }
            OptimalPlacementSolution optimalPlacementSolution = new OptimalPlacementSolution(gridOccupancy, deepCopy(this.mCarryOver), i9, true);
            optimalPlacementSolution.find();
            if (optimalPlacementSolution.lowestWeightLoss == 0.0f) {
                Iterator it5 = optimalPlacementSolution.finalPlacedItems.iterator();
                while (it5.hasNext()) {
                    DbEntry dbEntry4 = (DbEntry) it5.next();
                    dbEntry4.screenId = j2;
                    update(dbEntry4);
                }
                this.mCarryOver.clear();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void update(DbEntry dbEntry) {
        this.mTempValues.clear();
        dbEntry.addToContentValues(this.mTempValues);
        this.mUpdateOperations.add(ContentProviderOperation.newUpdate(Favorites.getContentUri(dbEntry.f52id)).withValues(this.mTempValues).build());
    }

    private ArrayList<DbEntry> tryRemove(int i, int i2, int i3, ArrayList<DbEntry> arrayList, float[] fArr) {
        GridOccupancy gridOccupancy = new GridOccupancy(this.mTrgX, this.mTrgY);
        gridOccupancy.markCells(0, 0, this.mTrgX, i3, true);
        if (!this.mShouldRemoveX) {
            i = Integer.MAX_VALUE;
        }
        if (!this.mShouldRemoveY) {
            i2 = Integer.MAX_VALUE;
        }
        ArrayList<DbEntry> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            DbEntry dbEntry = (DbEntry) it.next();
            if ((dbEntry.cellX > i || dbEntry.spanX + dbEntry.cellX <= i) && (dbEntry.cellY > i2 || dbEntry.spanY + dbEntry.cellY <= i2)) {
                if (dbEntry.cellX > i) {
                    dbEntry.cellX--;
                }
                if (dbEntry.cellY > i2) {
                    dbEntry.cellY--;
                }
                arrayList2.add(dbEntry);
                gridOccupancy.markCells((ItemInfo) dbEntry, true);
            } else {
                arrayList3.add(dbEntry);
                if (dbEntry.cellX >= i) {
                    dbEntry.cellX--;
                }
                if (dbEntry.cellY >= i2) {
                    dbEntry.cellY--;
                }
            }
        }
        OptimalPlacementSolution optimalPlacementSolution = new OptimalPlacementSolution(this, gridOccupancy, arrayList3, i3);
        optimalPlacementSolution.find();
        arrayList2.addAll(optimalPlacementSolution.finalPlacedItems);
        fArr[0] = optimalPlacementSolution.lowestWeightLoss;
        fArr[1] = optimalPlacementSolution.lowestMoveCost;
        return arrayList2;
    }

    private ArrayList<DbEntry> loadHotseatEntries() {
        Cursor query = this.mContext.getContentResolver().query(Favorites.CONTENT_URI, new String[]{"_id", BaseLauncherColumns.ITEM_TYPE, BaseLauncherColumns.INTENT, Favorites.SCREEN}, "container = -101", null, null, null);
        int columnIndexOrThrow = query.getColumnIndexOrThrow("_id");
        int columnIndexOrThrow2 = query.getColumnIndexOrThrow(BaseLauncherColumns.ITEM_TYPE);
        int columnIndexOrThrow3 = query.getColumnIndexOrThrow(BaseLauncherColumns.INTENT);
        int columnIndexOrThrow4 = query.getColumnIndexOrThrow(Favorites.SCREEN);
        ArrayList<DbEntry> arrayList = new ArrayList<>();
        while (query.moveToNext()) {
            DbEntry dbEntry = new DbEntry();
            dbEntry.f52id = query.getLong(columnIndexOrThrow);
            dbEntry.itemType = query.getInt(columnIndexOrThrow2);
            dbEntry.screenId = query.getLong(columnIndexOrThrow4);
            if (dbEntry.screenId >= ((long) this.mSrcHotseatSize)) {
                this.mEntryToRemove.add(Long.valueOf(dbEntry.f52id));
            } else {
                try {
                    int i = dbEntry.itemType;
                    if (i != 6) {
                        switch (i) {
                            case 0:
                            case 1:
                                break;
                            case 2:
                                int folderItemsCount = getFolderItemsCount(dbEntry.f52id);
                                if (folderItemsCount != 0) {
                                    dbEntry.weight = ((float) folderItemsCount) * 0.5f;
                                    break;
                                } else {
                                    throw new Exception("Folder is empty");
                                }
                            default:
                                throw new Exception("Invalid item type");
                        }
                    }
                    verifyIntent(query.getString(columnIndexOrThrow3));
                    dbEntry.weight = dbEntry.itemType == 0 ? WT_APPLICATION : 1.0f;
                    arrayList.add(dbEntry);
                } catch (Exception e) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Removing item ");
                    sb.append(dbEntry.f52id);
                    Log.d(str, sb.toString(), e);
                    this.mEntryToRemove.add(Long.valueOf(dbEntry.f52id));
                }
            }
        }
        query.close();
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public ArrayList<DbEntry> loadWorkspaceEntries(long j) {
        ArrayList arrayList;
        long j2 = j;
        String[] strArr = {"_id", BaseLauncherColumns.ITEM_TYPE, Favorites.CELLX, Favorites.CELLY, Favorites.SPANX, Favorites.SPANY, BaseLauncherColumns.INTENT, Favorites.APPWIDGET_PROVIDER, Favorites.APPWIDGET_ID};
        StringBuilder sb = new StringBuilder();
        sb.append("container = -100 AND screen = ");
        sb.append(j2);
        Cursor queryWorkspace = queryWorkspace(strArr, sb.toString());
        int columnIndexOrThrow = queryWorkspace.getColumnIndexOrThrow("_id");
        int columnIndexOrThrow2 = queryWorkspace.getColumnIndexOrThrow(BaseLauncherColumns.ITEM_TYPE);
        int columnIndexOrThrow3 = queryWorkspace.getColumnIndexOrThrow(Favorites.CELLX);
        int columnIndexOrThrow4 = queryWorkspace.getColumnIndexOrThrow(Favorites.CELLY);
        int columnIndexOrThrow5 = queryWorkspace.getColumnIndexOrThrow(Favorites.SPANX);
        int columnIndexOrThrow6 = queryWorkspace.getColumnIndexOrThrow(Favorites.SPANY);
        int columnIndexOrThrow7 = queryWorkspace.getColumnIndexOrThrow(BaseLauncherColumns.INTENT);
        int columnIndexOrThrow8 = queryWorkspace.getColumnIndexOrThrow(Favorites.APPWIDGET_PROVIDER);
        int columnIndexOrThrow9 = queryWorkspace.getColumnIndexOrThrow(Favorites.APPWIDGET_ID);
        ArrayList arrayList2 = new ArrayList();
        while (queryWorkspace.moveToNext()) {
            DbEntry dbEntry = new DbEntry();
            int i = columnIndexOrThrow9;
            ArrayList arrayList3 = arrayList2;
            dbEntry.f52id = queryWorkspace.getLong(columnIndexOrThrow);
            dbEntry.itemType = queryWorkspace.getInt(columnIndexOrThrow2);
            dbEntry.cellX = queryWorkspace.getInt(columnIndexOrThrow3);
            dbEntry.cellY = queryWorkspace.getInt(columnIndexOrThrow4);
            dbEntry.spanX = queryWorkspace.getInt(columnIndexOrThrow5);
            dbEntry.spanY = queryWorkspace.getInt(columnIndexOrThrow6);
            dbEntry.screenId = j2;
            try {
                int i2 = dbEntry.itemType;
                if (i2 != 4) {
                    if (i2 != 6) {
                        switch (i2) {
                            case 0:
                            case 1:
                                break;
                            case 2:
                                int folderItemsCount = getFolderItemsCount(dbEntry.f52id);
                                if (folderItemsCount != 0) {
                                    dbEntry.weight = ((float) folderItemsCount) * 0.5f;
                                    break;
                                } else {
                                    throw new Exception("Folder is empty");
                                }
                            default:
                                throw new Exception("Invalid item type");
                        }
                    }
                    verifyIntent(queryWorkspace.getString(columnIndexOrThrow7));
                    dbEntry.weight = dbEntry.itemType == 0 ? WT_APPLICATION : 1.0f;
                    columnIndexOrThrow9 = i;
                } else {
                    verifyPackage(ComponentName.unflattenFromString(queryWorkspace.getString(columnIndexOrThrow8)).getPackageName());
                    dbEntry.weight = Math.max(WT_WIDGET_MIN, ((float) dbEntry.spanX) * WT_WIDGET_FACTOR * ((float) dbEntry.spanY));
                    columnIndexOrThrow9 = i;
                    try {
                        LauncherAppWidgetProviderInfo launcherAppWidgetInfo = AppWidgetManagerCompat.getInstance(this.mContext).getLauncherAppWidgetInfo(queryWorkspace.getInt(columnIndexOrThrow9));
                        Point point = null;
                        if (launcherAppWidgetInfo != null) {
                            point = launcherAppWidgetInfo.getMinSpans(this.mIdp, this.mContext);
                        }
                        if (point != null) {
                            dbEntry.minSpanX = point.x > 0 ? point.x : dbEntry.spanX;
                            dbEntry.minSpanY = point.y > 0 ? point.y : dbEntry.spanY;
                        } else {
                            dbEntry.minSpanY = 2;
                            dbEntry.minSpanX = 2;
                        }
                        if (dbEntry.minSpanX > this.mTrgX || dbEntry.minSpanY > this.mTrgY) {
                            arrayList = arrayList3;
                            try {
                                throw new Exception("Widget can't be resized down to fit the grid");
                            } catch (Exception e) {
                                e = e;
                                String str = TAG;
                                StringBuilder sb2 = new StringBuilder();
                                int i3 = columnIndexOrThrow;
                                sb2.append("Removing item ");
                                int i4 = columnIndexOrThrow2;
                                sb2.append(dbEntry.f52id);
                                Log.d(str, sb2.toString(), e);
                                this.mEntryToRemove.add(Long.valueOf(dbEntry.f52id));
                                arrayList2 = arrayList;
                                columnIndexOrThrow = i3;
                                columnIndexOrThrow2 = i4;
                                j2 = j;
                            }
                        }
                    } catch (Exception e2) {
                        e = e2;
                        arrayList = arrayList3;
                        String str2 = TAG;
                        StringBuilder sb22 = new StringBuilder();
                        int i32 = columnIndexOrThrow;
                        sb22.append("Removing item ");
                        int i42 = columnIndexOrThrow2;
                        sb22.append(dbEntry.f52id);
                        Log.d(str2, sb22.toString(), e);
                        this.mEntryToRemove.add(Long.valueOf(dbEntry.f52id));
                        arrayList2 = arrayList;
                        columnIndexOrThrow = i32;
                        columnIndexOrThrow2 = i42;
                        j2 = j;
                    }
                }
                ArrayList arrayList4 = arrayList3;
                arrayList4.add(dbEntry);
                arrayList2 = arrayList4;
            } catch (Exception e3) {
                e = e3;
                columnIndexOrThrow9 = i;
                arrayList = arrayList3;
                String str22 = TAG;
                StringBuilder sb222 = new StringBuilder();
                int i322 = columnIndexOrThrow;
                sb222.append("Removing item ");
                int i422 = columnIndexOrThrow2;
                sb222.append(dbEntry.f52id);
                Log.d(str22, sb222.toString(), e);
                this.mEntryToRemove.add(Long.valueOf(dbEntry.f52id));
                arrayList2 = arrayList;
                columnIndexOrThrow = i322;
                columnIndexOrThrow2 = i422;
                j2 = j;
            }
            j2 = j;
        }
        ArrayList arrayList5 = arrayList2;
        queryWorkspace.close();
        return arrayList5;
    }

    private int getFolderItemsCount(long j) {
        String[] strArr = {"_id", BaseLauncherColumns.INTENT};
        StringBuilder sb = new StringBuilder();
        sb.append("container = ");
        sb.append(j);
        Cursor queryWorkspace = queryWorkspace(strArr, sb.toString());
        int i = 0;
        while (queryWorkspace.moveToNext()) {
            try {
                verifyIntent(queryWorkspace.getString(1));
                i++;
            } catch (Exception unused) {
                this.mEntryToRemove.add(Long.valueOf(queryWorkspace.getLong(0)));
            }
        }
        queryWorkspace.close();
        return i;
    }

    /* access modifiers changed from: protected */
    public Cursor queryWorkspace(String[] strArr, String str) {
        return this.mContext.getContentResolver().query(Favorites.CONTENT_URI, strArr, str, null, null, null);
    }

    private void verifyIntent(String str) throws Exception {
        Intent parseUri = Intent.parseUri(str, 0);
        if (parseUri.getComponent() != null) {
            verifyPackage(parseUri.getComponent().getPackageName());
        } else if (parseUri.getPackage() != null) {
            verifyPackage(parseUri.getPackage());
        }
    }

    private void verifyPackage(String str) throws Exception {
        if (!this.mValidPackages.contains(str)) {
            throw new Exception("Package not available");
        }
    }

    /* access modifiers changed from: private */
    public static ArrayList<DbEntry> deepCopy(ArrayList<DbEntry> arrayList) {
        ArrayList<DbEntry> arrayList2 = new ArrayList<>(arrayList.size());
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(((DbEntry) it.next()).copy());
        }
        return arrayList2;
    }

    private static Point parsePoint(String str) {
        String[] split = str.split(",");
        return new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private static String getPointString(int i, int i2) {
        return String.format(Locale.ENGLISH, "%d,%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
    }

    public static void markForMigration(Context context, int i, int i2, int i3) {
        Utilities.getPrefs(context).edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, getPointString(i, i2)).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, i3).apply();
    }

    public static boolean migrateGridIfNeeded(Context context) {
        boolean z;
        SharedPreferences prefs = Utilities.getPrefs(context);
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        String pointString = getPointString(idp.numColumns, idp.numRows);
        if (pointString.equals(prefs.getString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, "")) && idp.numHotseatIcons == prefs.getInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons)) {
            return true;
        }
        long currentTimeMillis = System.currentTimeMillis();
        try {
            HashSet validPackages = getValidPackages(context);
            int i = prefs.getInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons);
            if (i != idp.numHotseatIcons) {
                GridSizeMigrationTask gridSizeMigrationTask = new GridSizeMigrationTask(context, LauncherAppState.getIDP(context), validPackages, i, idp.numHotseatIcons);
                z = gridSizeMigrationTask.migrateHotseat();
            } else {
                z = false;
            }
            Point point = new Point(idp.numColumns, idp.numRows);
            if (new MultiStepMigrationTask(validPackages, context).migrate(parsePoint(prefs.getString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, pointString)), point)) {
                z = true;
            }
            if (z) {
                Cursor query = context.getContentResolver().query(Favorites.CONTENT_URI, null, null, null, null);
                boolean moveToNext = query.moveToNext();
                query.close();
                if (!moveToNext) {
                    throw new Exception("Removed every thing during grid resize");
                }
            }
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Workspace migration completed in ");
            sb.append(System.currentTimeMillis() - currentTimeMillis);
            Log.v(str, sb.toString());
            prefs.edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, pointString).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons).apply();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error during grid migration", e);
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Workspace migration completed in ");
            sb2.append(System.currentTimeMillis() - currentTimeMillis);
            Log.v(str2, sb2.toString());
            prefs.edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, pointString).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons).apply();
            return false;
        } catch (Throwable th) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Workspace migration completed in ");
            sb3.append(System.currentTimeMillis() - currentTimeMillis);
            Log.v(TAG, sb3.toString());
            prefs.edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, pointString).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons).apply();
            throw th;
        }
    }

    protected static HashSet<String> getValidPackages(Context context) {
        HashSet<String> hashSet = new HashSet<>();
        for (PackageInfo packageInfo : context.getPackageManager().getInstalledPackages(8192)) {
            hashSet.add(packageInfo.packageName);
        }
        hashSet.addAll(PackageInstallerCompat.getInstance(context).updateAndGetActiveSessionCache().keySet());
        return hashSet;
    }

    public static LongArrayMap<Object> removeBrokenHotseatItems(Context context) throws Exception {
        GridSizeMigrationTask gridSizeMigrationTask = new GridSizeMigrationTask(context, LauncherAppState.getIDP(context), getValidPackages(context), Integer.MAX_VALUE, Integer.MAX_VALUE);
        ArrayList loadHotseatEntries = gridSizeMigrationTask.loadHotseatEntries();
        gridSizeMigrationTask.applyOperations();
        LongArrayMap<Object> longArrayMap = new LongArrayMap<>();
        Iterator it = loadHotseatEntries.iterator();
        while (it.hasNext()) {
            DbEntry dbEntry = (DbEntry) it.next();
            longArrayMap.put(dbEntry.screenId, dbEntry);
        }
        return longArrayMap;
    }
}
