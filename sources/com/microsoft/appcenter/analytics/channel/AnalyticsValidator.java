package com.microsoft.appcenter.analytics.channel;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.analytics.ingestion.models.EventLog;
import com.microsoft.appcenter.analytics.ingestion.models.LogWithNameAndProperties;
import com.microsoft.appcenter.analytics.ingestion.models.PageLog;
import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.properties.BooleanTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.DateTimeTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.DoubleTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.LongTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.TypedProperty;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class AnalyticsValidator extends AbstractChannelListener {
    @VisibleForTesting
    static final int MAX_NAME_LENGTH = 256;
    @VisibleForTesting
    static final int MAX_PROPERTY_COUNT = 20;
    @VisibleForTesting
    static final int MAX_PROPERTY_ITEM_LENGTH = 125;

    private boolean validateLog(@NonNull LogWithNameAndProperties logWithNameAndProperties) {
        String validateName = validateName(logWithNameAndProperties.getName(), logWithNameAndProperties.getType());
        if (validateName == null) {
            return false;
        }
        Map validateProperties = validateProperties(logWithNameAndProperties.getProperties(), validateName, logWithNameAndProperties.getType());
        logWithNameAndProperties.setName(validateName);
        logWithNameAndProperties.setProperties(validateProperties);
        return true;
    }

    private boolean validateLog(@NonNull EventLog eventLog) {
        String validateName = validateName(eventLog.getName(), eventLog.getType());
        if (validateName == null) {
            return false;
        }
        validateProperties(eventLog.getTypedProperties());
        eventLog.setName(validateName);
        return true;
    }

    private static String validateName(String str, String str2) {
        if (str == null || str.isEmpty()) {
            String str3 = Analytics.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(" name cannot be null or empty.");
            AppCenterLog.error(str3, sb.toString());
            return null;
        }
        if (str.length() > 256) {
            AppCenterLog.warn(Analytics.LOG_TAG, String.format("%s '%s' : name length cannot be longer than %s characters. Name will be truncated.", new Object[]{str2, str, Integer.valueOf(256)}));
            str = str.substring(0, 256);
        }
        return str;
    }

    private static Map<String, String> validateProperties(Map<String, String> map, String str, String str2) {
        if (map == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        Iterator it = map.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Entry entry = (Entry) it.next();
            String str3 = (String) entry.getKey();
            String str4 = (String) entry.getValue();
            if (hashMap.size() >= 20) {
                AppCenterLog.warn(Analytics.LOG_TAG, String.format("%s '%s' : properties cannot contain more than %s items. Skipping other properties.", new Object[]{str2, str, Integer.valueOf(20)}));
                break;
            } else if (str3 == null || str3.isEmpty()) {
                AppCenterLog.warn(Analytics.LOG_TAG, String.format("%s '%s' : a property key cannot be null or empty. Property will be skipped.", new Object[]{str2, str}));
            } else if (str4 == null) {
                AppCenterLog.warn(Analytics.LOG_TAG, String.format("%s '%s' : property '%s' : property value cannot be null. Property '%s' will be skipped.", new Object[]{str2, str, str3, str3}));
            } else {
                if (str3.length() > 125) {
                    AppCenterLog.warn(Analytics.LOG_TAG, String.format("%s '%s' : property '%s' : property key length cannot be longer than %s characters. Property key will be truncated.", new Object[]{str2, str, str3, Integer.valueOf(125)}));
                    str3 = str3.substring(0, 125);
                }
                if (str4.length() > 125) {
                    AppCenterLog.warn(Analytics.LOG_TAG, String.format("%s '%s' : property '%s' : property value cannot be longer than %s characters. Property value will be truncated.", new Object[]{str2, str, str3, Integer.valueOf(125)}));
                    str4 = str4.substring(0, 125);
                }
                hashMap.put(str3, str4);
            }
        }
        return hashMap;
    }

    private static void validateProperties(List<TypedProperty> list) {
        boolean z;
        if (list != null) {
            ListIterator listIterator = list.listIterator();
            int i = 0;
            boolean z2 = false;
            while (listIterator.hasNext()) {
                TypedProperty typedProperty = (TypedProperty) listIterator.next();
                String name = typedProperty.getName();
                if (i >= 20) {
                    if (!z2) {
                        AppCenterLog.warn(Analytics.LOG_TAG, String.format("Typed properties cannot contain more than %s items. Skipping other properties.", new Object[]{Integer.valueOf(20)}));
                        z2 = true;
                    }
                    listIterator.remove();
                } else if (name == null || name.isEmpty()) {
                    AppCenterLog.warn(Analytics.LOG_TAG, "A typed property key cannot be null or empty. Property will be skipped.");
                    listIterator.remove();
                } else {
                    if (name.length() > 125) {
                        AppCenterLog.warn(Analytics.LOG_TAG, String.format("Typed property '%s' : property key length cannot be longer than %s characters. Property key will be truncated.", new Object[]{name, Integer.valueOf(125)}));
                        name = name.substring(0, 125);
                        typedProperty = copyProperty(typedProperty, name);
                        listIterator.set(typedProperty);
                        z = false;
                    } else {
                        z = true;
                    }
                    if (typedProperty instanceof StringTypedProperty) {
                        StringTypedProperty stringTypedProperty = (StringTypedProperty) typedProperty;
                        String value = stringTypedProperty.getValue();
                        if (value == null) {
                            AppCenterLog.warn(Analytics.LOG_TAG, String.format("Typed property '%s' : property value cannot be null. Property '%s' will be skipped.", new Object[]{name, name}));
                            listIterator.remove();
                        } else if (value.length() > 125) {
                            AppCenterLog.warn(Analytics.LOG_TAG, String.format("A String property '%s' : property value cannot be longer than %s characters. Property value will be truncated.", new Object[]{name, Integer.valueOf(125)}));
                            String substring = value.substring(0, 125);
                            if (z) {
                                StringTypedProperty stringTypedProperty2 = new StringTypedProperty();
                                stringTypedProperty2.setName(name);
                                stringTypedProperty2.setValue(substring);
                                listIterator.set(stringTypedProperty2);
                            } else {
                                stringTypedProperty.setValue(substring);
                            }
                        }
                    }
                    i++;
                }
            }
        }
    }

    private static TypedProperty copyProperty(TypedProperty typedProperty, String str) {
        TypedProperty typedProperty2;
        String type = typedProperty.getType();
        if (BooleanTypedProperty.TYPE.equals(type)) {
            BooleanTypedProperty booleanTypedProperty = new BooleanTypedProperty();
            booleanTypedProperty.setValue(((BooleanTypedProperty) typedProperty).getValue());
            typedProperty2 = booleanTypedProperty;
        } else if (DateTimeTypedProperty.TYPE.equals(type)) {
            DateTimeTypedProperty dateTimeTypedProperty = new DateTimeTypedProperty();
            dateTimeTypedProperty.setValue(((DateTimeTypedProperty) typedProperty).getValue());
            typedProperty2 = dateTimeTypedProperty;
        } else if (DoubleTypedProperty.TYPE.equals(type)) {
            DoubleTypedProperty doubleTypedProperty = new DoubleTypedProperty();
            doubleTypedProperty.setValue(((DoubleTypedProperty) typedProperty).getValue());
            typedProperty2 = doubleTypedProperty;
        } else if (LongTypedProperty.TYPE.equals(type)) {
            LongTypedProperty longTypedProperty = new LongTypedProperty();
            longTypedProperty.setValue(((LongTypedProperty) typedProperty).getValue());
            typedProperty2 = longTypedProperty;
        } else {
            StringTypedProperty stringTypedProperty = new StringTypedProperty();
            stringTypedProperty.setValue(((StringTypedProperty) typedProperty).getValue());
            typedProperty2 = stringTypedProperty;
        }
        typedProperty2.setName(str);
        return typedProperty2;
    }

    public boolean shouldFilter(@NonNull Log log) {
        if (log instanceof PageLog) {
            return !validateLog((LogWithNameAndProperties) log);
        }
        if (log instanceof EventLog) {
            return !validateLog((EventLog) log);
        }
        return false;
    }
}
