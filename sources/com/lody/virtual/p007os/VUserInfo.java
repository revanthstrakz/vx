package com.lody.virtual.p007os;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.microsoft.appcenter.Constants;

/* renamed from: com.lody.virtual.os.VUserInfo */
public class VUserInfo implements Parcelable {
    public static final Creator<VUserInfo> CREATOR = new Creator<VUserInfo>() {
        public VUserInfo createFromParcel(Parcel parcel) {
            return new VUserInfo(parcel);
        }

        public VUserInfo[] newArray(int i) {
            return new VUserInfo[i];
        }
    };
    public static final int FLAG_ADMIN = 2;
    public static final int FLAG_DISABLED = 64;
    public static final int FLAG_GUEST = 4;
    public static final int FLAG_INITIALIZED = 16;
    public static final int FLAG_MANAGED_PROFILE = 32;
    public static final int FLAG_MASK_USER_TYPE = 255;
    public static final int FLAG_PRIMARY = 1;
    public static final int FLAG_RESTRICTED = 8;
    public static final int NO_PROFILE_GROUP_ID = -1;
    public long creationTime;
    public int flags;
    public String iconPath;

    /* renamed from: id */
    public int f180id;
    public long lastLoggedInTime;
    public String name;
    public boolean partial;
    public int profileGroupId;
    public int serialNumber;

    public int describeContents() {
        return 0;
    }

    public VUserInfo(int i, String str, int i2) {
        this(i, str, null, i2);
    }

    public VUserInfo(int i, String str, String str2, int i2) {
        this.f180id = i;
        this.name = str;
        this.flags = i2;
        this.iconPath = str2;
        this.profileGroupId = -1;
    }

    public boolean isPrimary() {
        return (this.flags & 1) == 1;
    }

    public boolean isAdmin() {
        return (this.flags & 2) == 2;
    }

    public boolean isGuest() {
        return (this.flags & 4) == 4;
    }

    public boolean isRestricted() {
        return (this.flags & 8) == 8;
    }

    public boolean isManagedProfile() {
        return (this.flags & 32) == 32;
    }

    public boolean isEnabled() {
        return (this.flags & 64) != 64;
    }

    public VUserInfo() {
    }

    public VUserInfo(VUserInfo vUserInfo) {
        this.name = vUserInfo.name;
        this.iconPath = vUserInfo.iconPath;
        this.f180id = vUserInfo.f180id;
        this.flags = vUserInfo.flags;
        this.serialNumber = vUserInfo.serialNumber;
        this.creationTime = vUserInfo.creationTime;
        this.lastLoggedInTime = vUserInfo.lastLoggedInTime;
        this.partial = vUserInfo.partial;
        this.profileGroupId = vUserInfo.profileGroupId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserInfo{");
        sb.append(this.f180id);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(this.name);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(Integer.toHexString(this.flags));
        sb.append("}");
        return sb.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f180id);
        parcel.writeString(this.name);
        parcel.writeString(this.iconPath);
        parcel.writeInt(this.flags);
        parcel.writeInt(this.serialNumber);
        parcel.writeLong(this.creationTime);
        parcel.writeLong(this.lastLoggedInTime);
        parcel.writeInt(this.partial ? 1 : 0);
        parcel.writeInt(this.profileGroupId);
    }

    private VUserInfo(Parcel parcel) {
        this.f180id = parcel.readInt();
        this.name = parcel.readString();
        this.iconPath = parcel.readString();
        this.flags = parcel.readInt();
        this.serialNumber = parcel.readInt();
        this.creationTime = parcel.readLong();
        this.lastLoggedInTime = parcel.readLong();
        this.partial = parcel.readInt() != 0;
        this.profileGroupId = parcel.readInt();
    }
}
