package com.lody.virtual.p007os;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.lody.virtual.client.VClientImpl;
import java.io.PrintWriter;

/* renamed from: com.lody.virtual.os.VUserHandle */
public final class VUserHandle implements Parcelable {
    public static final VUserHandle ALL = new VUserHandle(-1);
    public static final Creator<VUserHandle> CREATOR = new Creator<VUserHandle>() {
        public VUserHandle createFromParcel(Parcel parcel) {
            return new VUserHandle(parcel);
        }

        public VUserHandle[] newArray(int i) {
            return new VUserHandle[i];
        }
    };
    public static final VUserHandle CURRENT = new VUserHandle(-2);
    public static final VUserHandle CURRENT_OR_SELF = new VUserHandle(-3);
    public static final int FIRST_ISOLATED_UID = 99000;
    public static final int FIRST_SHARED_APPLICATION_GID = 50000;
    public static final int LAST_ISOLATED_UID = 99999;
    public static final int LAST_SHARED_APPLICATION_GID = 59999;
    public static final boolean MU_ENABLED = true;
    public static final VUserHandle OWNER = new VUserHandle(0);
    public static final int PER_USER_RANGE = 100000;
    public static final int USER_ALL = -1;
    public static final int USER_CURRENT = -2;
    public static final int USER_CURRENT_OR_SELF = -3;
    public static final int USER_NULL = -10000;
    public static final int USER_OWNER = 0;
    private static final SparseArray<VUserHandle> userHandles = new SparseArray<>();
    final int mHandle;

    public int describeContents() {
        return 0;
    }

    public VUserHandle(int i) {
        this.mHandle = i;
    }

    public VUserHandle(Parcel parcel) {
        this.mHandle = parcel.readInt();
    }

    public static boolean isSameUser(int i, int i2) {
        return getUserId(i) == getUserId(i2);
    }

    public static boolean accept(int i) {
        return i == -1 || i == myUserId();
    }

    public static final boolean isSameApp(int i, int i2) {
        return getAppId(i) == getAppId(i2);
    }

    public static final boolean isIsolated(int i) {
        boolean z = false;
        if (i <= 0) {
            return false;
        }
        int appId = getAppId(i);
        if (appId >= 99000 && appId <= 99999) {
            z = true;
        }
        return z;
    }

    public static boolean isApp(int i) {
        boolean z = false;
        if (i <= 0) {
            return false;
        }
        int appId = getAppId(i);
        if (appId >= 10000 && appId <= 19999) {
            z = true;
        }
        return z;
    }

    public static int getUserId(int i) {
        return i / PER_USER_RANGE;
    }

    public static int getCallingUserId() {
        return getUserId(VBinder.getCallingUid());
    }

    public static VUserHandle getCallingUserHandle() {
        int userId = getUserId(VBinder.getCallingUid());
        VUserHandle vUserHandle = (VUserHandle) userHandles.get(userId);
        if (vUserHandle != null) {
            return vUserHandle;
        }
        VUserHandle vUserHandle2 = new VUserHandle(userId);
        userHandles.put(userId, vUserHandle2);
        return vUserHandle2;
    }

    public static int getUid(int i, int i2) {
        return (i * PER_USER_RANGE) + (i2 % PER_USER_RANGE);
    }

    public static int getAppId(int i) {
        return i % PER_USER_RANGE;
    }

    public static int myAppId() {
        return getAppId(VClientImpl.get().getVUid());
    }

    public static int getAppIdFromSharedAppGid(int i) {
        int appId = getAppId(i);
        if (appId >= 50000 && appId <= 59999) {
            return (appId + 10000) - FIRST_SHARED_APPLICATION_GID;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(i));
        sb.append(" is not a shared app gid");
        throw new IllegalArgumentException(sb.toString());
    }

    public static void formatUid(StringBuilder sb, int i) {
        if (i < 10000) {
            sb.append(i);
            return;
        }
        sb.append('u');
        sb.append(getUserId(i));
        int appId = getAppId(i);
        if (appId >= 99000 && appId <= 99999) {
            sb.append('i');
            sb.append(appId - FIRST_ISOLATED_UID);
        } else if (appId >= 10000) {
            sb.append('a');
            sb.append(appId - 10000);
        } else {
            sb.append('s');
            sb.append(appId);
        }
    }

    public static String formatUid(int i) {
        StringBuilder sb = new StringBuilder();
        formatUid(sb, i);
        return sb.toString();
    }

    public static void formatUid(PrintWriter printWriter, int i) {
        if (i < 10000) {
            printWriter.print(i);
            return;
        }
        printWriter.print('u');
        printWriter.print(getUserId(i));
        int appId = getAppId(i);
        if (appId >= 99000 && appId <= 99999) {
            printWriter.print('i');
            printWriter.print(appId - FIRST_ISOLATED_UID);
        } else if (appId >= 10000) {
            printWriter.print('a');
            printWriter.print(appId - 10000);
        } else {
            printWriter.print('s');
            printWriter.print(appId);
        }
    }

    public static int myUserId() {
        return getUserId(VClientImpl.get().getVUid());
    }

    public static void writeToParcel(VUserHandle vUserHandle, Parcel parcel) {
        if (vUserHandle != null) {
            vUserHandle.writeToParcel(parcel, 0);
        } else {
            parcel.writeInt(-10000);
        }
    }

    public static VUserHandle readFromParcel(Parcel parcel) {
        int readInt = parcel.readInt();
        if (readInt != -10000) {
            return new VUserHandle(readInt);
        }
        return null;
    }

    public static VUserHandle myUserHandle() {
        return new VUserHandle(myUserId());
    }

    public final boolean isOwner() {
        return equals(OWNER);
    }

    public int getIdentifier() {
        return this.mHandle;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VUserHandle{");
        sb.append(this.mHandle);
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj != null) {
            try {
                if (this.mHandle == ((VUserHandle) obj).mHandle) {
                    z = true;
                }
                return z;
            } catch (ClassCastException unused) {
            }
        }
        return false;
    }

    public int hashCode() {
        return this.mHandle;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mHandle);
    }
}
