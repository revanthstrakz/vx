package com.android.launcher3.popup;

import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.C0622R;
import com.android.launcher3.InfoDropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.popup.SystemShortcut.ClearApp;
import com.android.launcher3.popup.SystemShortcut.CreateDesktopShortcut;
import com.android.launcher3.popup.SystemShortcut.KillApp;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.WidgetsBottomSheet;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.core.VirtualCore.OnEmitShortcutListener;

public abstract class SystemShortcut extends ItemInfo {
    private final int mIconResId;
    private final int mLabelResId;

    public static class AppInfo extends SystemShortcut {
        public AppInfo() {
            super(C0622R.C0624drawable.ic_info_no_shadow, C0622R.string.app_info_drop_target_label);
        }

        public OnClickListener getOnClickListener(final Launcher launcher, final ItemInfo itemInfo) {
            return new OnClickListener() {
                public void onClick(View view) {
                    InfoDropTarget.startDetailsActivityForInfo(itemInfo, launcher, null, launcher.getViewBounds(view), launcher.getActivityLaunchOptions(view));
                    launcher.getUserEventDispatcher().logActionOnControl(0, 7, view);
                }
            };
        }
    }

    public static class ClearApp extends SystemShortcut {
        public ClearApp() {
            super(C0622R.C0624drawable.ic_clear_app, C0622R.string.clear_app_label);
        }

        public OnClickListener getOnClickListener(Launcher launcher, ItemInfo itemInfo) {
            return new OnClickListener(itemInfo, itemInfo.getTargetComponent().getPackageName()) {
                private final /* synthetic */ ItemInfo f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    ClearApp.lambda$getOnClickListener$8(Launcher.this, this.f$1, this.f$2, view);
                }
            };
        }

