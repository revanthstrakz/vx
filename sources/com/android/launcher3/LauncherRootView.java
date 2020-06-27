package com.android.launcher3;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup.MarginLayoutParams;
import com.android.launcher3.util.SystemUiController;

public class LauncherRootView extends InsettableFrameLayout {
    private View mAlignedView;
    @ExportedProperty(category = "launcher")
    private boolean mDrawSideInsetBar;
    @ExportedProperty(category = "launcher")
    private int mLeftInsetBarWidth;
    private final Paint mOpaquePaint = new Paint(1);
    @ExportedProperty(category = "launcher")
    private int mRightInsetBarWidth;

    public LauncherRootView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOpaquePaint.setColor(-16777216);
        this.mOpaquePaint.setStyle(Style.FILL);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        if (getChildCount() > 0) {
            this.mAlignedView = getChildAt(0);
        }
        super.onFinishInflate();
    }

    /* access modifiers changed from: protected */
    @TargetApi(23)
    public boolean fitSystemWindows(Rect rect) {
        int i = 0;
        this.mDrawSideInsetBar = (rect.right > 0 || rect.left > 0) && (!Utilities.ATLEAST_MARSHMALLOW || ((ActivityManager) getContext().getSystemService(ActivityManager.class)).isLowRamDevice());
        if (this.mDrawSideInsetBar) {
            this.mLeftInsetBarWidth = rect.left;
            this.mRightInsetBarWidth = rect.right;
            rect = new Rect(0, rect.top, 0, rect.bottom);
        } else {
            this.mRightInsetBarWidth = 0;
            this.mLeftInsetBarWidth = 0;
        }
        SystemUiController systemUiController = Launcher.getLauncher(getContext()).getSystemUiController();
        if (this.mDrawSideInsetBar) {
            i = 2;
        }
        systemUiController.updateUiState(3, i);
        boolean z = !this.mInsets.equals(rect);
        setInsets(rect);
        if (this.mAlignedView != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.mAlignedView.getLayoutParams();
            if (!(marginLayoutParams.leftMargin == this.mLeftInsetBarWidth && marginLayoutParams.rightMargin == this.mRightInsetBarWidth)) {
                marginLayoutParams.leftMargin = this.mLeftInsetBarWidth;
                marginLayoutParams.rightMargin = this.mRightInsetBarWidth;
                this.mAlignedView.setLayoutParams(marginLayoutParams);
            }
        }
        if (z) {
            Launcher.getLauncher(getContext()).onInsetsChanged(rect);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mDrawSideInsetBar) {
            if (this.mRightInsetBarWidth > 0) {
                int width = getWidth();
                canvas.drawRect((float) (width - this.mRightInsetBarWidth), 0.0f, (float) width, (float) getHeight(), this.mOpaquePaint);
            }
            if (this.mLeftInsetBarWidth > 0) {
                canvas.drawRect(0.0f, 0.0f, (float) this.mLeftInsetBarWidth, (float) getHeight(), this.mOpaquePaint);
            }
        }
    }
}
