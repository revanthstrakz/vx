package org.jdeferred;

public interface FailCallback<F> {
    void onFail(F f);
}
