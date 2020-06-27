package p013io.virtualapp.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.p004v7.widget.RecyclerView;
import android.support.p004v7.widget.RecyclerView.Adapter;
import android.support.p004v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import io.va.exposed.R;
import p013io.virtualapp.C1250R;

/* renamed from: io.virtualapp.widgets.DragSelectRecyclerView */
public class DragSelectRecyclerView extends RecyclerView {
    private static final int AUTO_SCROLL_DELAY = 25;
    private static final boolean LOGGING = false;
    private DragSelectRecyclerViewAdapter<?> mAdapter;
    /* access modifiers changed from: private */
    public Handler mAutoScrollHandler;
    private Runnable mAutoScrollRunnable = new Runnable() {
        public void run() {
            if (DragSelectRecyclerView.this.mAutoScrollHandler != null) {
                if (DragSelectRecyclerView.this.mInTopHotspot) {
                    DragSelectRecyclerView.this.scrollBy(0, -DragSelectRecyclerView.this.mAutoScrollVelocity);
                    DragSelectRecyclerView.this.mAutoScrollHandler.postDelayed(this, 25);
                } else if (DragSelectRecyclerView.this.mInBottomHotspot) {
                    DragSelectRecyclerView.this.scrollBy(0, DragSelectRecyclerView.this.mAutoScrollVelocity);
                    DragSelectRecyclerView.this.mAutoScrollHandler.postDelayed(this, 25);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int mAutoScrollVelocity;
    private RectF mBottomBoundRect;
    private boolean mDebugEnabled = false;
    private Paint mDebugPaint;
    private boolean mDragSelectActive;
    private FingerListener mFingerListener;
    private int mHotspotBottomBoundEnd;
    private int mHotspotBottomBoundStart;
    private int mHotspotHeight;
    private int mHotspotOffsetBottom;
    private int mHotspotOffsetTop;
    private int mHotspotTopBoundEnd;
    private int mHotspotTopBoundStart;
    /* access modifiers changed from: private */
    public boolean mInBottomHotspot;
    /* access modifiers changed from: private */
    public boolean mInTopHotspot;
    private int mInitialSelection;
    private int mLastDraggedIndex = -1;
    private int mMaxReached;
    private int mMinReached;
    private RectF mTopBoundRect;

    /* renamed from: io.virtualapp.widgets.DragSelectRecyclerView$FingerListener */
    public interface FingerListener {
        void onDragSelectFingerAction(boolean z);
    }

    private static void LOG(String str, Object... objArr) {
    }

    public DragSelectRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public DragSelectRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public DragSelectRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.mAutoScrollHandler = new Handler();
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.dsrv_defaultHotspotHeight);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, C1250R.styleable.DragSelectRecyclerView, 0, 0);
            try {
                if (!obtainStyledAttributes.getBoolean(0, true)) {
                    this.mHotspotHeight = -1;
                    this.mHotspotOffsetTop = -1;
                    this.mHotspotOffsetBottom = -1;
                    LOG("Auto-scroll disabled", new Object[0]);
                } else {
                    this.mHotspotHeight = obtainStyledAttributes.getDimensionPixelSize(1, dimensionPixelSize);
                    this.mHotspotOffsetTop = obtainStyledAttributes.getDimensionPixelSize(3, 0);
                    this.mHotspotOffsetBottom = obtainStyledAttributes.getDimensionPixelSize(2, 0);
                    LOG("Hotspot height = %d", Integer.valueOf(this.mHotspotHeight));
                }
            } finally {
                obtainStyledAttributes.recycle();
            }
        } else {
            this.mHotspotHeight = dimensionPixelSize;
            LOG("Hotspot height = %d", Integer.valueOf(this.mHotspotHeight));
        }
    }

