package com.lody.virtual.client.hook.proxies.location;

import com.lody.virtual.client.env.VirtualGPSSatalines;
import com.lody.virtual.client.ipc.VirtualLocationManager;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.remote.vloc.VLocation;
import com.microsoft.appcenter.Constants;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import mirror.android.location.LocationManager.GnssStatusListenerTransport;
import mirror.android.location.LocationManager.GpsStatusListenerTransport;
import mirror.android.location.LocationManager.GpsStatusListenerTransportOPPO_R815T;
import mirror.android.location.LocationManager.GpsStatusListenerTransportSumsungS5;
import mirror.android.location.LocationManager.GpsStatusListenerTransportVIVO;
import org.slf4j.Marker;

public class MockLocationHelper {
    public static void invokeNmeaReceived(Object obj) {
        if (obj != null) {
            VirtualGPSSatalines virtualGPSSatalines = VirtualGPSSatalines.get();
            try {
                VLocation location = VirtualLocationManager.get().getLocation();
                if (location != null) {
                    String format = new SimpleDateFormat("HHmmss:SS", Locale.US).format(new Date());
                    String gPSLat = getGPSLat(location.latitude);
                    String gPSLat2 = getGPSLat(location.longitude);
                    String northWest = getNorthWest(location);
                    String southEast = getSouthEast(location);
                    String checksum = checksum(String.format("$GPGGA,%s,%s,%s,%s,%s,1,%s,692,.00,M,.00,M,,,", new Object[]{format, gPSLat, northWest, gPSLat2, southEast, Integer.valueOf(virtualGPSSatalines.getSvCount())}));
                    String checksum2 = checksum(String.format("$GPRMC,%s,A,%s,%s,%s,%s,0,0,260717,,,A,", new Object[]{format, gPSLat, northWest, gPSLat2, southEast}));
                    if (GnssStatusListenerTransport.onNmeaReceived != null) {
                        GnssStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), "$GPGSV,1,1,04,12,05,159,36,15,41,087,15,19,38,262,30,31,56,146,19,*73");
                        GnssStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), checksum);
                        GnssStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), "$GPVTG,0,T,0,M,0,N,0,K,A,*25");
                        GnssStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), checksum2);
                        GnssStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), "$GPGSA,A,2,12,15,19,31,,,,,,,,,604,712,986,*27");
                    } else if (GpsStatusListenerTransport.onNmeaReceived != null) {
                        GpsStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), "$GPGSV,1,1,04,12,05,159,36,15,41,087,15,19,38,262,30,31,56,146,19,*73");
                        GpsStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), checksum);
                        GpsStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), "$GPVTG,0,T,0,M,0,N,0,K,A,*25");
                        GpsStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), checksum2);
                        GpsStatusListenerTransport.onNmeaReceived.call(obj, Long.valueOf(System.currentTimeMillis()), "$GPGSA,A,2,12,15,19,31,,,,,,,,,604,712,986,*27");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setGpsStatus(Object obj) {
        Method method;
        VirtualGPSSatalines virtualGPSSatalines = VirtualGPSSatalines.get();
        int svCount = virtualGPSSatalines.getSvCount();
        float[] snrs = virtualGPSSatalines.getSnrs();
        int[] prns = virtualGPSSatalines.getPrns();
        float[] elevations = virtualGPSSatalines.getElevations();
        float[] azimuths = virtualGPSSatalines.getAzimuths();
        Object obj2 = Reflect.m80on(obj).get("mGpsStatus");
        try {
            method = obj2.getClass().getDeclaredMethod("setStatus", new Class[]{Integer.TYPE, int[].class, float[].class, float[].class, float[].class, Integer.TYPE, Integer.TYPE, Integer.TYPE});
            try {
                method.setAccessible(true);
                method.invoke(obj2, new Object[]{Integer.valueOf(svCount), prns, snrs, elevations, azimuths, Integer.valueOf(virtualGPSSatalines.getEphemerisMask()), Integer.valueOf(virtualGPSSatalines.getAlmanacMask()), Integer.valueOf(virtualGPSSatalines.getUsedInFixMask())});
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            method = null;
        }
        if (method == null) {
            try {
                Method declaredMethod = obj2.getClass().getDeclaredMethod("setStatus", new Class[]{Integer.TYPE, int[].class, float[].class, float[].class, float[].class, int[].class, int[].class, int[].class});
                declaredMethod.setAccessible(true);
                int svCount2 = virtualGPSSatalines.getSvCount();
                int length = virtualGPSSatalines.getPrns().length;
                float[] elevations2 = virtualGPSSatalines.getElevations();
                float[] azimuths2 = virtualGPSSatalines.getAzimuths();
                int[] iArr = new int[length];
                for (int i = 0; i < length; i++) {
                    iArr[i] = virtualGPSSatalines.getEphemerisMask();
                }
                int[] iArr2 = new int[length];
                for (int i2 = 0; i2 < length; i2++) {
                    iArr2[i2] = virtualGPSSatalines.getAlmanacMask();
                }
                int[] iArr3 = new int[length];
                for (int i3 = 0; i3 < length; i3++) {
                    iArr3[i3] = virtualGPSSatalines.getUsedInFixMask();
                }
                declaredMethod.invoke(obj2, new Object[]{Integer.valueOf(svCount2), prns, snrs, elevations2, azimuths2, iArr, iArr2, iArr3});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void invokeSvStatusChanged(Object obj) {
        Object obj2 = obj;
        if (obj2 != null) {
            VirtualGPSSatalines virtualGPSSatalines = VirtualGPSSatalines.get();
            try {
                Class<?> cls = obj.getClass();
                if (cls == GnssStatusListenerTransport.TYPE) {
                    int svCount = virtualGPSSatalines.getSvCount();
                    int[] prnWithFlags = virtualGPSSatalines.getPrnWithFlags();
                    float[] snrs = virtualGPSSatalines.getSnrs();
                    float[] elevations = virtualGPSSatalines.getElevations();
                    float[] azimuths = virtualGPSSatalines.getAzimuths();
                    GnssStatusListenerTransport.onSvStatusChanged.call(obj2, Integer.valueOf(svCount), prnWithFlags, snrs, elevations, azimuths);
                } else if (cls == GpsStatusListenerTransport.TYPE) {
                    int svCount2 = virtualGPSSatalines.getSvCount();
                    int[] prns = virtualGPSSatalines.getPrns();
                    float[] snrs2 = virtualGPSSatalines.getSnrs();
                    float[] elevations2 = virtualGPSSatalines.getElevations();
                    float[] azimuths2 = virtualGPSSatalines.getAzimuths();
                    int ephemerisMask = virtualGPSSatalines.getEphemerisMask();
                    int almanacMask = virtualGPSSatalines.getAlmanacMask();
                    int usedInFixMask = virtualGPSSatalines.getUsedInFixMask();
                    if (GpsStatusListenerTransport.onSvStatusChanged != null) {
                        GpsStatusListenerTransport.onSvStatusChanged.call(obj2, Integer.valueOf(svCount2), prns, snrs2, elevations2, azimuths2, Integer.valueOf(ephemerisMask), Integer.valueOf(almanacMask), Integer.valueOf(usedInFixMask));
                    } else if (GpsStatusListenerTransportVIVO.onSvStatusChanged != null) {
                        GpsStatusListenerTransportVIVO.onSvStatusChanged.call(obj2, Integer.valueOf(svCount2), prns, snrs2, elevations2, azimuths2, Integer.valueOf(ephemerisMask), Integer.valueOf(almanacMask), Integer.valueOf(usedInFixMask), new long[svCount2]);
                    } else if (GpsStatusListenerTransportSumsungS5.onSvStatusChanged != null) {
                        GpsStatusListenerTransportSumsungS5.onSvStatusChanged.call(obj2, Integer.valueOf(svCount2), prns, snrs2, elevations2, azimuths2, Integer.valueOf(ephemerisMask), Integer.valueOf(almanacMask), Integer.valueOf(usedInFixMask), new int[svCount2]);
                    } else if (GpsStatusListenerTransportOPPO_R815T.onSvStatusChanged != null) {
                        int length = prns.length;
                        int[] iArr = new int[length];
                        for (int i = 0; i < length; i++) {
                            iArr[i] = virtualGPSSatalines.getEphemerisMask();
                        }
                        int[] iArr2 = new int[length];
                        for (int i2 = 0; i2 < length; i2++) {
                            iArr2[i2] = virtualGPSSatalines.getAlmanacMask();
                        }
                        int[] iArr3 = new int[length];
                        for (int i3 = 0; i3 < length; i3++) {
                            iArr3[i3] = virtualGPSSatalines.getUsedInFixMask();
                        }
                        GpsStatusListenerTransportOPPO_R815T.onSvStatusChanged.call(obj2, Integer.valueOf(svCount2), prns, snrs2, elevations2, azimuths2, iArr, iArr2, iArr3, Integer.valueOf(svCount2));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getSouthEast(VLocation vLocation) {
        return vLocation.longitude > 0.0d ? "E" : "W";
    }

    private static String getNorthWest(VLocation vLocation) {
        return vLocation.latitude > 0.0d ? "N" : "S";
    }

    public static String getGPSLat(double d) {
        int i = (int) d;
        double d2 = (d - ((double) i)) * 60.0d;
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append(leftZeroPad((int) d2, 2));
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(String.valueOf(d2).substring(2));
        return sb.toString();
    }

    private static String leftZeroPad(int i, int i2) {
        return leftZeroPad(String.valueOf(i), i2);
    }

    private static String leftZeroPad(String str, int i) {
        StringBuilder sb = new StringBuilder(i);
        int i2 = 0;
        if (str == null) {
            while (i2 < i) {
                sb.append('0');
                i2++;
            }
        } else {
            while (i2 < i - str.length()) {
                sb.append('0');
                i2++;
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static String checksum(String str) {
        String substring = str.startsWith("$") ? str.substring(1) : str;
        byte b = 0;
        for (int i = 0; i < substring.length(); i++) {
            b ^= (byte) substring.charAt(i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(Marker.ANY_MARKER);
        sb.append(String.format("%02X", new Object[]{Integer.valueOf(b)}).toLowerCase());
        return sb.toString();
    }
}
