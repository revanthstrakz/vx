package com.microsoft.appcenter.ingestion.models.one;

import com.microsoft.appcenter.ingestion.models.Model;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class Extensions implements Model {
    private static final String APP = "app";
    private static final String DEVICE = "device";
    private static final String LOC = "loc";
    private static final String METADATA = "metadata";
    private static final String NET = "net";

    /* renamed from: OS */
    private static final String f197OS = "os";
    private static final String PROTOCOL = "protocol";
    private static final String SDK = "sdk";
    private static final String USER = "user";
    private AppExtension app;
    private DeviceExtension device;
    private LocExtension loc;
    private MetadataExtension metadata;
    private NetExtension net;

    /* renamed from: os */
    private OsExtension f198os;
    private ProtocolExtension protocol;
    private SdkExtension sdk;
    private UserExtension user;

    public MetadataExtension getMetadata() {
        return this.metadata;
    }

    public void setMetadata(MetadataExtension metadataExtension) {
        this.metadata = metadataExtension;
    }

    public ProtocolExtension getProtocol() {
        return this.protocol;
    }

    public void setProtocol(ProtocolExtension protocolExtension) {
        this.protocol = protocolExtension;
    }

    public UserExtension getUser() {
        return this.user;
    }

    public void setUser(UserExtension userExtension) {
        this.user = userExtension;
    }

    public DeviceExtension getDevice() {
        return this.device;
    }

    public void setDevice(DeviceExtension deviceExtension) {
        this.device = deviceExtension;
    }

    public OsExtension getOs() {
        return this.f198os;
    }

    public void setOs(OsExtension osExtension) {
        this.f198os = osExtension;
    }

    public AppExtension getApp() {
        return this.app;
    }

    public void setApp(AppExtension appExtension) {
        this.app = appExtension;
    }

    public NetExtension getNet() {
        return this.net;
    }

    public void setNet(NetExtension netExtension) {
        this.net = netExtension;
    }

    public SdkExtension getSdk() {
        return this.sdk;
    }

    public void setSdk(SdkExtension sdkExtension) {
        this.sdk = sdkExtension;
    }

    public LocExtension getLoc() {
        return this.loc;
    }

    public void setLoc(LocExtension locExtension) {
        this.loc = locExtension;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        if (jSONObject.has(METADATA)) {
            MetadataExtension metadataExtension = new MetadataExtension();
            metadataExtension.read(jSONObject.getJSONObject(METADATA));
            setMetadata(metadataExtension);
        }
        if (jSONObject.has(PROTOCOL)) {
            ProtocolExtension protocolExtension = new ProtocolExtension();
            protocolExtension.read(jSONObject.getJSONObject(PROTOCOL));
            setProtocol(protocolExtension);
        }
        if (jSONObject.has("user")) {
            UserExtension userExtension = new UserExtension();
            userExtension.read(jSONObject.getJSONObject("user"));
            setUser(userExtension);
        }
        if (jSONObject.has("device")) {
            DeviceExtension deviceExtension = new DeviceExtension();
            deviceExtension.read(jSONObject.getJSONObject("device"));
            setDevice(deviceExtension);
        }
        if (jSONObject.has(f197OS)) {
            OsExtension osExtension = new OsExtension();
            osExtension.read(jSONObject.getJSONObject(f197OS));
            setOs(osExtension);
        }
        if (jSONObject.has("app")) {
            AppExtension appExtension = new AppExtension();
            appExtension.read(jSONObject.getJSONObject("app"));
            setApp(appExtension);
        }
        if (jSONObject.has(NET)) {
            NetExtension netExtension = new NetExtension();
            netExtension.read(jSONObject.getJSONObject(NET));
            setNet(netExtension);
        }
        if (jSONObject.has(SDK)) {
            SdkExtension sdkExtension = new SdkExtension();
            sdkExtension.read(jSONObject.getJSONObject(SDK));
            setSdk(sdkExtension);
        }
        if (jSONObject.has(LOC)) {
            LocExtension locExtension = new LocExtension();
            locExtension.read(jSONObject.getJSONObject(LOC));
            setLoc(locExtension);
        }
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        if (getMetadata() != null) {
            jSONStringer.key(METADATA).object();
            getMetadata().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getProtocol() != null) {
            jSONStringer.key(PROTOCOL).object();
            getProtocol().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getUser() != null) {
            jSONStringer.key("user").object();
            getUser().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getDevice() != null) {
            jSONStringer.key("device").object();
            getDevice().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getOs() != null) {
            jSONStringer.key(f197OS).object();
            getOs().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getApp() != null) {
            jSONStringer.key("app").object();
            getApp().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getNet() != null) {
            jSONStringer.key(NET).object();
            getNet().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getSdk() != null) {
            jSONStringer.key(SDK).object();
            getSdk().write(jSONStringer);
            jSONStringer.endObject();
        }
        if (getLoc() != null) {
            jSONStringer.key(LOC).object();
            getLoc().write(jSONStringer);
            jSONStringer.endObject();
        }
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Extensions extensions = (Extensions) obj;
        if (this.metadata == null ? extensions.metadata != null : !this.metadata.equals(extensions.metadata)) {
            return false;
        }
        if (this.protocol == null ? extensions.protocol != null : !this.protocol.equals(extensions.protocol)) {
            return false;
        }
        if (this.user == null ? extensions.user != null : !this.user.equals(extensions.user)) {
            return false;
        }
        if (this.device == null ? extensions.device != null : !this.device.equals(extensions.device)) {
            return false;
        }
        if (this.f198os == null ? extensions.f198os != null : !this.f198os.equals(extensions.f198os)) {
            return false;
        }
        if (this.app == null ? extensions.app != null : !this.app.equals(extensions.app)) {
            return false;
        }
        if (this.net == null ? extensions.net != null : !this.net.equals(extensions.net)) {
            return false;
        }
        if (this.sdk == null ? extensions.sdk != null : !this.sdk.equals(extensions.sdk)) {
            return false;
        }
        if (this.loc != null) {
            z = this.loc.equals(extensions.loc);
        } else if (extensions.loc != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((((((((((((((this.metadata != null ? this.metadata.hashCode() : 0) * 31) + (this.protocol != null ? this.protocol.hashCode() : 0)) * 31) + (this.user != null ? this.user.hashCode() : 0)) * 31) + (this.device != null ? this.device.hashCode() : 0)) * 31) + (this.f198os != null ? this.f198os.hashCode() : 0)) * 31) + (this.app != null ? this.app.hashCode() : 0)) * 31) + (this.net != null ? this.net.hashCode() : 0)) * 31) + (this.sdk != null ? this.sdk.hashCode() : 0)) * 31;
        if (this.loc != null) {
            i = this.loc.hashCode();
        }
        return hashCode + i;
    }
}
