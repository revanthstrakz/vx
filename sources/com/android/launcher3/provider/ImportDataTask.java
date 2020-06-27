package com.android.launcher3.provider;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Process;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import com.android.launcher3.AutoInstallsLayout.LayoutParserCallback;
import com.android.launcher3.C0622R;
import com.android.launcher3.DefaultLayoutParser;
import com.android.launcher3.DefaultLayoutParser.AppShortcutWithUriParser;
import com.android.launcher3.DefaultLayoutParser.ResolveParser;
import com.android.launcher3.DefaultLayoutParser.UriShortcutParser;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.LauncherSettings.Settings;
import com.android.launcher3.LauncherSettings.WorkspaceScreens;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.GridSizeMigrationTask;
import com.android.launcher3.util.LongArrayMap;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

public class ImportDataTask {
    private static final int BATCH_INSERT_SIZE = 15;
    public static final String KEY_DATA_IMPORT_SRC_AUTHORITY = "data_import_src_authority";
    public static final String KEY_DATA_IMPORT_SRC_PKG = "data_import_src_pkg";
    private static final int MIN_ITEM_COUNT_FOR_SUCCESSFUL_MIGRATION = 6;
    private static final String TAG = "ImportDataTask";
    private final Context mContext;
    private int mHotseatSize;
    private int mMaxGridSizeX;
    private int mMaxGridSizeY;
    private final Uri mOtherFavoritesUri;
    private final Uri mOtherScreensUri;

    private static class HotseatLayoutParser extends DefaultLayoutParser {
        public HotseatLayoutParser(Context context, LayoutParserCallback layoutParserCallback) {
            super(context, null, layoutParserCallback, context.getResources(), ImportDataTask.getMyHotseatLayoutId(context));
        }

        /* access modifiers changed from: protected */
        public ArrayMap<String, TagParser> getLayoutElementsMap() {
            ArrayMap<String, TagParser> arrayMap = new ArrayMap<>();
            arrayMap.put("favorite", new AppShortcutWithUriParser());
            arrayMap.put("shortcut", new UriShortcutParser(this.mSourceRes));
            arrayMap.put("resolve", new ResolveParser());
            return arrayMap;
        }
    }

    private static class HotseatParserCallback implements LayoutParserCallback {
        private final HashSet<String> mExistingApps;
        private final LongArrayMap<Object> mExistingItems;
        private final ArrayList<ContentProviderOperation> mOutOps;
        private final int mRequiredSize;
        private int mStartItemId;

        HotseatParserCallback(HashSet<String> hashSet, LongArrayMap<Object> longArrayMap, ArrayList<ContentProviderOperation> arrayList, int i, int i2) {
            this.mExistingApps = hashSet;
            this.mExistingItems = longArrayMap;
            this.mOutOps = arrayList;
            this.mRequiredSize = i2;
            this.mStartItemId = i;
        }

        public long generateNewItemId() {
            int i = this.mStartItemId;
            this.mStartItemId = i + 1;
            return (long) i;
        }

        public long insertAndCheck(SQLiteDatabase sQLiteDatabase, ContentValues contentValues) {
            if (this.mExistingItems.size() >= this.mRequiredSize) {
                return 0;
            }
            try {
                Intent parseUri = Intent.parseUri(contentValues.getAsString(BaseLauncherColumns.INTENT), 0);
                String access$100 = ImportDataTask.getPackage(parseUri);
                if (access$100 == null || this.mExistingApps.contains(access$100)) {
                    return 0;
                }
                this.mExistingApps.add(access$100);
                long j = 0;
                while (this.mExistingItems.get(j) != null) {
                    j++;
                }
                this.mExistingItems.put(j, parseUri);
                contentValues.put(Favorites.SCREEN, Long.valueOf(j));
                this.mOutOps.add(ContentProviderOperation.newInsert(Favorites.CONTENT_URI).withValues(contentValues).build());
                return 0;
            } catch (URISyntaxException unused) {
                return 0;
            }
        }
    }

