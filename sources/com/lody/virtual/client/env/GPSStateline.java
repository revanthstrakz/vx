package com.lody.virtual.client.env;

class GPSStateline {
    private double mAzimuth;
    private double mElevation;
    private boolean mHasAlmanac;
    private boolean mHasEphemeris;
    private int mPnr;
    private double mSnr;
    private boolean mUseInFix;

    public double getAzimuth() {
        return this.mAzimuth;
    }

    public double getElevation() {
        return this.mElevation;
    }

    public int getPnr() {
        return this.mPnr;
    }

    public double getSnr() {
        return this.mSnr;
    }

    public boolean isHasAlmanac() {
        return this.mHasAlmanac;
    }

    public boolean isHasEphemeris() {
        return this.mHasEphemeris;
    }

    public boolean isUseInFix() {
        return this.mUseInFix;
    }

    public void setAzimuth(double d) {
        this.mAzimuth = d;
    }

    public void setElevation(double d) {
        this.mElevation = d;
    }

    public void setHasAlmanac(boolean z) {
        this.mHasAlmanac = z;
    }

    public void setHasEphemeris(boolean z) {
        this.mHasEphemeris = z;
    }

    public void setPnr(int i) {
        this.mPnr = i;
    }

    public void setSnr(double d) {
        this.mSnr = d;
    }

    public void setUseInFix(boolean z) {
        this.mUseInFix = z;
    }

    public GPSStateline(int i, double d, double d2, double d3, boolean z, boolean z2, boolean z3) {
        this.mPnr = i;
        this.mSnr = d;
        this.mElevation = d2;
        this.mAzimuth = d3;
        this.mUseInFix = z;
        this.mHasAlmanac = z2;
        this.mHasEphemeris = z3;
    }
}
