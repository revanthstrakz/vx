package com.lody.virtual.server.p009pm.installer;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/* renamed from: com.lody.virtual.server.pm.installer.SessionInfo */
public class SessionInfo implements Parcelable {
    public static final Creator<SessionInfo> CREATOR = new Creator<SessionInfo>() {
        public SessionInfo createFromParcel(Parcel parcel) {
            return new SessionInfo(parcel);
        }

        public SessionInfo[] newArray(int i) {
            return new SessionInfo[i];
        }
    };
    public boolean active;
    public Bitmap appIcon;
    public CharSequence appLabel;
    public String appPackageName;
    public String installerPackageName;
    public int mode;
    public float progress;
    public String resolvedBaseCodePath;
    public boolean sealed;
    public int sessionId;
    public long sizeBytes;

    public int describeContents() {
        return 0;
    }

    public android.content.pm.PackageInstaller.SessionInfo alloc() {
        android.content.pm.PackageInstaller.SessionInfo sessionInfo = (android.content.pm.PackageInstaller.SessionInfo) mirror.android.content.p016pm.PackageInstaller.SessionInfo.ctor.newInstance();
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.sessionId.set(sessionInfo, this.sessionId);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.installerPackageName.set(sessionInfo, this.installerPackageName);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.resolvedBaseCodePath.set(sessionInfo, this.resolvedBaseCodePath);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.progress.set(sessionInfo, this.progress);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.sealed.set(sessionInfo, this.sealed);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.active.set(sessionInfo, this.active);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.mode.set(sessionInfo, this.mode);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.sizeBytes.set(sessionInfo, this.sizeBytes);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.appPackageName.set(sessionInfo, this.appPackageName);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.appIcon.set(sessionInfo, this.appIcon);
        mirror.android.content.p016pm.PackageInstaller.SessionInfo.appLabel.set(sessionInfo, this.appLabel);
        return sessionInfo;
    }

    public static SessionInfo realloc(android.content.pm.PackageInstaller.SessionInfo sessionInfo) {
        SessionInfo sessionInfo2 = new SessionInfo();
        sessionInfo2.sessionId = mirror.android.content.p016pm.PackageInstaller.SessionInfo.sessionId.get(sessionInfo);
        sessionInfo2.installerPackageName = (String) mirror.android.content.p016pm.PackageInstaller.SessionInfo.installerPackageName.get(sessionInfo);
        sessionInfo2.resolvedBaseCodePath = (String) mirror.android.content.p016pm.PackageInstaller.SessionInfo.resolvedBaseCodePath.get(sessionInfo);
        sessionInfo2.progress = mirror.android.content.p016pm.PackageInstaller.SessionInfo.progress.get(sessionInfo);
        sessionInfo2.sealed = mirror.android.content.p016pm.PackageInstaller.SessionInfo.sealed.get(sessionInfo);
        sessionInfo2.active = mirror.android.content.p016pm.PackageInstaller.SessionInfo.active.get(sessionInfo);
        sessionInfo2.mode = mirror.android.content.p016pm.PackageInstaller.SessionInfo.mode.get(sessionInfo);
        sessionInfo2.sizeBytes = mirror.android.content.p016pm.PackageInstaller.SessionInfo.sizeBytes.get(sessionInfo);
        sessionInfo2.appPackageName = (String) mirror.android.content.p016pm.PackageInstaller.SessionInfo.appPackageName.get(sessionInfo);
        sessionInfo2.appIcon = (Bitmap) mirror.android.content.p016pm.PackageInstaller.SessionInfo.appIcon.get(sessionInfo);
        sessionInfo2.appLabel = (CharSequence) mirror.android.content.p016pm.PackageInstaller.SessionInfo.appLabel.get(sessionInfo);
        return sessionInfo2;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.sessionId);
        parcel.writeString(this.installerPackageName);
        parcel.writeString(this.resolvedBaseCodePath);
        parcel.writeFloat(this.progress);
        parcel.writeByte(this.sealed ? (byte) 1 : 0);
        parcel.writeByte(this.active ? (byte) 1 : 0);
        parcel.writeInt(this.mode);
        parcel.writeLong(this.sizeBytes);
        parcel.writeString(this.appPackageName);
        parcel.writeParcelable(this.appIcon, i);
        if (this.appLabel != null) {
            parcel.writeString(this.appLabel.toString());
        }
    }

    public SessionInfo() {
    }

    protected SessionInfo(Parcel parcel) {
        this.sessionId = parcel.readInt();
        this.installerPackageName = parcel.readString();
        this.resolvedBaseCodePath = parcel.readString();
        this.progress = parcel.readFloat();
        boolean z = false;
        this.sealed = parcel.readByte() != 0;
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.active = z;
        this.mode = parcel.readInt();
        this.sizeBytes = parcel.readLong();
        this.appPackageName = parcel.readString();
        this.appIcon = (Bitmap) parcel.readParcelable(Bitmap.class.getClassLoader());
        this.appLabel = parcel.readString();
    }
}
