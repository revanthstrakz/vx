package com.microsoft.appcenter.crashes.ingestion.models;

import com.microsoft.appcenter.ingestion.models.Model;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class StackFrame implements Model {
    private static final String CLASS_NAME = "className";
    private static final String FILE_NAME = "fileName";
    private static final String LINE_NUMBER = "lineNumber";
    private static final String METHOD_NAME = "methodName";
    private String className;
    private String fileName;
    private Integer lineNumber;
    private String methodName;

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String str) {
        this.className = str;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String str) {
        this.methodName = str;
    }

    public Integer getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(Integer num) {
        this.lineNumber = num;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        setClassName(jSONObject.optString(CLASS_NAME, null));
        setMethodName(jSONObject.optString(METHOD_NAME, null));
        setLineNumber(JSONUtils.readInteger(jSONObject, LINE_NUMBER));
        setFileName(jSONObject.optString(FILE_NAME, null));
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        JSONUtils.write(jSONStringer, CLASS_NAME, getClassName());
        JSONUtils.write(jSONStringer, METHOD_NAME, getMethodName());
        JSONUtils.write(jSONStringer, LINE_NUMBER, getLineNumber());
        JSONUtils.write(jSONStringer, FILE_NAME, getFileName());
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StackFrame stackFrame = (StackFrame) obj;
        if (this.className == null ? stackFrame.className != null : !this.className.equals(stackFrame.className)) {
            return false;
        }
        if (this.methodName == null ? stackFrame.methodName != null : !this.methodName.equals(stackFrame.methodName)) {
            return false;
        }
        if (this.lineNumber == null ? stackFrame.lineNumber != null : !this.lineNumber.equals(stackFrame.lineNumber)) {
            return false;
        }
        if (this.fileName != null) {
            z = this.fileName.equals(stackFrame.fileName);
        } else if (stackFrame.fileName != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((((this.className != null ? this.className.hashCode() : 0) * 31) + (this.methodName != null ? this.methodName.hashCode() : 0)) * 31) + (this.lineNumber != null ? this.lineNumber.hashCode() : 0)) * 31;
        if (this.fileName != null) {
            i = this.fileName.hashCode();
        }
        return hashCode + i;
    }
}
