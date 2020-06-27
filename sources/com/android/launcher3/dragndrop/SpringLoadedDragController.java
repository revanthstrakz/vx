package com.android.launcher3.dragndrop;

import com.android.launcher3.Alarm;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.Workspace;

public class SpringLoadedDragController implements OnAlarmListener {
    final long ENTER_SPRING_LOAD_CANCEL_HOVER_TIME = 950;
    final long ENTER_SPRING_LOAD_HOVER_TIME = 500;
    Alarm mAlarm;
    private Launcher mLauncher;
    private CellLayout mScreen;

    public SpringLoadedDragController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mAlarm = new Alarm();
        this.mAlarm.setOnAlarmListener(this);
    }

    public void cancel() {
        this.mAlarm.cancelAlarm();
    }

    public void setAlarm(CellLayout cellLayout) {
        this.mAlarm.cancelAlarm();
        this.mAlarm.setAlarm(cellLayout == null ? 950 : 500);
        this.mScreen = cellLayout;
    }

    public void onAlarm(Alarm alarm) {
        if (this.mScreen != null) {
            Workspace workspace = this.mLauncher.getWorkspace();
            int indexOfChild = workspace.indexOfChild(this.mScreen);
            if (indexOfChild != workspace.getCurrentPage()) {
                workspace.snapToPage(indexOfChild);
                return;
            }
            return;
        }
        this.mLauncher.getDragController().cancelDrag();
    }
}
