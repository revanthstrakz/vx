package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.p001v4.view.ViewCompat;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.BubbleTextView.BubbleTextShadowHandler;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate;
import com.android.launcher3.accessibility.FolderAccessibilityHelper;
import com.android.launcher3.accessibility.WorkspaceAccessibilityHelper;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.util.CellAndSpan;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.ParcelableSparseArray;
import com.android.launcher3.util.Themes;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

public class CellLayout extends ViewGroup implements BubbleTextShadowHandler {
    private static final int[] BACKGROUND_STATE_ACTIVE = {16842914};
    private static final int[] BACKGROUND_STATE_DEFAULT = new int[0];
    private static final boolean DEBUG_VISUALIZE_OCCUPIED = false;
    private static final boolean DESTRUCTIVE_REORDER = false;
    public static final int FOLDER = 2;
    public static final int FOLDER_ACCESSIBILITY_DRAG = 1;
    public static final int HOTSEAT = 1;
    private static final int INVALID_DIRECTION = -100;
    private static final boolean LOGD = false;
    public static final int MODE_ACCEPT_DROP = 4;
    public static final int MODE_DRAG_OVER = 1;
    public static final int MODE_ON_DROP = 2;
    public static final int MODE_ON_DROP_EXTERNAL = 3;
    public static final int MODE_SHOW_REORDER_HINT = 0;
    private static final int REORDER_ANIMATION_DURATION = 150;
    private static final float REORDER_PREVIEW_MAGNITUDE = 0.12f;
    private static final String TAG = "CellLayout";
    public static final int WORKSPACE = 0;
    public static final int WORKSPACE_ACCESSIBILITY_DRAG = 2;
    private static final Paint sPaint = new Paint();
    private final Drawable mBackground;
    private float mBackgroundAlpha;
    @ExportedProperty(category = "launcher")
    int mCellHeight;
    @ExportedProperty(category = "launcher")
    int mCellWidth;
    private final float mChildScale;
    private final int mContainerType;
    /* access modifiers changed from: private */
    @ExportedProperty(category = "launcher")
    public int mCountX;
    /* access modifiers changed from: private */
    @ExportedProperty(category = "launcher")
    public int mCountY;
    private final int[] mDirectionVector;
    private final int[] mDragCell;
    final float[] mDragOutlineAlphas;
    private final InterruptibleInOutAnimator[] mDragOutlineAnims;
    private int mDragOutlineCurrent;
    private final Paint mDragOutlinePaint;
    final Rect[] mDragOutlines;
    private boolean mDragging;
    private boolean mDropPending;
    private final TimeInterpolator mEaseOutInterpolator;
    private int mFixedCellHeight;
    private int mFixedCellWidth;
    private int mFixedHeight;
    private int mFixedWidth;
    private final ArrayList<PreviewBackground> mFolderBackgrounds;
    final PreviewBackground mFolderLeaveBehind;
    private OnTouchListener mInterceptTouchListener;
    private final ArrayList<View> mIntersectingViews;
    private boolean mIsDragOverlapping;
    private boolean mIsDragTarget;
    private boolean mItemPlacementDirty;
    private boolean mJailContent;
    private final Launcher mLauncher;
    private GridOccupancy mOccupied;
    private final Rect mOccupiedRect;
    final int[] mPreviousReorderDirection;
    final ArrayMap<LayoutParams, Animator> mReorderAnimators;
    final float mReorderPreviewAnimationMagnitude;
    final ArrayMap<View, ReorderPreviewAnimation> mShakeAnimators;
    private final ShortcutAndWidgetContainer mShortcutsAndWidgets;
    private final StylusEventHelper mStylusEventHelper;
    final int[] mTempLocation;
    private final Rect mTempRect;
    private final Stack<Rect> mTempRectStack;
    private GridOccupancy mTmpOccupied;
    final int[] mTmpPoint;
    private final ClickShadowView mTouchFeedbackView;
    private DragAndDropAccessibilityDelegate mTouchHelper;
    private boolean mUseTouchHelper;

    public static final class CellInfo extends CellAndSpan {
        public final View cell;
        final long container;
        final long screenId;

