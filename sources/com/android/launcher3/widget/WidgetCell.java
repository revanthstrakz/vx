package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CancellationSignal;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.C0622R;
import com.android.launcher3.SimpleOnStylusPressListener;
import com.android.launcher3.StylusEventHelper;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.model.WidgetItem;

public class WidgetCell extends LinearLayout implements OnLayoutChangeListener {
    private static final boolean DEBUG = false;
    private static final int FADE_IN_DURATION_MS = 90;
    private static final float PREVIEW_SCALE = 0.8f;
    private static final String TAG = "WidgetCell";
    private static final float WIDTH_SCALE = 2.6f;
    protected CancellationSignal mActiveRequest;
    protected final BaseActivity mActivity;
    private boolean mAnimatePreview;
    private int mCellSize;
    protected WidgetItem mItem;
    protected int mPresetPreviewSize;
    private StylusEventHelper mStylusEventHelper;
    private TextView mWidgetDims;
    private WidgetImageView mWidgetImage;
    private TextView mWidgetName;
    private WidgetPreviewLoader mWidgetPreviewLoader;

    public WidgetCell(Context context) {
        this(context, null);
    }

    public WidgetCell(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetCell(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAnimatePreview = true;
        this.mActivity = BaseActivity.fromContext(context);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        setContainerWidth();
        setWillNotDraw(false);
        setClipToPadding(false);
        setAccessibilityDelegate(this.mActivity.getAccessibilityDelegate());
    }

    private void setContainerWidth() {
        this.mCellSize = (int) (((float) this.mActivity.getDeviceProfile().cellWidthPx) * WIDTH_SCALE);
        this.mPresetPreviewSize = (int) (((float) this.mCellSize) * PREVIEW_SCALE);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mWidgetImage = (WidgetImageView) findViewById(C0622R.C0625id.widget_preview);
        this.mWidgetName = (TextView) findViewById(C0622R.C0625id.widget_name);
        this.mWidgetDims = (TextView) findViewById(C0622R.C0625id.widget_dims);
    }

    public void clear() {
        this.mWidgetImage.animate().cancel();
        this.mWidgetImage.setBitmap(null, null);
        this.mWidgetName.setText(null);
        this.mWidgetDims.setText(null);
        if (this.mActiveRequest != null) {
            this.mActiveRequest.cancel();
            this.mActiveRequest = null;
        }
    }

    public void applyFromCellItem(WidgetItem widgetItem, WidgetPreviewLoader widgetPreviewLoader) {
        this.mItem = widgetItem;
        this.mWidgetName.setText(this.mItem.label);
        this.mWidgetDims.setText(getContext().getString(C0622R.string.widget_dims_format, new Object[]{Integer.valueOf(this.mItem.spanX), Integer.valueOf(this.mItem.spanY)}));
        this.mWidgetDims.setContentDescription(getContext().getString(C0622R.string.widget_accessible_dims_format, new Object[]{Integer.valueOf(this.mItem.spanX), Integer.valueOf(this.mItem.spanY)}));
        this.mWidgetPreviewLoader = widgetPreviewLoader;
        if (widgetItem.activityInfo != null) {
            setTag(new PendingAddShortcutInfo(widgetItem.activityInfo));
        } else {
            setTag(new PendingAddWidgetInfo(widgetItem.widgetInfo));
        }
    }

    public WidgetImageView getWidgetView() {
        return this.mWidgetImage;
    }

    public void setAnimatePreview(boolean z) {
        this.mAnimatePreview = z;
    }

    public void applyPreview(Bitmap bitmap) {
        applyPreview(bitmap, true);
    }

    public void applyPreview(Bitmap bitmap, boolean z) {
        if (bitmap != null) {
            this.mWidgetImage.setBitmap(bitmap, DrawableFactory.get(getContext()).getBadgeForUser(this.mItem.user, getContext()));
            if (this.mAnimatePreview) {
                this.mWidgetImage.setAlpha(0.0f);
                this.mWidgetImage.animate().alpha(1.0f).setDuration(90);
                return;
            }
            this.mWidgetImage.setAlpha(1.0f);
        }
    }

    public void ensurePreview() {
        ensurePreview(true);
    }

    public void ensurePreview(boolean z) {
        if (this.mActiveRequest == null) {
            this.mActiveRequest = this.mWidgetPreviewLoader.getPreview(this.mItem, this.mPresetPreviewSize, this.mPresetPreviewSize, this, z);
        }
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        removeOnLayoutChangeListener(this);
        ensurePreview();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        if (this.mStylusEventHelper.onMotionEvent(motionEvent)) {
            return true;
        }
        return onTouchEvent;
    }

    private String getTagToString() {
        if ((getTag() instanceof PendingAddWidgetInfo) || (getTag() instanceof PendingAddShortcutInfo)) {
            return getTag().toString();
        }
        return "";
    }

    public void setLayoutParams(LayoutParams layoutParams) {
        int i = this.mCellSize;
        layoutParams.height = i;
        layoutParams.width = i;
        super.setLayoutParams(layoutParams);
    }

    public CharSequence getAccessibilityClassName() {
        return WidgetCell.class.getName();
    }
}
