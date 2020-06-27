package com.lody.virtual;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@TargetApi(23)
public abstract class DelegateApplication64Bit extends Application {
    private Application mTarget;

    /* access modifiers changed from: protected */
    public abstract String get32BitPackageName();

    private static Field findField(Object obj, String str) throws NoSuchFieldException {
        Class cls = obj.getClass();
        while (cls != null) {
            try {
                Field declaredField = cls.getDeclaredField(str);
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                return declaredField;
            } catch (NoSuchFieldException unused) {
                cls = cls.getSuperclass();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Field ");
        sb.append(str);
        sb.append(" not found in ");
        sb.append(obj.getClass());
        throw new NoSuchFieldException(sb.toString());
    }

    private static Method findMethod(Object obj, String str, Class<?>... clsArr) throws NoSuchMethodException {
        Class cls = obj.getClass();
        while (cls != null) {
            try {
                Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
                if (!declaredMethod.isAccessible()) {
                    declaredMethod.setAccessible(true);
                }
                return declaredMethod;
            } catch (NoSuchMethodException unused) {
                cls = cls.getSuperclass();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Method ");
        sb.append(str);
        sb.append(" with parameters ");
        sb.append(Arrays.asList(clsArr));
        sb.append(" not found in ");
        sb.append(obj.getClass());
        throw new NoSuchMethodException(sb.toString());
    }

    private static void expandFieldArray(Object obj, String str, Object[] objArr) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field findField = findField(obj, str);
        Object[] objArr2 = (Object[]) findField.get(obj);
        Object[] objArr3 = (Object[]) Array.newInstance(objArr2.getClass().getComponentType(), objArr2.length + objArr.length);
        System.arraycopy(objArr2, 0, objArr3, 0, objArr2.length);
        System.arraycopy(objArr, 0, objArr3, objArr2.length, objArr.length);
        findField.set(obj, objArr3);
    }

    private static void expandFieldList(Object obj, String str, Object[] objArr) throws NoSuchFieldException, IllegalAccessException {
        Field findField = findField(obj, str);
        Object[] array = ((List) findField.get(obj)).toArray();
        Object[] objArr2 = (Object[]) Array.newInstance(array.getClass().getComponentType(), array.length + 1);
        System.arraycopy(array, 0, objArr2, 0, array.length);
        System.arraycopy(objArr, 0, objArr2, array.length, 1);
        findField.set(obj, Arrays.asList(objArr2));
    }

    private static Object[] makeDexElements(Object obj, ArrayList<File> arrayList, ArrayList<IOException> arrayList2) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Method method;
        if (VERSION.SDK_INT >= 23) {
            method = findMethod(obj, "makePathElements", List.class, File.class, List.class);
        } else {
            method = findMethod(obj, "makeDexElements", ArrayList.class, File.class, ArrayList.class);
        }
        return (Object[]) method.invoke(obj, new Object[]{arrayList, null, arrayList2});
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        IOException[] iOExceptionArr;
        super.attachBaseContext(context);
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(get32BitPackageName(), 0);
            ClassLoader classLoader = getClassLoader();
            Object obj = findField(classLoader, "pathList").get(classLoader);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(new File(applicationInfo.publicSourceDir));
            ArrayList arrayList3 = new ArrayList();
            arrayList3.add(new File(applicationInfo.nativeLibraryDir));
            if (VERSION.SDK_INT > 25) {
                expandFieldList(obj, "nativeLibraryDirectories", new File[]{new File(applicationInfo.nativeLibraryDir)});
                expandFieldArray(obj, "nativeLibraryPathElements", (Object[]) findMethod(obj, "makePathElements", List.class).invoke(obj, new Object[]{arrayList3}));
            } else if (VERSION.SDK_INT >= 23) {
                expandFieldList(obj, "nativeLibraryDirectories", new File[]{new File(applicationInfo.nativeLibraryDir)});
                expandFieldArray(obj, "nativeLibraryPathElements", makeDexElements(obj, arrayList3, arrayList));
            } else {
                expandFieldArray(obj, "nativeLibraryDirectories", new File[]{new File(applicationInfo.nativeLibraryDir)});
            }
            expandFieldArray(obj, "dexElements", makeDexElements(obj, arrayList2, arrayList));
            if (arrayList.size() > 0) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Log.w(getClass().getSimpleName(), "Exception in makeDexElement", (IOException) it.next());
                }
                Field findField = findField(classLoader, "dexElementsSuppressedExceptions");
                IOException[] iOExceptionArr2 = (IOException[]) findField.get(classLoader);
                if (iOExceptionArr2 == null) {
                    iOExceptionArr = (IOException[]) arrayList.toArray(new IOException[arrayList.size()]);
                } else {
                    IOException[] iOExceptionArr3 = new IOException[(arrayList.size() + iOExceptionArr2.length)];
                    arrayList.toArray(iOExceptionArr3);
                    System.arraycopy(iOExceptionArr2, 0, iOExceptionArr3, arrayList.size(), iOExceptionArr2.length);
                    iOExceptionArr = iOExceptionArr3;
                }
                findField.set(classLoader, iOExceptionArr);
            }
            this.mTarget = (Application) classLoader.loadClass(applicationInfo.className).newInstance();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mTarget != null) {
            this.mTarget.onConfigurationChanged(configuration);
        }
    }

    public void onCreate() {
        super.onCreate();
        if (this.mTarget != null) {
            this.mTarget.onCreate();
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (this.mTarget != null) {
            this.mTarget.onLowMemory();
        }
    }

    public void onTerminate() {
        super.onTerminate();
        if (this.mTarget != null) {
            this.mTarget.onTerminate();
        }
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        if (this.mTarget != null) {
            this.mTarget.onTrimMemory(i);
        }
    }
}
