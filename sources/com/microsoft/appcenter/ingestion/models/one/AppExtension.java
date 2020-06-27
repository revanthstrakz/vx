package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class AppExtension implements Model {

    /* renamed from: ID */
    private static final String f193ID = "id";
    private static final String LOCALE = "locale";
    private static final String NAME = "name";
    private static final String USER_ID = "userId";
    private static final String VER = "ver";

    /* renamed from: id */
    private String f194id;
    private String locale;
    private String name;
    private String userId;
    private String ver;

    public String getId() {
        return this.f194id;
    }

    public void setId(String str) {
        this.f194id = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getVer() {
        return this.ver;
    }

    public void setVer(String str) {
        this.ver = str;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String str) {
        this.locale = str;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String str) {
        this.userId = str;
    }

    public void read(JSONObject jSONObject) {
        setId(jSONObject.optString("id", null));
        setVer(jSONObject.optString(VER, null));
        setName(jSONObject.optString("name", null));
        setLocale(jSONObject.optString(LOCALE, null));
        setUserId(jSONObject.optString("userId", null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, "id", getId());
        JSONUtils.write(jSONStringer, VER, getVer());
        JSONUtils.write(jSONStringer, "name", getName());
        JSONUtils.write(jSONStringer, LOCALE, getLocale());
        JSONUtils.write(jSONStringer, "userId", getUserId());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AppExtension appExtension = (AppExtension) obj;
        if (this.f194id == null ? appExtension.f194id != null : !this.f194id.equals(appExtension.f194id)) {
            return false;
        }
        if (this.ver == null ? appExtension.ver != null : !this.ver.equals(appExtension.ver)) {
            return false;
        }
        if (this.name == null ? appExtension.name != null : !this.name.equals(appExtension.name)) {
            return false;
        }
        if (this.locale == null ? appExtension.locale != null : !this.locale.equals(appExtension.locale)) {
            return false;
        }
        if (this.userId != null) {
            z = this.userId.equals(appExtension.userId);
        } else if (appExtension.userId != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((((((this.f194id != null ? this.f194id.hashCode() : 0) * 31) + (this.ver != null ? this.ver.hashCode() : 0)) * 31) + (this.name != null ? this.name.hashCode() : 0)) * 31) + (this.locale != null ? this.locale.hashCode() : 0)) * 31;
        if (this.userId != null) {
            i = this.userId.hashCode();
        }
        return hashCode + i;
    }
}
