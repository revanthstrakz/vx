package com.microsoft.appcenter.http;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.io.IOException;

public class HttpException extends IOException {
    private final HttpResponse mHttpResponse;

    public HttpException(@NonNull HttpResponse httpResponse) {
        super(getDetailMessage(httpResponse.getStatusCode(), httpResponse.getPayload()));
        this.mHttpResponse = httpResponse;
    }

    @NonNull
    private static String getDetailMessage(int i, @NonNull String str) {
        if (TextUtils.isEmpty(str)) {
            return String.valueOf(i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append(" - ");
        sb.append(str);
        return sb.toString();
    }

    public HttpResponse getHttpResponse() {
        return this.mHttpResponse;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.mHttpResponse.equals(((HttpException) obj).mHttpResponse);
    }

    public int hashCode() {
        return this.mHttpResponse.hashCode();
    }
}
