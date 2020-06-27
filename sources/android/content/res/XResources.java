package android.content.res;

import android.content.Context;
import android.content.p000pm.PackageParser;
import android.content.p000pm.PackageParser.PackageLite;
import android.content.p000pm.PackageParser.PackageParserException;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.p001v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.dragndrop.DragView;
import com.microsoft.appcenter.Constants;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;
import p011de.robv.android.xposed.XC_MethodHook;
import p011de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import p011de.robv.android.xposed.XposedBridge;
import p011de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet;
import p011de.robv.android.xposed.XposedHelpers;
import p011de.robv.android.xposed.callbacks.XC_LayoutInflated;
import p011de.robv.android.xposed.callbacks.XC_LayoutInflated.LayoutInflatedParam;
import p011de.robv.android.xposed.callbacks.XC_LayoutInflated.Unhook;
import p011de.robv.android.xposed.callbacks.XCallback;
import xposed.dummy.XResourcesSuperClass;
import xposed.dummy.XTypedArraySuperClass;

public class XResources extends XResourcesSuperClass {
    private static final String EXTRA_XML_INSTANCE_DETAILS = "xmlInstanceDetails";
    /* access modifiers changed from: private */
    public static final SparseArray<ColorStateList> sColorStateListCache = new SparseArray<>(0);
    /* access modifiers changed from: private */
    public static final ThreadLocal<LinkedList<MethodHookParam>> sIncludedLayouts = new ThreadLocal<LinkedList<MethodHookParam>>() {
        /* access modifiers changed from: protected */
        public LinkedList<MethodHookParam> initialValue() {
            return new LinkedList<>();
        }
    };
    private static ThreadLocal<Object> sLatestResKey = null;
    private static final SparseArray<HashMap<String, CopyOnWriteSortedSet<XC_LayoutInflated>>> sLayoutCallbacks = new SparseArray<>();
    private static final SparseArray<HashMap<String, Object>> sReplacements = new SparseArray<>();
    private static final HashMap<String, byte[]> sReplacementsCacheMap = new HashMap<>();
    private static final HashMap<String, Long> sResDirLastModified = new HashMap<>();
    private static final HashMap<String, String> sResDirPackageNames = new HashMap<>();
    private static final SparseArray<HashMap<String, ResourceNames>> sResourceNames = new SparseArray<>();
    private static final byte[] sSystemReplacementsCache = new byte[256];
    /* access modifiers changed from: private */
    public static final WeakHashMap<XmlResourceParser, XMLInstanceDetails> sXmlInstanceDetails = new WeakHashMap<>();
    private boolean mIsObjectInited;
    private String mPackageName;
    private byte[] mReplacementsCache;
    private String mResDir;

    public static class DimensionReplacement {
        private final int mUnit;
        private final float mValue;

        public DimensionReplacement(float f, int i) {
            this.mValue = f;
            this.mUnit = i;
        }

        public float getDimension(DisplayMetrics displayMetrics) {
            return TypedValue.applyDimension(this.mUnit, this.mValue, displayMetrics);
        }

        public int getDimensionPixelOffset(DisplayMetrics displayMetrics) {
            return (int) TypedValue.applyDimension(this.mUnit, this.mValue, displayMetrics);
        }

        public int getDimensionPixelSize(DisplayMetrics displayMetrics) {
            int applyDimension = (int) (TypedValue.applyDimension(this.mUnit, this.mValue, displayMetrics) + 0.5f);
            if (applyDimension != 0) {
                return applyDimension;
            }
            if (this.mValue == 0.0f) {
                return 0;
            }
            return this.mValue > 0.0f ? 1 : -1;
        }
    }

    public static abstract class DrawableLoader {
        public abstract Drawable newDrawable(XResources xResources, int i) throws Throwable;

        public Drawable newDrawableForDensity(XResources xResources, int i, int i2) throws Throwable {
            return newDrawable(xResources, i);
        }
    }

    public static class ResourceNames {
        public final String fullName;

        /* renamed from: id */
        public final int f1id;
        public final String name;
        public final String pkg;
        public final String type;

        private ResourceNames(int i, String str, String str2, String str3) {
            this.f1id = i;
            this.pkg = str;
            this.name = str2;
            this.type = str3;
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
            sb.append(str3);
            sb.append("/");
            sb.append(str2);
            this.fullName = sb.toString();
        }

        public boolean equals(String str, String str2, String str3, int i) {
            return (str == null || str.equals(this.pkg)) && (str2 == null || str2.equals(this.name)) && ((str3 == null || str3.equals(this.type)) && (i == 0 || i == this.f1id));
        }
    }

    private class XMLInstanceDetails {
        public final CopyOnWriteSortedSet<XC_LayoutInflated> callbacks;
        public final XResources res;
        public final ResourceNames resNames;
        public final String variant;

        private XMLInstanceDetails(ResourceNames resourceNames, String str, CopyOnWriteSortedSet<XC_LayoutInflated> copyOnWriteSortedSet) {
            this.res = XResources.this;
            this.resNames = resourceNames;
            this.variant = str;
            this.callbacks = copyOnWriteSortedSet;
        }
    }

    public static class XTypedArray extends XTypedArraySuperClass {
        private XTypedArray() {
            super(null, null, null, 0);
            throw new UnsupportedOperationException();
        }

        public boolean getBoolean(int i, boolean z) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (access$400 instanceof Boolean) {
                return ((Boolean) access$400).booleanValue();
            }
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getBoolean(i, z);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getBoolean(xResForwarder.getId());
        }

