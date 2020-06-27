package com.microsoft.appcenter.ingestion.models;

import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class CustomPropertiesLog extends AbstractLog {
    private static final String PROPERTIES = "properties";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_TYPE = "type";
    private static final String PROPERTY_TYPE_BOOLEAN = "boolean";
    private static final String PROPERTY_TYPE_CLEAR = "clear";
    private static final String PROPERTY_TYPE_DATETIME = "dateTime";
    private static final String PROPERTY_TYPE_NUMBER = "number";
    private static final String PROPERTY_TYPE_STRING = "string";
    private static final String PROPERTY_VALUE = "value";
    public static final String TYPE = "customProperties";
    private Map<String, Object> properties;

    public String getType() {
        return TYPE;
    }

    private static Map<String, Object> readProperties(JSONObject jSONObject) throws JSONException {
        JSONArray jSONArray = jSONObject.getJSONArray(PROPERTIES);
        HashMap hashMap = new HashMap();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
            hashMap.put(jSONObject2.getString("name"), readPropertyValue(jSONObject2));
        }
        return hashMap;
    }

    private static Object readPropertyValue(JSONObject jSONObject) throws JSONException {
        String string = jSONObject.getString("type");
        if (string.equals(PROPERTY_TYPE_CLEAR)) {
            return null;
        }
        if (string.equals("boolean")) {
            return Boolean.valueOf(jSONObject.getBoolean("value"));
        }
        if (string.equals(PROPERTY_TYPE_NUMBER)) {
            Object obj = jSONObject.get("value");
            if (obj instanceof Number) {
                return obj;
            }
            throw new JSONException("Invalid value type");
        } else if (string.equals("dateTime")) {
            return JSONDateUtils.toDate(jSONObject.getString("value"));
        } else {
            if (string.equals("string")) {
                return jSONObject.getString("value");
            }
            throw new JSONException("Invalid value type");
        }
    }

    private static void writeProperties(JSONStringer jSONStringer, Map<String, Object> map) throws JSONException {
        if (map != null) {
            jSONStringer.key(PROPERTIES).array();
            for (Entry entry : map.entrySet()) {
                jSONStringer.object();
                JSONUtils.write(jSONStringer, "name", entry.getKey());
                writePropertyValue(jSONStringer, entry.getValue());
                jSONStringer.endObject();
            }
            jSONStringer.endArray();
            return;
        }
        throw new JSONException("Properties cannot be null");
    }

    private static void writePropertyValue(JSONStringer jSONStringer, Object obj) throws JSONException {
        if (obj == null) {
            JSONUtils.write(jSONStringer, "type", PROPERTY_TYPE_CLEAR);
        } else if (obj instanceof Boolean) {
            JSONUtils.write(jSONStringer, "type", "boolean");
            JSONUtils.write(jSONStringer, "value", obj);
        } else if (obj instanceof Number) {
            JSONUtils.write(jSONStringer, "type", PROPERTY_TYPE_NUMBER);
            JSONUtils.write(jSONStringer, "value", obj);
        } else if (obj instanceof Date) {
            JSONUtils.write(jSONStringer, "type", "dateTime");
            JSONUtils.write(jSONStringer, "value", JSONDateUtils.toString((Date) obj));
        } else if (obj instanceof String) {
            JSONUtils.write(jSONStringer, "type", "string");
            JSONUtils.write(jSONStringer, "value", obj);
        } else {
            throw new JSONException("Invalid value type");
        }
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, Object> map) {
        this.properties = map;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setProperties(readProperties(jSONObject));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        writeProperties(jSONStringer, getProperties());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        CustomPropertiesLog customPropertiesLog = (CustomPropertiesLog) obj;
        if (this.properties != null) {
            z = this.properties.equals(customPropertiesLog.properties);
        } else if (customPropertiesLog.properties != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (super.hashCode() * 31) + (this.properties != null ? this.properties.hashCode() : 0);
    }
}
