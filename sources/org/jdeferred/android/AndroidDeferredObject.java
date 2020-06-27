package org.jdeferred.android;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;
import org.jdeferred.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AndroidDeferredObject<D, F, P> extends DeferredObject<D, F, P> {
    private static final int MESSAGE_POST_ALWAYS = 4;
    private static final int MESSAGE_POST_DONE = 1;
    private static final int MESSAGE_POST_FAIL = 3;
    private static final int MESSAGE_POST_PROGRESS = 2;
    private static final InternalHandler sHandler = new InternalHandler();
    private final AndroidExecutionScope defaultAndroidExecutionScope;
    protected final Logger log;

    private static class CallbackMessage<Callback, D, F, P> {
        final Callback callback;
        final Deferred deferred;
        final P progress;
        final F rejected;
        final D resolved;
        final State state;

        CallbackMessage(Deferred deferred2, Callback callback2, State state2, D d, F f, P p) {
            this.deferred = deferred2;
            this.callback = callback2;
            this.state = state2;
            this.resolved = d;
            this.rejected = f;
            this.progress = p;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message message) {
            CallbackMessage callbackMessage = (CallbackMessage) message.obj;
            switch (message.what) {
                case 1:
                    ((DoneCallback) callbackMessage.callback).onDone(callbackMessage.resolved);
                    return;
                case 2:
                    ((ProgressCallback) callbackMessage.callback).onProgress(callbackMessage.progress);
                    return;
                case 3:
                    ((FailCallback) callbackMessage.callback).onFail(callbackMessage.rejected);
                    return;
                case 4:
                    ((AlwaysCallback) callbackMessage.callback).onAlways(callbackMessage.state, callbackMessage.resolved, callbackMessage.rejected);
                    return;
                default:
                    return;
            }
        }
    }

    public AndroidDeferredObject(Promise<D, F, P> promise) {
        this(promise, AndroidExecutionScope.UI);
    }

    public AndroidDeferredObject(Promise<D, F, P> promise, AndroidExecutionScope androidExecutionScope) {
        this.log = LoggerFactory.getLogger(AndroidDeferredObject.class);
        this.defaultAndroidExecutionScope = androidExecutionScope;
        promise.done(new DoneCallback<D>() {
            public void onDone(D d) {
                AndroidDeferredObject.this.resolve(d);
            }
        }).progress(new ProgressCallback<P>() {
            public void onProgress(P p) {
                AndroidDeferredObject.this.notify(p);
            }
        }).fail(new FailCallback<F>() {
            public void onFail(F f) {
                AndroidDeferredObject.this.reject(f);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void triggerDone(DoneCallback<D> doneCallback, D d) {
        if (determineAndroidExecutionScope(doneCallback) == AndroidExecutionScope.UI) {
            executeInUiThread(1, doneCallback, State.RESOLVED, d, null, null);
            return;
        }
        super.triggerDone(doneCallback, d);
    }

    /* access modifiers changed from: protected */
    public void triggerFail(FailCallback<F> failCallback, F f) {
        if (determineAndroidExecutionScope(failCallback) == AndroidExecutionScope.UI) {
            executeInUiThread(3, failCallback, State.REJECTED, null, f, null);
            return;
        }
        super.triggerFail(failCallback, f);
    }

    /* access modifiers changed from: protected */
    public void triggerProgress(ProgressCallback<P> progressCallback, P p) {
        if (determineAndroidExecutionScope(progressCallback) == AndroidExecutionScope.UI) {
            executeInUiThread(2, progressCallback, State.PENDING, null, null, p);
            return;
        }
        super.triggerProgress(progressCallback, p);
    }

    /* access modifiers changed from: protected */
    public void triggerAlways(AlwaysCallback<D, F> alwaysCallback, State state, D d, F f) {
        if (determineAndroidExecutionScope(alwaysCallback) == AndroidExecutionScope.UI) {
            executeInUiThread(4, alwaysCallback, state, d, f, null);
        } else {
            super.triggerAlways(alwaysCallback, state, d, f);
        }
    }

    /* access modifiers changed from: protected */
    public <Callback> void executeInUiThread(int i, Callback callback, State state, D d, F f, P p) {
        InternalHandler internalHandler = sHandler;
        CallbackMessage callbackMessage = new CallbackMessage(this, callback, state, d, f, p);
        internalHandler.obtainMessage(i, callbackMessage).sendToTarget();
    }

    /* access modifiers changed from: protected */
    public AndroidExecutionScope determineAndroidExecutionScope(Object obj) {
        AndroidExecutionScope executionScope = obj instanceof AndroidExecutionScopeable ? ((AndroidExecutionScopeable) obj).getExecutionScope() : null;
        return executionScope == null ? this.defaultAndroidExecutionScope : executionScope;
    }
}
