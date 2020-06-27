package okhttp3;

import com.microsoft.appcenter.Constants;
import java.nio.charset.Charset;
import okio.ByteString;

public final class Credentials {
    private Credentials() {
    }

    public static String basic(String str, String str2) {
        return basic(str, str2, Charset.forName("ISO-8859-1"));
    }

    public static String basic(String str, String str2, Charset charset) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sb.append(str2);
        String base64 = ByteString.m110of(sb.toString().getBytes(charset)).base64();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Basic ");
        sb2.append(base64);
        return sb2.toString();
    }
}
