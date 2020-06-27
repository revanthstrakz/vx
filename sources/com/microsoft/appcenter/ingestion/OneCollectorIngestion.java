package com.microsoft.appcenter.ingestion;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.http.DefaultHttpClient;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.http.HttpClient.CallTemplate;
import com.microsoft.appcenter.http.HttpUtils;
import com.microsoft.appcenter.http.ServiceCall;
import com.microsoft.appcenter.http.ServiceCallback;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.LogContainer;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.TicketCache;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class OneCollectorIngestion implements Ingestion {
    @VisibleForTesting
    static final String API_KEY = "apikey";
    private static final String CLIENT_VERSION_FORMAT = "ACS-Android-Java-no-%s-no";
    @VisibleForTesting
    static final String CLIENT_VERSION_KEY = "Client-Version";
    private static final String CONTENT_TYPE_VALUE = "application/x-json-stream; charset=utf-8";
    private static final String DEFAULT_LOG_URL = "https://mobile.events.data.microsoft.com/OneCollector/1.0";
    @VisibleForTesting
    static final String STRICT = "Strict";
    @VisibleForTesting
    static final String TICKETS = "Tickets";
    @VisibleForTesting
    static final String UPLOAD_TIME_KEY = "Upload-Time";
    private final HttpClient mHttpClient;
    private final LogSerializer mLogSerializer;
    private String mLogUrl = DEFAULT_LOG_URL;

    private static class IngestionCallTemplate implements CallTemplate {
        private final LogContainer mLogContainer;
        private final LogSerializer mLogSerializer;

        IngestionCallTemplate(LogSerializer logSerializer, LogContainer logContainer) {
            this.mLogSerializer = logSerializer;
            this.mLogContainer = logContainer;
        }

        public String buildRequestBody() throws JSONException {
            StringBuilder sb = new StringBuilder();
            for (Log serializeLog : this.mLogContainer.getLogs()) {
                sb.append(this.mLogSerializer.serializeLog(serializeLog));
                sb.append(10);
            }
            return sb.toString();
        }

        public void onBeforeCalling(URL url, Map<String, String> map) {
            if (AppCenterLog.getLogLevel() <= 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("Calling ");
                sb.append(url);
                sb.append("...");
                AppCenterLog.verbose("AppCenter", sb.toString());
                HashMap hashMap = new HashMap(map);
                String str = (String) hashMap.get(OneCollectorIngestion.API_KEY);
                if (str != null) {
                    hashMap.put(OneCollectorIngestion.API_KEY, HttpUtils.hideApiKeys(str));
                }
                String str2 = (String) hashMap.get(OneCollectorIngestion.TICKETS);
                if (str2 != null) {
                    hashMap.put(OneCollectorIngestion.TICKETS, HttpUtils.hideTickets(str2));
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Headers: ");
                sb2.append(hashMap);
                AppCenterLog.verbose("AppCenter", sb2.toString());
            }
        }
    }

    public OneCollectorIngestion(@NonNull HttpClient httpClient, @NonNull LogSerializer logSerializer) {
        this.mLogSerializer = logSerializer;
        this.mHttpClient = httpClient;
    }

    public ServiceCall sendAsync(String str, UUID uuid, LogContainer logContainer, ServiceCallback serviceCallback) throws IllegalArgumentException {
        HashMap hashMap = new HashMap();
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        for (Log transmissionTargetTokens : logContainer.getLogs()) {
            linkedHashSet.addAll(transmissionTargetTokens.getTransmissionTargetTokens());
        }
        StringBuilder sb = new StringBuilder();
        for (String append : linkedHashSet) {
            sb.append(append);
            sb.append(",");
        }
        if (!linkedHashSet.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        hashMap.put(API_KEY, sb.toString());
        JSONObject jSONObject = new JSONObject();
        for (Log log : logContainer.getLogs()) {
            List<String> ticketKeys = ((CommonSchemaLog) log).getExt().getProtocol().getTicketKeys();
            if (ticketKeys != null) {
                for (String str2 : ticketKeys) {
                    String ticket = TicketCache.getTicket(str2);
                    if (ticket != null) {
                        try {
                            jSONObject.put(str2, ticket);
                        } catch (JSONException e) {
                            AppCenterLog.error("AppCenter", "Cannot serialize tickets, sending log anonymously", e);
                        }
                    }
                }
            }
        }
        if (jSONObject.length() > 0) {
            hashMap.put(TICKETS, jSONObject.toString());
            if (Constants.APPLICATION_DEBUGGABLE) {
                hashMap.put(STRICT, Boolean.TRUE.toString());
            }
        }
        hashMap.put(DefaultHttpClient.CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
        hashMap.put(CLIENT_VERSION_KEY, String.format(CLIENT_VERSION_FORMAT, new Object[]{"3.0.0"}));
        hashMap.put(UPLOAD_TIME_KEY, String.valueOf(System.currentTimeMillis()));
        return this.mHttpClient.callAsync(this.mLogUrl, DefaultHttpClient.METHOD_POST, hashMap, new IngestionCallTemplate(this.mLogSerializer, logContainer), serviceCallback);
    }

    public void setLogUrl(@NonNull String str) {
        this.mLogUrl = str;
    }

    public void reopen() {
        this.mHttpClient.reopen();
    }

    public void close() throws IOException {
        this.mHttpClient.close();
    }
}
