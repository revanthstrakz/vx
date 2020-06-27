package com.android.launcher3.accessibility;

import android.view.View;
import android.view.accessibility.AccessibilityManager;
import com.android.launcher3.Launcher;

public class DragViewStateAnnouncer implements Runnable {
    private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;
    private final View mTargetView;

    private DragViewStateAnnouncer(View view) {
        this.mTargetView = view;
    }

    public void announce(CharSequence charSequence) {
        this.mTargetView.setContentDescription(charSequence);
        this.mTargetView.removeCallbacks(this);
        this.mTargetView.postDelayed(this, 200);
    }

    public void cancel() {
        this.mTargetView.removeCallbacks(this);
    }

    public void run() {
        this.mTargetView.sendAccessibilityEvent(4);
    }

    public void completeAction(int i) {
        cancel();
        Launcher launcher = Launcher.getLauncher(this.mTargetView.getContext());
        launcher.getDragLayer().announceForAccessibility(launcher.getText(i));
    }

    public static DragViewStateAnnouncer createFor(View view) {
        if (((AccessibilityManager) view.getContext().getSystemService("accessibility")).isEnabled()) {
            return new DragViewStateAnnouncer(view);
        }
        return null;
    }
}
