package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class NetExtension implements Model {
    private static final String PROVIDER = "provider";
    private String provider;

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String str) {
        this.provider = str;
    }

    public void read(JSONObject jSONObject) {
        setProvider(jSONObject.optString(PROVIDER, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, PROVIDER, getProvider());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NetExtension netExtension = (NetExtension) obj;
        if (this.provider != null) {
            z = this.provider.equals(netExtension.provider);
        } else if (netExtension.provider != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        if (this.provider != null) {
            return this.provider.hashCode();
        }
        return 0;
    }
}
