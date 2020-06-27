package com.lody.virtual.remote;

import android.content.BroadcastReceiver.PendingResult;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import mirror.android.content.BroadcastReceiver;
import mirror.android.content.BroadcastReceiver.PendingResultJBMR1;
import mirror.android.content.BroadcastReceiver.PendingResultMNC;

public class PendingResultData implements Parcelable {
    public static final Creator<PendingResultData> CREATOR = new Creator<PendingResultData>() {
        public PendingResultData createFromParcel(Parcel parcel) {
            return new PendingResultData(parcel);
        }

        public PendingResultData[] newArray(int i) {
            return new PendingResultData[i];
        }
    };
    public boolean mAbortBroadcast;
    public boolean mFinished;
    public int mFlags;
    public boolean mInitialStickyHint;
    public boolean mOrderedHint;
    public int mResultCode;
    public String mResultData;
    public Bundle mResultExtras;
    public int mSendingUser;
    public IBinder mToken;
    public int mType;

    public int describeContents() {
        return 0;
    }

    public PendingResultData(PendingResult pendingResult) {
        if (PendingResultMNC.ctor != null) {
            this.mType = PendingResultMNC.mType.get(pendingResult);
            this.mOrderedHint = PendingResultMNC.mOrderedHint.get(pendingResult);
            this.mInitialStickyHint = PendingResultMNC.mInitialStickyHint.get(pendingResult);
            this.mToken = (IBinder) PendingResultMNC.mToken.get(pendingResult);
            this.mSendingUser = PendingResultMNC.mSendingUser.get(pendingResult);
            this.mFlags = PendingResultMNC.mFlags.get(pendingResult);
            this.mResultCode = PendingResultMNC.mResultCode.get(pendingResult);
            this.mResultData = (String) PendingResultMNC.mResultData.get(pendingResult);
            this.mResultExtras = (Bundle) PendingResultMNC.mResultExtras.get(pendingResult);
            this.mAbortBroadcast = PendingResultMNC.mAbortBroadcast.get(pendingResult);
            this.mFinished = PendingResultMNC.mFinished.get(pendingResult);
        } else if (PendingResultJBMR1.ctor != null) {
            this.mType = PendingResultJBMR1.mType.get(pendingResult);
            this.mOrderedHint = PendingResultJBMR1.mOrderedHint.get(pendingResult);
            this.mInitialStickyHint = PendingResultJBMR1.mInitialStickyHint.get(pendingResult);
            this.mToken = (IBinder) PendingResultJBMR1.mToken.get(pendingResult);
            this.mSendingUser = PendingResultJBMR1.mSendingUser.get(pendingResult);
            this.mResultCode = PendingResultJBMR1.mResultCode.get(pendingResult);
            this.mResultData = (String) PendingResultJBMR1.mResultData.get(pendingResult);
            this.mResultExtras = (Bundle) PendingResultJBMR1.mResultExtras.get(pendingResult);
            this.mAbortBroadcast = PendingResultJBMR1.mAbortBroadcast.get(pendingResult);
            this.mFinished = PendingResultJBMR1.mFinished.get(pendingResult);
        } else {
            this.mType = BroadcastReceiver.PendingResult.mType.get(pendingResult);
            this.mOrderedHint = BroadcastReceiver.PendingResult.mOrderedHint.get(pendingResult);
            this.mInitialStickyHint = BroadcastReceiver.PendingResult.mInitialStickyHint.get(pendingResult);
            this.mToken = (IBinder) BroadcastReceiver.PendingResult.mToken.get(pendingResult);
            this.mResultCode = BroadcastReceiver.PendingResult.mResultCode.get(pendingResult);
            this.mResultData = (String) BroadcastReceiver.PendingResult.mResultData.get(pendingResult);
            this.mResultExtras = (Bundle) BroadcastReceiver.PendingResult.mResultExtras.get(pendingResult);
            this.mAbortBroadcast = BroadcastReceiver.PendingResult.mAbortBroadcast.get(pendingResult);
            this.mFinished = BroadcastReceiver.PendingResult.mFinished.get(pendingResult);
        }
    }

    protected PendingResultData(Parcel parcel) {
        this.mType = parcel.readInt();
        boolean z = false;
        this.mOrderedHint = parcel.readByte() != 0;
        this.mInitialStickyHint = parcel.readByte() != 0;
        this.mToken = parcel.readStrongBinder();
        this.mSendingUser = parcel.readInt();
        this.mFlags = parcel.readInt();
        this.mResultCode = parcel.readInt();
        this.mResultData = parcel.readString();
        this.mResultExtras = parcel.readBundle();
        this.mAbortBroadcast = parcel.readByte() != 0;
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.mFinished = z;
    }

    public PendingResult build() {
        if (PendingResultMNC.ctor != null) {
            return (PendingResult) PendingResultMNC.ctor.newInstance(Integer.valueOf(this.mResultCode), this.mResultData, this.mResultExtras, Integer.valueOf(this.mType), Boolean.valueOf(this.mOrderedHint), Boolean.valueOf(this.mInitialStickyHint), this.mToken, Integer.valueOf(this.mSendingUser), Integer.valueOf(this.mFlags));
        } else if (PendingResultJBMR1.ctor != null) {
            return (PendingResult) PendingResultJBMR1.ctor.newInstance(Integer.valueOf(this.mResultCode), this.mResultData, this.mResultExtras, Integer.valueOf(this.mType), Boolean.valueOf(this.mOrderedHint), Boolean.valueOf(this.mInitialStickyHint), this.mToken, Integer.valueOf(this.mSendingUser));
        } else {
            return (PendingResult) BroadcastReceiver.PendingResult.ctor.newInstance(Integer.valueOf(this.mResultCode), this.mResultData, this.mResultExtras, Integer.valueOf(this.mType), Boolean.valueOf(this.mOrderedHint), Boolean.valueOf(this.mInitialStickyHint), this.mToken);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mType);
        parcel.writeByte(this.mOrderedHint ? (byte) 1 : 0);
        parcel.writeByte(this.mInitialStickyHint ? (byte) 1 : 0);
        parcel.writeStrongBinder(this.mToken);
        parcel.writeInt(this.mSendingUser);
        parcel.writeInt(this.mFlags);
        parcel.writeInt(this.mResultCode);
        parcel.writeString(this.mResultData);
        parcel.writeBundle(this.mResultExtras);
        parcel.writeByte(this.mAbortBroadcast ? (byte) 1 : 0);
        parcel.writeByte(this.mFinished ? (byte) 1 : 0);
    }

    public void finish() {
        try {
            build().finish();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
