package com.lody.virtual.server.p009pm.installer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p000pm.IPackageInstallObserver2;
import android.content.p000pm.IPackageInstallerSession.Stub;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.VLog;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@TargetApi(21)
/* renamed from: com.lody.virtual.server.pm.installer.PackageInstallerSession */
public class PackageInstallerSession extends Stub {
    public static final int INSTALL_FAILED_ABORTED = -115;
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;
    public static final int INSTALL_FAILED_INVALID_APK = -2;
    public static final int INSTALL_SUCCEEDED = 1;
    private static final int MSG_COMMIT = 0;
    private static final String REMOVE_SPLIT_MARKER_EXTENSION = ".removed";
    private static final String TAG = "PackageInstaller";
    final String installerPackageName;
    final int installerUid;
    private final AtomicInteger mActiveCount = new AtomicInteger();
    private ArrayList<FileBridge> mBridges = new ArrayList<>();
    private final InternalCallback mCallback;
    private float mClientProgress = 0.0f;
    private final Context mContext;
    private boolean mDestroyed = false;
    private String mFinalMessage;
    private int mFinalStatus;
    private final Handler mHandler;
    private final Callback mHandlerCallback = new Callback() {
        public boolean handleMessage(Message message) {
            synchronized (PackageInstallerSession.this.mLock) {
                if (message.obj != null) {
                    PackageInstallerSession.this.mRemoteObserver = (IPackageInstallObserver2) message.obj;
                }
                try {
                    PackageInstallerSession.this.commitLocked();
                } catch (PackageManagerException e) {
                    String completeMessage = PackageInstallerSession.getCompleteMessage(e);
                    String str = PackageInstallerSession.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Commit of session ");
                    sb.append(PackageInstallerSession.this.sessionId);
                    sb.append(" failed: ");
                    sb.append(completeMessage);
                    VLog.m87e(str, sb.toString(), new Object[0]);
                    PackageInstallerSession.this.destroyInternal();
                    PackageInstallerSession.this.dispatchSessionFinished(e.error, completeMessage, null);
                }
            }
            return true;
        }
    };
    private float mInternalProgress = 0.0f;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private String mPackageName;
    private boolean mPermissionsAccepted;
    private boolean mPrepared = false;
    private float mProgress = 0.0f;
    /* access modifiers changed from: private */
    public IPackageInstallObserver2 mRemoteObserver;
    private float mReportedProgress = -1.0f;
    private File mResolvedBaseFile;
    private File mResolvedStageDir;
    private final List<File> mResolvedStagedFiles = new ArrayList();
    private boolean mSealed = false;
    final SessionParams params;
    final int sessionId;
    final File stageDir;
    final int userId;

    /* renamed from: com.lody.virtual.server.pm.installer.PackageInstallerSession$PackageManagerException */
    private class PackageManagerException extends Exception {
        public final int error;

        PackageManagerException(int i, String str) {
            super(str);
            this.error = i;
        }
    }

    private static float constrain(float f, float f2, float f3) {
        return f < f2 ? f2 : f > f3 ? f3 : f;
    }

    public PackageInstallerSession(InternalCallback internalCallback, Context context, Looper looper, String str, int i, int i2, int i3, SessionParams sessionParams, File file) {
        this.mCallback = internalCallback;
        this.mContext = context;
        this.mHandler = new Handler(looper, this.mHandlerCallback);
        this.installerPackageName = str;
        this.sessionId = i;
        this.userId = i2;
        this.installerUid = i3;
        this.mPackageName = sessionParams.appPackageName;
        this.params = sessionParams;
        this.stageDir = file;
    }

    public SessionInfo generateInfo() {
        SessionInfo sessionInfo = new SessionInfo();
        synchronized (this.mLock) {
            sessionInfo.sessionId = this.sessionId;
            sessionInfo.installerPackageName = this.installerPackageName;
            sessionInfo.resolvedBaseCodePath = this.mResolvedBaseFile != null ? this.mResolvedBaseFile.getAbsolutePath() : null;
            sessionInfo.progress = this.mProgress;
            sessionInfo.sealed = this.mSealed;
            sessionInfo.active = this.mActiveCount.get() > 0;
            sessionInfo.mode = this.params.mode;
            sessionInfo.sizeBytes = this.params.sizeBytes;
            sessionInfo.appPackageName = this.params.appPackageName;
            sessionInfo.appIcon = this.params.appIcon;
            sessionInfo.appLabel = this.params.appLabel;
        }
        return sessionInfo;
    }

