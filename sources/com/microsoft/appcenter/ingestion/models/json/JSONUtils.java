package com.microsoft.appcenter.ingestion.models.json;

import android.support.annotation.VisibleForTesting;
import com.microsoft.appcenter.ingestion.models.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class JSONUtils {
    @VisibleForTesting
    JSONUtils() {
    }

    public static Integer readInteger(JSONObject jSONObject, String str) throws JSONException {
        if (jSONObject.has(str)) {
            return Integer.valueOf(jSONObject.getInt(str));
        }
        return null;
    }

    public static Long readLong(JSONObject jSONObject, String str) throws JSONException {
        if (jSONObject.has(str)) {
            return Long.valueOf(jSONObject.getLong(str));
        }
        return null;
    }

    public static Boolean readBoolean(JSONObject jSONObject, String str) throws JSONException {
        if (jSONObject.has(str)) {
            return Boolean.valueOf(jSONObject.getBoolean(str));
        }
        return null;
    }

    public static Map<String, String> readMap(JSONObject jSONObject, String str) throws JSONException {
        JSONObject optJSONObject = jSONObject.optJSONObject(str);
        if (optJSONObject == null) {
            return null;
        }
        HashMap hashMap = new HashMap(optJSONObject.length());
        Iterator keys = optJSONObject.keys();
        while (keys.hasNext()) {
            String str2 = (String) keys.next();
            hashMap.put(str2, optJSONObject.getString(str2));
        }
        return hashMap;
    }

    public static <M extends Model> List<M> readArray(JSONObject jSONObject, String str, ModelFactory<M> modelFactory) throws JSONException {
        JSONArray optJSONArray = jSONObject.optJSONArray(str);
        if (optJSONArray == null) {
            return null;
        }
        List<M> createList = modelFactory.createList(optJSONArray.length());
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject jSONObject2 = optJSONArray.getJSONObject(i);
            Model create = modelFactory.create();
            create.read(jSONObject2);
            createList.add(create);
        }
        return createList;
    }

    public static List<String> readStringArray(JSONObject jSONObject, String str) throws JSONException {
        JSONArray optJSONArray = jSONObject.optJSONArray(str);
        if (optJSONArray == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(optJSONArray.length());
        for (int i = 0; i < optJSONArray.length(); i++) {
            arrayList.add(optJSONArray.getString(i));
        }
        return arrayList;
    }

    public static void write(JSONStringer jSONStringer, String str, Object obj) throws JSONException {
        if (obj != null) {
            jSONStringer.key(str).value(obj);
        }
    }

    public static void writeMap(JSONStringer jSONStringer, String str, Map<String, String> map) throws JSONException {
        if (map != null) {
            jSONStringer.key(str).object();
            for (Entry entry : map.entrySet()) {
                jSONStringer.key((String) entry.getKey()).value(entry.getValue());
            }
            jSONStringer.endObject();
        }
    }

    public static void writeArray(JSONStringer jSONStringer, String str, List<? extends Model> list) throws JSONException {
        if (list != null) {
            jSONStringer.key(str).array();
            for (Model model : list) {
                jSONStringer.object();
                model.write(jSONStringer);
                jSONStringer.endObject();
            }
            jSONStringer.endArray();
        }
    }

    public static void writeStringArray(JSONStringer jSONStringer, String str, List<String> list) throws JSONException {
        if (list != null) {
            jSONStringer.key(str).array();
            for (String value : list) {
                jSONStringer.value(value);
            }
            jSONStringer.endArray();
        }
    }
}
