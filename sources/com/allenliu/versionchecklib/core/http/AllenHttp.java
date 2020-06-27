package com.allenliu.versionchecklib.core.http;

import com.allenliu.versionchecklib.core.VersionParams;
import com.allenliu.versionchecklib.utils.ALog;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;

public class AllenHttp {
    private static OkHttpClient client;

    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        }

        private TrustAllCerts() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        public boolean verify(String str, SSLSession sSLSession) {
            return true;
        }

        private TrustAllHostnameVerifier() {
        }
    }

    public static OkHttpClient getHttpClient() {
        if (client == null) {
            Builder builder = new Builder();
            builder.sslSocketFactory(createSSLSocketFactory());
            builder.hostnameVerifier(new TrustAllHostnameVerifier());
            client = builder.build();
        }
        return client;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        try {
            SSLContext instance = SSLContext.getInstance("TLS");
            instance.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            return instance.getSocketFactory();
        } catch (Exception unused) {
            return null;
        }
    }

    private static <T extends Request.Builder> T assembleHeader(T t, VersionParams versionParams) {
        HttpHeaders httpHeaders = versionParams.getHttpHeaders();
        if (httpHeaders != null) {
            ALog.m10e("header:");
            for (Entry entry : httpHeaders.entrySet()) {
                String str = (String) entry.getKey();
                String str2 = (String) entry.getValue();
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("=");
                sb.append(str2);
                sb.append("\n");
                ALog.m10e(sb.toString());
                t.addHeader(str, str2);
            }
        }
        return t;
    }

    private static String assembleUrl(String str, HttpParams httpParams) {
        StringBuffer stringBuffer = new StringBuffer(str);
        if (httpParams != null) {
            stringBuffer.append("?");
            for (Entry entry : httpParams.entrySet()) {
                String str2 = (String) entry.getKey();
                StringBuilder sb = new StringBuilder();
                sb.append(entry.getValue());
                sb.append("");
                String sb2 = sb.toString();
                stringBuffer.append(str2);
                stringBuffer.append("=");
                stringBuffer.append(sb2);
                stringBuffer.append("&");
            }
            str = stringBuffer.substring(0, stringBuffer.length() - 1);
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("url:");
        sb3.append(str);
        ALog.m10e(sb3.toString());
        return str;
    }

    public static Request.Builder get(VersionParams versionParams) {
        Request.Builder assembleHeader = assembleHeader(new Request.Builder(), versionParams);
        assembleHeader.url(assembleUrl(versionParams.getRequestUrl(), versionParams.getRequestParams()));
        return assembleHeader;
    }

    public static Request.Builder post(VersionParams versionParams) {
        FormBody requestParams = getRequestParams(versionParams);
        Request.Builder assembleHeader = assembleHeader(new Request.Builder(), versionParams);
        assembleHeader.post(requestParams).url(versionParams.getRequestUrl());
        return assembleHeader;
    }

    public static Request.Builder postJson(VersionParams versionParams) {
        RequestBody create = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getRequestParamsJson(versionParams.getRequestParams()));
        Request.Builder assembleHeader = assembleHeader(new Request.Builder(), versionParams);
        assembleHeader.post(create).url(versionParams.getRequestUrl());
        return assembleHeader;
    }

    private static FormBody getRequestParams(VersionParams versionParams) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Entry entry : versionParams.getRequestParams().entrySet()) {
            String str = (String) entry.getKey();
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getValue());
            sb.append("");
            builder.add(str, sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("params key:");
            sb2.append((String) entry.getKey());
            sb2.append("-----value:");
            sb2.append(entry.getValue());
            ALog.m10e(sb2.toString());
        }
        return builder.build();
    }

    private static String getRequestParamsJson(HttpParams httpParams) {
        JSONObject jSONObject = new JSONObject();
        for (Entry entry : httpParams.entrySet()) {
            try {
                jSONObject.put((String) entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jSONObject2 = jSONObject.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("json:");
        sb.append(jSONObject2);
        ALog.m10e(sb.toString());
        return jSONObject2;
    }
}
