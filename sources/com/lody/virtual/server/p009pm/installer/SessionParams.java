package com.lody.virtual.server.p009pm.installer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import mirror.android.content.p016pm.PackageInstaller.SessionParamsLOLLIPOP;
import mirror.android.content.p016pm.PackageInstaller.SessionParamsMarshmallow;

@TargetApi(21)
/* renamed from: com.lody.virtual.server.pm.installer.SessionParams */
public class SessionParams implements Parcelable {
    public static final Creator<SessionParams> CREATOR = new Creator<SessionParams>() {
        public SessionParams createFromParcel(Parcel parcel) {
            return new SessionParams(parcel);
        }

        public SessionParams[] newArray(int i) {
            return new SessionParams[i];
        }
    };
    public static final int MODE_FULL_INSTALL = 1;
    public static final int MODE_INHERIT_EXISTING = 2;
    public static final int MODE_INVALID = -1;
    public String abiOverride;
    public Bitmap appIcon;
    public long appIconLastModified = -1;
    public String appLabel;
    public String appPackageName;
    public String[] grantedRuntimePermissions;
    public int installFlags;
    public int installLocation = 1;
    public int mode = -1;
    public Uri originatingUri;
    public Uri referrerUri;
    public long sizeBytes = -1;
    public String volumeUuid;

    public int describeContents() {
        return 0;
    }

    public SessionParams(int i) {
        this.mode = i;
    }

    public android.content.pm.PackageInstaller.SessionParams build() {
        if (VERSION.SDK_INT >= 23) {
            android.content.pm.PackageInstaller.SessionParams sessionParams = new android.content.pm.PackageInstaller.SessionParams(this.mode);
            SessionParamsMarshmallow.installFlags.set(sessionParams, this.installFlags);
            SessionParamsMarshmallow.installLocation.set(sessionParams, this.installLocation);
            SessionParamsMarshmallow.sizeBytes.set(sessionParams, this.sizeBytes);
            SessionParamsMarshmallow.appPackageName.set(sessionParams, this.appPackageName);
            SessionParamsMarshmallow.appIcon.set(sessionParams, this.appIcon);
            SessionParamsMarshmallow.appLabel.set(sessionParams, this.appLabel);
            SessionParamsMarshmallow.appIconLastModified.set(sessionParams, this.appIconLastModified);
            SessionParamsMarshmallow.originatingUri.set(sessionParams, this.originatingUri);
            SessionParamsMarshmallow.referrerUri.set(sessionParams, this.referrerUri);
            SessionParamsMarshmallow.abiOverride.set(sessionParams, this.abiOverride);
            SessionParamsMarshmallow.volumeUuid.set(sessionParams, this.volumeUuid);
            SessionParamsMarshmallow.grantedRuntimePermissions.set(sessionParams, this.grantedRuntimePermissions);
            return sessionParams;
        }
        android.content.pm.PackageInstaller.SessionParams sessionParams2 = new android.content.pm.PackageInstaller.SessionParams(this.mode);
        SessionParamsLOLLIPOP.installFlags.set(sessionParams2, this.installFlags);
        SessionParamsLOLLIPOP.installLocation.set(sessionParams2, this.installLocation);
        SessionParamsLOLLIPOP.sizeBytes.set(sessionParams2, this.sizeBytes);
        SessionParamsLOLLIPOP.appPackageName.set(sessionParams2, this.appPackageName);
        SessionParamsLOLLIPOP.appIcon.set(sessionParams2, this.appIcon);
        SessionParamsLOLLIPOP.appLabel.set(sessionParams2, this.appLabel);
        SessionParamsLOLLIPOP.appIconLastModified.set(sessionParams2, this.appIconLastModified);
        SessionParamsLOLLIPOP.originatingUri.set(sessionParams2, this.originatingUri);
        SessionParamsLOLLIPOP.referrerUri.set(sessionParams2, this.referrerUri);
        SessionParamsLOLLIPOP.abiOverride.set(sessionParams2, this.abiOverride);
        return sessionParams2;
    }

