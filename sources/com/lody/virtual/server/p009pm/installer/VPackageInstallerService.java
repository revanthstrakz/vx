package com.lody.virtual.server.p009pm.installer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.p000pm.IPackageInstallerCallback;
import android.content.p000pm.IPackageInstallerSession;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.SparseArray;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.compat.ObjectsCompat;
import com.lody.virtual.helper.utils.Singleton;
import com.lody.virtual.p007os.VBinder;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.VParceledListSlice;
import com.lody.virtual.server.IPackageInstaller.Stub;
import com.lody.virtual.server.p009pm.VAppManagerService;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

@TargetApi(21)
/* renamed from: com.lody.virtual.server.pm.installer.VPackageInstallerService */
public class VPackageInstallerService extends Stub {
    private static final long MAX_ACTIVE_SESSIONS = 1024;
    private static final String TAG = "PackageInstaller";
    private static final Singleton<VPackageInstallerService> gDefault = new Singleton<VPackageInstallerService>() {
        /* access modifiers changed from: protected */
        public VPackageInstallerService create() {
            return new VPackageInstallerService();
        }
    };
    /* access modifiers changed from: private */
    public final Callbacks mCallbacks;
    private Context mContext;
    /* access modifiers changed from: private */
    public final Handler mInstallHandler;
    private final HandlerThread mInstallThread;
    private final InternalCallback mInternalCallback;
    private final Random mRandom;
    /* access modifiers changed from: private */
    public final SparseArray<PackageInstallerSession> mSessions;

    /* renamed from: com.lody.virtual.server.pm.installer.VPackageInstallerService$Callbacks */
    private static class Callbacks extends Handler {
        private static final int MSG_SESSION_ACTIVE_CHANGED = 3;
        private static final int MSG_SESSION_BADGING_CHANGED = 2;
        private static final int MSG_SESSION_CREATED = 1;
        private static final int MSG_SESSION_FINISHED = 5;
        private static final int MSG_SESSION_PROGRESS_CHANGED = 4;
        private final RemoteCallbackList<IPackageInstallerCallback> mCallbacks = new RemoteCallbackList<>();

        public Callbacks(Looper looper) {
            super(looper);
        }

        public void register(IPackageInstallerCallback iPackageInstallerCallback, int i) {
            this.mCallbacks.register(iPackageInstallerCallback, new VUserHandle(i));
        }

        public void unregister(IPackageInstallerCallback iPackageInstallerCallback) {
            this.mCallbacks.unregister(iPackageInstallerCallback);
        }

        public void handleMessage(Message message) {
            int i = message.arg2;
            int beginBroadcast = this.mCallbacks.beginBroadcast();
            for (int i2 = 0; i2 < beginBroadcast; i2++) {
                IPackageInstallerCallback iPackageInstallerCallback = (IPackageInstallerCallback) this.mCallbacks.getBroadcastItem(i2);
                if (i == ((VUserHandle) this.mCallbacks.getBroadcastCookie(i2)).getIdentifier()) {
                    try {
                        invokeCallback(iPackageInstallerCallback, message);
                    } catch (RemoteException unused) {
                    }
                }
            }
            this.mCallbacks.finishBroadcast();
        }

        private void invokeCallback(IPackageInstallerCallback iPackageInstallerCallback, Message message) throws RemoteException {
            int i = message.arg1;
            switch (message.what) {
                case 1:
                    iPackageInstallerCallback.onSessionCreated(i);
                    return;
                case 2:
                    iPackageInstallerCallback.onSessionBadgingChanged(i);
                    return;
                case 3:
                    iPackageInstallerCallback.onSessionActiveChanged(i, ((Boolean) message.obj).booleanValue());
                    return;
                case 4:
                    iPackageInstallerCallback.onSessionProgressChanged(i, ((Float) message.obj).floatValue());
                    return;
                case 5:
                    iPackageInstallerCallback.onSessionFinished(i, ((Boolean) message.obj).booleanValue());
                    return;
                default:
                    return;
            }
        }

