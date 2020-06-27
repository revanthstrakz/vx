package com.lody.virtual.helper;

import android.os.Build.VERSION;
import com.lody.virtual.helper.compat.BuildCompat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import mirror.dalvik.system.VMRuntime;

public class ArtDexOptimizer {

    private static class StreamConsumer {
        static final Executor STREAM_CONSUMER = Executors.newSingleThreadExecutor();

        private StreamConsumer() {
        }

        static void consumeInputStream(final InputStream inputStream) {
            STREAM_CONSUMER.execute(new Runnable() {
                /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0012 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r2 = this;
                        java.io.InputStream r0 = r2
                        if (r0 != 0) goto L_0x0005
                        return
                    L_0x0005:
                        r0 = 256(0x100, float:3.59E-43)
                        byte[] r0 = new byte[r0]
                    L_0x0009:
                        java.io.InputStream r1 = r2     // Catch:{ IOException -> 0x0012, all -> 0x0018 }
                        int r1 = r1.read(r0)     // Catch:{ IOException -> 0x0012, all -> 0x0018 }
                        if (r1 <= 0) goto L_0x0012
                        goto L_0x0009
                    L_0x0012:
                        java.io.InputStream r0 = r2     // Catch:{ Exception -> 0x001f }
                        r0.close()     // Catch:{ Exception -> 0x001f }
                        goto L_0x001f
                    L_0x0018:
                        r0 = move-exception
                        java.io.InputStream r1 = r2     // Catch:{ Exception -> 0x001e }
                        r1.close()     // Catch:{ Exception -> 0x001e }
                    L_0x001e:
                        throw r0
                    L_0x001f:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.helper.ArtDexOptimizer.StreamConsumer.C10711.run():void");
                }
            });
        }
    }

    public static void compileDex2Oat(String str, String str2) throws IOException {
        File file = new File(str2);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("dex2oat");
        if (VERSION.SDK_INT >= 24) {
            arrayList.add("--runtime-arg");
            arrayList.add("-classpath");
            arrayList.add("--runtime-arg");
            arrayList.add("&");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--dex-file=");
        sb.append(str);
        arrayList.add(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("--oat-file=");
        sb2.append(str2);
        arrayList.add(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("--instruction-set=");
        sb3.append((String) VMRuntime.getCurrentInstructionSet.call(new Object[0]));
        arrayList.add(sb3.toString());
        arrayList.add("--compiler-filter=everything");
        if (VERSION.SDK_INT >= 22 && !BuildCompat.isQ()) {
            arrayList.add("--compile-pic");
        }
        if (VERSION.SDK_INT > 25) {
            arrayList.add("--inline-max-code-units=0");
        } else if (VERSION.SDK_INT >= 23) {
            arrayList.add("--inline-depth-limit=0");
        }
        ProcessBuilder processBuilder = new ProcessBuilder(arrayList);
        processBuilder.redirectErrorStream(true);
        Process start = processBuilder.start();
        StreamConsumer.consumeInputStream(start.getInputStream());
        StreamConsumer.consumeInputStream(start.getErrorStream());
        try {
            int waitFor = start.waitFor();
            if (waitFor != 0) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append("dex2oat works unsuccessfully, exit code: ");
                sb4.append(waitFor);
                throw new IOException(sb4.toString());
            }
        } catch (InterruptedException e) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("dex2oat is interrupted, msg: ");
            sb5.append(e.getMessage());
            throw new IOException(sb5.toString(), e);
        }
    }
}
