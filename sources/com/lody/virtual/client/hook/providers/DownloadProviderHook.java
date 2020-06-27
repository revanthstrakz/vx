package com.lody.virtual.client.hook.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.MethodBox;
import java.lang.reflect.InvocationTargetException;

class DownloadProviderHook extends ExternalProviderHook {
    private static final String COLUMN_COOKIE_DATA = "cookiedata";
    private static final String COLUMN_IS_PUBLIC_API = "is_public_api";
    private static final String COLUMN_NOTIFICATION_CLASS = "notificationclass";
    private static final String COLUMN_NOTIFICATION_PACKAGE = "notificationpackage";
    private static final String COLUMN_OTHER_UID = "otheruid";
    private static final String[] ENFORCE_REMOVE_COLUMNS = {COLUMN_OTHER_UID, COLUMN_NOTIFICATION_CLASS};
    private static final String INSERT_KEY_PREFIX = "http_header_";
    private static final String TAG = "DownloadProviderHook";

    DownloadProviderHook(Object obj) {
        super(obj);
    }

    public Uri insert(MethodBox methodBox, Uri uri, ContentValues contentValues) throws InvocationTargetException {
        String[] strArr;
        if (contentValues.containsKey(COLUMN_NOTIFICATION_PACKAGE)) {
            contentValues.put(COLUMN_NOTIFICATION_PACKAGE, VirtualCore.get().getHostPkg());
        }
        if (contentValues.containsKey(COLUMN_COOKIE_DATA)) {
            String asString = contentValues.getAsString(COLUMN_COOKIE_DATA);
            contentValues.remove(COLUMN_COOKIE_DATA);
            int i = 0;
            while (true) {
                StringBuilder sb = new StringBuilder();
                sb.append(INSERT_KEY_PREFIX);
                sb.append(i);
                if (!contentValues.containsKey(sb.toString())) {
                    break;
                }
                i++;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(INSERT_KEY_PREFIX);
            sb2.append(i);
            String sb3 = sb2.toString();
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Cookie: ");
            sb4.append(asString);
            contentValues.put(sb3, sb4.toString());
        }
        if (!contentValues.containsKey(COLUMN_IS_PUBLIC_API)) {
            contentValues.put(COLUMN_IS_PUBLIC_API, Boolean.valueOf(true));
        }
        for (String str : ENFORCE_REMOVE_COLUMNS) {
            if (contentValues.containsKey(str)) {
                contentValues.remove(str);
            }
        }
        return super.insert(methodBox, uri, contentValues);
    }

    public Cursor query(MethodBox methodBox, Uri uri, String[] strArr, String str, String[] strArr2, String str2, Bundle bundle) throws InvocationTargetException {
        return new QueryRedirectCursor(super.query(methodBox, uri, strArr, str, strArr2, str2, bundle), "local_filename");
    }
}
