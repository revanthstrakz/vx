package p013io.virtualapp.widgets.fittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import io.va.exposed.R;

/* renamed from: io.virtualapp.widgets.fittext.FitTextView */
public class FitTextView extends BaseTextView {
    protected FitTextHelper mFitTextHelper;
    protected volatile boolean mFittingText;
    private float mMaxTextSize;
    private boolean mMeasured;
    private float mMinTextSize;
    private boolean mNeedFit;
    protected CharSequence mOriginalText;
    protected float mOriginalTextSize;

    @TargetApi(16)
    public /* bridge */ /* synthetic */ boolean getIncludeFontPaddingCompat() {
        return super.getIncludeFontPaddingCompat();
    }

    @TargetApi(16)
    public /* bridge */ /* synthetic */ float getLineSpacingExtraCompat() {
        return super.getLineSpacingExtraCompat();
    }

    @TargetApi(16)
    public /* bridge */ /* synthetic */ float getLineSpacingMultiplierCompat() {
        return super.getLineSpacingMultiplierCompat();
    }

    @TargetApi(16)
    public /* bridge */ /* synthetic */ int getMaxLinesCompat() {
        return super.getMaxLinesCompat();
    }

    public /* bridge */ /* synthetic */ int getTextHeight() {
        return super.getTextHeight();
    }

    public /* bridge */ /* synthetic */ float getTextLineHeight() {
        return super.getTextLineHeight();
    }

    public /* bridge */ /* synthetic */ TextView getTextView() {
        return super.getTextView();
    }

    public /* bridge */ /* synthetic */ int getTextWidth() {
        return super.getTextWidth();
    }

    public /* bridge */ /* synthetic */ boolean isItalicText() {
        return super.isItalicText();
    }

    public /* bridge */ /* synthetic */ boolean isJustify() {
        return super.isJustify();
    }

    public /* bridge */ /* synthetic */ boolean isKeepWord() {
        return super.isKeepWord();
    }

    public /* bridge */ /* synthetic */ boolean isLineEndNoSpace() {
        return super.isLineEndNoSpace();
    }

    public /* bridge */ /* synthetic */ boolean isSingleLine() {
        return super.isSingleLine();
    }

    public /* bridge */ /* synthetic */ void setBoldText(boolean z) {
        super.setBoldText(z);
    }

    public /* bridge */ /* synthetic */ void setIncludeFontPadding(boolean z) {
        super.setIncludeFontPadding(z);
    }

    public /* bridge */ /* synthetic */ void setItalicText(boolean z) {
        super.setItalicText(z);
    }

    public /* bridge */ /* synthetic */ void setJustify(boolean z) {
        super.setJustify(z);
    }

    public /* bridge */ /* synthetic */ void setKeepWord(boolean z) {
        super.setKeepWord(z);
    }

    public /* bridge */ /* synthetic */ void setLineEndNoSpace(boolean z) {
        super.setLineEndNoSpace(z);
    }

    public /* bridge */ /* synthetic */ void setLineSpacing(float f, float f2) {
        super.setLineSpacing(f, f2);
    }

    public /* bridge */ /* synthetic */ void setMaxLines(int i) {
        super.setMaxLines(i);
    }

    public /* bridge */ /* synthetic */ void setSingleLine(boolean z) {
        super.setSingleLine(z);
    }

    public FitTextView(Context context) {
        this(context, null);
    }

    public FitTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FitTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMeasured = false;
        this.mNeedFit = true;
        this.mOriginalTextSize = 0.0f;
        this.mFittingText = false;
        this.mOriginalTextSize = getTextSize();
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{R.attr.ftMaxTextSize, R.attr.ftMinTextSize});
            this.mMaxTextSize = obtainStyledAttributes.getDimension(0, this.mOriginalTextSize * 2.0f);
            this.mMinTextSize = obtainStyledAttributes.getDimension(1, this.mOriginalTextSize / 2.0f);
            obtainStyledAttributes.recycle();
            return;
        }
        this.mMinTextSize = this.mOriginalTextSize;
        this.mMaxTextSize = this.mOriginalTextSize;
    }

    /* access modifiers changed from: protected */
    public FitTextHelper getFitTextHelper() {
        if (this.mFitTextHelper == null) {
            this.mFitTextHelper = new FitTextHelper(this);
        }
        return this.mFitTextHelper;
    }

    public float getMinTextSize() {
        return this.mMinTextSize;
    }

    public void setMinTextSize(float f) {
        this.mMinTextSize = f;
    }

    public float getMaxTextSize() {
        return this.mMaxTextSize;
    }

    public void setMaxTextSize(float f) {
        this.mMaxTextSize = f;
    }

    public boolean isNeedFit() {
        return this.mNeedFit;
    }

    public void setNeedFit(boolean z) {
        this.mNeedFit = z;
    }

    public void setTextSize(int i, float f) {
        super.setTextSize(i, f);
        this.mOriginalTextSize = getTextSize();
    }

    public float getOriginalTextSize() {
        return this.mOriginalTextSize;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        if (mode == 0 && mode2 == 0) {
            super.setTextSize(0, this.mOriginalTextSize);
            this.mMeasured = false;
            return;
        }
        this.mMeasured = true;
        fitText(getOriginalText());
    }

    public void setText(CharSequence charSequence, BufferType bufferType) {
        this.mOriginalText = charSequence;
        super.setText(charSequence, bufferType);
        fitText(charSequence);
    }

    public CharSequence getOriginalText() {
        return this.mOriginalText;
    }

    /* access modifiers changed from: protected */
    public void fitText(CharSequence charSequence) {
        if (this.mNeedFit && this.mMeasured && !this.mFittingText && !this.mSingleLine && !TextUtils.isEmpty(charSequence)) {
            this.mFittingText = true;
            super.setTextSize(0, getFitTextHelper().fitTextSize(getPaint(), charSequence, this.mMaxTextSize, this.mMinTextSize));
            super.setText(getFitTextHelper().getLineBreaks(charSequence, getPaint()));
            this.mFittingText = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
