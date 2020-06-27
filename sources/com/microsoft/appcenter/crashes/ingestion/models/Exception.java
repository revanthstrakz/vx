package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.crashes.ingestion.models.json.ExceptionFactory;
import com.microsoft.appcenter.crashes.ingestion.models.json.StackFrameFactory;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class Exception implements Model {
    private static final String INNER_EXCEPTIONS = "innerExceptions";
    private static final String MESSAGE = "message";
    private static final String MINIDUMP_FILE_PATH = "minidumpFilePath";
    private static final String STACK_TRACE = "stackTrace";
    private static final String WRAPPER_SDK_NAME = "wrapperSdkName";
    private List<StackFrame> frames;
    private List<Exception> innerExceptions;
    private String message;
    private String minidumpFilePath;
    private String stackTrace;
    private String type;
    private String wrapperSdkName;

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public void setStackTrace(String str) {
        this.stackTrace = str;
    }

    public List<StackFrame> getFrames() {
        return this.frames;
    }

    public void setFrames(List<StackFrame> list) {
        this.frames = list;
    }

    public List<Exception> getInnerExceptions() {
        return this.innerExceptions;
    }

    public void setInnerExceptions(List<Exception> list) {
        this.innerExceptions = list;
    }

    public String getWrapperSdkName() {
        return this.wrapperSdkName;
    }

    public void setWrapperSdkName(String str) {
        this.wrapperSdkName = str;
    }

    public String getMinidumpFilePath() {
        return this.minidumpFilePath;
    }

    public void setMinidumpFilePath(String str) {
        this.minidumpFilePath = str;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        setType(jSONObject.optString(CommonProperties.TYPE, null));
        setMessage(jSONObject.optString(MESSAGE, null));
        setStackTrace(jSONObject.optString(STACK_TRACE, null));
        setFrames(JSONUtils.readArray(jSONObject, CommonProperties.FRAMES, StackFrameFactory.getInstance()));
        setInnerExceptions(JSONUtils.readArray(jSONObject, INNER_EXCEPTIONS, ExceptionFactory.getInstance()));
        setWrapperSdkName(jSONObject.optString(WRAPPER_SDK_NAME, null));
        setMinidumpFilePath(jSONObject.optString(MINIDUMP_FILE_PATH, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, CommonProperties.TYPE, getType());
        JSONUtils.write(jSONStringer, MESSAGE, getMessage());
        JSONUtils.write(jSONStringer, STACK_TRACE, getStackTrace());
        JSONUtils.writeArray(jSONStringer, CommonProperties.FRAMES, getFrames());
        JSONUtils.writeArray(jSONStringer, INNER_EXCEPTIONS, getInnerExceptions());
        JSONUtils.write(jSONStringer, WRAPPER_SDK_NAME, getWrapperSdkName());
        JSONUtils.write(jSONStringer, MINIDUMP_FILE_PATH, getMinidumpFilePath());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Exception exception = (Exception) obj;
        if (this.type == null ? exception.type != null : !this.type.equals(exception.type)) {
            return false;
        }
        if (this.message == null ? exception.message != null : !this.message.equals(exception.message)) {
            return false;
        }
        if (this.stackTrace == null ? exception.stackTrace != null : !this.stackTrace.equals(exception.stackTrace)) {
            return false;
        }
        if (this.frames == null ? exception.frames != null : !this.frames.equals(exception.frames)) {
            return false;
        }
        if (this.innerExceptions == null ? exception.innerExceptions != null : !this.innerExceptions.equals(exception.innerExceptions)) {
            return false;
        }
        if (this.wrapperSdkName == null ? exception.wrapperSdkName != null : !this.wrapperSdkName.equals(exception.wrapperSdkName)) {
            return false;
        }
        if (this.minidumpFilePath != null) {
            z = this.minidumpFilePath.equals(exception.minidumpFilePath);
        } else if (exception.minidumpFilePath != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((((((((((this.type != null ? this.type.hashCode() : 0) * 31) + (this.message != null ? this.message.hashCode() : 0)) * 31) + (this.stackTrace != null ? this.stackTrace.hashCode() : 0)) * 31) + (this.frames != null ? this.frames.hashCode() : 0)) * 31) + (this.innerExceptions != null ? this.innerExceptions.hashCode() : 0)) * 31) + (this.wrapperSdkName != null ? this.wrapperSdkName.hashCode() : 0)) * 31;
        if (this.minidumpFilePath != null) {
            i = this.minidumpFilePath.hashCode();
        }
        return hashCode + i;
    }
}
