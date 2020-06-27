package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class SdkExtension implements Model {
    private static final String EPOCH = "epoch";
    private static final String INSTALL_ID = "installId";
    private static final String LIB_VER = "libVer";
    private static final String SEQ = "seq";
    private String epoch;
    private UUID installId;
    private String libVer;
    private Long seq;

    public String getLibVer() {
        return this.libVer;
    }

    public void setLibVer(String str) {
        this.libVer = str;
    }

    public String getEpoch() {
        return this.epoch;
    }

    public void setEpoch(String str) {
        this.epoch = str;
    }

    public Long getSeq() {
        return this.seq;
    }

    public void setSeq(Long l) {
        this.seq = l;
    }

    public UUID getInstallId() {
        return this.installId;
    }

    public void setInstallId(UUID uuid) {
        this.installId = uuid;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        setLibVer(jSONObject.optString(LIB_VER, null));
        setEpoch(jSONObject.optString(EPOCH, null));
        setSeq(JSONUtils.readLong(jSONObject, SEQ));
        if (jSONObject.has("installId")) {
            setInstallId(UUID.fromString(jSONObject.getString("installId")));
        }
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, LIB_VER, getLibVer());
        JSONUtils.write(jSONStringer, EPOCH, getEpoch());
        JSONUtils.write(jSONStringer, SEQ, getSeq());
        JSONUtils.write(jSONStringer, "installId", getInstallId());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SdkExtension sdkExtension = (SdkExtension) obj;
        if (this.libVer == null ? sdkExtension.libVer != null : !this.libVer.equals(sdkExtension.libVer)) {
            return false;
        }
        if (this.epoch == null ? sdkExtension.epoch != null : !this.epoch.equals(sdkExtension.epoch)) {
            return false;
        }
        if (this.seq == null ? sdkExtension.seq != null : !this.seq.equals(sdkExtension.seq)) {
            return false;
        }
        if (this.installId != null) {
            z = this.installId.equals(sdkExtension.installId);
        } else if (sdkExtension.installId != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((((this.libVer != null ? this.libVer.hashCode() : 0) * 31) + (this.epoch != null ? this.epoch.hashCode() : 0)) * 31) + (this.seq != null ? this.seq.hashCode() : 0)) * 31;
        if (this.installId != null) {
            i = this.installId.hashCode();
        }
        return hashCode + i;
    }
}
