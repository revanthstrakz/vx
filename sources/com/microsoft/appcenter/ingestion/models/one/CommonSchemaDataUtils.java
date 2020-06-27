package com.microsoft.appcenter.ingestion.models.one;

import android.support.annotation.VisibleForTesting;
import com.microsoft.appcenter.ingestion.models.json.JSONDateUtils;
import com.microsoft.appcenter.ingestion.models.properties.BooleanTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.DateTimeTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.DoubleTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.LongTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.TypedProperty;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonSchemaDataUtils {
    @VisibleForTesting
    static final int DATA_TYPE_DATETIME = 9;
    @VisibleForTesting
    static final int DATA_TYPE_DOUBLE = 6;
    @VisibleForTesting
    static final int DATA_TYPE_INT64 = 4;
    @VisibleForTesting
    static final String METADATA_FIELDS = "f";

    public static void addCommonSchemaData(List<TypedProperty> list, CommonSchemaLog commonSchemaLog) {
        if (list != null) {
            try {
                Data data = new Data();
                commonSchemaLog.setData(data);
                MetadataExtension metadataExtension = new MetadataExtension();
                for (TypedProperty typedProperty : list) {
                    try {
                        Object validateProperty = validateProperty(typedProperty);
                        Integer metadataType = getMetadataType(typedProperty);
                        String[] split = typedProperty.getName().split("\\.", -1);
                        int length = split.length - 1;
                        JSONObject properties = data.getProperties();
                        JSONObject metadata = metadataExtension.getMetadata();
                        for (int i = 0; i < length; i++) {
                            String str = split[i];
                            JSONObject optJSONObject = properties.optJSONObject(str);
                            if (optJSONObject == null) {
                                if (properties.has(str)) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Property key '");
                                    sb.append(str);
                                    sb.append("' already has a value, the old value will be overridden.");
                                    AppCenterLog.warn("AppCenter", sb.toString());
                                }
                                optJSONObject = new JSONObject();
                                properties.put(str, optJSONObject);
                            }
                            properties = optJSONObject;
                            metadata = addIntermediateMetadata(metadata, str);
                        }
                        String str2 = split[length];
                        if (properties.has(str2)) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Property key '");
                            sb2.append(str2);
                            sb2.append("' already has a value, the old value will be overridden.");
                            AppCenterLog.warn("AppCenter", sb2.toString());
                        }
                        properties.put(str2, validateProperty);
                        addLeafMetadata(metadataType, metadata, str2);
                    } catch (IllegalArgumentException e) {
                        AppCenterLog.warn("AppCenter", e.getMessage());
                    }
                }
                JSONObject properties2 = data.getProperties();
                String optString = properties2.optString("baseType", null);
                JSONObject optJSONObject2 = properties2.optJSONObject("baseData");
                if (optString == null && optJSONObject2 != null) {
                    AppCenterLog.warn("AppCenter", "baseData was set but baseType is missing.");
                    properties2.remove("baseData");
                    metadataExtension.getMetadata().optJSONObject(METADATA_FIELDS).remove("baseData");
                }
                if (optString != null && optJSONObject2 == null) {
                    AppCenterLog.warn("AppCenter", "baseType was set but baseData is missing.");
                    properties2.remove("baseType");
                }
                if (!cleanUpEmptyObjectsInMetadata(metadataExtension.getMetadata())) {
                    if (commonSchemaLog.getExt() == null) {
                        commonSchemaLog.setExt(new Extensions());
                    }
                    commonSchemaLog.getExt().setMetadata(metadataExtension);
                }
            } catch (JSONException unused) {
            }
        }
    }

    private static Object validateProperty(TypedProperty typedProperty) throws IllegalArgumentException, JSONException {
        Object obj;
        String name = typedProperty.getName();
        if (name == null) {
            throw new IllegalArgumentException("Property key cannot be null.");
        } else if (name.equals("baseType") && !(typedProperty instanceof StringTypedProperty)) {
            throw new IllegalArgumentException("baseType must be a string.");
        } else if (name.startsWith("baseType.")) {
            throw new IllegalArgumentException("baseType must be a string.");
        } else if (!name.equals("baseData")) {
            if (typedProperty instanceof StringTypedProperty) {
                obj = ((StringTypedProperty) typedProperty).getValue();
            } else if (typedProperty instanceof LongTypedProperty) {
                obj = Long.valueOf(((LongTypedProperty) typedProperty).getValue());
            } else if (typedProperty instanceof DoubleTypedProperty) {
                obj = Double.valueOf(((DoubleTypedProperty) typedProperty).getValue());
            } else if (typedProperty instanceof DateTimeTypedProperty) {
                obj = JSONDateUtils.toString(((DateTimeTypedProperty) typedProperty).getValue());
            } else if (typedProperty instanceof BooleanTypedProperty) {
                obj = Boolean.valueOf(((BooleanTypedProperty) typedProperty).getValue());
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unsupported property type: ");
                sb.append(typedProperty.getType());
                throw new IllegalArgumentException(sb.toString());
            }
            if (obj != null) {
                return obj;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Value of property with key '");
            sb2.append(name);
            sb2.append("' cannot be null.");
            throw new IllegalArgumentException(sb2.toString());
        } else {
            throw new IllegalArgumentException("baseData must be an object.");
        }
    }

    private static Integer getMetadataType(TypedProperty typedProperty) {
        if (typedProperty instanceof LongTypedProperty) {
            return Integer.valueOf(4);
        }
        if (typedProperty instanceof DoubleTypedProperty) {
            return Integer.valueOf(6);
        }
        if (typedProperty instanceof DateTimeTypedProperty) {
            return Integer.valueOf(9);
        }
        return null;
    }

    private static void addLeafMetadata(Integer num, JSONObject jSONObject, String str) throws JSONException {
        JSONObject optJSONObject = jSONObject.optJSONObject(METADATA_FIELDS);
        if (num != null) {
            if (optJSONObject == null) {
                optJSONObject = new JSONObject();
                jSONObject.put(METADATA_FIELDS, optJSONObject);
            }
            optJSONObject.put(str, num);
        } else if (optJSONObject != null) {
            optJSONObject.remove(str);
        }
    }

    private static JSONObject addIntermediateMetadata(JSONObject jSONObject, String str) throws JSONException {
        JSONObject optJSONObject = jSONObject.optJSONObject(METADATA_FIELDS);
        if (optJSONObject == null) {
            optJSONObject = new JSONObject();
            jSONObject.put(METADATA_FIELDS, optJSONObject);
        }
        JSONObject optJSONObject2 = optJSONObject.optJSONObject(str);
        if (optJSONObject2 != null) {
            return optJSONObject2;
        }
        JSONObject jSONObject2 = new JSONObject();
        optJSONObject.put(str, jSONObject2);
        return jSONObject2;
    }

    private static boolean cleanUpEmptyObjectsInMetadata(JSONObject jSONObject) {
        Iterator keys = jSONObject.keys();
        while (keys.hasNext()) {
            JSONObject optJSONObject = jSONObject.optJSONObject((String) keys.next());
            if (optJSONObject != null && cleanUpEmptyObjectsInMetadata(optJSONObject)) {
                keys.remove();
            }
        }
        return jSONObject.length() == 0;
    }
}
