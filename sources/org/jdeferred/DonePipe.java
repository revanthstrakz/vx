package org.jdeferred;

public interface DonePipe<D, D_OUT, F_OUT, P_OUT> {
    Promise<D_OUT, F_OUT, P_OUT> pipeDone(D d);
}
