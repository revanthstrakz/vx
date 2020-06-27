package p013io.virtualapp.widgets;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.p004v7.widget.RecyclerView.Adapter;
import android.support.p004v7.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;

/* renamed from: io.virtualapp.widgets.DragSelectRecyclerViewAdapter */
public abstract class DragSelectRecyclerViewAdapter<VH extends ViewHolder> extends Adapter<VH> {
    private int mLastCount = -1;
    private int mMaxSelectionCount = -1;
    private ArrayList<Integer> mSelectedIndices = new ArrayList<>();
    private SelectionListener mSelectionListener;

    /* renamed from: io.virtualapp.widgets.DragSelectRecyclerViewAdapter$SelectionListener */
    public interface SelectionListener {
        void onDragSelectionChanged(int i);
    }

    /* access modifiers changed from: protected */
    public boolean isIndexSelectable(int i) {
        return true;
    }

    protected DragSelectRecyclerViewAdapter() {
    }

    private void fireSelectionListener() {
        if (this.mLastCount != this.mSelectedIndices.size()) {
            this.mLastCount = this.mSelectedIndices.size();
            if (this.mSelectionListener != null) {
                this.mSelectionListener.onDragSelectionChanged(this.mLastCount);
            }
        }
    }

    public void setMaxSelectionCount(int i) {
        this.mMaxSelectionCount = i;
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.mSelectionListener = selectionListener;
    }

    public void saveInstanceState(Bundle bundle) {
        saveInstanceState("selected_indices", bundle);
    }

    public void saveInstanceState(String str, Bundle bundle) {
        bundle.putSerializable(str, this.mSelectedIndices);
    }

    public void restoreInstanceState(Bundle bundle) {
        restoreInstanceState("selected_indices", bundle);
    }

    public void restoreInstanceState(String str, Bundle bundle) {
        if (bundle != null && bundle.containsKey(str)) {
            this.mSelectedIndices = (ArrayList) bundle.getSerializable(str);
            if (this.mSelectedIndices == null) {
                this.mSelectedIndices = new ArrayList<>();
            } else {
                fireSelectionListener();
            }
        }
    }

    public final void setSelected(int i, boolean z) {
        if (!isIndexSelectable(i)) {
            z = false;
        }
        if (z) {
            if (!this.mSelectedIndices.contains(Integer.valueOf(i)) && (this.mMaxSelectionCount == -1 || this.mSelectedIndices.size() < this.mMaxSelectionCount)) {
                this.mSelectedIndices.add(Integer.valueOf(i));
                notifyItemChanged(i);
            }
        } else if (this.mSelectedIndices.contains(Integer.valueOf(i))) {
            this.mSelectedIndices.remove(Integer.valueOf(i));
            notifyItemChanged(i);
        }
        fireSelectionListener();
    }

    public final boolean toggleSelected(int i) {
        boolean z = false;
        if (isIndexSelectable(i)) {
            if (this.mSelectedIndices.contains(Integer.valueOf(i))) {
                this.mSelectedIndices.remove(Integer.valueOf(i));
            } else if (this.mMaxSelectionCount == -1 || this.mSelectedIndices.size() < this.mMaxSelectionCount) {
                this.mSelectedIndices.add(Integer.valueOf(i));
                z = true;
            }
            notifyItemChanged(i);
        }
        fireSelectionListener();
        return z;
    }

    @CallSuper
    public void onBindViewHolder(VH vh, int i) {
        vh.itemView.setTag(vh);
    }

    public final void selectRange(int i, int i2, int i3, int i4) {
        if (i == i2) {
            while (i3 <= i4) {
                if (i3 != i) {
                    setSelected(i3, false);
                }
                i3++;
            }
            fireSelectionListener();
            return;
        }
        if (i2 < i) {
            for (int i5 = i2; i5 <= i; i5++) {
                setSelected(i5, true);
            }
            if (i3 > -1 && i3 < i2) {
                while (i3 < i2) {
                    if (i3 != i) {
                        setSelected(i3, false);
                    }
                    i3++;
                }
            }
            if (i4 > -1) {
                for (int i6 = i + 1; i6 <= i4; i6++) {
                    setSelected(i6, false);
                }
            }
        } else {
            for (int i7 = i; i7 <= i2; i7++) {
                setSelected(i7, true);
            }
            if (i4 > -1 && i4 > i2) {
                for (int i8 = i2 + 1; i8 <= i4; i8++) {
                    if (i8 != i) {
                        setSelected(i8, false);
                    }
                }
            }
            if (i3 > -1) {
                while (i3 < i) {
                    setSelected(i3, false);
                    i3++;
                }
            }
        }
        fireSelectionListener();
    }

    public final void selectAll() {
        int itemCount = getItemCount();
        this.mSelectedIndices.clear();
        for (int i = 0; i < itemCount; i++) {
            if (isIndexSelectable(i)) {
                this.mSelectedIndices.add(Integer.valueOf(i));
            }
        }
        notifyDataSetChanged();
        fireSelectionListener();
    }

    public final void clearSelected() {
        this.mSelectedIndices.clear();
        notifyDataSetChanged();
        fireSelectionListener();
    }

    public final int getSelectedCount() {
        return this.mSelectedIndices.size();
    }

    public final Integer[] getSelectedIndices() {
        return (Integer[]) this.mSelectedIndices.toArray(new Integer[this.mSelectedIndices.size()]);
    }

    public final boolean isIndexSelected(int i) {
        return this.mSelectedIndices.contains(Integer.valueOf(i));
    }
}
