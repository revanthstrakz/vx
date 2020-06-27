package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.LogWithProperties;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class HandledErrorLog extends LogWithProperties {
    private static final String EXCEPTION = "exception";
    public static final String TYPE = "handledError";
    private Exception exception;

    /* renamed from: id */
    private UUID f189id;

    public String getType() {
        return TYPE;
    }

    public UUID getId() {
        return this.f189id;
    }

    public void setId(UUID uuid) {
        this.f189id = uuid;
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception2) {
        this.exception = exception2;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setId(UUID.fromString(jSONObject.getString(CommonProperties.f192ID)));
        if (jSONObject.has(EXCEPTION)) {
            JSONObject jSONObject2 = jSONObject.getJSONObject(EXCEPTION);
            Exception exception2 = new Exception();
            exception2.read(jSONObject2);
            setException(exception2);
        }
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        jSONStringer.key(CommonProperties.f192ID).value(getId());
        if (getException() != null) {
            jSONStringer.key(EXCEPTION).object();
            this.exception.write(jSONStringer);
            jSONStringer.endObject();
        }
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        HandledErrorLog handledErrorLog = (HandledErrorLog) obj;
        if (this.f189id == null ? handledErrorLog.f189id != null : !this.f189id.equals(handledErrorLog.f189id)) {
            return false;
        }
        if (this.exception != null) {
            z = this.exception.equals(handledErrorLog.exception);
        } else if (handledErrorLog.exception != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((super.hashCode() * 31) + (this.f189id != null ? this.f189id.hashCode() : 0)) * 31;
        if (this.exception != null) {
            i = this.exception.hashCode();
        }
        return hashCode + i;
    }
}
