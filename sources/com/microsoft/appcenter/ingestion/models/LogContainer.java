package com.microsoft.appcenter.ingestion.models;

import java.util.List;

public class LogContainer {
    private List<Log> logs;

    public List<Log> getLogs() {
        return this.logs;
    }

    public void setLogs(List<Log> list) {
        this.logs = list;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LogContainer logContainer = (LogContainer) obj;
        if (this.logs != null) {
            z = this.logs.equals(logContainer.logs);
        } else if (logContainer.logs != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        if (this.logs != null) {
            return this.logs.hashCode();
        }
        return 0;
    }
}
