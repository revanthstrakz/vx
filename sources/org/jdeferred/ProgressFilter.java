package org.jdeferred;

public interface ProgressFilter<P, P_OUT> {
    P_OUT filterProgress(P p);
}
