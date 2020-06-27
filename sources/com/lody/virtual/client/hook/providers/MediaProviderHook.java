package com.lody.virtual.client.hook.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import com.lody.virtual.client.NativeEngine;
import com.lody.virtual.client.hook.base.MethodBox;
import java.lang.reflect.InvocationTargetException;

class MediaProviderHook extends ProviderHook {
    private static final String COLUMN_NAME = "_data";

    MediaProviderHook(Object obj) {
        super(obj);
    }

    public Uri insert(MethodBox methodBox, Uri uri, ContentValues contentValues) throws InvocationTargetException {
        if ((!Media.INTERNAL_CONTENT_URI.equals(uri) && !Media.EXTERNAL_CONTENT_URI.equals(uri)) || Video.Media.INTERNAL_CONTENT_URI.equals(uri) || Video.Media.EXTERNAL_CONTENT_URI.equals(uri) || Images.Media.INTERNAL_CONTENT_URI.equals(uri) || Images.Media.EXTERNAL_CONTENT_URI.equals(uri)) {
            return super.insert(methodBox, uri, contentValues);
        }
        Object obj = contentValues.get(COLUMN_NAME);
        if (!(obj instanceof String)) {
            return super.insert(methodBox, uri, contentValues);
        }
        contentValues.put(COLUMN_NAME, NativeEngine.getEscapePath((String) obj));
        return super.insert(methodBox, uri, contentValues);
    }

    public Cursor query(MethodBox methodBox, Uri uri, String[] strArr, String str, String[] strArr2, String str2, Bundle bundle) throws InvocationTargetException {
        return new QueryRedirectCursor(super.query(methodBox, uri, strArr, str, strArr2, str2, bundle), COLUMN_NAME);
    }
}
