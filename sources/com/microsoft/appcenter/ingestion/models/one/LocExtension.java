package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class LocExtension implements Model {

    /* renamed from: TZ */
    private static final String f199TZ = "tz";

    /* renamed from: tz */
    private String f200tz;

    public String getTz() {
        return this.f200tz;
    }

    public void setTz(String str) {
        this.f200tz = str;
    }

    public void read(JSONObject jSONObject) {
        setTz(jSONObject.optString(f199TZ, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, f199TZ, getTz());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LocExtension locExtension = (LocExtension) obj;
        if (this.f200tz != null) {
            z = this.f200tz.equals(locExtension.f200tz);
        } else if (locExtension.f200tz != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        if (this.f200tz != null) {
            return this.f200tz.hashCode();
        }
        return 0;
    }
}
