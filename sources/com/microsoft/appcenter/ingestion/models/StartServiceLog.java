package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class StartServiceLog extends AbstractLog {
    private static final String SERVICES = "services";
    public static final String TYPE = "startService";
    private List<String> services;

    public String getType() {
        return TYPE;
    }

    public List<String> getServices() {
        return this.services;
    }

    public void setServices(List<String> list) {
        this.services = list;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setServices(JSONUtils.readStringArray(jSONObject, SERVICES));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        JSONUtils.writeStringArray(jSONStringer, SERVICES, getServices());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        StartServiceLog startServiceLog = (StartServiceLog) obj;
        if (this.services != null) {
            z = this.services.equals(startServiceLog.services);
        } else if (startServiceLog.services != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (super.hashCode() * 31) + (this.services != null ? this.services.hashCode() : 0);
    }
}
