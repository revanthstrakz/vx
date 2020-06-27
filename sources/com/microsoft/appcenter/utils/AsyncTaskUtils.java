package com.microsoft.appcenter.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import java.util.concurrent.RejectedExecutionException;

public class AsyncTaskUtils {
    @VisibleForTesting
    AsyncTaskUtils() {
    }

    @SafeVarargs
    @NonNull
    public static <Params, Type extends AsyncTask<Params, ?, ?>> Type execute(String str, @NonNull Type type, Params... paramsArr) {
        try {
            return type.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArr);
        } catch (RejectedExecutionException e) {
            AppCenterLog.warn(str, "THREAD_POOL_EXECUTOR saturated, fall back on SERIAL_EXECUTOR which has an unbounded queue", e);
            return type.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, paramsArr);
        }
    }
}
