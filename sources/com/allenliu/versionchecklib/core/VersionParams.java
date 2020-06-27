package com.allenliu.versionchecklib.core;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.allenliu.versionchecklib.core.http.HttpHeaders;
import com.allenliu.versionchecklib.core.http.HttpParams;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.utils.FileHelper;

public class VersionParams implements Parcelable {
    public static final Creator<VersionParams> CREATOR = new Creator<VersionParams>() {
        public VersionParams createFromParcel(Parcel parcel) {
            return new VersionParams(parcel);
        }

        public VersionParams[] newArray(int i) {
            return new VersionParams[i];
        }
    };
    /* access modifiers changed from: private */
    public Class<? extends VersionDialogActivity> customDownloadActivityClass;
    /* access modifiers changed from: private */
    public String downloadAPKPath;
    /* access modifiers changed from: private */
    public String downloadUrl;
    /* access modifiers changed from: private */
    public HttpHeaders httpHeaders;
    public boolean isForceRedownload;
    /* access modifiers changed from: private */
    public boolean isShowDownloadingDialog;
    /* access modifiers changed from: private */
    public boolean isShowNotification;
    public boolean isSilentDownload;
    /* access modifiers changed from: private */
    public boolean onlyDownload;
    /* access modifiers changed from: private */
    public Bundle paramBundle;
    /* access modifiers changed from: private */
    public long pauseRequestTime;
    /* access modifiers changed from: private */
    public HttpRequestMethod requestMethod;
    /* access modifiers changed from: private */
    public HttpParams requestParams;
    /* access modifiers changed from: private */
    public String requestUrl;
    /* access modifiers changed from: private */
    public Class<? extends AVersionService> service;
    /* access modifiers changed from: private */
    public String title;
    /* access modifiers changed from: private */
    public String updateMsg;

    public static class Builder {
        VersionParams params = new VersionParams();

        public Builder() {
            this.params.downloadAPKPath = FileHelper.getDownloadApkCachePath();
            this.params.pauseRequestTime = 30000;
            this.params.requestMethod = HttpRequestMethod.GET;
            this.params.customDownloadActivityClass = VersionDialogActivity.class;
            this.params.isForceRedownload = false;
            this.params.isSilentDownload = false;
            this.params.onlyDownload = false;
            this.params.service = MyService.class;
            this.params.isShowNotification = true;
            this.params.isShowDownloadingDialog = true;
        }

        public Builder setParamBundle(Bundle bundle) {
            this.params.paramBundle = bundle;
            return this;
        }

        public Builder setDownloadUrl(String str) {
            this.params.downloadUrl = str;
            return this;
        }

        public Builder setTitle(String str) {
            this.params.title = str;
            return this;
        }

        public Builder setUpdateMsg(String str) {
            this.params.updateMsg = str;
            return this;
        }

        public Builder setOnlyDownload(boolean z) {
            this.params.onlyDownload = z;
            return this;
        }

        public Builder setRequestUrl(String str) {
            this.params.requestUrl = str;
            return this;
        }

        public Builder setDownloadAPKPath(String str) {
            this.params.downloadAPKPath = str;
            return this;
        }

        public Builder setHttpHeaders(HttpHeaders httpHeaders) {
            this.params.httpHeaders = httpHeaders;
            return this;
        }

        public Builder setPauseRequestTime(long j) {
            this.params.pauseRequestTime = j;
            return this;
        }

        public Builder setRequestMethod(HttpRequestMethod httpRequestMethod) {
            this.params.requestMethod = httpRequestMethod;
            return this;
        }

        public Builder setRequestParams(HttpParams httpParams) {
            this.params.requestParams = httpParams;
            return this;
        }

        public Builder setCustomDownloadActivityClass(Class cls) {
            this.params.customDownloadActivityClass = cls;
            return this;
        }

        public Builder setForceRedownload(boolean z) {
            this.params.isForceRedownload = z;
            return this;
        }

        public Builder setSilentDownload(boolean z) {
            this.params.isSilentDownload = z;
            return this;
        }

        public Builder setService(Class<? extends AVersionService> cls) {
            this.params.service = cls;
            return this;
        }

        public Builder setShowDownloadingDialog(boolean z) {
            this.params.isShowDownloadingDialog = z;
            return this;
        }

        public Builder setShowNotification(boolean z) {
            this.params.isShowNotification = z;
            return this;
        }

        public VersionParams build() {
            return this.params;
        }
    }

    public int describeContents() {
        return 0;
    }

    public boolean isShowDownloadingDialog() {
        return this.isShowDownloadingDialog;
    }

