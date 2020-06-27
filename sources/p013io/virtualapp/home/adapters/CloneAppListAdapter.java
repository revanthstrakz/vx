package p013io.virtualapp.home.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.p004v7.widget.RecyclerView;
import android.support.p004v7.widget.StaggeredGridLayoutManager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import io.va.exposed.R;
import java.io.File;
import java.util.List;
import p013io.virtualapp.abs.p014ui.VUiKit;
import p013io.virtualapp.glide.GlideUtils;
import p013io.virtualapp.home.models.AppInfo;
import p013io.virtualapp.widgets.DragSelectRecyclerViewAdapter;
import p013io.virtualapp.widgets.LabelView;

/* renamed from: io.virtualapp.home.adapters.CloneAppListAdapter */
public class CloneAppListAdapter extends DragSelectRecyclerViewAdapter<ViewHolder> {
    private static final int TYPE_FOOTER = -2;
    private List<AppInfo> mAppList;
    private Context mContext;
    /* access modifiers changed from: private */
    public final View mFooterView;
    private File mFrom;
    private LayoutInflater mInflater;
    private ItemEventListener mItemEventListener;

    /* renamed from: io.virtualapp.home.adapters.CloneAppListAdapter$ItemEventListener */
    public interface ItemEventListener {
        boolean isSelectable(int i);

        void onItemClick(AppInfo appInfo, int i);
    }

    /* renamed from: io.virtualapp.home.adapters.CloneAppListAdapter$ViewHolder */
    class ViewHolder extends android.support.p004v7.widget.RecyclerView.ViewHolder {
        /* access modifiers changed from: private */
        public ImageView appCheckView;
        /* access modifiers changed from: private */
        public ImageView iconView;
        /* access modifiers changed from: private */
        public LabelView labelView;
        /* access modifiers changed from: private */
        public TextView nameView;
        /* access modifiers changed from: private */
        public TextView summaryView;

        ViewHolder(View view) {
            super(view);
            if (view != CloneAppListAdapter.this.mFooterView) {
                this.iconView = (ImageView) view.findViewById(R.id.item_app_icon);
                this.nameView = (TextView) view.findViewById(R.id.item_app_name);
                this.appCheckView = (ImageView) view.findViewById(R.id.item_app_checked);
                this.labelView = (LabelView) view.findViewById(R.id.item_app_clone_count);
                this.summaryView = (TextView) view.findViewById(R.id.item_app_summary);
            }
        }
    }

    public CloneAppListAdapter(Context context, @Nullable File file) {
        this.mContext = context;
        this.mFrom = file;
        this.mInflater = LayoutInflater.from(context);
        this.mFooterView = new View(context);
        LayoutParams layoutParams = new LayoutParams(-1, VUiKit.dpToPx(context, 60));
        layoutParams.setFullSpan(true);
        this.mFooterView.setLayoutParams(layoutParams);
    }

    public void setOnItemClickListener(ItemEventListener itemEventListener) {
        this.mItemEventListener = itemEventListener;
    }

    public List<AppInfo> getList() {
        return this.mAppList;
    }

    public void setList(List<AppInfo> list) {
        this.mAppList = list;
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == -2) {
            return new ViewHolder(this.mFooterView);
        }
        return new ViewHolder(this.mInflater.inflate(R.layout.item_clone_app, null));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (getItemViewType(i) != -2) {
            super.onBindViewHolder(viewHolder, i);
            AppInfo appInfo = (AppInfo) this.mAppList.get(i);
            if (this.mFrom == null) {
                GlideUtils.loadInstalledPackageIcon(this.mContext, appInfo.packageName, viewHolder.iconView, 17301651);
            } else {
                GlideUtils.loadPackageIconFromApkFile(this.mContext, appInfo.path, viewHolder.iconView, 17301651);
            }
            viewHolder.nameView.setText(String.format("%s: %s", new Object[]{appInfo.name, appInfo.version}));
            if (isIndexSelected(i)) {
                viewHolder.iconView.setAlpha(1.0f);
                viewHolder.appCheckView.setImageResource(R.drawable.ic_check);
            } else {
                viewHolder.iconView.setAlpha(0.65f);
                viewHolder.appCheckView.setImageResource(R.drawable.ic_no_check);
            }
            if (appInfo.cloneCount > 0) {
                viewHolder.labelView.setVisibility(0);
                LabelView access$300 = viewHolder.labelView;
                StringBuilder sb = new StringBuilder();
                sb.append(appInfo.cloneCount + 1);
                sb.append("");
                access$300.setText(sb.toString());
            } else {
                viewHolder.labelView.setVisibility(4);
            }
            if (appInfo.path == null) {
                viewHolder.summaryView.setVisibility(8);
            } else {
                viewHolder.summaryView.setVisibility(0);
                viewHolder.summaryView.setText(appInfo.path);
            }
            viewHolder.itemView.setOnClickListener(new OnClickListener(appInfo, i) {
                private final /* synthetic */ AppInfo f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    CloneAppListAdapter.this.mItemEventListener.onItemClick(this.f$1, this.f$2);
                }
            });
        }
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /* access modifiers changed from: protected */
    public boolean isIndexSelectable(int i) {
        return this.mItemEventListener.isSelectable(i);
    }

    public int getItemCount() {
        if (this.mAppList == null) {
            return 1;
        }
        return 1 + this.mAppList.size();
    }

    public int getItemViewType(int i) {
        if (i == getItemCount() - 1) {
            return -2;
        }
        return super.getItemViewType(i);
    }

    public AppInfo getItem(int i) {
        return (AppInfo) this.mAppList.get(i);
    }
}
