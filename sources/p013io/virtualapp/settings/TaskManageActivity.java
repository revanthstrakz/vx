package p013io.virtualapp.settings;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.InstalledAppInfo;
import io.va.exposed.R;
import java.util.ArrayList;
import java.util.List;
import org.jdeferred.DoneCallback;
import p013io.virtualapp.abs.p014ui.VActivity;
import p013io.virtualapp.glide.GlideUtils;

/* renamed from: io.virtualapp.settings.TaskManageActivity */
public class TaskManageActivity extends VActivity {
    private AppManageAdapter mAdapter;
    /* access modifiers changed from: private */
    public List<TaskManageInfo> mInstalledApps = new ArrayList();
    private ListView mListView;

    /* renamed from: io.virtualapp.settings.TaskManageActivity$AppManageAdapter */
    class AppManageAdapter extends BaseAdapter {
        public long getItemId(int i) {
            return 0;
        }

        AppManageAdapter() {
        }

        public int getCount() {
            return TaskManageActivity.this.mInstalledApps.size();
        }

        public TaskManageInfo getItem(int i) {
            return (TaskManageInfo) TaskManageActivity.this.mInstalledApps.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder(TaskManageActivity.this, viewGroup);
                view2 = viewHolder.root;
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            TaskManageInfo item = getItem(i);
            viewHolder.button.setText(R.string.task_manage_uninstall);
            viewHolder.label.setText(item.name);
            viewHolder.icon.setImageDrawable(item.icon);
            if (VirtualCore.get().isOutsideInstalled(item.name.toString())) {
                GlideUtils.loadInstalledPackageIcon(TaskManageActivity.this.getContext(), item.pkgName, viewHolder.icon, 17301651);
            } else {
                GlideUtils.loadPackageIconFromApkFile(TaskManageActivity.this.getContext(), item.path, viewHolder.icon, 17301651);
            }
            viewHolder.button.setOnClickListener(new OnClickListener(item, viewHolder) {
                private final /* synthetic */ TaskManageInfo f$1;
                private final /* synthetic */ ViewHolder f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    AppManageAdapter.lambda$getView$29(AppManageAdapter.this, this.f$1, this.f$2, view);
                }
            });
            return view2;
        }

        public static /* synthetic */ void lambda$getView$29(AppManageAdapter appManageAdapter, TaskManageInfo taskManageInfo, ViewHolder viewHolder, View view) {
            VActivityManager.get().killApplicationProcess(taskManageInfo.name.toString(), taskManageInfo.uid);
            viewHolder.button.postDelayed(new Runnable() {
                public final void run() {
                    TaskManageActivity.this.loadAsync();
                }
            }, 300);
        }
    }

    /* renamed from: io.virtualapp.settings.TaskManageActivity$TaskManageInfo */
    static class TaskManageInfo {
        Drawable icon;
        CharSequence name;
        public String path;
        int pid;
        public String pkgName;
        int uid;

        TaskManageInfo() {
        }
    }

    /* renamed from: io.virtualapp.settings.TaskManageActivity$ViewHolder */
    static class ViewHolder {
        Button button = ((Button) this.root.findViewById(R.id.item_app_button));
        ImageView icon = ((ImageView) this.root.findViewById(R.id.item_app_icon));
        TextView label = ((TextView) this.root.findViewById(R.id.item_app_name));
        View root;

        ViewHolder(Context context, ViewGroup viewGroup) {
            this.root = LayoutInflater.from(context).inflate(R.layout.item_task_manage, viewGroup, false);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_list);
        this.mListView = (ListView) findViewById(R.id.list);
        this.mAdapter = new AppManageAdapter();
        this.mListView.setAdapter(this.mAdapter);
        loadAsync();
    }

    /* access modifiers changed from: private */
    public void loadAsync() {
        defer().when((Runnable) new Runnable() {
            public final void run() {
                TaskManageActivity.this.loadApp();
            }
        }).done(new DoneCallback() {
            public final void onDone(Object obj) {
                TaskManageActivity.this.mAdapter.notifyDataSetChanged();
            }
        });
    }

    /* access modifiers changed from: private */
    public void loadApp() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ServiceManagerNative.ACTIVITY);
        if (activityManager != null) {
            ArrayList arrayList = new ArrayList();
            List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (runningAppProcesses != null) {
                ArrayList<RunningAppProcessInfo> arrayList2 = new ArrayList<>();
                String hostPkg = VirtualCore.get().getHostPkg();
                for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                    if (VActivityManager.get().isAppPid(runningAppProcessInfo.pid)) {
                        List processPkgList = VActivityManager.get().getProcessPkgList(runningAppProcessInfo.pid);
                        if (!processPkgList.contains(hostPkg)) {
                            String appProcessName = VActivityManager.get().getAppProcessName(runningAppProcessInfo.pid);
                            if (appProcessName != null) {
                                runningAppProcessInfo.processName = appProcessName;
                            }
                            runningAppProcessInfo.pkgList = (String[]) processPkgList.toArray(new String[processPkgList.size()]);
                            runningAppProcessInfo.uid = VUserHandle.getAppId(VActivityManager.get().getUidByPid(runningAppProcessInfo.pid));
                            arrayList2.add(runningAppProcessInfo);
                        }
                    }
                }
                for (RunningAppProcessInfo runningAppProcessInfo2 : arrayList2) {
                    TaskManageInfo taskManageInfo = new TaskManageInfo();
                    taskManageInfo.name = runningAppProcessInfo2.processName;
                    taskManageInfo.pid = runningAppProcessInfo2.pid;
                    taskManageInfo.uid = runningAppProcessInfo2.uid;
                    if (runningAppProcessInfo2.pkgList != null) {
                        for (String installedAppInfo : runningAppProcessInfo2.pkgList) {
                            InstalledAppInfo installedAppInfo2 = VirtualCore.get().getInstalledAppInfo(installedAppInfo, 0);
                            if (installedAppInfo2 != null) {
                                taskManageInfo.pkgName = installedAppInfo2.packageName;
                                taskManageInfo.path = installedAppInfo2.apkPath;
                            }
                        }
                    }
                    arrayList.add(taskManageInfo);
                }
                this.mInstalledApps.clear();
                this.mInstalledApps.addAll(arrayList);
            }
        }
    }
}
