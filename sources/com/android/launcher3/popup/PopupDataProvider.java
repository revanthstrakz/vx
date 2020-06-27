package com.android.launcher3.popup;

import android.content.ComponentName;
import android.content.pm.LauncherApps;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.notification.NotificationListener.NotificationsChangedListener;
import com.android.launcher3.popup.SystemShortcut.AppInfo;
import com.android.launcher3.popup.SystemShortcut.ClearApp;
import com.android.launcher3.popup.SystemShortcut.CreateDesktopShortcut;
import com.android.launcher3.popup.SystemShortcut.Custom;
import com.android.launcher3.popup.SystemShortcut.KillApp;
import com.android.launcher3.popup.SystemShortcut.Widgets;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.DeepShortcutManagerBackport;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PopupDataProvider implements NotificationsChangedListener {
    private static final boolean LOGD = false;
    private static final String TAG = "PopupDataProvider";
    private MultiHashMap<ComponentKey, String> mDeepShortcutMap = new MultiHashMap<>();
    private final Launcher mLauncher;
    private Map<PackageUserKey, BadgeInfo> mPackageUserToBadgeInfos = new HashMap();
    private final SystemShortcut[] mSystemShortcuts;

    public PopupDataProvider(Launcher launcher) {
        this.mLauncher = launcher;
        this.mSystemShortcuts = new SystemShortcut[]{(SystemShortcut) Utilities.getOverrideObject(Custom.class, launcher, C0622R.string.custom_shortcut_class), new AppInfo(), new Widgets(), new ClearApp(), new KillApp(), new CreateDesktopShortcut()};
    }

    public void onNotificationPosted(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData, boolean z) {
        boolean z2;
        BadgeInfo badgeInfo = (BadgeInfo) this.mPackageUserToBadgeInfos.get(packageUserKey);
        if (badgeInfo != null) {
            if (z) {
                z2 = badgeInfo.removeNotificationKey(notificationKeyData);
            } else {
                z2 = badgeInfo.addOrUpdateNotificationKey(notificationKeyData);
            }
            if (badgeInfo.getNotificationKeys().size() == 0) {
                this.mPackageUserToBadgeInfos.remove(packageUserKey);
            }
        } else if (!z) {
            BadgeInfo badgeInfo2 = new BadgeInfo(packageUserKey);
            badgeInfo2.addOrUpdateNotificationKey(notificationKeyData);
            this.mPackageUserToBadgeInfos.put(packageUserKey, badgeInfo2);
            z2 = true;
        } else {
            z2 = false;
        }
        updateLauncherIconBadges(Utilities.singletonHashSet(packageUserKey), z2);
    }

    public void onNotificationRemoved(PackageUserKey packageUserKey, NotificationKeyData notificationKeyData) {
        BadgeInfo badgeInfo = (BadgeInfo) this.mPackageUserToBadgeInfos.get(packageUserKey);
        if (badgeInfo != null && badgeInfo.removeNotificationKey(notificationKeyData)) {
            if (badgeInfo.getNotificationKeys().size() == 0) {
                this.mPackageUserToBadgeInfos.remove(packageUserKey);
            }
            updateLauncherIconBadges(Utilities.singletonHashSet(packageUserKey));
            PopupContainerWithArrow open = PopupContainerWithArrow.getOpen(this.mLauncher);
            if (open != null) {
                open.trimNotifications(this.mPackageUserToBadgeInfos);
            }
        }
    }

    public void onNotificationFullRefresh(List<StatusBarNotification> list) {
        if (list != null) {
            HashMap hashMap = new HashMap(this.mPackageUserToBadgeInfos);
            this.mPackageUserToBadgeInfos.clear();
            for (StatusBarNotification statusBarNotification : list) {
                PackageUserKey fromNotification = PackageUserKey.fromNotification(statusBarNotification);
                BadgeInfo badgeInfo = (BadgeInfo) this.mPackageUserToBadgeInfos.get(fromNotification);
                if (badgeInfo == null) {
                    badgeInfo = new BadgeInfo(fromNotification);
                    this.mPackageUserToBadgeInfos.put(fromNotification, badgeInfo);
                }
                badgeInfo.addOrUpdateNotificationKey(NotificationKeyData.fromNotification(statusBarNotification));
            }
            for (PackageUserKey packageUserKey : this.mPackageUserToBadgeInfos.keySet()) {
                BadgeInfo badgeInfo2 = (BadgeInfo) hashMap.get(packageUserKey);
                BadgeInfo badgeInfo3 = (BadgeInfo) this.mPackageUserToBadgeInfos.get(packageUserKey);
                if (badgeInfo2 == null) {
                    hashMap.put(packageUserKey, badgeInfo3);
                } else if (!badgeInfo2.shouldBeInvalidated(badgeInfo3)) {
                    hashMap.remove(packageUserKey);
                }
            }
            if (!hashMap.isEmpty()) {
                updateLauncherIconBadges(hashMap.keySet());
            }
            PopupContainerWithArrow open = PopupContainerWithArrow.getOpen(this.mLauncher);
            if (open != null) {
                open.trimNotifications(hashMap);
            }
        }
    }

    private void updateLauncherIconBadges(Set<PackageUserKey> set) {
        updateLauncherIconBadges(set, true);
    }

    private void updateLauncherIconBadges(Set<PackageUserKey> set, boolean z) {
        Iterator it = set.iterator();
        while (it.hasNext()) {
            BadgeInfo badgeInfo = (BadgeInfo) this.mPackageUserToBadgeInfos.get(it.next());
            if (badgeInfo != null && !updateBadgeIcon(badgeInfo) && !z) {
                it.remove();
            }
        }
        if (!set.isEmpty()) {
            this.mLauncher.updateIconBadges(set);
        }
    }

    private boolean updateBadgeIcon(BadgeInfo badgeInfo) {
        boolean hasNotificationToShow = badgeInfo.hasNotificationToShow();
        NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
        NotificationInfo notificationInfo = null;
        if (instanceIfConnected != null && badgeInfo.getNotificationKeys().size() >= 1) {
            Iterator it = badgeInfo.getNotificationKeys().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                StatusBarNotification[] activeNotifications = instanceIfConnected.getActiveNotifications(new String[]{((NotificationKeyData) it.next()).notificationKey});
                if (activeNotifications.length == 1) {
                    NotificationInfo notificationInfo2 = new NotificationInfo(this.mLauncher, activeNotifications[0]);
                    if (notificationInfo2.shouldShowIconInBadge()) {
                        notificationInfo = notificationInfo2;
                        break;
                    }
                }
            }
        }
        badgeInfo.setNotificationToShow(notificationInfo);
        if (hasNotificationToShow || badgeInfo.hasNotificationToShow()) {
            return true;
        }
        return false;
    }

    public void setDeepShortcutMap(MultiHashMap<ComponentKey, String> multiHashMap) {
        this.mDeepShortcutMap = multiHashMap;
    }

    public List<String> getShortcutIdsForItem(ItemInfo itemInfo) {
        if (!DeepShortcutManager.supportsShortcuts(itemInfo)) {
            return Collections.EMPTY_LIST;
        }
        ComponentName targetComponent = itemInfo.getTargetComponent();
        if (targetComponent == null) {
            return Collections.EMPTY_LIST;
        }
        if (!Utilities.ATLEAST_NOUGAT_MR1) {
            ArrayList arrayList = new ArrayList();
            for (ShortcutInfoCompat id : DeepShortcutManagerBackport.getForPackage(this.mLauncher, (LauncherApps) this.mLauncher.getSystemService("launcherapps"), itemInfo.getTargetComponent(), itemInfo.getTargetComponent().getPackageName())) {
                arrayList.add(id.getId());
            }
            return arrayList;
        }
        List<String> list = (List) this.mDeepShortcutMap.get(new ComponentKey(targetComponent, itemInfo.user));
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        return list;
    }

    public BadgeInfo getBadgeInfoForItem(ItemInfo itemInfo) {
        if (!DeepShortcutManager.supportsShortcuts(itemInfo)) {
            return null;
        }
        return (BadgeInfo) this.mPackageUserToBadgeInfos.get(PackageUserKey.fromItemInfo(itemInfo));
    }

    @NonNull
    public List<NotificationKeyData> getNotificationKeysForItem(ItemInfo itemInfo) {
        BadgeInfo badgeInfoForItem = getBadgeInfoForItem(itemInfo);
        return badgeInfoForItem == null ? Collections.EMPTY_LIST : badgeInfoForItem.getNotificationKeys();
    }

    @NonNull
    public List<StatusBarNotification> getStatusBarNotificationsForKeys(List<NotificationKeyData> list) {
        NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
        if (instanceIfConnected == null) {
            return Collections.EMPTY_LIST;
        }
        return instanceIfConnected.getNotificationsForKeys(list);
    }

    @NonNull
    public List<SystemShortcut> getEnabledSystemShortcutsForItem(ItemInfo itemInfo) {
        SystemShortcut[] systemShortcutArr;
        ArrayList arrayList = new ArrayList();
        for (SystemShortcut systemShortcut : this.mSystemShortcuts) {
            if (systemShortcut.getOnClickListener(this.mLauncher, itemInfo) != null) {
                arrayList.add(systemShortcut);
            }
        }
        return arrayList;
    }

    public void cancelNotification(String str) {
        NotificationListener instanceIfConnected = NotificationListener.getInstanceIfConnected();
        if (instanceIfConnected != null) {
            instanceIfConnected.cancelNotification(str);
        }
    }
}
