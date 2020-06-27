package com.microsoft.appcenter.http;

import android.net.TrafficStats;
import android.os.AsyncTask;
import com.bumptech.glide.load.Key;
import com.microsoft.appcenter.http.HttpClient.CallTemplate;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

class DefaultHttpClientCallTask extends AsyncTask<Void, Void, Object> {
    private static final int DEFAULT_STRING_BUILDER_CAPACITY = 16;
    private static final int MAX_PRETTIFY_LOG_LENGTH = 4096;
    private static final int MIN_GZIP_LENGTH = 1400;
    private static final Pattern REDIRECT_URI_REGEX_JSON = Pattern.compile("redirect_uri\":\"[^\"]+\"");
    private static final Pattern TOKEN_REGEX_JSON = Pattern.compile("token\":\"[^\"]+\"");
    private static final Pattern TOKEN_REGEX_URL_ENCODED = Pattern.compile("token=[^&]+");
    private final CallTemplate mCallTemplate;
    private final boolean mCompressionEnabled;
    private final Map<String, String> mHeaders;
    private final String mMethod;
    private final ServiceCallback mServiceCallback;
    private final Tracker mTracker;
    private final String mUrl;

    interface Tracker {
        void onFinish(DefaultHttpClientCallTask defaultHttpClientCallTask);

        void onStart(DefaultHttpClientCallTask defaultHttpClientCallTask);
    }

    DefaultHttpClientCallTask(String str, String str2, Map<String, String> map, CallTemplate callTemplate, ServiceCallback serviceCallback, Tracker tracker, boolean z) {
        this.mUrl = str;
        this.mMethod = str2;
        this.mHeaders = map;
        this.mCallTemplate = callTemplate;
        this.mServiceCallback = serviceCallback;
        this.mTracker = tracker;
        this.mCompressionEnabled = z;
    }

    private static InputStream getInputStream(HttpsURLConnection httpsURLConnection) throws IOException {
        int responseCode = httpsURLConnection.getResponseCode();
        if (responseCode < 200 || responseCode >= 400) {
            return httpsURLConnection.getErrorStream();
        }
        return httpsURLConnection.getInputStream();
    }

    private void writePayload(OutputStream outputStream, byte[] bArr) throws IOException {
        int i = 0;
        while (i < bArr.length) {
            outputStream.write(bArr, i, Math.min(bArr.length - i, 1024));
            if (!isCancelled()) {
                i += 1024;
            } else {
                return;
            }
        }
    }

