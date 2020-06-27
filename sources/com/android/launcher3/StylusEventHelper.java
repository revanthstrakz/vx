package com.android.launcher3;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class StylusEventHelper {
    private boolean mIsButtonPressed;
    private StylusButtonListener mListener;
    private final float mSlop;
    private View mView;

    public interface StylusButtonListener {
        boolean onPressed(MotionEvent motionEvent);

        boolean onReleased(MotionEvent motionEvent);
    }

    public StylusEventHelper(StylusButtonListener stylusButtonListener, View view) {
        this.mListener = stylusButtonListener;
        this.mView = view;
        if (this.mView != null) {
            this.mSlop = (float) ViewConfiguration.get(this.mView.getContext()).getScaledTouchSlop();
        } else {
            this.mSlop = (float) ViewConfiguration.getTouchSlop();
        }
    }

    public boolean onMotionEvent(MotionEvent motionEvent) {
        boolean isStylusButtonPressed = isStylusButtonPressed(motionEvent);
        switch (motionEvent.getAction()) {
            case 0:
                this.mIsButtonPressed = isStylusButtonPressed;
                if (this.mIsButtonPressed) {
                    return this.mListener.onPressed(motionEvent);
                }
                break;
            case 1:
            case 3:
                if (this.mIsButtonPressed) {
                    this.mIsButtonPressed = false;
                    return this.mListener.onReleased(motionEvent);
                }
                break;
            case 2:
                if (!Utilities.pointInView(this.mView, motionEvent.getX(), motionEvent.getY(), this.mSlop)) {
                    return false;
                }
                if (!this.mIsButtonPressed && isStylusButtonPressed) {
                    this.mIsButtonPressed = true;
                    return this.mListener.onPressed(motionEvent);
                } else if (this.mIsButtonPressed && !isStylusButtonPressed) {
                    this.mIsButtonPressed = false;
                    return this.mListener.onReleased(motionEvent);
                }
        }
        return false;
    }

    public boolean inStylusButtonPressed() {
        return this.mIsButtonPressed;
    }

    private static boolean isStylusButtonPressed(MotionEvent motionEvent) {
        if (motionEvent.getToolType(0) == 2 && (motionEvent.getButtonState() & 2) == 2) {
            return true;
        }
        return false;
    }
}
