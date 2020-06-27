package com.android.launcher3.keyboard;

import android.graphics.Rect;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import com.android.launcher3.PagedView;

public class ViewGroupFocusHelper extends FocusIndicatorHelper {
    private final View mContainer;

    public ViewGroupFocusHelper(View view) {
        super(view);
        this.mContainer = view;
    }

    public void viewToRect(View view, Rect rect) {
        rect.left = 0;
        rect.top = 0;
        computeLocationRelativeToContainer(view, rect);
        rect.left = (int) (((float) rect.left) + (((1.0f - view.getScaleX()) * ((float) view.getWidth())) / 2.0f));
        rect.top = (int) (((float) rect.top) + (((1.0f - view.getScaleY()) * ((float) view.getHeight())) / 2.0f));
        rect.right = rect.left + ((int) (view.getScaleX() * ((float) view.getWidth())));
        rect.bottom = rect.top + ((int) (view.getScaleY() * ((float) view.getHeight())));
    }

    private void computeLocationRelativeToContainer(View view, Rect rect) {
        View view2 = (View) view.getParent();
        rect.left += view.getLeft();
        rect.top += view.getTop();
        if (view2 != this.mContainer) {
            if (view2 instanceof PagedView) {
                PagedView pagedView = (PagedView) view2;
                rect.left -= pagedView.getScrollForPage(pagedView.indexOfChild(view));
            }
            computeLocationRelativeToContainer(view2, rect);
        }
    }

    public OnFocusChangeListener getHideIndicatorOnFocusListener() {
        return new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    ViewGroupFocusHelper.this.endCurrentAnimation();
                    ViewGroupFocusHelper.this.setCurrentView(null);
                    ViewGroupFocusHelper.this.setAlpha(0.0f);
                    ViewGroupFocusHelper.this.invalidateDirty();
                }
            }
        };
    }
}