        static /* synthetic */ void lambda$getOnClickListener$8(Launcher launcher, ItemInfo itemInfo, String str, View view) {
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(launcher);
            if (topOpenView instanceof PopupContainerWithArrow) {
                topOpenView.close(true);
                try {
                    new Builder(launcher).setTitle(C0622R.string.home_menu_clear_title).setMessage(launcher.getString(C0622R.string.home_menu_clear_content, new Object[]{itemInfo.title})).setPositiveButton(17039379, new DialogInterface.OnClickListener(str) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            VirtualCore.get().clearPackageAsUser(UserManagerCompat.toUserId(ItemInfo.this.user), this.f$1);
                        }
                    }).setNegativeButton(17039369, null).create().show();
                } catch (Throwable unused) {
                }
            }
        }
    }

    public static class CreateDesktopShortcut extends SystemShortcut {
        public CreateDesktopShortcut() {
            super(C0622R.C0624drawable.ic_create_shortcut, C0622R.string.create_shortcut_label);
        }

        public OnClickListener getOnClickListener(Launcher launcher, ItemInfo itemInfo) {
            return new OnClickListener(launcher, itemInfo, itemInfo.getTargetComponent().getPackageName()) {
                private final /* synthetic */ Launcher f$1;
                private final /* synthetic */ ItemInfo f$2;
                private final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    CreateDesktopShortcut.lambda$getOnClickListener$9(CreateDesktopShortcut.this, this.f$1, this.f$2, this.f$3, view);
                }
            };
        }

        public static /* synthetic */ void lambda$getOnClickListener$9(CreateDesktopShortcut createDesktopShortcut, Launcher launcher, ItemInfo itemInfo, String str, View view) {
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(launcher);
            if (topOpenView instanceof PopupContainerWithArrow) {
                topOpenView.close(true);
                final int userId = UserManagerCompat.toUserId(itemInfo.user);
                C08161 r5 = new OnEmitShortcutListener() {
                    public Bitmap getIcon(Bitmap bitmap) {
                        return bitmap;
                    }

                    public String getName(String str) {
                        if (userId == 0) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(str);
                            sb.append("(VXP)");
                            return sb.toString();
                        }
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("[");
                        sb2.append(userId + 1);
                        sb2.append("]");
                        sb2.append(str);
                        return sb2.toString();
                    }
                };
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(VirtualCore.get().getHostPkg(), "io.virtualapp.home.LoadingActivity"));
                if (VirtualCore.get().createShortcut(userId, str, intent, r5) && !Utilities.ATLEAST_OREO) {
                    Toast.makeText(VirtualCore.get().getContext(), C0622R.string.create_shortcut_success, 0).show();
                }
            }
        }
    }

    public static class Custom extends SystemShortcut {
        public OnClickListener getOnClickListener(Launcher launcher, ItemInfo itemInfo) {
            return null;
        }

        public Custom() {
            super(C0622R.C0624drawable.ic_edit_no_shadow, C0622R.string.action_preferences);
        }
    }

    public static class KillApp extends SystemShortcut {
        public KillApp() {
            super(C0622R.C0624drawable.ic_kill_app, C0622R.string.kill_app_label);
        }

        public OnClickListener getOnClickListener(Launcher launcher, ItemInfo itemInfo) {
            return new OnClickListener(itemInfo, itemInfo.getTargetComponent().getPackageName()) {
                private final /* synthetic */ ItemInfo f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    KillApp.lambda$getOnClickListener$6(Launcher.this, this.f$1, this.f$2, view);
                }
            };
        }

        static /* synthetic */ void lambda$getOnClickListener$6(Launcher launcher, ItemInfo itemInfo, String str, View view) {
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(launcher);
            if (topOpenView instanceof PopupContainerWithArrow) {
                topOpenView.close(true);
                try {
                    new Builder(launcher).setTitle(C0622R.string.home_menu_kill_title).setMessage(launcher.getResources().getString(C0622R.string.home_menu_kill_content, new Object[]{itemInfo.title})).setPositiveButton(17039379, new DialogInterface.OnClickListener(str, launcher) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ Launcher f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            KillApp.lambda$null$5(ItemInfo.this, this.f$1, this.f$2, dialogInterface, i);
                        }
                    }).setNegativeButton(17039369, null).create().show();
                } catch (Throwable unused) {
                }
            }
        }

        static /* synthetic */ void lambda$null$5(ItemInfo itemInfo, String str, Launcher launcher, DialogInterface dialogInterface, int i) {
            try {
                VirtualCore.get().killApp(str, UserManagerCompat.toUserId(itemInfo.user));
            } catch (Throwable unused) {
                StringBuilder sb = new StringBuilder();
                sb.append("Kill ");
                sb.append(itemInfo.title);
                sb.append(" Failed, please try again.");
                Toast.makeText(launcher, sb.toString(), 0).show();
            }
        }
    }

    public static class Widgets extends SystemShortcut {
        public Widgets() {
            super(C0622R.C0624drawable.ic_widget, C0622R.string.widget_button_text);
        }

        public OnClickListener getOnClickListener(final Launcher launcher, final ItemInfo itemInfo) {
            if (launcher.getWidgetsForPackageUser(new PackageUserKey(itemInfo.getTargetComponent().getPackageName(), itemInfo.user)) == null) {
                return null;
            }
            return new OnClickListener() {
                public void onClick(View view) {
                    AbstractFloatingView.closeAllOpenViews(launcher);
                    ((WidgetsBottomSheet) launcher.getLayoutInflater().inflate(C0622R.layout.widgets_bottom_sheet, launcher.getDragLayer(), false)).populateAndShow(itemInfo);
                    launcher.getUserEventDispatcher().logActionOnControl(0, 2, view);
                }
            };
        }
    }

    public abstract OnClickListener getOnClickListener(Launcher launcher, ItemInfo itemInfo);

    public SystemShortcut(int i, int i2) {
        this.mIconResId = i;
        this.mLabelResId = i2;
    }

    public Drawable getIcon(Context context) {
        return context.getResources().getDrawable(this.mIconResId, context.getTheme());
    }

    public String getLabel(Context context) {
        return context.getString(this.mLabelResId);
    }
}
