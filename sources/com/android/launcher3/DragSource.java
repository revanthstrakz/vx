package com.android.launcher3;

import android.view.View;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider;

public interface DragSource extends LogContainerProvider {
    float getIntrinsicIconScaleFactor();

    void onDropCompleted(View view, DragObject dragObject, boolean z, boolean z2);

    boolean supportsAppInfoDropTarget();

    boolean supportsDeleteDropTarget();
}
