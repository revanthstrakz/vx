package com.lody.virtual.client.ipc;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.compat.ContentProviderCompat;
import java.io.Serializable;

public class ProviderCall {

    public static final class Builder {
        private String arg;
        private String auth;
        private Bundle bundle = new Bundle();
        private Context context;
        private String method;

        public Builder(Context context2, String str) {
            this.context = context2;
            this.auth = str;
        }

        public Builder methodName(String str) {
            this.method = str;
            return this;
        }

        public Builder arg(String str) {
            this.arg = str;
            return this;
        }

        public Builder addArg(String str, Object obj) {
            if (obj != null) {
                if (obj instanceof Boolean) {
                    this.bundle.putBoolean(str, ((Boolean) obj).booleanValue());
                } else if (obj instanceof Integer) {
                    this.bundle.putInt(str, ((Integer) obj).intValue());
                } else if (obj instanceof String) {
                    this.bundle.putString(str, (String) obj);
                } else if (obj instanceof Serializable) {
                    this.bundle.putSerializable(str, (Serializable) obj);
                } else if (obj instanceof Bundle) {
                    this.bundle.putBundle(str, (Bundle) obj);
                } else if (obj instanceof Parcelable) {
                    this.bundle.putParcelable(str, (Parcelable) obj);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unknown type ");
                    sb.append(obj.getClass());
                    sb.append(" in Bundle.");
                    throw new IllegalArgumentException(sb.toString());
                }
            }
            return this;
        }

        public Bundle call() {
            return ProviderCall.call(this.auth, this.context, this.method, this.arg, this.bundle);
        }
    }

    public static Bundle call(String str, String str2, String str3, Bundle bundle) {
        return call(str, VirtualCore.get().getContext(), str2, str3, bundle);
    }

    public static Bundle call(String str, Context context, String str2, String str3, Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        sb.append("content://");
        sb.append(str);
        return ContentProviderCompat.call(context, Uri.parse(sb.toString()), str2, str3, bundle);
    }
}
