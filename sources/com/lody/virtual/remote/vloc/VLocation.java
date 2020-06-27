package com.lody.virtual.remote.vloc;

import android.location.Location;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.lody.virtual.client.env.VirtualGPSSatalines;
import com.lody.virtual.helper.utils.Reflect;

public class VLocation implements Parcelable {
    public static final Creator<VLocation> CREATOR = new Creator<VLocation>() {
        public VLocation createFromParcel(Parcel parcel) {
            return new VLocation(parcel);
        }

        public VLocation[] newArray(int i) {
            return new VLocation[i];
        }
    };
    public float accuracy = 0.0f;
    public double altitude = 0.0d;
    public float bearing;
    public double latitude = 0.0d;
    public double longitude = 0.0d;
    public float speed;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
        parcel.writeDouble(this.altitude);
        parcel.writeFloat(this.accuracy);
        parcel.writeFloat(this.speed);
        parcel.writeFloat(this.bearing);
    }

    public VLocation() {
    }

    public VLocation(Parcel parcel) {
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
        this.altitude = parcel.readDouble();
        this.accuracy = parcel.readFloat();
        this.speed = parcel.readFloat();
        this.bearing = parcel.readFloat();
    }

    public boolean isEmpty() {
        return this.latitude == 0.0d && this.longitude == 0.0d;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VLocation{latitude=");
        sb.append(this.latitude);
        sb.append(", longitude=");
        sb.append(this.longitude);
        sb.append(", altitude=");
        sb.append(this.altitude);
        sb.append(", accuracy=");
        sb.append(this.accuracy);
        sb.append(", speed=");
        sb.append(this.speed);
        sb.append(", bearing=");
        sb.append(this.bearing);
        sb.append('}');
        return sb.toString();
    }

    public Location toSysLocation() {
        Location location = new Location("gps");
        location.setAccuracy(8.0f);
        Bundle bundle = new Bundle();
        location.setBearing(this.bearing);
        Reflect.m80on((Object) location).call("setIsFromMockProvider", Boolean.valueOf(false));
        location.setLatitude(this.latitude);
        location.setLongitude(this.longitude);
        location.setSpeed(this.speed);
        location.setTime(System.currentTimeMillis());
        location.setExtras(bundle);
        if (VERSION.SDK_INT >= 17) {
            location.setElapsedRealtimeNanos(277000000);
        }
        int svCount = VirtualGPSSatalines.get().getSvCount();
        bundle.putInt("satellites", svCount);
        bundle.putInt("satellitesvalue", svCount);
        return location;
    }
}
