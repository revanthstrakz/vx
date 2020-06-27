package p013io.virtualapp.sys;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.p004v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.ipc.VPackageManager;
import io.va.exposed.R;
import java.util.List;

/* renamed from: io.virtualapp.sys.ShareBridgeActivity */
public class ShareBridgeActivity extends AppCompatActivity {
    private SharedAdapter mAdapter;
    /* access modifiers changed from: private */
    public List<ResolveInfo> mShareComponents;

    /* renamed from: io.virtualapp.sys.ShareBridgeActivity$SharedAdapter */
    private class SharedAdapter extends BaseAdapter {
        public long getItemId(int i) {
            return 0;
        }

        private SharedAdapter() {
        }

        public int getCount() {
            return ShareBridgeActivity.this.mShareComponents.size();
        }

        public ResolveInfo getItem(int i) {
            return (ResolveInfo) ShareBridgeActivity.this.mShareComponents.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder(ShareBridgeActivity.this.getActivity(), viewGroup);
                view2 = viewHolder.root;
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            ResolveInfo item = getItem(i);
            PackageManager packageManager = ShareBridgeActivity.this.getPackageManager();
            try {
                viewHolder.label.setText(item.loadLabel(packageManager));
            } catch (Throwable unused) {
                viewHolder.label.setText(R.string.package_state_unknown);
            }
            try {
                viewHolder.icon.setImageDrawable(item.loadIcon(packageManager));
            } catch (Throwable unused2) {
                viewHolder.icon.setImageDrawable(ShareBridgeActivity.this.getResources().getDrawable(17301651));
            }
            return view2;
        }
    }

    /* renamed from: io.virtualapp.sys.ShareBridgeActivity$ViewHolder */
    static class ViewHolder {
        ImageView icon = ((ImageView) this.root.findViewById(R.id.item_share_icon));
        TextView label = ((TextView) this.root.findViewById(R.id.item_share_name));
        View root;

        ViewHolder(Context context, ViewGroup viewGroup) {
            this.root = LayoutInflater.from(context).inflate(R.layout.item_share, viewGroup, false);
        }
    }

    /* access modifiers changed from: private */
    public Activity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        intent.setComponent(null);
        if (!"android.intent.action.SEND".equals(action)) {
            finish();
            return;
        }
        try {
            this.mShareComponents = VPackageManager.get().queryIntentActivities(new Intent("android.intent.action.SEND"), type, 0, 0);
        } catch (Throwable unused) {
        }
        if (this.mShareComponents == null || this.mShareComponents.size() == 0) {
            finish();
            return;
        }
        setContentView((int) R.layout.activity_list);
        ListView listView = (ListView) findViewById(R.id.list);
        this.mAdapter = new SharedAdapter();
        listView.setAdapter(this.mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener(intent) {
            private final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                ShareBridgeActivity.lambda$onCreate$108(ShareBridgeActivity.this, this.f$1, adapterView, view, i, j);
            }
        });
    }

    public static /* synthetic */ void lambda$onCreate$108(ShareBridgeActivity shareBridgeActivity, Intent intent, AdapterView adapterView, View view, int i, long j) {
        try {
            ResolveInfo item = shareBridgeActivity.mAdapter.getItem(i);
            Intent intent2 = new Intent(intent);
            intent2.setComponent(new ComponentName(item.activityInfo.packageName, item.activityInfo.name));
            VActivityManager.get().startActivity(intent2, 0);
        } catch (Throwable unused) {
            Toast.makeText(shareBridgeActivity.getApplicationContext(), R.string.shared_to_vxp_failed, 0).show();
        }
        shareBridgeActivity.finish();
    }
}
