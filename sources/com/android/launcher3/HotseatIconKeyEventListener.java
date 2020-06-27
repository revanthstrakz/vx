package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

/* compiled from: FocusHelper */
class HotseatIconKeyEventListener implements OnKeyListener {
    HotseatIconKeyEventListener() {
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return FocusHelper.handleHotseatButtonKeyEvent(view, i, keyEvent);
    }
}
