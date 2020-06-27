package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import com.android.launcher3.IconCache;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.util.ContentWriter.CommitParams;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.PackageManagerHelper;
import com.microsoft.appcenter.Constants;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class LoaderCursor extends CursorWrapper {
    private static final String TAG = "LoaderCursor";
    public final LongSparseArray<UserHandle> allUsers = new LongSparseArray<>();
    private final int cellXIndex;
    private final int cellYIndex;
    public long container;
    private final int containerIndex;
    private final int iconIndex;
    private final int iconPackageIndex;
    private final int iconResourceIndex;

    /* renamed from: id */
    public long f66id;
    private final int idIndex;
    private final int intentIndex;
    public int itemType;
    private final int itemTypeIndex;
    private final ArrayList<Long> itemsToRemove = new ArrayList<>();
    private final Context mContext;
    private final InvariantDeviceProfile mIDP;
    private final IconCache mIconCache;
    private final UserManagerCompat mUserManager;
    private final LongArrayMap<GridOccupancy> occupied = new LongArrayMap<>();
    private final int profileIdIndex;
    public int restoreFlag;
    private final int restoredIndex;
    private final ArrayList<Long> restoredRows = new ArrayList<>();
    private final int screenIndex;
    public long serialNumber;
    public final int titleIndex;
    public UserHandle user;

    public LoaderCursor(Cursor cursor, LauncherAppState launcherAppState) {
        super(cursor);
        this.mContext = launcherAppState.getContext();
        this.mIconCache = launcherAppState.getIconCache();
        this.mIDP = launcherAppState.getInvariantDeviceProfile();
        this.mUserManager = UserManagerCompat.getInstance(this.mContext);
        this.iconIndex = getColumnIndexOrThrow(BaseLauncherColumns.ICON);
        this.iconPackageIndex = getColumnIndexOrThrow(BaseLauncherColumns.ICON_PACKAGE);
        this.iconResourceIndex = getColumnIndexOrThrow(BaseLauncherColumns.ICON_RESOURCE);
        this.titleIndex = getColumnIndexOrThrow(BaseLauncherColumns.TITLE);
        this.idIndex = getColumnIndexOrThrow("_id");
        this.containerIndex = getColumnIndexOrThrow(Favorites.CONTAINER);
        this.itemTypeIndex = getColumnIndexOrThrow(BaseLauncherColumns.ITEM_TYPE);
        this.screenIndex = getColumnIndexOrThrow(Favorites.SCREEN);
        this.cellXIndex = getColumnIndexOrThrow(Favorites.CELLX);
        this.cellYIndex = getColumnIndexOrThrow(Favorites.CELLY);
        this.profileIdIndex = getColumnIndexOrThrow(Favorites.PROFILE_ID);
        this.restoredIndex = getColumnIndexOrThrow(Favorites.RESTORED);
        this.intentIndex = getColumnIndexOrThrow(BaseLauncherColumns.INTENT);
    }

    public boolean moveToNext() {
        boolean moveToNext = super.moveToNext();
        if (moveToNext) {
            this.itemType = getInt(this.itemTypeIndex);
            this.container = (long) getInt(this.containerIndex);
            this.f66id = getLong(this.idIndex);
            this.serialNumber = (long) getInt(this.profileIdIndex);
            this.user = (UserHandle) this.allUsers.get(this.serialNumber);
            this.restoreFlag = getInt(this.restoredIndex);
        }
        return moveToNext;
    }

    public Intent parseIntent() {
        Intent intent;
        String string = getString(this.intentIndex);
        try {
            if (TextUtils.isEmpty(string)) {
                intent = null;
            } else {
                intent = Intent.parseUri(string, 0);
            }
            return intent;
        } catch (URISyntaxException unused) {
            Log.e(TAG, "Error parsing Intent");
            return null;
        }
    }

    public ShortcutInfo loadSimpleShortcut() {
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.user = this.user;
        shortcutInfo.itemType = this.itemType;
        shortcutInfo.title = getTitle();
        shortcutInfo.iconBitmap = loadIcon(shortcutInfo);
        if (shortcutInfo.iconBitmap == null) {
            shortcutInfo.iconBitmap = this.mIconCache.getDefaultIcon(shortcutInfo.user);
        }
        return shortcutInfo;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0039  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.Bitmap loadIcon(com.android.launcher3.ShortcutInfo r6) {
        /*
            r5 = this;
            int r0 = r5.itemType
            r1 = 0
            r2 = 1
            if (r0 != r2) goto L_0x0036
            int r0 = r5.iconPackageIndex
            java.lang.String r0 = r5.getString(r0)
            int r2 = r5.iconResourceIndex
            java.lang.String r2 = r5.getString(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 == 0) goto L_0x001e
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0036
        L_0x001e:
            android.content.Intent$ShortcutIconResource r3 = new android.content.Intent$ShortcutIconResource
            r3.<init>()
            r6.iconResource = r3
            android.content.Intent$ShortcutIconResource r3 = r6.iconResource
            r3.packageName = r0
            android.content.Intent$ShortcutIconResource r0 = r6.iconResource
            r0.resourceName = r2
            android.content.Intent$ShortcutIconResource r0 = r6.iconResource
            android.content.Context r2 = r5.mContext
            android.graphics.Bitmap r0 = com.android.launcher3.graphics.LauncherIcons.createIconBitmap(r0, r2)
            goto L_0x0037
        L_0x0036:
            r0 = r1
        L_0x0037:
            if (r0 != 0) goto L_0x0064
            int r0 = r5.iconIndex
            byte[] r0 = r5.getBlob(r0)
            r2 = 0
            int r3 = r0.length     // Catch:{ Exception -> 0x004c }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeByteArray(r0, r2, r3)     // Catch:{ Exception -> 0x004c }
            android.content.Context r2 = r5.mContext     // Catch:{ Exception -> 0x004c }
            android.graphics.Bitmap r0 = com.android.launcher3.graphics.LauncherIcons.createIconBitmap(r0, r2)     // Catch:{ Exception -> 0x004c }
            goto L_0x0064
        L_0x004c:
            r0 = move-exception
            java.lang.String r2 = "LoaderCursor"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Failed to load icon for info "
            r3.append(r4)
            r3.append(r6)
            java.lang.String r6 = r3.toString()
            android.util.Log.e(r2, r6, r0)
            return r1
        L_0x0064:
            if (r0 != 0) goto L_0x007c
            java.lang.String r1 = "LoaderCursor"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed to load icon for info "
            r2.append(r3)
            r2.append(r6)
            java.lang.String r6 = r2.toString()
            android.util.Log.e(r1, r6)
        L_0x007c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderCursor.loadIcon(com.android.launcher3.ShortcutInfo):android.graphics.Bitmap");
    }

    private String getTitle() {
        String string = getString(this.titleIndex);
        return TextUtils.isEmpty(string) ? "" : Utilities.trim(string);
    }

    public ShortcutInfo getRestoredItemInfo(Intent intent) {
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.user = this.user;
        shortcutInfo.intent = intent;
        shortcutInfo.iconBitmap = loadIcon(shortcutInfo);
        if (shortcutInfo.iconBitmap == null) {
            this.mIconCache.getTitleAndIcon(shortcutInfo, false);
        }
        if (hasRestoreFlag(1)) {
            String title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                shortcutInfo.title = Utilities.trim(title);
            }
        } else if (!hasRestoreFlag(2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid restoreType ");
            sb.append(this.restoreFlag);
            throw new InvalidParameterException(sb.toString());
        } else if (TextUtils.isEmpty(shortcutInfo.title)) {
            shortcutInfo.title = getTitle();
        }
        shortcutInfo.contentDescription = this.mUserManager.getBadgedLabelForUser(shortcutInfo.title, shortcutInfo.user);
        shortcutInfo.itemType = this.itemType;
        shortcutInfo.status = this.restoreFlag;
        return shortcutInfo;
    }

    public ShortcutInfo getAppShortcutInfo(Intent intent, boolean z, boolean z2) {
        if (this.user == null) {
            Log.d(TAG, "Null user found in getShortcutInfo");
            return null;
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            Log.d(TAG, "Missing component found in getShortcutInfo");
            return null;
        }
        Intent intent2 = new Intent("android.intent.action.MAIN", null);
        intent2.addCategory("android.intent.category.LAUNCHER");
        intent2.setComponent(component);
        LauncherActivityInfo resolveActivity = LauncherAppsCompat.getInstance(this.mContext).resolveActivity(intent2, this.user);
        if (resolveActivity != null || z) {
            ShortcutInfo shortcutInfo = new ShortcutInfo();
            shortcutInfo.itemType = 0;
            shortcutInfo.user = this.user;
            shortcutInfo.intent = intent2;
            this.mIconCache.getTitleAndIcon(shortcutInfo, resolveActivity, z2);
            if (this.mIconCache.isDefaultIcon(shortcutInfo.iconBitmap, this.user)) {
                Bitmap loadIcon = loadIcon(shortcutInfo);
                if (loadIcon == null) {
                    loadIcon = shortcutInfo.iconBitmap;
                }
                shortcutInfo.iconBitmap = loadIcon;
            }
            if (resolveActivity != null && PackageManagerHelper.isAppSuspended(resolveActivity.getApplicationInfo())) {
                shortcutInfo.isDisabled = 4;
            }
            if (TextUtils.isEmpty(shortcutInfo.title)) {
                shortcutInfo.title = getTitle();
            }
            if (shortcutInfo.title == null) {
                shortcutInfo.title = component.getClassName();
            }
            shortcutInfo.contentDescription = this.mUserManager.getBadgedLabelForUser(shortcutInfo.title, shortcutInfo.user);
            return shortcutInfo;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Missing activity found in getShortcutInfo: ");
        sb.append(component);
        Log.d(str, sb.toString());
        return null;
    }

    public ContentWriter updater() {
        return new ContentWriter(this.mContext, new CommitParams("_id= ?", new String[]{Long.toString(this.f66id)}));
    }

    public void markDeleted(String str) {
        FileLog.m13e(TAG, str);
        this.itemsToRemove.add(Long.valueOf(this.f66id));
    }

    public boolean commitDeleted() {
        if (this.itemsToRemove.size() <= 0) {
            return false;
        }
        this.mContext.getContentResolver().delete(Favorites.CONTENT_URI, Utilities.createDbSelectionQuery("_id", this.itemsToRemove), null);
        return true;
    }

    public void markRestored() {
        if (this.restoreFlag != 0) {
            this.restoredRows.add(Long.valueOf(this.f66id));
            this.restoreFlag = 0;
        }
    }

    public boolean hasRestoreFlag(int i) {
        return (i & this.restoreFlag) != 0;
    }

    public void commitRestoredItems() {
        if (this.restoredRows.size() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Favorites.RESTORED, Integer.valueOf(0));
            this.mContext.getContentResolver().update(Favorites.CONTENT_URI, contentValues, Utilities.createDbSelectionQuery("_id", this.restoredRows), null);
        }
    }

    public boolean isOnWorkspaceOrHotseat() {
        return this.container == -100 || this.container == -101;
    }

    public void applyCommonProperties(ItemInfo itemInfo) {
        itemInfo.f52id = this.f66id;
        itemInfo.container = this.container;
        itemInfo.screenId = (long) getInt(this.screenIndex);
        itemInfo.cellX = getInt(this.cellXIndex);
        itemInfo.cellY = getInt(this.cellYIndex);
    }

    public void checkAndAddItem(ItemInfo itemInfo, BgDataModel bgDataModel) {
        if (checkItemPlacement(itemInfo, bgDataModel.workspaceScreens)) {
            bgDataModel.addItem(this.mContext, itemInfo, false);
        } else {
            markDeleted("Item position overlap");
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkItemPlacement(ItemInfo itemInfo, ArrayList<Long> arrayList) {
        ItemInfo itemInfo2 = itemInfo;
        long j = itemInfo2.screenId;
        if (itemInfo2.container == -101) {
            GridOccupancy gridOccupancy = (GridOccupancy) this.occupied.get(-101);
            if (itemInfo2.screenId >= ((long) this.mIDP.numHotseatIcons)) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Error loading shortcut ");
                sb.append(itemInfo2);
                sb.append(" into hotseat position ");
                sb.append(itemInfo2.screenId);
                sb.append(", position out of bounds: (0 to ");
                sb.append(this.mIDP.numHotseatIcons - 1);
                sb.append(")");
                Log.e(str, sb.toString());
                return false;
            } else if (gridOccupancy == null) {
                GridOccupancy gridOccupancy2 = new GridOccupancy(this.mIDP.numHotseatIcons, 1);
                gridOccupancy2.cells[(int) itemInfo2.screenId][0] = true;
                this.occupied.put(-101, gridOccupancy2);
                return true;
            } else if (gridOccupancy.cells[(int) itemInfo2.screenId][0]) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Error loading shortcut into hotseat ");
                sb2.append(itemInfo2);
                sb2.append(" into position (");
                sb2.append(itemInfo2.screenId);
                sb2.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
                sb2.append(itemInfo2.cellX);
                sb2.append(",");
                sb2.append(itemInfo2.cellY);
                sb2.append(") already occupied");
                Log.e(str2, sb2.toString());
                return false;
            } else {
                gridOccupancy.cells[(int) itemInfo2.screenId][0] = true;
                return true;
            }
        } else if (itemInfo2.container != -100) {
            return true;
        } else {
            if (!arrayList.contains(Long.valueOf(itemInfo2.screenId))) {
                return false;
            }
            int i = this.mIDP.numColumns;
            int i2 = this.mIDP.numRows;
            if ((itemInfo2.container != -100 || itemInfo2.cellX >= 0) && itemInfo2.cellY >= 0 && itemInfo2.cellX + itemInfo2.spanX <= i && itemInfo2.cellY + itemInfo2.spanY <= i2) {
                if (!this.occupied.containsKey(itemInfo2.screenId)) {
                    int i3 = i + 1;
                    GridOccupancy gridOccupancy3 = new GridOccupancy(i3, i2 + 1);
                    if (itemInfo2.screenId == 0) {
                        gridOccupancy3.markCells(0, 0, i3, 1, FeatureFlags.QSB_ON_FIRST_SCREEN);
                    }
                    this.occupied.put(itemInfo2.screenId, gridOccupancy3);
                }
                GridOccupancy gridOccupancy4 = (GridOccupancy) this.occupied.get(itemInfo2.screenId);
                if (gridOccupancy4.isRegionVacant(itemInfo2.cellX, itemInfo2.cellY, itemInfo2.spanX, itemInfo2.spanY)) {
                    gridOccupancy4.markCells(itemInfo2, true);
                    return true;
                }
                String str3 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Error loading shortcut ");
                sb3.append(itemInfo2);
                sb3.append(" into cell (");
                sb3.append(j);
                sb3.append("-");
                sb3.append(itemInfo2.screenId);
                sb3.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
                sb3.append(itemInfo2.cellX);
                sb3.append(",");
                sb3.append(itemInfo2.cellX);
                sb3.append(",");
                sb3.append(itemInfo2.spanX);
                sb3.append(",");
                sb3.append(itemInfo2.spanY);
                sb3.append(") already occupied");
                Log.e(str3, sb3.toString());
                return false;
            }
            String str4 = TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Error loading shortcut ");
            sb4.append(itemInfo2);
            sb4.append(" into cell (");
            sb4.append(j);
            sb4.append("-");
            sb4.append(itemInfo2.screenId);
            sb4.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
            sb4.append(itemInfo2.cellX);
            sb4.append(",");
            sb4.append(itemInfo2.cellY);
            sb4.append(") out of screen bounds ( ");
            sb4.append(i);
            sb4.append("x");
            sb4.append(i2);
            sb4.append(")");
            Log.e(str4, sb4.toString());
            return false;
        }
    }
}
