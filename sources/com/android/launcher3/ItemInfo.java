package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.util.ContentWriter;

public class ItemInfo {
    public static final int NO_ID = -1;
    public int cellX;
    public int cellY;
    public long container;
    public CharSequence contentDescription;

    /* renamed from: id */
    public long f52id;
    public int itemType;
    public int minSpanX;
    public int minSpanY;
    public int rank;
    public long screenId;
    public int spanX;
    public int spanY;
    public CharSequence title;
    public UserHandle user;

    public Intent getIntent() {
        return null;
    }

    public boolean isDisabled() {
        return false;
    }

    public ItemInfo() {
        this.f52id = -1;
        this.container = -1;
        this.screenId = -1;
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.minSpanX = 1;
        this.minSpanY = 1;
        this.rank = 0;
        this.user = Process.myUserHandle();
    }

    ItemInfo(ItemInfo itemInfo) {
        this.f52id = -1;
        this.container = -1;
        this.screenId = -1;
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.minSpanX = 1;
        this.minSpanY = 1;
        this.rank = 0;
        copyFrom(itemInfo);
        LauncherModel.checkItemInfo(this);
    }

    public void copyFrom(ItemInfo itemInfo) {
        this.f52id = itemInfo.f52id;
        this.cellX = itemInfo.cellX;
        this.cellY = itemInfo.cellY;
        this.spanX = itemInfo.spanX;
        this.spanY = itemInfo.spanY;
        this.rank = itemInfo.rank;
        this.screenId = itemInfo.screenId;
        this.itemType = itemInfo.itemType;
        this.container = itemInfo.container;
        this.user = itemInfo.user;
        this.contentDescription = itemInfo.contentDescription;
    }

    public ComponentName getTargetComponent() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getComponent();
        }
        return null;
    }

    public void writeToValues(ContentWriter contentWriter) {
        contentWriter.put(BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(this.itemType)).put(Favorites.CONTAINER, Long.valueOf(this.container)).put(Favorites.SCREEN, Long.valueOf(this.screenId)).put(Favorites.CELLX, Integer.valueOf(this.cellX)).put(Favorites.CELLY, Integer.valueOf(this.cellY)).put(Favorites.SPANX, Integer.valueOf(this.spanX)).put(Favorites.SPANY, Integer.valueOf(this.spanY)).put(Favorites.RANK, Integer.valueOf(this.rank));
    }

    public void readFromValues(ContentValues contentValues) {
        this.itemType = contentValues.getAsInteger(BaseLauncherColumns.ITEM_TYPE).intValue();
        this.container = contentValues.getAsLong(Favorites.CONTAINER).longValue();
        this.screenId = contentValues.getAsLong(Favorites.SCREEN).longValue();
        this.cellX = contentValues.getAsInteger(Favorites.CELLX).intValue();
        this.cellY = contentValues.getAsInteger(Favorites.CELLY).intValue();
        this.spanX = contentValues.getAsInteger(Favorites.SPANX).intValue();
        this.spanY = contentValues.getAsInteger(Favorites.SPANY).intValue();
        this.rank = contentValues.getAsInteger(Favorites.RANK).intValue();
    }

    public void onAddToDatabase(ContentWriter contentWriter) {
        if (this.screenId != -201) {
            writeToValues(contentWriter);
            contentWriter.put(Favorites.PROFILE_ID, this.user);
            return;
        }
        throw new RuntimeException("Screen id should not be EXTRA_EMPTY_SCREEN_ID");
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("(");
        sb.append(dumpProperties());
        sb.append(")");
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=");
        sb.append(this.f52id);
        sb.append(" type=");
        sb.append(Favorites.itemTypeToString(this.itemType));
        sb.append(" container=");
        sb.append(Favorites.containerToString((int) this.container));
        sb.append(" screen=");
        sb.append(this.screenId);
        sb.append(" cell(");
        sb.append(this.cellX);
        sb.append(",");
        sb.append(this.cellY);
        sb.append(")");
        sb.append(" span(");
        sb.append(this.spanX);
        sb.append(",");
        sb.append(this.spanY);
        sb.append(")");
        sb.append(" minSpan(");
        sb.append(this.minSpanX);
        sb.append(",");
        sb.append(this.minSpanY);
        sb.append(")");
        sb.append(" rank=");
        sb.append(this.rank);
        sb.append(" user=");
        sb.append(this.user);
        sb.append(" title=");
        sb.append(this.title);
        return sb.toString();
    }
}
