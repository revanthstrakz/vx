package com.microsoft.appcenter.ingestion.models;

import android.support.annotation.VisibleForTesting;
import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public abstract class AbstractLog implements Log {
    @VisibleForTesting
    static final String DEVICE = "device";
    private static final String DISTRIBUTION_GROUP_ID = "distributionGroupId";
    private static final String SID = "sid";
    private static final String TIMESTAMP = "timestamp";
    private static final String USER_ID = "userId";
    private Device device;
    private String distributionGroupId;
    private UUID sid;
    private Object tag;
    private Date timestamp;
    private final Set<String> transmissionTargetTokens = new LinkedHashSet();
    private String userId;

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    public UUID getSid() {
        return this.sid;
    }

    public void setSid(UUID uuid) {
        this.sid = uuid;
    }

    public String getDistributionGroupId() {
        return this.distributionGroupId;
    }

    public void setDistributionGroupId(String str) {
        this.distributionGroupId = str;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String str) {
        this.userId = str;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device2) {
        this.device = device2;
    }

    public Object getTag() {
        return this.tag;
    }

    public void setTag(Object obj) {
        this.tag = obj;
    }

    public synchronized void addTransmissionTarget(String str) {
        this.transmissionTargetTokens.add(str);
    }

    public synchronized Set<String> getTransmissionTargetTokens() {
        return Collections.unmodifiableSet(this.transmissionTargetTokens);
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, CommonProperties.TYPE, getType());
        jSONStringer.key(TIMESTAMP).value(JSONDateUtils.toString(getTimestamp()));
        JSONUtils.write(jSONStringer, SID, getSid());
        JSONUtils.write(jSONStringer, DISTRIBUTION_GROUP_ID, getDistributionGroupId());
        JSONUtils.write(jSONStringer, "userId", getUserId());
        if (getDevice() != null) {
            jSONStringer.key("device").object();
            getDevice().write(jSONStringer);
            jSONStringer.endObject();
        }
    }

    public void read(JSONObject jSONObject) throws JSONException {
        if (jSONObject.getString(CommonProperties.TYPE).equals(getType())) {
            setTimestamp(JSONDateUtils.toDate(jSONObject.getString(TIMESTAMP)));
            if (jSONObject.has(SID)) {
                setSid(UUID.fromString(jSONObject.getString(SID)));
            }
            setDistributionGroupId(jSONObject.optString(DISTRIBUTION_GROUP_ID, null));
            setUserId(jSONObject.optString("userId", null));
            if (jSONObject.has("device")) {
                Device device2 = new Device();
                device2.read(jSONObject.getJSONObject("device"));
                setDevice(device2);
                return;
            }
            return;
        }
        throw new JSONException("Invalid type");
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractLog abstractLog = (AbstractLog) obj;
        if (!this.transmissionTargetTokens.equals(abstractLog.transmissionTargetTokens)) {
            return false;
        }
        if (this.timestamp == null ? abstractLog.timestamp != null : !this.timestamp.equals(abstractLog.timestamp)) {
            return false;
        }
        if (this.sid == null ? abstractLog.sid != null : !this.sid.equals(abstractLog.sid)) {
            return false;
        }
        if (this.distributionGroupId == null ? abstractLog.distributionGroupId != null : !this.distributionGroupId.equals(abstractLog.distributionGroupId)) {
            return false;
        }
        if (this.userId == null ? abstractLog.userId != null : !this.userId.equals(abstractLog.userId)) {
            return false;
        }
        if (this.device == null ? abstractLog.device != null : !this.device.equals(abstractLog.device)) {
            return false;
        }
        if (this.tag != null) {
            z = this.tag.equals(abstractLog.tag);
        } else if (abstractLog.tag != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((((((((this.transmissionTargetTokens.hashCode() * 31) + (this.timestamp != null ? this.timestamp.hashCode() : 0)) * 31) + (this.sid != null ? this.sid.hashCode() : 0)) * 31) + (this.distributionGroupId != null ? this.distributionGroupId.hashCode() : 0)) * 31) + (this.userId != null ? this.userId.hashCode() : 0)) * 31) + (this.device != null ? this.device.hashCode() : 0)) * 31;
        if (this.tag != null) {
            i = this.tag.hashCode();
        }
        return hashCode + i;
    }
}