        public int getColor(int i, int i2) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (access$400 instanceof Integer) {
                return ((Integer) access$400).intValue();
            }
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getColor(i, i2);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getColor(xResForwarder.getId());
        }

        public ColorStateList getColorStateList(int i) {
            ColorStateList colorStateList;
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (access$400 instanceof ColorStateList) {
                return (ColorStateList) access$400;
            }
            if (access$400 instanceof Integer) {
                int intValue = ((Integer) access$400).intValue();
                synchronized (XResources.sColorStateListCache) {
                    colorStateList = (ColorStateList) XResources.sColorStateListCache.get(intValue);
                    if (colorStateList == null) {
                        colorStateList = ColorStateList.valueOf(intValue);
                        XResources.sColorStateListCache.put(intValue, colorStateList);
                    }
                }
                return colorStateList;
            } else if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getColorStateList(i);
            } else {
                XResForwarder xResForwarder = (XResForwarder) access$400;
                return xResForwarder.getResources().getColorStateList(xResForwarder.getId());
            }
        }

        public float getDimension(int i, float f) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getDimension(i, f);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getDimension(xResForwarder.getId());
        }

        public int getDimensionPixelOffset(int i, int i2) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getDimensionPixelOffset(i, i2);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getDimensionPixelOffset(xResForwarder.getId());
        }

        public int getDimensionPixelSize(int i, int i2) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getDimensionPixelSize(i, i2);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getDimensionPixelSize(xResForwarder.getId());
        }

        public Drawable getDrawable(int i) {
            int resourceId = getResourceId(i, 0);
            XResources xResources = (XResources) getResources();
            Object access$400 = xResources.getReplacement(resourceId);
            if (access$400 instanceof DrawableLoader) {
                try {
                    Drawable newDrawable = ((DrawableLoader) access$400).newDrawable(xResources, resourceId);
                    if (newDrawable != null) {
                        return newDrawable;
                    }
                } catch (Throwable th) {
                    XposedBridge.log(th);
                }
            } else if (access$400 instanceof Integer) {
                return new ColorDrawable(((Integer) access$400).intValue());
            } else {
                if (access$400 instanceof XResForwarder) {
                    XResForwarder xResForwarder = (XResForwarder) access$400;
                    return xResForwarder.getResources().getDrawable(xResForwarder.getId());
                }
            }
            return XResources.super.getDrawable(i);
        }

        public float getFloat(int i, float f) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getFloat(i, f);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getDimension(xResForwarder.getId());
        }

        public float getFraction(int i, int i2, int i3, float f) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getFraction(i, i2, i3, f);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getFraction(xResForwarder.getId(), i2, i3);
        }

        public int getInt(int i, int i2) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (access$400 instanceof Integer) {
                return ((Integer) access$400).intValue();
            }
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getInt(i, i2);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getInteger(xResForwarder.getId());
        }

        public int getInteger(int i, int i2) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (access$400 instanceof Integer) {
                return ((Integer) access$400).intValue();
            }
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getInteger(i, i2);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getInteger(xResForwarder.getId());
        }

        public int getLayoutDimension(int i, int i2) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getLayoutDimension(i, i2);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getDimensionPixelSize(xResForwarder.getId());
        }

        public int getLayoutDimension(int i, String str) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getLayoutDimension(i, str);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getDimensionPixelSize(xResForwarder.getId());
        }

        public String getString(int i) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (access$400 instanceof CharSequence) {
                return access$400.toString();
            }
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getString(i);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getString(xResForwarder.getId());
        }

        public CharSequence getText(int i) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (access$400 instanceof CharSequence) {
                return (CharSequence) access$400;
            }
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getText(i);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getText(xResForwarder.getId());
        }

        public CharSequence[] getTextArray(int i) {
            Object access$400 = ((XResources) getResources()).getReplacement(getResourceId(i, 0));
            if (access$400 instanceof CharSequence[]) {
                return (CharSequence[]) access$400;
            }
            if (!(access$400 instanceof XResForwarder)) {
                return XResources.super.getTextArray(i);
            }
            XResForwarder xResForwarder = (XResForwarder) access$400;
            return xResForwarder.getResources().getTextArray(xResForwarder.getId());
        }
    }

    private static native void rewriteXmlReferencesNative(long j, XResources xResources, Resources resources);

    private XResources() {
        throw new UnsupportedOperationException();
    }

    public void initObject(String str) {
        if (!this.mIsObjectInited) {
            this.mResDir = str;
            this.mPackageName = getPackageName(str);
            if (str != null) {
                synchronized (sReplacementsCacheMap) {
                    this.mReplacementsCache = (byte[]) sReplacementsCacheMap.get(str);
                    if (this.mReplacementsCache == null) {
                        this.mReplacementsCache = new byte[128];
                        sReplacementsCacheMap.put(str, this.mReplacementsCache);
                    }
                }
            }
            this.mIsObjectInited = true;
            return;
        }
        throw new IllegalStateException("Object has already been initialized");
    }

    public boolean isFirstLoad() {
        synchronized (sReplacements) {
            if (this.mResDir == null) {
                return false;
            }
            Long valueOf = Long.valueOf(new File(this.mResDir).lastModified());
            Long l = (Long) sResDirLastModified.get(this.mResDir);
            if (valueOf.equals(l)) {
                return false;
            }
            sResDirLastModified.put(this.mResDir, valueOf);
            if (l == null) {
                return true;
            }
            for (int i = 0; i < sReplacements.size(); i++) {
                ((HashMap) sReplacements.valueAt(i)).remove(this.mResDir);
            }
            Arrays.fill(this.mReplacementsCache, 0);
            return true;
        }
    }

    public static void setPackageNameForResDir(String str, String str2) {
        synchronized (sResDirPackageNames) {
            sResDirPackageNames.put(str2, str);
        }
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    private static String getPackageName(String str) {
        String str2;
        PackageLite packageLite;
        if (str == null) {
            return "android";
        }
        synchronized (sResDirPackageNames) {
            str2 = (String) sResDirPackageNames.get(str);
        }
        if (str2 != null) {
            return str2;
        }
        if (VERSION.SDK_INT >= 21) {
            try {
                packageLite = PackageParser.parsePackageLite(new File(str), 0);
            } catch (PackageParserException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Could not determine package name for ");
                sb.append(str);
                throw new IllegalStateException(sb.toString(), e);
            }
        } else {
            packageLite = PackageParser.parsePackageLite(str, 0);
        }
        if (packageLite == null || packageLite.packageName == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Could not determine package name for ");
            sb2.append(str);
            throw new IllegalStateException(sb2.toString());
        }
        String str3 = XposedBridge.TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Package name for ");
        sb3.append(str);
        sb3.append(" had to be retrieved via parser");
        Log.w(str3, sb3.toString());
        String str4 = packageLite.packageName;
        setPackageNameForResDir(str4, str);
        return str4;
    }

    public static String getPackageNameDuringConstruction() {
        if (sLatestResKey != null) {
            Object obj = sLatestResKey.get();
            if (obj != null) {
                return getPackageName((String) XposedHelpers.getObjectField(obj, "mResDir"));
            }
        }
        throw new IllegalStateException("This method can only be called during getTopLevelResources()");
    }

    public static void init(ThreadLocal<Object> threadLocal) throws Exception {
        sLatestResKey = threadLocal;
        XposedHelpers.findAndHookMethod(LayoutInflater.class, "inflate", XmlPullParser.class, ViewGroup.class, Boolean.TYPE, new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                XMLInstanceDetails xMLInstanceDetails;
                if (!methodHookParam.hasThrowable()) {
                    synchronized (XResources.sXmlInstanceDetails) {
                        xMLInstanceDetails = (XMLInstanceDetails) XResources.sXmlInstanceDetails.get(methodHookParam.args[0]);
                    }
                    if (xMLInstanceDetails != null) {
                        LayoutInflatedParam layoutInflatedParam = new LayoutInflatedParam(xMLInstanceDetails.callbacks);
                        layoutInflatedParam.view = (View) methodHookParam.getResult();
                        layoutInflatedParam.resNames = xMLInstanceDetails.resNames;
                        layoutInflatedParam.variant = xMLInstanceDetails.variant;
                        layoutInflatedParam.res = xMLInstanceDetails.res;
                        XCallback.callAll(layoutInflatedParam);
                    }
                }
            }
        });
        C00133 r9 = new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                ((LinkedList) XResources.sIncludedLayouts.get()).push(methodHookParam);
            }

            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                ((LinkedList) XResources.sIncludedLayouts.get()).pop();
                if (!methodHookParam.hasThrowable()) {
                    XMLInstanceDetails xMLInstanceDetails = (XMLInstanceDetails) methodHookParam.getObjectExtra(XResources.EXTRA_XML_INSTANCE_DETAILS);
                    if (xMLInstanceDetails != null) {
                        LayoutInflatedParam layoutInflatedParam = new LayoutInflatedParam(xMLInstanceDetails.callbacks);
                        ViewGroup viewGroup = (ViewGroup) methodHookParam.args[VERSION.SDK_INT < 23 ? (char) 1 : 2];
                        layoutInflatedParam.view = viewGroup.getChildAt(viewGroup.getChildCount() - 1);
                        layoutInflatedParam.resNames = xMLInstanceDetails.resNames;
                        layoutInflatedParam.variant = xMLInstanceDetails.variant;
                        layoutInflatedParam.res = xMLInstanceDetails.res;
                        XCallback.callAll(layoutInflatedParam);
                    }
                }
            }
        };
        if (VERSION.SDK_INT < 21) {
            XposedHelpers.findAndHookMethod(LayoutInflater.class, "parseInclude", XmlPullParser.class, View.class, AttributeSet.class, r9);
        } else if (VERSION.SDK_INT < 23) {
            XposedHelpers.findAndHookMethod(LayoutInflater.class, "parseInclude", XmlPullParser.class, View.class, AttributeSet.class, Boolean.TYPE, r9);
        } else {
            XposedHelpers.findAndHookMethod(LayoutInflater.class, "parseInclude", XmlPullParser.class, Context.class, View.class, AttributeSet.class, r9);
        }
    }

    private ResourceNames getResourceNames(int i) {
        ResourceNames resourceNames = new ResourceNames(i, getResourcePackageName(i), getResourceTypeName(i), getResourceEntryName(i));
        return resourceNames;
    }

    private static ResourceNames getSystemResourceNames(int i) {
        Resources system = getSystem();
        ResourceNames resourceNames = new ResourceNames(i, system.getResourcePackageName(i), system.getResourceTypeName(i), system.getResourceEntryName(i));
        return resourceNames;
    }

    private static void putResourceNames(String str, ResourceNames resourceNames) {
        int i = resourceNames.f1id;
        synchronized (sResourceNames) {
            HashMap hashMap = (HashMap) sResourceNames.get(i);
            if (hashMap == null) {
                hashMap = new HashMap();
                sResourceNames.put(i, hashMap);
            }
            synchronized (hashMap) {
                hashMap.put(str, resourceNames);
            }
        }
    }

    public void setReplacement(int i, Object obj) {
        setReplacement(i, obj, this);
    }

    @Deprecated
    public void setReplacement(String str, Object obj) {
        int identifier = getIdentifier(str, null, null);
        if (identifier != 0) {
            setReplacement(identifier, obj, this);
            return;
        }
        throw new NotFoundException(str);
    }

    public void setReplacement(String str, String str2, String str3, Object obj) {
        int identifier = getIdentifier(str3, str2, str);
        if (identifier != 0) {
            setReplacement(identifier, obj, this);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(str2);
        sb.append("/");
        sb.append(str3);
        throw new NotFoundException(sb.toString());
    }

    public static void setSystemWideReplacement(int i, Object obj) {
        setReplacement(i, obj, null);
    }

    @Deprecated
    public static void setSystemWideReplacement(String str, Object obj) {
        int identifier = getSystem().getIdentifier(str, null, null);
        if (identifier != 0) {
            setReplacement(identifier, obj, null);
            return;
        }
        throw new NotFoundException(str);
    }

    public static void setSystemWideReplacement(String str, String str2, String str3, Object obj) {
        int identifier = getSystem().getIdentifier(str3, str2, str);
        if (identifier != 0) {
            setReplacement(identifier, obj, null);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(str2);
        sb.append("/");
        sb.append(str3);
        throw new NotFoundException(sb.toString());
    }

    private static void setReplacement(int i, Object obj, XResources xResources) {
        String str = xResources != null ? xResources.mResDir : null;
        if (i == 0) {
            throw new IllegalArgumentException("id 0 is not an allowed resource identifier");
        } else if (str == null && i >= 2130706432) {
            throw new IllegalArgumentException("ids >= 0x7f000000 are app specific and cannot be set for the framework");
        } else if (!(obj instanceof Drawable)) {
            if (i < 2130706432) {
                int i2 = ((i & 458752) >> 11) | ((i & 248) >> 3);
                synchronized (sSystemReplacementsCache) {
                    byte[] bArr = sSystemReplacementsCache;
                    bArr[i2] = (byte) ((1 << (i & 7)) | bArr[i2]);
                }
            } else {
                int i3 = ((i & 458752) >> 12) | ((i & DragView.COLOR_CHANGE_DURATION) >> 3);
                synchronized (xResources.mReplacementsCache) {
                    byte[] bArr2 = xResources.mReplacementsCache;
                    bArr2[i3] = (byte) ((1 << (i & 7)) | bArr2[i3]);
                }
            }
            synchronized (sReplacements) {
                HashMap hashMap = (HashMap) sReplacements.get(i);
                if (hashMap == null) {
                    hashMap = new HashMap();
                    sReplacements.put(i, hashMap);
                }
                hashMap.put(str, obj);
            }
        } else {
            throw new IllegalArgumentException("Drawable replacements are deprecated since Xposed 2.1. Use DrawableLoader instead.");
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x005b, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object getReplacement(int r5) {
        /*
            r4 = this;
            r0 = 0
            if (r5 > 0) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 2130706432(0x7f000000, float:1.7014118E38)
            r2 = 1
            r3 = 458752(0x70000, float:6.42848E-40)
            if (r5 >= r1) goto L_0x001f
            r1 = r5 & r3
            int r1 = r1 >> 11
            r3 = r5 & 248(0xf8, float:3.48E-43)
            int r3 = r3 >> 3
            r1 = r1 | r3
            byte[] r3 = sSystemReplacementsCache
            byte r1 = r3[r1]
            r3 = r5 & 7
            int r2 = r2 << r3
            r1 = r1 & r2
            if (r1 != 0) goto L_0x0037
            return r0
        L_0x001f:
            java.lang.String r1 = r4.mResDir
            if (r1 == 0) goto L_0x0037
            r1 = r5 & r3
            int r1 = r1 >> 12
            r3 = r5 & 120(0x78, float:1.68E-43)
            int r3 = r3 >> 3
            r1 = r1 | r3
            byte[] r3 = r4.mReplacementsCache
            byte r1 = r3[r1]
            r3 = r5 & 7
            int r2 = r2 << r3
            r1 = r1 & r2
            if (r1 != 0) goto L_0x0037
            return r0
        L_0x0037:
            android.util.SparseArray<java.util.HashMap<java.lang.String, java.lang.Object>> r1 = sReplacements
            monitor-enter(r1)
            android.util.SparseArray<java.util.HashMap<java.lang.String, java.lang.Object>> r2 = sReplacements     // Catch:{ all -> 0x005f }
            java.lang.Object r5 = r2.get(r5)     // Catch:{ all -> 0x005f }
            java.util.HashMap r5 = (java.util.HashMap) r5     // Catch:{ all -> 0x005f }
            monitor-exit(r1)     // Catch:{ all -> 0x005f }
            if (r5 != 0) goto L_0x0046
            return r0
        L_0x0046:
            monitor-enter(r5)
            java.lang.String r1 = r4.mResDir     // Catch:{ all -> 0x005c }
            java.lang.Object r1 = r5.get(r1)     // Catch:{ all -> 0x005c }
            if (r1 != 0) goto L_0x005a
            java.lang.String r2 = r4.mResDir     // Catch:{ all -> 0x005c }
            if (r2 != 0) goto L_0x0054
            goto L_0x005a
        L_0x0054:
            java.lang.Object r0 = r5.get(r0)     // Catch:{ all -> 0x005c }
            monitor-exit(r5)     // Catch:{ all -> 0x005c }
            return r0
        L_0x005a:
            monitor-exit(r5)     // Catch:{ all -> 0x005c }
            return r1
        L_0x005c:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x005c }
            throw r0
        L_0x005f:
            r5 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x005f }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.res.XResources.getReplacement(int):java.lang.Object");
    }

    public XmlResourceParser getAnimation(int i) throws NotFoundException {
        long j;
        Object replacement = getReplacement(i);
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getAnimation(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        Resources resources = xResForwarder.getResources();
        int id = xResForwarder.getId();
        boolean isXmlCached = isXmlCached(resources, id);
        XmlResourceParser animation = resources.getAnimation(id);
        if (!isXmlCached) {
            if (VERSION.SDK_INT >= 21) {
                j = XposedHelpers.getLongField(animation, "mParseState");
            } else {
                j = (long) XposedHelpers.getIntField(animation, "mParseState");
            }
            rewriteXmlReferencesNative(j, this, resources);
        }
        return animation;
    }

    public boolean getBoolean(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof Boolean) {
            return ((Boolean) replacement).booleanValue();
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getBoolean(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getBoolean(xResForwarder.getId());
    }

    public int getColor(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof Integer) {
            return ((Integer) replacement).intValue();
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getColor(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getColor(xResForwarder.getId());
    }

    public ColorStateList getColorStateList(int i) throws NotFoundException {
        ColorStateList colorStateList;
        Object replacement = getReplacement(i);
        if (replacement instanceof ColorStateList) {
            return (ColorStateList) replacement;
        }
        if (replacement instanceof Integer) {
            int intValue = ((Integer) replacement).intValue();
            synchronized (sColorStateListCache) {
                colorStateList = (ColorStateList) sColorStateListCache.get(intValue);
                if (colorStateList == null) {
                    colorStateList = ColorStateList.valueOf(intValue);
                    sColorStateListCache.put(intValue, colorStateList);
                }
            }
            return colorStateList;
        } else if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getColorStateList(i);
        } else {
            XResForwarder xResForwarder = (XResForwarder) replacement;
            return xResForwarder.getResources().getColorStateList(xResForwarder.getId());
        }
    }

    public float getDimension(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof DimensionReplacement) {
            return ((DimensionReplacement) replacement).getDimension(getDisplayMetrics());
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getDimension(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getDimension(xResForwarder.getId());
    }

    public int getDimensionPixelOffset(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof DimensionReplacement) {
            return ((DimensionReplacement) replacement).getDimensionPixelOffset(getDisplayMetrics());
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getDimensionPixelOffset(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getDimensionPixelOffset(xResForwarder.getId());
    }

    public int getDimensionPixelSize(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof DimensionReplacement) {
            return ((DimensionReplacement) replacement).getDimensionPixelSize(getDisplayMetrics());
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getDimensionPixelSize(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getDimensionPixelSize(xResForwarder.getId());
    }

    public Drawable getDrawable(int i) throws NotFoundException {
        Drawable drawable;
        try {
            if (XposedHelpers.incrementMethodDepth("getDrawable") == 1) {
                Object replacement = getReplacement(i);
                if (replacement instanceof DrawableLoader) {
                    Drawable newDrawable = ((DrawableLoader) replacement).newDrawable(this, i);
                    if (newDrawable != null) {
                        XposedHelpers.decrementMethodDepth("getDrawable");
                        return newDrawable;
                    }
                } else {
                    if (replacement instanceof Integer) {
                        drawable = new ColorDrawable(((Integer) replacement).intValue());
                    } else if (replacement instanceof XResForwarder) {
                        drawable = ((XResForwarder) replacement).getResources().getDrawable(((XResForwarder) replacement).getId());
                    }
                    XposedHelpers.decrementMethodDepth("getDrawable");
                    return drawable;
                }
            }
        } catch (Throwable th) {
            XposedHelpers.decrementMethodDepth("getDrawable");
            throw th;
        }
        drawable = XResources.super.getDrawable(i);
        XposedHelpers.decrementMethodDepth("getDrawable");
        return drawable;
    }

    public Drawable getDrawable(int i, Theme theme) throws NotFoundException {
        Drawable drawable;
        try {
            if (XposedHelpers.incrementMethodDepth("getDrawable") == 1) {
                Object replacement = getReplacement(i);
                if (replacement instanceof DrawableLoader) {
                    Drawable newDrawable = ((DrawableLoader) replacement).newDrawable(this, i);
                    if (newDrawable != null) {
                        XposedHelpers.decrementMethodDepth("getDrawable");
                        return newDrawable;
                    }
                } else {
                    if (replacement instanceof Integer) {
                        drawable = new ColorDrawable(((Integer) replacement).intValue());
                    } else if (replacement instanceof XResForwarder) {
                        drawable = ((XResForwarder) replacement).getResources().getDrawable(((XResForwarder) replacement).getId());
                    }
                    XposedHelpers.decrementMethodDepth("getDrawable");
                    return drawable;
                }
            }
        } catch (Throwable th) {
            XposedHelpers.decrementMethodDepth("getDrawable");
            throw th;
        }
        drawable = XResources.super.getDrawable(i, theme);
        XposedHelpers.decrementMethodDepth("getDrawable");
        return drawable;
    }

    public Drawable getDrawable(int i, Theme theme, boolean z) throws NotFoundException {
        Drawable drawable;
        try {
            if (XposedHelpers.incrementMethodDepth("getDrawable") == 1) {
                Object replacement = getReplacement(i);
                if (replacement instanceof DrawableLoader) {
                    Drawable newDrawable = ((DrawableLoader) replacement).newDrawable(this, i);
                    if (newDrawable != null) {
                        XposedHelpers.decrementMethodDepth("getDrawable");
                        return newDrawable;
                    }
                } else {
                    if (replacement instanceof Integer) {
                        drawable = new ColorDrawable(((Integer) replacement).intValue());
                    } else if (replacement instanceof XResForwarder) {
                        drawable = ((XResForwarder) replacement).getResources().getDrawable(((XResForwarder) replacement).getId());
                    }
                    XposedHelpers.decrementMethodDepth("getDrawable");
                    return drawable;
                }
            }
        } catch (Throwable th) {
            XposedHelpers.decrementMethodDepth("getDrawable");
            throw th;
        }
        drawable = XResources.super.getDrawable(i, theme, z);
        XposedHelpers.decrementMethodDepth("getDrawable");
        return drawable;
    }

    public Drawable getDrawableForDensity(int i, int i2) throws NotFoundException {
        Drawable drawableForDensity;
        try {
            if (XposedHelpers.incrementMethodDepth("getDrawableForDensity") == 1) {
                Object replacement = getReplacement(i);
                if (replacement instanceof DrawableLoader) {
                    Drawable newDrawableForDensity = ((DrawableLoader) replacement).newDrawableForDensity(this, i, i2);
                    if (newDrawableForDensity != null) {
                        XposedHelpers.decrementMethodDepth("getDrawableForDensity");
                        return newDrawableForDensity;
                    }
                } else {
                    if (replacement instanceof Integer) {
                        drawableForDensity = new ColorDrawable(((Integer) replacement).intValue());
                    } else if (replacement instanceof XResForwarder) {
                        drawableForDensity = ((XResForwarder) replacement).getResources().getDrawableForDensity(((XResForwarder) replacement).getId(), i2);
                    }
                    XposedHelpers.decrementMethodDepth("getDrawableForDensity");
                    return drawableForDensity;
                }
            }
        } catch (Throwable th) {
            XposedHelpers.decrementMethodDepth("getDrawableForDensity");
            throw th;
        }
        drawableForDensity = XResources.super.getDrawableForDensity(i, i2);
        XposedHelpers.decrementMethodDepth("getDrawableForDensity");
        return drawableForDensity;
    }

    public Drawable getDrawableForDensity(int i, int i2, Theme theme) throws NotFoundException {
        Drawable drawableForDensity;
        try {
            if (XposedHelpers.incrementMethodDepth("getDrawableForDensity") == 1) {
                Object replacement = getReplacement(i);
                if (replacement instanceof DrawableLoader) {
                    Drawable newDrawableForDensity = ((DrawableLoader) replacement).newDrawableForDensity(this, i, i2);
                    if (newDrawableForDensity != null) {
                        XposedHelpers.decrementMethodDepth("getDrawableForDensity");
                        return newDrawableForDensity;
                    }
                } else {
                    if (replacement instanceof Integer) {
                        drawableForDensity = new ColorDrawable(((Integer) replacement).intValue());
                    } else if (replacement instanceof XResForwarder) {
                        drawableForDensity = ((XResForwarder) replacement).getResources().getDrawableForDensity(((XResForwarder) replacement).getId(), i2);
                    }
                    XposedHelpers.decrementMethodDepth("getDrawableForDensity");
                    return drawableForDensity;
                }
            }
        } catch (Throwable th) {
            XposedHelpers.decrementMethodDepth("getDrawableForDensity");
            throw th;
        }
        drawableForDensity = XResources.super.getDrawableForDensity(i, i2, theme);
        XposedHelpers.decrementMethodDepth("getDrawableForDensity");
        return drawableForDensity;
    }

    public Drawable getDrawableForDensity(int i, int i2, Theme theme, boolean z) throws NotFoundException {
        Drawable drawableForDensity;
        try {
            if (XposedHelpers.incrementMethodDepth("getDrawableForDensity") == 1) {
                Object replacement = getReplacement(i);
                if (replacement instanceof DrawableLoader) {
                    Drawable newDrawableForDensity = ((DrawableLoader) replacement).newDrawableForDensity(this, i, i2);
                    if (newDrawableForDensity != null) {
                        XposedHelpers.decrementMethodDepth("getDrawableForDensity");
                        return newDrawableForDensity;
                    }
                } else {
                    if (replacement instanceof Integer) {
                        drawableForDensity = new ColorDrawable(((Integer) replacement).intValue());
                    } else if (replacement instanceof XResForwarder) {
                        drawableForDensity = ((XResForwarder) replacement).getResources().getDrawableForDensity(((XResForwarder) replacement).getId(), i2);
                    }
                    XposedHelpers.decrementMethodDepth("getDrawableForDensity");
                    return drawableForDensity;
                }
            }
        } catch (Throwable th) {
            XposedHelpers.decrementMethodDepth("getDrawableForDensity");
            throw th;
        }
        drawableForDensity = XResources.super.getDrawableForDensity(i, i2, theme, z);
        XposedHelpers.decrementMethodDepth("getDrawableForDensity");
        return drawableForDensity;
    }

    public float getFraction(int i, int i2, int i3) {
        Object replacement = getReplacement(i);
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getFraction(i, i2, i3);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getFraction(xResForwarder.getId(), i2, i3);
    }

    public int getInteger(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof Integer) {
            return ((Integer) replacement).intValue();
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getInteger(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getInteger(xResForwarder.getId());
    }

    public int[] getIntArray(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof int[]) {
            return (int[]) replacement;
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getIntArray(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getIntArray(xResForwarder.getId());
    }

    public XmlResourceParser getLayout(int i) throws NotFoundException {
        XmlResourceParser xmlResourceParser;
        HashMap hashMap;
        CopyOnWriteSortedSet copyOnWriteSortedSet;
        long j;
        Object replacement = getReplacement(i);
        if (replacement instanceof XResForwarder) {
            XResForwarder xResForwarder = (XResForwarder) replacement;
            Resources resources = xResForwarder.getResources();
            int id = xResForwarder.getId();
            boolean isXmlCached = isXmlCached(resources, id);
            xmlResourceParser = resources.getLayout(id);
            if (!isXmlCached) {
                if (VERSION.SDK_INT >= 21) {
                    j = XposedHelpers.getLongField(xmlResourceParser, "mParseState");
                } else {
                    j = (long) XposedHelpers.getIntField(xmlResourceParser, "mParseState");
                }
                rewriteXmlReferencesNative(j, this, resources);
            }
        } else {
            xmlResourceParser = XResources.super.getLayout(i);
        }
        synchronized (sLayoutCallbacks) {
            hashMap = (HashMap) sLayoutCallbacks.get(i);
        }
        if (hashMap != null) {
            synchronized (hashMap) {
                CopyOnWriteSortedSet copyOnWriteSortedSet2 = (CopyOnWriteSortedSet) hashMap.get(this.mResDir);
                if (copyOnWriteSortedSet2 == null && this.mResDir != null) {
                    copyOnWriteSortedSet2 = (CopyOnWriteSortedSet) hashMap.get(null);
                }
                copyOnWriteSortedSet = copyOnWriteSortedSet2;
            }
            if (copyOnWriteSortedSet != null) {
                String str = "layout";
                TypedValue typedValue = (TypedValue) XposedHelpers.getObjectField(this, "mTmpValue");
                getValue(i, typedValue, true);
                if (typedValue.type == 3) {
                    String[] split = typedValue.string.toString().split("/", 3);
                    if (split.length == 3) {
                        str = split[1];
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unexpected resource path \"");
                        sb.append(typedValue.string.toString());
                        sb.append("\" for resource id 0x");
                        sb.append(Integer.toHexString(i));
                        XposedBridge.log(sb.toString());
                    }
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(new NotFoundException("Could not find file name for resource id 0x"));
                    sb2.append(Integer.toHexString(i));
                    XposedBridge.log(sb2.toString());
                }
                String str2 = str;
                synchronized (sXmlInstanceDetails) {
                    synchronized (sResourceNames) {
                        HashMap hashMap2 = (HashMap) sResourceNames.get(i);
                        if (hashMap2 != null) {
                            synchronized (hashMap2) {
                                XMLInstanceDetails xMLInstanceDetails = new XMLInstanceDetails((ResourceNames) hashMap2.get(this.mResDir), str2, copyOnWriteSortedSet);
                                sXmlInstanceDetails.put(xmlResourceParser, xMLInstanceDetails);
                                MethodHookParam methodHookParam = (MethodHookParam) ((LinkedList) sIncludedLayouts.get()).peek();
                                if (methodHookParam != null) {
                                    methodHookParam.setObjectExtra(EXTRA_XML_INSTANCE_DETAILS, xMLInstanceDetails);
                                }
                            }
                        }
                    }
                }
            }
        }
        return xmlResourceParser;
    }

    public Movie getMovie(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getMovie(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getMovie(xResForwarder.getId());
    }

    public CharSequence getQuantityText(int i, int i2) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getQuantityText(i, i2);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getQuantityText(xResForwarder.getId(), i2);
    }

    public String[] getStringArray(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof String[]) {
            return (String[]) replacement;
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getStringArray(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getStringArray(xResForwarder.getId());
    }

    public CharSequence getText(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof CharSequence) {
            return (CharSequence) replacement;
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getText(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getText(xResForwarder.getId());
    }

    public CharSequence getText(int i, CharSequence charSequence) {
        Object replacement = getReplacement(i);
        if (replacement instanceof CharSequence) {
            return (CharSequence) replacement;
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getText(i, charSequence);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getText(xResForwarder.getId(), charSequence);
    }

    public CharSequence[] getTextArray(int i) throws NotFoundException {
        Object replacement = getReplacement(i);
        if (replacement instanceof CharSequence[]) {
            return (CharSequence[]) replacement;
        }
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getTextArray(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        return xResForwarder.getResources().getTextArray(xResForwarder.getId());
    }

    public XmlResourceParser getXml(int i) throws NotFoundException {
        long j;
        Object replacement = getReplacement(i);
        if (!(replacement instanceof XResForwarder)) {
            return XResources.super.getXml(i);
        }
        XResForwarder xResForwarder = (XResForwarder) replacement;
        Resources resources = xResForwarder.getResources();
        int id = xResForwarder.getId();
        boolean isXmlCached = isXmlCached(resources, id);
        XmlResourceParser xml = resources.getXml(id);
        if (!isXmlCached) {
            if (VERSION.SDK_INT >= 21) {
                j = XposedHelpers.getLongField(xml, "mParseState");
            } else {
                j = (long) XposedHelpers.getIntField(xml, "mParseState");
            }
            rewriteXmlReferencesNative(j, this, resources);
        }
        return xml;
    }

    private static boolean isXmlCached(Resources resources, int i) {
        int[] iArr = (int[]) XposedHelpers.getObjectField(resources, "mCachedXmlBlockIds");
        synchronized (iArr) {
            for (int i2 : iArr) {
                if (i2 == i) {
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0032 A[Catch:{ Exception -> 0x0068 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0051 A[Catch:{ Exception -> 0x0068 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int translateResId(int r7, android.content.res.XResources r8, android.content.res.Resources r9) {
        /*
            java.lang.String r0 = r9.getResourceEntryName(r7)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r1 = r9.getResourceTypeName(r7)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r2 = r8.mPackageName     // Catch:{ Exception -> 0x0068 }
            r3 = 0
            int r2 = r8.getIdentifier(r0, r1, r2)     // Catch:{ NotFoundException -> 0x0010 }
            goto L_0x0011
        L_0x0010:
            r2 = 0
        L_0x0011:
            android.util.TypedValue r4 = new android.util.TypedValue     // Catch:{ NotFoundException -> 0x0025 }
            r4.<init>()     // Catch:{ NotFoundException -> 0x0025 }
            r9.getValue(r7, r4, r3)     // Catch:{ NotFoundException -> 0x0025 }
            int r5 = r4.type     // Catch:{ NotFoundException -> 0x0025 }
            r6 = 18
            if (r5 != r6) goto L_0x0023
            int r4 = r4.data     // Catch:{ NotFoundException -> 0x0025 }
            if (r4 == 0) goto L_0x0025
        L_0x0023:
            r4 = 1
            goto L_0x0026
        L_0x0025:
            r4 = 0
        L_0x0026:
            if (r4 != 0) goto L_0x004f
            if (r2 != 0) goto L_0x004f
            java.lang.String r5 = "id"
            boolean r5 = r1.equals(r5)     // Catch:{ Exception -> 0x0068 }
            if (r5 != 0) goto L_0x004f
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0068 }
            r8.<init>()     // Catch:{ Exception -> 0x0068 }
            r8.append(r1)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r9 = "/"
            r8.append(r9)     // Catch:{ Exception -> 0x0068 }
            r8.append(r0)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r9 = " is neither defined in module nor in original resources"
            r8.append(r9)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0068 }
            p011de.robv.android.xposed.XposedBridge.log(r8)     // Catch:{ Exception -> 0x0068 }
            return r3
        L_0x004f:
            if (r2 != 0) goto L_0x0055
            int r2 = getFakeResId(r9, r7)     // Catch:{ Exception -> 0x0068 }
        L_0x0055:
            if (r4 == 0) goto L_0x0067
            java.lang.String r0 = "id"
            boolean r0 = r1.equals(r0)     // Catch:{ Exception -> 0x0068 }
            if (r0 != 0) goto L_0x0067
            android.content.res.XResForwarder r0 = new android.content.res.XResForwarder     // Catch:{ Exception -> 0x0068 }
            r0.<init>(r9, r7)     // Catch:{ Exception -> 0x0068 }
            r8.setReplacement(r2, r0)     // Catch:{ Exception -> 0x0068 }
        L_0x0067:
            return r2
        L_0x0068:
            r8 = move-exception
            p011de.robv.android.xposed.XposedBridge.log(r8)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.res.XResources.translateResId(int, android.content.res.XResources, android.content.res.Resources):int");
    }

    public static int getFakeResId(String str) {
        return (str.hashCode() & ViewCompat.MEASURED_SIZE_MASK) | 2113929216;
    }

    public static int getFakeResId(Resources resources, int i) {
        return getFakeResId(resources.getResourceName(i));
    }

    public int addResource(Resources resources, int i) {
        int fakeResId = getFakeResId(resources, i);
        synchronized (sReplacements) {
            if (sReplacements.indexOfKey(fakeResId) < 0) {
                setReplacement(fakeResId, (Object) new XResForwarder(resources, i));
            }
        }
        return fakeResId;
    }

    private static int translateAttrId(String str, XResources xResources) {
        try {
            return xResources.getIdentifier(str, "attr", xResources.mPackageName);
        } catch (NotFoundException unused) {
            StringBuilder sb = new StringBuilder();
            sb.append("Attribute ");
            sb.append(str);
            sb.append(" not found in original resources");
            XposedBridge.log(sb.toString());
            return 0;
        }
    }

    public Unhook hookLayout(int i, XC_LayoutInflated xC_LayoutInflated) {
        return hookLayoutInternal(this.mResDir, i, getResourceNames(i), xC_LayoutInflated);
    }

    @Deprecated
    public Unhook hookLayout(String str, XC_LayoutInflated xC_LayoutInflated) {
        int identifier = getIdentifier(str, null, null);
        if (identifier != 0) {
            return hookLayout(identifier, xC_LayoutInflated);
        }
        throw new NotFoundException(str);
    }

    public Unhook hookLayout(String str, String str2, String str3, XC_LayoutInflated xC_LayoutInflated) {
        int identifier = getIdentifier(str3, str2, str);
        if (identifier != 0) {
            return hookLayout(identifier, xC_LayoutInflated);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(str2);
        sb.append("/");
        sb.append(str3);
        throw new NotFoundException(sb.toString());
    }

    public static Unhook hookSystemWideLayout(int i, XC_LayoutInflated xC_LayoutInflated) {
        if (i < 2130706432) {
            return hookLayoutInternal(null, i, getSystemResourceNames(i), xC_LayoutInflated);
        }
        throw new IllegalArgumentException("ids >= 0x7f000000 are app specific and cannot be set for the framework");
    }

    @Deprecated
    public static Unhook hookSystemWideLayout(String str, XC_LayoutInflated xC_LayoutInflated) {
        int identifier = getSystem().getIdentifier(str, null, null);
        if (identifier != 0) {
            return hookSystemWideLayout(identifier, xC_LayoutInflated);
        }
        throw new NotFoundException(str);
    }

    public static Unhook hookSystemWideLayout(String str, String str2, String str3, XC_LayoutInflated xC_LayoutInflated) {
        int identifier = getSystem().getIdentifier(str3, str2, str);
        if (identifier != 0) {
            return hookSystemWideLayout(identifier, xC_LayoutInflated);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(str2);
        sb.append("/");
        sb.append(str3);
        throw new NotFoundException(sb.toString());
    }

    private static Unhook hookLayoutInternal(String str, int i, ResourceNames resourceNames, XC_LayoutInflated xC_LayoutInflated) {
        HashMap hashMap;
        CopyOnWriteSortedSet copyOnWriteSortedSet;
        if (i != 0) {
            synchronized (sLayoutCallbacks) {
                hashMap = (HashMap) sLayoutCallbacks.get(i);
                if (hashMap == null) {
                    hashMap = new HashMap();
                    sLayoutCallbacks.put(i, hashMap);
                }
            }
            synchronized (hashMap) {
                copyOnWriteSortedSet = (CopyOnWriteSortedSet) hashMap.get(str);
                if (copyOnWriteSortedSet == null) {
                    copyOnWriteSortedSet = new CopyOnWriteSortedSet();
                    hashMap.put(str, copyOnWriteSortedSet);
                }
            }
            copyOnWriteSortedSet.add(xC_LayoutInflated);
            putResourceNames(str, resourceNames);
            xC_LayoutInflated.getClass();
            return new Unhook(str, i);
        }
        throw new IllegalArgumentException("id 0 is not an allowed resource identifier");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r2 = (p011de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet) r3.get(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0017, code lost:
        if (r2 != null) goto L_0x001b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0019, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001b, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x001c, code lost:
        r2.remove(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0010, code lost:
        monitor-enter(r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void unhookLayout(java.lang.String r2, int r3, p011de.robv.android.xposed.callbacks.XC_LayoutInflated r4) {
        /*
            android.util.SparseArray<java.util.HashMap<java.lang.String, de.robv.android.xposed.XposedBridge$CopyOnWriteSortedSet<de.robv.android.xposed.callbacks.XC_LayoutInflated>>> r0 = sLayoutCallbacks
            monitor-enter(r0)
            android.util.SparseArray<java.util.HashMap<java.lang.String, de.robv.android.xposed.XposedBridge$CopyOnWriteSortedSet<de.robv.android.xposed.callbacks.XC_LayoutInflated>>> r1 = sLayoutCallbacks     // Catch:{ all -> 0x0023 }
            java.lang.Object r3 = r1.get(r3)     // Catch:{ all -> 0x0023 }
            java.util.HashMap r3 = (java.util.HashMap) r3     // Catch:{ all -> 0x0023 }
            if (r3 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x0023 }
            return
        L_0x000f:
            monitor-exit(r0)     // Catch:{ all -> 0x0023 }
            monitor-enter(r3)
            java.lang.Object r2 = r3.get(r2)     // Catch:{ all -> 0x0020 }
            de.robv.android.xposed.XposedBridge$CopyOnWriteSortedSet r2 = (p011de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet) r2     // Catch:{ all -> 0x0020 }
            if (r2 != 0) goto L_0x001b
            monitor-exit(r3)     // Catch:{ all -> 0x0020 }
            return
        L_0x001b:
            monitor-exit(r3)     // Catch:{ all -> 0x0020 }
            r2.remove(r4)
            return
        L_0x0020:
            r2 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0020 }
            throw r2
        L_0x0023:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0023 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.res.XResources.unhookLayout(java.lang.String, int, de.robv.android.xposed.callbacks.XC_LayoutInflated):void");
    }
}
