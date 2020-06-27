package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.ingestion.models.AbstractLog;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.Date;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public abstract class AbstractErrorLog extends AbstractLog {
    private static final String APP_LAUNCH_TIMESTAMP = "appLaunchTimestamp";
    private static final String ARCHITECTURE = "architecture";
    private static final String ERROR_THREAD_ID = "errorThreadId";
    private static final String ERROR_THREAD_NAME = "errorThreadName";
    private static final String FATAL = "fatal";
    private static final String PARENT_PROCESS_ID = "parentProcessId";
    private static final String PARENT_PROCESS_NAME = "parentProcessName";
    private static final String PROCESS_ID = "processId";
    private static final String PROCESS_NAME = "processName";
    private Date appLaunchTimestamp;
    private String architecture;
    private Long errorThreadId;
    private String errorThreadName;
    private Boolean fatal;

    /* renamed from: id */
    private UUID f187id;
    private Integer parentProcessId;
    private String parentProcessName;
    private Integer processId;
    private String processName;

    public UUID getId() {
        return this.f187id;
    }

    public void setId(UUID uuid) {
        this.f187id = uuid;
    }

    public Integer getProcessId() {
        return this.processId;
    }

    public void setProcessId(Integer num) {
        this.processId = num;
    }

    public String getProcessName() {
        return this.processName;
    }

    public void setProcessName(String str) {
        this.processName = str;
    }

    public Integer getParentProcessId() {
        return this.parentProcessId;
    }

    public void setParentProcessId(Integer num) {
        this.parentProcessId = num;
    }

    public String getParentProcessName() {
        return this.parentProcessName;
    }

    public void setParentProcessName(String str) {
        this.parentProcessName = str;
    }

    public Long getErrorThreadId() {
        return this.errorThreadId;
    }

    public void setErrorThreadId(Long l) {
        this.errorThreadId = l;
    }

    public String getErrorThreadName() {
        return this.errorThreadName;
    }

    public void setErrorThreadName(String str) {
        this.errorThreadName = str;
    }

    public Boolean getFatal() {
        return this.fatal;
    }

    public void setFatal(Boolean bool) {
        this.fatal = bool;
    }

    public Date getAppLaunchTimestamp() {
        return this.appLaunchTimestamp;
    }

    public void setAppLaunchTimestamp(Date date) {
        this.appLaunchTimestamp = date;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public void setArchitecture(String str) {
        this.architecture = str;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setId(UUID.fromString(jSONObject.getString(CommonProperties.f192ID)));
        setProcessId(JSONUtils.readInteger(jSONObject, PROCESS_ID));
        setProcessName(jSONObject.optString(PROCESS_NAME, null));
        setParentProcessId(JSONUtils.readInteger(jSONObject, PARENT_PROCESS_ID));
        setParentProcessName(jSONObject.optString(PARENT_PROCESS_NAME, null));
        setErrorThreadId(JSONUtils.readLong(jSONObject, ERROR_THREAD_ID));
        setErrorThreadName(jSONObject.optString(ERROR_THREAD_NAME, null));
        setFatal(JSONUtils.readBoolean(jSONObject, FATAL));
        setAppLaunchTimestamp(JSONDateUtils.toDate(jSONObject.getString(APP_LAUNCH_TIMESTAMP)));
        setArchitecture(jSONObject.optString(ARCHITECTURE, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        JSONUtils.write(jSONStringer, CommonProperties.f192ID, getId());
        JSONUtils.write(jSONStringer, PROCESS_ID, getProcessId());
        JSONUtils.write(jSONStringer, PROCESS_NAME, getProcessName());
        JSONUtils.write(jSONStringer, PARENT_PROCESS_ID, getParentProcessId());
        JSONUtils.write(jSONStringer, PARENT_PROCESS_NAME, getParentProcessName());
        JSONUtils.write(jSONStringer, ERROR_THREAD_ID, getErrorThreadId());
        JSONUtils.write(jSONStringer, ERROR_THREAD_NAME, getErrorThreadName());
        JSONUtils.write(jSONStringer, FATAL, getFatal());
        JSONUtils.write(jSONStringer, APP_LAUNCH_TIMESTAMP, JSONDateUtils.toString(getAppLaunchTimestamp()));
        JSONUtils.write(jSONStringer, ARCHITECTURE, getArchitecture());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        AbstractErrorLog abstractErrorLog = (AbstractErrorLog) obj;
        if (this.f187id == null ? abstractErrorLog.f187id != null : !this.f187id.equals(abstractErrorLog.f187id)) {
            return false;
        }
        if (this.processId == null ? abstractErrorLog.processId != null : !this.processId.equals(abstractErrorLog.processId)) {
            return false;
        }
        if (this.processName == null ? abstractErrorLog.processName != null : !this.processName.equals(abstractErrorLog.processName)) {
            return false;
        }
        if (this.parentProcessId == null ? abstractErrorLog.parentProcessId != null : !this.parentProcessId.equals(abstractErrorLog.parentProcessId)) {
            return false;
        }
        if (this.parentProcessName == null ? abstractErrorLog.parentProcessName != null : !this.parentProcessName.equals(abstractErrorLog.parentProcessName)) {
            return false;
        }
        if (this.errorThreadId == null ? abstractErrorLog.errorThreadId != null : !this.errorThreadId.equals(abstractErrorLog.errorThreadId)) {
            return false;
        }
        if (this.errorThreadName == null ? abstractErrorLog.errorThreadName != null : !this.errorThreadName.equals(abstractErrorLog.errorThreadName)) {
            return false;
        }
        if (this.fatal == null ? abstractErrorLog.fatal != null : !this.fatal.equals(abstractErrorLog.fatal)) {
            return false;
        }
        if (this.appLaunchTimestamp == null ? abstractErrorLog.appLaunchTimestamp != null : !this.appLaunchTimestamp.equals(abstractErrorLog.appLaunchTimestamp)) {
            return false;
        }
        if (this.architecture != null) {
            z = this.architecture.equals(abstractErrorLog.architecture);
        } else if (abstractErrorLog.architecture != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((((((((((((((((super.hashCode() * 31) + (this.f187id != null ? this.f187id.hashCode() : 0)) * 31) + (this.processId != null ? this.processId.hashCode() : 0)) * 31) + (this.processName != null ? this.processName.hashCode() : 0)) * 31) + (this.parentProcessId != null ? this.parentProcessId.hashCode() : 0)) * 31) + (this.parentProcessName != null ? this.parentProcessName.hashCode() : 0)) * 31) + (this.errorThreadId != null ? this.errorThreadId.hashCode() : 0)) * 31) + (this.errorThreadName != null ? this.errorThreadName.hashCode() : 0)) * 31) + (this.fatal != null ? this.fatal.hashCode() : 0)) * 31) + (this.appLaunchTimestamp != null ? this.appLaunchTimestamp.hashCode() : 0)) * 31;
        if (this.architecture != null) {
            i = this.architecture.hashCode();
        }
        return hashCode + i;
    }
}
