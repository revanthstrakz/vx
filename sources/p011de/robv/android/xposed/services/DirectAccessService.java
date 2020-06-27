package p011de.robv.android.xposed.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/* renamed from: de.robv.android.xposed.services.DirectAccessService */
public final class DirectAccessService extends BaseService {
    public boolean hasDirectFileAccess() {
        return true;
    }

    public boolean checkFileAccess(String str, int i) {
        File file = new File(str);
        if (i == 0 && !file.exists()) {
            return false;
        }
        if ((i & 4) != 0 && !file.canRead()) {
            return false;
        }
        if ((i & 2) != 0 && !file.canWrite()) {
            return false;
        }
        if ((i & 1) == 0 || file.canExecute()) {
            return true;
        }
        return false;
    }

    public boolean checkFileExists(String str) {
        return new File(str).exists();
    }

    public FileResult statFile(String str) throws IOException {
        File file = new File(str);
        return new FileResult(file.length(), file.lastModified());
    }

    public byte[] readFile(String str) throws IOException {
        File file = new File(str);
        byte[] bArr = new byte[((int) file.length())];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bArr);
        fileInputStream.close();
        return bArr;
    }

    public FileResult readFile(String str, long j, long j2) throws IOException {
        File file = new File(str);
        long length = file.length();
        long lastModified = file.lastModified();
        if (j == length && j2 == lastModified) {
            return new FileResult(length, lastModified);
        }
        FileResult fileResult = new FileResult(readFile(str), length, lastModified);
        return fileResult;
    }

    public FileResult readFile(String str, int i, int i2, long j, long j2) throws IOException {
        File file = new File(str);
        long length = file.length();
        long lastModified = file.lastModified();
        if (j == length && j2 == lastModified) {
            return new FileResult(length, lastModified);
        }
        if (i <= 0 && i2 <= 0) {
            FileResult fileResult = new FileResult(readFile(str), length, lastModified);
            return fileResult;
        } else if (i <= 0 || ((long) i) < length) {
            if (i < 0) {
                i = 0;
            }
            if (i2 <= 0 || ((long) (i + i2)) <= length) {
                if (i2 <= 0) {
                    i2 = (int) (length - ((long) i));
                }
                byte[] bArr = new byte[i2];
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.skip((long) i);
                fileInputStream.read(bArr);
                fileInputStream.close();
                FileResult fileResult2 = new FileResult(bArr, length, lastModified);
                return fileResult2;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Length ");
            sb.append(i2);
            sb.append(" is out of range for ");
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Offset ");
            sb2.append(i);
            sb2.append(" is out of range for ");
            sb2.append(str);
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public InputStream getFileInputStream(String str) throws IOException {
        return new BufferedInputStream(new FileInputStream(str), 16384);
    }

    public FileResult getFileInputStream(String str, long j, long j2) throws IOException {
        File file = new File(str);
        long length = file.length();
        long lastModified = file.lastModified();
        if (j == length && j2 == lastModified) {
            return new FileResult(length, lastModified);
        }
        FileResult fileResult = new FileResult((InputStream) new BufferedInputStream(new FileInputStream(str), 16384), length, lastModified);
        return fileResult;
    }
}
