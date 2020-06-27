package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/* compiled from: FocusHelper */
class IconKeyEventListener implements OnKeyListener {
    IconKeyEventListener() {
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return FocusHelper.handleIconKeyEvent(view, i, keyEvent);
    }
}
