package com.android.launcher3.popup;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.ImageView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationItemView;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class PopupPopulator {
    public static final int MAX_SHORTCUTS = 4;
    public static final int MAX_SHORTCUTS_IF_NOTIFICATIONS = 2;
    @VisibleForTesting
    static final int NUM_DYNAMIC = 2;
    private static final Comparator<ShortcutInfoCompat> SHORTCUT_RANK_COMPARATOR = new Comparator<ShortcutInfoCompat>() {
        public int compare(ShortcutInfoCompat shortcutInfoCompat, ShortcutInfoCompat shortcutInfoCompat2) {
            if (shortcutInfoCompat.isDeclaredInManifest() && !shortcutInfoCompat2.isDeclaredInManifest()) {
                return -1;
            }
            if (shortcutInfoCompat.isDeclaredInManifest() || !shortcutInfoCompat2.isDeclaredInManifest()) {
                return Integer.compare(shortcutInfoCompat.getRank(), shortcutInfoCompat2.getRank());
            }
            return 1;
        }
    };

    public enum Item {
        SHORTCUT(C0622R.layout.deep_shortcut, true),
        NOTIFICATION(C0622R.layout.notification, false),
        SYSTEM_SHORTCUT(C0622R.layout.system_shortcut, true),
        SYSTEM_SHORTCUT_ICON(C0622R.layout.system_shortcut_icon_only, true);
        
        public final boolean isShortcut;
        public final int layoutId;

        private Item(int i, boolean z) {
            this.layoutId = i;
            this.isShortcut = z;
        }
    }

    private static class UpdateNotificationChild implements Runnable {
        private List<NotificationInfo> mNotificationInfos;
        private NotificationItemView mNotificationView;

        public UpdateNotificationChild(NotificationItemView notificationItemView, List<NotificationInfo> list) {
            this.mNotificationView = notificationItemView;
            this.mNotificationInfos = list;
        }

        public void run() {
            this.mNotificationView.applyNotificationInfos(this.mNotificationInfos);
        }
    }

    private static class UpdateShortcutChild implements Runnable {
        private final PopupContainerWithArrow mContainer;
        private final ShortcutInfoCompat mDetail;
        private final DeepShortcutView mShortcutChild;
        private final ShortcutInfo mShortcutChildInfo;

        public UpdateShortcutChild(PopupContainerWithArrow popupContainerWithArrow, DeepShortcutView deepShortcutView, ShortcutInfo shortcutInfo, ShortcutInfoCompat shortcutInfoCompat) {
            this.mContainer = popupContainerWithArrow;
            this.mShortcutChild = deepShortcutView;
            this.mShortcutChildInfo = shortcutInfo;
            this.mDetail = shortcutInfoCompat;
        }

        public void run() {
            this.mShortcutChild.applyShortcutInfo(this.mShortcutChildInfo, this.mDetail, this.mContainer.mShortcutsItemView);
        }
    }

    private static class UpdateSystemShortcutChild implements Runnable {
        private final PopupContainerWithArrow mContainer;
        private final ItemInfo mItemInfo;
        private final Launcher mLauncher;
        private final View mSystemShortcutChild;
        private final SystemShortcut mSystemShortcutInfo;

        public UpdateSystemShortcutChild(PopupContainerWithArrow popupContainerWithArrow, View view, SystemShortcut systemShortcut, Launcher launcher, ItemInfo itemInfo) {
            this.mContainer = popupContainerWithArrow;
            this.mSystemShortcutChild = view;
            this.mSystemShortcutInfo = systemShortcut;
            this.mLauncher = launcher;
            this.mItemInfo = itemInfo;
        }

        public void run() {
            PopupPopulator.initializeSystemShortcut(this.mSystemShortcutChild.getContext(), this.mSystemShortcutChild, this.mSystemShortcutInfo);
            this.mSystemShortcutChild.setOnClickListener(this.mSystemShortcutInfo.getOnClickListener(this.mLauncher, this.mItemInfo));
        }
    }

    @NonNull
    public static Item[] getItemsToPopulate(@NonNull List<String> list, @NonNull List<NotificationKeyData> list2, @NonNull List<SystemShortcut> list3) {
        int i = list2.size() > 0 ? 1 : 0;
        int min = Math.min(4, list.size()) + i + list3.size();
        Item[] itemArr = new Item[min];
        for (int i2 = 0; i2 < min; i2++) {
            itemArr[i2] = Item.SHORTCUT;
        }
        if (i != 0) {
            itemArr[0] = Item.NOTIFICATION;
        }
        boolean z = !list.isEmpty();
        for (int i3 = 0; i3 < list3.size(); i3++) {
            itemArr[(min - 1) - i3] = z ? Item.SYSTEM_SHORTCUT_ICON : Item.SYSTEM_SHORTCUT;
        }
        return itemArr;
    }

    public static Item[] reverseItems(Item[] itemArr) {
        if (itemArr == null) {
            return null;
        }
        int length = itemArr.length;
        Item[] itemArr2 = new Item[length];
        for (int i = 0; i < length; i++) {
            itemArr2[i] = itemArr[(length - i) - 1];
        }
        return itemArr2;
    }

    public static List<ShortcutInfoCompat> sortAndFilterShortcuts(List<ShortcutInfoCompat> list, @Nullable String str) {
        if (str != null) {
            Iterator it = list.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (((ShortcutInfoCompat) it.next()).getId().equals(str)) {
                        it.remove();
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        Collections.sort(list, SHORTCUT_RANK_COMPARATOR);
        if (list.size() <= 4) {
            return list;
        }
        ArrayList arrayList = new ArrayList(4);
        int size = list.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ShortcutInfoCompat shortcutInfoCompat = (ShortcutInfoCompat) list.get(i2);
            int size2 = arrayList.size();
            if (size2 < 4) {
                arrayList.add(shortcutInfoCompat);
                if (shortcutInfoCompat.isDynamic()) {
                    i++;
                }
            } else if (shortcutInfoCompat.isDynamic() && i < 2) {
                i++;
                arrayList.remove(size2 - i);
                arrayList.add(shortcutInfoCompat);
            }
        }
        return arrayList;
    }

    public static Runnable createUpdateRunnable(Launcher launcher, ItemInfo itemInfo, Handler handler, PopupContainerWithArrow popupContainerWithArrow, List<String> list, List<DeepShortcutView> list2, List<NotificationKeyData> list3, NotificationItemView notificationItemView, List<SystemShortcut> list4, List<View> list5) {
        final ComponentName targetComponent = itemInfo.getTargetComponent();
        final ItemInfo itemInfo2 = itemInfo;
        final UserHandle userHandle = itemInfo2.user;
        final NotificationItemView notificationItemView2 = notificationItemView;
        final Launcher launcher2 = launcher;
        final List<NotificationKeyData> list6 = list3;
        final Handler handler2 = handler;
        final List<String> list7 = list;
        final List<DeepShortcutView> list8 = list2;
        final PopupContainerWithArrow popupContainerWithArrow2 = popupContainerWithArrow;
        final List<SystemShortcut> list9 = list4;
        final List<View> list10 = list5;
        C08132 r0 = new Runnable() {
            public void run() {
                String str;
                if (notificationItemView2 != null) {
                    List statusBarNotificationsForKeys = launcher2.getPopupDataProvider().getStatusBarNotificationsForKeys(list6);
                    ArrayList arrayList = new ArrayList(statusBarNotificationsForKeys.size());
                    for (int i = 0; i < statusBarNotificationsForKeys.size(); i++) {
                        arrayList.add(new NotificationInfo(launcher2, (StatusBarNotification) statusBarNotificationsForKeys.get(i)));
                    }
                    handler2.post(new UpdateNotificationChild(notificationItemView2, arrayList));
                }
                List queryForShortcutsContainer = DeepShortcutManager.getInstance(launcher2).queryForShortcutsContainer(targetComponent, list7, userHandle);
                if (list6.isEmpty()) {
                    str = null;
                } else {
                    str = ((NotificationKeyData) list6.get(0)).shortcutId;
                }
                List sortAndFilterShortcuts = PopupPopulator.sortAndFilterShortcuts(queryForShortcutsContainer, str);
                int i2 = 0;
                while (i2 < sortAndFilterShortcuts.size() && i2 < list8.size()) {
                    ShortcutInfoCompat shortcutInfoCompat = (ShortcutInfoCompat) sortAndFilterShortcuts.get(i2);
                    ShortcutInfo shortcutInfo = new ShortcutInfo(shortcutInfoCompat, launcher2);
                    shortcutInfo.iconBitmap = LauncherIcons.createShortcutIcon(shortcutInfoCompat, (Context) launcher2, false);
                    shortcutInfo.rank = i2;
                    handler2.post(new UpdateShortcutChild(popupContainerWithArrow2, (DeepShortcutView) list8.get(i2), shortcutInfo, shortcutInfoCompat));
                    i2++;
                }
                for (int i3 = 0; i3 < list9.size(); i3++) {
                    SystemShortcut systemShortcut = (SystemShortcut) list9.get(i3);
                    Handler handler = handler2;
                    UpdateSystemShortcutChild updateSystemShortcutChild = new UpdateSystemShortcutChild(popupContainerWithArrow2, (View) list10.get(i3), systemShortcut, launcher2, itemInfo2);
                    handler.post(updateSystemShortcutChild);
                }
                handler2.post(new Runnable() {
                    public void run() {
                        launcher2.refreshAndBindWidgetsForPackageUser(PackageUserKey.fromItemInfo(itemInfo2));
                    }
                });
            }
        };
        return r0;
    }

    public static void initializeSystemShortcut(Context context, View view, SystemShortcut systemShortcut) {
        if (view instanceof DeepShortcutView) {
            DeepShortcutView deepShortcutView = (DeepShortcutView) view;
            deepShortcutView.getIconView().setBackground(systemShortcut.getIcon(context));
            deepShortcutView.getBubbleText().setText(systemShortcut.getLabel(context));
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageDrawable(systemShortcut.getIcon(context));
            imageView.setContentDescription(systemShortcut.getLabel(context));
        }
        view.setTag(systemShortcut);
    }
}
