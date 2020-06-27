package com.android.launcher3;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.AutoInstallsLayout.LayoutParserCallback;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.LauncherSettings.Settings;
import com.android.launcher3.LauncherSettings.WorkspaceScreens;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.dynamicui.ExtractionUtils;
import com.android.launcher3.graphics.IconShapeOverride;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.DbDowngradeHelper;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.provider.LauncherDbUtils.SQLiteTransaction;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.util.ManagedProfileHeuristic;
import com.android.launcher3.util.NoLocaleSqliteContext;
import com.android.launcher3.util.Preconditions;
import com.google.android.apps.nexuslauncher.utils.BuildUtil;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class LauncherProvider extends ContentProvider {
    public static final String AUTHORITY;
    private static final String DOWNGRADE_SCHEMA_FILE = "downgrade_schema.json";
    static final String EMPTY_DATABASE_CREATED = "EMPTY_DATABASE_CREATED";
    private static final boolean LOGD = false;
    private static final String RESTRICTION_PACKAGE_NAME = "workspace.configuration.package.name";
    public static final int SCHEMA_VERSION = 27;
    private static final String TAG = "LauncherProvider";
    private Handler mListenerHandler;
    private final ChangeListenerWrapper mListenerWrapper = new ChangeListenerWrapper();
    protected DatabaseHelper mOpenHelper;

    private static class ChangeListenerWrapper implements Callback {
        private static final int MSG_APP_WIDGET_HOST_RESET = 3;
        private static final int MSG_EXTRACTED_COLORS_CHANGED = 2;
        private static final int MSG_LAUNCHER_PROVIDER_CHANGED = 1;
        /* access modifiers changed from: private */
        public LauncherProviderChangeListener mListener;

        private ChangeListenerWrapper() {
        }

        public boolean handleMessage(Message message) {
            if (this.mListener != null) {
                switch (message.what) {
                    case 1:
                        this.mListener.onLauncherProviderChanged();
                        break;
                    case 2:
                        this.mListener.onExtractedColorsChanged();
                        break;
                    case 3:
                        this.mListener.onAppWidgetHostReset();
                        break;
                }
            }
            return true;
        }
    }

    public static class DatabaseHelper extends SQLiteOpenHelper implements LayoutParserCallback {
        private final Context mContext;
        private long mMaxItemId;
        private long mMaxScreenId;
        private final Handler mWidgetHostResetHandler;

        DatabaseHelper(Context context, Handler handler) {
            this(context, handler, LauncherFiles.LAUNCHER_DB);
            if (!tableExists(Favorites.TABLE_NAME) || !tableExists(WorkspaceScreens.TABLE_NAME)) {
                Log.e(LauncherProvider.TAG, "Tables are missing after onCreate has been called. Trying to recreate");
                addFavoritesTable(getWritableDatabase(), true);
                addWorkspacesTable(getWritableDatabase(), true);
            }
            initIds();
        }

        public DatabaseHelper(Context context, Handler handler, String str) {
            super(new NoLocaleSqliteContext(context), str, null, 27);
            this.mMaxItemId = -1;
            this.mMaxScreenId = -1;
            this.mContext = context;
            this.mWidgetHostResetHandler = handler;
        }

        /* access modifiers changed from: protected */
        public void initIds() {
            if (this.mMaxItemId == -1) {
                this.mMaxItemId = initializeMaxItemId(getWritableDatabase());
            }
            if (this.mMaxScreenId == -1) {
                this.mMaxScreenId = initializeMaxScreenId(getWritableDatabase());
            }
        }

        private boolean tableExists(String str) {
            boolean z = true;
            Cursor query = getReadableDatabase().query(true, "sqlite_master", new String[]{"tbl_name"}, "tbl_name = ?", new String[]{str}, null, null, null, null, null);
            try {
                if (query.getCount() <= 0) {
                    z = false;
                }
                return z;
            } finally {
                query.close();
            }
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            this.mMaxItemId = 1;
            this.mMaxScreenId = 0;
            addFavoritesTable(sQLiteDatabase, false);
            addWorkspacesTable(sQLiteDatabase, false);
            this.mMaxItemId = initializeMaxItemId(sQLiteDatabase);
            onEmptyDbCreated();
        }

        /* access modifiers changed from: protected */
        public void onEmptyDbCreated() {
            if (this.mWidgetHostResetHandler != null) {
                newLauncherWidgetHost().deleteHost();
                this.mWidgetHostResetHandler.sendEmptyMessage(3);
            }
            Utilities.getPrefs(this.mContext).edit().putBoolean(LauncherProvider.EMPTY_DATABASE_CREATED, true).commit();
            ManagedProfileHeuristic.processAllUsers(Collections.emptyList(), this.mContext);
        }

        public long getDefaultUserSerial() {
            return UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(Process.myUserHandle());
        }

        private void addFavoritesTable(SQLiteDatabase sQLiteDatabase, boolean z) {
            Favorites.addTableToDb(sQLiteDatabase, getDefaultUserSerial(), z);
        }

        private void addWorkspacesTable(SQLiteDatabase sQLiteDatabase, boolean z) {
            String str = z ? " IF NOT EXISTS " : "";
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE ");
            sb.append(str);
            sb.append(WorkspaceScreens.TABLE_NAME);
            sb.append(" (");
            sb.append("_id");
            sb.append(" INTEGER PRIMARY KEY,");
            sb.append(WorkspaceScreens.SCREEN_RANK);
            sb.append(" INTEGER,");
            sb.append(ChangeLogColumns.MODIFIED);
            sb.append(" INTEGER NOT NULL DEFAULT 0");
            sb.append(");");
            sQLiteDatabase.execSQL(sb.toString());
        }

        private void removeOrphanedItems(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("DELETE FROM favorites WHERE screen NOT IN (SELECT _id FROM workspaceScreens) AND container = -100");
            sQLiteDatabase.execSQL("DELETE FROM favorites WHERE container <> -100 AND container <> -101 AND container NOT IN (SELECT _id FROM favorites WHERE itemType = 2)");
        }

        public void onOpen(SQLiteDatabase sQLiteDatabase) {
            super.onOpen(sQLiteDatabase);
            File fileStreamPath = this.mContext.getFileStreamPath(LauncherProvider.DOWNGRADE_SCHEMA_FILE);
            if (!fileStreamPath.exists()) {
                handleOneTimeDataUpgrade(sQLiteDatabase);
            }
            DbDowngradeHelper.updateSchemaFile(fileStreamPath, 27, this.mContext, C0622R.raw.downgrade_schema);
        }

        /* access modifiers changed from: protected */
        public void handleOneTimeDataUpgrade(SQLiteDatabase sQLiteDatabase) {
            UserManagerCompat instance = UserManagerCompat.getInstance(this.mContext);
            for (UserHandle serialNumberForUser : instance.getUserProfiles()) {
                long serialNumberForUser2 = instance.getSerialNumberForUser(serialNumberForUser);
                StringBuilder sb = new StringBuilder();
                sb.append("update favorites set intent = replace(intent, ';l.profile=");
                sb.append(serialNumberForUser2);
                sb.append(";', ';') where itemType = 0;");
                sQLiteDatabase.execSQL(sb.toString());
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0039, code lost:
            if (addIntegerColumn(r4, com.android.launcher3.LauncherSettings.Favorites.RESTORED, 0) == false) goto L_0x00b1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
            removeOrphanedItems(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0044, code lost:
            if (addProfileColumn(r4) != false) goto L_0x0048;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x004d, code lost:
            if (updateFolderItemsRank(r4, true) == false) goto L_0x00b1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0054, code lost:
            if (recreateWorkspaceTable(r4) == false) goto L_0x00b1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x005d, code lost:
            if (addIntegerColumn(r4, com.android.launcher3.LauncherSettings.Favorites.OPTIONS, 0) == false) goto L_0x00b1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0060, code lost:
            com.android.launcher3.util.ManagedProfileHeuristic.markExistingUsersForNoFolderCreation(r3.mContext);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0065, code lost:
            convertShortcutsToLauncherActivities(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x006a, code lost:
            if (com.android.launcher3.config.FeatureFlags.QSB_ON_FIRST_SCREEN == false) goto L_0x0075;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0072, code lost:
            if (com.android.launcher3.provider.LauncherDbUtils.prepareScreenZeroToHostQsb(r3.mContext, r4) != false) goto L_0x0075;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0075, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x0094, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:4:?, code lost:
            r5 = new com.android.launcher3.provider.LauncherDbUtils.SQLiteTransaction(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x0098, code lost:
            if (r6 != null) goto L_0x009a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
            r5.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x009e, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
            r6.addSuppressed(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x00a3, code lost:
            r5.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x00a7, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x00a8, code lost:
            android.util.Log.e(com.android.launcher3.LauncherProvider.TAG, r5.getMessage(), r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            r4.execSQL("ALTER TABLE favorites ADD COLUMN appWidgetProvider TEXT;");
            r5.commit();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
            r5.close();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onUpgrade(android.database.sqlite.SQLiteDatabase r4, int r5, int r6) {
            /*
                r3 = this;
                r6 = 0
                r0 = 0
                switch(r5) {
                    case 12: goto L_0x0008;
                    case 13: goto L_0x000e;
                    case 14: goto L_0x001e;
                    case 15: goto L_0x0033;
                    case 16: goto L_0x003d;
                    case 17: goto L_0x003d;
                    case 18: goto L_0x003d;
                    case 19: goto L_0x0040;
                    case 20: goto L_0x0048;
                    case 21: goto L_0x0050;
                    case 22: goto L_0x0057;
                    case 23: goto L_0x0060;
                    case 24: goto L_0x0060;
                    case 25: goto L_0x0065;
                    case 26: goto L_0x0068;
                    case 27: goto L_0x0075;
                    default: goto L_0x0006;
                }
            L_0x0006:
                goto L_0x00b1
            L_0x0008:
                r3.mMaxScreenId = r0
                r5 = 0
                r3.addWorkspacesTable(r4, r5)
            L_0x000e:
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r5 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x00a7 }
                r5.<init>(r4)     // Catch:{ SQLException -> 0x00a7 }
                java.lang.String r2 = "ALTER TABLE favorites ADD COLUMN appWidgetProvider TEXT;"
                r4.execSQL(r2)     // Catch:{ Throwable -> 0x0096 }
                r5.commit()     // Catch:{ Throwable -> 0x0096 }
                r5.close()     // Catch:{ SQLException -> 0x00a7 }
            L_0x001e:
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r5 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x0089 }
                r5.<init>(r4)     // Catch:{ SQLException -> 0x0089 }
                java.lang.String r2 = "ALTER TABLE favorites ADD COLUMN modified INTEGER NOT NULL DEFAULT 0;"
                r4.execSQL(r2)     // Catch:{ Throwable -> 0x0078 }
                java.lang.String r2 = "ALTER TABLE workspaceScreens ADD COLUMN modified INTEGER NOT NULL DEFAULT 0;"
                r4.execSQL(r2)     // Catch:{ Throwable -> 0x0078 }
                r5.commit()     // Catch:{ Throwable -> 0x0078 }
                r5.close()     // Catch:{ SQLException -> 0x0089 }
            L_0x0033:
                java.lang.String r5 = "restored"
                boolean r5 = r3.addIntegerColumn(r4, r5, r0)
                if (r5 != 0) goto L_0x003d
                goto L_0x00b1
            L_0x003d:
                r3.removeOrphanedItems(r4)
            L_0x0040:
                boolean r5 = r3.addProfileColumn(r4)
                if (r5 != 0) goto L_0x0048
                goto L_0x00b1
            L_0x0048:
                r5 = 1
                boolean r5 = r3.updateFolderItemsRank(r4, r5)
                if (r5 != 0) goto L_0x0050
                goto L_0x00b1
            L_0x0050:
                boolean r5 = r3.recreateWorkspaceTable(r4)
                if (r5 != 0) goto L_0x0057
                goto L_0x00b1
            L_0x0057:
                java.lang.String r5 = "options"
                boolean r5 = r3.addIntegerColumn(r4, r5, r0)
                if (r5 != 0) goto L_0x0060
                goto L_0x00b1
            L_0x0060:
                android.content.Context r5 = r3.mContext
                com.android.launcher3.util.ManagedProfileHeuristic.markExistingUsersForNoFolderCreation(r5)
            L_0x0065:
                r3.convertShortcutsToLauncherActivities(r4)
            L_0x0068:
                boolean r5 = com.android.launcher3.config.FeatureFlags.QSB_ON_FIRST_SCREEN
                if (r5 == 0) goto L_0x0075
                android.content.Context r5 = r3.mContext
                boolean r5 = com.android.launcher3.provider.LauncherDbUtils.prepareScreenZeroToHostQsb(r5, r4)
                if (r5 != 0) goto L_0x0075
                goto L_0x00b1
            L_0x0075:
                return
            L_0x0076:
                r0 = move-exception
                goto L_0x007a
            L_0x0078:
                r6 = move-exception
                throw r6     // Catch:{ all -> 0x0076 }
            L_0x007a:
                if (r6 == 0) goto L_0x0085
                r5.close()     // Catch:{ Throwable -> 0x0080 }
                goto L_0x0088
            L_0x0080:
                r5 = move-exception
                r6.addSuppressed(r5)     // Catch:{ SQLException -> 0x0089 }
                goto L_0x0088
            L_0x0085:
                r5.close()     // Catch:{ SQLException -> 0x0089 }
            L_0x0088:
                throw r0     // Catch:{ SQLException -> 0x0089 }
            L_0x0089:
                r5 = move-exception
                java.lang.String r6 = "LauncherProvider"
                java.lang.String r0 = r5.getMessage()
                android.util.Log.e(r6, r0, r5)
                goto L_0x00b1
            L_0x0094:
                r0 = move-exception
                goto L_0x0098
            L_0x0096:
                r6 = move-exception
                throw r6     // Catch:{ all -> 0x0094 }
            L_0x0098:
                if (r6 == 0) goto L_0x00a3
                r5.close()     // Catch:{ Throwable -> 0x009e }
                goto L_0x00a6
            L_0x009e:
                r5 = move-exception
                r6.addSuppressed(r5)     // Catch:{ SQLException -> 0x00a7 }
                goto L_0x00a6
            L_0x00a3:
                r5.close()     // Catch:{ SQLException -> 0x00a7 }
            L_0x00a6:
                throw r0     // Catch:{ SQLException -> 0x00a7 }
            L_0x00a7:
                r5 = move-exception
                java.lang.String r6 = "LauncherProvider"
                java.lang.String r0 = r5.getMessage()
                android.util.Log.e(r6, r0, r5)
            L_0x00b1:
                java.lang.String r5 = "LauncherProvider"
                java.lang.String r6 = "Destroying all old data."
                android.util.Log.w(r5, r6)
                r3.createEmptyDB(r4)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase, int, int):void");
        }

        public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            try {
                DbDowngradeHelper.parse(this.mContext.getFileStreamPath(LauncherProvider.DOWNGRADE_SCHEMA_FILE)).onDowngrade(sQLiteDatabase, i, i2);
            } catch (Exception e) {
                String str = LauncherProvider.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Unable to downgrade from: ");
                sb.append(i);
                sb.append(" to ");
                sb.append(i2);
                sb.append(". Wiping databse.");
                Log.d(str, sb.toString(), e);
                createEmptyDB(sQLiteDatabase);
            }
        }

        public void createEmptyDB(SQLiteDatabase sQLiteDatabase) {
            Throwable th;
            SQLiteTransaction sQLiteTransaction = new SQLiteTransaction(sQLiteDatabase);
            try {
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS favorites");
                sQLiteDatabase.execSQL("DROP TABLE IF EXISTS workspaceScreens");
                onCreate(sQLiteDatabase);
                sQLiteTransaction.commit();
                sQLiteTransaction.close();
                return;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }

        @TargetApi(26)
        public void removeGhostWidgets(SQLiteDatabase sQLiteDatabase) {
            Cursor query;
            Throwable th;
            int i;
            AppWidgetHost newLauncherWidgetHost = newLauncherWidgetHost();
            try {
                int[] appWidgetIds = newLauncherWidgetHost.getAppWidgetIds();
                HashSet hashSet = new HashSet();
                try {
                    query = sQLiteDatabase.query(Favorites.TABLE_NAME, new String[]{Favorites.APPWIDGET_ID}, "itemType=4", null, null, null, null);
                    while (true) {
                        if (!query.moveToNext()) {
                            break;
                        }
                        hashSet.add(Integer.valueOf(query.getInt(0)));
                    }
                    if (query != null) {
                        query.close();
                    }
                    for (int i2 : appWidgetIds) {
                        if (!hashSet.contains(Integer.valueOf(i2))) {
                            String str = LauncherProvider.TAG;
                            try {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Deleting invalid widget ");
                                sb.append(i2);
                                FileLog.m11d(str, sb.toString());
                                newLauncherWidgetHost.deleteAppWidgetId(i2);
                            } catch (RuntimeException unused) {
                            }
                        }
                    }
                    return;
                } catch (SQLException e) {
                    Log.w(LauncherProvider.TAG, "Error getting widgets list", e);
                    return;
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            } catch (IncompatibleClassChangeError e2) {
                Log.e(LauncherProvider.TAG, "getAppWidgetIds not supported", e2);
            }
        }

        /* access modifiers changed from: 0000 */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x0096, code lost:
            r12 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x0097, code lost:
            r3 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x009b, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x009c, code lost:
            r10 = r3;
            r3 = r12;
            r12 = r10;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x0096 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:6:0x0030] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void convertShortcutsToLauncherActivities(android.database.sqlite.SQLiteDatabase r12) {
            /*
                r11 = this;
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r0 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x00c4 }
                r0.<init>(r12)     // Catch:{ SQLException -> 0x00c4 }
                r1 = 0
                java.lang.String r3 = "favorites"
                java.lang.String r2 = "_id"
                java.lang.String r4 = "intent"
                java.lang.String[] r4 = new java.lang.String[]{r2, r4}     // Catch:{ Throwable -> 0x00b2 }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x00b2 }
                r2.<init>()     // Catch:{ Throwable -> 0x00b2 }
                java.lang.String r5 = "itemType=1 AND profileId="
                r2.append(r5)     // Catch:{ Throwable -> 0x00b2 }
                long r5 = r11.getDefaultUserSerial()     // Catch:{ Throwable -> 0x00b2 }
                r2.append(r5)     // Catch:{ Throwable -> 0x00b2 }
                java.lang.String r5 = r2.toString()     // Catch:{ Throwable -> 0x00b2 }
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                r2 = r12
                android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Throwable -> 0x00b2 }
                java.lang.String r3 = "UPDATE favorites SET itemType=0 WHERE _id=?"
                android.database.sqlite.SQLiteStatement r12 = r12.compileStatement(r3)     // Catch:{ Throwable -> 0x0099, all -> 0x0096 }
                java.lang.String r3 = "_id"
                int r3 = r2.getColumnIndexOrThrow(r3)     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                java.lang.String r4 = "intent"
                int r4 = r2.getColumnIndexOrThrow(r4)     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
            L_0x0040:
                boolean r5 = r2.moveToNext()     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                if (r5 == 0) goto L_0x006b
                java.lang.String r5 = r2.getString(r4)     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                r6 = 0
                android.content.Intent r5 = android.content.Intent.parseUri(r5, r6)     // Catch:{ URISyntaxException -> 0x0062 }
                boolean r5 = com.android.launcher3.Utilities.isLauncherAppTarget(r5)     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                if (r5 != 0) goto L_0x0056
                goto L_0x0040
            L_0x0056:
                long r5 = r2.getLong(r3)     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                r7 = 1
                r12.bindLong(r7, r5)     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                r12.executeUpdateDelete()     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                goto L_0x0040
            L_0x0062:
                r5 = move-exception
                java.lang.String r6 = "LauncherProvider"
                java.lang.String r7 = "Unable to parse intent"
                android.util.Log.e(r6, r7, r5)     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                goto L_0x0040
            L_0x006b:
                r0.commit()     // Catch:{ Throwable -> 0x007f, all -> 0x007c }
                if (r12 == 0) goto L_0x0073
                r12.close()     // Catch:{ Throwable -> 0x0099, all -> 0x0096 }
            L_0x0073:
                if (r2 == 0) goto L_0x0078
                r2.close()     // Catch:{ Throwable -> 0x00b2 }
            L_0x0078:
                r0.close()     // Catch:{ SQLException -> 0x00c4 }
                goto L_0x00cc
            L_0x007c:
                r3 = move-exception
                r4 = r1
                goto L_0x0085
            L_0x007f:
                r3 = move-exception
                throw r3     // Catch:{ all -> 0x0081 }
            L_0x0081:
                r4 = move-exception
                r10 = r4
                r4 = r3
                r3 = r10
            L_0x0085:
                if (r12 == 0) goto L_0x0095
                if (r4 == 0) goto L_0x0092
                r12.close()     // Catch:{ Throwable -> 0x008d, all -> 0x0096 }
                goto L_0x0095
            L_0x008d:
                r12 = move-exception
                r4.addSuppressed(r12)     // Catch:{ Throwable -> 0x0099, all -> 0x0096 }
                goto L_0x0095
            L_0x0092:
                r12.close()     // Catch:{ Throwable -> 0x0099, all -> 0x0096 }
            L_0x0095:
                throw r3     // Catch:{ Throwable -> 0x0099, all -> 0x0096 }
            L_0x0096:
                r12 = move-exception
                r3 = r1
                goto L_0x009f
            L_0x0099:
                r12 = move-exception
                throw r12     // Catch:{ all -> 0x009b }
            L_0x009b:
                r3 = move-exception
                r10 = r3
                r3 = r12
                r12 = r10
            L_0x009f:
                if (r2 == 0) goto L_0x00af
                if (r3 == 0) goto L_0x00ac
                r2.close()     // Catch:{ Throwable -> 0x00a7 }
                goto L_0x00af
            L_0x00a7:
                r2 = move-exception
                r3.addSuppressed(r2)     // Catch:{ Throwable -> 0x00b2 }
                goto L_0x00af
            L_0x00ac:
                r2.close()     // Catch:{ Throwable -> 0x00b2 }
            L_0x00af:
                throw r12     // Catch:{ Throwable -> 0x00b2 }
            L_0x00b0:
                r12 = move-exception
                goto L_0x00b5
            L_0x00b2:
                r12 = move-exception
                r1 = r12
                throw r1     // Catch:{ all -> 0x00b0 }
            L_0x00b5:
                if (r1 == 0) goto L_0x00c0
                r0.close()     // Catch:{ Throwable -> 0x00bb }
                goto L_0x00c3
            L_0x00bb:
                r0 = move-exception
                r1.addSuppressed(r0)     // Catch:{ SQLException -> 0x00c4 }
                goto L_0x00c3
            L_0x00c0:
                r0.close()     // Catch:{ SQLException -> 0x00c4 }
            L_0x00c3:
                throw r12     // Catch:{ SQLException -> 0x00c4 }
            L_0x00c4:
                r12 = move-exception
                java.lang.String r0 = "LauncherProvider"
                java.lang.String r1 = "Error deduping shortcuts"
                android.util.Log.w(r0, r1, r12)
            L_0x00cc:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.convertShortcutsToLauncherActivities(android.database.sqlite.SQLiteDatabase):void");
        }

        public boolean recreateWorkspaceTable(SQLiteDatabase sQLiteDatabase) {
            SQLiteTransaction sQLiteTransaction;
            Throwable th;
            Throwable th2;
            Throwable th3;
            try {
                sQLiteTransaction = new SQLiteTransaction(sQLiteDatabase);
                try {
                    Cursor query = sQLiteDatabase.query(WorkspaceScreens.TABLE_NAME, new String[]{"_id"}, null, null, null, null, WorkspaceScreens.SCREEN_RANK);
                    try {
                        ArrayList arrayList = new ArrayList(LauncherDbUtils.iterateCursor(query, 0, new LinkedHashSet()));
                        if (query != null) {
                            query.close();
                        }
                        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS workspaceScreens");
                        addWorkspacesTable(sQLiteDatabase, false);
                        int size = arrayList.size();
                        for (int i = 0; i < size; i++) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("_id", (Long) arrayList.get(i));
                            contentValues.put(WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                            LauncherProvider.addModifiedTime(contentValues);
                            sQLiteDatabase.insertOrThrow(WorkspaceScreens.TABLE_NAME, null, contentValues);
                        }
                        sQLiteTransaction.commit();
                        this.mMaxScreenId = arrayList.isEmpty() ? 0 : ((Long) Collections.max(arrayList)).longValue();
                        sQLiteTransaction.close();
                        return true;
                    } catch (Throwable th4) {
                        Throwable th5 = th4;
                        th3 = r13;
                        th2 = th5;
                    }
                    if (query != null) {
                        if (th3 != null) {
                            query.close();
                        } else {
                            query.close();
                        }
                    }
                    throw th2;
                    throw th2;
                    throw th;
                } catch (Throwable th6) {
                    th = th6;
                    throw th;
                }
            } catch (SQLException e) {
                Log.e(LauncherProvider.TAG, e.getMessage(), e);
                return false;
            } catch (Throwable th7) {
                th.addSuppressed(th7);
            }
        }

        /* access modifiers changed from: 0000 */
        public boolean updateFolderItemsRank(SQLiteDatabase sQLiteDatabase, boolean z) {
            SQLiteTransaction sQLiteTransaction;
            Throwable th;
            try {
                sQLiteTransaction = new SQLiteTransaction(sQLiteDatabase);
                if (z) {
                    sQLiteDatabase.execSQL("ALTER TABLE favorites ADD COLUMN rank INTEGER NOT NULL DEFAULT 0;");
                }
                Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT container, MAX(cellX) FROM favorites WHERE container IN (SELECT _id FROM favorites WHERE itemType = ?) GROUP BY container;", new String[]{Integer.toString(2)});
                while (rawQuery.moveToNext()) {
                    sQLiteDatabase.execSQL("UPDATE favorites SET rank=cellX+(cellY*?) WHERE container=? AND cellX IS NOT NULL AND cellY IS NOT NULL;", new Object[]{Long.valueOf(rawQuery.getLong(1) + 1), Long.valueOf(rawQuery.getLong(0))});
                }
                rawQuery.close();
                sQLiteTransaction.commit();
                sQLiteTransaction.close();
                return true;
            } catch (SQLException e) {
                Log.e(LauncherProvider.TAG, e.getMessage(), e);
                return false;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }

        private boolean addProfileColumn(SQLiteDatabase sQLiteDatabase) {
            return addIntegerColumn(sQLiteDatabase, Favorites.PROFILE_ID, getDefaultUserSerial());
        }

        private boolean addIntegerColumn(SQLiteDatabase sQLiteDatabase, String str, long j) {
            SQLiteTransaction sQLiteTransaction;
            Throwable th;
            try {
                sQLiteTransaction = new SQLiteTransaction(sQLiteDatabase);
                StringBuilder sb = new StringBuilder();
                sb.append("ALTER TABLE favorites ADD COLUMN ");
                sb.append(str);
                sb.append(" INTEGER NOT NULL DEFAULT ");
                sb.append(j);
                sb.append(";");
                sQLiteDatabase.execSQL(sb.toString());
                sQLiteTransaction.commit();
                sQLiteTransaction.close();
                return true;
            } catch (SQLException e) {
                Log.e(LauncherProvider.TAG, e.getMessage(), e);
                return false;
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }

        public long generateNewItemId() {
            if (this.mMaxItemId >= 0) {
                this.mMaxItemId++;
                return this.mMaxItemId;
            }
            throw new RuntimeException("Error: max item id was not initialized");
        }

        public AppWidgetHost newLauncherWidgetHost() {
            return new LauncherAppWidgetHost(this.mContext);
        }

        public long insertAndCheck(SQLiteDatabase sQLiteDatabase, ContentValues contentValues) {
            return LauncherProvider.dbInsertAndCheck(this, sQLiteDatabase, Favorites.TABLE_NAME, null, contentValues);
        }

        public void checkId(String str, ContentValues contentValues) {
            long longValue = contentValues.getAsLong("_id").longValue();
            if (WorkspaceScreens.TABLE_NAME.equals(str)) {
                this.mMaxScreenId = Math.max(longValue, this.mMaxScreenId);
            } else {
                this.mMaxItemId = Math.max(longValue, this.mMaxItemId);
            }
        }

        private long initializeMaxItemId(SQLiteDatabase sQLiteDatabase) {
            return LauncherProvider.getMaxId(sQLiteDatabase, Favorites.TABLE_NAME);
        }

        public long generateNewScreenId() {
            if (this.mMaxScreenId >= 0) {
                this.mMaxScreenId++;
                return this.mMaxScreenId;
            }
            throw new RuntimeException("Error: max screen id was not initialized");
        }

        private long initializeMaxScreenId(SQLiteDatabase sQLiteDatabase) {
            return LauncherProvider.getMaxId(sQLiteDatabase, WorkspaceScreens.TABLE_NAME);
        }

        /* access modifiers changed from: 0000 */
        public int loadFavorites(SQLiteDatabase sQLiteDatabase, AutoInstallsLayout autoInstallsLayout) {
            ArrayList arrayList = new ArrayList();
            int loadLayout = autoInstallsLayout.loadLayout(sQLiteDatabase, arrayList);
            Collections.sort(arrayList);
            ContentValues contentValues = new ContentValues();
            Iterator it = arrayList.iterator();
            int i = 0;
            while (it.hasNext()) {
                Long l = (Long) it.next();
                contentValues.clear();
                contentValues.put("_id", l);
                contentValues.put(WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                if (LauncherProvider.dbInsertAndCheck(this, sQLiteDatabase, WorkspaceScreens.TABLE_NAME, null, contentValues) >= 0) {
                    i++;
                } else {
                    throw new RuntimeException("Failed initialize screen tablefrom default layout");
                }
            }
            this.mMaxItemId = initializeMaxItemId(sQLiteDatabase);
            this.mMaxScreenId = initializeMaxScreenId(sQLiteDatabase);
            return loadLayout;
        }
    }

    static class SqlArguments {
        public final String[] args;
        public final String table;
        public final String where;

        SqlArguments(Uri uri, String str, String[] strArr) {
            if (uri.getPathSegments().size() == 1) {
                this.table = (String) uri.getPathSegments().get(0);
                this.where = str;
                this.args = strArr;
            } else if (uri.getPathSegments().size() != 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid URI: ");
                sb.append(uri);
                throw new IllegalArgumentException(sb.toString());
            } else if (TextUtils.isEmpty(str)) {
                this.table = (String) uri.getPathSegments().get(0);
                StringBuilder sb2 = new StringBuilder();
                sb2.append("_id=");
                sb2.append(ContentUris.parseId(uri));
                this.where = sb2.toString();
                this.args = null;
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("WHERE clause not supported: ");
                sb3.append(uri);
                throw new UnsupportedOperationException(sb3.toString());
            }
        }

        SqlArguments(Uri uri) {
            if (uri.getPathSegments().size() == 1) {
                this.table = (String) uri.getPathSegments().get(0);
                this.where = null;
                this.args = null;
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid URI: ");
            sb.append(uri);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildUtil.getApplicationId());
        sb.append(".settings");
        AUTHORITY = sb.toString();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate != null && instanceNoCreate.getModel().isModelLoaded()) {
            instanceNoCreate.getModel().dumpState("", fileDescriptor, printWriter, strArr);
        }
    }

    public boolean onCreate() {
        this.mListenerHandler = new Handler(this.mListenerWrapper);
        FileLog.setDir(getContext().getApplicationContext().getFilesDir());
        IconShapeOverride.apply(getContext());
        SessionCommitReceiver.applyDefaultUserPrefs(getContext());
        return true;
    }

    public void setLauncherProviderChangeListener(LauncherProviderChangeListener launcherProviderChangeListener) {
        Preconditions.assertUIThread();
        this.mListenerWrapper.mListener = launcherProviderChangeListener;
    }

    public String getType(Uri uri) {
        SqlArguments sqlArguments = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(sqlArguments.where)) {
            StringBuilder sb = new StringBuilder();
            sb.append("vnd.android.cursor.dir/");
            sb.append(sqlArguments.table);
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("vnd.android.cursor.item/");
        sb2.append(sqlArguments.table);
        return sb2.toString();
    }

    /* access modifiers changed from: protected */
    public synchronized void createDbIfNotExists() {
        if (this.mOpenHelper == null) {
            this.mOpenHelper = new DatabaseHelper(getContext(), this.mListenerHandler);
            if (RestoreDbTask.isPending(getContext())) {
                if (!RestoreDbTask.performRestore(this.mOpenHelper)) {
                    this.mOpenHelper.createEmptyDB(this.mOpenHelper.getWritableDatabase());
                }
                RestoreDbTask.setPending(getContext(), false);
            }
        }
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri, str, strArr2);
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(sqlArguments.table);
        Cursor query = sQLiteQueryBuilder.query(this.mOpenHelper.getWritableDatabase(), strArr, sqlArguments.where, sqlArguments.args, null, null, str2);
        query.setNotificationUri(getContext().getContentResolver(), uri);
        return query;
    }

    static long dbInsertAndCheck(DatabaseHelper databaseHelper, SQLiteDatabase sQLiteDatabase, String str, String str2, ContentValues contentValues) {
        if (contentValues == null) {
            throw new RuntimeException("Error: attempting to insert null values");
        } else if (contentValues.containsKey("_id")) {
            databaseHelper.checkId(str, contentValues);
            return sQLiteDatabase.insert(str, str2, contentValues);
        } else {
            throw new RuntimeException("Error: attempting to add item without specifying an id");
        }
    }

    private void reloadLauncherIfExternal() {
        if (Utilities.ATLEAST_MARSHMALLOW && Binder.getCallingPid() != Process.myPid()) {
            LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
            if (instanceNoCreate != null) {
                instanceNoCreate.getModel().forceReload();
            }
        }
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri);
        if (Binder.getCallingPid() != Process.myPid() && !initializeExternalAdd(contentValues)) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        addModifiedTime(contentValues);
        long dbInsertAndCheck = dbInsertAndCheck(this.mOpenHelper, writableDatabase, sqlArguments.table, null, contentValues);
        if (dbInsertAndCheck < 0) {
            return null;
        }
        Uri withAppendedId = ContentUris.withAppendedId(uri, dbInsertAndCheck);
        notifyListeners();
        if (Utilities.ATLEAST_MARSHMALLOW) {
            reloadLauncherIfExternal();
        } else {
            LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
            if (instanceNoCreate != null && "true".equals(withAppendedId.getQueryParameter("isExternalAdd"))) {
                instanceNoCreate.getModel().forceReload();
            }
            String queryParameter = withAppendedId.getQueryParameter("notify");
            if (queryParameter == null || "true".equals(queryParameter)) {
                getContext().getContentResolver().notifyChange(withAppendedId, null);
            }
        }
        return withAppendedId;
    }

    private boolean initializeExternalAdd(ContentValues contentValues) {
        SQLiteStatement sQLiteStatement;
        Throwable th;
        contentValues.put("_id", Long.valueOf(this.mOpenHelper.generateNewItemId()));
        Integer asInteger = contentValues.getAsInteger(BaseLauncherColumns.ITEM_TYPE);
        if (asInteger != null && asInteger.intValue() == 4 && !contentValues.containsKey(Favorites.APPWIDGET_ID)) {
            AppWidgetManager instance = AppWidgetManager.getInstance(getContext());
            ComponentName unflattenFromString = ComponentName.unflattenFromString(contentValues.getAsString(Favorites.APPWIDGET_PROVIDER));
            if (unflattenFromString == null) {
                return false;
            }
            try {
                AppWidgetHost newLauncherWidgetHost = this.mOpenHelper.newLauncherWidgetHost();
                int allocateAppWidgetId = newLauncherWidgetHost.allocateAppWidgetId();
                contentValues.put(Favorites.APPWIDGET_ID, Integer.valueOf(allocateAppWidgetId));
                if (!instance.bindAppWidgetIdIfAllowed(allocateAppWidgetId, unflattenFromString)) {
                    newLauncherWidgetHost.deleteAppWidgetId(allocateAppWidgetId);
                    return false;
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to initialize external widget", e);
                return false;
            }
        }
        long longValue = contentValues.getAsLong(Favorites.SCREEN).longValue();
        try {
            sQLiteStatement = this.mOpenHelper.getWritableDatabase().compileStatement("INSERT OR IGNORE INTO workspaceScreens (_id, screenRank) select ?, (ifnull(MAX(screenRank), -1)+1) from workspaceScreens");
            try {
                sQLiteStatement.bindLong(1, longValue);
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("_id", Long.valueOf(sQLiteStatement.executeInsert()));
                this.mOpenHelper.checkId(WorkspaceScreens.TABLE_NAME, contentValues2);
                Utilities.closeSilently(sQLiteStatement);
                return true;
            } catch (Exception unused) {
                Utilities.closeSilently(sQLiteStatement);
                return false;
            } catch (Throwable th2) {
                th = th2;
                Utilities.closeSilently(sQLiteStatement);
                throw th;
            }
        } catch (Exception unused2) {
            sQLiteStatement = null;
            Utilities.closeSilently(sQLiteStatement);
            return false;
        } catch (Throwable th3) {
            Throwable th4 = th3;
            sQLiteStatement = null;
            th = th4;
            Utilities.closeSilently(sQLiteStatement);
            throw th;
        }
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        Throwable th;
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        SQLiteTransaction sQLiteTransaction = new SQLiteTransaction(writableDatabase);
        try {
            int length = contentValuesArr.length;
            for (int i = 0; i < length; i++) {
                addModifiedTime(contentValuesArr[i]);
                if (dbInsertAndCheck(this.mOpenHelper, writableDatabase, sqlArguments.table, null, contentValuesArr[i]) < 0) {
                    sQLiteTransaction.close();
                    return 0;
                }
            }
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            notifyListeners();
            reloadLauncherIfExternal();
            return contentValuesArr.length;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> arrayList) throws OperationApplicationException {
        Throwable th;
        createDbIfNotExists();
        SQLiteTransaction sQLiteTransaction = new SQLiteTransaction(this.mOpenHelper.getWritableDatabase());
        try {
            ContentProviderResult[] applyBatch = super.applyBatch(arrayList);
            sQLiteTransaction.commit();
            reloadLauncherIfExternal();
            sQLiteTransaction.close();
            return applyBatch;
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri, str, strArr);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        if (Binder.getCallingPid() != Process.myPid() && Favorites.TABLE_NAME.equalsIgnoreCase(sqlArguments.table)) {
            this.mOpenHelper.removeGhostWidgets(this.mOpenHelper.getWritableDatabase());
        }
        int delete = writableDatabase.delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
        if (delete > 0) {
            notifyListeners();
            reloadLauncherIfExternal();
        }
        return delete;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri, str, strArr);
        addModifiedTime(contentValues);
        int update = this.mOpenHelper.getWritableDatabase().update(sqlArguments.table, contentValues, sqlArguments.where, sqlArguments.args);
        if (update > 0) {
            notifyListeners();
        }
        reloadLauncherIfExternal();
        return update;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if (Binder.getCallingUid() != Process.myUid()) {
            return null;
        }
        createDbIfNotExists();
        char c = 65535;
        switch (str.hashCode()) {
            case -1999597249:
                if (str.equals(Settings.METHOD_DELETE_EMPTY_FOLDERS)) {
                    c = 3;
                    break;
                }
                break;
            case -1565944700:
                if (str.equals(Settings.METHOD_REMOVE_GHOST_WIDGETS)) {
                    c = 8;
                    break;
                }
                break;
            case -1107339682:
                if (str.equals(Settings.METHOD_NEW_ITEM_ID)) {
                    c = 4;
                    break;
                }
                break;
            case -1029923675:
                if (str.equals(Settings.METHOD_NEW_SCREEN_ID)) {
                    c = 5;
                    break;
                }
                break;
            case -1008511191:
                if (str.equals(Settings.METHOD_CLEAR_EMPTY_DB_FLAG)) {
                    c = 1;
                    break;
                }
                break;
            case -950799388:
                if (str.equals(Settings.METHOD_SET_EXTRACTED_COLORS_AND_WALLPAPER_ID)) {
                    c = 0;
                    break;
                }
                break;
            case 476749504:
                if (str.equals(Settings.METHOD_LOAD_DEFAULT_FAVORITES)) {
                    c = 7;
                    break;
                }
                break;
            case 684076146:
                if (str.equals(Settings.METHOD_WAS_EMPTY_DB_CREATED)) {
                    c = 2;
                    break;
                }
                break;
            case 2117515411:
                if (str.equals(Settings.METHOD_CREATE_EMPTY_DB)) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                String string = bundle.getString(Settings.EXTRA_EXTRACTED_COLORS);
                Utilities.getPrefs(getContext()).edit().putString(ExtractionUtils.EXTRACTED_COLORS_PREFERENCE_KEY, string).putInt(ExtractionUtils.WALLPAPER_ID_PREFERENCE_KEY, bundle.getInt(Settings.EXTRA_WALLPAPER_ID)).apply();
                this.mListenerHandler.sendEmptyMessage(2);
                Bundle bundle2 = new Bundle();
                bundle2.putString("value", string);
                return bundle2;
            case 1:
                clearFlagEmptyDbCreated();
                return null;
            case 2:
                Bundle bundle3 = new Bundle();
                bundle3.putBoolean("value", Utilities.getPrefs(getContext()).getBoolean(EMPTY_DATABASE_CREATED, false));
                return bundle3;
            case 3:
                Bundle bundle4 = new Bundle();
                bundle4.putSerializable("value", deleteEmptyFolders());
                return bundle4;
            case 4:
                Bundle bundle5 = new Bundle();
                bundle5.putLong("value", this.mOpenHelper.generateNewItemId());
                return bundle5;
            case 5:
                Bundle bundle6 = new Bundle();
                bundle6.putLong("value", this.mOpenHelper.generateNewScreenId());
                return bundle6;
            case 6:
                this.mOpenHelper.createEmptyDB(this.mOpenHelper.getWritableDatabase());
                return null;
            case 7:
                loadDefaultFavoritesIfNecessary();
                return null;
            case 8:
                this.mOpenHelper.removeGhostWidgets(this.mOpenHelper.getWritableDatabase());
                return null;
            default:
                return null;
        }
    }

    private ArrayList<Long> deleteEmptyFolders() {
        SQLiteTransaction sQLiteTransaction;
        Throwable th;
        Throwable th2;
        Throwable th3;
        ArrayList<Long> arrayList = new ArrayList<>();
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        try {
            sQLiteTransaction = new SQLiteTransaction(writableDatabase);
            try {
                Cursor query = writableDatabase.query(Favorites.TABLE_NAME, new String[]{"_id"}, "itemType = 2 AND _id NOT IN (SELECT container FROM favorites)", null, null, null, null);
                try {
                    LauncherDbUtils.iterateCursor(query, 0, arrayList);
                    if (query != null) {
                        query.close();
                    }
                    if (!arrayList.isEmpty()) {
                        writableDatabase.delete(Favorites.TABLE_NAME, Utilities.createDbSelectionQuery("_id", arrayList), null);
                    }
                    sQLiteTransaction.commit();
                    sQLiteTransaction.close();
                    return arrayList;
                } catch (Throwable th4) {
                    Throwable th5 = th4;
                    th2 = r1;
                    th3 = th5;
                }
                throw th;
                if (query != null) {
                    if (th2 != null) {
                        query.close();
                    } else {
                        query.close();
                    }
                }
                throw th3;
                throw th3;
            } catch (Throwable th6) {
                th = th6;
                throw th;
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            arrayList.clear();
        } catch (Throwable th7) {
            th.addSuppressed(th7);
        }
    }

    /* access modifiers changed from: protected */
    public void notifyListeners() {
        this.mListenerHandler.sendEmptyMessage(1);
    }

    static void addModifiedTime(ContentValues contentValues) {
        contentValues.put(ChangeLogColumns.MODIFIED, Long.valueOf(System.currentTimeMillis()));
    }

    private void clearFlagEmptyDbCreated() {
        Utilities.getPrefs(getContext()).edit().remove(EMPTY_DATABASE_CREATED).commit();
    }

    /* JADX WARNING: type inference failed for: r1v2, types: [com.android.launcher3.AutoInstallsLayout] */
    /* JADX WARNING: type inference failed for: r1v3 */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* JADX WARNING: type inference failed for: r1v5, types: [com.android.launcher3.AutoInstallsLayout] */
    /* JADX WARNING: type inference failed for: r1v9, types: [com.android.launcher3.DefaultLayoutParser] */
    /* JADX WARNING: type inference failed for: r3v6, types: [com.android.launcher3.DefaultLayoutParser] */
    /* JADX WARNING: type inference failed for: r1v12, types: [com.android.launcher3.AutoInstallsLayout] */
    /* JADX WARNING: type inference failed for: r1v13 */
    /* JADX WARNING: type inference failed for: r3v8, types: [com.android.launcher3.DefaultLayoutParser] */
    /* JADX WARNING: type inference failed for: r1v14 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r1v2, types: [com.android.launcher3.AutoInstallsLayout]
      assigns: [com.android.launcher3.AutoInstallsLayout, com.android.launcher3.DefaultLayoutParser]
      uses: [?[int, boolean, OBJECT, ARRAY, byte, short, char], com.android.launcher3.AutoInstallsLayout, com.android.launcher3.DefaultLayoutParser]
      mth insns count: 66
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void loadDefaultFavoritesIfNecessary() {
        /*
            r9 = this;
            monitor-enter(r9)
            android.content.Context r0 = r9.getContext()     // Catch:{ all -> 0x00a7 }
            android.content.SharedPreferences r0 = com.android.launcher3.Utilities.getPrefs(r0)     // Catch:{ all -> 0x00a7 }
            java.lang.String r1 = "EMPTY_DATABASE_CREATED"
            r2 = 0
            boolean r0 = r0.getBoolean(r1, r2)     // Catch:{ all -> 0x00a7 }
            if (r0 == 0) goto L_0x00a5
            java.lang.String r0 = "LauncherProvider"
            java.lang.String r1 = "loading default workspace"
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r0 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            android.appwidget.AppWidgetHost r0 = r0.newLauncherWidgetHost()     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.AutoInstallsLayout r1 = r9.createWorkspaceLoaderFromAppRestriction(r0)     // Catch:{ all -> 0x00a7 }
            if (r1 != 0) goto L_0x002f
            android.content.Context r1 = r9.getContext()     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.AutoInstallsLayout r1 = com.android.launcher3.AutoInstallsLayout.get(r1, r0, r3)     // Catch:{ all -> 0x00a7 }
        L_0x002f:
            if (r1 != 0) goto L_0x0064
            android.content.Context r3 = r9.getContext()     // Catch:{ all -> 0x00a7 }
            android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.Partner r3 = com.android.launcher3.Partner.get(r3)     // Catch:{ all -> 0x00a7 }
            if (r3 == 0) goto L_0x0064
            boolean r4 = r3.hasDefaultLayout()     // Catch:{ all -> 0x00a7 }
            if (r4 == 0) goto L_0x0064
            android.content.res.Resources r7 = r3.getResources()     // Catch:{ all -> 0x00a7 }
            java.lang.String r4 = "partner_default_layout"
            java.lang.String r5 = "xml"
            java.lang.String r3 = r3.getPackageName()     // Catch:{ all -> 0x00a7 }
            int r8 = r7.getIdentifier(r4, r5, r3)     // Catch:{ all -> 0x00a7 }
            if (r8 == 0) goto L_0x0064
            com.android.launcher3.DefaultLayoutParser r1 = new com.android.launcher3.DefaultLayoutParser     // Catch:{ all -> 0x00a7 }
            android.content.Context r4 = r9.getContext()     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r6 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            r3 = r1
            r5 = r0
            r3.<init>(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00a7 }
        L_0x0064:
            if (r1 == 0) goto L_0x0067
            r2 = 1
        L_0x0067:
            if (r1 != 0) goto L_0x006d
            com.android.launcher3.DefaultLayoutParser r1 = r9.getDefaultLayoutParser(r0)     // Catch:{ all -> 0x00a7 }
        L_0x006d:
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r4 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            android.database.sqlite.SQLiteDatabase r4 = r4.getWritableDatabase()     // Catch:{ all -> 0x00a7 }
            r3.createEmptyDB(r4)     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r4 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            android.database.sqlite.SQLiteDatabase r4 = r4.getWritableDatabase()     // Catch:{ all -> 0x00a7 }
            int r1 = r3.loadFavorites(r4, r1)     // Catch:{ all -> 0x00a7 }
            if (r1 > 0) goto L_0x00a2
            if (r2 == 0) goto L_0x00a2
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r2 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            android.database.sqlite.SQLiteDatabase r2 = r2.getWritableDatabase()     // Catch:{ all -> 0x00a7 }
            r1.createEmptyDB(r2)     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r2 = r9.mOpenHelper     // Catch:{ all -> 0x00a7 }
            android.database.sqlite.SQLiteDatabase r2 = r2.getWritableDatabase()     // Catch:{ all -> 0x00a7 }
            com.android.launcher3.DefaultLayoutParser r0 = r9.getDefaultLayoutParser(r0)     // Catch:{ all -> 0x00a7 }
            r1.loadFavorites(r2, r0)     // Catch:{ all -> 0x00a7 }
        L_0x00a2:
            r9.clearFlagEmptyDbCreated()     // Catch:{ all -> 0x00a7 }
        L_0x00a5:
            monitor-exit(r9)
            return
        L_0x00a7:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.loadDefaultFavoritesIfNecessary():void");
    }

    private AutoInstallsLayout createWorkspaceLoaderFromAppRestriction(AppWidgetHost appWidgetHost) {
        Context context = getContext();
        Bundle applicationRestrictions = ((UserManager) context.getSystemService(ServiceManagerNative.USER)).getApplicationRestrictions(context.getPackageName());
        if (applicationRestrictions == null) {
            return null;
        }
        String string = applicationRestrictions.getString(RESTRICTION_PACKAGE_NAME);
        if (string == null) {
            return null;
        }
        try {
            return AutoInstallsLayout.get(context, string, context.getPackageManager().getResourcesForApplication(string), appWidgetHost, this.mOpenHelper);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Target package for restricted profile not found", e);
            return null;
        }
    }

    private DefaultLayoutParser getDefaultLayoutParser(AppWidgetHost appWidgetHost) {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(getContext());
        int i = idp.defaultLayoutId;
        if (UserManagerCompat.getInstance(getContext()).isDemoUser() && idp.demoModeLayoutId != 0) {
            i = idp.demoModeLayoutId;
        }
        AppWidgetHost appWidgetHost2 = appWidgetHost;
        DefaultLayoutParser defaultLayoutParser = new DefaultLayoutParser(getContext(), appWidgetHost2, this.mOpenHelper, getContext().getResources(), i);
        return defaultLayoutParser;
    }

    static long getMaxId(SQLiteDatabase sQLiteDatabase, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT MAX(_id) FROM ");
        sb.append(str);
        Cursor rawQuery = sQLiteDatabase.rawQuery(sb.toString(), null);
        long j = (rawQuery == null || !rawQuery.moveToNext()) ? -1 : rawQuery.getLong(0);
        if (rawQuery != null) {
            rawQuery.close();
        }
        if (j != -1) {
            return j;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Error: could not query max id in ");
        sb2.append(str);
        throw new RuntimeException(sb2.toString());
    }
}
