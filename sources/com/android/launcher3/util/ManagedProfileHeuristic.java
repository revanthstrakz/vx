package com.android.launcher3.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.LauncherActivityInfo;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.C0622R;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.InstallShortcutReceiver;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.SessionCommitReceiver;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.ModelWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ManagedProfileHeuristic {
    private static final long AUTO_ADD_TO_FOLDER_DURATION = 28800000;
    private static final String USER_FOLDER_ID_PREFIX = "user_folder_";

    public static class UserFolderInfo {
        final long addIconToFolderTime;
        final boolean folderAlreadyCreated;
        final String folderIdKey;
        final FolderInfo folderInfo;
        boolean folderPendingAddition;
        final ArrayList<ShortcutInfo> pendingShortcuts = new ArrayList<>();
        final SharedPreferences prefs;
        final UserHandle user;
        final long userSerial;

        public UserFolderInfo(Context context, UserHandle userHandle, BgDataModel bgDataModel) {
            this.user = userHandle;
            UserManagerCompat instance = UserManagerCompat.getInstance(context);
            this.userSerial = instance.getSerialNumberForUser(userHandle);
            this.addIconToFolderTime = instance.getUserCreationTime(userHandle) + ManagedProfileHeuristic.AUTO_ADD_TO_FOLDER_DURATION;
            StringBuilder sb = new StringBuilder();
            sb.append(ManagedProfileHeuristic.USER_FOLDER_ID_PREFIX);
            sb.append(this.userSerial);
            this.folderIdKey = sb.toString();
            this.prefs = ManagedProfileHeuristic.prefs(context);
            this.folderAlreadyCreated = this.prefs.contains(this.folderIdKey);
            if (bgDataModel == null) {
                this.folderInfo = null;
            } else if (this.folderAlreadyCreated) {
                this.folderInfo = (FolderInfo) bgDataModel.folders.get(this.prefs.getLong(this.folderIdKey, -1));
            } else {
                this.folderInfo = new FolderInfo();
                this.folderInfo.title = context.getText(C0622R.string.work_folder_name);
                this.folderInfo.setOption(2, true, null);
                this.folderPendingAddition = true;
            }
        }

        public ItemInfo convertToWorkspaceItem(ShortcutInfo shortcutInfo, LauncherActivityInfo launcherActivityInfo) {
            if (launcherActivityInfo.getFirstInstallTime() >= this.addIconToFolderTime) {
                return shortcutInfo;
            }
            if (!this.folderAlreadyCreated) {
                this.pendingShortcuts.add(shortcutInfo);
                this.folderInfo.add(shortcutInfo, false);
                if (!this.folderPendingAddition) {
                    return null;
                }
                this.folderPendingAddition = false;
                return this.folderInfo;
            } else if (this.folderInfo == null) {
                return shortcutInfo;
            } else {
                this.pendingShortcuts.add(shortcutInfo);
                return null;
            }
        }

        public void applyPendingState(ModelWriter modelWriter) {
            if (this.folderInfo != null) {
                int i = 0;
                if (this.folderAlreadyCreated) {
                    i = this.folderInfo.contents.size();
                }
                Iterator it = this.pendingShortcuts.iterator();
                while (it.hasNext()) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) it.next();
                    int i2 = i + 1;
                    shortcutInfo.rank = i;
                    modelWriter.addItemToDatabase(shortcutInfo, this.folderInfo.f52id, 0, 0, 0);
                    i = i2;
                }
                if (this.folderAlreadyCreated) {
                    new MainThreadExecutor().execute(new Runnable() {
                        public void run() {
                            UserFolderInfo.this.folderInfo.prepareAutoUpdate();
                            Iterator it = UserFolderInfo.this.pendingShortcuts.iterator();
                            while (it.hasNext()) {
                                UserFolderInfo.this.folderInfo.add((ShortcutInfo) it.next(), false);
                            }
                        }
                    });
                } else {
                    this.prefs.edit().putLong(this.folderIdKey, this.folderInfo.f52id).apply();
                }
            }
        }
    }

    public static void onAllAppsLoaded(final Context context, List<LauncherActivityInfo> list, UserHandle userHandle) {
        if (!Process.myUserHandle().equals(userHandle)) {
            UserFolderInfo userFolderInfo = new UserFolderInfo(context, userHandle, null);
            if (!userFolderInfo.folderAlreadyCreated) {
                if (!Utilities.ATLEAST_OREO || SessionCommitReceiver.isEnabled(context)) {
                    InstallShortcutReceiver.enableInstallQueue(4);
                    for (LauncherActivityInfo launcherActivityInfo : list) {
                        if (launcherActivityInfo.getFirstInstallTime() < userFolderInfo.addIconToFolderTime) {
                            InstallShortcutReceiver.queueActivityInfo(launcherActivityInfo, context);
                        }
                    }
                    new Handler(LauncherModel.getWorkerLooper()).post(new Runnable() {
                        public void run() {
                            InstallShortcutReceiver.disableAndFlushInstallQueue(4, context);
                        }
                    });
                    return;
                }
                userFolderInfo.prefs.edit().putLong(userFolderInfo.folderIdKey, -1).apply();
            }
        }
    }

    public static void processAllUsers(List<UserHandle> list, Context context) {
        UserManagerCompat instance = UserManagerCompat.getInstance(context);
        HashSet hashSet = new HashSet();
        for (UserHandle userHandle : list) {
            StringBuilder sb = new StringBuilder();
            sb.append(USER_FOLDER_ID_PREFIX);
            sb.append(instance.getSerialNumberForUser(userHandle));
            hashSet.add(sb.toString());
        }
        SharedPreferences prefs = prefs(context);
        Editor edit = prefs.edit();
        for (String str : prefs.getAll().keySet()) {
            if (!hashSet.contains(str)) {
                edit.remove(str);
            }
        }
        edit.apply();
    }

    public static void markExistingUsersForNoFolderCreation(Context context) {
        UserManagerCompat instance = UserManagerCompat.getInstance(context);
        UserHandle myUserHandle = Process.myUserHandle();
        SharedPreferences sharedPreferences = null;
        for (UserHandle userHandle : instance.getUserProfiles()) {
            if (!myUserHandle.equals(userHandle)) {
                if (sharedPreferences == null) {
                    sharedPreferences = prefs(context);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(USER_FOLDER_ID_PREFIX);
                sb.append(instance.getSerialNumberForUser(userHandle));
                String sb2 = sb.toString();
                if (!sharedPreferences.contains(sb2)) {
                    sharedPreferences.edit().putLong(sb2, -1).apply();
                }
            }
        }
    }

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(LauncherFiles.MANAGED_USER_PREFERENCES_KEY, 0);
    }
}
