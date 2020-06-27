package com.android.launcher3.pageindicators;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.android.launcher3.dynamicui.ExtractedColors;

public abstract class PageIndicator extends FrameLayout {
    private CaretDrawable mCaretDrawable;
    protected int mNumPages = 1;

    /* access modifiers changed from: protected */
    public void onPageCountChanged() {
    }

    public void setActiveMarker(int i) {
    }

    public void setScroll(int i, int i2) {
    }

    public void setShouldAutoHide(boolean z) {
    }

    public void updateColor(ExtractedColors extractedColors) {
    }

    public PageIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setWillNotDraw(false);
    }

    public void addMarker() {
        this.mNumPages++;
        onPageCountChanged();
    }

    public void removeMarker() {
        this.mNumPages--;
        onPageCountChanged();
    }

    public void setMarkersCount(int i) {
        this.mNumPages = i;
        onPageCountChanged();
    }

    public CaretDrawable getCaretDrawable() {
        return this.mCaretDrawable;
    }

    public void setCaretDrawable(CaretDrawable caretDrawable) {
        if (this.mCaretDrawable != null) {
            this.mCaretDrawable.setCallback(null);
        }
        this.mCaretDrawable = caretDrawable;
        if (this.mCaretDrawable != null) {
            this.mCaretDrawable.setCallback(this);
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == getCaretDrawable();
    }
}
