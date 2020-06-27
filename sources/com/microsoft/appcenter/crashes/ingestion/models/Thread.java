package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.crashes.ingestion.models.json.StackFrameFactory;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class Thread implements Model {
    private List<StackFrame> frames;

    /* renamed from: id */
    private long f190id;
    private String name;

    public long getId() {
        return this.f190id;
    }

    public void setId(long j) {
        this.f190id = j;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public List<StackFrame> getFrames() {
        return this.frames;
    }

    public void setFrames(List<StackFrame> list) {
        this.frames = list;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        setId(jSONObject.getLong(CommonProperties.f192ID));
        setName(jSONObject.optString(CommonProperties.NAME, null));
        setFrames(JSONUtils.readArray(jSONObject, CommonProperties.FRAMES, StackFrameFactory.getInstance()));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, CommonProperties.f192ID, Long.valueOf(getId()));
        JSONUtils.write(jSONStringer, CommonProperties.NAME, getName());
        JSONUtils.writeArray(jSONStringer, CommonProperties.FRAMES, getFrames());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Thread thread = (Thread) obj;
        if (this.f190id != thread.f190id) {
            return false;
        }
        if (this.name == null ? thread.name != null : !this.name.equals(thread.name)) {
            return false;
        }
        if (this.frames != null) {
            z = this.frames.equals(thread.frames);
        } else if (thread.frames != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((int) (this.f190id ^ (this.f190id >>> 32))) * 31) + (this.name != null ? this.name.hashCode() : 0)) * 31;
        if (this.frames != null) {
            i = this.frames.hashCode();
        }
        return hashCode + i;
    }
}
