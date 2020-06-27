package p013io.virtualapp.home.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/* renamed from: io.virtualapp.home.models.AppInfoLite */
public class AppInfoLite implements Parcelable {
    public static final Creator<AppInfoLite> CREATOR = new Creator<AppInfoLite>() {
        public AppInfoLite createFromParcel(Parcel parcel) {
            return new AppInfoLite(parcel);
        }

        public AppInfoLite[] newArray(int i) {
            return new AppInfoLite[i];
        }
    };
    public boolean disableMultiVersion;
    public boolean fastOpen;
    public String packageName;
    public String path;

    public int describeContents() {
        return 0;
    }

    public AppInfoLite(String str, String str2, boolean z, boolean z2) {
        this.packageName = str;
        this.path = str2;
        this.fastOpen = z;
        this.disableMultiVersion = z2;
    }

    protected AppInfoLite(Parcel parcel) {
        this.packageName = parcel.readString();
        this.path = parcel.readString();
        boolean z = false;
        this.fastOpen = parcel.readByte() != 0;
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.disableMultiVersion = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeString(this.path);
        parcel.writeByte(this.fastOpen ? (byte) 1 : 0);
        parcel.writeByte(this.disableMultiVersion ? (byte) 1 : 0);
    }
}
