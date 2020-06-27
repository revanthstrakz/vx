package com.android.launcher3.dragndrop;

import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import com.android.launcher3.C0622R;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragOptions.PreDragCondition;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.widget.PendingItemDragHelper;
import java.util.UUID;

public abstract class BaseItemDragListener implements OnDragListener, DragSource, PreDragCondition {
    public static final String EXTRA_PIN_ITEM_DRAG_LISTENER = "pin_item_drag_listener";
    private static final String MIME_TYPE_PREFIX = "com.android.launcher3.drag_and_drop/";
    private static final String TAG = "BaseItemDragListener";
    private DragController mDragController;
    private long mDragStartTime;
    private final String mId;
    protected Launcher mLauncher;
    private final int mPreviewBitmapWidth;
    private final Rect mPreviewRect;
    private final int mPreviewViewWidth;

    /* access modifiers changed from: protected */
    public abstract PendingItemDragHelper createDragHelper();

    public float getIntrinsicIconScaleFactor() {
        return 1.0f;
    }

    public boolean supportsAppInfoDropTarget() {
        return false;
    }

    public boolean supportsDeleteDropTarget() {
        return false;
    }

    public BaseItemDragListener(Rect rect, int i, int i2) {
        this.mPreviewRect = rect;
        this.mPreviewBitmapWidth = i;
        this.mPreviewViewWidth = i2;
        this.mId = UUID.randomUUID().toString();
    }

    protected BaseItemDragListener(Parcel parcel) {
        this.mPreviewRect = (Rect) Rect.CREATOR.createFromParcel(parcel);
        this.mPreviewBitmapWidth = parcel.readInt();
        this.mPreviewViewWidth = parcel.readInt();
        this.mId = parcel.readString();
    }

    /* access modifiers changed from: protected */
    public void writeToParcel(Parcel parcel, int i) {
        this.mPreviewRect.writeToParcel(parcel, i);
        parcel.writeInt(this.mPreviewBitmapWidth);
        parcel.writeInt(this.mPreviewViewWidth);
        parcel.writeString(this.mId);
    }

    public String getMimeType() {
        StringBuilder sb = new StringBuilder();
        sb.append(MIME_TYPE_PREFIX);
        sb.append(this.mId);
        return sb.toString();
    }

    public void setLauncher(Launcher launcher) {
        this.mLauncher = launcher;
        this.mDragController = launcher.getDragController();
    }

    public boolean onDrag(View view, DragEvent dragEvent) {
        if (this.mLauncher == null || this.mDragController == null) {
            postCleanup();
            return false;
        } else if (dragEvent.getAction() != 1) {
            return this.mDragController.onDragEvent(this.mDragStartTime, dragEvent);
        } else {
            if (onDragStart(dragEvent)) {
                return true;
            }
            postCleanup();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean onDragStart(DragEvent dragEvent) {
        ClipDescription clipDescription = dragEvent.getClipDescription();
        if (clipDescription == null || !clipDescription.hasMimeType(getMimeType())) {
            Log.e(TAG, "Someone started a dragAndDrop before us.");
            return false;
        }
        Point point = new Point((int) dragEvent.getX(), (int) dragEvent.getY());
        DragOptions dragOptions = new DragOptions();
        dragOptions.systemDndStartPoint = point;
        dragOptions.preDragCondition = this;
        createDragHelper().startDrag(new Rect(this.mPreviewRect), this.mPreviewBitmapWidth, this.mPreviewViewWidth, point, this, dragOptions);
        this.mDragStartTime = SystemClock.uptimeMillis();
        return true;
    }

    public boolean shouldStartDrag(double d) {
        return !this.mLauncher.isWorkspaceLocked();
    }

    public void onPreDragStart(DragObject dragObject) {
        this.mLauncher.getDragLayer().setAlpha(1.0f);
        dragObject.dragView.setColor(this.mLauncher.getResources().getColor(C0622R.color.delete_target_hover_tint));
    }

    public void onPreDragEnd(DragObject dragObject, boolean z) {
        if (z) {
            dragObject.dragView.setColor(0);
        }
    }

    public void onDropCompleted(View view, DragObject dragObject, boolean z, boolean z2) {
        if (z || !z2 || (view != this.mLauncher.getWorkspace() && !(view instanceof DeleteDropTarget) && !(view instanceof Folder))) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 500, null);
        }
        if (!z2) {
            dragObject.deferDragViewCleanupPostAnimation = false;
        }
        postCleanup();
    }

    private void postCleanup() {
        if (this.mLauncher != null) {
            Intent intent = new Intent(this.mLauncher.getIntent());
            intent.removeExtra("pin_item_drag_listener");
            this.mLauncher.setIntent(intent);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                BaseItemDragListener.this.removeListener();
            }
        });
    }

    public void removeListener() {
        if (this.mLauncher != null) {
            this.mLauncher.getDragLayer().setOnDragListener(null);
        }
    }
}
