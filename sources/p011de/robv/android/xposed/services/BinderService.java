package p011de.robv.android.xposed.services;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import java.io.IOException;

/* renamed from: de.robv.android.xposed.services.BinderService */
public final class BinderService extends BaseService {
    private static final int ACCESS_FILE_TRANSACTION = 3;
    private static final String INTERFACE_TOKEN = "de.robv.android.xposed.IXposedService";
    private static final int READ_FILE_TRANSACTION = 5;
    private static final String[] SERVICE_NAMES = {"user.xposed.app", "user.xposed.system"};
    private static final int STAT_FILE_TRANSACTION = 4;
    public static final int TARGET_APP = 0;
    public static final int TARGET_SYSTEM = 1;
    private static final BinderService[] sServices = new BinderService[2];
    private final IBinder mRemote;

    public static BinderService getService(int i) {
        BinderService binderService;
        if (i < 0 || i > sServices.length) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid service target ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
        synchronized (sServices) {
            if (sServices[i] == null) {
                sServices[i] = new BinderService(i);
            }
            binderService = sServices[i];
        }
        return binderService;
    }

    public boolean checkFileAccess(String str, int i) {
        ensureAbsolutePath(str);
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        obtain.writeInterfaceToken(INTERFACE_TOKEN);
        obtain.writeString(str);
        obtain.writeInt(i);
        boolean z = false;
        try {
            this.mRemote.transact(3, obtain, obtain2, 0);
            obtain2.readException();
            int readInt = obtain2.readInt();
            obtain2.recycle();
            obtain.recycle();
            if (readInt == 0) {
                z = true;
            }
            return z;
        } catch (RemoteException unused) {
            obtain.recycle();
            obtain2.recycle();
            return false;
        }
    }

    public FileResult statFile(String str) throws IOException {
        ensureAbsolutePath(str);
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        obtain.writeInterfaceToken(INTERFACE_TOKEN);
        obtain.writeString(str);
        try {
            this.mRemote.transact(4, obtain, obtain2, 0);
            obtain2.readException();
            int readInt = obtain2.readInt();
            if (readInt != 0) {
                throwCommonIOException(readInt, null, str, " while retrieving attributes for ");
            }
            long readLong = obtain2.readLong();
            long readLong2 = obtain2.readLong();
            obtain2.recycle();
            obtain.recycle();
            return new FileResult(readLong, readLong2);
        } catch (RemoteException e) {
            obtain.recycle();
            obtain2.recycle();
            throw new IOException(e);
        }
    }

    public byte[] readFile(String str) throws IOException {
        return readFile(str, 0, 0, 0, 0).content;
    }

    public FileResult readFile(String str, long j, long j2) throws IOException {
        return readFile(str, 0, 0, j, j2);
    }

    public FileResult readFile(String str, int i, int i2, long j, long j2) throws IOException {
        ensureAbsolutePath(str);
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        obtain.writeInterfaceToken(INTERFACE_TOKEN);
        obtain.writeString(str);
        obtain.writeInt(i);
        obtain.writeInt(i2);
        obtain.writeLong(j);
        obtain.writeLong(j2);
        try {
            this.mRemote.transact(5, obtain, obtain2, 0);
            obtain2.readException();
            int readInt = obtain2.readInt();
            String readString = obtain2.readString();
            long readLong = obtain2.readLong();
            long readLong2 = obtain2.readLong();
            byte[] createByteArray = obtain2.createByteArray();
            obtain2.recycle();
            obtain.recycle();
            if (readInt == 0) {
                FileResult fileResult = new FileResult(createByteArray, readLong, readLong2);
                return fileResult;
            } else if (readInt != 22) {
                throwCommonIOException(readInt, readString, str, " while reading ");
                throw new IllegalStateException();
            } else if (readString != null) {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException(readString);
                if (i == 0 && i2 == 0) {
                    throw new IOException(illegalArgumentException);
                }
                throw illegalArgumentException;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Offset ");
                sb.append(i);
                sb.append(" / Length ");
                sb.append(i2);
                sb.append(" is out of range for ");
                sb.append(str);
                sb.append(" with size ");
                sb.append(readLong);
                throw new IllegalArgumentException(sb.toString());
            }
        } catch (RemoteException e) {
            obtain.recycle();
            obtain2.recycle();
            throw new IOException(e);
        }
    }

    private BinderService(int i) {
        IBinder service = ServiceManager.getService(SERVICE_NAMES[i]);
        if (service != null) {
            this.mRemote = service;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Service ");
        sb.append(SERVICE_NAMES[i]);
        sb.append(" does not exist");
        throw new IllegalStateException(sb.toString());
    }
}