    private ImportDataTask(Context context, String str) {
        this.mContext = context;
        StringBuilder sb = new StringBuilder();
        sb.append("content://");
        sb.append(str);
        sb.append("/");
        sb.append(WorkspaceScreens.TABLE_NAME);
        this.mOtherScreensUri = Uri.parse(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("content://");
        sb2.append(str);
        sb2.append("/");
        sb2.append(Favorites.TABLE_NAME);
        this.mOtherFavoritesUri = Uri.parse(sb2.toString());
    }

    public boolean importWorkspace() throws Exception {
        ArrayList screenIdsFromCursor = LauncherDbUtils.getScreenIdsFromCursor(this.mContext.getContentResolver().query(this.mOtherScreensUri, null, null, null, WorkspaceScreens.SCREEN_RANK));
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Importing DB from ");
        sb.append(this.mOtherFavoritesUri);
        FileLog.m11d(str, sb.toString());
        if (screenIdsFromCursor.isEmpty()) {
            FileLog.m13e(TAG, "No data found to import");
            return false;
        }
        this.mMaxGridSizeY = 0;
        this.mMaxGridSizeX = 0;
        this.mHotseatSize = 0;
        ArrayList arrayList = new ArrayList();
        int size = screenIdsFromCursor.size();
        LongSparseArray longSparseArray = new LongSparseArray(size);
        for (int i = 0; i < size; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", Integer.valueOf(i));
            contentValues.put(WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
            longSparseArray.put(((Long) screenIdsFromCursor.get(i)).longValue(), Long.valueOf((long) i));
            arrayList.add(ContentProviderOperation.newInsert(WorkspaceScreens.CONTENT_URI).withValues(contentValues).build());
        }
        this.mContext.getContentResolver().applyBatch(LauncherProvider.AUTHORITY, arrayList);
        importWorkspaceItems(((Long) screenIdsFromCursor.get(0)).longValue(), longSparseArray);
        GridSizeMigrationTask.markForMigration(this.mContext, this.mMaxGridSizeX, this.mMaxGridSizeY, this.mHotseatSize);
        Settings.call(this.mContext.getContentResolver(), Settings.METHOD_CLEAR_EMPTY_DB_FLAG);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0472, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0473, code lost:
        r3 = r1;
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0475, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0477, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x0478, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x01f9, code lost:
        if (r0 == 4) goto L_0x02c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x01fb, code lost:
        switch(r0) {
            case 0: goto L_0x024b;
            case 1: goto L_0x024b;
            case 2: goto L_0x022e;
            default: goto L_0x01fe;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x01fe, code lost:
        com.android.launcher3.logging.FileLog.m11d(TAG, java.lang.String.format("Skipping item %d, not a valid type %d", new java.lang.Object[]{java.lang.Integer.valueOf(r12), java.lang.Integer.valueOf(r0)}));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x021b, code lost:
        r40 = r4;
        r41 = r6;
        r42 = r7;
        r4 = r21;
        r46 = r22;
        r45 = r23;
        r44 = r24;
        r6 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x022e, code lost:
        r4.put(r12, true);
        r5 = new android.content.Intent();
        r39 = r0;
        r40 = r4;
        r41 = r6;
        r42 = r7;
        r4 = r21;
        r46 = r22;
        r45 = r23;
        r44 = r24;
        r6 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x024b, code lost:
        r39 = r0;
        r5 = android.content.Intent.parseUri(r10.getString(r11), 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x025a, code lost:
        if (com.android.launcher3.Utilities.isLauncherAppTarget(r5) == false) goto L_0x0268;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x025c, code lost:
        r40 = r4;
        r41 = r6;
        r42 = r7;
        r4 = r24;
        r6 = r26;
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0268, code lost:
        r40 = r4;
        r41 = r6;
        r6 = r26;
        r6.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, r10.getString(r9));
        r42 = r7;
        r4 = r24;
        r6.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, r10.getString(r4));
        r0 = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0286, code lost:
        r43 = r0;
        r44 = r4;
        r0 = r23;
        r6.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.ICON, r10.getBlob(r0));
        r45 = r0;
        r6.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.INTENT, r5.toUri(0));
        r4 = r22;
        r6.put(com.android.launcher3.LauncherSettings.Favorites.RANK, java.lang.Integer.valueOf(r10.getInt(r4)));
        r46 = r4;
        r6.put(com.android.launcher3.LauncherSettings.Favorites.RESTORED, java.lang.Integer.valueOf(1));
        r4 = r21;
        r39 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x02c1, code lost:
        r39 = r0;
        r40 = r4;
        r41 = r6;
        r42 = r7;
        r46 = r22;
        r45 = r23;
        r44 = r24;
        r6 = r26;
        r6.put(com.android.launcher3.LauncherSettings.Favorites.RESTORED, java.lang.Integer.valueOf(7));
        r4 = r21;
        r6.put(com.android.launcher3.LauncherSettings.Favorites.APPWIDGET_PROVIDER, r10.getString(r4));
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x02e9, code lost:
        if (r14 != -101) goto L_0x033a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x02eb, code lost:
        if (r5 != null) goto L_0x031e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x02ed, code lost:
        com.android.launcher3.logging.FileLog.m11d(TAG, java.lang.String.format("Skipping item %d, null intent on hotseat", new java.lang.Object[]{java.lang.Integer.valueOf(r12)}));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0302, code lost:
        r1 = r3;
        r21 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0305, code lost:
        r12 = r6;
        r0 = r27;
        r14 = r28;
        r3 = r33;
        r4 = r35;
        r5 = r38;
        r2 = r40;
        r6 = r41;
        r7 = r42;
        r24 = r44;
        r23 = r45;
        r22 = r46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0323, code lost:
        if (r5.getComponent() == null) goto L_0x0330;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0325, code lost:
        r5.setPackage(r5.getComponent().getPackageName());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0330, code lost:
        r0 = getPackage(r5);
        r5 = r20;
        r5.add(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x033a, code lost:
        r5 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x033d, code lost:
        r6.put("_id", java.lang.Integer.valueOf(r12));
        r6.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.ITEM_TYPE, java.lang.Integer.valueOf(r39));
        r6.put(com.android.launcher3.LauncherSettings.Favorites.CONTAINER, java.lang.Integer.valueOf(r14));
        r6.put(com.android.launcher3.LauncherSettings.Favorites.SCREEN, java.lang.Long.valueOf(r1));
        r6.put(com.android.launcher3.LauncherSettings.Favorites.CELLX, java.lang.Integer.valueOf(r16));
        r6.put(com.android.launcher3.LauncherSettings.Favorites.CELLY, java.lang.Integer.valueOf(r30));
        r6.put(com.android.launcher3.LauncherSettings.Favorites.SPANX, java.lang.Integer.valueOf(r31));
        r6.put(com.android.launcher3.LauncherSettings.Favorites.SPANY, java.lang.Integer.valueOf(r32));
        r1 = r19;
        r6.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.TITLE, r10.getString(r1));
        r2 = r18;
        r2.add(android.content.ContentProviderOperation.newInsert(com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI).withValues(r6).build());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x03a3, code lost:
        if (r14 >= 0) goto L_0x03aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x03a5, code lost:
        r25 = r25 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x03aa, code lost:
        r0 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x03b2, code lost:
        if (r2.size() < 15) goto L_0x03c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x03b4, code lost:
        r3.mContext.getContentResolver().applyBatch(com.android.launcher3.LauncherProvider.AUTHORITY, r2);
        r2.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x03c2, code lost:
        r19 = r1;
        r18 = r2;
        r1 = r3;
        r21 = r4;
        r20 = r5;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0477 A[ExcHandler: Throwable (th java.lang.Throwable), PHI: r1 
      PHI: (r1v4 com.android.launcher3.provider.ImportDataTask) = (r1v0 com.android.launcher3.provider.ImportDataTask), (r1v6 com.android.launcher3.provider.ImportDataTask) binds: [B:25:0x007f, B:29:0x00f8] A[DONT_GENERATE, DONT_INLINE], Splitter:B:25:0x007f] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0480  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void importWorkspaceItems(long r48, android.util.LongSparseArray<java.lang.Long> r50) throws java.lang.Exception {
        /*
            r47 = this;
            r1 = r47
            android.content.Context r0 = r1.mContext
            com.android.launcher3.compat.UserManagerCompat r0 = com.android.launcher3.compat.UserManagerCompat.getInstance(r0)
            android.os.UserHandle r2 = android.os.Process.myUserHandle()
            long r2 = r0.getSerialNumberForUser(r2)
            java.lang.String r0 = java.lang.Long.toString(r2)
            boolean r2 = com.android.launcher3.config.FeatureFlags.QSB_ON_FIRST_SCREEN
            r4 = 2
            r5 = 0
            r6 = 1
            if (r2 == 0) goto L_0x005b
            android.content.Context r2 = r1.mContext
            android.content.ContentResolver r7 = r2.getContentResolver()
            android.net.Uri r8 = r1.mOtherFavoritesUri
            r9 = 0
            java.lang.String r10 = "profileId = ? AND container = -100 AND screen = ? AND cellY = 0"
            java.lang.String[] r11 = new java.lang.String[r4]
            r11[r5] = r0
            java.lang.String r2 = java.lang.Long.toString(r48)
            r11[r6] = r2
            r12 = 0
            android.database.Cursor r2 = r7.query(r8, r9, r10, r11, r12)
            boolean r7 = r2.moveToNext()     // Catch:{ Throwable -> 0x0043, all -> 0x003f }
            if (r2 == 0) goto L_0x005c
            r2.close()
            goto L_0x005c
        L_0x003f:
            r0 = move-exception
            r3 = r0
            r4 = 0
            goto L_0x0049
        L_0x0043:
            r0 = move-exception
            r3 = r0
            throw r3     // Catch:{ all -> 0x0046 }
        L_0x0046:
            r0 = move-exception
            r4 = r3
            r3 = r0
        L_0x0049:
            if (r2 == 0) goto L_0x005a
            if (r4 == 0) goto L_0x0057
            r2.close()     // Catch:{ Throwable -> 0x0051 }
            goto L_0x005a
        L_0x0051:
            r0 = move-exception
            r2 = r0
            r4.addSuppressed(r2)
            goto L_0x005a
        L_0x0057:
            r2.close()
        L_0x005a:
            throw r3
        L_0x005b:
            r7 = 0
        L_0x005c:
            java.util.ArrayList r2 = new java.util.ArrayList
            r8 = 15
            r2.<init>(r8)
            java.util.HashSet r9 = new java.util.HashSet
            r9.<init>()
            android.content.Context r10 = r1.mContext
            android.content.ContentResolver r11 = r10.getContentResolver()
            android.net.Uri r12 = r1.mOtherFavoritesUri
            r13 = 0
            java.lang.String r14 = "profileId = ?"
            java.lang.String[] r15 = new java.lang.String[r6]
            r15[r5] = r0
            java.lang.String r16 = "container"
            android.database.Cursor r10 = r11.query(r12, r13, r14, r15, r16)
            java.lang.String r0 = "_id"
            int r0 = r10.getColumnIndexOrThrow(r0)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r11 = "intent"
            int r11 = r10.getColumnIndexOrThrow(r11)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r12 = "title"
            int r12 = r10.getColumnIndexOrThrow(r12)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r13 = "container"
            int r13 = r10.getColumnIndexOrThrow(r13)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r14 = "itemType"
            int r14 = r10.getColumnIndexOrThrow(r14)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r15 = "appWidgetProvider"
            int r15 = r10.getColumnIndexOrThrow(r15)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r3 = "screen"
            int r3 = r10.getColumnIndexOrThrow(r3)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r8 = "cellX"
            int r8 = r10.getColumnIndexOrThrow(r8)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r4 = "cellY"
            int r4 = r10.getColumnIndexOrThrow(r4)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r6 = "spanX"
            int r6 = r10.getColumnIndexOrThrow(r6)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            java.lang.String r5 = "spanY"
            int r5 = r10.getColumnIndexOrThrow(r5)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r18 = r2
            java.lang.String r2 = "rank"
            int r2 = r10.getColumnIndexOrThrow(r2)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r19 = r12
            java.lang.String r12 = "icon"
            int r12 = r10.getColumnIndexOrThrow(r12)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r20 = r9
            java.lang.String r9 = "iconPackage"
            int r9 = r10.getColumnIndexOrThrow(r9)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r21 = r15
            java.lang.String r15 = "iconResource"
            int r15 = r10.getColumnIndexOrThrow(r15)     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r22 = r2
            android.util.SparseBooleanArray r2 = new android.util.SparseBooleanArray     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r2.<init>()     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r23 = r12
            android.content.ContentValues r12 = new android.content.ContentValues     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r12.<init>()     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            r24 = r15
            r15 = 0
            r25 = 0
        L_0x00f2:
            boolean r16 = r10.moveToNext()     // Catch:{ Throwable -> 0x0477, all -> 0x0472 }
            if (r16 == 0) goto L_0x03e1
            r12.clear()     // Catch:{ Throwable -> 0x0477, all -> 0x03db }
            r26 = r12
            int r12 = r10.getInt(r0)     // Catch:{ Throwable -> 0x0477, all -> 0x03db }
            int r15 = java.lang.Math.max(r15, r12)     // Catch:{ Throwable -> 0x0477, all -> 0x03db }
            r27 = r0
            int r0 = r10.getInt(r14)     // Catch:{ Throwable -> 0x0477, all -> 0x03db }
            r28 = r14
            int r14 = r10.getInt(r13)     // Catch:{ Throwable -> 0x0477, all -> 0x03db }
            r29 = r2
            long r1 = r10.getLong(r3)     // Catch:{ Throwable -> 0x03d6, all -> 0x03d2 }
            int r16 = r10.getInt(r8)     // Catch:{ Throwable -> 0x03d6, all -> 0x03d2 }
            int r30 = r10.getInt(r4)     // Catch:{ Throwable -> 0x03d6, all -> 0x03d2 }
            int r31 = r10.getInt(r6)     // Catch:{ Throwable -> 0x03d6, all -> 0x03d2 }
            int r32 = r10.getInt(r5)     // Catch:{ Throwable -> 0x03d6, all -> 0x03d2 }
            r33 = r3
            switch(r14) {
                case -101: goto L_0x01bb;
                case -100: goto L_0x013a;
                default: goto L_0x012c;
            }
        L_0x012c:
            r35 = r4
            r38 = r5
            r4 = r29
            r3 = r47
            boolean r5 = r4.get(r14)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            goto L_0x01d1
        L_0x013a:
            r3 = r50
            java.lang.Object r34 = r3.get(r1)     // Catch:{ Throwable -> 0x03d6, all -> 0x03d2 }
            java.lang.Long r34 = (java.lang.Long) r34     // Catch:{ Throwable -> 0x03d6, all -> 0x03d2 }
            if (r34 != 0) goto L_0x018c
            java.lang.String r14 = "ImportDataTask"
            java.lang.String r3 = "Skipping item %d, type %d not on a valid screen %d"
            r35 = r4
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            r16 = 0
            r4[r16] = r12     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            r12 = 1
            r4[r12] = r0     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            java.lang.Long r0 = java.lang.Long.valueOf(r1)     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            r1 = 2
            r4[r1] = r0     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            java.lang.String r0 = java.lang.String.format(r3, r4)     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            com.android.launcher3.logging.FileLog.m11d(r14, r0)     // Catch:{ Throwable -> 0x0186, all -> 0x0180 }
            r38 = r5
            r41 = r6
            r42 = r7
            r4 = r21
            r46 = r22
            r45 = r23
            r44 = r24
            r6 = r26
            r40 = r29
            r3 = r47
            goto L_0x022b
        L_0x0180:
            r0 = move-exception
            r1 = r0
            r3 = r47
            goto L_0x03de
        L_0x0186:
            r0 = move-exception
            r1 = r0
            r3 = r47
            goto L_0x047a
        L_0x018c:
            r35 = r4
            long r1 = r34.longValue()     // Catch:{ Throwable -> 0x03d6, all -> 0x03d2 }
            if (r7 == 0) goto L_0x019c
            r3 = 0
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x019c
            int r30 = r30 + 1
        L_0x019c:
            r3 = r47
            int r4 = r3.mMaxGridSizeX     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r36 = r1
            int r1 = r16 + r31
            int r1 = java.lang.Math.max(r4, r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r3.mMaxGridSizeX = r1     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            int r1 = r3.mMaxGridSizeY     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            int r2 = r30 + r32
            int r1 = java.lang.Math.max(r1, r2)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r3.mMaxGridSizeY = r1     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r38 = r5
            r4 = r29
            r1 = r36
            goto L_0x01f8
        L_0x01bb:
            r35 = r4
            r3 = r47
            int r4 = r3.mHotseatSize     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r38 = r5
            int r5 = (int) r1     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r17 = 1
            int r5 = r5 + 1
            int r4 = java.lang.Math.max(r4, r5)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r3.mHotseatSize = r4     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r4 = r29
            goto L_0x01f8
        L_0x01d1:
            if (r5 != 0) goto L_0x01f8
            java.lang.String r1 = "ImportDataTask"
            java.lang.String r2 = "Skipping item %d, type %d not in a valid folder %d"
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r16 = 0
            r5[r16] = r12     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r12 = 1
            r5[r12] = r0     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r14)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r12 = 2
            r5[r12] = r0     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = java.lang.String.format(r2, r5)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            com.android.launcher3.logging.FileLog.m11d(r1, r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            goto L_0x021b
        L_0x01f8:
            r5 = 4
            if (r0 == r5) goto L_0x02c1
            switch(r0) {
                case 0: goto L_0x024b;
                case 1: goto L_0x024b;
                case 2: goto L_0x022e;
                default: goto L_0x01fe;
            }     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
        L_0x01fe:
            java.lang.String r1 = "ImportDataTask"
            java.lang.String r2 = "Skipping item %d, not a valid type %d"
            r5 = 2
            java.lang.Object[] r14 = new java.lang.Object[r5]     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r16 = 0
            r14[r16] = r12     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r12 = 1
            r14[r12] = r0     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = java.lang.String.format(r2, r14)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            com.android.launcher3.logging.FileLog.m11d(r1, r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
        L_0x021b:
            r40 = r4
            r41 = r6
            r42 = r7
            r4 = r21
            r46 = r22
            r45 = r23
            r44 = r24
            r6 = r26
        L_0x022b:
            r7 = 0
            goto L_0x0302
        L_0x022e:
            r5 = 1
            r4.put(r12, r5)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            android.content.Intent r5 = new android.content.Intent     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r5.<init>()     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r39 = r0
            r40 = r4
            r41 = r6
            r42 = r7
            r4 = r21
            r46 = r22
            r45 = r23
            r44 = r24
            r6 = r26
            goto L_0x02e7
        L_0x024b:
            java.lang.String r5 = r10.getString(r11)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r39 = r0
            r0 = 0
            android.content.Intent r5 = android.content.Intent.parseUri(r5, r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            boolean r0 = com.android.launcher3.Utilities.isLauncherAppTarget(r5)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            if (r0 == 0) goto L_0x0268
            r40 = r4
            r41 = r6
            r42 = r7
            r4 = r24
            r6 = r26
            r0 = 0
            goto L_0x0286
        L_0x0268:
            java.lang.String r0 = "iconPackage"
            r40 = r4
            java.lang.String r4 = r10.getString(r9)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r41 = r6
            r6 = r26
            r6.put(r0, r4)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "iconResource"
            r42 = r7
            r4 = r24
            java.lang.String r7 = r10.getString(r4)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r7)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r0 = r39
        L_0x0286:
            java.lang.String r7 = "icon"
            r43 = r0
            r44 = r4
            r0 = r23
            byte[] r4 = r10.getBlob(r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r7, r4)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r4 = "intent"
            r45 = r0
            r7 = 0
            java.lang.String r0 = r5.toUri(r7)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r4, r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "rank"
            r4 = r22
            int r7 = r10.getInt(r4)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r7)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "restored"
            r46 = r4
            r7 = 1
            java.lang.Integer r4 = java.lang.Integer.valueOf(r7)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r4)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r4 = r21
            r39 = r43
            goto L_0x02e7
        L_0x02c1:
            r39 = r0
            r40 = r4
            r41 = r6
            r42 = r7
            r46 = r22
            r45 = r23
            r44 = r24
            r6 = r26
            java.lang.String r0 = "restored"
            r4 = 7
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r4)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "appWidgetProvider"
            r4 = r21
            java.lang.String r5 = r10.getString(r4)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r5)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r5 = 0
        L_0x02e7:
            r0 = -101(0xffffffffffffff9b, float:NaN)
            if (r14 != r0) goto L_0x033a
            if (r5 != 0) goto L_0x031e
            java.lang.String r0 = "ImportDataTask"
            java.lang.String r1 = "Skipping item %d, null intent on hotseat"
            r2 = 1
            java.lang.Object[] r5 = new java.lang.Object[r2]     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r7 = 0
            r5[r7] = r2     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r1 = java.lang.String.format(r1, r5)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            com.android.launcher3.logging.FileLog.m11d(r0, r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
        L_0x0302:
            r1 = r3
            r21 = r4
        L_0x0305:
            r12 = r6
            r0 = r27
            r14 = r28
            r3 = r33
            r4 = r35
            r5 = r38
            r2 = r40
            r6 = r41
            r7 = r42
            r24 = r44
            r23 = r45
            r22 = r46
            goto L_0x00f2
        L_0x031e:
            r7 = 0
            android.content.ComponentName r0 = r5.getComponent()     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            if (r0 == 0) goto L_0x0330
            android.content.ComponentName r0 = r5.getComponent()     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = r0.getPackageName()     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r5.setPackage(r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
        L_0x0330:
            java.lang.String r0 = getPackage(r5)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r5 = r20
            r5.add(r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            goto L_0x033d
        L_0x033a:
            r5 = r20
            r7 = 0
        L_0x033d:
            java.lang.String r0 = "_id"
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r12)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "itemType"
            java.lang.Integer r12 = java.lang.Integer.valueOf(r39)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r12)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "container"
            java.lang.Integer r12 = java.lang.Integer.valueOf(r14)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r12)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "screen"
            java.lang.Long r1 = java.lang.Long.valueOf(r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "cellX"
            java.lang.Integer r1 = java.lang.Integer.valueOf(r16)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "cellY"
            java.lang.Integer r1 = java.lang.Integer.valueOf(r30)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "spanX"
            java.lang.Integer r1 = java.lang.Integer.valueOf(r31)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "spanY"
            java.lang.Integer r1 = java.lang.Integer.valueOf(r32)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r0 = "title"
            r1 = r19
            java.lang.String r2 = r10.getString(r1)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r6.put(r0, r2)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            android.net.Uri r0 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            android.content.ContentProviderOperation$Builder r0 = android.content.ContentProviderOperation.newInsert(r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            android.content.ContentProviderOperation$Builder r0 = r0.withValues(r6)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            android.content.ContentProviderOperation r0 = r0.build()     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r2 = r18
            r2.add(r0)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            if (r14 >= 0) goto L_0x03aa
            r0 = r25
            int r25 = r0 + 1
            goto L_0x03ac
        L_0x03aa:
            r0 = r25
        L_0x03ac:
            int r0 = r2.size()     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r12 = 15
            if (r0 < r12) goto L_0x03c2
            android.content.Context r0 = r3.mContext     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            java.lang.String r14 = com.android.launcher3.LauncherProvider.AUTHORITY     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r0.applyBatch(r14, r2)     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
            r2.clear()     // Catch:{ Throwable -> 0x03cf, all -> 0x03cd }
        L_0x03c2:
            r19 = r1
            r18 = r2
            r1 = r3
            r21 = r4
            r20 = r5
            goto L_0x0305
        L_0x03cd:
            r0 = move-exception
            goto L_0x03dd
        L_0x03cf:
            r0 = move-exception
            goto L_0x0479
        L_0x03d2:
            r0 = move-exception
            r3 = r47
            goto L_0x03dd
        L_0x03d6:
            r0 = move-exception
            r3 = r47
            goto L_0x0479
        L_0x03db:
            r0 = move-exception
            r3 = r1
        L_0x03dd:
            r1 = r0
        L_0x03de:
            r5 = 0
            goto L_0x047e
        L_0x03e1:
            r3 = r1
            r2 = r18
            r5 = r20
            r0 = r25
            if (r10 == 0) goto L_0x03ed
            r10.close()
        L_0x03ed:
            java.lang.String r1 = "ImportDataTask"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            java.lang.String r6 = " items imported from external source"
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            com.android.launcher3.logging.FileLog.m11d(r1, r4)
            r1 = 6
            if (r0 < r1) goto L_0x046a
            boolean r0 = r2.isEmpty()
            if (r0 != 0) goto L_0x041a
            android.content.Context r0 = r3.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = com.android.launcher3.LauncherProvider.AUTHORITY
            r0.applyBatch(r1, r2)
            r2.clear()
        L_0x041a:
            android.content.Context r0 = r3.mContext
            com.android.launcher3.util.LongArrayMap r0 = com.android.launcher3.model.GridSizeMigrationTask.removeBrokenHotseatItems(r0)
            android.content.Context r1 = r3.mContext
            com.android.launcher3.InvariantDeviceProfile r1 = com.android.launcher3.LauncherAppState.getIDP(r1)
            int r13 = r1.numHotseatIcons
            int r1 = r0.size()
            if (r1 >= r13) goto L_0x0469
            com.android.launcher3.provider.ImportDataTask$HotseatParserCallback r1 = new com.android.launcher3.provider.ImportDataTask$HotseatParserCallback
            r4 = 1
            int r12 = r15 + 1
            r8 = r1
            r9 = r5
            r10 = r0
            r11 = r2
            r8.<init>(r9, r10, r11, r12, r13)
            com.android.launcher3.provider.ImportDataTask$HotseatLayoutParser r4 = new com.android.launcher3.provider.ImportDataTask$HotseatLayoutParser
            android.content.Context r5 = r3.mContext
            r4.<init>(r5, r1)
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r5 = 0
            r4.loadLayout(r5, r1)
            int r1 = r0.size()
            r4 = 1
            int r1 = r1 - r4
            long r0 = r0.keyAt(r1)
            int r0 = (int) r0
            int r0 = r0 + r4
            r3.mHotseatSize = r0
            boolean r0 = r2.isEmpty()
            if (r0 != 0) goto L_0x0469
            android.content.Context r0 = r3.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = com.android.launcher3.LauncherProvider.AUTHORITY
            r0.applyBatch(r1, r2)
        L_0x0469:
            return
        L_0x046a:
            java.lang.Exception r0 = new java.lang.Exception
            java.lang.String r1 = "Insufficient data"
            r0.<init>(r1)
            throw r0
        L_0x0472:
            r0 = move-exception
            r3 = r1
            r5 = 0
        L_0x0475:
            r1 = r0
            goto L_0x047e
        L_0x0477:
            r0 = move-exception
            r3 = r1
        L_0x0479:
            r1 = r0
        L_0x047a:
            throw r1     // Catch:{ all -> 0x047b }
        L_0x047b:
            r0 = move-exception
            r5 = r1
            goto L_0x0475
        L_0x047e:
            if (r10 == 0) goto L_0x048f
            if (r5 == 0) goto L_0x048c
            r10.close()     // Catch:{ Throwable -> 0x0486 }
            goto L_0x048f
        L_0x0486:
            r0 = move-exception
            r2 = r0
            r5.addSuppressed(r2)
            goto L_0x048f
        L_0x048c:
            r10.close()
        L_0x048f:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.provider.ImportDataTask.importWorkspaceItems(long, android.util.LongSparseArray):void");
    }

    /* access modifiers changed from: private */
    public static String getPackage(Intent intent) {
        if (intent.getComponent() != null) {
            return intent.getComponent().getPackageName();
        }
        return intent.getPackage();
    }

    public static boolean performImportIfPossible(Context context) throws Exception {
        SharedPreferences devicePrefs = Utilities.getDevicePrefs(context);
        String string = devicePrefs.getString(KEY_DATA_IMPORT_SRC_PKG, "");
        String string2 = devicePrefs.getString(KEY_DATA_IMPORT_SRC_AUTHORITY, "");
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2)) {
            return false;
        }
        devicePrefs.edit().remove(KEY_DATA_IMPORT_SRC_PKG).remove(KEY_DATA_IMPORT_SRC_AUTHORITY).commit();
        if (!Settings.call(context.getContentResolver(), Settings.METHOD_WAS_EMPTY_DB_CREATED).getBoolean("value", false)) {
            return false;
        }
        for (ProviderInfo providerInfo : context.getPackageManager().queryContentProviders(null, context.getApplicationInfo().uid, 0)) {
            if (string.equals(providerInfo.packageName)) {
                if ((providerInfo.applicationInfo.flags & 1) == 0) {
                    return false;
                }
                if (string2.equals(providerInfo.authority) && (TextUtils.isEmpty(providerInfo.readPermission) || context.checkPermission(providerInfo.readPermission, Process.myPid(), Process.myUid()) == 0)) {
                    return new ImportDataTask(context, string2).importWorkspace();
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static int getMyHotseatLayoutId(Context context) {
        return LauncherAppState.getIDP(context).numHotseatIcons <= 5 ? C0622R.xml.dw_phone_hotseat : C0622R.xml.dw_tablet_hotseat;
    }
}
