package org.jdeferred;

public interface DoneFilter<D, D_OUT> {
    D_OUT filterDone(D d);
}
