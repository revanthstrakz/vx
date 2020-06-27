package org.slf4j;

import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticMarkerBinder;

public class MarkerFactory {
    static IMarkerFactory markerFactory;

    private MarkerFactory() {
    }

    static {
        try {
            markerFactory = StaticMarkerBinder.SINGLETON.getMarkerFactory();
        } catch (NoClassDefFoundError unused) {
            markerFactory = new BasicMarkerFactory();
        } catch (Exception e) {
            Util.report("Unexpected failure while binding MarkerFactory", e);
        }
    }

    public static Marker getMarker(String str) {
        return markerFactory.getMarker(str);
    }

    public static Marker getDetachedMarker(String str) {
        return markerFactory.getDetachedMarker(str);
    }

    public static IMarkerFactory getIMarkerFactory() {
        return markerFactory;
    }
}
