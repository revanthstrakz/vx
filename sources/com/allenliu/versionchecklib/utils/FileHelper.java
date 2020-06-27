package com.allenliu.versionchecklib.utils;

import android.os.Environment;
import java.io.File;

public class FileHelper {
    public static String getDownloadApkCachePath() {
        String str;
        if (checkSDCard()) {
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory());
            sb.append("/AllenVersionPath/");
            str = sb.toString();
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(Environment.getDataDirectory().getPath());
            sb2.append("/AllenVersionPath/");
            str = sb2.toString();
        }
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return str;
    }

    public static boolean checkSDCard() {
        return Environment.getExternalStorageState().equals("mounted");
    }
}
