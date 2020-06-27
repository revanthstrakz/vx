package com.lody.virtual.client.env;

public class DeadServerException extends RuntimeException {
    public DeadServerException() {
    }

    public DeadServerException(String str) {
        super(str);
    }

    public DeadServerException(String str, Throwable th) {
        super(str, th);
    }

    public DeadServerException(Throwable th) {
        super(th);
    }

    public DeadServerException(String str, Throwable th, boolean z, boolean z2) {
        super(str, th, z, z2);
    }
}
