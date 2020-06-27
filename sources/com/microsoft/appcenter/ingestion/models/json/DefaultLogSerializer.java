package com.microsoft.appcenter.ingestion.models.json;

import android.support.annotation.NonNull;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.LogContainer;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class DefaultLogSerializer implements LogSerializer {
    private static final String LOGS = "logs";
    private final Map<String, LogFactory> mLogFactories = new HashMap();

    @NonNull
    private JSONStringer writeLog(JSONStringer jSONStringer, Log log) throws JSONException {
        jSONStringer.object();
        log.write(jSONStringer);
        jSONStringer.endObject();
        return jSONStringer;
    }

    @NonNull
    private Log readLog(JSONObject jSONObject, String str) throws JSONException {
        if (str == null) {
            str = jSONObject.getString(CommonProperties.TYPE);
        }
        LogFactory logFactory = (LogFactory) this.mLogFactories.get(str);
        if (logFactory != null) {
            Log create = logFactory.create();
            create.read(jSONObject);
            return create;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown log type: ");
        sb.append(str);
        throw new JSONException(sb.toString());
    }

    @NonNull
    public String serializeLog(@NonNull Log log) throws JSONException {
        return writeLog(new JSONStringer(), log).toString();
    }

    @NonNull
    public Log deserializeLog(@NonNull String str, String str2) throws JSONException {
        return readLog(new JSONObject(str), str2);
    }

    public Collection<CommonSchemaLog> toCommonSchemaLog(@NonNull Log log) {
        return ((LogFactory) this.mLogFactories.get(log.getType())).toCommonSchemaLogs(log);
    }

    @NonNull
    public String serializeContainer(@NonNull LogContainer logContainer) throws JSONException {
        JSONStringer jSONStringer = new JSONStringer();
        jSONStringer.object();
        jSONStringer.key(LOGS).array();
        for (Log writeLog : logContainer.getLogs()) {
            writeLog(jSONStringer, writeLog);
        }
        jSONStringer.endArray();
        jSONStringer.endObject();
        return jSONStringer.toString();
    }

    @NonNull
    public LogContainer deserializeContainer(@NonNull String str, String str2) throws JSONException {
        JSONObject jSONObject = new JSONObject(str);
        LogContainer logContainer = new LogContainer();
        JSONArray jSONArray = jSONObject.getJSONArray(LOGS);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(readLog(jSONArray.getJSONObject(i), str2));
        }
        logContainer.setLogs(arrayList);
        return logContainer;
    }

    public void addLogFactory(@NonNull String str, @NonNull LogFactory logFactory) {
        this.mLogFactories.put(str, logFactory);
    }
}
