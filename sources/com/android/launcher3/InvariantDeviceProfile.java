package com.android.launcher3;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.android.launcher3.dragndrop.DragView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class InvariantDeviceProfile {
    private static float DEFAULT_ICON_SIZE_DP = 60.0f;
    private static final float ICON_SIZE_DEFINED_IN_APP_DP = 48.0f;
    private static float KNEARESTNEIGHBOR = 3.0f;
    private static float WEIGHT_EFFICIENT = 100000.0f;
    private static float WEIGHT_POWER = 5.0f;
    int defaultLayoutId;
    public Point defaultWallpaperSize;
    int demoModeLayoutId;
    public int fillResIconDpi;
    public int iconBitmapSize;
    public float iconSize;
    public float iconTextSize;
    public float landscapeIconSize;
    public DeviceProfile landscapeProfile;
    @Deprecated
    int minAllAppsPredictionColumns;
    float minHeightDps;
    float minWidthDps;
    String name;
    public int numColumns;
    public int numFolderColumns;
    public int numFolderRows;
    public int numHotseatIcons;
    public int numRows;
    public DeviceProfile portraitProfile;

    private static float wallpaperTravelToScreenWidthRatio(int i, int i2) {
        return ((((float) i) / ((float) i2)) * 0.30769226f) + 1.0076923f;
    }

    public InvariantDeviceProfile() {
    }

    public InvariantDeviceProfile(InvariantDeviceProfile invariantDeviceProfile) {
        InvariantDeviceProfile invariantDeviceProfile2 = invariantDeviceProfile;
        String str = invariantDeviceProfile2.name;
        float f = invariantDeviceProfile2.minWidthDps;
        float f2 = invariantDeviceProfile2.minHeightDps;
        int i = invariantDeviceProfile2.numRows;
        int i2 = invariantDeviceProfile2.numColumns;
        int i3 = invariantDeviceProfile2.numFolderRows;
        int i4 = invariantDeviceProfile2.numFolderColumns;
        int i5 = invariantDeviceProfile2.minAllAppsPredictionColumns;
        float f3 = invariantDeviceProfile2.iconSize;
        float f4 = invariantDeviceProfile2.landscapeIconSize;
        float f5 = invariantDeviceProfile2.iconTextSize;
        int i6 = invariantDeviceProfile2.numHotseatIcons;
        int i7 = invariantDeviceProfile2.defaultLayoutId;
        int i8 = invariantDeviceProfile2.demoModeLayoutId;
        this(str, f, f2, i, i2, i3, i4, i5, f3, f4, f5, i6, i7, i8);
    }

    InvariantDeviceProfile(String str, float f, float f2, int i, int i2, int i3, int i4, int i5, float f3, float f4, float f5, int i6, int i7, int i8) {
        this.name = str;
        this.minWidthDps = f;
        this.minHeightDps = f2;
        this.numRows = i;
        this.numColumns = i2;
        this.numFolderRows = i3;
        this.numFolderColumns = i4;
        this.minAllAppsPredictionColumns = i5;
        this.iconSize = f3;
        this.landscapeIconSize = f4;
        this.iconTextSize = f5;
        this.numHotseatIcons = i6;
        this.defaultLayoutId = i7;
        this.demoModeLayoutId = i8;
    }

    @TargetApi(23)
    InvariantDeviceProfile(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        Point point = new Point();
        Point point2 = new Point();
        defaultDisplay.getCurrentSizeRange(point, point2);
        this.minWidthDps = Utilities.dpiFromPx(Math.min(point.x, point.y), displayMetrics);
        this.minHeightDps = Utilities.dpiFromPx(Math.min(point2.x, point2.y), displayMetrics);
        ArrayList findClosestDeviceProfiles = findClosestDeviceProfiles(this.minWidthDps, this.minHeightDps, getPredefinedDeviceProfiles(context));
        InvariantDeviceProfile invDistWeightedInterpolate = invDistWeightedInterpolate(this.minWidthDps, this.minHeightDps, findClosestDeviceProfiles);
        InvariantDeviceProfile invariantDeviceProfile = (InvariantDeviceProfile) findClosestDeviceProfiles.get(0);
        this.numRows = invariantDeviceProfile.numRows;
        this.numColumns = invariantDeviceProfile.numColumns;
        this.numHotseatIcons = invariantDeviceProfile.numHotseatIcons;
        this.defaultLayoutId = invariantDeviceProfile.defaultLayoutId;
        this.demoModeLayoutId = invariantDeviceProfile.demoModeLayoutId;
        this.numFolderRows = invariantDeviceProfile.numFolderRows;
        this.numFolderColumns = invariantDeviceProfile.numFolderColumns;
        this.minAllAppsPredictionColumns = invariantDeviceProfile.minAllAppsPredictionColumns;
        this.iconSize = invDistWeightedInterpolate.iconSize;
        this.landscapeIconSize = invDistWeightedInterpolate.landscapeIconSize;
        this.iconBitmapSize = Utilities.pxFromDp(this.iconSize, displayMetrics);
        this.iconTextSize = invDistWeightedInterpolate.iconTextSize;
        this.fillResIconDpi = getLauncherIconDensity(this.iconBitmapSize);
        applyPartnerDeviceProfileOverrides(context, displayMetrics);
        Point point3 = new Point();
        defaultDisplay.getRealSize(point3);
        int min = Math.min(point3.x, point3.y);
        int max = Math.max(point3.x, point3.y);
        Context context2 = context;
        Point point4 = point;
        Point point5 = point2;
        DeviceProfile deviceProfile = new DeviceProfile(context2, this, point4, point5, max, min, true);
        this.landscapeProfile = deviceProfile;
        DeviceProfile deviceProfile2 = new DeviceProfile(context2, this, point4, point5, min, max, false);
        this.portraitProfile = deviceProfile2;
        if (context.getResources().getConfiguration().smallestScreenWidthDp >= 720) {
            this.defaultWallpaperSize = new Point((int) (((float) max) * wallpaperTravelToScreenWidthRatio(max, min)), max);
        } else {
            this.defaultWallpaperSize = new Point(Math.max(min * 2, max), max);
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00c3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        r3.addSuppressed(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00cd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00d3, code lost:
        throw new java.lang.RuntimeException(r0);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00cd A[ExcHandler: IOException | XmlPullParserException (r0v1 'e' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:1:0x0005] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<com.android.launcher3.InvariantDeviceProfile> getPredefinedDeviceProfiles(android.content.Context r24) {
        /*
            r23 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.content.res.Resources r1 = r24.getResources()     // Catch:{ IOException | XmlPullParserException -> 0x00cd }
            int r2 = com.android.launcher3.C0622R.xml.device_profiles     // Catch:{ IOException | XmlPullParserException -> 0x00cd }
            android.content.res.XmlResourceParser r1 = r1.getXml(r2)     // Catch:{ IOException | XmlPullParserException -> 0x00cd }
            int r3 = r1.getDepth()     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
        L_0x0013:
            int r4 = r1.next()     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            r5 = 3
            if (r4 != r5) goto L_0x0020
            int r5 = r1.getDepth()     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            if (r5 <= r3) goto L_0x00ab
        L_0x0020:
            r5 = 1
            if (r4 == r5) goto L_0x00ab
            r5 = 2
            if (r4 != r5) goto L_0x00a7
            java.lang.String r4 = "profile"
            java.lang.String r5 = r1.getName()     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            boolean r4 = r4.equals(r5)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            if (r4 == 0) goto L_0x00a7
            android.util.AttributeSet r4 = android.util.Xml.asAttributeSet(r1)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int[] r5 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            r6 = r24
            android.content.res.TypedArray r4 = r6.obtainStyledAttributes(r4, r5)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r5 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_numRows     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            r7 = 0
            int r12 = r4.getInt(r5, r7)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r5 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_numColumns     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r13 = r4.getInt(r5, r7)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r5 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_iconSize     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            r8 = 0
            float r5 = r4.getFloat(r5, r8)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            com.android.launcher3.InvariantDeviceProfile r15 = new com.android.launcher3.InvariantDeviceProfile     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r9 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_name     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            java.lang.String r9 = r4.getString(r9)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r10 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_minWidthDps     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            float r10 = r4.getFloat(r10, r8)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r11 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_minHeightDps     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            float r11 = r4.getFloat(r11, r8)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r14 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_numFolderRows     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r14 = r4.getInt(r14, r12)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r2 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_numFolderColumns     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r2 = r4.getInt(r2, r13)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r7 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_minAllAppsPredictionColumns     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r16 = r4.getInt(r7, r13)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r7 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_landscapeIconSize     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            float r18 = r4.getFloat(r7, r5)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r7 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_iconTextSize     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            float r19 = r4.getFloat(r7, r8)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r7 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_numHotseatIcons     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r20 = r4.getInt(r7, r13)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r7 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_defaultLayoutId     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            r8 = 0
            int r21 = r4.getResourceId(r7, r8)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r7 = com.android.launcher3.C0622R.styleable.InvariantDeviceProfile_demoModeLayoutId     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            int r22 = r4.getResourceId(r7, r8)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            r8 = r15
            r7 = r15
            r15 = r2
            r17 = r5
            r8.<init>(r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            r0.add(r7)     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            r4.recycle()     // Catch:{ Throwable -> 0x00b5, all -> 0x00b1 }
            goto L_0x0013
        L_0x00a7:
            r6 = r24
            goto L_0x0013
        L_0x00ab:
            if (r1 == 0) goto L_0x00b0
            r1.close()     // Catch:{ IOException | XmlPullParserException -> 0x00cd }
        L_0x00b0:
            return r0
        L_0x00b1:
            r0 = move-exception
            r2 = r0
            r3 = 0
            goto L_0x00bb
        L_0x00b5:
            r0 = move-exception
            r2 = r0
            throw r2     // Catch:{ all -> 0x00b8 }
        L_0x00b8:
            r0 = move-exception
            r3 = r2
            r2 = r0
        L_0x00bb:
            if (r1 == 0) goto L_0x00cc
            if (r3 == 0) goto L_0x00c9
            r1.close()     // Catch:{ Throwable -> 0x00c3, IOException | XmlPullParserException -> 0x00cd }
            goto L_0x00cc
        L_0x00c3:
            r0 = move-exception
            r1 = r0
            r3.addSuppressed(r1)     // Catch:{ IOException | XmlPullParserException -> 0x00cd }
            goto L_0x00cc
        L_0x00c9:
            r1.close()     // Catch:{ IOException | XmlPullParserException -> 0x00cd }
        L_0x00cc:
            throw r2     // Catch:{ IOException | XmlPullParserException -> 0x00cd }
        L_0x00cd:
            r0 = move-exception
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            r1.<init>(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.InvariantDeviceProfile.getPredefinedDeviceProfiles(android.content.Context):java.util.ArrayList");
    }

    private int getLauncherIconDensity(int i) {
        int[] iArr = {DragView.COLOR_CHANGE_DURATION, 160, 213, ShortcutInfo.FLAG_RESTORED_APP_TYPE, 320, 480, 640};
        int i2 = 640;
        for (int length = iArr.length - 1; length >= 0; length--) {
            if ((((float) iArr[length]) * ICON_SIZE_DEFINED_IN_APP_DP) / 160.0f >= ((float) i)) {
                i2 = iArr[length];
            }
        }
        return i2;
    }

    private void applyPartnerDeviceProfileOverrides(Context context, DisplayMetrics displayMetrics) {
        Partner partner = Partner.get(context.getPackageManager());
        if (partner != null) {
            partner.applyInvariantDeviceProfileOverrides(this, displayMetrics);
        }
    }

    /* access modifiers changed from: 0000 */
    public float dist(float f, float f2, float f3, float f4) {
        return (float) Math.hypot((double) (f3 - f), (double) (f4 - f2));
    }

    /* access modifiers changed from: 0000 */
    public ArrayList<InvariantDeviceProfile> findClosestDeviceProfiles(final float f, final float f2, ArrayList<InvariantDeviceProfile> arrayList) {
        Collections.sort(arrayList, new Comparator<InvariantDeviceProfile>() {
            public int compare(InvariantDeviceProfile invariantDeviceProfile, InvariantDeviceProfile invariantDeviceProfile2) {
                return Float.compare(InvariantDeviceProfile.this.dist(f, f2, invariantDeviceProfile.minWidthDps, invariantDeviceProfile.minHeightDps), InvariantDeviceProfile.this.dist(f, f2, invariantDeviceProfile2.minWidthDps, invariantDeviceProfile2.minHeightDps));
            }
        });
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public InvariantDeviceProfile invDistWeightedInterpolate(float f, float f2, ArrayList<InvariantDeviceProfile> arrayList) {
        int i = 0;
        InvariantDeviceProfile invariantDeviceProfile = (InvariantDeviceProfile) arrayList.get(0);
        float f3 = 0.0f;
        if (dist(f, f2, invariantDeviceProfile.minWidthDps, invariantDeviceProfile.minHeightDps) == 0.0f) {
            return invariantDeviceProfile;
        }
        InvariantDeviceProfile invariantDeviceProfile2 = new InvariantDeviceProfile();
        while (i < arrayList.size() && ((float) i) < KNEARESTNEIGHBOR) {
            InvariantDeviceProfile invariantDeviceProfile3 = new InvariantDeviceProfile((InvariantDeviceProfile) arrayList.get(i));
            float weight = weight(f, f2, invariantDeviceProfile3.minWidthDps, invariantDeviceProfile3.minHeightDps, WEIGHT_POWER);
            f3 += weight;
            invariantDeviceProfile2.add(invariantDeviceProfile3.multiply(weight));
            i++;
        }
        return invariantDeviceProfile2.multiply(1.0f / f3);
    }

    private void add(InvariantDeviceProfile invariantDeviceProfile) {
        this.iconSize += invariantDeviceProfile.iconSize;
        this.landscapeIconSize += invariantDeviceProfile.landscapeIconSize;
        this.iconTextSize += invariantDeviceProfile.iconTextSize;
    }

    private InvariantDeviceProfile multiply(float f) {
        this.iconSize *= f;
        this.landscapeIconSize *= f;
        this.iconTextSize *= f;
        return this;
    }

    public int getAllAppsButtonRank() {
        return this.numHotseatIcons / 2;
    }

    public boolean isAllAppsButtonRank(int i) {
        return i == getAllAppsButtonRank();
    }

    public DeviceProfile getDeviceProfile(Context context) {
        return context.getResources().getConfiguration().orientation == 2 ? this.landscapeProfile : this.portraitProfile;
    }

    private float weight(float f, float f2, float f3, float f4, float f5) {
        float dist = dist(f, f2, f3, f4);
        if (Float.compare(dist, 0.0f) == 0) {
            return Float.POSITIVE_INFINITY;
        }
        return (float) (((double) WEIGHT_EFFICIENT) / Math.pow((double) dist, (double) f5));
    }
}
