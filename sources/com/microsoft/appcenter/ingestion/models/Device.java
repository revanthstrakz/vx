package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class Device extends WrapperSdk {
    private static final String APP_BUILD = "appBuild";
    private static final String APP_NAMESPACE = "appNamespace";
    private static final String APP_VERSION = "appVersion";
    private static final String CARRIER_COUNTRY = "carrierCountry";
    private static final String CARRIER_NAME = "carrierName";
    private static final String LOCALE = "locale";
    private static final String MODEL = "model";
    private static final String OEM_NAME = "oemName";
    private static final String OS_API_LEVEL = "osApiLevel";
    private static final String OS_BUILD = "osBuild";
    private static final String OS_NAME = "osName";
    private static final String OS_VERSION = "osVersion";
    private static final String SCREEN_SIZE = "screenSize";
    private static final String SDK_NAME = "sdkName";
    private static final String SDK_VERSION = "sdkVersion";
    private static final String TIME_ZONE_OFFSET = "timeZoneOffset";
    private String appBuild;
    private String appNamespace;
    private String appVersion;
    private String carrierCountry;
    private String carrierName;
    private String locale;
    private String model;
    private String oemName;
    private Integer osApiLevel;
    private String osBuild;
    private String osName;
    private String osVersion;
    private String screenSize;
    private String sdkName;
    private String sdkVersion;
    private Integer timeZoneOffset;

    public String getSdkName() {
        return this.sdkName;
    }

    public void setSdkName(String str) {
        this.sdkName = str;
    }

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public void setSdkVersion(String str) {
        this.sdkVersion = str;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String str) {
        this.model = str;
    }

    public String getOemName() {
        return this.oemName;
    }

    public void setOemName(String str) {
        this.oemName = str;
    }

    public String getOsName() {
        return this.osName;
    }

    public void setOsName(String str) {
        this.osName = str;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public void setOsVersion(String str) {
        this.osVersion = str;
    }

    public String getOsBuild() {
        return this.osBuild;
    }

    public void setOsBuild(String str) {
        this.osBuild = str;
    }

    public Integer getOsApiLevel() {
        return this.osApiLevel;
    }

    public void setOsApiLevel(Integer num) {
        this.osApiLevel = num;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String str) {
        this.locale = str;
    }

    public Integer getTimeZoneOffset() {
        return this.timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer num) {
        this.timeZoneOffset = num;
    }

    public String getScreenSize() {
        return this.screenSize;
    }

    public void setScreenSize(String str) {
        this.screenSize = str;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public void setAppVersion(String str) {
        this.appVersion = str;
    }

    public String getCarrierName() {
        return this.carrierName;
    }

    public void setCarrierName(String str) {
        this.carrierName = str;
    }

    public String getCarrierCountry() {
        return this.carrierCountry;
    }

    public void setCarrierCountry(String str) {
        this.carrierCountry = str;
    }

    public String getAppBuild() {
        return this.appBuild;
    }

    public void setAppBuild(String str) {
        this.appBuild = str;
    }

    public String getAppNamespace() {
        return this.appNamespace;
    }

    public void setAppNamespace(String str) {
        this.appNamespace = str;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setSdkName(jSONObject.getString(SDK_NAME));
        setSdkVersion(jSONObject.getString(SDK_VERSION));
        setModel(jSONObject.getString(MODEL));
        setOemName(jSONObject.getString(OEM_NAME));
        setOsName(jSONObject.getString(OS_NAME));
        setOsVersion(jSONObject.getString(OS_VERSION));
        setOsBuild(jSONObject.optString(OS_BUILD, null));
        setOsApiLevel(JSONUtils.readInteger(jSONObject, OS_API_LEVEL));
        setLocale(jSONObject.getString(LOCALE));
        setTimeZoneOffset(Integer.valueOf(jSONObject.getInt(TIME_ZONE_OFFSET)));
        setScreenSize(jSONObject.getString(SCREEN_SIZE));
        setAppVersion(jSONObject.getString(APP_VERSION));
        setCarrierName(jSONObject.optString(CARRIER_NAME, null));
        setCarrierCountry(jSONObject.optString(CARRIER_COUNTRY, null));
        setAppBuild(jSONObject.getString(APP_BUILD));
        setAppNamespace(jSONObject.optString(APP_NAMESPACE, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        jSONStringer.key(SDK_NAME).value(getSdkName());
        jSONStringer.key(SDK_VERSION).value(getSdkVersion());
        jSONStringer.key(MODEL).value(getModel());
        jSONStringer.key(OEM_NAME).value(getOemName());
        jSONStringer.key(OS_NAME).value(getOsName());
        jSONStringer.key(OS_VERSION).value(getOsVersion());
        JSONUtils.write(jSONStringer, OS_BUILD, getOsBuild());
        JSONUtils.write(jSONStringer, OS_API_LEVEL, getOsApiLevel());
        jSONStringer.key(LOCALE).value(getLocale());
        jSONStringer.key(TIME_ZONE_OFFSET).value(getTimeZoneOffset());
        jSONStringer.key(SCREEN_SIZE).value(getScreenSize());
        jSONStringer.key(APP_VERSION).value(getAppVersion());
        JSONUtils.write(jSONStringer, CARRIER_NAME, getCarrierName());
        JSONUtils.write(jSONStringer, CARRIER_COUNTRY, getCarrierCountry());
        jSONStringer.key(APP_BUILD).value(getAppBuild());
        JSONUtils.write(jSONStringer, APP_NAMESPACE, getAppNamespace());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        Device device = (Device) obj;
        if (this.sdkName == null ? device.sdkName != null : !this.sdkName.equals(device.sdkName)) {
            return false;
        }
        if (this.sdkVersion == null ? device.sdkVersion != null : !this.sdkVersion.equals(device.sdkVersion)) {
            return false;
        }
        if (this.model == null ? device.model != null : !this.model.equals(device.model)) {
            return false;
        }
        if (this.oemName == null ? device.oemName != null : !this.oemName.equals(device.oemName)) {
            return false;
        }
        if (this.osName == null ? device.osName != null : !this.osName.equals(device.osName)) {
            return false;
        }
        if (this.osVersion == null ? device.osVersion != null : !this.osVersion.equals(device.osVersion)) {
            return false;
        }
        if (this.osBuild == null ? device.osBuild != null : !this.osBuild.equals(device.osBuild)) {
            return false;
        }
        if (this.osApiLevel == null ? device.osApiLevel != null : !this.osApiLevel.equals(device.osApiLevel)) {
            return false;
        }
        if (this.locale == null ? device.locale != null : !this.locale.equals(device.locale)) {
            return false;
        }
        if (this.timeZoneOffset == null ? device.timeZoneOffset != null : !this.timeZoneOffset.equals(device.timeZoneOffset)) {
            return false;
        }
        if (this.screenSize == null ? device.screenSize != null : !this.screenSize.equals(device.screenSize)) {
            return false;
        }
        if (this.appVersion == null ? device.appVersion != null : !this.appVersion.equals(device.appVersion)) {
            return false;
        }
        if (this.carrierName == null ? device.carrierName != null : !this.carrierName.equals(device.carrierName)) {
            return false;
        }
        if (this.carrierCountry == null ? device.carrierCountry != null : !this.carrierCountry.equals(device.carrierCountry)) {
            return false;
        }
        if (this.appBuild == null ? device.appBuild != null : !this.appBuild.equals(device.appBuild)) {
            return false;
        }
        if (this.appNamespace != null) {
            z = this.appNamespace.equals(device.appNamespace);
        } else if (device.appNamespace != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((((((((((((((((((((((((((((super.hashCode() * 31) + (this.sdkName != null ? this.sdkName.hashCode() : 0)) * 31) + (this.sdkVersion != null ? this.sdkVersion.hashCode() : 0)) * 31) + (this.model != null ? this.model.hashCode() : 0)) * 31) + (this.oemName != null ? this.oemName.hashCode() : 0)) * 31) + (this.osName != null ? this.osName.hashCode() : 0)) * 31) + (this.osVersion != null ? this.osVersion.hashCode() : 0)) * 31) + (this.osBuild != null ? this.osBuild.hashCode() : 0)) * 31) + (this.osApiLevel != null ? this.osApiLevel.hashCode() : 0)) * 31) + (this.locale != null ? this.locale.hashCode() : 0)) * 31) + (this.timeZoneOffset != null ? this.timeZoneOffset.hashCode() : 0)) * 31) + (this.screenSize != null ? this.screenSize.hashCode() : 0)) * 31) + (this.appVersion != null ? this.appVersion.hashCode() : 0)) * 31) + (this.carrierName != null ? this.carrierName.hashCode() : 0)) * 31) + (this.carrierCountry != null ? this.carrierCountry.hashCode() : 0)) * 31) + (this.appBuild != null ? this.appBuild.hashCode() : 0)) * 31;
        if (this.appNamespace != null) {
            i = this.appNamespace.hashCode();
        }
        return hashCode + i;
    }
}
