package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.AbstractLog;
import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public abstract class CommonSchemaLog extends AbstractLog {

    /* renamed from: CV */
    private static final String f195CV = "cV";
    private static final String DATA = "data";
    private static final String EXT = "ext";
    private static final String FLAGS = "flags";
    private static final String IKEY = "iKey";
    private static final String NAME = "name";
    private static final String POP_SAMPLE = "popSample";
    private static final String TIME = "time";
    private static final String VER = "ver";

    /* renamed from: cV */
    private String f196cV;
    private Data data;
    private Extensions ext;
    private Long flags;
    private String iKey;
    private String name;
    private Double popSample;
    private String ver;

    public String getVer() {
        return this.ver;
    }

    public void setVer(String str) {
        this.ver = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public Double getPopSample() {
        return this.popSample;
    }

    public void setPopSample(Double d) {
        this.popSample = d;
    }

    public String getIKey() {
        return this.iKey;
    }

    public void setIKey(String str) {
        this.iKey = str;
    }

    public Long getFlags() {
        return this.flags;
    }

    public void setFlags(Long l) {
        this.flags = l;
    }

    public String getCV() {
        return this.f196cV;
    }

    public void setCV(String str) {
        this.f196cV = str;
    }

    public Extensions getExt() {
        return this.ext;
    }

    public void setExt(Extensions extensions) {
        this.ext = extensions;
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data2) {
        this.data = data2;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        setVer(jSONObject.getString(VER));
        setName(jSONObject.getString("name"));
        setTimestamp(JSONDateUtils.toDate(jSONObject.getString(TIME)));
        if (jSONObject.has(POP_SAMPLE)) {
            setPopSample(Double.valueOf(jSONObject.getDouble(POP_SAMPLE)));
        }
        setIKey(jSONObject.optString(IKEY, null));
        setFlags(JSONUtils.readLong(jSONObject, FLAGS));
        setCV(jSONObject.optString(f195CV, null));
        if (jSONObject.has(EXT)) {
            Extensions extensions = new Extensions();
            extensions.read(jSONObject.getJSONObject(EXT));
            setExt(extensions);
        }
        if (jSONObject.has(DATA)) {
            Data data2 = new Data();
            data2.read(jSONObject.getJSONObject(DATA));
            setData(data2);
        }
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        jSONStringer.key(VER).value(getVer());
        jSONStringer.key("name").value(getName());
        jSONStringer.key(TIME).value(JSONDateUtils.toString(getTimestamp()));
        JSONUtils.write(jSONStringer, POP_SAMPLE, getPopSample());
        JSONUtils.write(jSONStringer, IKEY, getIKey());
        JSONUtils.write(jSONStringer, FLAGS, getFlags());
        JSONUtils.write(jSONStringer, f195CV, getCV());
        if (getExt() != null) {
            jSONStringer.key(EXT).object();
            getExt().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getData() != null) {
            jSONStringer.key(DATA).object();
            getData().write(jSONStringer);
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
        CommonSchemaLog commonSchemaLog = (CommonSchemaLog) obj;
        if (this.ver == null ? commonSchemaLog.ver != null : !this.ver.equals(commonSchemaLog.ver)) {
            return false;
        }
        if (this.name == null ? commonSchemaLog.name != null : !this.name.equals(commonSchemaLog.name)) {
            return false;
        }
        if (this.popSample == null ? commonSchemaLog.popSample != null : !this.popSample.equals(commonSchemaLog.popSample)) {
            return false;
        }
        if (this.iKey == null ? commonSchemaLog.iKey != null : !this.iKey.equals(commonSchemaLog.iKey)) {
            return false;
        }
        if (this.flags == null ? commonSchemaLog.flags != null : !this.flags.equals(commonSchemaLog.flags)) {
            return false;
        }
        if (this.f196cV == null ? commonSchemaLog.f196cV != null : !this.f196cV.equals(commonSchemaLog.f196cV)) {
            return false;
        }
        if (this.ext == null ? commonSchemaLog.ext != null : !this.ext.equals(commonSchemaLog.ext)) {
            return false;
        }
        if (this.data != null) {
            z = this.data.equals(commonSchemaLog.data);
        } else if (commonSchemaLog.data != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((((((((((((super.hashCode() * 31) + (this.ver != null ? this.ver.hashCode() : 0)) * 31) + (this.name != null ? this.name.hashCode() : 0)) * 31) + (this.popSample != null ? this.popSample.hashCode() : 0)) * 31) + (this.iKey != null ? this.iKey.hashCode() : 0)) * 31) + (this.flags != null ? this.flags.hashCode() : 0)) * 31) + (this.f196cV != null ? this.f196cV.hashCode() : 0)) * 31) + (this.ext != null ? this.ext.hashCode() : 0)) * 31;
        if (this.data != null) {
            i = this.data.hashCode();
        }
        return hashCode + i;
    }
}
