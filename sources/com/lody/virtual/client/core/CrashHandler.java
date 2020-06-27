package com.lody.virtual.client.core;

public interface CrashHandler {
    void handleUncaughtException(Thread thread, Throwable th);
}
