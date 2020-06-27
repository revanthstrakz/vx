package com.lody.virtual.client.env;

import java.util.ArrayList;

public class VirtualGPSSatalines {
    private static VirtualGPSSatalines INSTANCE = new VirtualGPSSatalines();
    private int mAlmanacMask;
    private float[] mAzimuths;
    private float[] mElevations;
    private int mEphemerisMask;
    private float[] mSnrs;
    private int mUsedInFixMask;
    private int[] pnrs;
    private int[] prnWithFlags;
    private int svCount;

    public int getAlmanacMask() {
        return this.mAlmanacMask;
    }

    public float[] getAzimuths() {
        return this.mAzimuths;
    }

    public float[] getElevations() {
        return this.mElevations;
    }

    public int getEphemerisMask() {
        return this.mEphemerisMask;
    }

    public int[] getPrns() {
        return this.pnrs;
    }

    public float[] getSnrs() {
        return this.mSnrs;
    }

    public int getUsedInFixMask() {
        return this.mUsedInFixMask;
    }

    public static VirtualGPSSatalines get() {
        return INSTANCE;
    }

    private VirtualGPSSatalines() {
        ArrayList arrayList = new ArrayList();
        GPSStateline gPSStateline = new GPSStateline(5, 1.0d, 5.0d, 112.0d, false, true, true);
        arrayList.add(gPSStateline);
        GPSStateline gPSStateline2 = new GPSStateline(13, 13.5d, 23.0d, 53.0d, true, true, true);
        arrayList.add(gPSStateline2);
        GPSStateline gPSStateline3 = new GPSStateline(14, 19.1d, 6.0d, 247.0d, true, true, true);
        arrayList.add(gPSStateline3);
        GPSStateline gPSStateline4 = new GPSStateline(15, 31.0d, 58.0d, 45.0d, true, true, true);
        arrayList.add(gPSStateline4);
        GPSStateline gPSStateline5 = new GPSStateline(18, 0.0d, 52.0d, 309.0d, false, true, true);
        arrayList.add(gPSStateline5);
        GPSStateline gPSStateline6 = new GPSStateline(20, 30.1d, 54.0d, 105.0d, true, true, true);
        arrayList.add(gPSStateline6);
        GPSStateline gPSStateline7 = new GPSStateline(21, 33.2d, 56.0d, 251.0d, true, true, true);
        arrayList.add(gPSStateline7);
        GPSStateline gPSStateline8 = new GPSStateline(22, 0.0d, 14.0d, 299.0d, false, true, true);
        arrayList.add(gPSStateline8);
        GPSStateline gPSStateline9 = new GPSStateline(24, 25.9d, 57.0d, 157.0d, true, true, true);
        arrayList.add(gPSStateline9);
        GPSStateline gPSStateline10 = new GPSStateline(27, 18.0d, 3.0d, 309.0d, true, true, true);
        arrayList.add(gPSStateline10);
        GPSStateline gPSStateline11 = new GPSStateline(28, 18.2d, 3.0d, 42.0d, true, true, true);
        arrayList.add(gPSStateline11);
        GPSStateline gPSStateline12 = new GPSStateline(41, 28.8d, 0.0d, 0.0d, false, false, false);
        arrayList.add(gPSStateline12);
        GPSStateline gPSStateline13 = new GPSStateline(50, 29.2d, 0.0d, 0.0d, false, true, true);
        arrayList.add(gPSStateline13);
        GPSStateline gPSStateline14 = new GPSStateline(67, 14.4d, 2.0d, 92.0d, false, false, false);
        arrayList.add(gPSStateline14);
        GPSStateline gPSStateline15 = new GPSStateline(68, 21.2d, 45.0d, 60.0d, false, false, false);
        arrayList.add(gPSStateline15);
        GPSStateline gPSStateline16 = new GPSStateline(69, 17.5d, 50.0d, 330.0d, false, true, true);
        arrayList.add(gPSStateline16);
        GPSStateline gPSStateline17 = new GPSStateline(70, 22.4d, 7.0d, 291.0d, false, false, false);
        arrayList.add(gPSStateline17);
        GPSStateline gPSStateline18 = new GPSStateline(77, 23.8d, 10.0d, 23.0d, true, true, true);
        arrayList.add(gPSStateline18);
        GPSStateline gPSStateline19 = new GPSStateline(78, 18.0d, 47.0d, 70.0d, true, true, true);
        arrayList.add(gPSStateline19);
        GPSStateline gPSStateline20 = new GPSStateline(79, 22.8d, 41.0d, 142.0d, true, true, true);
        arrayList.add(gPSStateline20);
        GPSStateline gPSStateline21 = new GPSStateline(83, 0.2d, 9.0d, 212.0d, false, false, false);
        arrayList.add(gPSStateline21);
        GPSStateline gPSStateline22 = new GPSStateline(84, 16.7d, 30.0d, 264.0d, true, true, true);
        arrayList.add(gPSStateline22);
        GPSStateline gPSStateline23 = new GPSStateline(85, 12.1d, 20.0d, 317.0d, true, true, true);
        arrayList.add(gPSStateline23);
        this.svCount = arrayList.size();
        this.pnrs = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            this.pnrs[i] = ((GPSStateline) arrayList.get(i)).getPnr();
        }
        this.mSnrs = new float[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            this.mSnrs[i2] = (float) ((GPSStateline) arrayList.get(i2)).getSnr();
        }
        this.mElevations = new float[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            this.mElevations[i3] = (float) ((GPSStateline) arrayList.get(i3)).getElevation();
        }
        this.mAzimuths = new float[arrayList.size()];
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            this.mAzimuths[i4] = (float) ((GPSStateline) arrayList.get(i4)).getAzimuth();
        }
        this.mEphemerisMask = 0;
        for (int i5 = 0; i5 < arrayList.size(); i5++) {
            if (((GPSStateline) arrayList.get(i5)).isHasEphemeris()) {
                this.mEphemerisMask |= 1 << (((GPSStateline) arrayList.get(i5)).getPnr() - 1);
            }
        }
        this.mAlmanacMask = 0;
        for (int i6 = 0; i6 < arrayList.size(); i6++) {
            if (((GPSStateline) arrayList.get(i6)).isHasAlmanac()) {
                this.mAlmanacMask |= 1 << (((GPSStateline) arrayList.get(i6)).getPnr() - 1);
            }
        }
        this.mUsedInFixMask = 0;
        for (int i7 = 0; arrayList.size() > i7; i7++) {
            if (((GPSStateline) arrayList.get(i7)).isUseInFix()) {
                this.mUsedInFixMask |= 1 << (((GPSStateline) arrayList.get(i7)).getPnr() - 1);
            }
        }
        this.prnWithFlags = new int[arrayList.size()];
        for (int i8 = 0; i8 < arrayList.size(); i8++) {
            GPSStateline gPSStateline24 = (GPSStateline) arrayList.get(i8);
            this.prnWithFlags[i8] = (gPSStateline24.getPnr() << 7) | gPSStateline24.isHasEphemeris() | ((gPSStateline24.isHasAlmanac() ? 1 : 0) << true) | ((gPSStateline24.isUseInFix() ? 1 : 0) << true) | true;
        }
    }

    public int getSvCount() {
        return this.svCount;
    }

    public int[] getPrnWithFlags() {
        return this.prnWithFlags;
    }
}
