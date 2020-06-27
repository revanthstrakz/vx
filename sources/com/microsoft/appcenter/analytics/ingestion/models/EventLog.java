package com.microsoft.appcenter.analytics.ingestion.models;

import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import com.microsoft.appcenter.ingestion.models.properties.TypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.TypedPropertyUtils;
import java.util.List;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class EventLog extends LogWithNameAndProperties {
    public static final String TYPE = "event";

    /* renamed from: id */
    private UUID f186id;
    private List<TypedProperty> typedProperties;

    public String getType() {
        return "event";
    }

    public UUID getId() {
        return this.f186id;
    }

    public void setId(UUID uuid) {
        this.f186id = uuid;
    }

    public List<TypedProperty> getTypedProperties() {
        return this.typedProperties;
    }

    public void setTypedProperties(List<TypedProperty> list) {
        this.typedProperties = list;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setId(UUID.fromString(jSONObject.getString(CommonProperties.f192ID)));
        setTypedProperties(TypedPropertyUtils.read(jSONObject));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        jSONStringer.key(CommonProperties.f192ID).value(getId());
        JSONUtils.writeArray(jSONStringer, CommonProperties.TYPED_PROPERTIES, getTypedProperties());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        EventLog eventLog = (EventLog) obj;
        if (this.f186id == null ? eventLog.f186id != null : !this.f186id.equals(eventLog.f186id)) {
            return false;
        }
        if (this.typedProperties != null) {
            z = this.typedProperties.equals(eventLog.typedProperties);
        } else if (eventLog.typedProperties != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((super.hashCode() * 31) + (this.f186id != null ? this.f186id.hashCode() : 0)) * 31;
        if (this.typedProperties != null) {
            i = this.typedProperties.hashCode();
        }
        return hashCode + i;
    }
}
