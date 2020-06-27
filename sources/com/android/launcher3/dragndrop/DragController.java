package com.android.launcher3.dragndrop;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.android.launcher3.C0622R;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.dragndrop.DragDriver.EventListener;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.TouchController;
import java.util.ArrayList;
import java.util.Iterator;

public class DragController implements EventListener, TouchController {
    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;
    private final int[] mCoordinatesTemp = new int[2];
    int mDistanceSinceScroll = 0;
    private DragDriver mDragDriver = null;
    private Rect mDragLayerRect = new Rect();
    private DragObject mDragObject;
    private ArrayList<DropTarget> mDropTargets = new ArrayList<>();
    private FlingToDeleteHelper mFlingToDeleteHelper;
    private boolean mIsInPreDrag;
    private DropTarget mLastDropTarget;
    int[] mLastTouch = new int[2];
    long mLastTouchUpTime = -1;
    Launcher mLauncher;
    private ArrayList<DragListener> mListeners = new ArrayList<>();
    private int mMotionDownX;
    private int mMotionDownY;
    private View mMoveTarget;
    private DragOptions mOptions;
    private Rect mRectTemp = new Rect();
    private int[] mTmpPoint = new int[2];
    private IBinder mWindowToken;

    public interface DragListener {
        void onDragEnd();

        void onDragStart(DragObject dragObject, DragOptions dragOptions);
    }

