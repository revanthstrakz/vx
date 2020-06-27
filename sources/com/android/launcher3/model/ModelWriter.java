package com.android.launcher3.model;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.LauncherSettings.Settings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LooperExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;

public class ModelWriter {
    private static final String TAG = "ModelWriter";
    /* access modifiers changed from: private */
    public final BgDataModel mBgDataModel;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final boolean mHasVerticalHotseat;
    private final Executor mWorkerExecutor = new LooperExecutor(LauncherModel.getWorkerLooper());

    private abstract class UpdateItemBaseRunnable implements Runnable {
        private final StackTraceElement[] mStackTrace = new Throwable().getStackTrace();

        UpdateItemBaseRunnable() {
        }

        /* access modifiers changed from: protected */
        public void updateItemArrays(ItemInfo itemInfo, long j) {
            synchronized (ModelWriter.this.mBgDataModel) {
                ModelWriter.this.checkItemInfoLocked(j, itemInfo, this.mStackTrace);
                if (!(itemInfo.container == -100 || itemInfo.container == -101 || ModelWriter.this.mBgDataModel.folders.containsKey(itemInfo.container))) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("item: ");
                    sb.append(itemInfo);
                    sb.append(" container being set to: ");
                    sb.append(itemInfo.container);
                    sb.append(", not in the list of folders");
                    Log.e(ModelWriter.TAG, sb.toString());
                }
                ItemInfo itemInfo2 = (ItemInfo) ModelWriter.this.mBgDataModel.itemsIdMap.get(j);
                if (itemInfo2 == null || !(itemInfo2.container == -100 || itemInfo2.container == -101)) {
                    ModelWriter.this.mBgDataModel.workspaceItems.remove(itemInfo2);
                } else {
                    int i = itemInfo2.itemType;
                    if (i != 6) {
                        switch (i) {
                            case 0:
                            case 1:
                            case 2:
                                break;
                        }
                    }
                    if (!ModelWriter.this.mBgDataModel.workspaceItems.contains(itemInfo2)) {
                        ModelWriter.this.mBgDataModel.workspaceItems.add(itemInfo2);
                    }
                }
            }
        }
    }

    private class UpdateItemRunnable extends UpdateItemBaseRunnable {
        private final ItemInfo mItem;
        private final long mItemId;
        private final ContentWriter mWriter;

        UpdateItemRunnable(ItemInfo itemInfo, ContentWriter contentWriter) {
            super();
            this.mItem = itemInfo;
            this.mWriter = contentWriter;
            this.mItemId = itemInfo.f52id;
        }

        public void run() {
            ModelWriter.this.mContext.getContentResolver().update(Favorites.getContentUri(this.mItemId), this.mWriter.getValues(ModelWriter.this.mContext), null, null);
            updateItemArrays(this.mItem, this.mItemId);
        }
    }

    private class UpdateItemsRunnable extends UpdateItemBaseRunnable {
        private final ArrayList<ItemInfo> mItems;
        private final ArrayList<ContentValues> mValues;

        UpdateItemsRunnable(ArrayList<ItemInfo> arrayList, ArrayList<ContentValues> arrayList2) {
            super();
            this.mValues = arrayList2;
            this.mItems = arrayList;
        }

        public void run() {
            ArrayList arrayList = new ArrayList();
            int size = this.mItems.size();
            for (int i = 0; i < size; i++) {
                ItemInfo itemInfo = (ItemInfo) this.mItems.get(i);
                long j = itemInfo.f52id;
                ContentValues contentValues = (ContentValues) this.mValues.get(i);
                arrayList.add(ContentProviderOperation.newUpdate(Favorites.getContentUri(j)).withValues(contentValues).build());
                updateItemArrays(itemInfo, j);
            }
            try {
                ModelWriter.this.mContext.getContentResolver().applyBatch(LauncherProvider.AUTHORITY, arrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ModelWriter(Context context, BgDataModel bgDataModel, boolean z) {
        this.mContext = context;
        this.mBgDataModel = bgDataModel;
        this.mHasVerticalHotseat = z;
    }

    private void updateItemInfoProps(ItemInfo itemInfo, long j, long j2, int i, int i2) {
        itemInfo.container = j;
        itemInfo.cellX = i;
        itemInfo.cellY = i2;
        if (j == -101) {
            itemInfo.screenId = this.mHasVerticalHotseat ? (long) ((LauncherAppState.getIDP(this.mContext).numHotseatIcons - i2) - 1) : (long) i;
        } else {
            itemInfo.screenId = j2;
        }
    }

    public void addOrMoveItemInDatabase(ItemInfo itemInfo, long j, long j2, int i, int i2) {
        if (itemInfo.container == -1) {
            addItemToDatabase(itemInfo, j, j2, i, i2);
        } else {
            moveItemInDatabase(itemInfo, j, j2, i, i2);
        }
    }

    /* access modifiers changed from: private */
    public void checkItemInfoLocked(long j, ItemInfo itemInfo, StackTraceElement[] stackTraceElementArr) {
        ItemInfo itemInfo2 = (ItemInfo) this.mBgDataModel.itemsIdMap.get(j);
        if (itemInfo2 != null && itemInfo != itemInfo2) {
            if ((itemInfo2 instanceof ShortcutInfo) && (itemInfo instanceof ShortcutInfo)) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo2;
                ShortcutInfo shortcutInfo2 = (ShortcutInfo) itemInfo;
                if (shortcutInfo.title.toString().equals(shortcutInfo2.title.toString()) && shortcutInfo.intent.filterEquals(shortcutInfo2.intent) && shortcutInfo.f52id == shortcutInfo2.f52id && shortcutInfo.itemType == shortcutInfo2.itemType && shortcutInfo.container == shortcutInfo2.container && shortcutInfo.screenId == shortcutInfo2.screenId && shortcutInfo.cellX == shortcutInfo2.cellX && shortcutInfo.cellY == shortcutInfo2.cellY && shortcutInfo.spanX == shortcutInfo2.spanX && shortcutInfo.spanY == shortcutInfo2.spanY) {
                    return;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("item: ");
            sb.append(itemInfo != null ? itemInfo.toString() : "null");
            sb.append("modelItem: ");
            sb.append(itemInfo2 != null ? itemInfo2.toString() : "null");
            sb.append("Error: ItemInfo passed to checkItemInfo doesn't match original");
            RuntimeException runtimeException = new RuntimeException(sb.toString());
            if (stackTraceElementArr != null) {
                runtimeException.setStackTrace(stackTraceElementArr);
            }
            throw runtimeException;
        }
    }

    public void moveItemInDatabase(ItemInfo itemInfo, long j, long j2, int i, int i2) {
        updateItemInfoProps(itemInfo, j, j2, i, i2);
        this.mWorkerExecutor.execute(new UpdateItemRunnable(itemInfo, new ContentWriter(this.mContext).put(Favorites.CONTAINER, Long.valueOf(itemInfo.container)).put(Favorites.CELLX, Integer.valueOf(itemInfo.cellX)).put(Favorites.CELLY, Integer.valueOf(itemInfo.cellY)).put(Favorites.RANK, Integer.valueOf(itemInfo.rank)).put(Favorites.SCREEN, Long.valueOf(itemInfo.screenId))));
    }

    public void moveItemsInDatabase(ArrayList<ItemInfo> arrayList, long j, int i) {
        ArrayList<ItemInfo> arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList();
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            ItemInfo itemInfo = (ItemInfo) arrayList2.get(i2);
            updateItemInfoProps(itemInfo, j, (long) i, itemInfo.cellX, itemInfo.cellY);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Favorites.CONTAINER, Long.valueOf(itemInfo.container));
            contentValues.put(Favorites.CELLX, Integer.valueOf(itemInfo.cellX));
            contentValues.put(Favorites.CELLY, Integer.valueOf(itemInfo.cellY));
            contentValues.put(Favorites.RANK, Integer.valueOf(itemInfo.rank));
            contentValues.put(Favorites.SCREEN, Long.valueOf(itemInfo.screenId));
            arrayList3.add(contentValues);
        }
        this.mWorkerExecutor.execute(new UpdateItemsRunnable(arrayList2, arrayList3));
    }

    public void modifyItemInDatabase(ItemInfo itemInfo, long j, long j2, int i, int i2, int i3, int i4) {
        updateItemInfoProps(itemInfo, j, j2, i, i2);
        itemInfo.spanX = i3;
        itemInfo.spanY = i4;
        this.mWorkerExecutor.execute(new UpdateItemRunnable(itemInfo, new ContentWriter(this.mContext).put(Favorites.CONTAINER, Long.valueOf(itemInfo.container)).put(Favorites.CELLX, Integer.valueOf(itemInfo.cellX)).put(Favorites.CELLY, Integer.valueOf(itemInfo.cellY)).put(Favorites.RANK, Integer.valueOf(itemInfo.rank)).put(Favorites.SPANX, Integer.valueOf(itemInfo.spanX)).put(Favorites.SPANY, Integer.valueOf(itemInfo.spanY)).put(Favorites.SCREEN, Long.valueOf(itemInfo.screenId))));
    }

    public void updateItemInDatabase(ItemInfo itemInfo) {
        ContentWriter contentWriter = new ContentWriter(this.mContext);
        itemInfo.onAddToDatabase(contentWriter);
        this.mWorkerExecutor.execute(new UpdateItemRunnable(itemInfo, contentWriter));
    }

    public void addItemToDatabase(ItemInfo itemInfo, long j, long j2, int i, int i2) {
        updateItemInfoProps(itemInfo, j, j2, i, i2);
        final ContentWriter contentWriter = new ContentWriter(this.mContext);
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        itemInfo.onAddToDatabase(contentWriter);
        itemInfo.f52id = Settings.call(contentResolver, Settings.METHOD_NEW_ITEM_ID).getLong("value");
        contentWriter.put("_id", Long.valueOf(itemInfo.f52id));
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Executor executor = this.mWorkerExecutor;
        final ItemInfo itemInfo2 = itemInfo;
        C07801 r4 = new Runnable() {
            public void run() {
                contentResolver.insert(Favorites.CONTENT_URI, contentWriter.getValues(ModelWriter.this.mContext));
                synchronized (ModelWriter.this.mBgDataModel) {
                    ModelWriter.this.checkItemInfoLocked(itemInfo2.f52id, itemInfo2, stackTrace);
                    ModelWriter.this.mBgDataModel.addItem(ModelWriter.this.mContext, itemInfo2, true);
                }
            }
        };
        executor.execute(r4);
    }

    public void deleteItemFromDatabase(ItemInfo itemInfo) {
        deleteItemsFromDatabase((Iterable<? extends ItemInfo>) Arrays.asList(new ItemInfo[]{itemInfo}));
    }

    public void deleteItemsFromDatabase(ItemInfoMatcher itemInfoMatcher) {
        deleteItemsFromDatabase((Iterable<? extends ItemInfo>) itemInfoMatcher.filterItemInfos(this.mBgDataModel.itemsIdMap));
    }

    public void deleteItemsFromDatabase(final Iterable<? extends ItemInfo> iterable) {
        this.mWorkerExecutor.execute(new Runnable() {
            public void run() {
                for (ItemInfo itemInfo : iterable) {
                    ModelWriter.this.mContext.getContentResolver().delete(Favorites.getContentUri(itemInfo.f52id), null, null);
                    ModelWriter.this.mBgDataModel.removeItem(ModelWriter.this.mContext, itemInfo);
                }
            }
        });
    }

    public void deleteFolderAndContentsFromDatabase(final FolderInfo folderInfo) {
        this.mWorkerExecutor.execute(new Runnable() {
            public void run() {
                ContentResolver contentResolver = ModelWriter.this.mContext.getContentResolver();
                Uri uri = Favorites.CONTENT_URI;
                StringBuilder sb = new StringBuilder();
                sb.append("container=");
                sb.append(folderInfo.f52id);
                contentResolver.delete(uri, sb.toString(), null);
                ModelWriter.this.mBgDataModel.removeItem(ModelWriter.this.mContext, (Iterable<? extends ItemInfo>) folderInfo.contents);
                folderInfo.contents.clear();
                contentResolver.delete(Favorites.getContentUri(folderInfo.f52id), null, null);
                ModelWriter.this.mBgDataModel.removeItem(ModelWriter.this.mContext, folderInfo);
            }
        });
    }
}
