package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class WrapperSdk implements Model {
    private static final String LIVE_UPDATE_DEPLOYMENT_KEY = "liveUpdateDeploymentKey";
    private static final String LIVE_UPDATE_PACKAGE_HASH = "liveUpdatePackageHash";
    private static final String LIVE_UPDATE_RELEASE_LABEL = "liveUpdateReleaseLabel";
    private static final String WRAPPER_RUNTIME_VERSION = "wrapperRuntimeVersion";
    private static final String WRAPPER_SDK_NAME = "wrapperSdkName";
    private static final String WRAPPER_SDK_VERSION = "wrapperSdkVersion";
    private String liveUpdateDeploymentKey;
    private String liveUpdatePackageHash;
    private String liveUpdateReleaseLabel;
    private String wrapperRuntimeVersion;
    private String wrapperSdkName;
    private String wrapperSdkVersion;

    public String getWrapperSdkVersion() {
        return this.wrapperSdkVersion;
    }

    public void setWrapperSdkVersion(String str) {
        this.wrapperSdkVersion = str;
    }

    public String getWrapperSdkName() {
        return this.wrapperSdkName;
    }

    public void setWrapperSdkName(String str) {
        this.wrapperSdkName = str;
    }

    public String getWrapperRuntimeVersion() {
        return this.wrapperRuntimeVersion;
    }

    public void setWrapperRuntimeVersion(String str) {
        this.wrapperRuntimeVersion = str;
    }

    public String getLiveUpdateReleaseLabel() {
        return this.liveUpdateReleaseLabel;
    }

    public void setLiveUpdateReleaseLabel(String str) {
        this.liveUpdateReleaseLabel = str;
    }

    public String getLiveUpdateDeploymentKey() {
        return this.liveUpdateDeploymentKey;
    }

    public void setLiveUpdateDeploymentKey(String str) {
        this.liveUpdateDeploymentKey = str;
    }

    public String getLiveUpdatePackageHash() {
        return this.liveUpdatePackageHash;
    }

    public void setLiveUpdatePackageHash(String str) {
        this.liveUpdatePackageHash = str;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        setWrapperSdkVersion(jSONObject.optString(WRAPPER_SDK_VERSION, null));
        setWrapperSdkName(jSONObject.optString(WRAPPER_SDK_NAME, null));
        setWrapperRuntimeVersion(jSONObject.optString(WRAPPER_RUNTIME_VERSION, null));
        setLiveUpdateReleaseLabel(jSONObject.optString(LIVE_UPDATE_RELEASE_LABEL, null));
        setLiveUpdateDeploymentKey(jSONObject.optString(LIVE_UPDATE_DEPLOYMENT_KEY, null));
        setLiveUpdatePackageHash(jSONObject.optString(LIVE_UPDATE_PACKAGE_HASH, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, WRAPPER_SDK_VERSION, getWrapperSdkVersion());
        JSONUtils.write(jSONStringer, WRAPPER_SDK_NAME, getWrapperSdkName());
        JSONUtils.write(jSONStringer, WRAPPER_RUNTIME_VERSION, getWrapperRuntimeVersion());
        JSONUtils.write(jSONStringer, LIVE_UPDATE_RELEASE_LABEL, getLiveUpdateReleaseLabel());
        JSONUtils.write(jSONStringer, LIVE_UPDATE_DEPLOYMENT_KEY, getLiveUpdateDeploymentKey());
        JSONUtils.write(jSONStringer, LIVE_UPDATE_PACKAGE_HASH, getLiveUpdatePackageHash());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        WrapperSdk wrapperSdk = (WrapperSdk) obj;
        if (this.wrapperSdkVersion == null ? wrapperSdk.wrapperSdkVersion != null : !this.wrapperSdkVersion.equals(wrapperSdk.wrapperSdkVersion)) {
            return false;
        }
        if (this.wrapperSdkName == null ? wrapperSdk.wrapperSdkName != null : !this.wrapperSdkName.equals(wrapperSdk.wrapperSdkName)) {
            return false;
        }
        if (this.wrapperRuntimeVersion == null ? wrapperSdk.wrapperRuntimeVersion != null : !this.wrapperRuntimeVersion.equals(wrapperSdk.wrapperRuntimeVersion)) {
            return false;
        }
        if (this.liveUpdateReleaseLabel == null ? wrapperSdk.liveUpdateReleaseLabel != null : !this.liveUpdateReleaseLabel.equals(wrapperSdk.liveUpdateReleaseLabel)) {
            return false;
        }
        if (this.liveUpdateDeploymentKey == null ? wrapperSdk.liveUpdateDeploymentKey != null : !this.liveUpdateDeploymentKey.equals(wrapperSdk.liveUpdateDeploymentKey)) {
            return false;
        }
        if (this.liveUpdatePackageHash != null) {
            z = this.liveUpdatePackageHash.equals(wrapperSdk.liveUpdatePackageHash);
        } else if (wrapperSdk.liveUpdatePackageHash != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((((((((this.wrapperSdkVersion != null ? this.wrapperSdkVersion.hashCode() : 0) * 31) + (this.wrapperSdkName != null ? this.wrapperSdkName.hashCode() : 0)) * 31) + (this.wrapperRuntimeVersion != null ? this.wrapperRuntimeVersion.hashCode() : 0)) * 31) + (this.liveUpdateReleaseLabel != null ? this.liveUpdateReleaseLabel.hashCode() : 0)) * 31) + (this.liveUpdateDeploymentKey != null ? this.liveUpdateDeploymentKey.hashCode() : 0)) * 31;
        if (this.liveUpdatePackageHash != null) {
            i = this.liveUpdatePackageHash.hashCode();
        }
        return hashCode + i;
    }
}
