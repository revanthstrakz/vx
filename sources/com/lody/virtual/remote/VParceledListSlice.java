package com.lody.virtual.remote;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class VParceledListSlice<T extends Parcelable> implements Parcelable {
    public static final ClassLoaderCreator<VParceledListSlice> CREATOR = new ClassLoaderCreator<VParceledListSlice>() {
        public VParceledListSlice createFromParcel(Parcel parcel) {
            return new VParceledListSlice(parcel, null);
        }

        public VParceledListSlice createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new VParceledListSlice(parcel, classLoader);
        }

        public VParceledListSlice[] newArray(int i) {
            return new VParceledListSlice[i];
        }
    };
    /* access modifiers changed from: private */
    public static boolean DEBUG = false;
    private static final int MAX_FIRST_IPC_SIZE = 131072;
    private static final int MAX_IPC_SIZE = 262144;
    /* access modifiers changed from: private */
    public static String TAG = "ParceledListSlice";
    /* access modifiers changed from: private */
    public final List<T> mList;

    public VParceledListSlice(List<T> list) {
        this.mList = list;
    }

    private VParceledListSlice(Parcel parcel, ClassLoader classLoader) {
        int readInt = parcel.readInt();
        this.mList = new ArrayList(readInt);
        if (DEBUG) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Retrieving ");
            sb.append(readInt);
            sb.append(" items");
            Log.d(str, sb.toString());
        }
        if (readInt > 0) {
            Class cls = null;
            int i = 0;
            while (i < readInt && parcel.readInt() != 0) {
                Parcelable readParcelable = parcel.readParcelable(classLoader);
                if (cls == null) {
                    cls = readParcelable.getClass();
                } else {
                    verifySameType(cls, readParcelable.getClass());
                }
                this.mList.add(readParcelable);
                if (DEBUG) {
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Read inline #");
                    sb2.append(i);
                    sb2.append(": ");
                    sb2.append(this.mList.get(this.mList.size() - 1));
                    Log.d(str2, sb2.toString());
                }
                i++;
            }
            if (i < readInt) {
                IBinder readStrongBinder = parcel.readStrongBinder();
                while (i < readInt) {
                    if (DEBUG) {
                        String str3 = TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("Reading more @");
                        sb3.append(i);
                        sb3.append(" of ");
                        sb3.append(readInt);
                        sb3.append(": retriever=");
                        sb3.append(readStrongBinder);
                        Log.d(str3, sb3.toString());
                    }
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInt(i);
                    try {
                        readStrongBinder.transact(1, obtain, obtain2, 0);
                        while (i < readInt && obtain2.readInt() != 0) {
                            Parcelable readParcelable2 = obtain2.readParcelable(classLoader);
                            verifySameType(cls, readParcelable2.getClass());
                            this.mList.add(readParcelable2);
                            if (DEBUG) {
                                String str4 = TAG;
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("Read extra #");
                                sb4.append(i);
                                sb4.append(": ");
                                sb4.append(this.mList.get(this.mList.size() - 1));
                                Log.d(str4, sb4.toString());
                            }
                            i++;
                        }
                        obtain2.recycle();
                        obtain.recycle();
                    } catch (RemoteException e) {
                        String str5 = TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("Failure retrieving array; only received ");
                        sb5.append(i);
                        sb5.append(" of ");
                        sb5.append(readInt);
                        Log.w(str5, sb5.toString(), e);
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static void verifySameType(Class<?> cls, Class<?> cls2) {
        if (!cls2.equals(cls)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Can't unparcel type ");
            sb.append(cls2.getName());
            sb.append(" in list of type ");
            sb.append(cls.getName());
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public List<T> getList() {
        return this.mList;
    }

    public int describeContents() {
        int i = 0;
        for (int i2 = 0; i2 < this.mList.size(); i2++) {
            i |= ((Parcelable) this.mList.get(i2)).describeContents();
        }
        return i;
    }

    public void writeToParcel(Parcel parcel, final int i) {
        final int size = this.mList.size();
        parcel.writeInt(size);
        if (DEBUG) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Writing ");
            sb.append(size);
            sb.append(" items");
            Log.d(str, sb.toString());
        }
        if (size > 0) {
            final Class cls = ((Parcelable) this.mList.get(0)).getClass();
            int i2 = 0;
            while (i2 < size && parcel.dataSize() < 131072) {
                parcel.writeInt(1);
                Parcelable parcelable = (Parcelable) this.mList.get(i2);
                verifySameType(cls, parcelable.getClass());
                parcel.writeParcelable(parcelable, i);
                if (DEBUG) {
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Wrote inline #");
                    sb2.append(i2);
                    sb2.append(": ");
                    sb2.append(this.mList.get(i2));
                    Log.d(str2, sb2.toString());
                }
                i2++;
            }
            if (i2 < size) {
                parcel.writeInt(0);
                C10892 r2 = new Binder() {
                    /* access modifiers changed from: protected */
                    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
                        if (i != 1) {
                            return super.onTransact(i, parcel, parcel2, i2);
                        }
                        int readInt = parcel.readInt();
                        if (VParceledListSlice.DEBUG) {
                            String access$200 = VParceledListSlice.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Writing more @");
                            sb.append(readInt);
                            sb.append(" of ");
                            sb.append(size);
                            Log.d(access$200, sb.toString());
                        }
                        while (readInt < size && parcel2.dataSize() < 262144) {
                            parcel2.writeInt(1);
                            Parcelable parcelable = (Parcelable) VParceledListSlice.this.mList.get(readInt);
                            VParceledListSlice.verifySameType(cls, parcelable.getClass());
                            parcel2.writeParcelable(parcelable, i);
                            if (VParceledListSlice.DEBUG) {
                                String access$2002 = VParceledListSlice.TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("Wrote extra #");
                                sb2.append(readInt);
                                sb2.append(": ");
                                sb2.append(VParceledListSlice.this.mList.get(readInt));
                                Log.d(access$2002, sb2.toString());
                            }
                            readInt++;
                        }
                        if (readInt < size) {
                            if (VParceledListSlice.DEBUG) {
                                String access$2003 = VParceledListSlice.TAG;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("Breaking @");
                                sb3.append(readInt);
                                sb3.append(" of ");
                                sb3.append(size);
                                Log.d(access$2003, sb3.toString());
                            }
                            parcel2.writeInt(0);
                        }
                        return true;
                    }
                };
                if (DEBUG) {
                    String str3 = TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Breaking @");
                    sb3.append(i2);
                    sb3.append(" of ");
                    sb3.append(size);
                    sb3.append(": retriever=");
                    sb3.append(r2);
                    Log.d(str3, sb3.toString());
                }
                parcel.writeStrongBinder(r2);
            }
        }
    }
}
