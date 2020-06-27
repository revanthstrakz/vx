package com.android.launcher3.widget;

import android.content.Context;
import android.support.p004v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import com.android.launcher3.C0622R;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.util.LabelComparator;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.WidgetsDiffReporter.NotifyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class WidgetsListAdapter extends Adapter<WidgetsRowViewHolder> {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsListAdapter";
    private final WidgetsDiffReporter mDiffReporter;
    private ArrayList<WidgetListRowEntry> mEntries = new ArrayList<>();
    private final OnClickListener mIconClickListener;
    private final OnLongClickListener mIconLongClickListener;
    private final int mIndent;
    private final AlphabeticIndexCompat mIndexer;
    private final LayoutInflater mLayoutInflater;
    private final WidgetPreviewLoader mWidgetPreviewLoader;

    public static class WidgetListRowEntryComparator implements Comparator<WidgetListRowEntry> {
        private final LabelComparator mComparator = new LabelComparator();

        public int compare(WidgetListRowEntry widgetListRowEntry, WidgetListRowEntry widgetListRowEntry2) {
            return this.mComparator.compare(widgetListRowEntry.pkgItem.title.toString(), widgetListRowEntry2.pkgItem.title.toString());
        }
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean onFailedToRecycleView(WidgetsRowViewHolder widgetsRowViewHolder) {
        return true;
    }

    public WidgetsListAdapter(Context context, LayoutInflater layoutInflater, WidgetPreviewLoader widgetPreviewLoader, AlphabeticIndexCompat alphabeticIndexCompat, OnClickListener onClickListener, OnLongClickListener onLongClickListener, WidgetsDiffReporter widgetsDiffReporter) {
        this.mLayoutInflater = layoutInflater;
        this.mWidgetPreviewLoader = widgetPreviewLoader;
        this.mIndexer = alphabeticIndexCompat;
        this.mIconClickListener = onClickListener;
        this.mIconLongClickListener = onLongClickListener;
        this.mIndent = context.getResources().getDimensionPixelSize(C0622R.dimen.widget_section_indent);
        this.mDiffReporter = widgetsDiffReporter;
    }

    public void setNotifyListener() {
        this.mDiffReporter.setListener(new NotifyListener() {
            public void notifyDataSetChanged() {
                WidgetsListAdapter.this.notifyDataSetChanged();
            }

            public void notifyItemChanged(int i) {
                WidgetsListAdapter.this.notifyItemChanged(i);
            }

            public void notifyItemInserted(int i) {
                WidgetsListAdapter.this.notifyItemInserted(i);
            }

            public void notifyItemRemoved(int i) {
                WidgetsListAdapter.this.notifyItemRemoved(i);
            }
        });
    }

    public void setWidgets(MultiHashMap<PackageItemInfo, WidgetItem> multiHashMap) {
        ArrayList arrayList = new ArrayList();
        WidgetItemComparator widgetItemComparator = new WidgetItemComparator();
        for (Entry entry : multiHashMap.entrySet()) {
            WidgetListRowEntry widgetListRowEntry = new WidgetListRowEntry((PackageItemInfo) entry.getKey(), (ArrayList) entry.getValue());
            widgetListRowEntry.titleSectionName = this.mIndexer.computeSectionName(widgetListRowEntry.pkgItem.title);
            Collections.sort(widgetListRowEntry.widgets, widgetItemComparator);
            arrayList.add(widgetListRowEntry);
        }
        WidgetListRowEntryComparator widgetListRowEntryComparator = new WidgetListRowEntryComparator();
        Collections.sort(arrayList, widgetListRowEntryComparator);
        this.mDiffReporter.process(this.mEntries, arrayList, widgetListRowEntryComparator);
    }

    public int getItemCount() {
        return this.mEntries.size();
    }

    public String getSectionName(int i) {
        return ((WidgetListRowEntry) this.mEntries.get(i)).titleSectionName;
    }

    public List<WidgetItem> copyWidgetsForPackageUser(PackageUserKey packageUserKey) {
        Iterator it = this.mEntries.iterator();
        while (it.hasNext()) {
            WidgetListRowEntry widgetListRowEntry = (WidgetListRowEntry) it.next();
            if (widgetListRowEntry.pkgItem.packageName.equals(packageUserKey.mPackageName)) {
                ArrayList arrayList = new ArrayList(widgetListRowEntry.widgets);
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    if (!((WidgetItem) it2.next()).user.equals(packageUserKey.mUser)) {
                        it2.remove();
                    }
                }
                if (arrayList.isEmpty()) {
                    arrayList = null;
                }
                return arrayList;
            }
        }
        return null;
    }

    public void onBindViewHolder(WidgetsRowViewHolder widgetsRowViewHolder, int i) {
        WidgetListRowEntry widgetListRowEntry = (WidgetListRowEntry) this.mEntries.get(i);
        ArrayList<WidgetItem> arrayList = widgetListRowEntry.widgets;
        ViewGroup viewGroup = widgetsRowViewHolder.cellContainer;
        int size = arrayList.size() + Math.max(0, arrayList.size() - 1);
        int childCount = viewGroup.getChildCount();
        if (size > childCount) {
            while (childCount < size) {
                if ((childCount & 1) == 1) {
                    this.mLayoutInflater.inflate(C0622R.layout.widget_list_divider, viewGroup);
                } else {
                    WidgetCell widgetCell = (WidgetCell) this.mLayoutInflater.inflate(C0622R.layout.widget_cell, viewGroup, false);
                    widgetCell.setOnClickListener(this.mIconClickListener);
                    widgetCell.setOnLongClickListener(this.mIconLongClickListener);
                    viewGroup.addView(widgetCell);
                }
                childCount++;
            }
        } else if (size < childCount) {
            while (size < childCount) {
                viewGroup.getChildAt(size).setVisibility(8);
                size++;
            }
        }
        widgetsRowViewHolder.title.applyFromPackageItemInfo(widgetListRowEntry.pkgItem);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            int i3 = i2 * 2;
            WidgetCell widgetCell2 = (WidgetCell) viewGroup.getChildAt(i3);
            widgetCell2.applyFromCellItem((WidgetItem) arrayList.get(i2), this.mWidgetPreviewLoader);
            widgetCell2.ensurePreview();
            widgetCell2.setVisibility(0);
            if (i2 > 0) {
                viewGroup.getChildAt(i3 - 1).setVisibility(0);
            }
        }
    }

    public WidgetsRowViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewGroup viewGroup2 = (ViewGroup) this.mLayoutInflater.inflate(C0622R.layout.widgets_list_row_view, viewGroup, false);
        viewGroup2.findViewById(C0622R.C0625id.widgets_cell_list).setPaddingRelative(this.mIndent, 0, 1, 0);
        return new WidgetsRowViewHolder(viewGroup2);
    }

    public void onViewRecycled(WidgetsRowViewHolder widgetsRowViewHolder) {
        int childCount = widgetsRowViewHolder.cellContainer.getChildCount();
        for (int i = 0; i < childCount; i += 2) {
            ((WidgetCell) widgetsRowViewHolder.cellContainer.getChildAt(i)).clear();
        }
    }
}
