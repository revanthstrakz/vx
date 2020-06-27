package com.google.android.apps.nexuslauncher.smartspace;

public class SmartspaceDataContainer {

    /* renamed from: dO */
    SmartspaceCard f130dO = null;

    /* renamed from: dP */
    SmartspaceCard f131dP = null;

    public boolean isWeatherAvailable() {
        return this.f130dO != null;
    }

    /* renamed from: cS */
    public boolean mo12989cS() {
        return this.f131dP != null;
    }

    /* renamed from: cT */
    public long mo12990cT() {
        long currentTimeMillis = System.currentTimeMillis();
        if (mo12989cS() && isWeatherAvailable()) {
            return Math.min(this.f131dP.mo12968cF(), this.f130dO.mo12968cF()) - currentTimeMillis;
        }
        if (mo12989cS()) {
            return this.f131dP.mo12968cF() - currentTimeMillis;
        }
        if (isWeatherAvailable()) {
            return this.f130dO.mo12968cF() - currentTimeMillis;
        }
        return 0;
    }

    /* renamed from: cU */
    public boolean mo12991cU() {
        boolean z;
        if (!isWeatherAvailable() || !this.f130dO.mo12970cM()) {
            z = false;
        } else {
            this.f130dO = null;
            z = true;
        }
        if (!mo12989cS() || !this.f131dP.mo12970cM()) {
            return z;
        }
        this.f131dP = null;
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(this.f131dP);
        sb.append(",");
        sb.append(this.f130dO);
        sb.append("}");
        return sb.toString();
    }
}
