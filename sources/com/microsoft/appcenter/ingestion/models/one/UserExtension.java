package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class UserExtension implements Model {
    private static final String LOCALE = "locale";
    private static final String LOCAL_ID = "localId";
    private String localId;
    private String locale;

    public String getLocalId() {
        return this.localId;
    }

    public void setLocalId(String str) {
        this.localId = str;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String str) {
        this.locale = str;
    }

    public void read(JSONObject jSONObject) {
        setLocalId(jSONObject.optString(LOCAL_ID, null));
        setLocale(jSONObject.optString(LOCALE, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, LOCAL_ID, getLocalId());
        JSONUtils.write(jSONStringer, LOCALE, getLocale());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserExtension userExtension = (UserExtension) obj;
        if (this.localId == null ? userExtension.localId != null : !this.localId.equals(userExtension.localId)) {
            return false;
        }
        if (this.locale != null) {
            z = this.locale.equals(userExtension.locale);
        } else if (userExtension.locale != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (this.localId != null ? this.localId.hashCode() : 0) * 31;
        if (this.locale != null) {
            i = this.locale.hashCode();
        }
        return hashCode + i;
    }
}
