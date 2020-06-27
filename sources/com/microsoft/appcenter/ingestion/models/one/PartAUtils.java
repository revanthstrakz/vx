package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.utils.context.UserIdContext;
import java.util.Locale;
import java.util.regex.Pattern;
import org.slf4j.Marker;

public class PartAUtils {
    private static final Pattern NAME_REGEX = Pattern.compile("^[a-zA-Z0-9]((\\.(?!(\\.|$)))|[_a-zA-Z0-9]){3,99}$");

    public static String getTargetKey(String str) {
        return str.split("-")[0];
    }

    public static void setName(CommonSchemaLog commonSchemaLog, String str) throws IllegalArgumentException {
        if (str == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        } else if (NAME_REGEX.matcher(str).matches()) {
            commonSchemaLog.setName(str);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Name must match '");
            sb.append(NAME_REGEX);
            sb.append("' but was '");
            sb.append(str);
            sb.append("'.");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public static void addPartAFromLog(Log log, CommonSchemaLog commonSchemaLog, String str) {
        Device device = log.getDevice();
        commonSchemaLog.setVer("3.0");
        commonSchemaLog.setTimestamp(log.getTimestamp());
        StringBuilder sb = new StringBuilder();
        sb.append("o:");
        sb.append(getTargetKey(str));
        commonSchemaLog.setIKey(sb.toString());
        commonSchemaLog.addTransmissionTarget(str);
        if (commonSchemaLog.getExt() == null) {
            commonSchemaLog.setExt(new Extensions());
        }
        commonSchemaLog.getExt().setProtocol(new ProtocolExtension());
        commonSchemaLog.getExt().getProtocol().setDevModel(device.getModel());
        commonSchemaLog.getExt().getProtocol().setDevMake(device.getOemName());
        commonSchemaLog.getExt().setUser(new UserExtension());
        commonSchemaLog.getExt().getUser().setLocalId(UserIdContext.getPrefixedUserId(log.getUserId()));
        commonSchemaLog.getExt().getUser().setLocale(device.getLocale().replace("_", "-"));
        commonSchemaLog.getExt().setOs(new OsExtension());
        commonSchemaLog.getExt().getOs().setName(device.getOsName());
        OsExtension os = commonSchemaLog.getExt().getOs();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(device.getOsVersion());
        sb2.append("-");
        sb2.append(device.getOsBuild());
        sb2.append("-");
        sb2.append(device.getOsApiLevel());
        os.setVer(sb2.toString());
        commonSchemaLog.getExt().setApp(new AppExtension());
        commonSchemaLog.getExt().getApp().setVer(device.getAppVersion());
        AppExtension app = commonSchemaLog.getExt().getApp();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("a:");
        sb3.append(device.getAppNamespace());
        app.setId(sb3.toString());
        commonSchemaLog.getExt().setNet(new NetExtension());
        commonSchemaLog.getExt().getNet().setProvider(device.getCarrierName());
        commonSchemaLog.getExt().setSdk(new SdkExtension());
        SdkExtension sdk = commonSchemaLog.getExt().getSdk();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(device.getSdkName());
        sb4.append("-");
        sb4.append(device.getSdkVersion());
        sdk.setLibVer(sb4.toString());
        commonSchemaLog.getExt().setLoc(new LocExtension());
        Locale locale = Locale.US;
        String str2 = "%s%02d:%02d";
        Object[] objArr = new Object[3];
        objArr[0] = device.getTimeZoneOffset().intValue() >= 0 ? Marker.ANY_NON_NULL_MARKER : "-";
        objArr[1] = Integer.valueOf(Math.abs(device.getTimeZoneOffset().intValue() / 60));
        objArr[2] = Integer.valueOf(Math.abs(device.getTimeZoneOffset().intValue() % 60));
        commonSchemaLog.getExt().getLoc().setTz(String.format(locale, str2, objArr));
        commonSchemaLog.getExt().setDevice(new DeviceExtension());
    }
}
