package p013io.virtualapp.settings;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.ipc.VirtualStorageManager;
import com.lody.virtual.helper.ArtDexOptimizer;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.remote.InstalledAppInfo;
import io.va.exposed.R;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import p013io.virtualapp.abs.p014ui.VActivity;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.glide.GlideUtils;

/* renamed from: io.virtualapp.settings.AppManageActivity */
public class AppManageActivity extends VActivity {
    private AppManageAdapter mAdapter;
    /* access modifiers changed from: private */
    public List<AppManageInfo> mInstalledApps = new ArrayList();
    private ListView mListView;

    /* renamed from: io.virtualapp.settings.AppManageActivity$AppManageAdapter */
    class AppManageAdapter extends BaseAdapter {
        public long getItemId(int i) {
            return 0;
        }

        AppManageAdapter() {
        }

        public int getCount() {
            return AppManageActivity.this.mInstalledApps.size();
        }

        public AppManageInfo getItem(int i) {
            return (AppManageInfo) AppManageActivity.this.mInstalledApps.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder(AppManageActivity.this, viewGroup);
                view2 = viewHolder.root;
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            AppManageInfo item = getItem(i);
            viewHolder.label.setText(item.getName());
            if (VirtualCore.get().isOutsideInstalled(item.pkgName)) {
                GlideUtils.loadInstalledPackageIcon(AppManageActivity.this.getContext(), item.pkgName, viewHolder.icon, 17301651);
            } else {
                GlideUtils.loadPackageIconFromApkFile(AppManageActivity.this.getContext(), item.path, viewHolder.icon, 17301651);
            }
            viewHolder.button.setOnClickListener(new OnClickListener(item) {
                private final /* synthetic */ AppManageInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AppManageActivity.this.showContextMenu(this.f$1, view);
                }
            });
            return view2;
        }
    }

    /* renamed from: io.virtualapp.settings.AppManageActivity$AppManageInfo */
    static class AppManageInfo {
        Drawable icon;
        CharSequence name;
        String path;
        String pkgName;
        int userId;

        AppManageInfo() {
        }

        /* access modifiers changed from: 0000 */
        public CharSequence getName() {
            if (this.userId == 0) {
                return this.name;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(this.name);
            sb.append("[");
            sb.append(this.userId + 1);
            sb.append("]");
            return sb.toString();
        }
    }

    /* renamed from: io.virtualapp.settings.AppManageActivity$ViewHolder */
    static class ViewHolder {
        ImageView button = ((ImageView) this.root.findViewById(R.id.item_app_button));
        ImageView icon = ((ImageView) this.root.findViewById(R.id.item_app_icon));
        TextView label = ((TextView) this.root.findViewById(R.id.item_app_name));
        View root;

        ViewHolder(Context context, ViewGroup viewGroup) {
            this.root = LayoutInflater.from(context).inflate(R.layout.item_app_manage, viewGroup, false);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_list);
        this.mListView = (ListView) findViewById(R.id.list);
        this.mAdapter = new AppManageAdapter();
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener(new OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                AppManageActivity.this.showContextMenu((AppManageInfo) AppManageActivity.this.mInstalledApps.get(i), view);
            }
        });
        loadAsync();
    }

    private void loadAsync() {
        VUiKit.defer().when((Runnable) new Runnable() {
            public final void run() {
                AppManageActivity.this.loadApp();
            }
        }).done(new DoneCallback() {
            public final void onDone(Object obj) {
                AppManageActivity.this.mAdapter.notifyDataSetChanged();
            }
        });
    }

    /* access modifiers changed from: private */
    public void loadApp() {
        int[] installedUsers;
        ArrayList arrayList = new ArrayList();
        List<InstalledAppInfo> installedApps = VirtualCore.get().getInstalledApps(0);
        PackageManager packageManager = getPackageManager();
        for (InstalledAppInfo installedAppInfo : installedApps) {
            for (int i : installedAppInfo.getInstalledUsers()) {
                AppManageInfo appManageInfo = new AppManageInfo();
                appManageInfo.userId = i;
                ApplicationInfo applicationInfo = installedAppInfo.getApplicationInfo(i);
                appManageInfo.name = applicationInfo.loadLabel(packageManager);
                appManageInfo.pkgName = installedAppInfo.packageName;
                appManageInfo.path = applicationInfo.sourceDir;
                arrayList.add(appManageInfo);
            }
        }
        this.mInstalledApps.clear();
        this.mInstalledApps.addAll(arrayList);
    }

    /* access modifiers changed from: private */
    public void showContextMenu(AppManageInfo appManageInfo, View view) {
        if (appManageInfo != null) {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.inflate(R.menu.app_manage_menu);
            MenuItem findItem = popupMenu.getMenu().findItem(R.id.action_redirect);
            try {
                findItem.setTitle(VirtualStorageManager.get().isVirtualStorageEnable(appManageInfo.pkgName, appManageInfo.userId) ? R.string.app_manage_redirect_off : R.string.app_manage_redirect_on);
            } catch (Throwable unused) {
                findItem.setVisible(false);
            }
            popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(appManageInfo) {
                private final /* synthetic */ AppManageInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean onMenuItemClick(MenuItem menuItem) {
                    return AppManageActivity.lambda$showContextMenu$20(AppManageActivity.this, this.f$1, menuItem);
                }
            });
            try {
                popupMenu.show();
            } catch (Throwable unused2) {
            }
        }
    }

    public static /* synthetic */ boolean lambda$showContextMenu$20(AppManageActivity appManageActivity, AppManageInfo appManageInfo, MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_redirect) {
            appManageActivity.showStorageRedirectDialog(appManageInfo);
        } else if (itemId == R.id.action_repair) {
            appManageActivity.showRepairDialog(appManageInfo);
        } else if (itemId == R.id.action_uninstall) {
            appManageActivity.showUninstallDialog(appManageInfo, appManageInfo.getName());
        }
        return false;
    }

    private void showRepairDialog(AppManageInfo appManageInfo) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.app_manage_repairing));
        try {
            progressDialog.setCancelable(false);
            progressDialog.show();
            VUiKit.defer().when((Runnable) new Runnable(appManageInfo) {
                private final /* synthetic */ AppManageInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AppManageActivity.lambda$showRepairDialog$21(AppManageActivity.this, this.f$1);
                }
            }).done(new DoneCallback(progressDialog) {
                private final /* synthetic */ ProgressDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void onDone(Object obj) {
                    AppManageActivity.lambda$showRepairDialog$22(AppManageActivity.this, this.f$1, (Void) obj);
                }
            }).fail(new FailCallback(progressDialog) {
                private final /* synthetic */ ProgressDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void onFail(Object obj) {
                    AppManageActivity.lambda$showRepairDialog$23(AppManageActivity.this, this.f$1, (Throwable) obj);
                }
            });
        } catch (Throwable unused) {
        }
    }

    public static /* synthetic */ void lambda$showRepairDialog$21(AppManageActivity appManageActivity, AppManageInfo appManageInfo) {
        NougatPolicy.fullCompile(appManageActivity.getApplicationContext());
        String str = appManageInfo.pkgName;
        String str2 = appManageInfo.path;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            VirtualCore.get().killApp(str, appManageInfo.userId);
            File odexFile = VEnvironment.getOdexFile(str);
            if (odexFile.delete()) {
                try {
                    ArtDexOptimizer.compileDex2Oat(str2, odexFile.getPath());
                } catch (IOException unused) {
                    throw new RuntimeException("compile failed.");
                }
            }
        }
    }

    public static /* synthetic */ void lambda$showRepairDialog$22(AppManageActivity appManageActivity, ProgressDialog progressDialog, Void voidR) {
        dismiss(progressDialog);
        appManageActivity.showAppDetailDialog();
    }

    public static /* synthetic */ void lambda$showRepairDialog$23(AppManageActivity appManageActivity, ProgressDialog progressDialog, Throwable th) {
        dismiss(progressDialog);
        Toast.makeText(appManageActivity, R.string.app_manage_repair_failed_tips, 0).show();
    }

    private void showAppDetailDialog() {
        AlertDialog create = new Builder(this).setTitle(R.string.app_manage_repair_success_title).setMessage(getResources().getString(R.string.app_manage_repair_success_content)).setPositiveButton(R.string.app_manage_repair_reboot_now, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                AppManageActivity.lambda$showAppDetailDialog$24(AppManageActivity.this, dialogInterface, i);
            }
        }).create();
        create.setCancelable(false);
        try {
            create.show();
        } catch (Throwable unused) {
        }
    }

    public static /* synthetic */ void lambda$showAppDetailDialog$24(AppManageActivity appManageActivity, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts(ServiceManagerNative.PACKAGE, appManageActivity.getPackageName(), null));
        intent.setFlags(268468224);
        appManageActivity.startActivity(intent);
    }

    private void showUninstallDialog(AppManageInfo appManageInfo, CharSequence charSequence) {
        try {
            new Builder(this).setTitle(R.string.home_menu_delete_title).setMessage(getResources().getString(R.string.home_menu_delete_content, new Object[]{charSequence})).setPositiveButton(17039379, new DialogInterface.OnClickListener(appManageInfo) {
                private final /* synthetic */ AppManageInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AppManageActivity.lambda$showUninstallDialog$25(AppManageActivity.this, this.f$1, dialogInterface, i);
                }
            }).setNegativeButton(17039369, null).create().show();
        } catch (Throwable unused) {
        }
    }

    public static /* synthetic */ void lambda$showUninstallDialog$25(AppManageActivity appManageActivity, AppManageInfo appManageInfo, DialogInterface dialogInterface, int i) {
        VirtualCore.get().uninstallPackageAsUser(appManageInfo.pkgName, appManageInfo.userId);
        appManageActivity.loadAsync();
    }

    private void showStorageRedirectDialog(AppManageInfo appManageInfo) {
        String str = appManageInfo.pkgName;
        int i = appManageInfo.userId;
        try {
            boolean isVirtualStorageEnable = VirtualStorageManager.get().isVirtualStorageEnable(str, i);
            try {
                new Builder(this).setTitle(isVirtualStorageEnable ? R.string.app_manage_redirect_off : R.string.app_manage_redirect_on).setMessage(getResources().getString(R.string.app_manage_redirect_desc)).setPositiveButton(isVirtualStorageEnable ? R.string.app_manage_redirect_off_confirm : R.string.app_manage_redirect_on_confirm, new DialogInterface.OnClickListener(str, i, isVirtualStorageEnable) {
                    private final /* synthetic */ String f$0;
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ boolean f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AppManageActivity.lambda$showStorageRedirectDialog$26(this.f$0, this.f$1, this.f$2, dialogInterface, i);
                    }
                }).setNegativeButton(17039369, null).create().show();
            } catch (Throwable unused) {
            }
        } catch (Throwable unused2) {
        }
    }

    static /* synthetic */ void lambda$showStorageRedirectDialog$26(String str, int i, boolean z, DialogInterface dialogInterface, int i2) {
        try {
            VirtualStorageManager.get().setVirtualStorageState(str, i, !z);
        } catch (Throwable unused) {
        }
    }

    private static void dismiss(Dialog dialog) {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Throwable unused) {
            }
        }
    }
}
