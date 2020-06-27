package com.google.android.apps.nexuslauncher.qsb;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.launcher3.C0622R;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DeviceProfile.LauncherLayoutChangeListener;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.ShadowGenerator.Builder;
import com.google.android.apps.nexuslauncher.NexusLauncherActivity;

public abstract class AbstractQsbLayout extends FrameLayout implements LauncherLayoutChangeListener, OnClickListener, OnSharedPreferenceChangeListener {
    protected static final String GOOGLE_QSB = "com.google.android.googlequicksearchbox";
    protected final NexusLauncherActivity mActivity;
    protected int mColor;
    private final RectF mDestRect;
    protected View mMicIconView;
    protected Bitmap mShadowBitmap;
    protected final Paint mShadowPaint;
    private final Rect mSrcRect;

    /* access modifiers changed from: protected */
    public abstract int getWidth(int i);

    /* access modifiers changed from: protected */
    public abstract void loadBottomMargin();

    /* access modifiers changed from: protected */
    public void noGoogleAppSearch() {
    }

    public AbstractQsbLayout(Context context) {
        this(context, null);
    }

    public AbstractQsbLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AbstractQsbLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSrcRect = new Rect();
        this.mDestRect = new RectF();
        this.mShadowPaint = new Paint(1);
        this.mColor = 0;
        this.mActivity = (NexusLauncherActivity) Launcher.getLauncher(context);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mActivity.getDeviceProfile().addLauncherLayoutChangedListener(this);
        loadAndGetPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /* access modifiers changed from: protected */
    public SharedPreferences loadAndGetPreferences() {
        this.mMicIconView = findViewById(C0622R.C0625id.mic_icon);
        this.mMicIconView.setOnClickListener(this);
        SharedPreferences devicePrefs = Utilities.getDevicePrefs(getContext());
        loadPreferences(devicePrefs);
        return devicePrefs;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.mActivity.getDeviceProfile().removeLauncherLayoutChangedListener(this);
        Utilities.getDevicePrefs(getContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDetachedFromWindow();
    }

    /* renamed from: bz */
    public void mo12905bz(int i) {
        if (this.mColor != i) {
            this.mColor = i;
            this.mShadowBitmap = null;
            invalidate();
        }
    }

    public void onLauncherLayoutChanged() {
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        loadBottomMargin();
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        int width = getWidth(MeasureSpec.getSize(i));
        int calculateCellWidth = DeviceProfile.calculateCellWidth(width, deviceProfile.inv.numHotseatIcons);
        int round = Math.round(((float) deviceProfile.iconSizePx) * 0.92f);
        setMeasuredDimension((width - (calculateCellWidth - round)) + getPaddingLeft() + getPaddingRight(), MeasureSpec.getSize(i2));
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            measureChildWithMargins(childAt, i, 0, i2, 0);
            if (childAt.getMeasuredWidth() <= round) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int measuredWidth = (round - childAt.getMeasuredWidth()) / 2;
                layoutParams.rightMargin = measuredWidth;
                layoutParams.leftMargin = measuredWidth;
            }
        }
    }

    public void draw(Canvas canvas) {
        if (this.mShadowBitmap == null) {
            float f = (float) LauncherAppState.getIDP(getContext()).iconBitmapSize;
            this.mShadowBitmap = createBitmap(f / 96.0f, f / 48.0f, this.mColor);
        }
        loadDimensions(this.mShadowBitmap, canvas);
        super.draw(canvas);
    }

    /* access modifiers changed from: protected */
    public void loadDimensions(Bitmap bitmap, Canvas canvas) {
        int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
        int i = height + 20;
        int width = bitmap.getWidth();
        int height2 = bitmap.getHeight();
        this.mSrcRect.top = 0;
        this.mSrcRect.bottom = height2;
        this.mDestRect.top = (float) (getPaddingTop() - ((height2 - height) / 2));
        this.mDestRect.bottom = ((float) height2) + this.mDestRect.top;
        float f = (float) ((width - i) / 2);
        int i2 = width / 2;
        float paddingLeft = ((float) getPaddingLeft()) - f;
        float f2 = (float) i2;
        float f3 = paddingLeft + f2;
        Canvas canvas2 = canvas;
        drawWithDimensions(bitmap, canvas2, 0, i2, paddingLeft, f3);
        float width2 = ((float) (getWidth() - getPaddingRight())) + f;
        float f4 = width2 - f2;
        Bitmap bitmap2 = bitmap;
        drawWithDimensions(bitmap2, canvas2, i2, width, f4, width2);
        drawWithDimensions(bitmap2, canvas2, i2 - 5, i2 + 5, f3, f4);
    }

    private void drawWithDimensions(Bitmap bitmap, Canvas canvas, int i, int i2, float f, float f2) {
        this.mSrcRect.left = i;
        this.mSrcRect.right = i2;
        this.mDestRect.left = f;
        this.mDestRect.right = f2;
        canvas.drawBitmap(bitmap, this.mSrcRect, this.mDestRect, this.mShadowPaint);
    }

    /* access modifiers changed from: protected */
    public Bitmap createBitmap(float f, float f2, int i) {
        int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
        int i2 = height + 20;
        Builder builder = new Builder(i);
        builder.shadowBlur = f;
        builder.keyShadowDistance = f2;
        builder.keyShadowAlpha = builder.ambientShadowAlpha;
        Bitmap createPill = builder.createPill(i2, height);
        if (Color.alpha(i) < 255) {
            Canvas canvas = new Canvas(createPill);
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            float f3 = (float) (height / 2);
            canvas.drawRoundRect(builder.bounds, f3, f3, paint);
            paint.setXfermode(null);
            paint.setColor(i);
            canvas.drawRoundRect(builder.bounds, f3, f3, paint);
            canvas.setBitmap(null);
        }
        return Utilities.ATLEAST_OREO ? createPill.copy(Config.HARDWARE, false) : createPill;
    }

    public void onClick(View view) {
        if (view == this.mMicIconView) {
            fallbackSearch("android.intent.action.VOICE_ASSIST");
        }
    }

    /* access modifiers changed from: protected */
    public void fallbackSearch(String str) {
        try {
            getContext().startActivity(new Intent(str).addFlags(268468224).setPackage(GOOGLE_QSB));
        } catch (ActivityNotFoundException unused) {
            noGoogleAppSearch();
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if ("opa_enabled".equals(str)) {
            loadPreferences(sharedPreferences);
        }
    }

    private void loadPreferences(SharedPreferences sharedPreferences) {
        this.mMicIconView.setVisibility(sharedPreferences.getBoolean("opa_enabled", true) ? 8 : 0);
        requestLayout();
    }
}
