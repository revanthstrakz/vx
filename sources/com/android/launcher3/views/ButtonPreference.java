package com.android.launcher3.views;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ButtonPreference extends Preference {
    private View mView;
    private boolean mWidgetFrameVisible = false;

    public ButtonPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public ButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public ButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ButtonPreference(Context context) {
        super(context);
    }

    public void setWidgetFrameVisible(boolean z) {
        if (this.mWidgetFrameVisible != z) {
            this.mWidgetFrameVisible = z;
            notifyChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        this.mView = view;
        ViewGroup viewGroup = (ViewGroup) view.findViewById(16908312);
        if (viewGroup != null) {
            viewGroup.setVisibility(this.mWidgetFrameVisible ? 0 : 8);
        }
    }

    public View getView() {
        return this.mView;
    }
}
