package p013io.virtualapp.home.adapters;

import android.os.Build.VERSION;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.p001v4.app.Fragment;
import android.support.p001v4.app.FragmentManager;
import android.support.p001v4.app.FragmentPagerAdapter;
import com.lody.virtual.helper.utils.DeviceUtil;
import com.lody.virtual.helper.utils.Reflect;
import io.va.exposed.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import p013io.virtualapp.XApp;
import p013io.virtualapp.home.ListAppFragment;

/* renamed from: io.virtualapp.home.adapters.AppPagerAdapter */
public class AppPagerAdapter extends FragmentPagerAdapter {
    private List<File> dirs = new ArrayList();
    private List<String> titles = new ArrayList();

    public AppPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.titles.add(XApp.getApp().getResources().getString(R.string.clone_apps));
        this.dirs.add(null);
        if (VERSION.SDK_INT >= 24) {
            for (StorageVolume storageVolume : ((StorageManager) XApp.getApp().getSystemService("storage")).getStorageVolumes()) {
                File file = (File) Reflect.m80on((Object) storageVolume).call("getPathFile").get();
                String str = (String) Reflect.m80on((Object) storageVolume).call("getUserLabel").get();
                if (file.listFiles() != null) {
                    this.titles.add(str);
                    this.dirs.add(file);
                }
            }
        } else if (!DeviceUtil.isMeizuBelowN()) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            if (externalStorageDirectory != null && externalStorageDirectory.isDirectory()) {
                this.titles.add(XApp.getApp().getResources().getString(R.string.external_storage));
                this.dirs.add(externalStorageDirectory);
            }
        }
    }

    public Fragment getItem(int i) {
        return ListAppFragment.newInstance((File) this.dirs.get(i));
    }

    public int getCount() {
        return this.titles.size();
    }

    public CharSequence getPageTitle(int i) {
        return (CharSequence) this.titles.get(i);
    }
}
