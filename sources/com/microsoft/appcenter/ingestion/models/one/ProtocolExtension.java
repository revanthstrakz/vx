package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class ProtocolExtension implements Model {
    private static final String DEV_MAKE = "devMake";
    private static final String DEV_MODEL = "devModel";
    private static final String TICKET_KEYS = "ticketKeys";
    private String devMake;
    private String devModel;
    private List<String> ticketKeys;

    public List<String> getTicketKeys() {
        return this.ticketKeys;
    }

    public void setTicketKeys(List<String> list) {
        this.ticketKeys = list;
    }

    public String getDevMake() {
        return this.devMake;
    }

    public void setDevMake(String str) {
        this.devMake = str;
    }

    public String getDevModel() {
        return this.devModel;
    }

    public void setDevModel(String str) {
        this.devModel = str;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        setTicketKeys(JSONUtils.readStringArray(jSONObject, TICKET_KEYS));
        setDevMake(jSONObject.optString(DEV_MAKE, null));
        setDevModel(jSONObject.optString(DEV_MODEL, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.writeStringArray(jSONStringer, TICKET_KEYS, getTicketKeys());
        JSONUtils.write(jSONStringer, DEV_MAKE, getDevMake());
        JSONUtils.write(jSONStringer, DEV_MODEL, getDevModel());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProtocolExtension protocolExtension = (ProtocolExtension) obj;
        if (this.ticketKeys == null ? protocolExtension.ticketKeys != null : !this.ticketKeys.equals(protocolExtension.ticketKeys)) {
            return false;
        }
        if (this.devMake == null ? protocolExtension.devMake != null : !this.devMake.equals(protocolExtension.devMake)) {
            return false;
        }
        if (this.devModel != null) {
            z = this.devModel.equals(protocolExtension.devModel);
        } else if (protocolExtension.devModel != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((this.ticketKeys != null ? this.ticketKeys.hashCode() : 0) * 31) + (this.devMake != null ? this.devMake.hashCode() : 0)) * 31;
        if (this.devModel != null) {
            i = this.devModel.hashCode();
        }
        return hashCode + i;
    }
}
