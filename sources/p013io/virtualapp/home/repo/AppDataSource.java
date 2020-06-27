package p013io.virtualapp.home.repo;

import android.content.Context;
import com.lody.virtual.remote.InstallResult;
import java.io.File;
import java.util.List;
import org.jdeferred.Promise;
import p013io.virtualapp.home.models.AppData;
import p013io.virtualapp.home.models.AppInfo;
import p013io.virtualapp.home.models.AppInfoLite;

/* renamed from: io.virtualapp.home.repo.AppDataSource */
public interface AppDataSource {
    InstallResult addVirtualApp(AppInfoLite appInfoLite);

    Promise<List<AppInfo>, Throwable, Void> getInstalledApps(Context context);

    Promise<List<AppInfo>, Throwable, Void> getStorageApps(Context context, File file);

    Promise<List<AppData>, Throwable, Void> getVirtualApps();

    boolean removeVirtualApp(String str, int i);
}
