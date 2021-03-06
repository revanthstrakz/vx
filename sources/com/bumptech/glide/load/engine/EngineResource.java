package com.bumptech.glide.load.engine;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;

class EngineResource<Z> implements Resource<Z> {
    private int acquired;
    private final boolean isCacheable;
    private final boolean isRecyclable;
    private boolean isRecycled;
    private Key key;
    private ResourceListener listener;
    private final Resource<Z> resource;

    interface ResourceListener {
        void onResourceReleased(Key key, EngineResource<?> engineResource);
    }

    EngineResource(Resource<Z> resource2, boolean z, boolean z2) {
        this.resource = (Resource) Preconditions.checkNotNull(resource2);
        this.isCacheable = z;
        this.isRecyclable = z2;
    }

    /* access modifiers changed from: 0000 */
    public void setResourceListener(Key key2, ResourceListener resourceListener) {
        this.key = key2;
        this.listener = resourceListener;
    }

    /* access modifiers changed from: 0000 */
    public Resource<Z> getResource() {
        return this.resource;
    }

    /* access modifiers changed from: 0000 */
    public boolean isCacheable() {
        return this.isCacheable;
    }

    @NonNull
    public Class<Z> getResourceClass() {
        return this.resource.getResourceClass();
    }

    @NonNull
    public Z get() {
        return this.resource.get();
    }

    public int getSize() {
        return this.resource.getSize();
    }

    public void recycle() {
        if (this.acquired > 0) {
            throw new IllegalStateException("Cannot recycle a resource while it is still acquired");
        } else if (!this.isRecycled) {
            this.isRecycled = true;
            if (this.isRecyclable) {
                this.resource.recycle();
            }
        } else {
            throw new IllegalStateException("Cannot recycle a resource that has already been recycled");
        }
    }

    /* access modifiers changed from: 0000 */
    public void acquire() {
        if (this.isRecycled) {
            throw new IllegalStateException("Cannot acquire a recycled resource");
        } else if (Looper.getMainLooper().equals(Looper.myLooper())) {
            this.acquired++;
        } else {
            throw new IllegalThreadStateException("Must call acquire on the main thread");
        }
    }

    /* access modifiers changed from: 0000 */
    public void release() {
        if (this.acquired <= 0) {
            throw new IllegalStateException("Cannot release a recycled or not yet acquired resource");
        } else if (Looper.getMainLooper().equals(Looper.myLooper())) {
            int i = this.acquired - 1;
            this.acquired = i;
            if (i == 0) {
                this.listener.onResourceReleased(this.key, this);
            }
        } else {
            throw new IllegalThreadStateException("Must call release on the main thread");
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EngineResource{isCacheable=");
        sb.append(this.isCacheable);
        sb.append(", listener=");
        sb.append(this.listener);
        sb.append(", key=");
        sb.append(this.key);
        sb.append(", acquired=");
        sb.append(this.acquired);
        sb.append(", isRecycled=");
        sb.append(this.isRecycled);
        sb.append(", resource=");
        sb.append(this.resource);
        sb.append('}');
        return sb.toString();
    }
}
