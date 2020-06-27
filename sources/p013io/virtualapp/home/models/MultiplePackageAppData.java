package p013io.virtualapp.home.models;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.remote.InstalledAppInfo;

/* renamed from: io.virtualapp.home.models.MultiplePackageAppData */
public class MultiplePackageAppData implements AppData {
    public InstalledAppInfo appInfo;
    public Drawable icon;
    public boolean isFirstOpen;
    public boolean isInstalling;
    public boolean isLoading;
    public String name;
    public int userId;

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

    public MultiplePackageAppData(PackageAppData packageAppData, int i) {
        this.userId = i;
        this.appInfo = VirtualCore.get().getInstalledAppInfo(packageAppData.packageName, 0);
        this.isFirstOpen = !this.appInfo.isLaunched(i);
        if (packageAppData.icon != null) {
            ConstantState constantState = packageAppData.icon.getConstantState();
            if (constantState != null) {
                this.icon = constantState.newDrawable();
            }
        }
        this.name = packageAppData.name;
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
