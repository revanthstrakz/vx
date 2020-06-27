package okhttp3.internal.http;

import com.microsoft.appcenter.http.DefaultHttpClient;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public final class RealResponseBody extends ResponseBody {
    private final Headers headers;
    private final BufferedSource source;

    public RealResponseBody(Headers headers2, BufferedSource bufferedSource) {
        this.headers = headers2;
        this.source = bufferedSource;
    }

    public MediaType contentType() {
        String str = this.headers.get(DefaultHttpClient.CONTENT_TYPE_KEY);
        if (str != null) {
            return MediaType.parse(str);
        }
        return null;
    }

    public long contentLength() {
        return HttpHeaders.contentLength(this.headers);
    }

    public BufferedSource source() {
        return this.source;
    }
}
