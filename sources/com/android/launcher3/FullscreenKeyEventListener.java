package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/* compiled from: FocusHelper */
class FullscreenKeyEventListener implements OnKeyListener {
    FullscreenKeyEventListener() {
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == 21 || i == 22 || i == 93 || i == 92) {
            return FocusHelper.handleIconKeyEvent(view, i, keyEvent);
        }
        return false;
    }
}
