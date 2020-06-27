package com.android.launcher3;

import android.graphics.Rect;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.dragndrop.DragView;

public interface DropTarget {

    public static class DragObject {
        public boolean accessibleDrag;
        public boolean cancelled = false;
        public boolean deferDragViewCleanupPostAnimation = true;
        public boolean dragComplete = false;
        public ItemInfo dragInfo = null;
        public DragSource dragSource = null;
        public DragView dragView = null;
        public ItemInfo originalDragInfo = null;
        public Runnable postAnimationRunnable = null;
        public DragViewStateAnnouncer stateAnnouncer;

        /* renamed from: x */
        public int f49x = -1;
        public int xOffset = -1;

        /* renamed from: y */
        public int f50y = -1;
        public int yOffset = -1;

        public final float[] getVisualCenter(float[] fArr) {
            if (fArr == null) {
                fArr = new float[2];
            }
            int i = this.f50y - this.yOffset;
            fArr[0] = (float) ((this.f49x - this.xOffset) + (this.dragView.getDragRegion().width() / 2));
            fArr[1] = (float) (i + (this.dragView.getDragRegion().height() / 2));
            return fArr;
        }
    }

    boolean acceptDrop(DragObject dragObject);

    void getHitRectRelativeToDragLayer(Rect rect);

    boolean isDropEnabled();

    void onDragEnter(DragObject dragObject);

    void onDragExit(DragObject dragObject);

    void onDragOver(DragObject dragObject);

    void onDrop(DragObject dragObject);

    void prepareAccessibilityDrop();
}
