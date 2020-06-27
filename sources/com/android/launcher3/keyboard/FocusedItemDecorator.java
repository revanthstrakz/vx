package com.android.launcher3.keyboard;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.p004v7.widget.RecyclerView;
import android.support.p004v7.widget.RecyclerView.ItemDecoration;
import android.support.p004v7.widget.RecyclerView.State;
import android.view.View;
import android.view.View.OnFocusChangeListener;

public class FocusedItemDecorator extends ItemDecoration {
    private FocusIndicatorHelper mHelper;

    public FocusedItemDecorator(View view) {
        this.mHelper = new FocusIndicatorHelper(view) {
            public void viewToRect(View view, Rect rect) {
                rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            }
        };
    }

    public OnFocusChangeListener getFocusListener() {
        return this.mHelper;
    }

    public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
        this.mHelper.draw(canvas);
    }
}
