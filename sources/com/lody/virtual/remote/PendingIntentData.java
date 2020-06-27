package com.lody.virtual.remote;

import android.app.PendingIntent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PendingIntentData implements Parcelable {
    public static final Creator<PendingIntentData> CREATOR = new Creator<PendingIntentData>() {
        public final PendingIntentData createFromParcel(Parcel parcel) {
            return new PendingIntentData(parcel);
        }

        public final PendingIntentData[] newArray(int i) {
            return new PendingIntentData[i];
        }
    };
    public String creator;
    public PendingIntent pendingIntent;

    public int describeContents() {
        return 0;
    }

    protected PendingIntentData(Parcel parcel) {
        this.creator = parcel.readString();
        this.pendingIntent = PendingIntent.readPendingIntentOrNullFromParcel(parcel);
    }

    public PendingIntentData(String str, IBinder iBinder) {
        this.creator = str;
        this.pendingIntent = readPendingIntent(iBinder);
    }

    public static PendingIntent readPendingIntent(IBinder iBinder) {
        Parcel obtain = Parcel.obtain();
        obtain.writeStrongBinder(iBinder);
        obtain.setDataPosition(0);
        try {
            return PendingIntent.readPendingIntentOrNullFromParcel(obtain);
        } finally {
            obtain.recycle();
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.creator);
        this.pendingIntent.writeToParcel(parcel, i);
    }
}
