package p013io.virtualapp.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.microsoft.appcenter.http.DefaultHttpClient;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import io.va.exposed.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p013io.virtualapp.abs.p014ui.VActivity;

/* renamed from: io.virtualapp.settings.RecommendPluginActivity */
public class RecommendPluginActivity extends VActivity {
    private static final String ADDRESS = "http://vaexposed.weishu.me/plugin.json";
    private PluginAdapter mAdapter;
    /* access modifiers changed from: private */
    public List<PluginInfo> mData = new ArrayList();
    private ProgressDialog mLoadingDialog;

    /* renamed from: io.virtualapp.settings.RecommendPluginActivity$PluginAdapter */
    class PluginAdapter extends BaseAdapter {
        public long getItemId(int i) {
            return 0;
        }

        PluginAdapter() {
        }

        public int getCount() {
            return RecommendPluginActivity.this.mData.size();
        }

        public PluginInfo getItem(int i) {
            return (PluginInfo) RecommendPluginActivity.this.mData.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder(RecommendPluginActivity.this, viewGroup);
                view2 = viewHolder.root;
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            PluginInfo item = getItem(i);
            viewHolder.title.setText(item.name);
            viewHolder.summary.setText(item.desc);
            return view2;
        }
    }

    /* renamed from: io.virtualapp.settings.RecommendPluginActivity$PluginInfo */
    static class PluginInfo {
        String desc;
        String link;
        String name;

        PluginInfo() {
        }
    }

    /* renamed from: io.virtualapp.settings.RecommendPluginActivity$ViewHolder */
    static class ViewHolder {
        View root;
        TextView summary = ((TextView) this.root.findViewById(R.id.item_plugin_summary));
        TextView title = ((TextView) this.root.findViewById(R.id.item_plugin_name));

        public ViewHolder(Context context, ViewGroup viewGroup) {
            this.root = LayoutInflater.from(context).inflate(R.layout.item_plugin_recommend, viewGroup, false);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_list);
        this.mLoadingDialog = new ProgressDialog(this);
        this.mLoadingDialog.setTitle("Loading");
        ListView listView = (ListView) findViewById(R.id.list);
        this.mAdapter = new PluginAdapter();
        listView.setAdapter(this.mAdapter);
        listView.setEmptyView(findViewById(R.id.empty_view));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                RecommendPluginActivity.lambda$onCreate$0(RecommendPluginActivity.this, adapterView, view, i, j);
            }
        });
        loadRecommend();
    }

    public static /* synthetic */ void lambda$onCreate$0(RecommendPluginActivity recommendPluginActivity, AdapterView adapterView, View view, int i, long j) {
        try {
            PluginInfo item = recommendPluginActivity.mAdapter.getItem(i);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(item.link));
            recommendPluginActivity.startActivity(intent);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void loadRecommend() {
        try {
            this.mLoadingDialog.show();
        } catch (Throwable unused) {
        }
        defer().when((Callable) $$Lambda$RecommendPluginActivity$sr6khfWhi20mIGz2ltUFP2E_5I.INSTANCE).done(new DoneCallback() {
            public final void onDone(Object obj) {
                RecommendPluginActivity.lambda$loadRecommend$2(RecommendPluginActivity.this, (JSONArray) obj);
            }
        }).fail(new FailCallback() {
            public final void onFail(Object obj) {
                RecommendPluginActivity.this.mLoadingDialog.dismiss();
            }
        });
    }

    static /* synthetic */ JSONArray lambda$loadRecommend$1() throws Exception {
        JSONArray jSONArray;
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(ADDRESS).openConnection();
        httpURLConnection.setRequestMethod(DefaultHttpClient.METHOD_GET);
        httpURLConnection.setConnectTimeout(30000);
        httpURLConnection.setReadTimeout(30000);
        if (httpURLConnection.getResponseCode() == 200) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
            }
            jSONArray = new JSONArray(sb.toString());
            bufferedReader.close();
        } else {
            jSONArray = null;
        }
        httpURLConnection.disconnect();
        return jSONArray;
    }

    public static /* synthetic */ void lambda$loadRecommend$2(RecommendPluginActivity recommendPluginActivity, JSONArray jSONArray) {
        recommendPluginActivity.mLoadingDialog.dismiss();
        if (jSONArray != null) {
            recommendPluginActivity.mData.clear();
            int length = jSONArray.length();
            for (int i = 0; i < length; i++) {
                PluginInfo pluginInfo = new PluginInfo();
                try {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    pluginInfo.name = jSONObject.getString(CommonProperties.NAME);
                    pluginInfo.desc = jSONObject.getString("desc");
                    pluginInfo.link = jSONObject.getString("link");
                    recommendPluginActivity.mData.add(pluginInfo);
                } catch (JSONException unused) {
                }
            }
            recommendPluginActivity.mAdapter.notifyDataSetChanged();
        }
    }
}
