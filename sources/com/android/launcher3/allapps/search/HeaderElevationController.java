package com.android.launcher3.allapps.search;

import android.content.res.Resources;
import android.graphics.Outline;
import android.support.p004v7.widget.RecyclerView;
import android.support.p004v7.widget.RecyclerView.OnScrollListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.C0622R;

public class HeaderElevationController extends OnScrollListener {
    private int mCurrentY = 0;
    private final View mHeader;
    private final View mHeaderChild;
    /* access modifiers changed from: private */
    public final float mMaxElevation;
    private final float mScrollToElevation;

    public HeaderElevationController(View view) {
        this.mHeader = view;
        Resources resources = this.mHeader.getContext().getResources();
        this.mMaxElevation = resources.getDimension(C0622R.dimen.all_apps_header_max_elevation);
        this.mScrollToElevation = resources.getDimension(C0622R.dimen.all_apps_header_scroll_to_elevation);
        this.mHeader.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                int i = -view.getLeft();
                int i2 = -view.getTop();
                int width = view.getWidth() - i;
                int access$000 = (int) HeaderElevationController.this.mMaxElevation;
                outline.setRect(i - access$000, i2 - access$000, width + access$000, view.getHeight());
            }
        });
        this.mHeaderChild = ((ViewGroup) this.mHeader).getChildAt(0);
    }

    public void reset() {
        this.mCurrentY = 0;
        onScroll(this.mCurrentY);
    }

    public final void onScrolled(RecyclerView recyclerView, int i, int i2) {
        this.mCurrentY = ((BaseRecyclerView) recyclerView).getCurrentScrollY();
        onScroll(this.mCurrentY);
    }

    private void onScroll(int i) {
        float min = this.mMaxElevation * (Math.min((float) i, this.mScrollToElevation) / this.mScrollToElevation);
        if (Float.compare(this.mHeader.getElevation(), min) != 0) {
            this.mHeader.setElevation(min);
            int min2 = Math.min(this.mHeader.getHeight(), i);
            this.mHeader.setTranslationY((float) (-min2));
            this.mHeaderChild.setTranslationY((float) min2);
        }
    }
}
