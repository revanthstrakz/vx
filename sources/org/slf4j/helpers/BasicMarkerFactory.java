package org.slf4j.helpers;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

public class BasicMarkerFactory implements IMarkerFactory {
    Map markerMap = new HashMap();

    public synchronized Marker getMarker(String str) {
        Marker marker;
        if (str != null) {
            marker = (Marker) this.markerMap.get(str);
            if (marker == null) {
                marker = new BasicMarker(str);
                this.markerMap.put(str, marker);
            }
        } else {
            throw new IllegalArgumentException("Marker name cannot be null");
        }
        return marker;
    }

    public synchronized boolean exists(String str) {
        if (str == null) {
            return false;
        }
        return this.markerMap.containsKey(str);
    }

    public boolean detachMarker(String str) {
        boolean z = false;
        if (str == null) {
            return false;
        }
        if (this.markerMap.remove(str) != null) {
            z = true;
        }
        return z;
    }

    public Marker getDetachedMarker(String str) {
        return new BasicMarker(str);
    }
}
