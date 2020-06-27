package org.jdeferred;

public interface FailPipe<F, D_OUT, F_OUT, P_OUT> {
    Promise<D_OUT, F_OUT, P_OUT> pipeFail(F f);
}
