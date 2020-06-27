package com.microsoft.appcenter.crashes.ingestion.models;

import android.support.annotation.VisibleForTesting;
import android.util.Base64;
import com.bumptech.glide.load.Key;
import com.microsoft.appcenter.ingestion.models.AbstractLog;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.json.JSONUtils;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class ErrorAttachmentLog extends AbstractLog {
    @VisibleForTesting
    static final Charset CHARSET = Charset.forName(Key.STRING_CHARSET_NAME);
    private static final String CONTENT_TYPE = "contentType";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    @VisibleForTesting
    static final String DATA = "data";
    private static final String ERROR_ID = "errorId";
    private static final String FILE_NAME = "fileName";
    public static final String TYPE = "errorAttachment";
    private String contentType;
    private byte[] data;
    private UUID errorId;
    private String fileName;

    /* renamed from: id */
    private UUID f188id;

    public String getType() {
        return TYPE;
    }

    public static ErrorAttachmentLog attachmentWithText(String str, String str2) {
        return attachmentWithBinary(str.getBytes(CHARSET), str2, CONTENT_TYPE_TEXT_PLAIN);
    }

    public static ErrorAttachmentLog attachmentWithBinary(byte[] bArr, String str, String str2) {
        ErrorAttachmentLog errorAttachmentLog = new ErrorAttachmentLog();
        errorAttachmentLog.setData(bArr);
        errorAttachmentLog.setFileName(str);
        errorAttachmentLog.setContentType(str2);
        return errorAttachmentLog;
    }

    public UUID getId() {
        return this.f188id;
    }

    public void setId(UUID uuid) {
        this.f188id = uuid;
    }

    public UUID getErrorId() {
        return this.errorId;
    }

    public void setErrorId(UUID uuid) {
        this.errorId = uuid;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String str) {
        this.contentType = str;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public boolean isValid() {
        return (getId() == null || getErrorId() == null || getContentType() == null || getData() == null) ? false : true;
    }

    public void read(JSONObject jSONObject) throws JSONException {
        super.read(jSONObject);
        setId(UUID.fromString(jSONObject.getString(CommonProperties.f192ID)));
        setErrorId(UUID.fromString(jSONObject.getString(ERROR_ID)));
        setContentType(jSONObject.getString(CONTENT_TYPE));
        setFileName(jSONObject.optString(FILE_NAME, null));
        try {
            setData(Base64.decode(jSONObject.getString(DATA), 0));
        } catch (IllegalArgumentException e) {
            throw new JSONException(e.getMessage());
        }
    }

    public void write(JSONStringer jSONStringer) throws JSONException {
        super.write(jSONStringer);
        JSONUtils.write(jSONStringer, CommonProperties.f192ID, getId());
        JSONUtils.write(jSONStringer, ERROR_ID, getErrorId());
        JSONUtils.write(jSONStringer, CONTENT_TYPE, getContentType());
        JSONUtils.write(jSONStringer, FILE_NAME, getFileName());
        JSONUtils.write(jSONStringer, DATA, Base64.encodeToString(getData(), 2));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        ErrorAttachmentLog errorAttachmentLog = (ErrorAttachmentLog) obj;
        if (this.f188id == null ? errorAttachmentLog.f188id != null : !this.f188id.equals(errorAttachmentLog.f188id)) {
            return false;
        }
        if (this.errorId == null ? errorAttachmentLog.errorId != null : !this.errorId.equals(errorAttachmentLog.errorId)) {
            return false;
        }
        if (this.contentType == null ? errorAttachmentLog.contentType != null : !this.contentType.equals(errorAttachmentLog.contentType)) {
            return false;
        }
        if (this.fileName == null ? errorAttachmentLog.fileName == null : this.fileName.equals(errorAttachmentLog.fileName)) {
            return Arrays.equals(this.data, errorAttachmentLog.data);
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((((super.hashCode() * 31) + (this.f188id != null ? this.f188id.hashCode() : 0)) * 31) + (this.errorId != null ? this.errorId.hashCode() : 0)) * 31) + (this.contentType != null ? this.contentType.hashCode() : 0)) * 31;
        if (this.fileName != null) {
            i = this.fileName.hashCode();
        }
        return ((hashCode + i) * 31) + Arrays.hashCode(this.data);
    }
}
