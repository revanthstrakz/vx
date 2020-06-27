package com.android.launcher3.dragndrop;

import android.graphics.Point;
import com.android.launcher3.DropTarget.DragObject;

public class DragOptions {
    public boolean isAccessibleDrag = false;
    public PreDragCondition preDragCondition = null;
    public Point systemDndStartPoint = null;

    public interface PreDragCondition {
        void onPreDragEnd(DragObject dragObject, boolean z);

        void onPreDragStart(DragObject dragObject);

        boolean shouldStartDrag(double d);
    }
}
