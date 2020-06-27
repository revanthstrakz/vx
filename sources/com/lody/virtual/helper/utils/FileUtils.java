package com.lody.virtual.helper.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.system.Os;
import android.text.TextUtils;
import com.android.launcher3.IconCache;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import p013io.virtualapp.utils.HanziToPinyin.Token;

public class FileUtils {

    public static class FileLock {
        private static FileLock singleton;
        private Map<String, FileLockCount> mRefCountMap = new ConcurrentHashMap();

        private class FileLockCount {
            FileChannel fChannel;
            RandomAccessFile fOs;
            java.nio.channels.FileLock mFileLock;
            int mRefCount;

            FileLockCount(java.nio.channels.FileLock fileLock, int i, RandomAccessFile randomAccessFile, FileChannel fileChannel) {
                this.mFileLock = fileLock;
                this.mRefCount = i;
                this.fOs = randomAccessFile;
                this.fChannel = fileChannel;
            }
        }

        public static FileLock getInstance() {
            if (singleton == null) {
                singleton = new FileLock();
            }
            return singleton;
        }

        private int RefCntInc(String str, java.nio.channels.FileLock fileLock, RandomAccessFile randomAccessFile, FileChannel fileChannel) {
            if (this.mRefCountMap.containsKey(str)) {
                FileLockCount fileLockCount = (FileLockCount) this.mRefCountMap.get(str);
                int i = fileLockCount.mRefCount;
                fileLockCount.mRefCount = i + 1;
                return i;
            }
            Map<String, FileLockCount> map = this.mRefCountMap;
            FileLockCount fileLockCount2 = new FileLockCount(fileLock, 1, randomAccessFile, fileChannel);
            map.put(str, fileLockCount2);
            return 1;
        }

        private int RefCntDec(String str) {
            if (!this.mRefCountMap.containsKey(str)) {
                return 0;
            }
            FileLockCount fileLockCount = (FileLockCount) this.mRefCountMap.get(str);
            int i = fileLockCount.mRefCount - 1;
            fileLockCount.mRefCount = i;
            if (i > 0) {
                return i;
            }
            this.mRefCountMap.remove(str);
            return i;
        }

        public boolean LockExclusive(File file) {
            if (file == null) {
                return false;
            }
            try {
                File file2 = new File(file.getParentFile().getAbsolutePath().concat("/lock"));
                if (!file2.exists()) {
                    file2.createNewFile();
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file2.getAbsolutePath(), "rw");
                FileChannel channel = randomAccessFile.getChannel();
                java.nio.channels.FileLock lock = channel.lock();
                if (!lock.isValid()) {
                    return false;
                }
                RefCntInc(file2.getAbsolutePath(), lock, randomAccessFile, channel);
                return true;
            } catch (Exception unused) {
                return false;
            }
        }

