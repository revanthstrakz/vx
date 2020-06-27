package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public abstract class LogWithProperties extends AbstractLog {
    private static final String PROPERTIES = "properties";
    private Map<String, String> properties;

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> map) {
        this.properties = map;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setProperties(JSONUtils.readMap(jSONObject, PROPERTIES));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        JSONUtils.writeMap(jSONStringer, PROPERTIES, getProperties());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        LogWithProperties logWithProperties = (LogWithProperties) obj;
        if (this.properties != null) {
            z = this.properties.equals(logWithProperties.properties);
        } else if (logWithProperties.properties != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (super.hashCode() * 31) + (this.properties != null ? this.properties.hashCode() : 0);
    }
}
