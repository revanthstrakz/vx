package org.jdeferred;

import org.jdeferred.Promise.State;

public interface AlwaysCallback<D, R> {
    void onAlways(State state, D d, R r);
}
