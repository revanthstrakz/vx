package com.microsoft.appcenter.analytics.channel;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.analytics.ingestion.models.StartSessionLog;
import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.StartServiceLog;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.context.SessionContext;
import com.microsoft.appcenter.utils.context.SessionContext.SessionInfo;
import java.util.Date;
import java.util.UUID;

public class SessionTracker extends AbstractChannelListener {
    private static final long SESSION_TIMEOUT = 20000;
    private final Channel mChannel;
    private final String mGroupName;
    private Long mLastPausedTime;
    private long mLastQueuedLogTime;
    private Long mLastResumedTime;
    private UUID mSid;

    public SessionTracker(Channel channel, String str) {
        this.mChannel = channel;
        this.mGroupName = str;
    }

    public void onPreparingLog(@NonNull Log log, @NonNull String str) {
        if (!(log instanceof StartSessionLog) && !(log instanceof StartServiceLog)) {
            Date timestamp = log.getTimestamp();
            if (timestamp != null) {
                SessionInfo sessionAt = SessionContext.getInstance().getSessionAt(timestamp.getTime());
                if (sessionAt != null) {
                    log.setSid(sessionAt.getSessionId());
                }
            } else {
                log.setSid(this.mSid);
                this.mLastQueuedLogTime = SystemClock.elapsedRealtime();
            }
        }
    }

    private void sendStartSessionIfNeeded() {
        if (this.mSid == null || hasSessionTimedOut()) {
            this.mSid = UUID.randomUUID();
            SessionContext.getInstance().addSession(this.mSid);
            this.mLastQueuedLogTime = SystemClock.elapsedRealtime();
            StartSessionLog startSessionLog = new StartSessionLog();
            startSessionLog.setSid(this.mSid);
            this.mChannel.enqueue(startSessionLog, this.mGroupName, 1);
        }
    }

    public void onActivityResumed() {
        AppCenterLog.debug(Analytics.LOG_TAG, "onActivityResumed");
        this.mLastResumedTime = Long.valueOf(SystemClock.elapsedRealtime());
        sendStartSessionIfNeeded();
    }

    public void onActivityPaused() {
        AppCenterLog.debug(Analytics.LOG_TAG, "onActivityPaused");
        this.mLastPausedTime = Long.valueOf(SystemClock.elapsedRealtime());
    }

    public void clearSessions() {
        SessionContext.getInstance().clearSessions();
    }

    private boolean hasSessionTimedOut() {
        if (this.mLastPausedTime == null) {
            return false;
        }
        boolean z = true;
        boolean z2 = SystemClock.elapsedRealtime() - this.mLastQueuedLogTime >= SESSION_TIMEOUT;
        boolean z3 = this.mLastResumedTime.longValue() - Math.max(this.mLastPausedTime.longValue(), this.mLastQueuedLogTime) >= SESSION_TIMEOUT;
        String str = Analytics.LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("noLogSentForLong=");
        sb.append(z2);
        sb.append(" wasBackgroundForLong=");
        sb.append(z3);
        AppCenterLog.debug(str, sb.toString());
        if (!z2 || !z3) {
            z = false;
        }
        return z;
    }
}
