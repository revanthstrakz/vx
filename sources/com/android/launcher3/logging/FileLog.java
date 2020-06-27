package com.android.launcher3.logging;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.Utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class FileLog {
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(3, 3);
    protected static final boolean ENABLED = Utilities.IS_DEBUG_DEVICE;
    private static final String FILE_NAME_PREFIX = "log-";
    private static final long MAX_LOG_FILE_SIZE = 4194304;
    /* access modifiers changed from: private */
    public static Handler sHandler = null;
    /* access modifiers changed from: private */
    public static File sLogsDirectory = null;

    private static class LogWriterCallback implements Callback {
        private static final long CLOSE_DELAY = 5000;
        private static final int MSG_CLOSE = 2;
        private static final int MSG_FLUSH = 3;
        private static final int MSG_WRITE = 1;
        private String mCurrentFileName;
        private PrintWriter mCurrentWriter;

        private LogWriterCallback() {
            this.mCurrentFileName = null;
            this.mCurrentWriter = null;
        }

        private void closeWriter() {
            Utilities.closeSilently(this.mCurrentWriter);
            this.mCurrentWriter = null;
        }

        public boolean handleMessage(Message message) {
            if (FileLog.sLogsDirectory == null || !FileLog.ENABLED) {
                return true;
            }
            switch (message.what) {
                case 1:
                    Calendar instance = Calendar.getInstance();
                    StringBuilder sb = new StringBuilder();
                    sb.append(FileLog.FILE_NAME_PREFIX);
                    sb.append(instance.get(6) & 1);
                    String sb2 = sb.toString();
                    if (!sb2.equals(this.mCurrentFileName)) {
                        closeWriter();
                    }
                    try {
                        if (this.mCurrentWriter == null) {
                            this.mCurrentFileName = sb2;
                            File file = new File(FileLog.sLogsDirectory, sb2);
                            boolean z = false;
                            if (file.exists()) {
                                Calendar instance2 = Calendar.getInstance();
                                instance2.setTimeInMillis(file.lastModified());
                                instance2.add(10, 36);
                                if (instance.before(instance2) && file.length() < FileLog.MAX_LOG_FILE_SIZE) {
                                    z = true;
                                }
                            }
                            this.mCurrentWriter = new PrintWriter(new FileWriter(file, z));
                        }
                        this.mCurrentWriter.println((String) message.obj);
                        this.mCurrentWriter.flush();
                        FileLog.sHandler.removeMessages(2);
                        FileLog.sHandler.sendEmptyMessageDelayed(2, CLOSE_DELAY);
                    } catch (Exception e) {
                        Log.e("FileLog", "Error writing logs to file", e);
                        closeWriter();
                    }
                    return true;
                case 2:
                    closeWriter();
                    return true;
                case 3:
                    closeWriter();
                    Pair pair = (Pair) message.obj;
                    if (pair.first != null) {
                        FileLog.dumpFile((PrintWriter) pair.first, "log-0");
                        FileLog.dumpFile((PrintWriter) pair.first, "log-1");
                    }
                    ((CountDownLatch) pair.second).countDown();
                    return true;
                default:
                    return true;
            }
        }
    }

    public static void setDir(File file) {
        if (ENABLED) {
            synchronized (DATE_FORMAT) {
                if (sHandler != null && !file.equals(sLogsDirectory)) {
                    ((HandlerThread) sHandler.getLooper().getThread()).quit();
                    sHandler = null;
                }
            }
        }
        sLogsDirectory = file;
    }

    /* renamed from: d */
    public static void m12d(String str, String str2, Exception exc) {
        Log.d(str, str2, exc);
        print(str, str2, exc);
    }

    /* renamed from: d */
    public static void m11d(String str, String str2) {
        Log.d(str, str2);
        print(str, str2);
    }

    /* renamed from: e */
    public static void m14e(String str, String str2, Exception exc) {
        Log.e(str, str2, exc);
        print(str, str2, exc);
    }

    /* renamed from: e */
    public static void m13e(String str, String str2) {
        Log.e(str, str2);
        print(str, str2);
    }

    public static void print(String str, String str2) {
        print(str, str2, null);
    }

    public static void print(String str, String str2, Exception exc) {
        if (ENABLED) {
            String format = String.format("%s %s %s", new Object[]{DATE_FORMAT.format(new Date()), str, str2});
            if (exc != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(format);
                sb.append("\n");
                sb.append(Log.getStackTraceString(exc));
                format = sb.toString();
            }
            Message.obtain(getHandler(), 1, format).sendToTarget();
        }
    }

    private static Handler getHandler() {
        synchronized (DATE_FORMAT) {
            if (sHandler == null) {
                HandlerThread handlerThread = new HandlerThread("file-logger");
                handlerThread.start();
                sHandler = new Handler(handlerThread.getLooper(), new LogWriterCallback());
            }
        }
        return sHandler;
    }

    public static void flushAll(PrintWriter printWriter) throws InterruptedException {
        if (ENABLED) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            Message.obtain(getHandler(), 3, Pair.create(printWriter, countDownLatch)).sendToTarget();
            countDownLatch.await(2, TimeUnit.SECONDS);
        }
    }

    /* access modifiers changed from: private */
    public static void dumpFile(PrintWriter printWriter, String str) {
        BufferedReader bufferedReader;
        File file = new File(sLogsDirectory, str);
        if (file.exists()) {
            try {
                bufferedReader = new BufferedReader(new FileReader(file));
                try {
                    printWriter.println();
                    StringBuilder sb = new StringBuilder();
                    sb.append("--- logfile: ");
                    sb.append(str);
                    sb.append(" ---");
                    printWriter.println(sb.toString());
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        printWriter.println(readLine);
                    }
                } catch (Exception unused) {
                } catch (Throwable th) {
                    th = th;
                    Utilities.closeSilently(bufferedReader);
                    throw th;
                }
            } catch (Exception unused2) {
                bufferedReader = null;
            } catch (Throwable th2) {
                th = th2;
                bufferedReader = null;
                Utilities.closeSilently(bufferedReader);
                throw th;
            }
            Utilities.closeSilently(bufferedReader);
        }
    }
}
