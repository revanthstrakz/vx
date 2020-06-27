package org.jdeferred;

public interface ProgressPipe<P, D_OUT, F_OUT, P_OUT> {
    Promise<D_OUT, F_OUT, P_OUT> pipeProgress(P p);
}
