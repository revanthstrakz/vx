package com.microsoft.appcenter.analytics;

import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.HashUtils;
import com.microsoft.appcenter.utils.TicketCache;
import java.util.Date;

public class AuthenticationProvider {
    private static final long REFRESH_THRESHOLD = 600000;
    private AuthenticationCallback mCallback;
    private Date mExpiryDate;
    private final String mTicketKey;
    private final String mTicketKeyHash;
    private final TokenProvider mTokenProvider;
    private final Type mType;

    public interface AuthenticationCallback {
        void onAuthenticationResult(String str, Date date);
    }

    public interface TokenProvider {
        void acquireToken(String str, AuthenticationCallback authenticationCallback);
    }

    public enum Type {
        MSA_COMPACT("p"),
        MSA_DELEGATE("d");
        
        /* access modifiers changed from: private */
        public final String mTokenPrefix;

        private Type(String str) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
            this.mTokenPrefix = sb.toString();
        }
    }

    public AuthenticationProvider(Type type, String str, TokenProvider tokenProvider) {
        String str2;
        this.mType = type;
        this.mTicketKey = str;
        if (str == null) {
            str2 = null;
        } else {
            str2 = HashUtils.sha256(str);
        }
        this.mTicketKeyHash = str2;
        this.mTokenProvider = tokenProvider;
    }

    /* access modifiers changed from: 0000 */
    public Type getType() {
        return this.mType;
    }

    /* access modifiers changed from: 0000 */
    public String getTicketKey() {
        return this.mTicketKey;
    }

    /* access modifiers changed from: 0000 */
    public String getTicketKeyHash() {
        return this.mTicketKeyHash;
    }

    /* access modifiers changed from: 0000 */
    public TokenProvider getTokenProvider() {
        return this.mTokenProvider;
    }

    /* access modifiers changed from: 0000 */
    public synchronized void acquireTokenAsync() {
        if (this.mCallback == null) {
            String str = Analytics.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Calling token provider=");
            sb.append(this.mType);
            sb.append(" callback.");
            AppCenterLog.debug(str, sb.toString());
            this.mCallback = new AuthenticationCallback() {
                public void onAuthenticationResult(String str, Date date) {
                    AuthenticationProvider.this.handleTokenUpdate(str, date, this);
                }
            };
            this.mTokenProvider.acquireToken(this.mTicketKey, this.mCallback);
        }
    }

    /* access modifiers changed from: private */
    public synchronized void handleTokenUpdate(String str, Date date, AuthenticationCallback authenticationCallback) {
        if (this.mCallback != authenticationCallback) {
            String str2 = Analytics.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Ignore duplicate authentication callback calls, provider=");
            sb.append(this.mType);
            AppCenterLog.debug(str2, sb.toString());
            return;
        }
        this.mCallback = null;
        String str3 = Analytics.LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Got result back from token provider=");
        sb2.append(this.mType);
        AppCenterLog.debug(str3, sb2.toString());
        if (str == null) {
            String str4 = Analytics.LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Authentication failed for ticketKey=");
            sb3.append(this.mTicketKey);
            AppCenterLog.error(str4, sb3.toString());
        } else if (date == null) {
            String str5 = Analytics.LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("No expiry date provided for ticketKey=");
            sb4.append(this.mTicketKey);
            AppCenterLog.error(str5, sb4.toString());
        } else {
            String str6 = this.mTicketKeyHash;
            StringBuilder sb5 = new StringBuilder();
            sb5.append(this.mType.mTokenPrefix);
            sb5.append(str);
            TicketCache.putTicket(str6, sb5.toString());
            this.mExpiryDate = date;
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized void checkTokenExpiry() {
        if (this.mExpiryDate != null && this.mExpiryDate.getTime() <= System.currentTimeMillis() + REFRESH_THRESHOLD) {
            acquireTokenAsync();
        }
    }
}