        public void unLock(File file) {
            File file2 = new File(file.getParentFile().getAbsolutePath().concat("/lock"));
            if (file2.exists() && this.mRefCountMap.containsKey(file2.getAbsolutePath())) {
                FileLockCount fileLockCount = (FileLockCount) this.mRefCountMap.get(file2.getAbsolutePath());
                if (fileLockCount != null) {
                    java.nio.channels.FileLock fileLock = fileLockCount.mFileLock;
                    RandomAccessFile randomAccessFile = fileLockCount.fOs;
                    FileChannel fileChannel = fileLockCount.fChannel;
                    try {
                        if (RefCntDec(file2.getAbsolutePath()) <= 0) {
                            if (fileLock != null && fileLock.isValid()) {
                                fileLock.release();
                            }
                            if (randomAccessFile != null) {
                                randomAccessFile.close();
                            }
                            if (fileChannel != null) {
                                fileChannel.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public interface FileMode {
        public static final int MODE_755 = 493;
        public static final int MODE_IRGRP = 32;
        public static final int MODE_IROTH = 4;
        public static final int MODE_IRUSR = 256;
        public static final int MODE_ISGID = 1024;
        public static final int MODE_ISUID = 2048;
        public static final int MODE_ISVTX = 512;
        public static final int MODE_IWGRP = 16;
        public static final int MODE_IWOTH = 2;
        public static final int MODE_IWUSR = 128;
        public static final int MODE_IXGRP = 8;
        public static final int MODE_IXOTH = 1;
        public static final int MODE_IXUSR = 64;
    }

    private static boolean isValidExtFilenameChar(char c) {
        return (c == 0 || c == '/') ? false : true;
    }

    public static void chmod(String str, int i) throws Exception {
        if (VERSION.SDK_INT >= 21) {
            try {
                Os.chmod(str, i);
                return;
            } catch (Exception unused) {
            }
        }
        String str2 = "chmod ";
        if (new File(str).isDirectory()) {
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(" -R ");
            str2 = sb.toString();
        }
        String format = String.format("%o", new Object[]{Integer.valueOf(i)});
        Runtime runtime = Runtime.getRuntime();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str2);
        sb2.append(format);
        sb2.append(Token.SEPARATOR);
        sb2.append(str);
        runtime.exec(sb2.toString()).waitFor();
    }

    public static void createSymlink(String str, String str2) throws Exception {
        if (VERSION.SDK_INT >= 21) {
            Os.symlink(str, str2);
            return;
        }
        Runtime runtime = Runtime.getRuntime();
        StringBuilder sb = new StringBuilder();
        sb.append("ln -s ");
        sb.append(str);
        sb.append(Token.SEPARATOR);
        sb.append(str2);
        runtime.exec(sb.toString()).waitFor();
    }

    public static boolean isSymlink(File file) throws IOException {
        if (file != null) {
            if (file.getParent() != null) {
                file = new File(file.getParentFile().getCanonicalFile(), file.getName());
            }
            return !file.getCanonicalFile().equals(file.getAbsoluteFile());
        }
        throw new NullPointerException("File must not be null");
    }

    public static void writeParcelToFile(Parcel parcel, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(parcel.marshall());
        fileOutputStream.close();
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[100];
        while (true) {
            int read = inputStream.read(bArr, 0, 100);
            if (read <= 0) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    public static boolean deleteDir(File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            boolean z = true;
            for (String file2 : file.list()) {
                if (!deleteDir(new File(file, file2))) {
                    z = false;
                }
            }
            if (z) {
                return file.delete();
            }
        }
        return file.delete();
    }

    public static boolean deleteDir(File file, Set<File> set) {
        boolean z = false;
        if (file.isDirectory()) {
            for (String file2 : file.list()) {
                if (!deleteDir(new File(file, file2), set)) {
                    return false;
                }
            }
        }
        if ((set != null && set.contains(file)) || file.delete()) {
            z = true;
        }
        return z;
    }

    public static boolean deleteDir(String str) {
        return deleteDir(new File(str));
    }

    public static void writeToFile(InputStream inputStream, File file) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr, 0, 1024);
            if (read != -1) {
                bufferedOutputStream.write(bArr, 0, read);
            } else {
                bufferedOutputStream.close();
                return;
            }
        }
    }

    public static String getFileFromUri(Context context, Uri uri) {
        FileOutputStream fileOutputStream;
        InputStream inputStream;
        IOException e;
        String str = null;
        if (uri == null) {
            return null;
        }
        if ("file".equals(uri.getScheme())) {
            str = uri.getPath();
        } else if ("content".equals(uri.getScheme())) {
            File file = new File(context.getCacheDir(), uri.getLastPathSegment());
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (IOException e2) {
                    e = e2;
                    fileOutputStream = null;
                    try {
                        e.printStackTrace();
                        closeQuietly(inputStream);
                        closeQuietly(fileOutputStream);
                        str = file.getPath();
                        return str;
                    } catch (Throwable th) {
                        th = th;
                        closeQuietly(inputStream);
                        closeQuietly(fileOutputStream);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    fileOutputStream = null;
                    closeQuietly(inputStream);
                    closeQuietly(fileOutputStream);
                    throw th;
                }
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int read = inputStream.read(bArr);
                        if (read <= 0) {
                            break;
                        }
                        fileOutputStream.write(bArr, 0, read);
                    }
                    fileOutputStream.flush();
                } catch (IOException e3) {
                    e = e3;
                    e.printStackTrace();
                    closeQuietly(inputStream);
                    closeQuietly(fileOutputStream);
                    str = file.getPath();
                    return str;
                }
            } catch (IOException e4) {
                fileOutputStream = null;
                e = e4;
                inputStream = null;
                e.printStackTrace();
                closeQuietly(inputStream);
                closeQuietly(fileOutputStream);
                str = file.getPath();
                return str;
            } catch (Throwable th3) {
                fileOutputStream = null;
                th = th3;
                inputStream = null;
                closeQuietly(inputStream);
                closeQuietly(fileOutputStream);
                throw th;
            }
            closeQuietly(inputStream);
            closeQuietly(fileOutputStream);
            str = file.getPath();
        }
        return str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x003d  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0042  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void writeToFile(byte[] r9, java.io.File r10) throws java.io.IOException {
        /*
            r0 = 0
            java.io.ByteArrayInputStream r1 = new java.io.ByteArrayInputStream     // Catch:{ all -> 0x0033 }
            r1.<init>(r9)     // Catch:{ all -> 0x0033 }
            java.nio.channels.ReadableByteChannel r1 = java.nio.channels.Channels.newChannel(r1)     // Catch:{ all -> 0x0033 }
            java.io.FileOutputStream r8 = new java.io.FileOutputStream     // Catch:{ all -> 0x0030 }
            r8.<init>(r10)     // Catch:{ all -> 0x0030 }
            java.nio.channels.FileChannel r10 = r8.getChannel()     // Catch:{ all -> 0x002c }
            r4 = 0
            int r9 = r9.length     // Catch:{ all -> 0x002a }
            long r6 = (long) r9     // Catch:{ all -> 0x002a }
            r2 = r10
            r3 = r1
            r2.transferFrom(r3, r4, r6)     // Catch:{ all -> 0x002a }
            r8.close()
            if (r1 == 0) goto L_0x0024
            r1.close()
        L_0x0024:
            if (r10 == 0) goto L_0x0029
            r10.close()
        L_0x0029:
            return
        L_0x002a:
            r9 = move-exception
            goto L_0x002e
        L_0x002c:
            r9 = move-exception
            r10 = r0
        L_0x002e:
            r0 = r8
            goto L_0x0036
        L_0x0030:
            r9 = move-exception
            r10 = r0
            goto L_0x0036
        L_0x0033:
            r9 = move-exception
            r10 = r0
            r1 = r10
        L_0x0036:
            if (r0 == 0) goto L_0x003b
            r0.close()
        L_0x003b:
            if (r1 == 0) goto L_0x0040
            r1.close()
        L_0x0040:
            if (r10 == 0) goto L_0x0045
            r10.close()
        L_0x0045:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.helper.utils.FileUtils.writeToFile(byte[], java.io.File):void");
    }

    public static void copyFile(File file, File file2) throws IOException {
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                fileOutputStream = new FileOutputStream(file2);
                try {
                    FileChannel channel = fileInputStream.getChannel();
                    FileChannel channel2 = fileOutputStream.getChannel();
                    ByteBuffer allocate = ByteBuffer.allocate(1024);
                    while (true) {
                        allocate.clear();
                        if (channel.read(allocate) == -1) {
                            closeQuietly(fileInputStream);
                            closeQuietly(fileOutputStream);
                            return;
                        }
                        allocate.limit(allocate.position());
                        allocate.position(0);
                        channel2.write(allocate);
                    }
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream = null;
                closeQuietly(fileInputStream);
                closeQuietly(fileOutputStream);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = null;
            fileInputStream = null;
            closeQuietly(fileInputStream);
            closeQuietly(fileOutputStream);
            throw th;
        }
    }

    public static void copyFile(String str, String str2) throws IOException {
        File file = new File(str);
        if (file.exists()) {
            if (file.isFile()) {
                copyFile(file, new File(str2));
            } else {
                copyDir(str, str2);
            }
        }
    }

    public static void copyDir(String str, String str2) throws IOException {
        String[] list;
        File file = new File(str);
        if (file.exists()) {
            File file2 = new File(str2);
            if (file2.exists() || file2.mkdirs()) {
                for (String str3 : file.list()) {
                    File file3 = new File(str, str3);
                    if (file3.isDirectory()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append(File.separator);
                        sb.append(str3);
                        String sb2 = sb.toString();
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(str2);
                        sb3.append(File.separator);
                        sb3.append(str3);
                        copyDir(sb2, sb3.toString());
                    } else {
                        copyFile(file3, new File(str2, str3));
                    }
                }
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
            }
        }
    }

    public static int peekInt(byte[] bArr, int i, ByteOrder byteOrder) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            int i2 = i + 1;
            int i3 = i2 + 1;
            return (bArr[i3 + 1] & 255) | ((bArr[i] & 255) << 24) | ((bArr[i2] & 255) << 16) | ((bArr[i3] & 255) << 8);
        }
        int i4 = i + 1;
        int i5 = i4 + 1;
        return ((bArr[i5 + 1] & 255) << 24) | (bArr[i] & 255) | ((bArr[i4] & 255) << 8) | ((bArr[i5] & 255) << 16);
    }

    public static boolean isValidExtFilename(String str) {
        return str != null && str.equals(buildValidExtFilename(str));
    }

    public static String buildValidExtFilename(String str) {
        if (TextUtils.isEmpty(str) || IconCache.EMPTY_CLASS_NAME.equals(str) || "..".equals(str)) {
            return "(invalid)";
        }
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (isValidExtFilenameChar(charAt)) {
                sb.append(charAt);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }
}
