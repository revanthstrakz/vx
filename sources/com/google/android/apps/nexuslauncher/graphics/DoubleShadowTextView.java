package com.google.android.apps.nexuslauncher.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.launcher3.views.DoubleShadowBubbleTextView.ShadowInfo;

public class DoubleShadowTextView extends TextView {
    private final ShadowInfo mShadowInfo;

    public DoubleShadowTextView(Context context) {
        this(context, null);
    }

    public DoubleShadowTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DoubleShadowTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShadowInfo = new ShadowInfo(context, attributeSet, i);
        setShadowLayer(Math.max(this.mShadowInfo.keyShadowBlur + this.mShadowInfo.keyShadowOffset, this.mShadowInfo.ambientShadowBlur), 0.0f, 0.0f, this.mShadowInfo.keyShadowColor);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mShadowInfo.skipDoubleShadow(this)) {
            super.onDraw(canvas);
            return;
        }
        getPaint().setShadowLayer(this.mShadowInfo.ambientShadowBlur, 0.0f, 0.0f, this.mShadowInfo.ambientShadowColor);
        super.onDraw(canvas);
        getPaint().setShadowLayer(this.mShadowInfo.keyShadowBlur, 0.0f, this.mShadowInfo.keyShadowOffset, this.mShadowInfo.keyShadowColor);
        super.onDraw(canvas);
    }
}