    private String readResponse(HttpsURLConnection httpsURLConnection) throws IOException {
        StringBuilder sb = new StringBuilder(Math.max(httpsURLConnection.getContentLength(), 16));
        InputStream inputStream = getInputStream(httpsURLConnection);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Key.STRING_CHARSET_NAME);
            char[] cArr = new char[1024];
            do {
                int read = inputStreamReader.read(cArr);
                if (read <= 0) {
                    break;
                }
                sb.append(cArr, 0, read);
            } while (!isCancelled());
            return sb.toString();
        } finally {
            inputStream.close();
        }
    }

    private HttpResponse doHttpCall() throws Exception {
        byte[] bArr;
        String str;
        String str2;
        OutputStream outputStream;
        URL url = new URL(this.mUrl);
        HttpsURLConnection createHttpsConnection = HttpUtils.createHttpsConnection(url);
        try {
            createHttpsConnection.setRequestMethod(this.mMethod);
            boolean z = false;
            if (!this.mMethod.equals(DefaultHttpClient.METHOD_POST) || this.mCallTemplate == null) {
                str = null;
                bArr = null;
            } else {
                str = this.mCallTemplate.buildRequestBody();
                bArr = str.getBytes(Key.STRING_CHARSET_NAME);
                if (this.mCompressionEnabled && bArr.length >= MIN_GZIP_LENGTH) {
                    z = true;
                }
                if (!this.mHeaders.containsKey(DefaultHttpClient.CONTENT_TYPE_KEY)) {
                    this.mHeaders.put(DefaultHttpClient.CONTENT_TYPE_KEY, "application/json");
                }
            }
            if (z) {
                this.mHeaders.put("Content-Encoding", "gzip");
            }
            for (Entry entry : this.mHeaders.entrySet()) {
                createHttpsConnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }
            if (isCancelled()) {
                createHttpsConnection.disconnect();
                return null;
            }
            if (this.mCallTemplate != null) {
                this.mCallTemplate.onBeforeCalling(url, this.mHeaders);
            }
            if (bArr != null) {
                if (AppCenterLog.getLogLevel() <= 2) {
                    if (str.length() < 4096) {
                        str = TOKEN_REGEX_URL_ENCODED.matcher(str).replaceAll("token=***");
                        if ("application/json".equals(this.mHeaders.get(DefaultHttpClient.CONTENT_TYPE_KEY))) {
                            str = new JSONObject(str).toString(2);
                        }
                    }
                    AppCenterLog.verbose("AppCenter", str);
                }
                if (z) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bArr.length);
                    GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                    gZIPOutputStream.write(bArr);
                    gZIPOutputStream.close();
                    bArr = byteArrayOutputStream.toByteArray();
                }
                createHttpsConnection.setDoOutput(true);
                createHttpsConnection.setFixedLengthStreamingMode(bArr.length);
                outputStream = createHttpsConnection.getOutputStream();
                writePayload(outputStream, bArr);
                outputStream.close();
            }
            if (isCancelled()) {
                createHttpsConnection.disconnect();
                return null;
            }
            int responseCode = createHttpsConnection.getResponseCode();
            String readResponse = readResponse(createHttpsConnection);
            if (AppCenterLog.getLogLevel() <= 2) {
                String headerField = createHttpsConnection.getHeaderField(DefaultHttpClient.CONTENT_TYPE_KEY);
                if (headerField != null && !headerField.startsWith("text/")) {
                    if (!headerField.startsWith("application/")) {
                        str2 = "<binary>";
                        StringBuilder sb = new StringBuilder();
                        sb.append("HTTP response status=");
                        sb.append(responseCode);
                        sb.append(" payload=");
                        sb.append(str2);
                        AppCenterLog.verbose("AppCenter", sb.toString());
                    }
                }
                str2 = REDIRECT_URI_REGEX_JSON.matcher(TOKEN_REGEX_JSON.matcher(readResponse).replaceAll("token\":\"***\"")).replaceAll("redirect_uri\":\"***\"");
                StringBuilder sb2 = new StringBuilder();
                sb2.append("HTTP response status=");
                sb2.append(responseCode);
                sb2.append(" payload=");
                sb2.append(str2);
                AppCenterLog.verbose("AppCenter", sb2.toString());
            }
            HashMap hashMap = new HashMap();
            for (Entry entry2 : createHttpsConnection.getHeaderFields().entrySet()) {
                hashMap.put(entry2.getKey(), ((List) entry2.getValue()).iterator().next());
            }
            HttpResponse httpResponse = new HttpResponse(responseCode, readResponse, hashMap);
            if (responseCode < 200 || responseCode >= 300) {
                throw new HttpException(httpResponse);
            }
            createHttpsConnection.disconnect();
            return httpResponse;
        } catch (Throwable th) {
            createHttpsConnection.disconnect();
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public Object doInBackground(Void... voidArr) {
        TrafficStats.setThreadStatsTag(HttpUtils.THREAD_STATS_TAG);
        try {
            return doHttpCall();
        } catch (Exception e) {
            return e;
        } finally {
            TrafficStats.clearThreadStatsTag();
        }
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
        this.mTracker.onStart(this);
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Object obj) {
        this.mTracker.onFinish(this);
        if (obj instanceof Exception) {
            this.mServiceCallback.onCallFailed((Exception) obj);
            return;
        }
        this.mServiceCallback.onCallSucceeded((HttpResponse) obj);
    }

    /* access modifiers changed from: protected */
    public void onCancelled(Object obj) {
        if ((obj instanceof HttpResponse) || (obj instanceof HttpException)) {
            onPostExecute(obj);
        } else {
            this.mTracker.onFinish(this);
        }
    }
}