        /* access modifiers changed from: private */
        public void notifySessionCreated(int i, int i2) {
            obtainMessage(1, i, i2).sendToTarget();
        }

        /* access modifiers changed from: private */
        public void notifySessionBadgingChanged(int i, int i2) {
            obtainMessage(2, i, i2).sendToTarget();
        }

        /* access modifiers changed from: private */
        public void notifySessionActiveChanged(int i, int i2, boolean z) {
            obtainMessage(3, i, i2, Boolean.valueOf(z)).sendToTarget();
        }

        /* access modifiers changed from: private */
        public void notifySessionProgressChanged(int i, int i2, float f) {
            obtainMessage(4, i, i2, Float.valueOf(f)).sendToTarget();
        }

        public void notifySessionFinished(int i, int i2, boolean z) {
            obtainMessage(5, i, i2, Boolean.valueOf(z)).sendToTarget();
        }
    }

    /* renamed from: com.lody.virtual.server.pm.installer.VPackageInstallerService$InternalCallback */
    class InternalCallback {
        public void onSessionPrepared(PackageInstallerSession packageInstallerSession) {
        }

        public void onSessionSealedBlocking(PackageInstallerSession packageInstallerSession) {
        }

        InternalCallback() {
        }

        public void onSessionBadgingChanged(PackageInstallerSession packageInstallerSession) {
            VPackageInstallerService.this.mCallbacks.notifySessionBadgingChanged(packageInstallerSession.sessionId, packageInstallerSession.userId);
        }

        public void onSessionActiveChanged(PackageInstallerSession packageInstallerSession, boolean z) {
            VPackageInstallerService.this.mCallbacks.notifySessionActiveChanged(packageInstallerSession.sessionId, packageInstallerSession.userId, z);
        }

        public void onSessionProgressChanged(PackageInstallerSession packageInstallerSession, float f) {
            VPackageInstallerService.this.mCallbacks.notifySessionProgressChanged(packageInstallerSession.sessionId, packageInstallerSession.userId, f);
        }

        public void onSessionFinished(final PackageInstallerSession packageInstallerSession, boolean z) {
            VPackageInstallerService.this.mCallbacks.notifySessionFinished(packageInstallerSession.sessionId, packageInstallerSession.userId, z);
            VPackageInstallerService.this.mInstallHandler.post(new Runnable() {
                public void run() {
                    synchronized (VPackageInstallerService.this.mSessions) {
                        VPackageInstallerService.this.mSessions.remove(packageInstallerSession.sessionId);
                    }
                }
            });
        }
    }

    /* renamed from: com.lody.virtual.server.pm.installer.VPackageInstallerService$PackageInstallObserverAdapter */
    static class PackageInstallObserverAdapter extends PackageInstallObserver {
        private final Context mContext;
        private final int mSessionId;
        private final IntentSender mTarget;
        private final int mUserId;

        PackageInstallObserverAdapter(Context context, IntentSender intentSender, int i, int i2) {
            this.mContext = context;
            this.mTarget = intentSender;
            this.mSessionId = i;
            this.mUserId = i2;
        }

        public void onUserActionRequired(Intent intent) {
            Intent intent2 = new Intent();
            intent2.putExtra("android.content.pm.extra.SESSION_ID", this.mSessionId);
            intent2.putExtra("android.content.pm.extra.STATUS", -1);
            intent2.putExtra("android.intent.extra.INTENT", intent);
            try {
                this.mTarget.sendIntent(this.mContext, 0, intent2, null, null);
            } catch (SendIntentException unused) {
            }
        }

        public void onPackageInstalled(String str, int i, String str2, Bundle bundle) {
            Intent intent = new Intent();
            intent.putExtra("android.content.pm.extra.PACKAGE_NAME", str);
            intent.putExtra("android.content.pm.extra.SESSION_ID", this.mSessionId);
            intent.putExtra("android.content.pm.extra.STATUS", PackageHelper.installStatusToPublicStatus(i));
            intent.putExtra("android.content.pm.extra.STATUS_MESSAGE", PackageHelper.installStatusToString(i, str2));
            intent.putExtra("android.content.pm.extra.LEGACY_STATUS", i);
            if (bundle != null) {
                String string = bundle.getString("android.content.pm.extra.FAILURE_EXISTING_PACKAGE");
                if (!TextUtils.isEmpty(string)) {
                    intent.putExtra("android.content.pm.extra.OTHER_PACKAGE_NAME", string);
                }
            }
            try {
                this.mTarget.sendIntent(this.mContext, 0, intent, null, null);
            } catch (SendIntentException unused) {
            }
        }
    }