    public DragController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mFlingToDeleteHelper = new FlingToDeleteHelper(launcher);
    }

    public DragView startDrag(Bitmap bitmap, int i, int i2, DragSource dragSource, ItemInfo itemInfo, Point point, Rect rect, float f, DragOptions dragOptions) {
        int i3;
        int i4;
        ItemInfo itemInfo2 = itemInfo;
        Point point2 = point;
        Rect rect2 = rect;
        ((InputMethodManager) this.mLauncher.getSystemService("input_method")).hideSoftInputFromWindow(this.mWindowToken, 0);
        this.mOptions = dragOptions;
        if (this.mOptions.systemDndStartPoint != null) {
            this.mMotionDownX = this.mOptions.systemDndStartPoint.x;
            this.mMotionDownY = this.mOptions.systemDndStartPoint.y;
        }
        int i5 = this.mMotionDownX - i;
        int i6 = this.mMotionDownY - i2;
        if (rect2 == null) {
            i3 = 0;
        } else {
            i3 = rect2.left;
        }
        if (rect2 == null) {
            i4 = 0;
        } else {
            i4 = rect2.top;
        }
        this.mLastDropTarget = null;
        this.mDragObject = new DragObject();
        this.mIsInPreDrag = this.mOptions.preDragCondition != null && !this.mOptions.preDragCondition.shouldStartDrag(0.0d);
        float dimensionPixelSize = this.mIsInPreDrag ? (float) this.mLauncher.getResources().getDimensionPixelSize(C0622R.dimen.pre_drag_view_scale) : 0.0f;
        DragView dragView = r9;
        DragObject dragObject = this.mDragObject;
        DragView dragView2 = new DragView(this.mLauncher, bitmap, i5, i6, f, dimensionPixelSize);
        dragObject.dragView = dragView;
        dragView.setItemInfo(itemInfo2);
        this.mDragObject.dragComplete = false;
        if (this.mOptions.isAccessibleDrag) {
            this.mDragObject.xOffset = bitmap.getWidth() / 2;
            this.mDragObject.yOffset = bitmap.getHeight() / 2;
            this.mDragObject.accessibleDrag = true;
        } else {
            this.mDragObject.xOffset = this.mMotionDownX - (i + i3);
            this.mDragObject.yOffset = this.mMotionDownY - (i2 + i4);
            this.mDragObject.stateAnnouncer = DragViewStateAnnouncer.createFor(dragView);
            this.mDragDriver = DragDriver.create(this.mLauncher, this, this.mDragObject, this.mOptions);
        }
        this.mDragObject.dragSource = dragSource;
        this.mDragObject.dragInfo = itemInfo2;
        this.mDragObject.originalDragInfo = new ItemInfo();
        this.mDragObject.originalDragInfo.copyFrom(itemInfo2);
        Point point3 = point;
        if (point3 != null) {
            dragView.setDragVisualizeOffset(new Point(point3));
        }
        Rect rect3 = rect;
        if (rect3 != null) {
            dragView.setDragRegion(new Rect(rect3));
        }
        this.mLauncher.getDragLayer().performHapticFeedback(0);
        dragView.show(this.mMotionDownX, this.mMotionDownY);
        this.mDistanceSinceScroll = 0;
        if (!this.mIsInPreDrag) {
            callOnDragStart();
        } else if (this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragStart(this.mDragObject);
        }
        this.mLastTouch[0] = this.mMotionDownX;
        this.mLastTouch[1] = this.mMotionDownY;
        handleMoveEvent(this.mMotionDownX, this.mMotionDownY);
        this.mLauncher.getUserEventDispatcher().resetActionDurationMillis();
        return dragView;
    }

    private void callOnDragStart() {
        if (this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragEnd(this.mDragObject, true);
        }
        this.mIsInPreDrag = false;
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((DragListener) it.next()).onDragStart(this.mDragObject, this.mOptions);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return this.mDragDriver != null;
    }

    public boolean isDragging() {
        return this.mDragDriver != null || (this.mOptions != null && this.mOptions.isAccessibleDrag);
    }

    public void cancelDrag() {
        if (isDragging()) {
            if (this.mLastDropTarget != null) {
                this.mLastDropTarget.onDragExit(this.mDragObject);
            }
            this.mDragObject.deferDragViewCleanupPostAnimation = false;
            this.mDragObject.cancelled = true;
            this.mDragObject.dragComplete = true;
            if (!this.mIsInPreDrag) {
                this.mDragObject.dragSource.onDropCompleted(null, this.mDragObject, false, false);
            }
        }
        endDrag();
    }

    public void onAppsRemoved(ItemInfoMatcher itemInfoMatcher) {
        if (this.mDragObject != null) {
            ItemInfo itemInfo = this.mDragObject.dragInfo;
            if (itemInfo instanceof ShortcutInfo) {
                ComponentName targetComponent = itemInfo.getTargetComponent();
                if (targetComponent != null && itemInfoMatcher.matches(itemInfo, targetComponent)) {
                    cancelDrag();
                }
            }
        }
    }

    private void endDrag() {
        if (isDragging()) {
            this.mDragDriver = null;
            boolean z = false;
            if (this.mDragObject.dragView != null) {
                z = this.mDragObject.deferDragViewCleanupPostAnimation;
                if (!z) {
                    this.mDragObject.dragView.remove();
                } else if (this.mIsInPreDrag) {
                    animateDragViewToOriginalPosition(null, null, -1);
                }
                this.mDragObject.dragView = null;
            }
            if (!z) {
                callOnDragEnd();
            }
        }
        this.mFlingToDeleteHelper.releaseVelocityTracker();
    }

    public void animateDragViewToOriginalPosition(final Runnable runnable, final View view, int i) {
        this.mDragObject.dragView.animateTo(this.mMotionDownX, this.mMotionDownY, new Runnable() {
            public void run() {
                if (view != null) {
                    view.setVisibility(0);
                }
                if (runnable != null) {
                    runnable.run();
                }
            }
        }, i);
    }

    private void callOnDragEnd() {
        if (this.mIsInPreDrag && this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragEnd(this.mDragObject, false);
        }
        this.mIsInPreDrag = false;
        this.mOptions = null;
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((DragListener) it.next()).onDragEnd();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDeferredEndDrag(DragView dragView) {
        dragView.remove();
        if (this.mDragObject.deferDragViewCleanupPostAnimation) {
            callOnDragEnd();
        }
    }

    private int[] getClampedDragLayerPos(float f, float f2) {
        this.mLauncher.getDragLayer().getLocalVisibleRect(this.mDragLayerRect);
        this.mTmpPoint[0] = (int) Math.max((float) this.mDragLayerRect.left, Math.min(f, (float) (this.mDragLayerRect.right - 1)));
        this.mTmpPoint[1] = (int) Math.max((float) this.mDragLayerRect.top, Math.min(f2, (float) (this.mDragLayerRect.bottom - 1)));
        return this.mTmpPoint;
    }

    public long getLastGestureUpTime() {
        if (this.mDragDriver != null) {
            return System.currentTimeMillis();
        }
        return this.mLastTouchUpTime;
    }

    public void resetLastGestureUpTime() {
        this.mLastTouchUpTime = -1;
    }

    public void onDriverDragMove(float f, float f2) {
        int[] clampedDragLayerPos = getClampedDragLayerPos(f, f2);
        handleMoveEvent(clampedDragLayerPos[0], clampedDragLayerPos[1]);
    }

    public void onDriverDragExitWindow() {
        if (this.mLastDropTarget != null) {
            this.mLastDropTarget.onDragExit(this.mDragObject);
            this.mLastDropTarget = null;
        }
    }

    public void onDriverDragEnd(float f, float f2) {
        DropTarget dropTarget;
        Runnable flingAnimation = this.mFlingToDeleteHelper.getFlingAnimation(this.mDragObject);
        if (flingAnimation != null) {
            dropTarget = this.mFlingToDeleteHelper.getDropTarget();
        } else {
            dropTarget = findDropTarget((int) f, (int) f2, this.mCoordinatesTemp);
        }
        drop(dropTarget, flingAnimation);
        endDrag();
    }

    public void onDriverDragCancel() {
        cancelDrag();
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (this.mOptions != null && this.mOptions.isAccessibleDrag) {
            return false;
        }
        this.mFlingToDeleteHelper.recordMotionEvent(motionEvent);
        int action = motionEvent.getAction();
        int[] clampedDragLayerPos = getClampedDragLayerPos(motionEvent.getX(), motionEvent.getY());
        int i = clampedDragLayerPos[0];
        int i2 = clampedDragLayerPos[1];
        switch (action) {
            case 0:
                this.mMotionDownX = i;
                this.mMotionDownY = i2;
                break;
            case 1:
                this.mLastTouchUpTime = System.currentTimeMillis();
                break;
        }
        if (this.mDragDriver != null && this.mDragDriver.onInterceptTouchEvent(motionEvent)) {
            z = true;
        }
        return z;
    }

    public boolean onDragEvent(long j, DragEvent dragEvent) {
        this.mFlingToDeleteHelper.recordDragEvent(j, dragEvent);
        return this.mDragDriver != null && this.mDragDriver.onDragEvent(dragEvent);
    }

    public void onDragViewAnimationEnd() {
        if (this.mDragDriver != null) {
            this.mDragDriver.onDragViewAnimationEnd();
        }
    }

    public void setMoveTarget(View view) {
        this.mMoveTarget = view;
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        return this.mMoveTarget != null && this.mMoveTarget.dispatchUnhandledMove(view, i);
    }

    private void handleMoveEvent(int i, int i2) {
        this.mDragObject.dragView.move(i, i2);
        int[] iArr = this.mCoordinatesTemp;
        DropTarget findDropTarget = findDropTarget(i, i2, iArr);
        this.mDragObject.f49x = iArr[0];
        this.mDragObject.f50y = iArr[1];
        checkTouchMove(findDropTarget);
        this.mDistanceSinceScroll = (int) (((double) this.mDistanceSinceScroll) + Math.hypot((double) (this.mLastTouch[0] - i), (double) (this.mLastTouch[1] - i2)));
        this.mLastTouch[0] = i;
        this.mLastTouch[1] = i2;
        if (this.mIsInPreDrag && this.mOptions.preDragCondition != null && this.mOptions.preDragCondition.shouldStartDrag((double) this.mDistanceSinceScroll)) {
            callOnDragStart();
        }
    }

    public float getDistanceDragged() {
        return (float) this.mDistanceSinceScroll;
    }

    public void forceTouchMove() {
        int[] iArr = this.mCoordinatesTemp;
        DropTarget findDropTarget = findDropTarget(this.mLastTouch[0], this.mLastTouch[1], iArr);
        this.mDragObject.f49x = iArr[0];
        this.mDragObject.f50y = iArr[1];
        checkTouchMove(findDropTarget);
    }

    private void checkTouchMove(DropTarget dropTarget) {
        if (dropTarget != null) {
            if (this.mLastDropTarget != dropTarget) {
                if (this.mLastDropTarget != null) {
                    this.mLastDropTarget.onDragExit(this.mDragObject);
                }
                dropTarget.onDragEnter(this.mDragObject);
            }
            dropTarget.onDragOver(this.mDragObject);
        } else if (this.mLastDropTarget != null) {
            this.mLastDropTarget.onDragExit(this.mDragObject);
        }
        this.mLastDropTarget = dropTarget;
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        if (this.mDragDriver == null || this.mOptions == null || this.mOptions.isAccessibleDrag) {
            return false;
        }
        this.mFlingToDeleteHelper.recordMotionEvent(motionEvent);
        int action = motionEvent.getAction();
        int[] clampedDragLayerPos = getClampedDragLayerPos(motionEvent.getX(), motionEvent.getY());
        int i = clampedDragLayerPos[0];
        int i2 = clampedDragLayerPos[1];
        if (action == 0) {
            this.mMotionDownX = i;
            this.mMotionDownY = i2;
        }
        return this.mDragDriver.onTouchEvent(motionEvent);
    }

    public void prepareAccessibleDrag(int i, int i2) {
        this.mMotionDownX = i;
        this.mMotionDownY = i2;
    }

    public void completeAccessibleDrag(int[] iArr) {
        int[] iArr2 = this.mCoordinatesTemp;
        DropTarget findDropTarget = findDropTarget(iArr[0], iArr[1], iArr2);
        this.mDragObject.f49x = iArr2[0];
        this.mDragObject.f50y = iArr2[1];
        checkTouchMove(findDropTarget);
        findDropTarget.prepareAccessibilityDrop();
        drop(findDropTarget, null);
        endDrag();
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0059  */
    /* JADX WARNING: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void drop(com.android.launcher3.DropTarget r6, java.lang.Runnable r7) {
        /*
            r5 = this;
            int[] r0 = r5.mCoordinatesTemp
            com.android.launcher3.DropTarget$DragObject r1 = r5.mDragObject
            r2 = 0
            r3 = r0[r2]
            r1.f49x = r3
            com.android.launcher3.DropTarget$DragObject r1 = r5.mDragObject
            r3 = 1
            r0 = r0[r3]
            r1.f50y = r0
            com.android.launcher3.DropTarget r0 = r5.mLastDropTarget
            if (r6 == r0) goto L_0x0028
            com.android.launcher3.DropTarget r0 = r5.mLastDropTarget
            if (r0 == 0) goto L_0x001f
            com.android.launcher3.DropTarget r0 = r5.mLastDropTarget
            com.android.launcher3.DropTarget$DragObject r1 = r5.mDragObject
            r0.onDragExit(r1)
        L_0x001f:
            r5.mLastDropTarget = r6
            if (r6 == 0) goto L_0x0028
            com.android.launcher3.DropTarget$DragObject r0 = r5.mDragObject
            r6.onDragEnter(r0)
        L_0x0028:
            com.android.launcher3.DropTarget$DragObject r0 = r5.mDragObject
            r0.dragComplete = r3
            if (r6 == 0) goto L_0x004c
            com.android.launcher3.DropTarget$DragObject r0 = r5.mDragObject
            r6.onDragExit(r0)
            com.android.launcher3.DropTarget$DragObject r0 = r5.mDragObject
            boolean r0 = r6.acceptDrop(r0)
            if (r0 == 0) goto L_0x004c
            if (r7 == 0) goto L_0x0041
            r7.run()
            goto L_0x004a
        L_0x0041:
            boolean r0 = r5.mIsInPreDrag
            if (r0 != 0) goto L_0x004a
            com.android.launcher3.DropTarget$DragObject r0 = r5.mDragObject
            r6.onDrop(r0)
        L_0x004a:
            r0 = 1
            goto L_0x004d
        L_0x004c:
            r0 = 0
        L_0x004d:
            boolean r1 = r6 instanceof android.view.View
            if (r1 == 0) goto L_0x0054
            android.view.View r6 = (android.view.View) r6
            goto L_0x0055
        L_0x0054:
            r6 = 0
        L_0x0055:
            boolean r1 = r5.mIsInPreDrag
            if (r1 != 0) goto L_0x0070
            com.android.launcher3.Launcher r1 = r5.mLauncher
            com.android.launcher3.logging.UserEventDispatcher r1 = r1.getUserEventDispatcher()
            com.android.launcher3.DropTarget$DragObject r4 = r5.mDragObject
            r1.logDragNDrop(r4, r6)
            com.android.launcher3.DropTarget$DragObject r1 = r5.mDragObject
            com.android.launcher3.DragSource r1 = r1.dragSource
            com.android.launcher3.DropTarget$DragObject r4 = r5.mDragObject
            if (r7 == 0) goto L_0x006d
            r2 = 1
        L_0x006d:
            r1.onDropCompleted(r6, r4, r2, r0)
        L_0x0070:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dragndrop.DragController.drop(com.android.launcher3.DropTarget, java.lang.Runnable):void");
    }

    private DropTarget findDropTarget(int i, int i2, int[] iArr) {
        Rect rect = this.mRectTemp;
        ArrayList<DropTarget> arrayList = this.mDropTargets;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            DropTarget dropTarget = (DropTarget) arrayList.get(size);
            if (dropTarget.isDropEnabled()) {
                dropTarget.getHitRectRelativeToDragLayer(rect);
                this.mDragObject.f49x = i;
                this.mDragObject.f50y = i2;
                if (rect.contains(i, i2)) {
                    iArr[0] = i;
                    iArr[1] = i2;
                    this.mLauncher.getDragLayer().mapCoordInSelfToDescendant((View) dropTarget, iArr);
                    return dropTarget;
                }
            }
        }
        return null;
    }

    public void setWindowToken(IBinder iBinder) {
        this.mWindowToken = iBinder;
    }

    public void addDragListener(DragListener dragListener) {
        this.mListeners.add(dragListener);
    }

    public void removeDragListener(DragListener dragListener) {
        this.mListeners.remove(dragListener);
    }

    public void addDropTarget(DropTarget dropTarget) {
        this.mDropTargets.add(dropTarget);
    }

    public void removeDropTarget(DropTarget dropTarget) {
        this.mDropTargets.remove(dropTarget);
    }
}
