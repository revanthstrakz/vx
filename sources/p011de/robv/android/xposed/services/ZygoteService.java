package p011de.robv.android.xposed.services;

import java.io.IOException;
import java.util.Arrays;

/* renamed from: de.robv.android.xposed.services.ZygoteService */
public final class ZygoteService extends BaseService {
    public native boolean checkFileAccess(String str, int i);

    public native byte[] readFile(String str) throws IOException;

    public native FileResult statFile(String str) throws IOException;

    public FileResult readFile(String str, long j, long j2) throws IOException {
        FileResult statFile = statFile(str);
        if (j == statFile.size && j2 == statFile.mtime) {
            return statFile;
        }
        FileResult fileResult = new FileResult(readFile(str), statFile.size, statFile.mtime);
        return fileResult;
    }

    public FileResult readFile(String str, int i, int i2, long j, long j2) throws IOException {
        FileResult statFile = statFile(str);
        if (j == statFile.size && j2 == statFile.mtime) {
            return statFile;
        }
        if (i <= 0 && i2 <= 0) {
            FileResult fileResult = new FileResult(readFile(str), statFile.size, statFile.mtime);
            return fileResult;
        } else if (i <= 0 || ((long) i) < statFile.size) {
            if (i < 0) {
                i = 0;
            }
            if (i2 <= 0 || ((long) (i + i2)) <= statFile.size) {
                if (i2 <= 0) {
                    i2 = (int) (statFile.size - ((long) i));
                }
                FileResult fileResult2 = new FileResult(Arrays.copyOfRange(readFile(str), i, i2 + i), statFile.size, statFile.mtime);
                return fileResult2;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("offset ");
            sb.append(i);
            sb.append(" + length ");
            sb.append(i2);
            sb.append(" > size ");
            sb.append(statFile.size);
            sb.append(" for ");
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("offset ");
            sb2.append(i);
            sb2.append(" >= size ");
            sb2.append(statFile.size);
            sb2.append(" for ");
            sb2.append(str);
            throw new IllegalArgumentException(sb2.toString());
        }
    }
}