    private boolean isCallingUidOwner(PackageInstallerSession packageInstallerSession) {
        return true;
    }

    private VPackageInstallerService() {
        this.mRandom = new SecureRandom();
        this.mSessions = new SparseArray<>();
        this.mInternalCallback = new InternalCallback();
        this.mContext = VirtualCore.get().getContext();
        this.mInstallThread = new HandlerThread(TAG);
        this.mInstallThread.start();
        this.mInstallHandler = new Handler(this.mInstallThread.getLooper());
        this.mCallbacks = new Callbacks(this.mInstallThread.getLooper());
    }

    public static VPackageInstallerService get() {
        return (VPackageInstallerService) gDefault.get();
    }

    private static int getSessionCount(SparseArray<PackageInstallerSession> sparseArray, int i) {
        int size = sparseArray.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            if (((PackageInstallerSession) sparseArray.valueAt(i3)).installerUid == i) {
                i2++;
            }
        }
        return i2;
    }

    public int createSession(SessionParams sessionParams, String str, int i) throws RemoteException {
        try {
            return createSessionInternal(sessionParams, str, i);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private int createSessionInternal(SessionParams sessionParams, String str, int i) throws IOException {
        int allocateSessionIdLocked;
        PackageInstallerSession packageInstallerSession;
        int callingUid = VBinder.getCallingUid();
        synchronized (this.mSessions) {
            if (((long) getSessionCount(this.mSessions, callingUid)) < 1024) {
                allocateSessionIdLocked = allocateSessionIdLocked();
                packageInstallerSession = new PackageInstallerSession(this.mInternalCallback, this.mContext, this.mInstallHandler.getLooper(), str, allocateSessionIdLocked, i, callingUid, sessionParams, VEnvironment.getPackageInstallerStageDir());
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Too many active sessions for UID ");
                sb.append(callingUid);
                throw new IllegalStateException(sb.toString());
            }
        }
        this.mCallbacks.notifySessionCreated(packageInstallerSession.sessionId, packageInstallerSession.userId);
        return allocateSessionIdLocked;
    }

    public void updateSessionAppIcon(int i, Bitmap bitmap) {
        synchronized (this.mSessions) {
            PackageInstallerSession packageInstallerSession = (PackageInstallerSession) this.mSessions.get(i);
            if (packageInstallerSession == null || !isCallingUidOwner(packageInstallerSession)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Caller has no access to session ");
                sb.append(i);
                throw new SecurityException(sb.toString());
            }
            packageInstallerSession.params.appIcon = bitmap;
            packageInstallerSession.params.appIconLastModified = -1;
            this.mInternalCallback.onSessionBadgingChanged(packageInstallerSession);
        }
    }

    public void updateSessionAppLabel(int i, String str) throws RemoteException {
        synchronized (this.mSessions) {
            PackageInstallerSession packageInstallerSession = (PackageInstallerSession) this.mSessions.get(i);
            if (packageInstallerSession == null || !isCallingUidOwner(packageInstallerSession)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Caller has no access to session ");
                sb.append(i);
                throw new SecurityException(sb.toString());
            }
            packageInstallerSession.params.appLabel = str;
            this.mInternalCallback.onSessionBadgingChanged(packageInstallerSession);
        }
    }

    public void abandonSession(int i) throws RemoteException {
        synchronized (this.mSessions) {
            PackageInstallerSession packageInstallerSession = (PackageInstallerSession) this.mSessions.get(i);
            if (packageInstallerSession == null || !isCallingUidOwner(packageInstallerSession)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Caller has no access to session ");
                sb.append(i);
                throw new SecurityException(sb.toString());
            }
            packageInstallerSession.abandon();
        }
    }

    public IPackageInstallerSession openSession(int i) throws RemoteException {
        try {
            return openSessionInternal(i);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private IPackageInstallerSession openSessionInternal(int i) throws IOException {
        PackageInstallerSession packageInstallerSession;
        synchronized (this.mSessions) {
            packageInstallerSession = (PackageInstallerSession) this.mSessions.get(i);
            if (packageInstallerSession == null || !isCallingUidOwner(packageInstallerSession)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Caller has no access to session ");
                sb.append(i);
                throw new SecurityException(sb.toString());
            }
            packageInstallerSession.open();
        }
        return packageInstallerSession;
    }

    public SessionInfo getSessionInfo(int i) throws RemoteException {
        SessionInfo generateInfo;
        synchronized (this.mSessions) {
            PackageInstallerSession packageInstallerSession = (PackageInstallerSession) this.mSessions.get(i);
            generateInfo = packageInstallerSession != null ? packageInstallerSession.generateInfo() : null;
        }
        return generateInfo;
    }

    public VParceledListSlice getAllSessions(int i) throws RemoteException {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mSessions) {
            for (int i2 = 0; i2 < this.mSessions.size(); i2++) {
                PackageInstallerSession packageInstallerSession = (PackageInstallerSession) this.mSessions.valueAt(i2);
                if (packageInstallerSession.userId == i) {
                    arrayList.add(packageInstallerSession.generateInfo());
                }
            }
        }
        return new VParceledListSlice(arrayList);
    }

    public VParceledListSlice getMySessions(String str, int i) throws RemoteException {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mSessions) {
            for (int i2 = 0; i2 < this.mSessions.size(); i2++) {
                PackageInstallerSession packageInstallerSession = (PackageInstallerSession) this.mSessions.valueAt(i2);
                if (ObjectsCompat.equals(packageInstallerSession.installerPackageName, str) && packageInstallerSession.userId == i) {
                    arrayList.add(packageInstallerSession.generateInfo());
                }
            }
        }
        return new VParceledListSlice(arrayList);
    }

    public void registerCallback(IPackageInstallerCallback iPackageInstallerCallback, int i) throws RemoteException {
        this.mCallbacks.register(iPackageInstallerCallback, i);
    }

    public void unregisterCallback(IPackageInstallerCallback iPackageInstallerCallback) throws RemoteException {
        this.mCallbacks.unregister(iPackageInstallerCallback);
    }

    public void uninstall(String str, String str2, int i, IntentSender intentSender, int i2) throws RemoteException {
        boolean uninstallPackage = VAppManagerService.get().uninstallPackage(str);
        if (intentSender != null) {
            Intent intent = new Intent();
            intent.putExtra("android.content.pm.extra.PACKAGE_NAME", str);
            intent.putExtra("android.content.pm.extra.STATUS", uninstallPackage ^ true ? 1 : 0);
            intent.putExtra("android.content.pm.extra.STATUS_MESSAGE", PackageHelper.deleteStatusToString(uninstallPackage));
            intent.putExtra("android.content.pm.extra.LEGACY_STATUS", uninstallPackage ? 1 : -1);
            try {
                intentSender.sendIntent(this.mContext, 0, intent, null, null);
            } catch (SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPermissionsResult(int i, boolean z) throws RemoteException {
        synchronized (this.mSessions) {
            PackageInstallerSession packageInstallerSession = (PackageInstallerSession) this.mSessions.get(i);
            if (packageInstallerSession != null) {
                packageInstallerSession.setPermissionsResult(z);
            }
        }
    }

    private int allocateSessionIdLocked() {
        int i = 0;
        while (true) {
            int nextInt = this.mRandom.nextInt(2147483646) + 1;
            if (this.mSessions.get(nextInt) == null) {
                return nextInt;
            }
            int i2 = i + 1;
            if (i < 32) {
                i = i2;
            } else {
                throw new IllegalStateException("Failed to allocate session ID");
            }
        }
    }
}
