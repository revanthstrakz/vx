package p013io.virtualapp.home.models;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import com.lody.virtual.remote.InstalledAppInfo;

/* renamed from: io.virtualapp.home.models.PackageAppData */
public class PackageAppData implements AppData {
    public boolean fastOpen;
    public Drawable icon;
    public boolean isFirstOpen;
    public boolean isInstalling;
    public boolean isLoading;
    public String name;
    public String packageName;

    public boolean canCreateShortcut() {
        return true;
    }

    public boolean canDelete() {
        return true;
    }

    public boolean canLaunch() {
        return true;
    }

    public boolean canReorder() {
        return true;
    }

    public PackageAppData(Context context, InstalledAppInfo installedAppInfo) {
        this.packageName = installedAppInfo.packageName;
        this.isFirstOpen = !installedAppInfo.isLaunched(0);
        loadData(context, installedAppInfo.getApplicationInfo(installedAppInfo.getInstalledUsers()[0]));
    }

    public PackageAppData(Context context, ApplicationInfo applicationInfo) {
        this.packageName = applicationInfo.packageName;
        loadData(context, applicationInfo);
    }

    private void loadData(Context context, ApplicationInfo applicationInfo) {
        if (applicationInfo != null) {
            PackageManager packageManager = context.getPackageManager();
            try {
                CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                if (loadLabel != null) {
                    this.name = loadLabel.toString();
                }
                this.icon = applicationInfo.loadIcon(packageManager);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public boolean isInstalling() {
        return this.isInstalling;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public boolean isFirstOpen() {
        return this.isFirstOpen;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }
}
