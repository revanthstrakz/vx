package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.util.Preconditions;
import java.util.concurrent.Callable;

@TargetApi(26)
public class FolderAdaptiveIcon extends AdaptiveIconDrawable {
    private static final String TAG = "FolderAdaptiveIcon";
    private final Drawable mBadge;
    private final Path mMask;

    private static class ShiftedBitmapDrawable extends Drawable {
        private final Bitmap mBitmap;
        private final Paint mPaint = new Paint(2);
        private final float mShiftX;
        private final float mShiftY;

        public int getOpacity() {
            return -3;
        }

        public void setAlpha(int i) {
        }

        ShiftedBitmapDrawable(Bitmap bitmap, float f, float f2) {
            this.mBitmap = bitmap;
            this.mShiftX = f;
            this.mShiftY = f2;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.mBitmap, this.mShiftX, this.mShiftY, this.mPaint);
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.mPaint.setColorFilter(colorFilter);
        }
    }

    private FolderAdaptiveIcon(Drawable drawable, Drawable drawable2, Drawable drawable3, Path path) {
        super(drawable, drawable2);
        this.mBadge = drawable3;
        this.mMask = path;
    }

    public Path getIconMask() {
        return this.mMask;
    }

    public Drawable getBadge() {
        return this.mBadge;
    }

    public static FolderAdaptiveIcon createFolderAdaptiveIcon(Launcher launcher, long j, Point point) {
        Preconditions.assertNonUiThread();
        int dimensionPixelSize = launcher.getResources().getDimensionPixelSize(C0622R.dimen.blur_size_medium_outline);
        final Bitmap createBitmap = Bitmap.createBitmap(point.x - dimensionPixelSize, point.y - dimensionPixelSize, Config.ARGB_8888);
        float extraInsetFraction = (AdaptiveIconDrawable.getExtraInsetFraction() * 2.0f) + 1.0f;
        final Bitmap createBitmap2 = Bitmap.createBitmap((int) (((float) point.x) * extraInsetFraction), (int) (((float) point.y) * extraInsetFraction), Config.ARGB_8888);
        try {
            MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();
            final Launcher launcher2 = launcher;
            final long j2 = j;
            C07041 r3 = new Callable<FolderAdaptiveIcon>() {
                public FolderAdaptiveIcon call() throws Exception {
                    FolderIcon findFolderIcon = launcher2.findFolderIcon(j2);
                    if (findFolderIcon == null) {
                        return null;
                    }
                    return FolderAdaptiveIcon.createDrawableOnUiThread(findFolderIcon, createBitmap, createBitmap2);
                }
            };
            return (FolderAdaptiveIcon) mainThreadExecutor.submit(r3).get();
        } catch (Exception e) {
            Log.e(TAG, "Unable to create folder icon", e);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public static FolderAdaptiveIcon createDrawableOnUiThread(FolderIcon folderIcon, Bitmap bitmap, Bitmap bitmap2) {
        Preconditions.assertUIThread();
        float dimension = folderIcon.getResources().getDimension(C0622R.dimen.blur_size_medium_outline) / 2.0f;
        Canvas canvas = new Canvas();
        PreviewBackground folderBackground = folderIcon.getFolderBackground();
        canvas.setBitmap(bitmap);
        folderBackground.drawShadow(canvas);
        folderBackground.drawBackgroundStroke(canvas);
        folderIcon.drawBadge(canvas);
        float extraInsetFraction = AdaptiveIconDrawable.getExtraInsetFraction() / ((AdaptiveIconDrawable.getExtraInsetFraction() * 2.0f) + 1.0f);
        float width = ((float) bitmap2.getWidth()) * extraInsetFraction;
        float height = extraInsetFraction * ((float) bitmap2.getHeight());
        canvas.setBitmap(bitmap2);
        canvas.translate(width, height);
        folderIcon.getPreviewItemManager().draw(canvas);
        canvas.setBitmap(null);
        Path path = new Path();
        Matrix matrix = new Matrix();
        matrix.setTranslate(dimension, dimension);
        folderBackground.getClipPath().transform(matrix, path);
        ShiftedBitmapDrawable shiftedBitmapDrawable = new ShiftedBitmapDrawable(bitmap, dimension, dimension);
        return new FolderAdaptiveIcon(new ColorDrawable(folderBackground.getBgColor()), new ShiftedBitmapDrawable(bitmap2, dimension - width, dimension - height), shiftedBitmapDrawable, path);
    }
}
