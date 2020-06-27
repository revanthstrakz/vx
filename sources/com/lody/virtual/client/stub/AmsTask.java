package com.lody.virtual.client.stub;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.IAccountManagerResponse;
import android.accounts.IAccountManagerResponse.Stub;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.helper.utils.VLog;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public abstract class AmsTask extends FutureTask<Bundle> implements AccountManagerFuture<Bundle> {
    final Activity mActivity;
    final AccountManagerCallback<Bundle> mCallback;
    final Handler mHandler;
    protected final IAccountManagerResponse mResponse = new Response();

    private class Response extends Stub {
        private Response() {
        }

        public void onResult(Bundle bundle) {
            Intent intent = (Intent) bundle.getParcelable(BaseLauncherColumns.INTENT);
            if (intent != null && AmsTask.this.mActivity != null) {
                AmsTask.this.mActivity.startActivity(intent);
            } else if (bundle.getBoolean("retry")) {
                try {
                    AmsTask.this.doWork();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AmsTask.this.set(bundle);
            }
        }

        public void onError(int i, String str) {
            if (i == 4 || i == 100 || i == 101) {
                AmsTask.this.cancel(true);
            } else {
                AmsTask.this.setException(AmsTask.this.convertErrorToException(i, str));
            }
        }
    }

    public abstract void doWork() throws RemoteException;

    public AmsTask(Activity activity, Handler handler, AccountManagerCallback<Bundle> accountManagerCallback) {
        super(new Callable<Bundle>() {
            public Bundle call() throws Exception {
                throw new IllegalStateException("this should never be called");
            }
        });
        this.mHandler = handler;
        this.mCallback = accountManagerCallback;
        this.mActivity = activity;
    }

    public final AccountManagerFuture<Bundle> start() {
        try {
            doWork();
        } catch (RemoteException e) {
            setException(e);
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public void set(Bundle bundle) {
        if (bundle == null) {
            VLog.m87e("AccountManager", "the bundle must not be null", new Exception());
        }
        super.set(bundle);
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x0058 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.os.Bundle internalGetResult(java.lang.Long r4, java.util.concurrent.TimeUnit r5) throws android.accounts.OperationCanceledException, java.io.IOException, android.accounts.AuthenticatorException {
        /*
            r3 = this;
            r0 = 1
            if (r4 != 0) goto L_0x0011
            java.lang.Object r4 = r3.get()     // Catch:{ CancellationException -> 0x0058, InterruptedException | TimeoutException -> 0x004f, ExecutionException -> 0x000f }
            android.os.Bundle r4 = (android.os.Bundle) r4     // Catch:{ CancellationException -> 0x0058, InterruptedException | TimeoutException -> 0x004f, ExecutionException -> 0x000f }
            r3.cancel(r0)
            return r4
        L_0x000d:
            r4 = move-exception
            goto L_0x005e
        L_0x000f:
            r4 = move-exception
            goto L_0x001f
        L_0x0011:
            long r1 = r4.longValue()     // Catch:{ CancellationException -> 0x0058, InterruptedException | TimeoutException -> 0x004f, ExecutionException -> 0x000f }
            java.lang.Object r4 = r3.get(r1, r5)     // Catch:{ CancellationException -> 0x0058, InterruptedException | TimeoutException -> 0x004f, ExecutionException -> 0x000f }
            android.os.Bundle r4 = (android.os.Bundle) r4     // Catch:{ CancellationException -> 0x0058, InterruptedException | TimeoutException -> 0x004f, ExecutionException -> 0x000f }
            r3.cancel(r0)
            return r4
        L_0x001f:
            java.lang.Throwable r4 = r4.getCause()     // Catch:{ all -> 0x000d }
            boolean r5 = r4 instanceof java.io.IOException     // Catch:{ all -> 0x000d }
            if (r5 != 0) goto L_0x004c
            boolean r5 = r4 instanceof java.lang.UnsupportedOperationException     // Catch:{ all -> 0x000d }
            if (r5 != 0) goto L_0x0046
            boolean r5 = r4 instanceof android.accounts.AuthenticatorException     // Catch:{ all -> 0x000d }
            if (r5 != 0) goto L_0x0043
            boolean r5 = r4 instanceof java.lang.RuntimeException     // Catch:{ all -> 0x000d }
            if (r5 != 0) goto L_0x0040
            boolean r5 = r4 instanceof java.lang.Error     // Catch:{ all -> 0x000d }
            if (r5 == 0) goto L_0x003a
            java.lang.Error r4 = (java.lang.Error) r4     // Catch:{ all -> 0x000d }
            throw r4     // Catch:{ all -> 0x000d }
        L_0x003a:
            java.lang.IllegalStateException r5 = new java.lang.IllegalStateException     // Catch:{ all -> 0x000d }
            r5.<init>(r4)     // Catch:{ all -> 0x000d }
            throw r5     // Catch:{ all -> 0x000d }
        L_0x0040:
            java.lang.RuntimeException r4 = (java.lang.RuntimeException) r4     // Catch:{ all -> 0x000d }
            throw r4     // Catch:{ all -> 0x000d }
        L_0x0043:
            android.accounts.AuthenticatorException r4 = (android.accounts.AuthenticatorException) r4     // Catch:{ all -> 0x000d }
            throw r4     // Catch:{ all -> 0x000d }
        L_0x0046:
            android.accounts.AuthenticatorException r5 = new android.accounts.AuthenticatorException     // Catch:{ all -> 0x000d }
            r5.<init>(r4)     // Catch:{ all -> 0x000d }
            throw r5     // Catch:{ all -> 0x000d }
        L_0x004c:
            java.io.IOException r4 = (java.io.IOException) r4     // Catch:{ all -> 0x000d }
            throw r4     // Catch:{ all -> 0x000d }
        L_0x004f:
            r3.cancel(r0)
            android.accounts.OperationCanceledException r4 = new android.accounts.OperationCanceledException
            r4.<init>()
            throw r4
        L_0x0058:
            android.accounts.OperationCanceledException r4 = new android.accounts.OperationCanceledException     // Catch:{ all -> 0x000d }
            r4.<init>()     // Catch:{ all -> 0x000d }
            throw r4     // Catch:{ all -> 0x000d }
        L_0x005e:
            r3.cancel(r0)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.stub.AmsTask.internalGetResult(java.lang.Long, java.util.concurrent.TimeUnit):android.os.Bundle");
    }

    public Bundle getResult() throws OperationCanceledException, IOException, AuthenticatorException {
        return internalGetResult(null, null);
    }

    public Bundle getResult(long j, TimeUnit timeUnit) throws OperationCanceledException, IOException, AuthenticatorException {
        return internalGetResult(Long.valueOf(j), timeUnit);
    }

    /* access modifiers changed from: protected */
    public void done() {
        if (this.mCallback != null) {
            postToHandler(this.mHandler, this.mCallback, this);
        }
    }

    /* access modifiers changed from: private */
    public Exception convertErrorToException(int i, String str) {
        if (i == 3) {
            return new IOException(str);
        }
        if (i == 6) {
            return new UnsupportedOperationException(str);
        }
        if (i == 5) {
            return new AuthenticatorException(str);
        }
        if (i == 7) {
            return new IllegalArgumentException(str);
        }
        return new AuthenticatorException(str);
    }

    private void postToHandler(Handler handler, final AccountManagerCallback<Bundle> accountManagerCallback, final AccountManagerFuture<Bundle> accountManagerFuture) {
        if (handler == null) {
            handler = VirtualRuntime.getUIHandler();
        }
        handler.post(new Runnable() {
            public void run() {
                accountManagerCallback.run(accountManagerFuture);
            }
        });
    }
}