    public boolean isShowNotification() {
        return this.isShowNotification;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public String getUpdateMsg() {
        return this.updateMsg;
    }

    public Bundle getParamBundle() {
        return this.paramBundle;
    }

    private VersionParams() {
    }

    public VersionParams(String str, String str2, HttpHeaders httpHeaders2, long j, HttpRequestMethod httpRequestMethod, HttpParams httpParams, Class<? extends VersionDialogActivity> cls, boolean z, boolean z2, Class<? extends AVersionService> cls2, boolean z3, String str3, String str4, String str5, Bundle bundle) {
        String str6 = str;
        this.requestUrl = str6;
        this.downloadAPKPath = str2;
        this.httpHeaders = httpHeaders2;
        this.pauseRequestTime = j;
        this.requestMethod = httpRequestMethod;
        this.requestParams = httpParams;
        this.customDownloadActivityClass = cls;
        this.isForceRedownload = z;
        this.isSilentDownload = z2;
        this.service = cls2;
        this.onlyDownload = z3;
        this.title = str3;
        this.downloadUrl = str4;
        this.updateMsg = str5;
        this.paramBundle = bundle;
        if (this.service == null) {
            throw new RuntimeException("you must define your service which extends AVService.");
        } else if (str6 == null) {
            throw new RuntimeException("requestUrl is needed.");
        }
    }

    public Class<? extends AVersionService> getService() {
        return this.service;
    }

    public String getRequestUrl() {
        return this.requestUrl;
    }

    public String getDownloadAPKPath() {
        return this.downloadAPKPath;
    }

    public HttpHeaders getHttpHeaders() {
        return this.httpHeaders;
    }

    public long getPauseRequestTime() {
        return this.pauseRequestTime;
    }

    public HttpRequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public HttpParams getRequestParams() {
        return this.requestParams;
    }

    public Class getCustomDownloadActivityClass() {
        return this.customDownloadActivityClass;
    }

    public boolean isForceRedownload() {
        return this.isForceRedownload;
    }

    public boolean isSilentDownload() {
        return this.isSilentDownload;
    }

    public boolean isOnlyDownload() {
        return this.onlyDownload;
    }

    public void setParamBundle(Bundle bundle) {
        this.paramBundle = bundle;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.requestUrl);
        parcel.writeString(this.downloadAPKPath);
        parcel.writeSerializable(this.httpHeaders);
        parcel.writeLong(this.pauseRequestTime);
        parcel.writeInt(this.requestMethod == null ? -1 : this.requestMethod.ordinal());
        parcel.writeSerializable(this.requestParams);
        parcel.writeSerializable(this.customDownloadActivityClass);
        parcel.writeByte(this.isForceRedownload ? (byte) 1 : 0);
        parcel.writeByte(this.isSilentDownload ? (byte) 1 : 0);
        parcel.writeSerializable(this.service);
        parcel.writeByte(this.onlyDownload ? (byte) 1 : 0);
        parcel.writeString(this.title);
        parcel.writeString(this.downloadUrl);
        parcel.writeString(this.updateMsg);
        parcel.writeBundle(this.paramBundle);
        parcel.writeByte(this.isShowDownloadingDialog ? (byte) 1 : 0);
        parcel.writeByte(this.isShowNotification ? (byte) 1 : 0);
    }

    protected VersionParams(Parcel parcel) {
        HttpRequestMethod httpRequestMethod;
        this.requestUrl = parcel.readString();
        this.downloadAPKPath = parcel.readString();
        this.httpHeaders = (HttpHeaders) parcel.readSerializable();
        this.pauseRequestTime = parcel.readLong();
        int readInt = parcel.readInt();
        if (readInt == -1) {
            httpRequestMethod = null;
        } else {
            httpRequestMethod = HttpRequestMethod.values()[readInt];
        }
        this.requestMethod = httpRequestMethod;
        this.requestParams = (HttpParams) parcel.readSerializable();
        this.customDownloadActivityClass = (Class) parcel.readSerializable();
        boolean z = false;
        this.isForceRedownload = parcel.readByte() != 0;
        this.isSilentDownload = parcel.readByte() != 0;
        this.service = (Class) parcel.readSerializable();
        this.onlyDownload = parcel.readByte() != 0;
        this.title = parcel.readString();
        this.downloadUrl = parcel.readString();
        this.updateMsg = parcel.readString();
        this.paramBundle = parcel.readBundle();
        this.isShowDownloadingDialog = parcel.readByte() != 0;
        if (parcel.readByte() != 0) {
            z = true;
        }
        this.isShowNotification = z;
    }
}
