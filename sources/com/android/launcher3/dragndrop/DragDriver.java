package com.android.launcher3.dragndrop;

import android.content.Context;
import android.view.DragEvent;
import android.view.MotionEvent;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Utilities;

public abstract class DragDriver {
    protected final EventListener mEventListener;

    public interface EventListener {
        void onDriverDragCancel();

        void onDriverDragEnd(float f, float f2);

        void onDriverDragExitWindow();

        void onDriverDragMove(float f, float f2);
    }

    public abstract boolean onDragEvent(DragEvent dragEvent);

    public void onDragViewAnimationEnd() {
    }

    public DragDriver(EventListener eventListener) {
        this.mEventListener = eventListener;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 1:
                this.mEventListener.onDriverDragMove(motionEvent.getX(), motionEvent.getY());
                this.mEventListener.onDriverDragEnd(motionEvent.getX(), motionEvent.getY());
                break;
            case 2:
                this.mEventListener.onDriverDragMove(motionEvent.getX(), motionEvent.getY());
                break;
            case 3:
                this.mEventListener.onDriverDragCancel();
                break;
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 1) {
            this.mEventListener.onDriverDragEnd(motionEvent.getX(), motionEvent.getY());
        } else if (action == 3) {
            this.mEventListener.onDriverDragCancel();
        }
        return true;
    }

    public static DragDriver create(Context context, DragController dragController, DragObject dragObject, DragOptions dragOptions) {
        if (!Utilities.ATLEAST_NOUGAT || dragOptions.systemDndStartPoint == null) {
            return new InternalDragDriver(dragController);
        }
        return new SystemDragDriver(dragController, context, dragObject);
    }
}
