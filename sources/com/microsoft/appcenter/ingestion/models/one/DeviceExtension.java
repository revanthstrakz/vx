package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class DeviceExtension implements Model {
    private static final String LOCAL_ID = "localId";
    private String localId;

    public String getLocalId() {
        return this.localId;
    }

    public void setLocalId(String str) {
        this.localId = str;
    }

    public void read(JSONObject jSONObject) {
        setLocalId(jSONObject.optString(LOCAL_ID, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, LOCAL_ID, getLocalId());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DeviceExtension deviceExtension = (DeviceExtension) obj;
        if (this.localId != null) {
            z = this.localId.equals(deviceExtension.localId);
        } else if (deviceExtension.localId != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        if (this.localId != null) {
            return this.localId.hashCode();
        }
        return 0;
    }
}
