package org.jdeferred;

public interface ProgressCallback<P> {
    void onProgress(P p);
}
