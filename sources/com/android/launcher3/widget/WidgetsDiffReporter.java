package com.android.launcher3.widget;

import com.android.launcher3.IconCache;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.widget.WidgetsListAdapter.WidgetListRowEntryComparator;
import java.util.ArrayList;
import java.util.Iterator;

public class WidgetsDiffReporter {
    private final boolean DEBUG = false;
    private final String TAG = "WidgetsDiffReporter";
    private final IconCache mIconCache;
    private NotifyListener mListener;

    public interface NotifyListener {
        void notifyDataSetChanged();

        void notifyItemChanged(int i);

        void notifyItemInserted(int i);

        void notifyItemRemoved(int i);
    }

    public WidgetsDiffReporter(IconCache iconCache) {
        this.mIconCache = iconCache;
    }

    public void setListener(NotifyListener notifyListener) {
        this.mListener = notifyListener;
    }

    public void process(ArrayList<WidgetListRowEntry> arrayList, ArrayList<WidgetListRowEntry> arrayList2, WidgetListRowEntryComparator widgetListRowEntryComparator) {
        int i;
        if (arrayList.size() != 0 || arrayList2.size() < 0) {
            Iterator it = ((ArrayList) arrayList.clone()).iterator();
            Iterator it2 = arrayList2.iterator();
            WidgetListRowEntry widgetListRowEntry = it.hasNext() ? (WidgetListRowEntry) it.next() : null;
            WidgetListRowEntry widgetListRowEntry2 = it2.hasNext() ? (WidgetListRowEntry) it2.next() : null;
            while (true) {
                int comparePackageName = comparePackageName(widgetListRowEntry, widgetListRowEntry2, widgetListRowEntryComparator);
                if (comparePackageName < 0) {
                    int indexOf = arrayList.indexOf(widgetListRowEntry);
                    this.mListener.notifyItemRemoved(indexOf);
                    arrayList.remove(indexOf);
                    widgetListRowEntry = it.hasNext() ? (WidgetListRowEntry) it.next() : null;
                } else if (comparePackageName > 0) {
                    if (widgetListRowEntry != null) {
                        i = arrayList.indexOf(widgetListRowEntry);
                    } else {
                        i = arrayList.size();
                    }
                    arrayList.add(i, widgetListRowEntry2);
                    widgetListRowEntry2 = it2.hasNext() ? (WidgetListRowEntry) it2.next() : null;
                    this.mListener.notifyItemInserted(i);
                } else {
                    if (!isSamePackageItemInfo(widgetListRowEntry.pkgItem, widgetListRowEntry2.pkgItem) || !widgetListRowEntry.widgets.equals(widgetListRowEntry2.widgets)) {
                        int indexOf2 = arrayList.indexOf(widgetListRowEntry);
                        arrayList.set(indexOf2, widgetListRowEntry2);
                        this.mListener.notifyItemChanged(indexOf2);
                    }
                    widgetListRowEntry = it.hasNext() ? (WidgetListRowEntry) it.next() : null;
                    widgetListRowEntry2 = it2.hasNext() ? (WidgetListRowEntry) it2.next() : null;
                }
                if (widgetListRowEntry == null && widgetListRowEntry2 == null) {
                    return;
                }
            }
        } else {
            arrayList.addAll(arrayList2);
            this.mListener.notifyDataSetChanged();
        }
    }

    private int comparePackageName(WidgetListRowEntry widgetListRowEntry, WidgetListRowEntry widgetListRowEntry2, WidgetListRowEntryComparator widgetListRowEntryComparator) {
        if (widgetListRowEntry == null && widgetListRowEntry2 == null) {
            throw new IllegalStateException("Cannot compare PackageItemInfo if both rows are null.");
        } else if (widgetListRowEntry == null && widgetListRowEntry2 != null) {
            return 1;
        } else {
            if (widgetListRowEntry == null || widgetListRowEntry2 != null) {
                return widgetListRowEntryComparator.compare(widgetListRowEntry, widgetListRowEntry2);
            }
            return -1;
        }
    }

    private boolean isSamePackageItemInfo(PackageItemInfo packageItemInfo, PackageItemInfo packageItemInfo2) {
        return packageItemInfo.iconBitmap.equals(packageItemInfo2.iconBitmap) && !this.mIconCache.isDefaultIcon(packageItemInfo.iconBitmap, packageItemInfo.user);
    }
}
