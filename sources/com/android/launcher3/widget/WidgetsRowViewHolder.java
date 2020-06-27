package com.android.launcher3.widget;

import android.support.p004v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;

public class WidgetsRowViewHolder extends ViewHolder {
    public final ViewGroup cellContainer;
    public final BubbleTextView title;

    public WidgetsRowViewHolder(ViewGroup viewGroup) {
        super(viewGroup);
        this.cellContainer = (ViewGroup) viewGroup.findViewById(C0622R.C0625id.widgets_cell_list);
        this.title = (BubbleTextView) viewGroup.findViewById(C0622R.C0625id.section);
    }
}
