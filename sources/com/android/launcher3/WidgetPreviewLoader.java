package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.graphics.ShadowGenerator.Builder;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.SQLiteCacheHelper;
import com.android.launcher3.widget.WidgetCell;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class WidgetPreviewLoader {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetPreviewLoader";
    private final Context mContext;
    private final CacheDb mDb;
    private final IconCache mIconCache;
    private final MainThreadExecutor mMainThreadExecutor = new MainThreadExecutor();
    private final HashMap<String, long[]> mPackageVersions = new HashMap<>();
    final Set<Bitmap> mUnusedBitmaps = Collections.newSetFromMap(new WeakHashMap());
    private final UserManagerCompat mUserManager;
    private final AppWidgetManagerCompat mWidgetManager;
    final Handler mWorkerHandler;

    private static class CacheDb extends SQLiteCacheHelper {
        private static final String COLUMN_COMPONENT = "componentName";
        private static final String COLUMN_LAST_UPDATED = "lastUpdated";
        private static final String COLUMN_PACKAGE = "packageName";
        private static final String COLUMN_PREVIEW_BITMAP = "preview_bitmap";
        private static final String COLUMN_SIZE = "size";
        private static final String COLUMN_USER = "profileId";
        private static final String COLUMN_VERSION = "version";
        private static final int DB_VERSION = 9;
        private static final String TABLE_NAME = "shortcut_and_widget_previews";

        public CacheDb(Context context) {
            super(context, LauncherFiles.WIDGET_PREVIEWS_DB, 9, TABLE_NAME);
        }

        public void onCreateTable(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS shortcut_and_widget_previews (componentName TEXT NOT NULL, profileId INTEGER NOT NULL, size TEXT NOT NULL, packageName TEXT NOT NULL, lastUpdated INTEGER NOT NULL DEFAULT 0, version INTEGER NOT NULL DEFAULT 0, preview_bitmap BLOB, PRIMARY KEY (componentName, profileId, size) );");
        }
    }

    public class PreviewLoadTask extends AsyncTask<Void, Void, Bitmap> implements OnCancelListener {
        private final BaseActivity mActivity = BaseActivity.fromContext(this.mCaller.getContext());
        private final boolean mAnimatePreviewIn;
        Bitmap mBitmapToRecycle;
        private final WidgetCell mCaller;
        private final WidgetItem mInfo;
        final WidgetCacheKey mKey;
        private final int mPreviewHeight;
        private final int mPreviewWidth;
        long[] mVersions;

        PreviewLoadTask(WidgetCacheKey widgetCacheKey, WidgetItem widgetItem, int i, int i2, WidgetCell widgetCell, boolean z) {
            this.mKey = widgetCacheKey;
            this.mInfo = widgetItem;
            this.mPreviewHeight = i2;
            this.mPreviewWidth = i;
            this.mCaller = widgetCell;
            this.mAnimatePreviewIn = z;
        }

        /* access modifiers changed from: protected */
        public Bitmap doInBackground(Void... voidArr) {
            Bitmap bitmap;
            long[] jArr = null;
            if (isCancelled()) {
                return null;
            }
            synchronized (WidgetPreviewLoader.this.mUnusedBitmaps) {
                Iterator it = WidgetPreviewLoader.this.mUnusedBitmaps.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        bitmap = null;
                        break;
                    }
                    bitmap = (Bitmap) it.next();
                    if (bitmap != null && bitmap.isMutable() && bitmap.getWidth() == this.mPreviewWidth && bitmap.getHeight() == this.mPreviewHeight) {
                        WidgetPreviewLoader.this.mUnusedBitmaps.remove(bitmap);
                        break;
                    }
                }
            }
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(this.mPreviewWidth, this.mPreviewHeight, Config.ARGB_8888);
            }
            Bitmap bitmap2 = bitmap;
            if (isCancelled()) {
                return bitmap2;
            }
            Bitmap readFromDb = WidgetPreviewLoader.this.readFromDb(this.mKey, bitmap2, this);
            if (!isCancelled() && readFromDb == null) {
                if (this.mInfo.activityInfo == null || this.mInfo.activityInfo.isPersistable()) {
                    jArr = WidgetPreviewLoader.this.getPackageVersion(this.mKey.componentName.getPackageName());
                }
                this.mVersions = jArr;
                readFromDb = WidgetPreviewLoader.this.generatePreview(this.mActivity, this.mInfo, bitmap2, this.mPreviewWidth, this.mPreviewHeight);
            }
            return readFromDb;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(final Bitmap bitmap) {
            this.mCaller.applyPreview(bitmap, this.mAnimatePreviewIn);
            if (this.mVersions != null) {
                WidgetPreviewLoader.this.mWorkerHandler.post(new Runnable() {
                    public void run() {
                        if (!PreviewLoadTask.this.isCancelled()) {
                            WidgetPreviewLoader.this.writeToDb(PreviewLoadTask.this.mKey, PreviewLoadTask.this.mVersions, bitmap);
                            PreviewLoadTask.this.mBitmapToRecycle = bitmap;
                            return;
                        }
                        synchronized (WidgetPreviewLoader.this.mUnusedBitmaps) {
                            WidgetPreviewLoader.this.mUnusedBitmaps.add(bitmap);
                        }
                    }
                });
            } else {
                this.mBitmapToRecycle = bitmap;
            }
        }

        /* access modifiers changed from: protected */
        public void onCancelled(final Bitmap bitmap) {
            if (bitmap != null) {
                WidgetPreviewLoader.this.mWorkerHandler.post(new Runnable() {
                    public void run() {
                        synchronized (WidgetPreviewLoader.this.mUnusedBitmaps) {
                            WidgetPreviewLoader.this.mUnusedBitmaps.add(bitmap);
                        }
                    }
                });
            }
        }

        public void onCancel() {
            cancel(true);
            if (this.mBitmapToRecycle != null) {
                WidgetPreviewLoader.this.mWorkerHandler.post(new Runnable() {
                    public void run() {
                        synchronized (WidgetPreviewLoader.this.mUnusedBitmaps) {
                            WidgetPreviewLoader.this.mUnusedBitmaps.add(PreviewLoadTask.this.mBitmapToRecycle);
                        }
                        PreviewLoadTask.this.mBitmapToRecycle = null;
                    }
                });
            }
        }
    }

    private static final class WidgetCacheKey extends ComponentKey {
        final String size;

        public WidgetCacheKey(ComponentName componentName, UserHandle userHandle, String str) {
            super(componentName, userHandle);
            this.size = str;
        }

        public int hashCode() {
            return super.hashCode() ^ this.size.hashCode();
        }

        public boolean equals(Object obj) {
            return super.equals(obj) && ((WidgetCacheKey) obj).size.equals(this.size);
        }
    }

    public WidgetPreviewLoader(Context context, IconCache iconCache) {
        this.mContext = context;
        this.mIconCache = iconCache;
        this.mWidgetManager = AppWidgetManagerCompat.getInstance(context);
        this.mUserManager = UserManagerCompat.getInstance(context);
        this.mDb = new CacheDb(context);
        this.mWorkerHandler = new Handler(LauncherModel.getWorkerLooper());
    }

    public CancellationSignal getPreview(WidgetItem widgetItem, int i, int i2, WidgetCell widgetCell, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append("x");
        sb.append(i2);
        PreviewLoadTask previewLoadTask = new PreviewLoadTask(new WidgetCacheKey(widgetItem.componentName, widgetItem.user, sb.toString()), widgetItem, i, i2, widgetCell, z);
        previewLoadTask.executeOnExecutor(Utilities.THREAD_POOL_EXECUTOR, new Void[0]);
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(previewLoadTask);
        return cancellationSignal;
    }

    /* access modifiers changed from: 0000 */
    public void writeToDb(WidgetCacheKey widgetCacheKey, long[] jArr, Bitmap bitmap) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("componentName", widgetCacheKey.componentName.flattenToShortString());
        contentValues.put(Favorites.PROFILE_ID, Long.valueOf(this.mUserManager.getSerialNumberForUser(widgetCacheKey.user)));
        contentValues.put("size", widgetCacheKey.size);
        contentValues.put("packageName", widgetCacheKey.componentName.getPackageName());
        contentValues.put("version", Long.valueOf(jArr[0]));
        contentValues.put("lastUpdated", Long.valueOf(jArr[1]));
        contentValues.put("preview_bitmap", Utilities.flattenBitmap(bitmap));
        this.mDb.insertOrReplace(contentValues);
    }

    public void removePackage(String str, UserHandle userHandle) {
        removePackage(str, userHandle, this.mUserManager.getSerialNumberForUser(userHandle));
    }

    private void removePackage(String str, UserHandle userHandle, long j) {
        synchronized (this.mPackageVersions) {
            this.mPackageVersions.remove(str);
        }
        this.mDb.delete("packageName = ? AND profileId = ?", new String[]{str, Long.toString(j)});
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:74:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeObsoletePreviews(java.util.ArrayList<? extends com.android.launcher3.util.ComponentKey> r19, @android.support.annotation.Nullable com.android.launcher3.util.PackageUserKey r20) {
        /*
            r18 = this;
            r1 = r18
            r0 = r20
            com.android.launcher3.util.Preconditions.assertWorkerThread()
            android.util.LongSparseArray r2 = new android.util.LongSparseArray
            r2.<init>()
            java.util.Iterator r3 = r19.iterator()
        L_0x0010:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x003e
            java.lang.Object r4 = r3.next()
            com.android.launcher3.util.ComponentKey r4 = (com.android.launcher3.util.ComponentKey) r4
            com.android.launcher3.compat.UserManagerCompat r5 = r1.mUserManager
            android.os.UserHandle r6 = r4.user
            long r5 = r5.getSerialNumberForUser(r6)
            java.lang.Object r7 = r2.get(r5)
            java.util.HashSet r7 = (java.util.HashSet) r7
            if (r7 != 0) goto L_0x0034
            java.util.HashSet r7 = new java.util.HashSet
            r7.<init>()
            r2.put(r5, r7)
        L_0x0034:
            android.content.ComponentName r4 = r4.componentName
            java.lang.String r4 = r4.getPackageName()
            r7.add(r4)
            goto L_0x0010
        L_0x003e:
            android.util.LongSparseArray r3 = new android.util.LongSparseArray
            r3.<init>()
            if (r0 != 0) goto L_0x0048
            r4 = 0
            goto L_0x0050
        L_0x0048:
            com.android.launcher3.compat.UserManagerCompat r4 = r1.mUserManager
            android.os.UserHandle r5 = r0.mUser
            long r4 = r4.getSerialNumberForUser(r5)
        L_0x0050:
            r6 = 0
            com.android.launcher3.WidgetPreviewLoader$CacheDb r7 = r1.mDb     // Catch:{ SQLException -> 0x00fb }
            java.lang.String r8 = "profileId"
            java.lang.String r9 = "packageName"
            java.lang.String r10 = "lastUpdated"
            java.lang.String r11 = "version"
            java.lang.String[] r8 = new java.lang.String[]{r8, r9, r10, r11}     // Catch:{ SQLException -> 0x00fb }
            android.database.Cursor r7 = r7.query(r8, r6, r6)     // Catch:{ SQLException -> 0x00fb }
        L_0x0063:
            boolean r6 = r7.moveToNext()     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            r8 = 0
            if (r6 == 0) goto L_0x00c0
            long r9 = r7.getLong(r8)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            r6 = 1
            java.lang.String r11 = r7.getString(r6)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            r12 = 2
            long r12 = r7.getLong(r12)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            r14 = 3
            long r14 = r7.getLong(r14)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            if (r0 == 0) goto L_0x008c
            java.lang.String r6 = r0.mPackageName     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            boolean r6 = r11.equals(r6)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            if (r6 == 0) goto L_0x0063
            int r6 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x008c
            goto L_0x0063
        L_0x008c:
            java.lang.Object r6 = r2.get(r9)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            java.util.HashSet r6 = (java.util.HashSet) r6     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            if (r6 == 0) goto L_0x00ac
            boolean r6 = r6.contains(r11)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            if (r6 == 0) goto L_0x00ac
            long[] r6 = r1.getPackageVersion(r11)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            r16 = r6[r8]     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            int r8 = (r16 > r14 ? 1 : (r16 == r14 ? 0 : -1))
            if (r8 != 0) goto L_0x00ac
            r8 = 1
            r14 = r6[r8]     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            int r6 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r6 != 0) goto L_0x00ac
            goto L_0x0063
        L_0x00ac:
            java.lang.Object r6 = r3.get(r9)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            java.util.HashSet r6 = (java.util.HashSet) r6     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            if (r6 != 0) goto L_0x00bc
            java.util.HashSet r6 = new java.util.HashSet     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            r6.<init>()     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            r3.put(r9, r6)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
        L_0x00bc:
            r6.add(r11)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            goto L_0x0063
        L_0x00c0:
            int r0 = r3.size()     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            if (r8 >= r0) goto L_0x00ed
            long r4 = r3.keyAt(r8)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            com.android.launcher3.compat.UserManagerCompat r0 = r1.mUserManager     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            android.os.UserHandle r0 = r0.getUserForSerialNumber(r4)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            java.lang.Object r2 = r3.valueAt(r8)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            java.util.HashSet r2 = (java.util.HashSet) r2     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
        L_0x00da:
            boolean r6 = r2.hasNext()     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            if (r6 == 0) goto L_0x00ea
            java.lang.Object r6 = r2.next()     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            r1.removePackage(r6, r0, r4)     // Catch:{ SQLException -> 0x00f5, all -> 0x00f3 }
            goto L_0x00da
        L_0x00ea:
            int r8 = r8 + 1
            goto L_0x00c0
        L_0x00ed:
            if (r7 == 0) goto L_0x0108
            r7.close()
            goto L_0x0108
        L_0x00f3:
            r0 = move-exception
            goto L_0x0109
        L_0x00f5:
            r0 = move-exception
            r6 = r7
            goto L_0x00fc
        L_0x00f8:
            r0 = move-exception
            r7 = r6
            goto L_0x0109
        L_0x00fb:
            r0 = move-exception
        L_0x00fc:
            java.lang.String r2 = "WidgetPreviewLoader"
            java.lang.String r3 = "Error updating widget previews"
            android.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x00f8 }
            if (r6 == 0) goto L_0x0108
            r6.close()
        L_0x0108:
            return
        L_0x0109:
            if (r7 == 0) goto L_0x010e
            r7.close()
        L_0x010e:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.WidgetPreviewLoader.removeObsoletePreviews(java.util.ArrayList, com.android.launcher3.util.PackageUserKey):void");
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0064, code lost:
        if (r10 != null) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0074, code lost:
        if (r10 != null) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0076, code lost:
        r10.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0079, code lost:
        return null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x007e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap readFromDb(com.android.launcher3.WidgetPreviewLoader.WidgetCacheKey r10, android.graphics.Bitmap r11, com.android.launcher3.WidgetPreviewLoader.PreviewLoadTask r12) {
        /*
            r9 = this;
            r0 = 0
            com.android.launcher3.WidgetPreviewLoader$CacheDb r1 = r9.mDb     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            java.lang.String r2 = "preview_bitmap"
            java.lang.String[] r2 = new java.lang.String[]{r2}     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            java.lang.String r3 = "componentName = ? AND profileId = ? AND size = ?"
            r4 = 3
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            android.content.ComponentName r5 = r10.componentName     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            java.lang.String r5 = r5.flattenToShortString()     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            r6 = 0
            r4[r6] = r5     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            r5 = 1
            com.android.launcher3.compat.UserManagerCompat r7 = r9.mUserManager     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            android.os.UserHandle r8 = r10.user     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            long r7 = r7.getSerialNumberForUser(r8)     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            java.lang.String r7 = java.lang.Long.toString(r7)     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            r4[r5] = r7     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            r5 = 2
            java.lang.String r10 = r10.size     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            r4[r5] = r10     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            android.database.Cursor r10 = r1.query(r2, r3, r4)     // Catch:{ SQLException -> 0x006b, all -> 0x0069 }
            boolean r1 = r12.isCancelled()     // Catch:{ SQLException -> 0x0067 }
            if (r1 == 0) goto L_0x003b
            if (r10 == 0) goto L_0x003a
            r10.close()
        L_0x003a:
            return r0
        L_0x003b:
            boolean r1 = r10.moveToNext()     // Catch:{ SQLException -> 0x0067 }
            if (r1 == 0) goto L_0x0064
            byte[] r1 = r10.getBlob(r6)     // Catch:{ SQLException -> 0x0067 }
            android.graphics.BitmapFactory$Options r2 = new android.graphics.BitmapFactory$Options     // Catch:{ SQLException -> 0x0067 }
            r2.<init>()     // Catch:{ SQLException -> 0x0067 }
            r2.inBitmap = r11     // Catch:{ SQLException -> 0x0067 }
            boolean r11 = r12.isCancelled()     // Catch:{ Exception -> 0x005d }
            if (r11 != 0) goto L_0x0064
            int r11 = r1.length     // Catch:{ Exception -> 0x005d }
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeByteArray(r1, r6, r11, r2)     // Catch:{ Exception -> 0x005d }
            if (r10 == 0) goto L_0x005c
            r10.close()
        L_0x005c:
            return r11
        L_0x005d:
            if (r10 == 0) goto L_0x0063
            r10.close()
        L_0x0063:
            return r0
        L_0x0064:
            if (r10 == 0) goto L_0x0079
            goto L_0x0076
        L_0x0067:
            r11 = move-exception
            goto L_0x006d
        L_0x0069:
            r11 = move-exception
            goto L_0x007c
        L_0x006b:
            r11 = move-exception
            r10 = r0
        L_0x006d:
            java.lang.String r12 = "WidgetPreviewLoader"
            java.lang.String r1 = "Error loading preview from DB"
            android.util.Log.w(r12, r1, r11)     // Catch:{ all -> 0x007a }
            if (r10 == 0) goto L_0x0079
        L_0x0076:
            r10.close()
        L_0x0079:
            return r0
        L_0x007a:
            r11 = move-exception
            r0 = r10
        L_0x007c:
            if (r0 == 0) goto L_0x0081
            r0.close()
        L_0x0081:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.WidgetPreviewLoader.readFromDb(com.android.launcher3.WidgetPreviewLoader$WidgetCacheKey, android.graphics.Bitmap, com.android.launcher3.WidgetPreviewLoader$PreviewLoadTask):android.graphics.Bitmap");
    }

    /* access modifiers changed from: private */
    public Bitmap generatePreview(BaseActivity baseActivity, WidgetItem widgetItem, Bitmap bitmap, int i, int i2) {
        if (widgetItem.widgetInfo != null) {
            return generateWidgetPreview(baseActivity, widgetItem.widgetInfo, i, bitmap, null);
        }
        return generateShortcutPreview(baseActivity, widgetItem.activityInfo, i, i2, bitmap);
    }

    public Bitmap generateWidgetPreview(BaseActivity baseActivity, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo, int i, Bitmap bitmap, int[] iArr) {
        Drawable drawable;
        int i2;
        int i3;
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo2 = launcherAppWidgetProviderInfo;
        Bitmap bitmap2 = bitmap;
        int i4 = i < 0 ? Integer.MAX_VALUE : i;
        if (launcherAppWidgetProviderInfo2.previewImage != 0) {
            try {
                drawable = launcherAppWidgetProviderInfo2.loadPreviewImage(this.mContext, 0);
            } catch (OutOfMemoryError e) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Error loading widget preview for: ");
                sb.append(launcherAppWidgetProviderInfo2.provider);
                Log.w(str, sb.toString(), e);
                drawable = null;
            }
            if (drawable != null) {
                drawable = mutateOnMainThread(drawable);
            } else {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Can't load widget preview drawable 0x");
                sb2.append(Integer.toHexString(launcherAppWidgetProviderInfo2.previewImage));
                sb2.append(" for provider: ");
                sb2.append(launcherAppWidgetProviderInfo2.provider);
                Log.w(str2, sb2.toString());
            }
        } else {
            drawable = null;
        }
        boolean z = drawable != null;
        int i5 = launcherAppWidgetProviderInfo2.spanX;
        int i6 = launcherAppWidgetProviderInfo2.spanY;
        if (!z || drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            DeviceProfile deviceProfile = baseActivity.getDeviceProfile();
            int min = Math.min(deviceProfile.cellWidthPx, deviceProfile.cellHeightPx);
            i2 = min * i6;
            i3 = min * i5;
        } else {
            i3 = drawable.getIntrinsicWidth();
            i2 = drawable.getIntrinsicHeight();
        }
        if (iArr != null) {
            iArr[0] = i3;
        }
        float f = i3 > i4 ? ((float) i4) / ((float) i3) : 1.0f;
        if (f != 1.0f) {
            i3 = Math.max((int) (((float) i3) * f), 1);
            i2 = Math.max((int) (((float) i2) * f), 1);
        }
        Canvas canvas = new Canvas();
        if (bitmap2 == null) {
            bitmap2 = Bitmap.createBitmap(i3, i2, Config.ARGB_8888);
            canvas.setBitmap(bitmap2);
        } else {
            if (bitmap.getHeight() > i2) {
                bitmap2.reconfigure(bitmap.getWidth(), i2, bitmap.getConfig());
            }
            canvas.setBitmap(bitmap2);
            canvas.drawColor(0, Mode.CLEAR);
        }
        int width = (bitmap2.getWidth() - i3) / 2;
        if (z) {
            drawable.setBounds(width, 0, i3 + width, i2);
            drawable.draw(canvas);
        } else {
            RectF drawBoxWithShadow = drawBoxWithShadow(canvas, i3, i2);
            Paint paint = new Paint(1);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(this.mContext.getResources().getDimension(C0622R.dimen.widget_preview_cell_divider_width));
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            float width2 = drawBoxWithShadow.width() / ((float) i5);
            float f2 = drawBoxWithShadow.left;
            int i7 = 1;
            while (i7 < i5) {
                float f3 = f2 + width2;
                canvas.drawLine(f3, 0.0f, f3, (float) i2, paint);
                i7++;
                f2 = f3;
            }
            float f4 = drawBoxWithShadow.top;
            float height = drawBoxWithShadow.height() / ((float) i6);
            for (int i8 = 1; i8 < i6; i8++) {
                f4 += height;
                canvas.drawLine(0.0f, f4, (float) i3, f4, paint);
            }
            try {
                Drawable icon = launcherAppWidgetProviderInfo2.getIcon(baseActivity, this.mIconCache);
                if (icon != null) {
                    int min2 = (int) Math.min(((float) baseActivity.getDeviceProfile().iconSizePx) * f, Math.min(drawBoxWithShadow.width(), drawBoxWithShadow.height()));
                    Drawable mutateOnMainThread = mutateOnMainThread(icon);
                    int i9 = (i3 - min2) / 2;
                    int i10 = (i2 - min2) / 2;
                    mutateOnMainThread.setBounds(i9, i10, i9 + min2, min2 + i10);
                    mutateOnMainThread.draw(canvas);
                }
            } catch (NotFoundException unused) {
            }
            canvas.setBitmap(null);
        }
        return bitmap2;
    }

    private RectF drawBoxWithShadow(Canvas canvas, int i, int i2) {
        Resources resources = this.mContext.getResources();
        Builder builder = new Builder(-1);
        builder.shadowBlur = resources.getDimension(C0622R.dimen.widget_preview_shadow_blur);
        builder.radius = resources.getDimension(C0622R.dimen.widget_preview_corner_radius);
        builder.keyShadowDistance = resources.getDimension(C0622R.dimen.widget_preview_key_shadow_distance);
        builder.bounds.set(builder.shadowBlur, builder.shadowBlur, ((float) i) - builder.shadowBlur, (((float) i2) - builder.shadowBlur) - builder.keyShadowDistance);
        builder.drawShadow(canvas);
        return builder.bounds;
    }

    private Bitmap generateShortcutPreview(BaseActivity baseActivity, ShortcutConfigActivityInfo shortcutConfigActivityInfo, int i, int i2, Bitmap bitmap) {
        int i3 = baseActivity.getDeviceProfile().iconSizePx;
        int dimensionPixelSize = baseActivity.getResources().getDimensionPixelSize(C0622R.dimen.widget_preview_shortcut_padding);
        int i4 = (dimensionPixelSize * 2) + i3;
        if (i2 < i4 || i < i4) {
            throw new RuntimeException("Max size is too small for preview");
        }
        Canvas canvas = new Canvas();
        if (bitmap == null || bitmap.getWidth() < i4 || bitmap.getHeight() < i4) {
            bitmap = Bitmap.createBitmap(i4, i4, Config.ARGB_8888);
            canvas.setBitmap(bitmap);
        } else {
            if (bitmap.getWidth() > i4 || bitmap.getHeight() > i4) {
                bitmap.reconfigure(i4, i4, bitmap.getConfig());
            }
            canvas.setBitmap(bitmap);
            canvas.drawColor(0, Mode.CLEAR);
        }
        RectF drawBoxWithShadow = drawBoxWithShadow(canvas, i4, i4);
        Bitmap createScaledBitmapWithoutShadow = LauncherIcons.createScaledBitmapWithoutShadow(mutateOnMainThread(shortcutConfigActivityInfo.getFullResIcon(this.mIconCache)), this.mContext, 0);
        Rect rect = new Rect(0, 0, createScaledBitmapWithoutShadow.getWidth(), createScaledBitmapWithoutShadow.getHeight());
        float f = (float) i3;
        drawBoxWithShadow.set(0.0f, 0.0f, f, f);
        float f2 = (float) dimensionPixelSize;
        drawBoxWithShadow.offset(f2, f2);
        canvas.drawBitmap(createScaledBitmapWithoutShadow, rect, drawBoxWithShadow, new Paint(3));
        canvas.setBitmap(null);
        return bitmap;
    }

    private Drawable mutateOnMainThread(final Drawable drawable) {
        try {
            return (Drawable) this.mMainThreadExecutor.submit(new Callable<Drawable>() {
                public Drawable call() throws Exception {
                    return drawable.mutate();
                }
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e2) {
            throw new RuntimeException(e2);
        }
    }

    /* access modifiers changed from: 0000 */
    public long[] getPackageVersion(String str) {
        long[] jArr;
        synchronized (this.mPackageVersions) {
            jArr = (long[]) this.mPackageVersions.get(str);
            if (jArr == null) {
                jArr = new long[2];
                try {
                    PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(str, 8192);
                    jArr[0] = (long) packageInfo.versionCode;
                    jArr[1] = packageInfo.lastUpdateTime;
                } catch (NameNotFoundException e) {
                    Log.e(TAG, "PackageInfo not found", e);
                }
                this.mPackageVersions.put(str, jArr);
            }
        }
        return jArr;
    }
}
