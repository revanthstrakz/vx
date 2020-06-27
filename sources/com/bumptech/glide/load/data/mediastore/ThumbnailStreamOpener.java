package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

class ThumbnailStreamOpener {
    private static final FileService DEFAULT_SERVICE = new FileService();
    private static final String TAG = "ThumbStreamOpener";
    private final ArrayPool byteArrayPool;
    private final ContentResolver contentResolver;
    private final List<ImageHeaderParser> parsers;
    private final ThumbnailQuery query;
    private final FileService service;

    ThumbnailStreamOpener(List<ImageHeaderParser> list, ThumbnailQuery thumbnailQuery, ArrayPool arrayPool, ContentResolver contentResolver2) {
        this(list, DEFAULT_SERVICE, thumbnailQuery, arrayPool, contentResolver2);
    }

    ThumbnailStreamOpener(List<ImageHeaderParser> list, FileService fileService, ThumbnailQuery thumbnailQuery, ArrayPool arrayPool, ContentResolver contentResolver2) {
        this.service = fileService;
        this.query = thumbnailQuery;
        this.byteArrayPool = arrayPool;
        this.contentResolver = contentResolver2;
        this.parsers = list;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0027 A[Catch:{ all -> 0x0044 }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x003f A[SYNTHETIC, Splitter:B:21:0x003f] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0047 A[SYNTHETIC, Splitter:B:28:0x0047] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getOrientation(android.net.Uri r7) {
        /*
            r6 = this;
            r0 = 0
            android.content.ContentResolver r1 = r6.contentResolver     // Catch:{ IOException | NullPointerException -> 0x001a, all -> 0x0017 }
            java.io.InputStream r1 = r1.openInputStream(r7)     // Catch:{ IOException | NullPointerException -> 0x001a, all -> 0x0017 }
            java.util.List<com.bumptech.glide.load.ImageHeaderParser> r0 = r6.parsers     // Catch:{ IOException | NullPointerException -> 0x0015 }
            com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool r2 = r6.byteArrayPool     // Catch:{ IOException | NullPointerException -> 0x0015 }
            int r0 = com.bumptech.glide.load.ImageHeaderParserUtils.getOrientation(r0, r1, r2)     // Catch:{ IOException | NullPointerException -> 0x0015 }
            if (r1 == 0) goto L_0x0014
            r1.close()     // Catch:{ IOException -> 0x0014 }
        L_0x0014:
            return r0
        L_0x0015:
            r0 = move-exception
            goto L_0x001e
        L_0x0017:
            r7 = move-exception
            r1 = r0
            goto L_0x0045
        L_0x001a:
            r1 = move-exception
            r5 = r1
            r1 = r0
            r0 = r5
        L_0x001e:
            java.lang.String r2 = "ThumbStreamOpener"
            r3 = 3
            boolean r2 = android.util.Log.isLoggable(r2, r3)     // Catch:{ all -> 0x0044 }
            if (r2 == 0) goto L_0x003d
            java.lang.String r2 = "ThumbStreamOpener"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0044 }
            r3.<init>()     // Catch:{ all -> 0x0044 }
            java.lang.String r4 = "Failed to open uri: "
            r3.append(r4)     // Catch:{ all -> 0x0044 }
            r3.append(r7)     // Catch:{ all -> 0x0044 }
            java.lang.String r7 = r3.toString()     // Catch:{ all -> 0x0044 }
            android.util.Log.d(r2, r7, r0)     // Catch:{ all -> 0x0044 }
        L_0x003d:
            if (r1 == 0) goto L_0x0042
            r1.close()     // Catch:{ IOException -> 0x0042 }
        L_0x0042:
            r7 = -1
            return r7
        L_0x0044:
            r7 = move-exception
        L_0x0045:
            if (r1 == 0) goto L_0x004a
            r1.close()     // Catch:{ IOException -> 0x004a }
        L_0x004a:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.data.mediastore.ThumbnailStreamOpener.getOrientation(android.net.Uri):int");
    }

    public InputStream open(Uri uri) throws FileNotFoundException {
        String path = getPath(uri);
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = this.service.get(path);
        if (!isValid(file)) {
            return null;
        }
        Uri fromFile = Uri.fromFile(file);
        try {
            return this.contentResolver.openInputStream(fromFile);
        } catch (NullPointerException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("NPE opening uri: ");
            sb.append(uri);
            sb.append(" -> ");
            sb.append(fromFile);
            throw ((FileNotFoundException) new FileNotFoundException(sb.toString()).initCause(e));
        }
    }

    /* JADX INFO: finally extract failed */
    @Nullable
    private String getPath(@NonNull Uri uri) {
        Cursor query2 = this.query.query(uri);
        if (query2 != null) {
            try {
                if (query2.moveToFirst()) {
                    String string = query2.getString(0);
                    if (query2 != null) {
                        query2.close();
                    }
                    return string;
                }
            } catch (Throwable th) {
                if (query2 != null) {
                    query2.close();
                }
                throw th;
            }
        }
        if (query2 != null) {
            query2.close();
        }
        return null;
    }

    private boolean isValid(File file) {
        return this.service.exists(file) && 0 < this.service.length(file);
    }
}
