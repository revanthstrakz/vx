package com.lody.virtual.server.p009pm.installer;

import android.annotation.TargetApi;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.FileUtils;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteOrder;

@TargetApi(21)
/* renamed from: com.lody.virtual.server.pm.installer.FileBridge */
public class FileBridge extends Thread {
    private static final int CMD_CLOSE = 3;
    private static final int CMD_FSYNC = 2;
    private static final int CMD_WRITE = 1;
    private static final int MSG_LENGTH = 8;
    private static final String TAG = "FileBridge";
    private final FileDescriptor mClient = new FileDescriptor();
    private volatile boolean mClosed;
    private final FileDescriptor mServer = new FileDescriptor();
    private FileDescriptor mTarget;

    public FileBridge() {
        try {
            Os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, this.mServer, this.mClient);
        } catch (ErrnoException unused) {
            throw new RuntimeException("Failed to create bridge");
        }
    }

    public boolean isClosed() {
        return this.mClosed;
    }

    public void forceClose() {
        closeQuietly(this.mTarget);
        closeQuietly(this.mServer);
        closeQuietly(this.mClient);
        this.mClosed = true;
    }

    public void setTargetFile(FileDescriptor fileDescriptor) {
        this.mTarget = fileDescriptor;
    }

    public FileDescriptor getClientSocket() {
        return this.mClient;
    }

    public void run() {
        byte[] bArr = new byte[8192];
        while (true) {
            try {
                if (read(this.mServer, bArr, 0, 8) != 8) {
                    break;
                }
                int peekInt = FileUtils.peekInt(bArr, 0, ByteOrder.BIG_ENDIAN);
                if (peekInt == 1) {
                    int peekInt2 = FileUtils.peekInt(bArr, 4, ByteOrder.BIG_ENDIAN);
                    while (peekInt2 > 0) {
                        int read = read(this.mServer, bArr, 0, Math.min(bArr.length, peekInt2));
                        if (read != -1) {
                            write(this.mTarget, bArr, 0, read);
                            peekInt2 -= read;
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Unexpected EOF; still expected ");
                            sb.append(peekInt2);
                            sb.append(" bytes");
                            throw new IOException(sb.toString());
                        }
                    }
                    continue;
                } else if (peekInt == 2) {
                    Os.fsync(this.mTarget);
                    write(this.mServer, bArr, 0, 8);
                } else if (peekInt == 3) {
                    Os.fsync(this.mTarget);
                    Os.close(this.mTarget);
                    this.mClosed = true;
                    write(this.mServer, bArr, 0, 8);
                    break;
                }
            } catch (ErrnoException | IOException e) {
                Log.wtf(TAG, "Failed during bridge", e);
            } catch (Throwable th) {
                forceClose();
                throw th;
            }
        }
        forceClose();
    }

    public static void closeQuietly(FileDescriptor fileDescriptor) {
        if (fileDescriptor != null && fileDescriptor.valid()) {
            try {
                Os.close(fileDescriptor);
            } catch (ErrnoException e) {
                e.printStackTrace();
            }
        }
    }

    public static int read(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2) throws IOException {
        ArrayUtils.checkOffsetAndCount(bArr.length, i, i2);
        if (i2 == 0) {
            return 0;
        }
        try {
            int read = Os.read(fileDescriptor, bArr, i, i2);
            if (read == 0) {
                return -1;
            }
            return read;
        } catch (ErrnoException e) {
            if (e.errno == OsConstants.EAGAIN) {
                return 0;
            }
            throw new IOException(e);
        }
    }

    public static void write(FileDescriptor fileDescriptor, byte[] bArr, int i, int i2) throws IOException {
        ArrayUtils.checkOffsetAndCount(bArr.length, i, i2);
        if (i2 != 0) {
            while (i2 > 0) {
                try {
                    int write = Os.write(fileDescriptor, bArr, i, i2);
                    i2 -= write;
                    i += write;
                } catch (ErrnoException e) {
                    throw new IOException(e);
                }
            }
        }
    }
}
