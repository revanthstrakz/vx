package com.android.launcher3.folder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragLayer.LayoutParams;

public class PreviewImageView extends ImageView {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final DragLayer mParent;
    private final Rect mTempRect = new Rect();

    public PreviewImageView(DragLayer dragLayer) {
        super(dragLayer.getContext());
        this.mParent = dragLayer;
    }

    public void copy(View view) {
        LayoutParams layoutParams;
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        if (!(this.mBitmap != null && this.mBitmap.getWidth() == measuredWidth && this.mBitmap.getHeight() == measuredHeight)) {
            this.mBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Config.ARGB_8888);
            this.mCanvas = new Canvas(this.mBitmap);
        }
        if (getLayoutParams() instanceof LayoutParams) {
            layoutParams = (LayoutParams) getLayoutParams();
        } else {
            layoutParams = new LayoutParams(measuredWidth, measuredHeight);
        }
        float descendantRectRelativeToSelf = this.mParent.getDescendantRectRelativeToSelf(view, this.mTempRect);
        layoutParams.customPosition = true;
        layoutParams.f61x = this.mTempRect.left;
        layoutParams.f62y = this.mTempRect.top;
        layoutParams.width = (int) (((float) measuredWidth) * descendantRectRelativeToSelf);
        layoutParams.height = (int) (descendantRectRelativeToSelf * ((float) measuredHeight));
        this.mCanvas.drawColor(0, Mode.CLEAR);
        view.draw(this.mCanvas);
        setImageBitmap(this.mBitmap);
        removeFromParent();
        this.mParent.addView(this, layoutParams);
    }

    public void removeFromParent() {
        if (this.mParent.indexOfChild(this) != -1) {
            this.mParent.removeView(this);
        }
    }

    public static PreviewImageView get(Context context) {
        DragLayer dragLayer = Launcher.getLauncher(context).getDragLayer();
        PreviewImageView previewImageView = (PreviewImageView) dragLayer.getTag(C0622R.C0625id.preview_image_id);
        if (previewImageView != null) {
            return previewImageView;
        }
        PreviewImageView previewImageView2 = new PreviewImageView(dragLayer);
        dragLayer.setTag(C0622R.C0625id.preview_image_id, previewImageView2);
        return previewImageView2;
    }
}
