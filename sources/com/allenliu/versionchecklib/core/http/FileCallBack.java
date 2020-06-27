package com.allenliu.versionchecklib.core.http;

import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class FileCallBack implements Callback {
    private Handler handler = new Handler(Looper.getMainLooper());
    private String name;
    private String path;

    public abstract void onDownloadFailed();

    public abstract void onDownloading(int i);

    public abstract void onSuccess(File file, Call call, Response response);

    public FileCallBack(String str, String str2) {
        this.path = str;
        this.name = str2;
    }

    public void onFailure(Call call, IOException iOException) {
        this.handler.post(new Runnable() {
            public void run() {
                FileCallBack.this.onDownloadFailed();
            }
        });
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x007b */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0098 A[SYNTHETIC, Splitter:B:37:0x0098] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a3 A[SYNTHETIC, Splitter:B:45:0x00a3] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00a8 A[SYNTHETIC, Splitter:B:49:0x00a8] */
    /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onResponse(final okhttp3.Call r12, final okhttp3.Response r13) throws java.io.IOException {
        /*
            r11 = this;
            r0 = 2048(0x800, float:2.87E-42)
            byte[] r0 = new byte[r0]
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r11.path
            r1.<init>(r2)
            boolean r2 = r1.exists()
            if (r2 != 0) goto L_0x0014
            r1.mkdirs()
        L_0x0014:
            r1 = 0
            okhttp3.ResponseBody r2 = r13.body()     // Catch:{ Exception -> 0x008b, all -> 0x0087 }
            java.io.InputStream r2 = r2.byteStream()     // Catch:{ Exception -> 0x008b, all -> 0x0087 }
            okhttp3.ResponseBody r3 = r13.body()     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            r3.contentLength()     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            java.lang.String r4 = r11.path     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            java.lang.String r5 = r11.name     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            r3.<init>(r4, r5)     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            boolean r4 = r3.exists()     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            if (r4 == 0) goto L_0x0037
            r3.delete()     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            goto L_0x003a
        L_0x0037:
            r3.createNewFile()     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
        L_0x003a:
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            r4.<init>(r3)     // Catch:{ Exception -> 0x0084, all -> 0x0081 }
            r5 = 0
        L_0x0041:
            int r1 = r2.read(r0)     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            r7 = -1
            if (r1 == r7) goto L_0x0069
            okhttp3.ResponseBody r7 = r13.body()     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            long r7 = r7.contentLength()     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            r9 = 0
            r4.write(r0, r9, r1)     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            long r9 = (long) r1     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            long r5 = r5 + r9
            double r9 = (double) r5     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            double r7 = (double) r7     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            double r9 = r9 / r7
            r7 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r9 = r9 * r7
            int r1 = (int) r9     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            android.os.Handler r7 = r11.handler     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            com.allenliu.versionchecklib.core.http.FileCallBack$2 r8 = new com.allenliu.versionchecklib.core.http.FileCallBack$2     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            r8.<init>(r1)     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            r7.post(r8)     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            goto L_0x0041
        L_0x0069:
            r4.flush()     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            android.os.Handler r0 = r11.handler     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            com.allenliu.versionchecklib.core.http.FileCallBack$3 r1 = new com.allenliu.versionchecklib.core.http.FileCallBack$3     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            r1.<init>(r3, r12, r13)     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            r0.post(r1)     // Catch:{ Exception -> 0x0085, all -> 0x007f }
            if (r2 == 0) goto L_0x007b
            r2.close()     // Catch:{ IOException -> 0x007b }
        L_0x007b:
            r4.close()     // Catch:{ IOException -> 0x009e }
            goto L_0x009e
        L_0x007f:
            r12 = move-exception
            goto L_0x00a1
        L_0x0081:
            r12 = move-exception
            r4 = r1
            goto L_0x00a1
        L_0x0084:
            r4 = r1
        L_0x0085:
            r1 = r2
            goto L_0x008c
        L_0x0087:
            r12 = move-exception
            r2 = r1
            r4 = r2
            goto L_0x00a1
        L_0x008b:
            r4 = r1
        L_0x008c:
            android.os.Handler r12 = r11.handler     // Catch:{ all -> 0x009f }
            com.allenliu.versionchecklib.core.http.FileCallBack$4 r13 = new com.allenliu.versionchecklib.core.http.FileCallBack$4     // Catch:{ all -> 0x009f }
            r13.<init>()     // Catch:{ all -> 0x009f }
            r12.post(r13)     // Catch:{ all -> 0x009f }
            if (r1 == 0) goto L_0x009b
            r1.close()     // Catch:{ IOException -> 0x009b }
        L_0x009b:
            if (r4 == 0) goto L_0x009e
            goto L_0x007b
        L_0x009e:
            return
        L_0x009f:
            r12 = move-exception
            r2 = r1
        L_0x00a1:
            if (r2 == 0) goto L_0x00a6
            r2.close()     // Catch:{ IOException -> 0x00a6 }
        L_0x00a6:
            if (r4 == 0) goto L_0x00ab
            r4.close()     // Catch:{ IOException -> 0x00ab }
        L_0x00ab:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.allenliu.versionchecklib.core.http.FileCallBack.onResponse(okhttp3.Call, okhttp3.Response):void");
    }
}
