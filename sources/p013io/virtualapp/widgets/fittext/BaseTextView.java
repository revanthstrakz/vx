package p013io.virtualapp.widgets.fittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import p013io.virtualapp.utils.HanziToPinyin.Token;

/* renamed from: io.virtualapp.widgets.fittext.BaseTextView */
class BaseTextView extends TextView {
    private static final int[] ANDROID_ATTRS = {16843103, 16843288, 16843287, 16843091, 16843101};
    protected boolean mIncludeFontPadding;
    protected boolean mJustify;
    protected boolean mKeepWord;
    protected boolean mLineEndNoSpace;
    protected float mLineSpacingAdd;
    protected float mLineSpacingMult;
    protected int mMaxLines;
    protected boolean mSingleLine;

    public TextView getTextView() {
        return this;
    }

    public BaseTextView(Context context) {
        this(context, null);
    }

    public BaseTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSingleLine = false;
        this.mIncludeFontPadding = true;
        this.mLineSpacingMult = 1.0f;
        this.mLineSpacingAdd = 0.0f;
        this.mMaxLines = Integer.MAX_VALUE;
        this.mLineEndNoSpace = true;
        this.mJustify = false;
        this.mKeepWord = true;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ANDROID_ATTRS);
            if (VERSION.SDK_INT < 16) {
                this.mIncludeFontPadding = obtainStyledAttributes.getBoolean(obtainStyledAttributes.getIndex(0), this.mIncludeFontPadding);
                this.mLineSpacingMult = obtainStyledAttributes.getFloat(obtainStyledAttributes.getIndex(1), this.mLineSpacingMult);
                this.mLineSpacingAdd = (float) obtainStyledAttributes.getDimensionPixelSize(obtainStyledAttributes.getIndex(2), (int) this.mLineSpacingAdd);
                this.mMaxLines = obtainStyledAttributes.getInteger(obtainStyledAttributes.getIndex(3), this.mMaxLines);
            }
            this.mSingleLine = obtainStyledAttributes.getBoolean(16843101, this.mSingleLine);
            obtainStyledAttributes.recycle();
        }
    }

    public BaseTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mSingleLine = false;
        this.mIncludeFontPadding = true;
        this.mLineSpacingMult = 1.0f;
        this.mLineSpacingAdd = 0.0f;
        this.mMaxLines = Integer.MAX_VALUE;
        this.mLineEndNoSpace = true;
        this.mJustify = false;
        this.mKeepWord = true;
    }

    public boolean isKeepWord() {
        return this.mKeepWord;
    }

    public void setKeepWord(boolean z) {
        this.mKeepWord = z;
    }

    public boolean isJustify() {
        return this.mJustify;
    }

    public void setJustify(boolean z) {
        this.mJustify = z;
    }

    public boolean isLineEndNoSpace() {
        return this.mLineEndNoSpace;
    }

    public void setLineEndNoSpace(boolean z) {
        this.mLineEndNoSpace = z;
    }

    @TargetApi(16)
    public boolean getIncludeFontPaddingCompat() {
        if (VERSION.SDK_INT >= 16) {
            return getIncludeFontPadding();
        }
        return this.mIncludeFontPadding;
    }

    @TargetApi(16)
    public float getLineSpacingMultiplierCompat() {
        if (VERSION.SDK_INT >= 16) {
            return getLineSpacingMultiplier();
        }
        return this.mLineSpacingMult;
    }

    @TargetApi(16)
    public float getLineSpacingExtraCompat() {
        if (VERSION.SDK_INT >= 16) {
            return getLineSpacingExtra();
        }
        return this.mLineSpacingAdd;
    }

    @TargetApi(16)
    public int getMaxLinesCompat() {
        if (VERSION.SDK_INT >= 16) {
            return getMaxLines();
        }
        return this.mMaxLines;
    }

    public void setLineSpacing(float f, float f2) {
        super.setLineSpacing(f, f2);
        this.mLineSpacingAdd = f;
        this.mLineSpacingMult = f2;
    }

    public void setIncludeFontPadding(boolean z) {
        super.setIncludeFontPadding(z);
        this.mIncludeFontPadding = z;
    }

    public void setMaxLines(int i) {
        super.setMaxLines(i);
        this.mMaxLines = i;
    }

    public void setSingleLine(boolean z) {
        super.setSingleLine(z);
        this.mSingleLine = z;
    }

    public int getTextWidth() {
        return FitTextHelper.getTextWidth(this);
    }

    public int getTextHeight() {
        return (getMeasuredHeight() - getCompoundPaddingTop()) - getCompoundPaddingBottom();
    }

    public void setBoldText(boolean z) {
        getPaint().setFakeBoldText(z);
    }

    public void setItalicText(boolean z) {
        getPaint().setTextSkewX(z ? -0.25f : 0.0f);
    }

    public boolean isItalicText() {
        return getPaint().getTextSkewX() != 0.0f;
    }

    public boolean isSingleLine() {
        return this.mSingleLine;
    }

    public float getTextLineHeight() {
        return (float) getLineHeight();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        if (!this.mJustify || this.mSingleLine) {
            super.onDraw(canvas);
            return;
        }
        TextPaint paint = getPaint();
        float textWidth = (float) getTextWidth();
        if (isItalicText()) {
            textWidth -= getPaint().measureText("a");
        }
        float f = textWidth;
        CharSequence text = getText();
        Layout layout = getLayout();
        if (layout == null) {
            layout = FitTextHelper.getStaticLayout(this, getText(), getPaint());
        }
        Layout layout2 = layout;
        int lineCount = layout2.getLineCount();
        int i2 = 0;
        while (i2 < lineCount) {
            int lineStart = layout2.getLineStart(i2);
            int lineEnd = layout2.getLineEnd(i2);
            float lineLeft = layout2.getLineLeft(i2);
            int i3 = i2 + 1;
            int topPadding = layout2.getTopPadding() + (getLineHeight() * i3);
            CharSequence subSequence = text.subSequence(lineStart, lineEnd);
            if (subSequence.length() != 0) {
                boolean z = true;
                if (this.mLineEndNoSpace) {
                    if (TextUtils.equals(subSequence.subSequence(subSequence.length() - 1, subSequence.length()), Token.SEPARATOR)) {
                        i = 0;
                        subSequence = subSequence.subSequence(0, subSequence.length() - 1);
                    } else {
                        i = 0;
                    }
                    if (TextUtils.equals(subSequence.subSequence(i, 1), Token.SEPARATOR)) {
                        subSequence = subSequence.subSequence(1, subSequence.length() - 1);
                    }
                }
                float measureText = getPaint().measureText(text, lineStart, lineEnd);
                if (i2 >= lineCount - 1 || !needScale(text.subSequence(lineEnd - 1, lineEnd))) {
                    z = false;
                }
                if (!z || f <= measureText) {
                    canvas.drawText(subSequence, 0, subSequence.length(), lineLeft, (float) topPadding, paint);
                } else {
                    float countEmpty = (f - measureText) / ((float) countEmpty(subSequence));
                    int i4 = 0;
                    while (i4 < subSequence.length()) {
                        int i5 = i4 + 1;
                        float measureText2 = getPaint().measureText(subSequence, i4, i5);
                        canvas.drawText(subSequence, i4, i5, lineLeft, (float) topPadding, getPaint());
                        lineLeft += measureText2;
                        if (isEmpty(subSequence, i5, i4 + 2)) {
                            lineLeft += countEmpty / 2.0f;
                        }
                        if (isEmpty(subSequence, i4, i5)) {
                            lineLeft += countEmpty / 2.0f;
                        }
                        i4 = i5;
                    }
                }
            }
            i2 = i3;
        }
    }

    /* access modifiers changed from: protected */
    public int countEmpty(CharSequence charSequence) {
        int length = charSequence.length();
        int i = 0;
        int i2 = 0;
        while (i < length) {
            int i3 = i + 1;
            if (isEmpty(charSequence, i, i3)) {
                i2++;
            }
            i = i3;
        }
        return i2;
    }

    /* access modifiers changed from: protected */
    public boolean isEmpty(CharSequence charSequence, int i, int i2) {
        boolean z = false;
        if (i2 >= charSequence.length()) {
            return false;
        }
        CharSequence subSequence = charSequence.subSequence(i, i2);
        if (TextUtils.equals(subSequence, "\t") || TextUtils.equals(subSequence, Token.SEPARATOR) || FitTextHelper.sSpcaeList.contains(subSequence)) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public boolean needScale(CharSequence charSequence) {
        return TextUtils.equals(charSequence, Token.SEPARATOR);
    }
}
