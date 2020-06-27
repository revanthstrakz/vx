package org.jdeferred;

public interface FailFilter<F, F_OUT> {
    F_OUT filterFail(F f);
}
