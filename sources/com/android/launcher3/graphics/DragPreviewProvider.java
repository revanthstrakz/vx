package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.folder.FolderIcon;

public class DragPreviewProvider {
    protected final int blurSizeOutline;
    public Bitmap generatedDragOutline;
    private final Rect mTempRect;
    protected final View mView;
    public final int previewPadding;

    public DragPreviewProvider(View view) {
        this(view, view.getContext());
    }

    public DragPreviewProvider(View view, Context context) {
        this.mTempRect = new Rect();
        this.mView = view;
        this.blurSizeOutline = context.getResources().getDimensionPixelSize(C0622R.dimen.blur_size_medium_outline);
        if (this.mView instanceof BubbleTextView) {
            Rect drawableBounds = getDrawableBounds(((BubbleTextView) this.mView).getIcon());
            this.previewPadding = (this.blurSizeOutline - drawableBounds.left) - drawableBounds.top;
            return;
        }
        this.previewPadding = this.blurSizeOutline;
    }

    private void drawDragView(Canvas canvas) {
        canvas.save();
        if (this.mView instanceof BubbleTextView) {
            Drawable icon = ((BubbleTextView) this.mView).getIcon();
            Rect drawableBounds = getDrawableBounds(icon);
            canvas.translate((float) ((this.blurSizeOutline / 2) - drawableBounds.left), (float) ((this.blurSizeOutline / 2) - drawableBounds.top));
            icon.draw(canvas);
        } else {
            Rect rect = this.mTempRect;
            this.mView.getDrawingRect(rect);
            boolean z = false;
            if ((this.mView instanceof FolderIcon) && ((FolderIcon) this.mView).getTextVisible()) {
                ((FolderIcon) this.mView).setTextVisible(false);
                z = true;
            }
            canvas.translate((float) ((-this.mView.getScrollX()) + (this.blurSizeOutline / 2)), (float) ((-this.mView.getScrollY()) + (this.blurSizeOutline / 2)));
            canvas.clipRect(rect, Op.REPLACE);
            this.mView.draw(canvas);
            if (z) {
                ((FolderIcon) this.mView).setTextVisible(true);
            }
        }
        canvas.restore();
    }

    public Bitmap createDragBitmap(Canvas canvas) {
        int width = this.mView.getWidth();
        int height = this.mView.getHeight();
        float f = 1.0f;
        if (this.mView instanceof BubbleTextView) {
            Rect drawableBounds = getDrawableBounds(((BubbleTextView) this.mView).getIcon());
            int width2 = drawableBounds.width();
            height = drawableBounds.height();
            width = width2;
        } else if (this.mView instanceof LauncherAppWidgetHostView) {
            f = ((LauncherAppWidgetHostView) this.mView).getScaleToFit();
            width = (int) (((float) this.mView.getWidth()) * f);
            height = (int) (((float) this.mView.getHeight()) * f);
        }
        Bitmap createBitmap = Bitmap.createBitmap(width + this.blurSizeOutline, height + this.blurSizeOutline, Config.ARGB_8888);
        canvas.setBitmap(createBitmap);
        canvas.save();
        canvas.scale(f, f);
        drawDragView(canvas);
        canvas.restore();
        canvas.setBitmap(null);
        return createBitmap;
    }

    public final void generateDragOutline(Canvas canvas) {
        this.generatedDragOutline = createDragOutline(canvas);
    }

    public Bitmap createDragOutline(Canvas canvas) {
        float f;
        int width = this.mView.getWidth();
        int height = this.mView.getHeight();
        if (this.mView instanceof LauncherAppWidgetHostView) {
            float scaleToFit = ((LauncherAppWidgetHostView) this.mView).getScaleToFit();
            int floor = (int) Math.floor((double) (((float) this.mView.getHeight()) * scaleToFit));
            f = scaleToFit;
            width = (int) Math.floor((double) (((float) this.mView.getWidth()) * scaleToFit));
            height = floor;
        } else {
            f = 1.0f;
        }
        Bitmap createBitmap = Bitmap.createBitmap(width + this.blurSizeOutline, height + this.blurSizeOutline, Config.ALPHA_8);
        canvas.setBitmap(createBitmap);
        canvas.save();
        canvas.scale(f, f);
        drawDragView(canvas);
        canvas.restore();
        HolographicOutlineHelper.getInstance(this.mView.getContext()).applyExpensiveOutlineWithBlur(createBitmap, canvas);
        canvas.setBitmap(null);
        return createBitmap;
    }

    protected static Rect getDrawableBounds(Drawable drawable) {
        Rect rect = new Rect();
        drawable.copyBounds(rect);
        if (rect.width() == 0 || rect.height() == 0) {
            rect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        } else {
            rect.offsetTo(0, 0);
        }
        return rect;
    }

    public float getScaleAndPosition(Bitmap bitmap, int[] iArr) {
        float locationInDragLayer = Launcher.getLauncher(this.mView.getContext()).getDragLayer().getLocationInDragLayer(this.mView, iArr);
        if (this.mView instanceof LauncherAppWidgetHostView) {
            locationInDragLayer /= ((LauncherAppWidgetHostView) this.mView).getScaleToFit();
        }
        iArr[0] = Math.round(((float) iArr[0]) - ((((float) bitmap.getWidth()) - ((((float) this.mView.getWidth()) * locationInDragLayer) * this.mView.getScaleX())) / 2.0f));
        iArr[1] = Math.round((((float) iArr[1]) - (((1.0f - locationInDragLayer) * ((float) bitmap.getHeight())) / 2.0f)) - ((float) (this.previewPadding / 2)));
        return locationInDragLayer;
    }
}
