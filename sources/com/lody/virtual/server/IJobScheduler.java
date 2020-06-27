package com.lody.virtual.server;

import android.app.job.JobInfo;
import android.app.job.JobWorkItem;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface IJobScheduler extends IInterface {

    public static abstract class Stub extends Binder implements IJobScheduler {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IJobScheduler";
        static final int TRANSACTION_cancel = 2;
        static final int TRANSACTION_cancelAll = 3;
        static final int TRANSACTION_enqueue = 5;
        static final int TRANSACTION_getAllPendingJobs = 4;
        static final int TRANSACTION_getPendingJob = 6;
        static final int TRANSACTION_schedule = 1;

        private static class Proxy implements IJobScheduler {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public int schedule(JobInfo jobInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (jobInfo != null) {
                        obtain.writeInt(1);
                        jobInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void cancel(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void cancelAll() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<JobInfo> getAllPendingJobs() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(JobInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int enqueue(JobInfo jobInfo, JobWorkItem jobWorkItem) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (jobInfo != null) {
                        obtain.writeInt(1);
                        jobInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (jobWorkItem != null) {
                        obtain.writeInt(1);
                        jobWorkItem.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public JobInfo getPendingJob(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (JobInfo) JobInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IJobScheduler asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IJobScheduler)) {
                return new Proxy(iBinder);
            }
            return (IJobScheduler) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r0v1 */
        /* JADX WARNING: type inference failed for: r0v2, types: [android.app.job.JobInfo] */
        /* JADX WARNING: type inference failed for: r0v4, types: [android.app.job.JobInfo] */
        /* JADX WARNING: type inference failed for: r0v5, types: [android.app.job.JobWorkItem] */
        /* JADX WARNING: type inference failed for: r0v7, types: [android.app.job.JobWorkItem] */
        /* JADX WARNING: type inference failed for: r0v8 */
        /* JADX WARNING: type inference failed for: r0v9 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r0v1
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], android.app.job.JobWorkItem, android.app.job.JobInfo]
          uses: [android.app.job.JobInfo, android.app.job.JobWorkItem]
          mth insns count: 69
        	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$0(DepthTraversal.java:13)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
        	at jadx.core.ProcessClass.process(ProcessClass.java:30)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
        	at jadx.core.ProcessClass.process(ProcessClass.java:35)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
         */
        /* JADX WARNING: Unknown variable types count: 3 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r4, android.os.Parcel r5, android.os.Parcel r6, int r7) throws android.os.RemoteException {
            /*
                r3 = this;
                r0 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r1 = 1
                if (r4 == r0) goto L_0x00ad
                r0 = 0
                switch(r4) {
                    case 1: goto L_0x008e;
                    case 2: goto L_0x007e;
                    case 3: goto L_0x0072;
                    case 4: goto L_0x0062;
                    case 5: goto L_0x002d;
                    case 6: goto L_0x000f;
                    default: goto L_0x000a;
                }
            L_0x000a:
                boolean r4 = super.onTransact(r4, r5, r6, r7)
                return r4
            L_0x000f:
                java.lang.String r4 = "com.lody.virtual.server.IJobScheduler"
                r5.enforceInterface(r4)
                int r4 = r5.readInt()
                android.app.job.JobInfo r4 = r3.getPendingJob(r4)
                r6.writeNoException()
                if (r4 == 0) goto L_0x0028
                r6.writeInt(r1)
                r4.writeToParcel(r6, r1)
                goto L_0x002c
            L_0x0028:
                r4 = 0
                r6.writeInt(r4)
            L_0x002c:
                return r1
            L_0x002d:
                java.lang.String r4 = "com.lody.virtual.server.IJobScheduler"
                r5.enforceInterface(r4)
                int r4 = r5.readInt()
                if (r4 == 0) goto L_0x0041
                android.os.Parcelable$Creator r4 = android.app.job.JobInfo.CREATOR
                java.lang.Object r4 = r4.createFromParcel(r5)
                android.app.job.JobInfo r4 = (android.app.job.JobInfo) r4
                goto L_0x0042
            L_0x0041:
                r4 = r0
            L_0x0042:
                int r7 = r5.readInt()
                if (r7 == 0) goto L_0x0057
                int r7 = android.os.Build.VERSION.SDK_INT
                r2 = 26
                if (r7 < r2) goto L_0x0057
                android.os.Parcelable$Creator r7 = android.app.job.JobWorkItem.CREATOR
                java.lang.Object r5 = r7.createFromParcel(r5)
                r0 = r5
                android.app.job.JobWorkItem r0 = (android.app.job.JobWorkItem) r0
            L_0x0057:
                int r4 = r3.enqueue(r4, r0)
                r6.writeNoException()
                r6.writeInt(r4)
                return r1
            L_0x0062:
                java.lang.String r4 = "com.lody.virtual.server.IJobScheduler"
                r5.enforceInterface(r4)
                java.util.List r4 = r3.getAllPendingJobs()
                r6.writeNoException()
                r6.writeTypedList(r4)
                return r1
            L_0x0072:
                java.lang.String r4 = "com.lody.virtual.server.IJobScheduler"
                r5.enforceInterface(r4)
                r3.cancelAll()
                r6.writeNoException()
                return r1
            L_0x007e:
                java.lang.String r4 = "com.lody.virtual.server.IJobScheduler"
                r5.enforceInterface(r4)
                int r4 = r5.readInt()
                r3.cancel(r4)
                r6.writeNoException()
                return r1
            L_0x008e:
                java.lang.String r4 = "com.lody.virtual.server.IJobScheduler"
                r5.enforceInterface(r4)
                int r4 = r5.readInt()
                if (r4 == 0) goto L_0x00a2
                android.os.Parcelable$Creator r4 = android.app.job.JobInfo.CREATOR
                java.lang.Object r4 = r4.createFromParcel(r5)
                r0 = r4
                android.app.job.JobInfo r0 = (android.app.job.JobInfo) r0
            L_0x00a2:
                int r4 = r3.schedule(r0)
                r6.writeNoException()
                r6.writeInt(r4)
                return r1
            L_0x00ad:
                java.lang.String r4 = "com.lody.virtual.server.IJobScheduler"
                r6.writeString(r4)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.IJobScheduler.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void cancel(int i) throws RemoteException;

    void cancelAll() throws RemoteException;

    int enqueue(JobInfo jobInfo, JobWorkItem jobWorkItem) throws RemoteException;

    List<JobInfo> getAllPendingJobs() throws RemoteException;

    JobInfo getPendingJob(int i) throws RemoteException;

    int schedule(JobInfo jobInfo) throws RemoteException;
}