    /* access modifiers changed from: private */
    public void commitLocked() throws PackageManagerException {
        if (this.mDestroyed) {
            throw new PackageManagerException(-110, "Session destroyed");
        } else if (this.mSealed) {
            try {
                resolveStageDir();
            } catch (IOException e) {
                e.printStackTrace();
            }
            validateInstallLocked();
            this.mInternalProgress = 0.5f;
            computeProgressLocked(true);
            new IPackageInstallObserver2.Stub() {
                public void onUserActionRequired(Intent intent) {
                    throw new IllegalStateException();
                }

                public void onPackageInstalled(String str, int i, String str2, Bundle bundle) {
                    PackageInstallerSession.this.destroyInternal();
                    PackageInstallerSession.this.dispatchSessionFinished(i, str2, bundle);
                }
            };
        } else {
            throw new PackageManagerException(-110, "Session not sealed");
        }
    }

    private void validateInstallLocked() throws PackageManagerException {
        this.mResolvedBaseFile = null;
        this.mResolvedStagedFiles.clear();
        File[] listFiles = this.mResolvedStageDir.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            throw new PackageManagerException(-2, "No packages staged");
        }
        for (File file : listFiles) {
            if (!file.isDirectory()) {
                File file2 = new File(this.mResolvedStageDir, "base.apk");
                if (!file.equals(file2)) {
                    file.renameTo(file2);
                }
                this.mResolvedBaseFile = file2;
                this.mResolvedStagedFiles.add(file2);
            }
        }
        if (this.mResolvedBaseFile == null) {
            throw new PackageManagerException(-2, "Full install must include a base package");
        }
    }

    public void setClientProgress(float f) throws RemoteException {
        synchronized (this.mLock) {
            boolean z = this.mClientProgress == 0.0f;
            this.mClientProgress = f;
            computeProgressLocked(z);
        }
    }

    private void computeProgressLocked(boolean z) {
        this.mProgress = constrain(this.mClientProgress * 0.8f, 0.0f, 0.8f) + constrain(this.mInternalProgress * 0.2f, 0.0f, 0.2f);
        if (z || ((double) Math.abs(this.mProgress - this.mReportedProgress)) >= 0.01d) {
            this.mReportedProgress = this.mProgress;
            this.mCallback.onSessionProgressChanged(this, this.mProgress);
        }
    }

    public void addClientProgress(float f) throws RemoteException {
        synchronized (this.mLock) {
            setClientProgress(this.mClientProgress + f);
        }
    }

    public String[] getNames() throws RemoteException {
        assertPreparedAndNotSealed("getNames");
        try {
            return resolveStageDir().list();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private File resolveStageDir() throws IOException {
        File file;
        synchronized (this.mLock) {
            if (this.mResolvedStageDir == null && this.stageDir != null) {
                this.mResolvedStageDir = this.stageDir;
                if (!this.stageDir.exists()) {
                    this.stageDir.mkdirs();
                }
            }
            file = this.mResolvedStageDir;
        }
        return file;
    }

    public ParcelFileDescriptor openWrite(String str, long j, long j2) throws RemoteException {
        try {
            return openWriteInternal(str, j, j2);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void assertPreparedAndNotSealed(String str) {
        synchronized (this.mLock) {
            if (!this.mPrepared) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(" before prepared");
                throw new IllegalStateException(sb.toString());
            } else if (this.mSealed) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(" not allowed after commit");
                throw new SecurityException(sb2.toString());
            }
        }
    }

    private ParcelFileDescriptor openWriteInternal(String str, long j, long j2) throws IOException {
        FileBridge fileBridge;
        synchronized (this.mLock) {
            assertPreparedAndNotSealed("openWrite");
            fileBridge = new FileBridge();
            this.mBridges.add(fileBridge);
        }
        try {
            FileDescriptor open = Os.open(new File(resolveStageDir(), str).getAbsolutePath(), OsConstants.O_CREAT | OsConstants.O_WRONLY, 420);
            if (j2 > 0) {
                Os.posix_fallocate(open, 0, j2);
            }
            if (j > 0) {
                Os.lseek(open, j, OsConstants.SEEK_SET);
            }
            fileBridge.setTargetFile(open);
            fileBridge.start();
            return ParcelFileDescriptor.dup(fileBridge.getClientSocket());
        } catch (ErrnoException e) {
            throw new IOException(e);
        }
    }

    public ParcelFileDescriptor openRead(String str) throws RemoteException {
        try {
            return openReadInternal(str);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private ParcelFileDescriptor openReadInternal(String str) throws IOException {
        assertPreparedAndNotSealed("openRead");
        try {
            if (FileUtils.isValidExtFilename(str)) {
                return ParcelFileDescriptor.dup(Os.open(new File(resolveStageDir(), str).getAbsolutePath(), OsConstants.O_RDONLY, 0));
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid name: ");
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        } catch (ErrnoException e) {
            throw new IOException(e);
        }
    }

    public void removeSplit(String str) throws RemoteException {
        if (!TextUtils.isEmpty(this.params.appPackageName)) {
            try {
                createRemoveSplitMarker(str);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException("Must specify package name to remove a split");
        }
    }

    private void createRemoveSplitMarker(String str) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(REMOVE_SPLIT_MARKER_EXTENSION);
            String sb2 = sb.toString();
            if (FileUtils.isValidExtFilename(sb2)) {
                File file = new File(resolveStageDir(), sb2);
                file.createNewFile();
                Os.chmod(file.getAbsolutePath(), 0);
                return;
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Invalid marker: ");
            sb3.append(sb2);
            throw new IllegalArgumentException(sb3.toString());
        } catch (ErrnoException e) {
            throw new IOException(e);
        }
    }

    public void close() throws RemoteException {
        if (this.mActiveCount.decrementAndGet() == 0) {
            this.mCallback.onSessionActiveChanged(this, false);
        }
    }

    public void commit(IntentSender intentSender) throws RemoteException {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSealed;
            if (!this.mSealed) {
                Iterator it = this.mBridges.iterator();
                while (it.hasNext()) {
                    if (!((FileBridge) it.next()).isClosed()) {
                        throw new SecurityException("Files still open");
                    }
                }
                this.mSealed = true;
            }
            this.mClientProgress = 1.0f;
            computeProgressLocked(true);
        }
        if (!z) {
            this.mCallback.onSessionSealedBlocking(this);
        }
        this.mActiveCount.incrementAndGet();
        this.mHandler.obtainMessage(0, new PackageInstallObserverAdapter(this.mContext, intentSender, this.sessionId, this.userId).getBinder()).sendToTarget();
    }

    public void abandon() throws RemoteException {
        destroyInternal();
        dispatchSessionFinished(-115, "Session was abandoned", null);
    }

    /* access modifiers changed from: private */
    public void destroyInternal() {
        synchronized (this.mLock) {
            this.mSealed = true;
            this.mDestroyed = true;
            Iterator it = this.mBridges.iterator();
            while (it.hasNext()) {
                ((FileBridge) it.next()).forceClose();
            }
        }
        if (this.stageDir != null) {
            FileUtils.deleteDir(this.stageDir.getAbsolutePath());
        }
    }

    /* access modifiers changed from: private */
    public void dispatchSessionFinished(int i, String str, Bundle bundle) {
        this.mFinalStatus = i;
        this.mFinalMessage = str;
        if (this.mRemoteObserver != null) {
            try {
                this.mRemoteObserver.onPackageInstalled(this.mPackageName, i, str, bundle);
            } catch (RemoteException unused) {
            }
        }
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        this.mCallback.onSessionFinished(this, z);
    }

    /* access modifiers changed from: 0000 */
    public void setPermissionsResult(boolean z) {
        if (!this.mSealed) {
            throw new SecurityException("Must be sealed to accept permissions");
        } else if (z) {
            synchronized (this.mLock) {
                this.mPermissionsAccepted = true;
            }
            this.mHandler.obtainMessage(0).sendToTarget();
        } else {
            destroyInternal();
            dispatchSessionFinished(-115, "User rejected permissions", null);
        }
    }

    public void open() throws IOException {
        if (this.mActiveCount.getAndIncrement() == 0) {
            this.mCallback.onSessionActiveChanged(this, true);
        }
        synchronized (this.mLock) {
            if (!this.mPrepared) {
                if (this.stageDir != null) {
                    this.mPrepared = true;
                    this.mCallback.onSessionPrepared(this);
                } else {
                    throw new IllegalArgumentException("Exactly one of stageDir or stageCid stage must be set");
                }
            }
        }
    }

    public static String getCompleteMessage(Throwable th) {
        StringBuilder sb = new StringBuilder();
        sb.append(th.getMessage());
        while (true) {
            th = th.getCause();
            if (th == null) {
                return sb.toString();
            }
            sb.append(": ");
            sb.append(th.getMessage());
        }
    }
}
