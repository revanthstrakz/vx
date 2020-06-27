package com.microsoft.appcenter.utils.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public class FileManager {
    @SuppressLint({"StaticFieldLeak"})
    private static Context sContext;

    public static synchronized void initialize(Context context) {
        synchronized (FileManager.class) {
            if (sContext == null) {
                sContext = context;
            }
        }
    }

    public static String read(@NonNull String str) {
        return read(new File(str));
    }

    public static String read(@NonNull File file) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String property = System.getProperty("line.separator");
            StringBuilder sb = new StringBuilder();
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                sb.append(readLine);
                while (true) {
                    String readLine2 = bufferedReader.readLine();
                    if (readLine2 != null) {
                        sb.append(property);
                        sb.append(readLine2);
                    }
                }
                bufferedReader.close();
                return sb.toString();
            }
            bufferedReader.close();
            return sb.toString();
        } catch (IOException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Could not read file ");
            sb2.append(file.getAbsolutePath());
            AppCenterLog.error("AppCenter", sb2.toString(), e);
            return null;
        } catch (Throwable th) {
            bufferedReader.close();
            throw th;
        }
    }

    public static byte[] readBytes(@NonNull File file) {
        FileInputStream fileInputStream;
        byte[] bArr = new byte[((int) file.length())];
        try {
            fileInputStream = new FileInputStream(file);
            new DataInputStream(fileInputStream).readFully(bArr);
            fileInputStream.close();
            return bArr;
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Could not read file ");
            sb.append(file.getAbsolutePath());
            AppCenterLog.error("AppCenter", sb.toString(), e);
            return null;
        } catch (Throwable th) {
            fileInputStream.close();
            throw th;
        }
    }

    public static void write(@NonNull String str, @NonNull String str2) throws IOException {
        write(new File(str), str2);
    }

    public static void write(@NonNull File file, @NonNull String str) throws IOException {
        if (!TextUtils.isEmpty(str) && TextUtils.getTrimmedLength(str) > 0) {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            try {
                bufferedWriter.write(str);
            } finally {
                bufferedWriter.close();
            }
        }
    }

    @NonNull
    public static String[] getFilenames(@NonNull String str, @Nullable FilenameFilter filenameFilter) {
        File file = new File(str);
        if (file.exists()) {
            return file.list(filenameFilter);
        }
        return new String[0];
    }

    @Nullable
    public static File lastModifiedFile(@NonNull String str, @Nullable FilenameFilter filenameFilter) {
        return lastModifiedFile(new File(str), filenameFilter);
    }

    @Nullable
    public static File lastModifiedFile(@NonNull File file, @Nullable FilenameFilter filenameFilter) {
        File file2 = null;
        if (file.exists()) {
            File[] listFiles = file.listFiles(filenameFilter);
            long j = 0;
            if (listFiles != null) {
                for (File file3 : listFiles) {
                    if (file3.lastModified() > j) {
                        j = file3.lastModified();
                        file2 = file3;
                    }
                }
                return file2;
            }
        }
        return null;
    }

    public static boolean delete(@NonNull String str) {
        return delete(new File(str));
    }

    public static boolean delete(@NonNull File file) {
        return file.delete();
    }

    public static boolean deleteDir(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File deleteDir : listFiles) {
                deleteDir(deleteDir);
            }
        }
        return file.delete();
    }

    public static void mkdir(@NonNull String str) {
        new File(str).mkdirs();
    }
}