        public CellInfo(View view, ItemInfo itemInfo) {
            this.cellX = itemInfo.cellX;
            this.cellY = itemInfo.cellY;
            this.spanX = itemInfo.spanX;
            this.spanY = itemInfo.spanY;
            this.cell = view;
            this.screenId = itemInfo.screenId;
            this.container = itemInfo.container;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Cell[view=");
            sb.append(this.cell == null ? "null" : this.cell.getClass());
            sb.append(", x=");
            sb.append(this.cellX);
            sb.append(", y=");
            sb.append(this.cellY);
            sb.append("]");
            return sb.toString();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ContainerType {
    }

    private static class ItemConfiguration extends CellAndSpan {
        ArrayList<View> intersectingViews;
        boolean isSolution;
        final ArrayMap<View, CellAndSpan> map;
        private final ArrayMap<View, CellAndSpan> savedMap;
        final ArrayList<View> sortedViews;

        private ItemConfiguration() {
            this.map = new ArrayMap<>();
            this.savedMap = new ArrayMap<>();
            this.sortedViews = new ArrayList<>();
            this.isSolution = false;
        }

        /* access modifiers changed from: 0000 */
        public void save() {
            for (View view : this.map.keySet()) {
                ((CellAndSpan) this.savedMap.get(view)).copyFrom((CellAndSpan) this.map.get(view));
            }
        }

        /* access modifiers changed from: 0000 */
        public void restore() {
            for (View view : this.savedMap.keySet()) {
                ((CellAndSpan) this.map.get(view)).copyFrom((CellAndSpan) this.savedMap.get(view));
            }
        }

        /* access modifiers changed from: 0000 */
        public void add(View view, CellAndSpan cellAndSpan) {
            this.map.put(view, cellAndSpan);
            this.savedMap.put(view, new CellAndSpan());
            this.sortedViews.add(view);
        }

        /* access modifiers changed from: 0000 */
        public int area() {
            return this.spanX * this.spanY;
        }

        /* access modifiers changed from: 0000 */
        public void getBoundingRectForViews(ArrayList<View> arrayList, Rect rect) {
            Iterator it = arrayList.iterator();
            boolean z = true;
            while (it.hasNext()) {
                CellAndSpan cellAndSpan = (CellAndSpan) this.map.get((View) it.next());
                if (z) {
                    rect.set(cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.cellX + cellAndSpan.spanX, cellAndSpan.cellY + cellAndSpan.spanY);
                    z = false;
                } else {
                    rect.union(cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.cellX + cellAndSpan.spanX, cellAndSpan.cellY + cellAndSpan.spanY);
                }
            }
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        public boolean canReorder;
        @ExportedProperty
        public int cellHSpan;
        @ExportedProperty
        public int cellVSpan;
        @ExportedProperty
        public int cellX;
        @ExportedProperty
        public int cellY;
        boolean dropped;
        public boolean isFullscreen;
        public boolean isLockedToGrid;
        public int tmpCellX;
        public int tmpCellY;
        public boolean useTmpCoords;
        @ExportedProperty

        /* renamed from: x */
        public int f46x;
        @ExportedProperty

        /* renamed from: y */
        public int f47y;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.isLockedToGrid = true;
            this.isFullscreen = false;
            this.canReorder = true;
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.isLockedToGrid = true;
            this.isFullscreen = false;
            this.canReorder = true;
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.isLockedToGrid = true;
            this.isFullscreen = false;
            this.canReorder = true;
            this.cellX = layoutParams.cellX;
            this.cellY = layoutParams.cellY;
            this.cellHSpan = layoutParams.cellHSpan;
            this.cellVSpan = layoutParams.cellVSpan;
        }

        public LayoutParams(int i, int i2, int i3, int i4) {
            super(-1, -1);
            this.isLockedToGrid = true;
            this.isFullscreen = false;
            this.canReorder = true;
            this.cellX = i;
            this.cellY = i2;
            this.cellHSpan = i3;
            this.cellVSpan = i4;
        }

        public void setup(int i, int i2, boolean z, int i3) {
            setup(i, i2, z, i3, 1.0f, 1.0f);
        }

        public void setup(int i, int i2, boolean z, int i3, float f, float f2) {
            if (this.isLockedToGrid) {
                int i4 = this.cellHSpan;
                int i5 = this.cellVSpan;
                int i6 = this.useTmpCoords ? this.tmpCellX : this.cellX;
                int i7 = this.useTmpCoords ? this.tmpCellY : this.cellY;
                if (z) {
                    i6 = (i3 - i6) - this.cellHSpan;
                }
                this.width = (int) (((((float) (i4 * i)) / f) - ((float) this.leftMargin)) - ((float) this.rightMargin));
                this.height = (int) (((((float) (i5 * i2)) / f2) - ((float) this.topMargin)) - ((float) this.bottomMargin));
                this.f46x = (i6 * i) + this.leftMargin;
                this.f47y = (i7 * i2) + this.topMargin;
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append(this.cellX);
            sb.append(", ");
            sb.append(this.cellY);
            sb.append(")");
            return sb.toString();
        }

        public void setWidth(int i) {
            this.width = i;
        }

        public int getWidth() {
            return this.width;
        }

        public void setHeight(int i) {
            this.height = i;
        }

        public int getHeight() {
            return this.height;
        }

        public void setX(int i) {
            this.f46x = i;
        }

        public int getX() {
            return this.f46x;
        }

        public void setY(int i) {
            this.f47y = i;
        }

        public int getY() {
            return this.f47y;
        }
    }

    class ReorderPreviewAnimation {
        private static final float CHILD_DIVIDEND = 4.0f;
        private static final int HINT_DURATION = 350;
        public static final int MODE_HINT = 0;
        public static final int MODE_PREVIEW = 1;
        private static final int PREVIEW_DURATION = 300;

        /* renamed from: a */
        Animator f48a;
        final View child;
        float finalDeltaX;
        float finalDeltaY;
        final float finalScale;
        float initDeltaX;
        float initDeltaY;
        float initScale;
        final int mode;
        boolean repeating = false;
        final /* synthetic */ CellLayout this$0;

        public ReorderPreviewAnimation(CellLayout cellLayout, View view, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            CellLayout cellLayout2 = cellLayout;
            int i8 = i;
            this.this$0 = cellLayout2;
            int i9 = i6;
            int i10 = i7;
            cellLayout.regionToCenterPoint(i2, i3, i9, i10, cellLayout2.mTmpPoint);
            int i11 = cellLayout2.mTmpPoint[0];
            int i12 = 1;
            int i13 = cellLayout2.mTmpPoint[1];
            cellLayout.regionToCenterPoint(i4, i5, i9, i10, cellLayout2.mTmpPoint);
            int i14 = cellLayout2.mTmpPoint[0] - i11;
            int i15 = cellLayout2.mTmpPoint[1] - i13;
            this.child = view;
            this.mode = i8;
            setInitialAnimationValues(false);
            this.finalScale = (1.0f - (CHILD_DIVIDEND / ((float) view.getWidth()))) * this.initScale;
            this.finalDeltaX = this.initDeltaX;
            this.finalDeltaY = this.initDeltaY;
            if (i8 == 0) {
                i12 = -1;
            }
            if (i14 != i15 || i14 != 0) {
                if (i15 == 0) {
                    this.finalDeltaX += ((float) (-i12)) * Math.signum((float) i14) * cellLayout2.mReorderPreviewAnimationMagnitude;
                } else if (i14 == 0) {
                    this.finalDeltaY += ((float) (-i12)) * Math.signum((float) i15) * cellLayout2.mReorderPreviewAnimationMagnitude;
                } else {
                    float f = (float) i15;
                    float f2 = (float) i14;
                    double atan = Math.atan((double) (f / f2));
                    float f3 = (float) (-i12);
                    this.finalDeltaX += (float) ((int) (((double) (Math.signum(f2) * f3)) * Math.abs(Math.cos(atan) * ((double) cellLayout2.mReorderPreviewAnimationMagnitude))));
                    this.finalDeltaY += (float) ((int) (((double) (f3 * Math.signum(f))) * Math.abs(Math.sin(atan) * ((double) cellLayout2.mReorderPreviewAnimationMagnitude))));
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void setInitialAnimationValues(boolean z) {
            if (!z) {
                this.initScale = this.child.getScaleX();
                this.initDeltaX = this.child.getTranslationX();
                this.initDeltaY = this.child.getTranslationY();
            } else if (this.child instanceof LauncherAppWidgetHostView) {
                LauncherAppWidgetHostView launcherAppWidgetHostView = (LauncherAppWidgetHostView) this.child;
                this.initScale = launcherAppWidgetHostView.getScaleToFit();
                this.initDeltaX = launcherAppWidgetHostView.getTranslationForCentering().x;
                this.initDeltaY = launcherAppWidgetHostView.getTranslationForCentering().y;
            } else {
                this.initScale = 1.0f;
                this.initDeltaX = 0.0f;
                this.initDeltaY = 0.0f;
            }
        }

        /* access modifiers changed from: 0000 */
        public void animate() {
            boolean z = this.finalDeltaX == this.initDeltaX && this.finalDeltaY == this.initDeltaY;
            if (this.this$0.mShakeAnimators.containsKey(this.child)) {
                ((ReorderPreviewAnimation) this.this$0.mShakeAnimators.get(this.child)).cancel();
                this.this$0.mShakeAnimators.remove(this.child);
                if (z) {
                    completeAnimationImmediately();
                    return;
                }
            }
            if (!z) {
                ValueAnimator ofFloat = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
                this.f48a = ofFloat;
                if (!Utilities.isPowerSaverOn(this.this$0.getContext())) {
                    ofFloat.setRepeatMode(2);
                    ofFloat.setRepeatCount(-1);
                }
                ofFloat.setDuration(this.mode == 0 ? 350 : 300);
                ofFloat.setStartDelay((long) ((int) (Math.random() * 60.0d)));
                ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        float f = (ReorderPreviewAnimation.this.mode != 0 || !ReorderPreviewAnimation.this.repeating) ? floatValue : 1.0f;
                        float f2 = 1.0f - f;
                        float f3 = (ReorderPreviewAnimation.this.finalDeltaX * f) + (ReorderPreviewAnimation.this.initDeltaX * f2);
                        float f4 = (f * ReorderPreviewAnimation.this.finalDeltaY) + (f2 * ReorderPreviewAnimation.this.initDeltaY);
                        ReorderPreviewAnimation.this.child.setTranslationX(f3);
                        ReorderPreviewAnimation.this.child.setTranslationY(f4);
                        float f5 = (ReorderPreviewAnimation.this.finalScale * floatValue) + ((1.0f - floatValue) * ReorderPreviewAnimation.this.initScale);
                        ReorderPreviewAnimation.this.child.setScaleX(f5);
                        ReorderPreviewAnimation.this.child.setScaleY(f5);
                    }
                });
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationRepeat(Animator animator) {
                        ReorderPreviewAnimation.this.setInitialAnimationValues(true);
                        ReorderPreviewAnimation.this.repeating = true;
                    }
                });
                this.this$0.mShakeAnimators.put(this.child, this);
                ofFloat.start();
            }
        }

        private void cancel() {
            if (this.f48a != null) {
                this.f48a.cancel();
            }
        }

        /* access modifiers changed from: 0000 */
        public void completeAnimationImmediately() {
            if (this.f48a != null) {
                this.f48a.cancel();
            }
            setInitialAnimationValues(true);
            this.f48a = LauncherAnimUtils.ofPropertyValuesHolder(this.child, new PropertyListBuilder().scale(this.initScale).translationX(this.initDeltaX).translationY(this.initDeltaY).build()).setDuration(150);
            this.f48a.setInterpolator(new DecelerateInterpolator(1.5f));
            this.f48a.start();
        }
    }

    private class ViewCluster {
        static final int BOTTOM = 8;
        static final int LEFT = 1;
        static final int RIGHT = 4;
        static final int TOP = 2;
        final int[] bottomEdge = new int[CellLayout.this.mCountX];
        final Rect boundingRect = new Rect();
        boolean boundingRectDirty;
        final PositionComparator comparator = new PositionComparator();
        final ItemConfiguration config;
        int dirtyEdges;
        final int[] leftEdge = new int[CellLayout.this.mCountY];
        final int[] rightEdge = new int[CellLayout.this.mCountY];
        final int[] topEdge = new int[CellLayout.this.mCountX];
        final ArrayList<View> views;

        class PositionComparator implements Comparator<View> {
            int whichEdge = 0;

            PositionComparator() {
            }

            public int compare(View view, View view2) {
                CellAndSpan cellAndSpan = (CellAndSpan) ViewCluster.this.config.map.get(view);
                CellAndSpan cellAndSpan2 = (CellAndSpan) ViewCluster.this.config.map.get(view2);
                int i = this.whichEdge;
                if (i == 4) {
                    return cellAndSpan.cellX - cellAndSpan2.cellX;
                }
                switch (i) {
                    case 1:
                        return (cellAndSpan2.cellX + cellAndSpan2.spanX) - (cellAndSpan.cellX + cellAndSpan.spanX);
                    case 2:
                        return (cellAndSpan2.cellY + cellAndSpan2.spanY) - (cellAndSpan.cellY + cellAndSpan.spanY);
                    default:
                        return cellAndSpan.cellY - cellAndSpan2.cellY;
                }
            }
        }

        public ViewCluster(ArrayList<View> arrayList, ItemConfiguration itemConfiguration) {
            this.views = (ArrayList) arrayList.clone();
            this.config = itemConfiguration;
            resetEdges();
        }

        /* access modifiers changed from: 0000 */
        public void resetEdges() {
            for (int i = 0; i < CellLayout.this.mCountX; i++) {
                this.topEdge[i] = -1;
                this.bottomEdge[i] = -1;
            }
            for (int i2 = 0; i2 < CellLayout.this.mCountY; i2++) {
                this.leftEdge[i2] = -1;
                this.rightEdge[i2] = -1;
            }
            this.dirtyEdges = 15;
            this.boundingRectDirty = true;
        }

        /* access modifiers changed from: 0000 */
        public void computeEdge(int i) {
            int size = this.views.size();
            for (int i2 = 0; i2 < size; i2++) {
                CellAndSpan cellAndSpan = (CellAndSpan) this.config.map.get(this.views.get(i2));
                if (i == 4) {
                    int i3 = cellAndSpan.cellX + cellAndSpan.spanX;
                    for (int i4 = cellAndSpan.cellY; i4 < cellAndSpan.cellY + cellAndSpan.spanY; i4++) {
                        if (i3 > this.rightEdge[i4]) {
                            this.rightEdge[i4] = i3;
                        }
                    }
                } else if (i != 8) {
                    switch (i) {
                        case 1:
                            int i5 = cellAndSpan.cellX;
                            for (int i6 = cellAndSpan.cellY; i6 < cellAndSpan.cellY + cellAndSpan.spanY; i6++) {
                                if (i5 < this.leftEdge[i6] || this.leftEdge[i6] < 0) {
                                    this.leftEdge[i6] = i5;
                                }
                            }
                            break;
                        case 2:
                            int i7 = cellAndSpan.cellY;
                            for (int i8 = cellAndSpan.cellX; i8 < cellAndSpan.cellX + cellAndSpan.spanX; i8++) {
                                if (i7 < this.topEdge[i8] || this.topEdge[i8] < 0) {
                                    this.topEdge[i8] = i7;
                                }
                            }
                            break;
                    }
                } else {
                    int i9 = cellAndSpan.cellY + cellAndSpan.spanY;
                    for (int i10 = cellAndSpan.cellX; i10 < cellAndSpan.cellX + cellAndSpan.spanX; i10++) {
                        if (i9 > this.bottomEdge[i10]) {
                            this.bottomEdge[i10] = i9;
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public boolean isViewTouchingEdge(View view, int i) {
            CellAndSpan cellAndSpan = (CellAndSpan) this.config.map.get(view);
            if ((this.dirtyEdges & i) == i) {
                computeEdge(i);
                this.dirtyEdges &= ~i;
            }
            if (i == 4) {
                for (int i2 = cellAndSpan.cellY; i2 < cellAndSpan.cellY + cellAndSpan.spanY; i2++) {
                    if (this.rightEdge[i2] == cellAndSpan.cellX) {
                        return true;
                    }
                }
            } else if (i != 8) {
                switch (i) {
                    case 1:
                        for (int i3 = cellAndSpan.cellY; i3 < cellAndSpan.cellY + cellAndSpan.spanY; i3++) {
                            if (this.leftEdge[i3] == cellAndSpan.cellX + cellAndSpan.spanX) {
                                return true;
                            }
                        }
                        break;
                    case 2:
                        for (int i4 = cellAndSpan.cellX; i4 < cellAndSpan.cellX + cellAndSpan.spanX; i4++) {
                            if (this.topEdge[i4] == cellAndSpan.cellY + cellAndSpan.spanY) {
                                return true;
                            }
                        }
                        break;
                }
            } else {
                for (int i5 = cellAndSpan.cellX; i5 < cellAndSpan.cellX + cellAndSpan.spanX; i5++) {
                    if (this.bottomEdge[i5] == cellAndSpan.cellY) {
                        return true;
                    }
                }
            }
            return false;
        }

        /* access modifiers changed from: 0000 */
        public void shift(int i, int i2) {
            Iterator it = this.views.iterator();
            while (it.hasNext()) {
                CellAndSpan cellAndSpan = (CellAndSpan) this.config.map.get((View) it.next());
                if (i != 4) {
                    switch (i) {
                        case 1:
                            cellAndSpan.cellX -= i2;
                            break;
                        case 2:
                            cellAndSpan.cellY -= i2;
                            break;
                        default:
                            cellAndSpan.cellY += i2;
                            break;
                    }
                } else {
                    cellAndSpan.cellX += i2;
                }
            }
            resetEdges();
        }

        public void addView(View view) {
            this.views.add(view);
            resetEdges();
        }

        public Rect getBoundingRect() {
            if (this.boundingRectDirty) {
                this.config.getBoundingRectForViews(this.views, this.boundingRect);
            }
            return this.boundingRect;
        }

        public void sortConfigurationForEdgePush(int i) {
            this.comparator.whichEdge = i;
            Collections.sort(this.config.sortedViews, this.comparator);
        }
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public CellLayout(Context context) {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CellLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDropPending = false;
        this.mIsDragTarget = true;
        this.mJailContent = true;
        this.mTmpPoint = new int[2];
        this.mTempLocation = new int[2];
        this.mFolderBackgrounds = new ArrayList<>();
        this.mFolderLeaveBehind = new PreviewBackground();
        this.mFixedWidth = -1;
        this.mFixedHeight = -1;
        this.mIsDragOverlapping = false;
        this.mDragOutlines = new Rect[4];
        this.mDragOutlineAlphas = new float[this.mDragOutlines.length];
        this.mDragOutlineAnims = new InterruptibleInOutAnimator[this.mDragOutlines.length];
        this.mDragOutlineCurrent = 0;
        this.mDragOutlinePaint = new Paint();
        this.mReorderAnimators = new ArrayMap<>();
        this.mShakeAnimators = new ArrayMap<>();
        this.mItemPlacementDirty = false;
        this.mDragCell = new int[2];
        this.mDragging = false;
        this.mChildScale = 1.0f;
        this.mIntersectingViews = new ArrayList<>();
        this.mOccupiedRect = new Rect();
        this.mDirectionVector = new int[2];
        this.mPreviousReorderDirection = new int[2];
        this.mTempRect = new Rect();
        this.mUseTouchHelper = false;
        this.mTempRectStack = new Stack<>();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0622R.styleable.CellLayout, i, 0);
        this.mContainerType = obtainStyledAttributes.getInteger(C0622R.styleable.CellLayout_containerType, 0);
        obtainStyledAttributes.recycle();
        setWillNotDraw(false);
        setClipToPadding(false);
        this.mLauncher = Launcher.getLauncher(context);
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mCellHeight = -1;
        this.mCellWidth = -1;
        this.mFixedCellHeight = -1;
        this.mFixedCellWidth = -1;
        this.mCountX = deviceProfile.inv.numColumns;
        this.mCountY = deviceProfile.inv.numRows;
        this.mOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTmpOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mPreviousReorderDirection[0] = -100;
        this.mPreviousReorderDirection[1] = -100;
        this.mFolderLeaveBehind.delegateCellX = -1;
        this.mFolderLeaveBehind.delegateCellY = -1;
        setAlwaysDrawnWithCacheEnabled(false);
        Resources resources = getResources();
        this.mBackground = resources.getDrawable(C0622R.C0624drawable.bg_celllayout);
        this.mBackground.setCallback(this);
        this.mBackground.setAlpha((int) (this.mBackgroundAlpha * 255.0f));
        this.mReorderPreviewAnimationMagnitude = ((float) deviceProfile.iconSizePx) * REORDER_PREVIEW_MAGNITUDE;
        this.mEaseOutInterpolator = new DecelerateInterpolator(2.5f);
        int[] iArr = this.mDragCell;
        this.mDragCell[1] = -1;
        iArr[0] = -1;
        for (int i2 = 0; i2 < this.mDragOutlines.length; i2++) {
            this.mDragOutlines[i2] = new Rect(-1, -1, -1, -1);
        }
        this.mDragOutlinePaint.setColor(Themes.getAttrColor(context, C0622R.attr.workspaceTextColor));
        int integer = resources.getInteger(C0622R.integer.config_dragOutlineFadeTime);
        float integer2 = (float) resources.getInteger(C0622R.integer.config_dragOutlineMaxAlpha);
        Arrays.fill(this.mDragOutlineAlphas, 0.0f);
        for (final int i3 = 0; i3 < this.mDragOutlineAnims.length; i3++) {
            final InterruptibleInOutAnimator interruptibleInOutAnimator = new InterruptibleInOutAnimator(this, (long) integer, 0.0f, integer2);
            interruptibleInOutAnimator.getAnimator().setInterpolator(this.mEaseOutInterpolator);
            interruptibleInOutAnimator.getAnimator().addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (((Bitmap) interruptibleInOutAnimator.getTag()) == null) {
                        valueAnimator.cancel();
                        return;
                    }
                    CellLayout.this.mDragOutlineAlphas[i3] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    CellLayout.this.invalidate(CellLayout.this.mDragOutlines[i3]);
                }
            });
            interruptibleInOutAnimator.getAnimator().addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (((Float) ((ValueAnimator) animator).getAnimatedValue()).floatValue() == 0.0f) {
                        interruptibleInOutAnimator.setTag(null);
                    }
                }
            });
            this.mDragOutlineAnims[i3] = interruptibleInOutAnimator;
        }
        this.mShortcutsAndWidgets = new ShortcutAndWidgetContainer(context, this.mContainerType);
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mTouchFeedbackView = new ClickShadowView(context);
        addView(this.mTouchFeedbackView);
        addView(this.mShortcutsAndWidgets);
    }

