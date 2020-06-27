package com.lody.virtual.helper.compat;

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;

public class ContentProviderCompat {
    public static Bundle call(Context context, Uri uri, String str, String str2, Bundle bundle) {
        if (VERSION.SDK_INT < 17) {
            return context.getContentResolver().call(uri, str, str2, bundle);
        }
        ContentProviderClient crazyAcquireContentProvider = crazyAcquireContentProvider(context, uri);
        Bundle bundle2 = null;
        try {
            Bundle call = crazyAcquireContentProvider.call(str, str2, bundle);
            releaseQuietly(crazyAcquireContentProvider);
            bundle2 = call;
        } catch (RemoteException e) {
            e.printStackTrace();
            releaseQuietly(crazyAcquireContentProvider);
        } catch (Throwable th) {
            releaseQuietly(crazyAcquireContentProvider);
            throw th;
        }
        return bundle2;
    }

    private static ContentProviderClient acquireContentProviderClient(Context context, Uri uri) {
        if (VERSION.SDK_INT >= 16) {
            return context.getContentResolver().acquireUnstableContentProviderClient(uri);
        }
        return context.getContentResolver().acquireContentProviderClient(uri);
    }

    public static ContentProviderClient crazyAcquireContentProvider(Context context, Uri uri) {
        ContentProviderClient acquireContentProviderClient = acquireContentProviderClient(context, uri);
        if (acquireContentProviderClient == null) {
            int i = 0;
            while (i < 5 && acquireContentProviderClient == null) {
                SystemClock.sleep(100);
                i++;
                acquireContentProviderClient = acquireContentProviderClient(context, uri);
            }
        }
        return acquireContentProviderClient;
    }

    public static ContentProviderClient crazyAcquireContentProvider(Context context, String str) {
        ContentProviderClient acquireContentProviderClient = acquireContentProviderClient(context, str);
        if (acquireContentProviderClient == null) {
            int i = 0;
            while (i < 5 && acquireContentProviderClient == null) {
                SystemClock.sleep(100);
                i++;
                acquireContentProviderClient = acquireContentProviderClient(context, str);
            }
        }
        return acquireContentProviderClient;
    }

    private static ContentProviderClient acquireContentProviderClient(Context context, String str) {
        if (VERSION.SDK_INT >= 16) {
            return context.getContentResolver().acquireUnstableContentProviderClient(str);
        }
        return context.getContentResolver().acquireContentProviderClient(str);
    }

    public static void releaseQuietly(ContentProviderClient contentProviderClient) {
        if (contentProviderClient != null) {
            try {
                if (VERSION.SDK_INT >= 24) {
                    contentProviderClient.close();
                } else {
                    contentProviderClient.release();
                }
            } catch (Exception unused) {
            }
        }
    }
}
