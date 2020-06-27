package com.android.launcher3.pageindicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;

public class PageIndicatorCaretLandscape extends PageIndicator {
    public PageIndicatorCaretLandscape(Context context) {
        this(context, null);
    }

    public PageIndicatorCaretLandscape(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PageIndicatorCaretLandscape(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(C0622R.dimen.all_apps_caret_size);
        CaretDrawable caretDrawable = new CaretDrawable(context);
        caretDrawable.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
        setCaretDrawable(caretDrawable);
        Launcher launcher = Launcher.getLauncher(context);
        setOnClickListener(launcher);
        setOnLongClickListener(launcher);
        setOnFocusChangeListener(launcher.mFocusHandler);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Rect bounds = getCaretDrawable().getBounds();
        int save = canvas.save();
        canvas.translate((float) ((getWidth() - bounds.width()) / 2), (float) (getHeight() - bounds.height()));
        getCaretDrawable().draw(canvas);
        canvas.restoreToCount(save);
    }
}
