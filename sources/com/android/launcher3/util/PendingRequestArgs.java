package com.android.launcher3.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.widget.WidgetAddFlowHandler;

public class PendingRequestArgs extends ItemInfo implements Parcelable {
    public static final Creator<PendingRequestArgs> CREATOR = new Creator<PendingRequestArgs>() {
        public PendingRequestArgs createFromParcel(Parcel parcel) {
            return new PendingRequestArgs(parcel);
        }

        public PendingRequestArgs[] newArray(int i) {
            return new PendingRequestArgs[i];
        }
    };
    private static final int TYPE_APP_WIDGET = 2;
    private static final int TYPE_INTENT = 1;
    private static final int TYPE_NONE = 0;
    private final int mArg1;
    private final Parcelable mObject;
    private final int mObjectType;

    public int describeContents() {
        return 0;
    }

    public PendingRequestArgs(ItemInfo itemInfo) {
        this.mArg1 = 0;
        this.mObjectType = 0;
        this.mObject = null;
        copyFrom(itemInfo);
    }

    private PendingRequestArgs(int i, int i2, Parcelable parcelable) {
        this.mArg1 = i;
        this.mObjectType = i2;
        this.mObject = parcelable;
    }

    public PendingRequestArgs(Parcel parcel) {
        readFromValues((ContentValues) ContentValues.CREATOR.createFromParcel(parcel));
        this.user = (UserHandle) parcel.readParcelable(null);
        this.mArg1 = parcel.readInt();
        this.mObjectType = parcel.readInt();
        this.mObject = parcel.readParcelable(null);
    }

    public void writeToParcel(Parcel parcel, int i) {
        ContentValues contentValues = new ContentValues();
        writeToValues(new ContentWriter(contentValues, (Context) null));
        contentValues.writeToParcel(parcel, i);
        parcel.writeParcelable(this.user, i);
        parcel.writeInt(this.mArg1);
        parcel.writeInt(this.mObjectType);
        parcel.writeParcelable(this.mObject, i);
    }

    public WidgetAddFlowHandler getWidgetHandler() {
        if (this.mObjectType == 2) {
            return (WidgetAddFlowHandler) this.mObject;
        }
        return null;
    }

    public int getWidgetId() {
        if (this.mObjectType == 2) {
            return this.mArg1;
        }
        return 0;
    }

    public Intent getPendingIntent() {
        if (this.mObjectType == 1) {
            return (Intent) this.mObject;
        }
        return null;
    }

    public int getRequestCode() {
        if (this.mObjectType == 1) {
            return this.mArg1;
        }
        return 0;
    }

    public static PendingRequestArgs forWidgetInfo(int i, WidgetAddFlowHandler widgetAddFlowHandler, ItemInfo itemInfo) {
        PendingRequestArgs pendingRequestArgs = new PendingRequestArgs(i, 2, widgetAddFlowHandler);
        pendingRequestArgs.copyFrom(itemInfo);
        return pendingRequestArgs;
    }

    public static PendingRequestArgs forIntent(int i, Intent intent, ItemInfo itemInfo) {
        PendingRequestArgs pendingRequestArgs = new PendingRequestArgs(i, 1, intent);
        pendingRequestArgs.copyFrom(itemInfo);
        return pendingRequestArgs;
    }
}
