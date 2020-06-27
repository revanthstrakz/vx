package p011de.robv.android.xposed.services;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/* renamed from: de.robv.android.xposed.services.BaseService */
public abstract class BaseService {
    public static final int F_OK = 0;
    public static final int R_OK = 4;
    public static final int W_OK = 2;
    public static final int X_OK = 1;

    public abstract boolean checkFileAccess(String str, int i);

    public boolean hasDirectFileAccess() {
        return false;
    }

    public abstract FileResult readFile(String str, int i, int i2, long j, long j2) throws IOException;

    public abstract FileResult readFile(String str, long j, long j2) throws IOException;

    public abstract byte[] readFile(String str) throws IOException;

    public abstract FileResult statFile(String str) throws IOException;

    public boolean checkFileExists(String str) {
        return checkFileAccess(str, 0);
    }

    public long getFileSize(String str) throws IOException {
        return statFile(str).size;
    }

    public long getFileModificationTime(String str) throws IOException {
        return statFile(str).mtime;
    }

    public InputStream getFileInputStream(String str) throws IOException {
        return new ByteArrayInputStream(readFile(str));
    }

    public FileResult getFileInputStream(String str, long j, long j2) throws IOException {
        FileResult readFile = readFile(str, j, j2);
        if (readFile.content == null) {
            return readFile;
        }
        FileResult fileResult = new FileResult((InputStream) new ByteArrayInputStream(readFile.content), readFile.size, readFile.mtime);
        return fileResult;
    }

    BaseService() {
    }

    static void ensureAbsolutePath(String str) {
        if (!str.startsWith("/")) {
            StringBuilder sb = new StringBuilder();
            sb.append("Only absolute filenames are allowed: ");
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    static void throwCommonIOException(int i, String str, String str2, String str3) throws IOException {
        switch (i) {
            case 1:
            case 13:
                if (str == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Permission denied: ");
                    sb.append(str2);
                    str = sb.toString();
                }
                throw new FileNotFoundException(str);
            case 2:
                if (str == null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("No such file or directory: ");
                    sb2.append(str2);
                    str = sb2.toString();
                }
                throw new FileNotFoundException(str);
            case 12:
                throw new OutOfMemoryError(str);
            case 21:
                if (str == null) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Is a directory: ");
                    sb3.append(str2);
                    str = sb3.toString();
                }
                throw new FileNotFoundException(str);
            default:
                if (str == null) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Error ");
                    sb4.append(i);
                    sb4.append(str3);
                    sb4.append(str2);
                    str = sb4.toString();
                }
                throw new IOException(str);
        }
    }
}