    public void enableAccessibleDrag(boolean z, int i) {
        this.mUseTouchHelper = z;
        if (!z) {
            ViewCompat.setAccessibilityDelegate(this, null);
            setImportantForAccessibility(2);
            getShortcutsAndWidgets().setImportantForAccessibility(2);
            setOnClickListener(this.mLauncher);
        } else {
            if (i == 2 && !(this.mTouchHelper instanceof WorkspaceAccessibilityHelper)) {
                this.mTouchHelper = new WorkspaceAccessibilityHelper(this);
            } else if (i == 1 && !(this.mTouchHelper instanceof FolderAccessibilityHelper)) {
                this.mTouchHelper = new FolderAccessibilityHelper(this);
            }
            ViewCompat.setAccessibilityDelegate(this, this.mTouchHelper);
            setImportantForAccessibility(1);
            getShortcutsAndWidgets().setImportantForAccessibility(1);
            setOnClickListener(this.mTouchHelper);
        }
        if (getParent() != null) {
            getParent().notifySubtreeAccessibilityStateChanged(this, this, 1);
        }
    }

    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        if (!this.mUseTouchHelper || !this.mTouchHelper.dispatchHoverEvent(motionEvent)) {
            return super.dispatchHoverEvent(motionEvent);
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mUseTouchHelper || (this.mInterceptTouchListener != null && this.mInterceptTouchListener.onTouch(this, motionEvent));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        if (!this.mLauncher.mWorkspace.isInOverviewMode() || !this.mStylusEventHelper.onMotionEvent(motionEvent)) {
            return onTouchEvent;
        }
        return true;
    }

    public void enableHardwareLayer(boolean z) {
        this.mShortcutsAndWidgets.setLayerType(z ? 2 : 0, sPaint);
    }

    public void buildHardwareLayer() {
        this.mShortcutsAndWidgets.buildLayer();
    }

    public void setCellDimensions(int i, int i2) {
        this.mCellWidth = i;
        this.mFixedCellWidth = i;
        this.mCellHeight = i2;
        this.mFixedCellHeight = i2;
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY);
    }