    public void setFingerListener(@Nullable FingerListener fingerListener) {
        this.mFingerListener = fingerListener;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mHotspotHeight > -1) {
            this.mHotspotTopBoundStart = this.mHotspotOffsetTop;
            this.mHotspotTopBoundEnd = this.mHotspotOffsetTop + this.mHotspotHeight;
            this.mHotspotBottomBoundStart = (getMeasuredHeight() - this.mHotspotHeight) - this.mHotspotOffsetBottom;
            this.mHotspotBottomBoundEnd = getMeasuredHeight() - this.mHotspotOffsetBottom;
            LOG("RecyclerView height = %d", Integer.valueOf(getMeasuredHeight()));
            LOG("Hotspot top bound = %d to %d", Integer.valueOf(this.mHotspotTopBoundStart), Integer.valueOf(this.mHotspotTopBoundStart));
            LOG("Hotspot bottom bound = %d to %d", Integer.valueOf(this.mHotspotBottomBoundStart), Integer.valueOf(this.mHotspotBottomBoundEnd));
        }
    }

    public boolean setDragSelectActive(boolean z, int i) {
        if (!z || !this.mDragSelectActive) {
            this.mLastDraggedIndex = -1;
            this.mMinReached = -1;
            this.mMaxReached = -1;
            if (!this.mAdapter.isIndexSelectable(i)) {
                this.mDragSelectActive = false;
                this.mInitialSelection = -1;
                this.mLastDraggedIndex = -1;
                LOG("Index %d is not selectable.", Integer.valueOf(i));
                return false;
            }
            this.mAdapter.setSelected(i, true);
            this.mDragSelectActive = z;
            this.mInitialSelection = i;
            this.mLastDraggedIndex = i;
            if (this.mFingerListener != null) {
                this.mFingerListener.onDragSelectFingerAction(true);
            }
            LOG("Drag selection initialized, starting at index %d.", Integer.valueOf(i));
            return true;
        }
        LOG("Drag selection is already active.", new Object[0]);
        return false;
    }

    @Deprecated
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof DragSelectRecyclerViewAdapter) {
            setAdapter((DragSelectRecyclerViewAdapter) adapter);
            return;
        }
        throw new IllegalArgumentException("Adapter must be a DragSelectRecyclerViewAdapter.");
    }

    public void setAdapter(DragSelectRecyclerViewAdapter<?> dragSelectRecyclerViewAdapter) {
        super.setAdapter(dragSelectRecyclerViewAdapter);
        this.mAdapter = dragSelectRecyclerViewAdapter;
    }

    private int getItemPosition(MotionEvent motionEvent) {
        View findChildViewUnder = findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (findChildViewUnder == null) {
            return -1;
        }
        if (findChildViewUnder.getTag() != null && (findChildViewUnder.getTag() instanceof ViewHolder)) {
            return ((ViewHolder) findChildViewUnder.getTag()).getAdapterPosition();
        }
        throw new IllegalStateException("Make sure your adapter makes a call to super.onBindViewHolder(), and doesn't override itemView tags.");
    }

    public final void enableDebug() {
        this.mDebugEnabled = true;
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDebugEnabled) {
            if (this.mDebugPaint == null) {
                this.mDebugPaint = new Paint();
                this.mDebugPaint.setColor(-16777216);
                this.mDebugPaint.setAntiAlias(true);
                this.mDebugPaint.setStyle(Style.FILL);
                this.mTopBoundRect = new RectF(0.0f, (float) this.mHotspotTopBoundStart, (float) getMeasuredWidth(), (float) this.mHotspotTopBoundEnd);
                this.mBottomBoundRect = new RectF(0.0f, (float) this.mHotspotBottomBoundStart, (float) getMeasuredWidth(), (float) this.mHotspotBottomBoundEnd);
            }
            canvas.drawRect(this.mTopBoundRect, this.mDebugPaint);
            canvas.drawRect(this.mBottomBoundRect, this.mDebugPaint);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.mAdapter.getItemCount() == 0) {
            return super.dispatchTouchEvent(motionEvent);
        }
        if (this.mDragSelectActive) {
            if (motionEvent.getAction() == 1) {
                this.mDragSelectActive = false;
                this.mInTopHotspot = false;
                this.mInBottomHotspot = false;
                this.mAutoScrollHandler.removeCallbacks(this.mAutoScrollRunnable);
                if (this.mFingerListener != null) {
                    this.mFingerListener.onDragSelectFingerAction(false);
                }
                return true;
            } else if (motionEvent.getAction() == 2) {
                if (this.mHotspotHeight > -1) {
                    if (motionEvent.getY() >= ((float) this.mHotspotTopBoundStart) && motionEvent.getY() <= ((float) this.mHotspotTopBoundEnd)) {
                        this.mInBottomHotspot = false;
                        if (!this.mInTopHotspot) {
                            this.mInTopHotspot = true;
                            LOG("Now in TOP hotspot", new Object[0]);
                            this.mAutoScrollHandler.removeCallbacks(this.mAutoScrollRunnable);
                            this.mAutoScrollHandler.postDelayed(this.mAutoScrollRunnable, 25);
                        }
                        this.mAutoScrollVelocity = ((int) (((float) (this.mHotspotTopBoundEnd - this.mHotspotTopBoundStart)) - (motionEvent.getY() - ((float) this.mHotspotTopBoundStart)))) / 2;
                        LOG("Auto scroll velocity = %d", Integer.valueOf(this.mAutoScrollVelocity));
                    } else if (motionEvent.getY() >= ((float) this.mHotspotBottomBoundStart) && motionEvent.getY() <= ((float) this.mHotspotBottomBoundEnd)) {
                        this.mInTopHotspot = false;
                        if (!this.mInBottomHotspot) {
                            this.mInBottomHotspot = true;
                            LOG("Now in BOTTOM hotspot", new Object[0]);
                            this.mAutoScrollHandler.removeCallbacks(this.mAutoScrollRunnable);
                            this.mAutoScrollHandler.postDelayed(this.mAutoScrollRunnable, 25);
                        }
                        this.mAutoScrollVelocity = ((int) ((motionEvent.getY() + ((float) this.mHotspotBottomBoundEnd)) - ((float) (this.mHotspotBottomBoundStart + this.mHotspotBottomBoundEnd)))) / 2;
                        LOG("Auto scroll velocity = %d", Integer.valueOf(this.mAutoScrollVelocity));
                    } else if (this.mInTopHotspot || this.mInBottomHotspot) {
                        LOG("Left the hotspot", new Object[0]);
                        this.mAutoScrollHandler.removeCallbacks(this.mAutoScrollRunnable);
                        this.mInTopHotspot = false;
                        this.mInBottomHotspot = false;
                    }
                }
                return true;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
}
