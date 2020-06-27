package com.microsoft.appcenter.http;

import android.support.annotation.VisibleForTesting;
import java.io.IOException;

public abstract class HttpClientDecorator implements HttpClient {
    final HttpClient mDecoratedApi;

    HttpClientDecorator(HttpClient httpClient) {
        this.mDecoratedApi = httpClient;
    }

    public void close() throws IOException {
        this.mDecoratedApi.close();
    }

    public void reopen() {
        this.mDecoratedApi.reopen();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public HttpClient getDecoratedApi() {
        return this.mDecoratedApi;
    }
}