    public void setGridSize(int i, int i2) {
        this.mCountX = i;
        this.mCountY = i2;
        this.mOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTmpOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTempRectStack.clear();
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY);
        requestLayout();
    }

    public void setInvertIfRtl(boolean z) {
        this.mShortcutsAndWidgets.setInvertIfRtl(z);
    }

    public void setDropPending(boolean z) {
        this.mDropPending = z;
    }

    public boolean isDropPending() {
        return this.mDropPending;
    }

    public void setPressedIcon(BubbleTextView bubbleTextView, Bitmap bitmap) {
        if (bubbleTextView == null || bitmap == null) {
            this.mTouchFeedbackView.setBitmap(null);
            this.mTouchFeedbackView.animate().cancel();
        } else if (this.mTouchFeedbackView.setBitmap(bitmap)) {
            this.mTouchFeedbackView.alignWithIconView(bubbleTextView, this.mShortcutsAndWidgets, null);
            this.mTouchFeedbackView.animateShadow();
        }
    }

    /* access modifiers changed from: 0000 */
    public void disableDragTarget() {
        this.mIsDragTarget = false;
    }

    public boolean isDragTarget() {
        return this.mIsDragTarget;
    }

    /* access modifiers changed from: 0000 */
    public void setIsDragOverlapping(boolean z) {
        if (this.mIsDragOverlapping != z) {
            this.mIsDragOverlapping = z;
            this.mBackground.setState(this.mIsDragOverlapping ? BACKGROUND_STATE_ACTIVE : BACKGROUND_STATE_DEFAULT);
            invalidate();
        }
    }

    public void disableJailContent() {
        this.mJailContent = false;
    }

    /* access modifiers changed from: protected */
    public void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        if (this.mJailContent) {
            ParcelableSparseArray jailedArray = getJailedArray(sparseArray);
            super.dispatchSaveInstanceState(jailedArray);
            sparseArray.put(C0622R.C0625id.cell_layout_jail_id, jailedArray);
            return;
        }
        super.dispatchSaveInstanceState(sparseArray);
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        if (this.mJailContent) {
            sparseArray = getJailedArray(sparseArray);
        }
        super.dispatchRestoreInstanceState(sparseArray);
    }

    private ParcelableSparseArray getJailedArray(SparseArray<Parcelable> sparseArray) {
        Parcelable parcelable = (Parcelable) sparseArray.get(C0622R.C0625id.cell_layout_jail_id);
        return parcelable instanceof ParcelableSparseArray ? (ParcelableSparseArray) parcelable : new ParcelableSparseArray();
    }

    public boolean getIsDragOverlapping() {
        return this.mIsDragOverlapping;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mIsDragTarget) {
            if (this.mBackgroundAlpha > 0.0f) {
                this.mBackground.draw(canvas);
            }
            Paint paint = this.mDragOutlinePaint;
            for (int i = 0; i < this.mDragOutlines.length; i++) {
                float f = this.mDragOutlineAlphas[i];
                if (f > 0.0f) {
                    Bitmap bitmap = (Bitmap) this.mDragOutlineAnims[i].getTag();
                    paint.setAlpha((int) (f + 0.5f));
                    canvas.drawBitmap(bitmap, null, this.mDragOutlines[i], paint);
                }
            }
            for (int i2 = 0; i2 < this.mFolderBackgrounds.size(); i2++) {
                PreviewBackground previewBackground = (PreviewBackground) this.mFolderBackgrounds.get(i2);
                cellToPoint(previewBackground.delegateCellX, previewBackground.delegateCellY, this.mTempLocation);
                canvas.save();
                canvas.translate((float) this.mTempLocation[0], (float) this.mTempLocation[1]);
                previewBackground.drawBackground(canvas);
                if (!previewBackground.isClipping) {
                    previewBackground.drawBackgroundStroke(canvas);
                }
                canvas.restore();
            }
            if (this.mFolderLeaveBehind.delegateCellX >= 0 && this.mFolderLeaveBehind.delegateCellY >= 0) {
                cellToPoint(this.mFolderLeaveBehind.delegateCellX, this.mFolderLeaveBehind.delegateCellY, this.mTempLocation);
                canvas.save();
                canvas.translate((float) this.mTempLocation[0], (float) this.mTempLocation[1]);
                this.mFolderLeaveBehind.drawLeaveBehind(canvas);
                canvas.restore();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        for (int i = 0; i < this.mFolderBackgrounds.size(); i++) {
            PreviewBackground previewBackground = (PreviewBackground) this.mFolderBackgrounds.get(i);
            if (previewBackground.isClipping) {
                cellToPoint(previewBackground.delegateCellX, previewBackground.delegateCellY, this.mTempLocation);
                canvas.save();
                canvas.translate((float) this.mTempLocation[0], (float) this.mTempLocation[1]);
                previewBackground.drawBackgroundStroke(canvas);
                canvas.restore();
            }
        }
    }

    public void addFolderBackground(PreviewBackground previewBackground) {
        this.mFolderBackgrounds.add(previewBackground);
    }

    public void removeFolderBackground(PreviewBackground previewBackground) {
        this.mFolderBackgrounds.remove(previewBackground);
    }

    public void setFolderLeaveBehindCell(int i, int i2) {
        View childAt = getChildAt(i, i2);
        this.mFolderLeaveBehind.setup(this.mLauncher, null, childAt.getMeasuredWidth(), childAt.getPaddingTop());
        this.mFolderLeaveBehind.delegateCellX = i;
        this.mFolderLeaveBehind.delegateCellY = i2;
        invalidate();
    }

    public void clearFolderLeaveBehind() {
        this.mFolderLeaveBehind.delegateCellX = -1;
        this.mFolderLeaveBehind.delegateCellY = -1;
        invalidate();
    }

    public void restoreInstanceState(SparseArray<Parcelable> sparseArray) {
        try {
            dispatchRestoreInstanceState(sparseArray);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Ignoring an error while restoring a view instance state", e);
        }
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).cancelLongPress();
        }
    }

    public void setOnInterceptTouchListener(OnTouchListener onTouchListener) {
        this.mInterceptTouchListener = onTouchListener;
    }

    public int getCountX() {
        return this.mCountX;
    }

    public int getCountY() {
        return this.mCountY;
    }

    public boolean acceptsWidget() {
        return this.mContainerType == 0;
    }

    public boolean addViewToCellLayout(View view, int i, int i2, LayoutParams layoutParams, boolean z) {
        if (view instanceof BubbleTextView) {
            ((BubbleTextView) view).setTextVisibility(this.mContainerType != 1);
        }
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        if (layoutParams.cellX < 0 || layoutParams.cellX > this.mCountX - 1 || layoutParams.cellY < 0 || layoutParams.cellY > this.mCountY - 1) {
            return false;
        }
        if (layoutParams.cellHSpan < 0) {
            layoutParams.cellHSpan = this.mCountX;
        }
        if (layoutParams.cellVSpan < 0) {
            layoutParams.cellVSpan = this.mCountY;
        }
        view.setId(i2);
        this.mShortcutsAndWidgets.addView(view, i, layoutParams);
        if (z) {
            markCellsAsOccupiedForView(view);
        }
        return true;
    }

    public void removeAllViews() {
        this.mOccupied.clear();
        this.mShortcutsAndWidgets.removeAllViews();
    }

    public void removeAllViewsInLayout() {
        if (this.mShortcutsAndWidgets.getChildCount() > 0) {
            this.mOccupied.clear();
            this.mShortcutsAndWidgets.removeAllViewsInLayout();
        }
    }

    public void removeView(View view) {
        markCellsAsUnoccupiedForView(view);
        this.mShortcutsAndWidgets.removeView(view);
    }

    public void removeViewAt(int i) {
        markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i));
        this.mShortcutsAndWidgets.removeViewAt(i);
    }

    public void removeViewInLayout(View view) {
        markCellsAsUnoccupiedForView(view);
        this.mShortcutsAndWidgets.removeViewInLayout(view);
    }

    public void removeViews(int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3++) {
            markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i3));
        }
        this.mShortcutsAndWidgets.removeViews(i, i2);
    }

    public void removeViewsInLayout(int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3++) {
            markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i3));
        }
        this.mShortcutsAndWidgets.removeViewsInLayout(i, i2);
    }

    public void pointToCellExact(int i, int i2, int[] iArr) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        iArr[0] = (i - paddingLeft) / this.mCellWidth;
        iArr[1] = (i2 - paddingTop) / this.mCellHeight;
        int i3 = this.mCountX;
        int i4 = this.mCountY;
        if (iArr[0] < 0) {
            iArr[0] = 0;
        }
        if (iArr[0] >= i3) {
            iArr[0] = i3 - 1;
        }
        if (iArr[1] < 0) {
            iArr[1] = 0;
        }
        if (iArr[1] >= i4) {
            iArr[1] = i4 - 1;
        }
    }

    /* access modifiers changed from: 0000 */
    public void pointToCellRounded(int i, int i2, int[] iArr) {
        pointToCellExact(i + (this.mCellWidth / 2), i2 + (this.mCellHeight / 2), iArr);
    }

    /* access modifiers changed from: 0000 */
    public void cellToPoint(int i, int i2, int[] iArr) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        iArr[0] = paddingLeft + (i * this.mCellWidth);
        iArr[1] = paddingTop + (i2 * this.mCellHeight);
    }

    /* access modifiers changed from: 0000 */
    public void cellToCenterPoint(int i, int i2, int[] iArr) {
        regionToCenterPoint(i, i2, 1, 1, iArr);
    }

    /* access modifiers changed from: 0000 */
    public void regionToCenterPoint(int i, int i2, int i3, int i4, int[] iArr) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        iArr[0] = paddingLeft + (i * this.mCellWidth) + ((i3 * this.mCellWidth) / 2);
        iArr[1] = paddingTop + (i2 * this.mCellHeight) + ((i4 * this.mCellHeight) / 2);
    }

    /* access modifiers changed from: 0000 */
    public void regionToRect(int i, int i2, int i3, int i4, Rect rect) {
        int paddingLeft = getPaddingLeft();
        int i5 = paddingLeft + (i * this.mCellWidth);
        int paddingTop = getPaddingTop() + (i2 * this.mCellHeight);
        rect.set(i5, paddingTop, (i3 * this.mCellWidth) + i5, (i4 * this.mCellHeight) + paddingTop);
    }

    public float getDistanceFromCell(float f, float f2, int[] iArr) {
        cellToCenterPoint(iArr[0], iArr[1], this.mTmpPoint);
        return (float) Math.hypot((double) (f - ((float) this.mTmpPoint[0])), (double) (f2 - ((float) this.mTmpPoint[1])));
    }

    public int getCellWidth() {
        return this.mCellWidth;
    }

    public int getCellHeight() {
        return this.mCellHeight;
    }

    public void setFixedSize(int i, int i2) {
        this.mFixedWidth = i;
        this.mFixedHeight = i2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        int paddingLeft = size - (getPaddingLeft() + getPaddingRight());
        int paddingTop = size2 - (getPaddingTop() + getPaddingBottom());
        if (this.mFixedCellWidth < 0 || this.mFixedCellHeight < 0) {
            int calculateCellWidth = DeviceProfile.calculateCellWidth(paddingLeft, this.mCountX);
            int calculateCellHeight = DeviceProfile.calculateCellHeight(paddingTop, this.mCountY);
            if (!(calculateCellWidth == this.mCellWidth && calculateCellHeight == this.mCellHeight)) {
                this.mCellWidth = calculateCellWidth;
                this.mCellHeight = calculateCellHeight;
                this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY);
            }
        }
        if (this.mFixedWidth > 0 && this.mFixedHeight > 0) {
            paddingLeft = this.mFixedWidth;
            paddingTop = this.mFixedHeight;
        } else if (mode == 0 || mode2 == 0) {
            throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
        }
        this.mTouchFeedbackView.measure(MeasureSpec.makeMeasureSpec(this.mCellWidth + this.mTouchFeedbackView.getExtraSize(), 1073741824), MeasureSpec.makeMeasureSpec(this.mCellHeight + this.mTouchFeedbackView.getExtraSize(), 1073741824));
        this.mShortcutsAndWidgets.measure(MeasureSpec.makeMeasureSpec(paddingLeft, 1073741824), MeasureSpec.makeMeasureSpec(paddingTop, 1073741824));
        int measuredWidth = this.mShortcutsAndWidgets.getMeasuredWidth();
        int measuredHeight = this.mShortcutsAndWidgets.getMeasuredHeight();
        if (this.mFixedWidth <= 0 || this.mFixedHeight <= 0) {
            setMeasuredDimension(size, size2);
        } else {
            setMeasuredDimension(measuredWidth, measuredHeight);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        boolean z2 = false;
        if (this.mShortcutsAndWidgets.getChildCount() > 0 && ((LayoutParams) this.mShortcutsAndWidgets.getChildAt(0).getLayoutParams()).isFullscreen) {
            z2 = true;
        }
        int paddingLeft = getPaddingLeft();
        if (!z2) {
            paddingLeft += (int) Math.ceil((double) (((float) getUnusedHorizontalSpace()) / 2.0f));
        }
        int paddingRight = (i3 - i) - getPaddingRight();
        if (!z2) {
            paddingRight -= (int) Math.ceil((double) (((float) getUnusedHorizontalSpace()) / 2.0f));
        }
        int paddingTop = getPaddingTop();
        int paddingBottom = (i4 - i2) - getPaddingBottom();
        this.mTouchFeedbackView.layout(paddingLeft, paddingTop, this.mTouchFeedbackView.getMeasuredWidth() + paddingLeft, this.mTouchFeedbackView.getMeasuredHeight() + paddingTop);
        this.mShortcutsAndWidgets.layout(paddingLeft, paddingTop, paddingRight, paddingBottom);
        this.mBackground.getPadding(this.mTempRect);
        this.mBackground.setBounds((paddingLeft - this.mTempRect.left) - getPaddingLeft(), (paddingTop - this.mTempRect.top) - getPaddingTop(), paddingRight + this.mTempRect.right + getPaddingRight(), paddingBottom + this.mTempRect.bottom + getPaddingBottom());
    }

    public int getUnusedHorizontalSpace() {
        return ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - (this.mCountX * this.mCellWidth);
    }

    public float getBackgroundAlpha() {
        return this.mBackgroundAlpha;
    }

    public void setBackgroundAlpha(float f) {
        if (this.mBackgroundAlpha != f) {
            this.mBackgroundAlpha = f;
            this.mBackground.setAlpha((int) (this.mBackgroundAlpha * 255.0f));
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || (this.mIsDragTarget && drawable == this.mBackground);
    }

    public void setShortcutAndWidgetAlpha(float f) {
        this.mShortcutsAndWidgets.setAlpha(f);
    }

    public ShortcutAndWidgetContainer getShortcutsAndWidgets() {
        return this.mShortcutsAndWidgets;
    }

    public View getChildAt(int i, int i2) {
        return this.mShortcutsAndWidgets.getChildAt(i, i2);
    }

    public boolean animateChildToPosition(View view, int i, int i2, int i3, int i4, boolean z, boolean z2) {
        int i5;
        final View view2 = view;
        int i6 = i;
        int i7 = i2;
        ShortcutAndWidgetContainer shortcutsAndWidgets = getShortcutsAndWidgets();
        if (shortcutsAndWidgets.indexOfChild(view2) == -1) {
            return false;
        }
        final LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        ItemInfo itemInfo = (ItemInfo) view.getTag();
        if (this.mReorderAnimators.containsKey(layoutParams)) {
            ((Animator) this.mReorderAnimators.get(layoutParams)).cancel();
            this.mReorderAnimators.remove(layoutParams);
        }
        int i8 = layoutParams.f46x;
        int i9 = layoutParams.f47y;
        if (z2) {
            GridOccupancy gridOccupancy = z ? this.mOccupied : this.mTmpOccupied;
            gridOccupancy.markCells(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, false);
            i5 = i9;
            gridOccupancy.markCells(i, i2, layoutParams.cellHSpan, layoutParams.cellVSpan, true);
        } else {
            i5 = i9;
        }
        layoutParams.isLockedToGrid = true;
        if (z) {
            itemInfo.cellX = i6;
            layoutParams.cellX = i6;
            itemInfo.cellY = i7;
            layoutParams.cellY = i7;
        } else {
            layoutParams.tmpCellX = i6;
            layoutParams.tmpCellY = i7;
        }
        shortcutsAndWidgets.setupLp(view2);
        layoutParams.isLockedToGrid = false;
        final int i10 = layoutParams.f46x;
        final int i11 = layoutParams.f47y;
        layoutParams.f46x = i8;
        int i12 = i5;
        layoutParams.f47y = i12;
        if (i8 == i10 && i12 == i11) {
            layoutParams.isLockedToGrid = true;
            return true;
        }
        ValueAnimator ofFloat = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        ofFloat.setDuration((long) i3);
        this.mReorderAnimators.put(layoutParams, ofFloat);
        final LayoutParams layoutParams2 = layoutParams;
        final int i13 = i8;
        final int i14 = i12;
        final View view3 = view;
        C05253 r0 = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                float f = 1.0f - floatValue;
                layoutParams2.f46x = (int) ((((float) i13) * f) + (((float) i10) * floatValue));
                layoutParams2.f47y = (int) ((f * ((float) i14)) + (floatValue * ((float) i11)));
                view3.requestLayout();
            }
        };
        ofFloat.addUpdateListener(r0);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            boolean cancelled = false;

            public void onAnimationEnd(Animator animator) {
                if (!this.cancelled) {
                    layoutParams.isLockedToGrid = true;
                    view2.requestLayout();
                }
                if (CellLayout.this.mReorderAnimators.containsKey(layoutParams)) {
                    CellLayout.this.mReorderAnimators.remove(layoutParams);
                }
            }

            public void onAnimationCancel(Animator animator) {
                this.cancelled = true;
            }
        });
        ofFloat.setStartDelay((long) i4);
        ofFloat.start();
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void visualizeDropLocation(View view, DragPreviewProvider dragPreviewProvider, int i, int i2, int i3, int i4, boolean z, DragObject dragObject) {
        int i5;
        int i6;
        View view2 = view;
        DragPreviewProvider dragPreviewProvider2 = dragPreviewProvider;
        int i7 = i;
        int i8 = i2;
        DragObject dragObject2 = dragObject;
        int i9 = this.mDragCell[0];
        int i10 = this.mDragCell[1];
        if (dragPreviewProvider2 != null && dragPreviewProvider2.generatedDragOutline != null) {
            Bitmap bitmap = dragPreviewProvider2.generatedDragOutline;
            if (!(i7 == i9 && i8 == i10)) {
                Point dragVisualizeOffset = dragObject2.dragView.getDragVisualizeOffset();
                Rect dragRegion = dragObject2.dragView.getDragRegion();
                this.mDragCell[0] = i7;
                this.mDragCell[1] = i8;
                int i11 = this.mDragOutlineCurrent;
                this.mDragOutlineAnims[i11].animateOut();
                this.mDragOutlineCurrent = (i11 + 1) % this.mDragOutlines.length;
                Rect rect = this.mDragOutlines[this.mDragOutlineCurrent];
                if (z) {
                    cellToRect(i, i2, i3, i4, rect);
                    if (view2 instanceof LauncherAppWidgetHostView) {
                        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
                        Utilities.shrinkRect(rect, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y);
                    }
                } else {
                    int[] iArr = this.mTmpPoint;
                    cellToPoint(i7, i8, iArr);
                    int i12 = iArr[0];
                    int i13 = iArr[1];
                    if (view2 != null && dragVisualizeOffset == null) {
                        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
                        i5 = i13 + marginLayoutParams.topMargin + (((this.mCellHeight * i4) - bitmap.getHeight()) / 2);
                        i6 = i12 + marginLayoutParams.leftMargin + (((this.mCellWidth * i3) - bitmap.getWidth()) / 2);
                    } else if (dragVisualizeOffset == null || dragRegion == null) {
                        i6 = i12 + (((this.mCellWidth * i3) - bitmap.getWidth()) / 2);
                        i5 = i13 + (((this.mCellHeight * i4) - bitmap.getHeight()) / 2);
                    } else {
                        i6 = i12 + dragVisualizeOffset.x + (((this.mCellWidth * i3) - dragRegion.width()) / 2);
                        i5 = i13 + dragVisualizeOffset.y + ((int) Math.max(0.0f, ((float) (this.mCellHeight - getShortcutsAndWidgets().getCellContentHeight())) / 2.0f));
                    }
                    rect.set(i6, i5, bitmap.getWidth() + i6, bitmap.getHeight() + i5);
                }
                Utilities.scaleRectAboutCenter(rect, 1.0f);
                this.mDragOutlineAnims[this.mDragOutlineCurrent].setTag(bitmap);
                this.mDragOutlineAnims[this.mDragOutlineCurrent].animateIn();
                if (dragObject2.stateAnnouncer != null) {
                    dragObject2.stateAnnouncer.announce(getItemMoveDescription(i7, i8));
                }
            }
        }
    }

    public String getItemMoveDescription(int i, int i2) {
        if (this.mContainerType == 1) {
            return getContext().getString(C0622R.string.move_to_hotseat_position, new Object[]{Integer.valueOf(Math.max(i, i2) + 1)});
        }
        return getContext().getString(C0622R.string.move_to_empty_cell, new Object[]{Integer.valueOf(i2 + 1), Integer.valueOf(i + 1)});
    }

    public void clearDragOutlines() {
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        int[] iArr = this.mDragCell;
        this.mDragCell[1] = -1;
        iArr[0] = -1;
    }

    /* access modifiers changed from: 0000 */
    public int[] findNearestVacantArea(int i, int i2, int i3, int i4, int i5, int i6, int[] iArr, int[] iArr2) {
        return findNearestArea(i, i2, i3, i4, i5, i6, true, iArr, iArr2);
    }

    private void lazyInitTempRectStack() {
        if (this.mTempRectStack.isEmpty()) {
            for (int i = 0; i < this.mCountX * this.mCountY; i++) {
                this.mTempRectStack.push(new Rect());
            }
        }
    }

    private void recycleTempRects(Stack<Rect> stack) {
        while (!stack.isEmpty()) {
            this.mTempRectStack.push(stack.pop());
        }
    }

    private int[] findNearestArea(int i, int i2, int i3, int i4, int i5, int i6, boolean z, int[] iArr, int[] iArr2) {
        int[] iArr3;
        int i7;
        int i8;
        int[] iArr4;
        Rect rect;
        Rect rect2;
        boolean z2;
        int i9;
        Rect rect3;
        int i10 = i3;
        int i11 = i4;
        int i12 = i5;
        int i13 = i6;
        lazyInitTempRectStack();
        int i14 = (int) (((float) i) - (((float) (this.mCellWidth * (i12 - 1))) / 2.0f));
        int i15 = (int) (((float) i2) - (((float) (this.mCellHeight * (i13 - 1))) / 2.0f));
        if (iArr != null) {
            iArr3 = iArr;
        } else {
            iArr3 = new int[2];
        }
        Rect rect4 = new Rect(-1, -1, -1, -1);
        Stack stack = new Stack();
        int i16 = this.mCountX;
        int i17 = this.mCountY;
        if (i10 <= 0 || i11 <= 0 || i12 <= 0 || i13 <= 0 || i12 < i10 || i13 < i11) {
            return iArr3;
        }
        int i18 = 0;
        double d = Double.MAX_VALUE;
        while (i18 < i17 - (i11 - 1)) {
            int i19 = 0;
            while (i19 < i16 - (i10 - 1)) {
                if (z) {
                    for (int i20 = 0; i20 < i10; i20++) {
                        int i21 = 0;
                        while (i21 < i11) {
                            iArr4 = iArr3;
                            if (this.mOccupied.cells[i19 + i20][i18 + i21]) {
                                i7 = i14;
                                i8 = i15;
                                rect = rect4;
                                break;
                            }
                            i21++;
                            iArr3 = iArr4;
                        }
                        int[] iArr5 = iArr3;
                    }
                    iArr4 = iArr3;
                    boolean z3 = i10 >= i12;
                    boolean z4 = i11 >= i13;
                    boolean z5 = true;
                    while (true) {
                        if (z3 && z4) {
                            break;
                        }
                        if (!z5 || z3) {
                            i9 = i15;
                            rect3 = rect4;
                            if (!z4) {
                                int i22 = 0;
                                while (i22 < i10) {
                                    int i23 = i18 + i11;
                                    int i24 = i10;
                                    if (i23 > i17 - 1 || this.mOccupied.cells[i19 + i22][i23]) {
                                        z4 = true;
                                    }
                                    i22++;
                                    i10 = i24;
                                }
                                int i25 = i10;
                                if (!z4) {
                                    i11++;
                                }
                                i10 = i25;
                            } else {
                                int i26 = i10;
                            }
                        } else {
                            boolean z6 = z3;
                            int i27 = 0;
                            while (i27 < i11) {
                                Rect rect5 = rect4;
                                int i28 = i19 + i10;
                                int i29 = i15;
                                if (i28 > i16 - 1 || this.mOccupied.cells[i28][i18 + i27]) {
                                    z6 = true;
                                }
                                i27++;
                                rect4 = rect5;
                                i15 = i29;
                            }
                            i9 = i15;
                            rect3 = rect4;
                            if (!z6) {
                                i10++;
                            }
                            z3 = z6;
                        }
                        z3 |= i10 >= i12;
                        z4 |= i11 >= i13;
                        z5 = !z5;
                        rect4 = rect3;
                        i15 = i9;
                    }
                    i8 = i15;
                    rect2 = rect4;
                } else {
                    i8 = i15;
                    iArr4 = iArr3;
                    rect2 = rect4;
                    i10 = -1;
                    i11 = -1;
                }
                int[] iArr6 = this.mTmpPoint;
                cellToCenterPoint(i19, i18, iArr6);
                Rect rect6 = (Rect) this.mTempRectStack.pop();
                rect6.set(i19, i18, i19 + i10, i18 + i11);
                Iterator it = stack.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((Rect) it.next()).contains(rect6)) {
                            z2 = true;
                            break;
                        }
                    } else {
                        z2 = false;
                        break;
                    }
                }
                stack.push(rect6);
                i7 = i14;
                double hypot = Math.hypot((double) (iArr6[0] - i14), (double) (iArr6[1] - i8));
                if (hypot > d || z2) {
                    rect = rect2;
                    if (!rect6.contains(rect)) {
                        i19++;
                        rect4 = rect;
                        iArr3 = iArr4;
                        i15 = i8;
                        i14 = i7;
                        i10 = i3;
                        i11 = i4;
                        i12 = i5;
                        i13 = i6;
                    }
                } else {
                    rect = rect2;
                }
                iArr4[0] = i19;
                iArr4[1] = i18;
                if (iArr2 != null) {
                    iArr2[0] = i10;
                    iArr2[1] = i11;
                }
                rect.set(rect6);
                d = hypot;
                i19++;
                rect4 = rect;
                iArr3 = iArr4;
                i15 = i8;
                i14 = i7;
                i10 = i3;
                i11 = i4;
                i12 = i5;
                i13 = i6;
            }
            int i30 = i14;
            int i31 = i15;
            int[] iArr7 = iArr3;
            Rect rect7 = rect4;
            i18++;
            i10 = i3;
            i11 = i4;
            i12 = i5;
            i13 = i6;
        }
        int[] iArr8 = iArr3;
        if (d == Double.MAX_VALUE) {
            iArr8[0] = -1;
            iArr8[1] = -1;
        }
        recycleTempRects(stack);
        return iArr8;
    }

    private int[] findNearestArea(int i, int i2, int i3, int i4, int[] iArr, boolean[][] zArr, boolean[][] zArr2, int[] iArr2) {
        int i5;
        int i6 = i3;
        int i7 = i4;
        int[] iArr3 = iArr2 != null ? iArr2 : new int[2];
        int i8 = this.mCountX;
        int i9 = this.mCountY;
        int i10 = 0;
        float f = Float.MAX_VALUE;
        int i11 = Integer.MIN_VALUE;
        while (i10 < i9 - (i7 - 1)) {
            int i12 = i11;
            float f2 = f;
            int i13 = 0;
            while (i13 < i8 - (i6 - 1)) {
                int i14 = 0;
                while (true) {
                    if (i14 < i6) {
                        int i15 = 0;
                        while (i15 < i7) {
                            if (!zArr[i13 + i14][i10 + i15] || (zArr2 != null && !zArr2[i14][i15])) {
                                i15++;
                            }
                        }
                        i14++;
                    } else {
                        int i16 = i13 - i;
                        i5 = i13;
                        int i17 = i10 - i2;
                        float hypot = (float) Math.hypot((double) i16, (double) i17);
                        int[] iArr4 = this.mTmpPoint;
                        computeDirectionVector((float) i16, (float) i17, iArr4);
                        int i18 = (iArr[0] * iArr4[0]) + (iArr[1] * iArr4[1]);
                        if (Float.compare(hypot, f2) < 0 || (Float.compare(hypot, f2) == 0 && i18 > i12)) {
                            iArr3[0] = i5;
                            iArr3[1] = i10;
                            f2 = hypot;
                            i12 = i18;
                        }
                    }
                }
                i5 = i13;
                i13 = i5 + 1;
                i6 = i3;
                i7 = i4;
            }
            i10++;
            f = f2;
            i11 = i12;
            i6 = i3;
            i7 = i4;
        }
        if (f == Float.MAX_VALUE) {
            iArr3[0] = -1;
            iArr3[1] = -1;
        }
        return iArr3;
    }

    private boolean addViewToTempLocation(View view, Rect rect, int[] iArr, ItemConfiguration itemConfiguration) {
        CellAndSpan cellAndSpan = (CellAndSpan) itemConfiguration.map.get(view);
        boolean z = false;
        this.mTmpOccupied.markCells(cellAndSpan, false);
        this.mTmpOccupied.markCells(rect, true);
        findNearestArea(cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.spanX, cellAndSpan.spanY, iArr, this.mTmpOccupied.cells, null, this.mTempLocation);
        if (this.mTempLocation[0] >= 0 && this.mTempLocation[1] >= 0) {
            cellAndSpan.cellX = this.mTempLocation[0];
            cellAndSpan.cellY = this.mTempLocation[1];
            z = true;
        }
        this.mTmpOccupied.markCells(cellAndSpan, true);
        return z;
    }

    private boolean pushViewsToTempLocation(ArrayList<View> arrayList, Rect rect, int[] iArr, View view, ItemConfiguration itemConfiguration) {
        int i;
        int i2;
        ViewCluster viewCluster = new ViewCluster(arrayList, itemConfiguration);
        Rect boundingRect = viewCluster.getBoundingRect();
        boolean z = false;
        if (iArr[0] < 0) {
            i2 = boundingRect.right - rect.left;
            i = 1;
        } else if (iArr[0] > 0) {
            i = 4;
            i2 = rect.right - boundingRect.left;
        } else if (iArr[1] < 0) {
            i = 2;
            i2 = boundingRect.bottom - rect.top;
        } else {
            i = 8;
            i2 = rect.bottom - boundingRect.top;
        }
        if (i2 <= 0) {
            return false;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            this.mTmpOccupied.markCells((CellAndSpan) itemConfiguration.map.get((View) it.next()), false);
        }
        itemConfiguration.save();
        viewCluster.sortConfigurationForEdgePush(i);
        boolean z2 = false;
        while (i2 > 0 && !z2) {
            Iterator it2 = itemConfiguration.sortedViews.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                View view2 = (View) it2.next();
                if (!viewCluster.views.contains(view2) && view2 != view && viewCluster.isViewTouchingEdge(view2, i)) {
                    if (!((LayoutParams) view2.getLayoutParams()).canReorder) {
                        z2 = true;
                        break;
                    }
                    viewCluster.addView(view2);
                    this.mTmpOccupied.markCells((CellAndSpan) itemConfiguration.map.get(view2), false);
                }
            }
            i2--;
            viewCluster.shift(i, 1);
        }
        Rect boundingRect2 = viewCluster.getBoundingRect();
        if (z2 || boundingRect2.left < 0 || boundingRect2.right > this.mCountX || boundingRect2.top < 0 || boundingRect2.bottom > this.mCountY) {
            itemConfiguration.restore();
        } else {
            z = true;
        }
        Iterator it3 = viewCluster.views.iterator();
        while (it3.hasNext()) {
            this.mTmpOccupied.markCells((CellAndSpan) itemConfiguration.map.get((View) it3.next()), true);
        }
        return z;
    }

    private boolean addViewsToTempLocation(ArrayList<View> arrayList, Rect rect, int[] iArr, View view, ItemConfiguration itemConfiguration) {
        boolean z;
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        if (arrayList.size() == 0) {
            return true;
        }
        Rect rect2 = new Rect();
        itemConfiguration2.getBoundingRectForViews(arrayList, rect2);
        Iterator it = arrayList.iterator();
        while (true) {
            z = false;
            if (!it.hasNext()) {
                break;
            }
            this.mTmpOccupied.markCells((CellAndSpan) itemConfiguration2.map.get((View) it.next()), false);
        }
        GridOccupancy gridOccupancy = new GridOccupancy(rect2.width(), rect2.height());
        int i = rect2.top;
        int i2 = rect2.left;
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            CellAndSpan cellAndSpan = (CellAndSpan) itemConfiguration2.map.get((View) it2.next());
            gridOccupancy.markCells(cellAndSpan.cellX - i2, cellAndSpan.cellY - i, cellAndSpan.spanX, cellAndSpan.spanY, true);
        }
        this.mTmpOccupied.markCells(rect, true);
        findNearestArea(rect2.left, rect2.top, rect2.width(), rect2.height(), iArr, this.mTmpOccupied.cells, gridOccupancy.cells, this.mTempLocation);
        if (this.mTempLocation[0] >= 0 && this.mTempLocation[1] >= 0) {
            int i3 = this.mTempLocation[0] - rect2.left;
            int i4 = this.mTempLocation[1] - rect2.top;
            Iterator it3 = arrayList.iterator();
            while (it3.hasNext()) {
                CellAndSpan cellAndSpan2 = (CellAndSpan) itemConfiguration2.map.get((View) it3.next());
                cellAndSpan2.cellX += i3;
                cellAndSpan2.cellY += i4;
            }
            z = true;
        }
        Iterator it4 = arrayList.iterator();
        while (it4.hasNext()) {
            this.mTmpOccupied.markCells((CellAndSpan) itemConfiguration2.map.get((View) it4.next()), true);
        }
        return z;
    }

    private boolean attemptPushInDirection(ArrayList<View> arrayList, Rect rect, int[] iArr, View view, ItemConfiguration itemConfiguration) {
        if (Math.abs(iArr[0]) + Math.abs(iArr[1]) > 1) {
            int i = iArr[1];
            iArr[1] = 0;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[1] = i;
            int i2 = iArr[0];
            iArr[0] = 0;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = i2;
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            int i3 = iArr[1];
            iArr[1] = 0;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[1] = i3;
            int i4 = iArr[0];
            iArr[0] = 0;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = i4;
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
        } else if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
            return true;
        } else {
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            int i5 = iArr[1];
            iArr[1] = iArr[0];
            iArr[0] = i5;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            if (pushViewsToTempLocation(arrayList, rect, iArr, view, itemConfiguration)) {
                return true;
            }
            iArr[0] = iArr[0] * -1;
            iArr[1] = iArr[1] * -1;
            int i6 = iArr[1];
            iArr[1] = iArr[0];
            iArr[0] = i6;
        }
        return false;
    }

    private boolean rearrangementExists(int i, int i2, int i3, int i4, int[] iArr, View view, ItemConfiguration itemConfiguration) {
        if (i < 0 || i2 < 0) {
            return false;
        }
        this.mIntersectingViews.clear();
        int i5 = i3 + i;
        int i6 = i4 + i2;
        this.mOccupiedRect.set(i, i2, i5, i6);
        if (view != null) {
            CellAndSpan cellAndSpan = (CellAndSpan) itemConfiguration.map.get(view);
            if (cellAndSpan != null) {
                cellAndSpan.cellX = i;
                cellAndSpan.cellY = i2;
            }
        }
        Rect rect = new Rect(i, i2, i5, i6);
        Rect rect2 = new Rect();
        for (View view2 : itemConfiguration.map.keySet()) {
            if (view2 != view) {
                CellAndSpan cellAndSpan2 = (CellAndSpan) itemConfiguration.map.get(view2);
                LayoutParams layoutParams = (LayoutParams) view2.getLayoutParams();
                rect2.set(cellAndSpan2.cellX, cellAndSpan2.cellY, cellAndSpan2.cellX + cellAndSpan2.spanX, cellAndSpan2.cellY + cellAndSpan2.spanY);
                if (!Rect.intersects(rect, rect2)) {
                    continue;
                } else if (!layoutParams.canReorder) {
                    return false;
                } else {
                    this.mIntersectingViews.add(view2);
                }
            }
        }
        itemConfiguration.intersectingViews = new ArrayList<>(this.mIntersectingViews);
        if (attemptPushInDirection(this.mIntersectingViews, this.mOccupiedRect, iArr, view, itemConfiguration)) {
            return true;
        }
        if (addViewsToTempLocation(this.mIntersectingViews, this.mOccupiedRect, iArr, view, itemConfiguration)) {
            return true;
        }
        Iterator it = this.mIntersectingViews.iterator();
        while (it.hasNext()) {
            if (!addViewToTempLocation((View) it.next(), this.mOccupiedRect, iArr, itemConfiguration)) {
                return false;
            }
        }
        return true;
    }

    private void computeDirectionVector(float f, float f2, int[] iArr) {
        double atan = Math.atan((double) (f2 / f));
        iArr[0] = 0;
        iArr[1] = 0;
        if (Math.abs(Math.cos(atan)) > 0.5d) {
            iArr[0] = (int) Math.signum(f);
        }
        if (Math.abs(Math.sin(atan)) > 0.5d) {
            iArr[1] = (int) Math.signum(f2);
        }
    }

    private ItemConfiguration findReorderSolution(int i, int i2, int i3, int i4, int i5, int i6, int[] iArr, View view, boolean z, ItemConfiguration itemConfiguration) {
        int i7 = i4;
        int i8 = i5;
        int i9 = i6;
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        copyCurrentStateToSolution(itemConfiguration2, false);
        this.mOccupied.copyTo(this.mTmpOccupied);
        int i10 = i5;
        int i11 = i6;
        int[] findNearestArea = findNearestArea(i, i2, i10, i11, new int[2]);
        if (rearrangementExists(findNearestArea[0], findNearestArea[1], i10, i11, iArr, view, itemConfiguration)) {
            itemConfiguration2.isSolution = true;
            itemConfiguration2.cellX = findNearestArea[0];
            itemConfiguration2.cellY = findNearestArea[1];
            itemConfiguration2.spanX = i8;
            itemConfiguration2.spanY = i9;
        } else if (i8 > i3 && (i7 == i9 || z)) {
            return findReorderSolution(i, i2, i3, i4, i8 - 1, i6, iArr, view, false, itemConfiguration);
        } else if (i9 > i7) {
            return findReorderSolution(i, i2, i3, i4, i5, i9 - 1, iArr, view, true, itemConfiguration);
        } else {
            itemConfiguration2.isSolution = false;
        }
        return itemConfiguration2;
    }

    private void copyCurrentStateToSolution(ItemConfiguration itemConfiguration, boolean z) {
        CellAndSpan cellAndSpan;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (z) {
                cellAndSpan = new CellAndSpan(layoutParams.tmpCellX, layoutParams.tmpCellY, layoutParams.cellHSpan, layoutParams.cellVSpan);
            } else {
                cellAndSpan = new CellAndSpan(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan);
            }
            itemConfiguration.add(childAt, cellAndSpan);
        }
    }

    private void copySolutionToTempState(ItemConfiguration itemConfiguration, View view) {
        this.mTmpOccupied.clear();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            if (childAt != view) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                CellAndSpan cellAndSpan = (CellAndSpan) itemConfiguration.map.get(childAt);
                if (cellAndSpan != null) {
                    layoutParams.tmpCellX = cellAndSpan.cellX;
                    layoutParams.tmpCellY = cellAndSpan.cellY;
                    layoutParams.cellHSpan = cellAndSpan.spanX;
                    layoutParams.cellVSpan = cellAndSpan.spanY;
                    this.mTmpOccupied.markCells(cellAndSpan, true);
                }
            }
        }
        this.mTmpOccupied.markCells((CellAndSpan) itemConfiguration, true);
    }

    private void animateItemsToSolution(ItemConfiguration itemConfiguration, View view, boolean z) {
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        GridOccupancy gridOccupancy = this.mTmpOccupied;
        gridOccupancy.clear();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            if (childAt != view) {
                CellAndSpan cellAndSpan = (CellAndSpan) itemConfiguration2.map.get(childAt);
                if (cellAndSpan != null) {
                    animateChildToPosition(childAt, cellAndSpan.cellX, cellAndSpan.cellY, 150, 0, false, false);
                    gridOccupancy.markCells(cellAndSpan, true);
                }
            }
        }
        if (z) {
            gridOccupancy.markCells((CellAndSpan) itemConfiguration2, true);
        }
    }

    private void beginOrAdjustReorderPreviewAnimations(ItemConfiguration itemConfiguration, View view, int i, int i2) {
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i3);
            if (childAt != view) {
                CellAndSpan cellAndSpan = (CellAndSpan) itemConfiguration2.map.get(childAt);
                boolean z = i2 == 0 && itemConfiguration2.intersectingViews != null && !itemConfiguration2.intersectingViews.contains(childAt);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (cellAndSpan != null && !z) {
                    ReorderPreviewAnimation reorderPreviewAnimation = new ReorderPreviewAnimation(this, childAt, i2, layoutParams.cellX, layoutParams.cellY, cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.spanX, cellAndSpan.spanY);
                    reorderPreviewAnimation.animate();
                }
            }
        }
    }

    private void completeAndClearReorderPreviewAnimations() {
        for (ReorderPreviewAnimation completeAnimationImmediately : this.mShakeAnimators.values()) {
            completeAnimationImmediately.completeAnimationImmediately();
        }
        this.mShakeAnimators.clear();
    }

    private void commitTempPlacement() {
        int i;
        int i2;
        this.mTmpOccupied.copyTo(this.mOccupied);
        long idForScreen = this.mLauncher.getWorkspace().getIdForScreen(this);
        if (this.mContainerType == 1) {
            idForScreen = -1;
            i = -101;
        } else {
            i = -100;
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        int i3 = 0;
        while (i3 < childCount) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i3);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            ItemInfo itemInfo = (ItemInfo) childAt.getTag();
            if (itemInfo != null) {
                boolean z = (itemInfo.cellX == layoutParams.tmpCellX && itemInfo.cellY == layoutParams.tmpCellY && itemInfo.spanX == layoutParams.cellHSpan && itemInfo.spanY == layoutParams.cellVSpan) ? false : true;
                int i4 = layoutParams.tmpCellX;
                layoutParams.cellX = i4;
                itemInfo.cellX = i4;
                int i5 = layoutParams.tmpCellY;
                layoutParams.cellY = i5;
                itemInfo.cellY = i5;
                itemInfo.spanX = layoutParams.cellHSpan;
                itemInfo.spanY = layoutParams.cellVSpan;
                if (z) {
                    i2 = i3;
                    this.mLauncher.getModelWriter().modifyItemInDatabase(itemInfo, (long) i, idForScreen, itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY);
                    i3 = i2 + 1;
                }
            }
            i2 = i3;
            i3 = i2 + 1;
        }
    }

    private void setUseTempCoords(boolean z) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((LayoutParams) this.mShortcutsAndWidgets.getChildAt(i).getLayoutParams()).useTmpCoords = z;
        }
    }

    private ItemConfiguration findConfigurationNoShuffle(int i, int i2, int i3, int i4, int i5, int i6, View view, ItemConfiguration itemConfiguration) {
        ItemConfiguration itemConfiguration2 = itemConfiguration;
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        findNearestVacantArea(i, i2, i3, i4, i5, i6, iArr, iArr2);
        if (iArr[0] < 0 || iArr[1] < 0) {
            itemConfiguration2.isSolution = false;
        } else {
            copyCurrentStateToSolution(itemConfiguration2, false);
            itemConfiguration2.cellX = iArr[0];
            itemConfiguration2.cellY = iArr[1];
            itemConfiguration2.spanX = iArr2[0];
            itemConfiguration2.spanY = iArr2[1];
            itemConfiguration2.isSolution = true;
        }
        return itemConfiguration2;
    }

    private void getDirectionVectorForDrop(int i, int i2, int i3, int i4, View view, int[] iArr) {
        int i5 = i3;
        int i6 = i4;
        int[] iArr2 = iArr;
        int[] iArr3 = new int[2];
        int i7 = i3;
        int i8 = i4;
        findNearestArea(i, i2, i7, i8, iArr3);
        Rect rect = new Rect();
        regionToRect(iArr3[0], iArr3[1], i7, i8, rect);
        rect.offset(i - rect.centerX(), i2 - rect.centerY());
        Rect rect2 = new Rect();
        Rect rect3 = rect2;
        getViewsIntersectingRegion(iArr3[0], iArr3[1], i7, i8, view, rect2, this.mIntersectingViews);
        int width = rect3.width();
        int height = rect3.height();
        Rect rect4 = rect3;
        Rect rect5 = rect4;
        regionToRect(rect4.left, rect4.top, rect4.width(), rect4.height(), rect4);
        int centerX = (rect5.centerX() - i) / i5;
        int centerY = (rect5.centerY() - i2) / i6;
        if (width == this.mCountX || i5 == this.mCountX) {
            centerX = 0;
        }
        if (height == this.mCountY || i6 == this.mCountY) {
            centerY = 0;
        }
        if (centerX == 0 && centerY == 0) {
            iArr2[0] = 1;
            iArr2[1] = 0;
            return;
        }
        computeDirectionVector((float) centerX, (float) centerY, iArr2);
    }

    private void getViewsIntersectingRegion(int i, int i2, int i3, int i4, View view, Rect rect, ArrayList<View> arrayList) {
        if (rect != null) {
            rect.set(i, i2, i + i3, i2 + i4);
        }
        arrayList.clear();
        Rect rect2 = new Rect(i, i2, i3 + i, i4 + i2);
        Rect rect3 = new Rect();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i5);
            if (childAt != view) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                rect3.set(layoutParams.cellX, layoutParams.cellY, layoutParams.cellX + layoutParams.cellHSpan, layoutParams.cellY + layoutParams.cellVSpan);
                if (Rect.intersects(rect2, rect3)) {
                    this.mIntersectingViews.add(childAt);
                    if (rect != null) {
                        rect.union(rect3);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isNearestDropLocationOccupied(int i, int i2, int i3, int i4, View view, int[] iArr) {
        int i5 = i3;
        int i6 = i4;
        int[] findNearestArea = findNearestArea(i, i2, i5, i6, iArr);
        getViewsIntersectingRegion(findNearestArea[0], findNearestArea[1], i5, i6, view, null, this.mIntersectingViews);
        return !this.mIntersectingViews.isEmpty();
    }

    /* access modifiers changed from: 0000 */
    public void revertTempState() {
        completeAndClearReorderPreviewAnimations();
        if (isItemPlacementDirty()) {
            int childCount = this.mShortcutsAndWidgets.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.mShortcutsAndWidgets.getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.tmpCellX != layoutParams.cellX || layoutParams.tmpCellY != layoutParams.cellY) {
                    layoutParams.tmpCellX = layoutParams.cellX;
                    layoutParams.tmpCellY = layoutParams.cellY;
                    animateChildToPosition(childAt, layoutParams.cellX, layoutParams.cellY, 150, 0, false, false);
                }
            }
            setItemPlacementDirty(false);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean createAreaForResize(int i, int i2, int i3, int i4, View view, int[] iArr, boolean z) {
        View view2 = view;
        boolean z2 = z;
        int[] iArr2 = new int[2];
        int i5 = i3;
        int i6 = i4;
        regionToCenterPoint(i, i2, i5, i6, iArr2);
        ItemConfiguration findReorderSolution = findReorderSolution(iArr2[0], iArr2[1], i5, i6, i3, i4, iArr, view, true, new ItemConfiguration());
        setUseTempCoords(true);
        if (findReorderSolution != null && findReorderSolution.isSolution) {
            copySolutionToTempState(findReorderSolution, view2);
            setItemPlacementDirty(true);
            animateItemsToSolution(findReorderSolution, view2, z2);
            if (z2) {
                commitTempPlacement();
                completeAndClearReorderPreviewAnimations();
                setItemPlacementDirty(false);
            } else {
                beginOrAdjustReorderPreviewAnimations(findReorderSolution, view2, 150, 1);
            }
            this.mShortcutsAndWidgets.requestLayout();
        }
        return findReorderSolution.isSolution;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00f9, code lost:
        if (r13 == 3) goto L_0x00fe;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int[] performReorder(int r20, int r21, int r22, int r23, int r24, int r25, android.view.View r26, int[] r27, int[] r28, int r29) {
        /*
            r19 = this;
            r11 = r19
            r12 = r26
            r13 = r29
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r24
            r4 = r25
            r5 = r27
            int[] r14 = r0.findNearestArea(r1, r2, r3, r4, r5)
            r15 = 2
            if (r28 != 0) goto L_0x001e
            int[] r0 = new int[r15]
            r16 = r0
            goto L_0x0020
        L_0x001e:
            r16 = r28
        L_0x0020:
            r10 = 3
            r9 = 1
            r8 = 0
            if (r13 == r15) goto L_0x002a
            if (r13 == r10) goto L_0x002a
            r0 = 4
            if (r13 != r0) goto L_0x004f
        L_0x002a:
            int[] r0 = r11.mPreviousReorderDirection
            r0 = r0[r8]
            r1 = -100
            if (r0 == r1) goto L_0x004f
            int[] r0 = r11.mDirectionVector
            int[] r2 = r11.mPreviousReorderDirection
            r2 = r2[r8]
            r0[r8] = r2
            int[] r0 = r11.mDirectionVector
            int[] r2 = r11.mPreviousReorderDirection
            r2 = r2[r9]
            r0[r9] = r2
            if (r13 == r15) goto L_0x0046
            if (r13 != r10) goto L_0x0070
        L_0x0046:
            int[] r0 = r11.mPreviousReorderDirection
            r0[r8] = r1
            int[] r0 = r11.mPreviousReorderDirection
            r0[r9] = r1
            goto L_0x0070
        L_0x004f:
            int[] r6 = r11.mDirectionVector
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r24
            r4 = r25
            r5 = r26
            r0.getDirectionVectorForDrop(r1, r2, r3, r4, r5, r6)
            int[] r0 = r11.mPreviousReorderDirection
            int[] r1 = r11.mDirectionVector
            r1 = r1[r8]
            r0[r8] = r1
            int[] r0 = r11.mPreviousReorderDirection
            int[] r1 = r11.mDirectionVector
            r1 = r1[r9]
            r0[r9] = r1
        L_0x0070:
            int[] r7 = r11.mDirectionVector
            r17 = 1
            com.android.launcher3.CellLayout$ItemConfiguration r6 = new com.android.launcher3.CellLayout$ItemConfiguration
            r5 = 0
            r6.<init>()
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            r15 = r5
            r5 = r24
            r18 = r6
            r6 = r25
            r8 = r26
            r9 = r17
            r10 = r18
            com.android.launcher3.CellLayout$ItemConfiguration r9 = r0.findReorderSolution(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            com.android.launcher3.CellLayout$ItemConfiguration r8 = new com.android.launcher3.CellLayout$ItemConfiguration
            r8.<init>()
            r7 = r26
            com.android.launcher3.CellLayout$ItemConfiguration r5 = r0.findConfigurationNoShuffle(r1, r2, r3, r4, r5, r6, r7, r8)
            boolean r0 = r9.isSolution
            if (r0 == 0) goto L_0x00b0
            int r0 = r9.area()
            int r1 = r5.area()
            if (r0 < r1) goto L_0x00b0
            r5 = r9
            goto L_0x00b6
        L_0x00b0:
            boolean r0 = r5.isSolution
            if (r0 == 0) goto L_0x00b5
            goto L_0x00b6
        L_0x00b5:
            r5 = r15
        L_0x00b6:
            r0 = -1
            if (r13 != 0) goto L_0x00dc
            if (r5 == 0) goto L_0x00d1
            r1 = 0
            r11.beginOrAdjustReorderPreviewAnimations(r5, r12, r1, r1)
            int r0 = r5.cellX
            r14[r1] = r0
            int r0 = r5.cellY
            r2 = 1
            r14[r2] = r0
            int r0 = r5.spanX
            r16[r1] = r0
            int r0 = r5.spanY
            r16[r2] = r0
            goto L_0x00db
        L_0x00d1:
            r1 = 0
            r2 = 1
            r16[r2] = r0
            r16[r1] = r0
            r14[r2] = r0
            r14[r1] = r0
        L_0x00db:
            return r14
        L_0x00dc:
            r1 = 0
            r2 = 1
            r11.setUseTempCoords(r2)
            if (r5 == 0) goto L_0x0122
            int r0 = r5.cellX
            r14[r1] = r0
            int r0 = r5.cellY
            r14[r2] = r0
            int r0 = r5.spanX
            r16[r1] = r0
            int r0 = r5.spanY
            r16[r2] = r0
            if (r13 == r2) goto L_0x00fc
            r0 = 2
            if (r13 == r0) goto L_0x00fd
            r3 = 3
            if (r13 != r3) goto L_0x0120
            goto L_0x00fe
        L_0x00fc:
            r0 = 2
        L_0x00fd:
            r3 = 3
        L_0x00fe:
            r11.copySolutionToTempState(r5, r12)
            r11.setItemPlacementDirty(r2)
            if (r13 != r0) goto L_0x0108
            r4 = 1
            goto L_0x0109
        L_0x0108:
            r4 = 0
        L_0x0109:
            r11.animateItemsToSolution(r5, r12, r4)
            if (r13 == r0) goto L_0x0117
            if (r13 != r3) goto L_0x0111
            goto L_0x0117
        L_0x0111:
            r0 = 150(0x96, float:2.1E-43)
            r11.beginOrAdjustReorderPreviewAnimations(r5, r12, r0, r2)
            goto L_0x0120
        L_0x0117:
            r19.commitTempPlacement()
            r19.completeAndClearReorderPreviewAnimations()
            r11.setItemPlacementDirty(r1)
        L_0x0120:
            r0 = 2
            goto L_0x012c
        L_0x0122:
            r16[r2] = r0
            r16[r1] = r0
            r14[r2] = r0
            r14[r1] = r0
            r0 = 2
            r2 = 0
        L_0x012c:
            if (r13 == r0) goto L_0x0130
            if (r2 != 0) goto L_0x0133
        L_0x0130:
            r11.setUseTempCoords(r1)
        L_0x0133:
            com.android.launcher3.ShortcutAndWidgetContainer r0 = r11.mShortcutsAndWidgets
            r0.requestLayout()
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.CellLayout.performReorder(int, int, int, int, int, int, android.view.View, int[], int[], int):int[]");
    }

    /* access modifiers changed from: 0000 */
    public void setItemPlacementDirty(boolean z) {
        this.mItemPlacementDirty = z;
    }

    /* access modifiers changed from: 0000 */
    public boolean isItemPlacementDirty() {
        return this.mItemPlacementDirty;
    }

    public int[] findNearestArea(int i, int i2, int i3, int i4, int[] iArr) {
        return findNearestArea(i, i2, i3, i4, i3, i4, false, iArr, null);
    }

    /* access modifiers changed from: 0000 */
    public boolean existsEmptyCell() {
        return findCellForSpan(null, 1, 1);
    }

    public boolean findCellForSpan(int[] iArr, int i, int i2) {
        if (iArr == null) {
            iArr = new int[2];
        }
        return this.mOccupied.findVacantCell(iArr, i, i2);
    }

    /* access modifiers changed from: 0000 */
    public void onDragEnter() {
        this.mDragging = true;
    }

    /* access modifiers changed from: 0000 */
    public void onDragExit() {
        if (this.mDragging) {
            this.mDragging = false;
        }
        int[] iArr = this.mDragCell;
        this.mDragCell[1] = -1;
        iArr[0] = -1;
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        this.mDragOutlineCurrent = (this.mDragOutlineCurrent + 1) % this.mDragOutlineAnims.length;
        revertTempState();
        setIsDragOverlapping(false);
    }

    /* access modifiers changed from: 0000 */
    public void onDropChild(View view) {
        if (view != null) {
            ((LayoutParams) view.getLayoutParams()).dropped = true;
            view.requestLayout();
            markCellsAsOccupiedForView(view);
        }
    }

    public void cellToRect(int i, int i2, int i3, int i4, Rect rect) {
        int i5 = this.mCellWidth;
        int i6 = this.mCellHeight;
        int paddingLeft = getPaddingLeft() + (i * i5);
        int paddingTop = getPaddingTop() + (i2 * i6);
        rect.set(paddingLeft, paddingTop, (i3 * i5) + paddingLeft, (i4 * i6) + paddingTop);
    }

    public void markCellsAsOccupiedForView(View view) {
        if (view != null && view.getParent() == this.mShortcutsAndWidgets) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            this.mOccupied.markCells(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, true);
        }
    }

    public void markCellsAsUnoccupiedForView(View view) {
        if (view != null && view.getParent() == this.mShortcutsAndWidgets) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            this.mOccupied.markCells(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, false);
        }
    }

    public int getDesiredWidth() {
        return getPaddingLeft() + getPaddingRight() + (this.mCountX * this.mCellWidth);
    }

    public int getDesiredHeight() {
        return getPaddingTop() + getPaddingBottom() + (this.mCountY * this.mCellHeight);
    }

    public boolean isOccupied(int i, int i2) {
        if (i < this.mCountX && i2 < this.mCountY) {
            return this.mOccupied.cells[i][i2];
        }
        throw new RuntimeException("Position exceeds the bound of this CellLayout");
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    public boolean hasReorderSolution(ItemInfo itemInfo) {
        ItemInfo itemInfo2 = itemInfo;
        int[] iArr = new int[2];
        char c = 0;
        int i = 0;
        while (i < getCountX()) {
            int i2 = 0;
            while (i2 < getCountY()) {
                cellToPoint(i, i2, iArr);
                int i3 = i2;
                if (findReorderSolution(iArr[c], iArr[1], itemInfo2.minSpanX, itemInfo2.minSpanY, itemInfo2.spanX, itemInfo2.spanY, this.mDirectionVector, null, true, new ItemConfiguration()).isSolution) {
                    return true;
                }
                i2 = i3 + 1;
                c = 0;
            }
            i++;
            c = 0;
        }
        return false;
    }

    public boolean isRegionVacant(int i, int i2, int i3, int i4) {
        return this.mOccupied.isRegionVacant(i, i2, i3, i4);
    }
}