    public static SessionParams create(android.content.pm.PackageInstaller.SessionParams sessionParams) {
        if (VERSION.SDK_INT >= 23) {
            SessionParams sessionParams2 = new SessionParams(SessionParamsMarshmallow.mode.get(sessionParams));
            sessionParams2.installFlags = SessionParamsMarshmallow.installFlags.get(sessionParams);
            sessionParams2.installLocation = SessionParamsMarshmallow.installLocation.get(sessionParams);
            sessionParams2.sizeBytes = SessionParamsMarshmallow.sizeBytes.get(sessionParams);
            sessionParams2.appPackageName = (String) SessionParamsMarshmallow.appPackageName.get(sessionParams);
            sessionParams2.appIcon = (Bitmap) SessionParamsMarshmallow.appIcon.get(sessionParams);
            sessionParams2.appLabel = (String) SessionParamsMarshmallow.appLabel.get(sessionParams);
            sessionParams2.appIconLastModified = SessionParamsMarshmallow.appIconLastModified.get(sessionParams);
            sessionParams2.originatingUri = (Uri) SessionParamsMarshmallow.originatingUri.get(sessionParams);
            sessionParams2.referrerUri = (Uri) SessionParamsMarshmallow.referrerUri.get(sessionParams);
            sessionParams2.abiOverride = (String) SessionParamsMarshmallow.abiOverride.get(sessionParams);
            sessionParams2.volumeUuid = (String) SessionParamsMarshmallow.volumeUuid.get(sessionParams);
            sessionParams2.grantedRuntimePermissions = (String[]) SessionParamsMarshmallow.grantedRuntimePermissions.get(sessionParams);
            return sessionParams2;
        }
        SessionParams sessionParams3 = new SessionParams(SessionParamsLOLLIPOP.mode.get(sessionParams));
        sessionParams3.installFlags = SessionParamsLOLLIPOP.installFlags.get(sessionParams);
        sessionParams3.installLocation = SessionParamsLOLLIPOP.installLocation.get(sessionParams);
        sessionParams3.sizeBytes = SessionParamsLOLLIPOP.sizeBytes.get(sessionParams);
        sessionParams3.appPackageName = (String) SessionParamsLOLLIPOP.appPackageName.get(sessionParams);
        sessionParams3.appIcon = (Bitmap) SessionParamsLOLLIPOP.appIcon.get(sessionParams);
        sessionParams3.appLabel = (String) SessionParamsLOLLIPOP.appLabel.get(sessionParams);
        sessionParams3.appIconLastModified = SessionParamsLOLLIPOP.appIconLastModified.get(sessionParams);
        sessionParams3.originatingUri = (Uri) SessionParamsLOLLIPOP.originatingUri.get(sessionParams);
        sessionParams3.referrerUri = (Uri) SessionParamsLOLLIPOP.referrerUri.get(sessionParams);
        sessionParams3.abiOverride = (String) SessionParamsLOLLIPOP.abiOverride.get(sessionParams);
        return sessionParams3;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mode);
        parcel.writeInt(this.installFlags);
        parcel.writeInt(this.installLocation);
        parcel.writeLong(this.sizeBytes);
        parcel.writeString(this.appPackageName);
        parcel.writeParcelable(this.appIcon, i);
        parcel.writeString(this.appLabel);
        parcel.writeLong(this.appIconLastModified);
        parcel.writeParcelable(this.originatingUri, i);
        parcel.writeParcelable(this.referrerUri, i);
        parcel.writeString(this.abiOverride);
        parcel.writeString(this.volumeUuid);
        parcel.writeStringArray(this.grantedRuntimePermissions);
    }

    protected SessionParams(Parcel parcel) {
        this.mode = parcel.readInt();
        this.installFlags = parcel.readInt();
        this.installLocation = parcel.readInt();
        this.sizeBytes = parcel.readLong();
        this.appPackageName = parcel.readString();
        this.appIcon = (Bitmap) parcel.readParcelable(Bitmap.class.getClassLoader());
        this.appLabel = parcel.readString();
        this.appIconLastModified = parcel.readLong();
        this.originatingUri = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.referrerUri = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.abiOverride = parcel.readString();
        this.volumeUuid = parcel.readString();
        this.grantedRuntimePermissions = parcel.createStringArray();
    }
}
