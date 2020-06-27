package com.lody.virtual.client.natives;

import android.hardware.Camera;
import android.media.AudioRecord;
import android.os.Build.VERSION;
import com.lody.virtual.helper.utils.EncodeUtils;
import dalvik.system.DexFile;
import java.lang.reflect.Method;

public class NativeMethods {
    public static Method gAudioRecordNativeCheckPermission;
    public static int gCameraMethodType;
    public static Method gCameraNativeSetup;
    public static Method gOpenDexFileNative;

    public static void init() {
        Method[] declaredMethods;
        String decode = EncodeUtils.decode("b3BlbkRleEZpbGVOYXRpdmU=");
        String decode2 = EncodeUtils.decode("b3BlbkRleEZpbGU=");
        if (VERSION.SDK_INT < 19) {
            decode = decode2;
        }
        Method[] declaredMethods2 = DexFile.class.getDeclaredMethods();
        int length = declaredMethods2.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Method method = declaredMethods2[i];
            if (method.getName().equals(decode)) {
                gOpenDexFileNative = method;
                break;
            }
            i++;
        }
        if (gOpenDexFileNative != null) {
            gOpenDexFileNative.setAccessible(true);
            gCameraMethodType = -1;
            try {
                gCameraNativeSetup = Camera.class.getDeclaredMethod("native_setup", new Class[]{Object.class, Integer.TYPE, String.class});
                gCameraMethodType = 1;
            } catch (NoSuchMethodException unused) {
            }
            if (gCameraNativeSetup == null) {
                try {
                    gCameraNativeSetup = Camera.class.getDeclaredMethod("native_setup", new Class[]{Object.class, Integer.TYPE, Integer.TYPE, String.class});
                    gCameraMethodType = 2;
                } catch (NoSuchMethodException unused2) {
                }
            }
            if (gCameraNativeSetup == null) {
                try {
                    gCameraNativeSetup = Camera.class.getDeclaredMethod("native_setup", new Class[]{Object.class, Integer.TYPE, Integer.TYPE, String.class, Boolean.TYPE});
                    gCameraMethodType = 3;
                } catch (NoSuchMethodException unused3) {
                }
            }
            if (gCameraNativeSetup == null) {
                try {
                    gCameraNativeSetup = Camera.class.getDeclaredMethod("native_setup", new Class[]{Object.class, Integer.TYPE, String.class, Boolean.TYPE});
                    gCameraMethodType = 4;
                } catch (NoSuchMethodException unused4) {
                }
            }
            if (gCameraNativeSetup != null) {
                gCameraNativeSetup.setAccessible(true);
            }
            for (Method method2 : AudioRecord.class.getDeclaredMethods()) {
                if (method2.getName().equals("native_check_permission") && method2.getParameterTypes().length == 1 && method2.getParameterTypes()[0] == String.class) {
                    gAudioRecordNativeCheckPermission = method2;
                    method2.setAccessible(true);
                    return;
                }
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unable to find method : ");
        sb.append(decode);
        throw new RuntimeException(sb.toString());
    }
}
