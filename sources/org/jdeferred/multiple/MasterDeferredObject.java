package org.jdeferred.multiple;

import java.util.concurrent.atomic.AtomicInteger;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

public class MasterDeferredObject extends DeferredObject<MultipleResults, OneReject, MasterProgress> implements Promise<MultipleResults, OneReject, MasterProgress> {
    /* access modifiers changed from: private */
    public final AtomicInteger doneCount = new AtomicInteger();
    /* access modifiers changed from: private */
    public final AtomicInteger failCount = new AtomicInteger();
    /* access modifiers changed from: private */
    public final int numberOfPromises;
    /* access modifiers changed from: private */
    public final MultipleResults results;

    public MasterDeferredObject(Promise... promiseArr) {
        if (promiseArr == null || promiseArr.length == 0) {
            throw new IllegalArgumentException("Promises is null or empty");
        }
        this.numberOfPromises = promiseArr.length;
        this.results = new MultipleResults(this.numberOfPromises);
        int length = promiseArr.length;
        int i = 0;
        final int i2 = 0;
        while (i < length) {
            final Promise promise = promiseArr[i];
            int i3 = i2 + 1;
            promise.fail(new FailCallback<Object>() {
                public void onFail(Object obj) {
                    synchronized (MasterDeferredObject.this) {
                        if (MasterDeferredObject.this.isPending()) {
                            MasterDeferredObject.this.notify(new MasterProgress(MasterDeferredObject.this.doneCount.get(), MasterDeferredObject.this.failCount.incrementAndGet(), MasterDeferredObject.this.numberOfPromises));
                            MasterDeferredObject.this.reject(new OneReject(i2, promise, obj));
                        }
                    }
                }
            }).progress(new ProgressCallback() {
                public void onProgress(Object obj) {
                    synchronized (MasterDeferredObject.this) {
                        if (MasterDeferredObject.this.isPending()) {
                            MasterDeferredObject masterDeferredObject = MasterDeferredObject.this;
                            OneProgress oneProgress = new OneProgress(MasterDeferredObject.this.doneCount.get(), MasterDeferredObject.this.failCount.get(), MasterDeferredObject.this.numberOfPromises, i2, promise, obj);
                            masterDeferredObject.notify(oneProgress);
                        }
                    }
                }
            }).done(new DoneCallback() {
                /* JADX WARNING: Code restructure failed: missing block: B:11:0x0059, code lost:
                    return;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onDone(java.lang.Object r7) {
                    /*
                        r6 = this;
                        org.jdeferred.multiple.MasterDeferredObject r0 = org.jdeferred.multiple.MasterDeferredObject.this
                        monitor-enter(r0)
                        org.jdeferred.multiple.MasterDeferredObject r1 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        boolean r1 = r1.isPending()     // Catch:{ all -> 0x005a }
                        if (r1 != 0) goto L_0x000d
                        monitor-exit(r0)     // Catch:{ all -> 0x005a }
                        return
                    L_0x000d:
                        org.jdeferred.multiple.MasterDeferredObject r1 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MultipleResults r1 = r1.results     // Catch:{ all -> 0x005a }
                        int r2 = r2     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.OneResult r3 = new org.jdeferred.multiple.OneResult     // Catch:{ all -> 0x005a }
                        int r4 = r2     // Catch:{ all -> 0x005a }
                        org.jdeferred.Promise r5 = r3     // Catch:{ all -> 0x005a }
                        r3.<init>(r4, r5, r7)     // Catch:{ all -> 0x005a }
                        r1.set(r2, r3)     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MasterDeferredObject r7 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        java.util.concurrent.atomic.AtomicInteger r7 = r7.doneCount     // Catch:{ all -> 0x005a }
                        int r7 = r7.incrementAndGet()     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MasterDeferredObject r1 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MasterProgress r2 = new org.jdeferred.multiple.MasterProgress     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MasterDeferredObject r3 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        java.util.concurrent.atomic.AtomicInteger r3 = r3.failCount     // Catch:{ all -> 0x005a }
                        int r3 = r3.get()     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MasterDeferredObject r4 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        int r4 = r4.numberOfPromises     // Catch:{ all -> 0x005a }
                        r2.<init>(r7, r3, r4)     // Catch:{ all -> 0x005a }
                        r1.notify(r2)     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MasterDeferredObject r1 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        int r1 = r1.numberOfPromises     // Catch:{ all -> 0x005a }
                        if (r7 != r1) goto L_0x0058
                        org.jdeferred.multiple.MasterDeferredObject r7 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MasterDeferredObject r1 = org.jdeferred.multiple.MasterDeferredObject.this     // Catch:{ all -> 0x005a }
                        org.jdeferred.multiple.MultipleResults r1 = r1.results     // Catch:{ all -> 0x005a }
                        r7.resolve(r1)     // Catch:{ all -> 0x005a }
                    L_0x0058:
                        monitor-exit(r0)     // Catch:{ all -> 0x005a }
                        return
                    L_0x005a:
                        r7 = move-exception
                        monitor-exit(r0)     // Catch:{ all -> 0x005a }
                        throw r7
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.jdeferred.multiple.MasterDeferredObject.C13841.onDone(java.lang.Object):void");
                }
            });
            i++;
            i2 = i3;
        }
    }
}
