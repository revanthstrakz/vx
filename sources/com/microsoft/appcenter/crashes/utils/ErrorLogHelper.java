package com.microsoft.appcenter.crashes.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.crashes.ingestion.models.Exception;
import com.microsoft.appcenter.crashes.ingestion.models.ManagedErrorLog;
import com.microsoft.appcenter.crashes.ingestion.models.StackFrame;
import com.microsoft.appcenter.crashes.ingestion.models.Thread;
import com.microsoft.appcenter.crashes.model.ErrorReport;
import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.DeviceInfoHelper.DeviceInfoException;
import com.microsoft.appcenter.utils.context.UserIdContext;
import com.microsoft.appcenter.utils.storage.FileManager;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class ErrorLogHelper {
    @VisibleForTesting
    static final int CAUSE_LIMIT = 16;
    private static final int CAUSE_LIMIT_HALF = 8;
    public static final String DEVICE_INFO_FILE = "deviceInfo";
    @VisibleForTesting
    static final String ERROR_DIRECTORY = "error";
    public static final String ERROR_LOG_FILE_EXTENSION = ".json";
    @VisibleForTesting
    public static final int FRAME_LIMIT = 256;
    private static final int FRAME_LIMIT_HALF = 128;
    private static final int MAX_PROPERTY_COUNT = 20;
    public static final int MAX_PROPERTY_ITEM_LENGTH = 125;
    private static final String MINIDUMP_DIRECTORY = "minidump";
    private static final String NEW_MINIDUMP_DIRECTORY = "new";
    private static final String PENDING_MINIDUMP_DIRECTORY = "pending";
    public static final String THROWABLE_FILE_EXTENSION = ".throwable";
    private static File sErrorLogDirectory;
    /* access modifiers changed from: private */
    public static File sNewMinidumpDirectory;
    private static File sPendingMinidumpDirectory;

    @NonNull
    public static ManagedErrorLog createErrorLog(@NonNull Context context, @NonNull Thread thread, @NonNull Throwable th, @NonNull Map<Thread, StackTraceElement[]> map, long j) {
        return createErrorLog(context, thread, getModelExceptionFromThrowable(th), map, j, true);
    }

    @NonNull
    public static ManagedErrorLog createErrorLog(@NonNull Context context, @NonNull Thread thread, @NonNull Exception exception, @NonNull Map<Thread, StackTraceElement[]> map, long j, boolean z) {
        ManagedErrorLog managedErrorLog = new ManagedErrorLog();
        managedErrorLog.setId(UUID.randomUUID());
        managedErrorLog.setTimestamp(new Date());
        managedErrorLog.setUserId(UserIdContext.getInstance().getUserId());
        try {
            managedErrorLog.setDevice(DeviceInfoHelper.getDeviceInfo(context));
        } catch (DeviceInfoException e) {
            AppCenterLog.error(Crashes.LOG_TAG, "Could not attach device properties snapshot to error log, will attach at sending time", e);
        }
        managedErrorLog.setProcessId(Integer.valueOf(Process.myPid()));
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ServiceManagerNative.ACTIVITY);
        if (activityManager != null) {
            List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (runningAppProcesses != null) {
                for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                    if (runningAppProcessInfo.pid == Process.myPid()) {
                        managedErrorLog.setProcessName(runningAppProcessInfo.processName);
                    }
                }
            }
        }
        if (managedErrorLog.getProcessName() == null) {
            managedErrorLog.setProcessName("");
        }
        managedErrorLog.setArchitecture(getArchitecture());
        managedErrorLog.setErrorThreadId(Long.valueOf(thread.getId()));
        managedErrorLog.setErrorThreadName(thread.getName());
        managedErrorLog.setFatal(Boolean.valueOf(z));
        managedErrorLog.setAppLaunchTimestamp(new Date(j));
        managedErrorLog.setException(exception);
        ArrayList arrayList = new ArrayList(map.size());
        for (Entry entry : map.entrySet()) {
            Thread thread2 = new Thread();
            thread2.setId(((Thread) entry.getKey()).getId());
            thread2.setName(((Thread) entry.getKey()).getName());
            thread2.setFrames(getModelFramesFromStackTrace((StackTraceElement[]) entry.getValue()));
            arrayList.add(thread2);
        }
        managedErrorLog.setThreads(arrayList);
        return managedErrorLog;
    }

    @TargetApi(21)
    private static String getArchitecture() {
        if (VERSION.SDK_INT >= 21) {
            return Build.SUPPORTED_ABIS[0];
        }
        return Build.CPU_ABI;
    }

    @NonNull
    public static synchronized File getErrorStorageDirectory() {
        File file;
        synchronized (ErrorLogHelper.class) {
            if (sErrorLogDirectory == null) {
                sErrorLogDirectory = new File(Constants.FILES_PATH, ERROR_DIRECTORY);
                FileManager.mkdir(sErrorLogDirectory.getAbsolutePath());
            }
            file = sErrorLogDirectory;
        }
        return file;
    }

    @NonNull
    public static synchronized File getNewMinidumpDirectory() {
        File file;
        synchronized (ErrorLogHelper.class) {
            file = new File(new File(getErrorStorageDirectory().getAbsolutePath(), MINIDUMP_DIRECTORY), NEW_MINIDUMP_DIRECTORY);
        }
        return file;
    }

    @NonNull
    public static synchronized File getNewMinidumpSubfolder() {
        File file;
        synchronized (ErrorLogHelper.class) {
            if (sNewMinidumpDirectory == null) {
                sNewMinidumpDirectory = new File(getNewMinidumpDirectory(), UUID.randomUUID().toString());
                FileManager.mkdir(sNewMinidumpDirectory.getPath());
            }
            file = sNewMinidumpDirectory;
        }
        return file;
    }

    @NonNull
    public static synchronized File getNewMinidumpSubfolderWithContextData(Context context) {
        File newMinidumpSubfolder;
        synchronized (ErrorLogHelper.class) {
            newMinidumpSubfolder = getNewMinidumpSubfolder();
            File file = new File(newMinidumpSubfolder, DEVICE_INFO_FILE);
            try {
                Device deviceInfo = DeviceInfoHelper.getDeviceInfo(context);
                deviceInfo.setWrapperSdkName(Constants.WRAPPER_SDK_NAME_NDK);
                JSONStringer jSONStringer = new JSONStringer();
                jSONStringer.object();
                deviceInfo.write(jSONStringer);
                jSONStringer.endObject();
                FileManager.write(file, jSONStringer.toString());
            } catch (DeviceInfoException | IOException | JSONException e) {
                AppCenterLog.error(Crashes.LOG_TAG, "Failed to store device info in a minidump folder.", e);
                file.delete();
            }
        }
        return newMinidumpSubfolder;
    }

    @NonNull
    public static synchronized File getPendingMinidumpDirectory() {
        File file;
        synchronized (ErrorLogHelper.class) {
            if (sPendingMinidumpDirectory == null) {
                sPendingMinidumpDirectory = new File(new File(getErrorStorageDirectory().getAbsolutePath(), MINIDUMP_DIRECTORY), PENDING_MINIDUMP_DIRECTORY);
                FileManager.mkdir(sPendingMinidumpDirectory.getPath());
            }
            file = sPendingMinidumpDirectory;
        }
        return file;
    }

    @NonNull
    public static File[] getStoredErrorLogFiles() {
        File[] listFiles = getErrorStorageDirectory().listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.endsWith(ErrorLogHelper.ERROR_LOG_FILE_EXTENSION);
            }
        });
        return listFiles != null ? listFiles : new File[0];
    }

    @NonNull
    public static File[] getNewMinidumpFiles() {
        File[] listFiles = getNewMinidumpDirectory().listFiles();
        return listFiles != null ? listFiles : new File[0];
    }

    @Nullable
    public static Device getStoredDeviceInfo(File file) {
        File[] listFiles = file.listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.equals(ErrorLogHelper.DEVICE_INFO_FILE);
            }
        });
        if (listFiles == null || listFiles.length == 0) {
            AppCenterLog.warn(Crashes.LOG_TAG, "No stored deviceinfo file found in a minidump folder.");
            return null;
        }
        String read = FileManager.read(listFiles[0]);
        if (read != null) {
            return parseDevice(read);
        }
        AppCenterLog.error(Crashes.LOG_TAG, "Failed to read stored device info.");
        return null;
    }

    @VisibleForTesting
    static Device parseDevice(String str) {
        try {
            Device device = new Device();
            device.read(new JSONObject(str));
            return device;
        } catch (JSONException e) {
            AppCenterLog.error(Crashes.LOG_TAG, "Failed to deserialize device info.", e);
            return null;
        }
    }

    public static void removeStaleMinidumpSubfolders() {
        File[] listFiles = getNewMinidumpDirectory().listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                if (ErrorLogHelper.sNewMinidumpDirectory != null) {
                    return !str.equals(ErrorLogHelper.sNewMinidumpDirectory.getName());
                }
                return true;
            }
        });
        if (listFiles == null || listFiles.length == 0) {
            AppCenterLog.debug(Crashes.LOG_TAG, "No previous minidump sub-folders.");
            return;
        }
        for (File deleteDir : listFiles) {
            FileManager.deleteDir(deleteDir);
        }
    }

    public static void removeMinidumpFolder() {
        FileManager.deleteDir(new File(getErrorStorageDirectory().getAbsolutePath(), MINIDUMP_DIRECTORY));
    }

    @Nullable
    public static File getLastErrorLogFile() {
        return FileManager.lastModifiedFile(getErrorStorageDirectory(), (FilenameFilter) new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.endsWith(ErrorLogHelper.ERROR_LOG_FILE_EXTENSION);
            }
        });
    }

    @Nullable
    public static File getStoredThrowableFile(@NonNull UUID uuid) {
        return getStoredFile(uuid, THROWABLE_FILE_EXTENSION);
    }

    public static void removeStoredThrowableFile(@NonNull UUID uuid) {
        File storedThrowableFile = getStoredThrowableFile(uuid);
        if (storedThrowableFile != null) {
            String str = Crashes.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Deleting throwable file ");
            sb.append(storedThrowableFile.getName());
            AppCenterLog.info(str, sb.toString());
            FileManager.delete(storedThrowableFile);
        }
    }

    @Nullable
    static File getStoredErrorLogFile(@NonNull UUID uuid) {
        return getStoredFile(uuid, ERROR_LOG_FILE_EXTENSION);
    }

    public static void removeStoredErrorLogFile(@NonNull UUID uuid) {
        File storedErrorLogFile = getStoredErrorLogFile(uuid);
        if (storedErrorLogFile != null) {
            String str = Crashes.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Deleting error log file ");
            sb.append(storedErrorLogFile.getName());
            AppCenterLog.info(str, sb.toString());
            FileManager.delete(storedErrorLogFile);
        }
    }

    @NonNull
    public static ErrorReport getErrorReportFromErrorLog(@NonNull ManagedErrorLog managedErrorLog, String str) {
        ErrorReport errorReport = new ErrorReport();
        errorReport.setId(managedErrorLog.getId().toString());
        errorReport.setThreadName(managedErrorLog.getErrorThreadName());
        errorReport.setStackTrace(str);
        errorReport.setAppStartTime(managedErrorLog.getAppLaunchTimestamp());
        errorReport.setAppErrorTime(managedErrorLog.getTimestamp());
        errorReport.setDevice(managedErrorLog.getDevice());
        return errorReport;
    }

    @VisibleForTesting
    static void setErrorLogDirectory(File file) {
        sErrorLogDirectory = file;
    }

    @Nullable
    private static File getStoredFile(@NonNull final UUID uuid, @NonNull final String str) {
        File[] listFiles = getErrorStorageDirectory().listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.startsWith(uuid.toString()) && str.endsWith(str);
            }
        });
        if (listFiles == null || listFiles.length <= 0) {
            return null;
        }
        return listFiles[0];
    }

    @NonNull
    public static Exception getModelExceptionFromThrowable(@NonNull Throwable th) {
        LinkedList<Throwable> linkedList = new LinkedList<>();
        while (th != null) {
            linkedList.add(th);
            th = th.getCause();
        }
        if (linkedList.size() > 16) {
            String str = Crashes.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Crash causes truncated from ");
            sb.append(linkedList.size());
            sb.append(" to ");
            sb.append(16);
            sb.append(" causes.");
            AppCenterLog.warn(str, sb.toString());
            linkedList.subList(8, linkedList.size() - 8).clear();
        }
        Exception exception = null;
        Exception exception2 = null;
        for (Throwable th2 : linkedList) {
            Exception exception3 = new Exception();
            exception3.setType(th2.getClass().getName());
            exception3.setMessage(th2.getMessage());
            exception3.setFrames(getModelFramesFromStackTrace(th2));
            if (exception == null) {
                exception = exception3;
            } else {
                exception2.setInnerExceptions(Collections.singletonList(exception3));
            }
            exception2 = exception3;
        }
        return exception;
    }

    @NonNull
    private static List<StackFrame> getModelFramesFromStackTrace(@NonNull Throwable th) {
        StackTraceElement[] stackTrace = th.getStackTrace();
        if (stackTrace.length > 256) {
            StackTraceElement[] stackTraceElementArr = new StackTraceElement[256];
            System.arraycopy(stackTrace, 0, stackTraceElementArr, 0, 128);
            System.arraycopy(stackTrace, stackTrace.length - 128, stackTraceElementArr, 128, 128);
            th.setStackTrace(stackTraceElementArr);
            String str = Crashes.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Crash frames truncated from ");
            sb.append(stackTrace.length);
            sb.append(" to ");
            sb.append(stackTraceElementArr.length);
            sb.append(" frames.");
            AppCenterLog.warn(str, sb.toString());
            stackTrace = stackTraceElementArr;
        }
        return getModelFramesFromStackTrace(stackTrace);
    }

    @NonNull
    private static List<StackFrame> getModelFramesFromStackTrace(@NonNull StackTraceElement[] stackTraceElementArr) {
        ArrayList arrayList = new ArrayList();
        for (StackTraceElement modelStackFrame : stackTraceElementArr) {
            arrayList.add(getModelStackFrame(modelStackFrame));
        }
        return arrayList;
    }

    @NonNull
    private static StackFrame getModelStackFrame(StackTraceElement stackTraceElement) {
        StackFrame stackFrame = new StackFrame();
        stackFrame.setClassName(stackTraceElement.getClassName());
        stackFrame.setMethodName(stackTraceElement.getMethodName());
        stackFrame.setLineNumber(Integer.valueOf(stackTraceElement.getLineNumber()));
        stackFrame.setFileName(stackTraceElement.getFileName());
        return stackFrame;
    }

    public static Map<String, String> validateProperties(Map<String, String> map, String str) {
        if (map == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        Iterator it = map.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Entry entry = (Entry) it.next();
            String str2 = (String) entry.getKey();
            String str3 = (String) entry.getValue();
            if (hashMap.size() >= 20) {
                AppCenterLog.warn(Crashes.LOG_TAG, String.format("%s : properties cannot contain more than %s items. Skipping other properties.", new Object[]{str, Integer.valueOf(20)}));
                break;
            } else if (str2 == null || str2.isEmpty()) {
                AppCenterLog.warn(Crashes.LOG_TAG, String.format("%s : a property key cannot be null or empty. Property will be skipped.", new Object[]{str}));
            } else if (str3 == null) {
                AppCenterLog.warn(Crashes.LOG_TAG, String.format("%s : property '%s' : property value cannot be null. Property '%s' will be skipped.", new Object[]{str, str2, str2}));
            } else {
                if (str2.length() > 125) {
                    AppCenterLog.warn(Crashes.LOG_TAG, String.format("%s : property '%s' : property key length cannot be longer than %s characters. Property key will be truncated.", new Object[]{str, str2, Integer.valueOf(MAX_PROPERTY_ITEM_LENGTH)}));
                    str2 = str2.substring(0, MAX_PROPERTY_ITEM_LENGTH);
                }
                if (str3.length() > 125) {
                    AppCenterLog.warn(Crashes.LOG_TAG, String.format("%s : property '%s' : property value cannot be longer than %s characters. Property value will be truncated.", new Object[]{str, str2, Integer.valueOf(MAX_PROPERTY_ITEM_LENGTH)}));
                    str3 = str3.substring(0, MAX_PROPERTY_ITEM_LENGTH);
                }
                hashMap.put(str2, str3);
            }
        }
        return hashMap;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001a  */
    @android.support.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.UUID parseLogFolderUuid(java.io.File r2) {
        /*
            boolean r0 = r2.isDirectory()
            if (r0 == 0) goto L_0x0017
            java.lang.String r2 = r2.getName()     // Catch:{ IllegalArgumentException -> 0x000f }
            java.util.UUID r2 = java.util.UUID.fromString(r2)     // Catch:{ IllegalArgumentException -> 0x000f }
            goto L_0x0018
        L_0x000f:
            r2 = move-exception
            java.lang.String r0 = "AppCenterCrashes"
            java.lang.String r1 = "Cannot parse minidump folder name to UUID."
            com.microsoft.appcenter.utils.AppCenterLog.warn(r0, r1, r2)
        L_0x0017:
            r2 = 0
        L_0x0018:
            if (r2 != 0) goto L_0x001e
            java.util.UUID r2 = java.util.UUID.randomUUID()
        L_0x001e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.crashes.utils.ErrorLogHelper.parseLogFolderUuid(java.io.File):java.util.UUID");
    }

    @VisibleForTesting
    public static void clearStaticState() {
        sNewMinidumpDirectory = null;
        sErrorLogDirectory = null;
        sPendingMinidumpDirectory = null;
    }
}
