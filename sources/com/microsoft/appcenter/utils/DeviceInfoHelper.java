package com.microsoft.appcenter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.ingestion.models.WrapperSdk;
import java.util.Locale;
import java.util.TimeZone;

public class DeviceInfoHelper {
    private static final String OS_NAME = "Android";
    private static WrapperSdk sWrapperSdk;

    public static class DeviceInfoException extends Exception {
        public DeviceInfoException(String str, Throwable th) {
            super(str, th);
        }
    }

    public static synchronized Device getDeviceInfo(Context context) throws DeviceInfoException {
        Device device;
        synchronized (DeviceInfoHelper.class) {
            device = new Device();
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                device.setAppVersion(packageInfo.versionName);
                device.setAppBuild(String.valueOf(getVersionCode(packageInfo)));
                device.setAppNamespace(context.getPackageName());
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                String networkCountryIso = telephonyManager.getNetworkCountryIso();
                if (!TextUtils.isEmpty(networkCountryIso)) {
                    device.setCarrierCountry(networkCountryIso);
                }
                String networkOperatorName = telephonyManager.getNetworkOperatorName();
                if (!TextUtils.isEmpty(networkOperatorName)) {
                    device.setCarrierName(networkOperatorName);
                }
            } catch (Exception e) {
                AppCenterLog.error("AppCenter", "Cannot retrieve package info", e);
                throw new DeviceInfoException("Cannot retrieve package info", e);
            } catch (Exception e2) {
                AppCenterLog.error("AppCenter", "Cannot retrieve carrier info", e2);
            }
            device.setLocale(Locale.getDefault().toString());
            device.setModel(Build.MODEL);
            device.setOemName(Build.MANUFACTURER);
            device.setOsApiLevel(Integer.valueOf(VERSION.SDK_INT));
            device.setOsName(OS_NAME);
            device.setOsVersion(VERSION.RELEASE);
            device.setOsBuild(Build.ID);
            try {
                device.setScreenSize(getScreenSize(context));
            } catch (Exception e3) {
                AppCenterLog.error("AppCenter", "Cannot retrieve screen size", e3);
            }
            device.setSdkName("appcenter.android");
            device.setSdkVersion("3.0.0");
            device.setTimeZoneOffset(Integer.valueOf((TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 60) / 1000));
            if (sWrapperSdk != null) {
                device.setWrapperSdkVersion(sWrapperSdk.getWrapperSdkVersion());
                device.setWrapperSdkName(sWrapperSdk.getWrapperSdkName());
                device.setWrapperRuntimeVersion(sWrapperSdk.getWrapperRuntimeVersion());
                device.setLiveUpdateReleaseLabel(sWrapperSdk.getLiveUpdateReleaseLabel());
                device.setLiveUpdateDeploymentKey(sWrapperSdk.getLiveUpdateDeploymentKey());
                device.setLiveUpdatePackageHash(sWrapperSdk.getLiveUpdatePackageHash());
            }
        }
        return device;
    }

    public static int getVersionCode(PackageInfo packageInfo) {
        return packageInfo.versionCode;
    }

    @SuppressLint({"SwitchIntDef"})
    private static String getScreenSize(Context context) {
        int i;
        int i2;
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int rotation = defaultDisplay.getRotation();
        if (rotation == 1 || rotation == 3) {
            int i3 = point.x;
            int i4 = point.y;
            i2 = i3;
            i = i4;
        } else {
            i = point.x;
            i2 = point.y;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append("x");
        sb.append(i2);
        return sb.toString();
    }

    public static synchronized void setWrapperSdk(WrapperSdk wrapperSdk) {
        synchronized (DeviceInfoHelper.class) {
            sWrapperSdk = wrapperSdk;
        }
    }
}
