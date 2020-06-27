package com.microsoft.appcenter;

import android.support.annotation.VisibleForTesting;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CustomProperties {
    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
    @VisibleForTesting
    static final int MAX_PROPERTIES_COUNT = 60;
    @VisibleForTesting
    static final int MAX_PROPERTY_KEY_LENGTH = 128;
    private static final int MAX_PROPERTY_VALUE_LENGTH = 128;
    private static final String VALUE_NULL_ERROR_MESSAGE = "Custom property value cannot be null, did you mean to call clear?";
    private final Map<String, Object> mProperties = new HashMap();

    /* access modifiers changed from: 0000 */
    public synchronized Map<String, Object> getProperties() {
        return new HashMap(this.mProperties);
    }

    public synchronized CustomProperties set(String str, String str2) {
        if (isValidKey(str) && isValidStringValue(str, str2)) {
            addProperty(str, str2);
        }
        return this;
    }

    public synchronized CustomProperties set(String str, Date date) {
        if (isValidKey(str)) {
            if (date != null) {
                addProperty(str, date);
            } else {
                AppCenterLog.error("AppCenter", VALUE_NULL_ERROR_MESSAGE);
            }
        }
        return this;
    }

    public synchronized CustomProperties set(String str, Number number) {
        if (isValidKey(str) && isValidNumberValue(str, number)) {
            addProperty(str, number);
        }
        return this;
    }

    public synchronized CustomProperties set(String str, boolean z) {
        if (isValidKey(str)) {
            addProperty(str, Boolean.valueOf(z));
        }
        return this;
    }

    public synchronized CustomProperties clear(String str) {
        if (isValidKey(str)) {
            addProperty(str, null);
        }
        return this;
    }

    private void addProperty(String str, Object obj) {
        if (this.mProperties.containsKey(str) || this.mProperties.size() < 60) {
            this.mProperties.put(str, obj);
        } else {
            AppCenterLog.error("AppCenter", "Custom properties cannot contain more than 60 items");
        }
    }

    private boolean isValidKey(String str) {
        if (str == null || !KEY_PATTERN.matcher(str).matches()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Custom property \"");
            sb.append(str);
            sb.append("\" must match \"");
            sb.append(KEY_PATTERN);
            sb.append("\"");
            AppCenterLog.error("AppCenter", sb.toString());
            return false;
        } else if (str.length() > 128) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Custom property \"");
            sb2.append(str);
            sb2.append("\" length cannot be longer than ");
            sb2.append(128);
            sb2.append(" characters.");
            AppCenterLog.error("AppCenter", sb2.toString());
            return false;
        } else {
            if (this.mProperties.containsKey(str)) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Custom property \"");
                sb3.append(str);
                sb3.append("\" is already set or cleared and will be overridden.");
                AppCenterLog.warn("AppCenter", sb3.toString());
            }
            return true;
        }
    }

    private boolean isValidStringValue(String str, String str2) {
        if (str2 == null) {
            AppCenterLog.error("AppCenter", VALUE_NULL_ERROR_MESSAGE);
            return false;
        } else if (str2.length() <= 128) {
            return true;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Custom property \"");
            sb.append(str);
            sb.append("\" value length cannot be longer than ");
            sb.append(128);
            sb.append(" characters.");
            AppCenterLog.error("AppCenter", sb.toString());
            return false;
        }
    }

    private boolean isValidNumberValue(String str, Number number) {
        if (number == null) {
            AppCenterLog.error("AppCenter", VALUE_NULL_ERROR_MESSAGE);
            return false;
        }
        double doubleValue = number.doubleValue();
        if (!Double.isInfinite(doubleValue) && !Double.isNaN(doubleValue)) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Custom property \"");
        sb.append(str);
        sb.append("\" value cannot be NaN or infinite.");
        AppCenterLog.error("AppCenter", sb.toString());
        return false;
    }
}
