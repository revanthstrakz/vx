package com.microsoft.appcenter.channel;

import android.support.annotation.NonNull;
import com.microsoft.appcenter.channel.Channel.GroupListener;
import com.microsoft.appcenter.channel.Channel.Listener;
import com.microsoft.appcenter.ingestion.models.Log;

public class AbstractChannelListener implements Listener {
    public void onClear(@NonNull String str) {
    }

    public void onGloballyEnabled(boolean z) {
    }

    public void onGroupAdded(@NonNull String str, GroupListener groupListener, long j) {
    }

    public void onGroupRemoved(@NonNull String str) {
    }

    public void onPaused(@NonNull String str, String str2) {
    }

    public void onPreparedLog(@NonNull Log log, @NonNull String str, int i) {
    }

    public void onPreparingLog(@NonNull Log log, @NonNull String str) {
    }

    public void onResumed(@NonNull String str, String str2) {
    }

    public boolean shouldFilter(@NonNull Log log) {
        return false;
    }
}
