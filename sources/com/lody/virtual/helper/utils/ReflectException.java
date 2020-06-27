package com.lody.virtual.helper.utils;

public class ReflectException extends RuntimeException {
    public ReflectException(String str, Throwable th) {
        super(str, th);
    }

    public ReflectException(Throwable th) {
        super(th);
    }
}
