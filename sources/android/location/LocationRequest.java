package android.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class LocationRequest implements Parcelable {
    public static final Creator<LocationRequest> CREATOR = new Creator<LocationRequest>() {
        public LocationRequest createFromParcel(Parcel parcel) {
            return null;
        }

        public LocationRequest[] newArray(int i) {
            return null;
        }
    };

    public int describeContents() {
        return 0;
    }

    public String getProvider() {
        return null;
    }

    public void writeToParcel(Parcel parcel, int i) {
    }
}
