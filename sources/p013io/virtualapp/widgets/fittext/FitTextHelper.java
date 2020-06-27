package p013io.virtualapp.widgets.fittext;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.support.p001v4.view.GravityCompat;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;
import com.android.launcher3.IconCache;
import com.microsoft.appcenter.Constants;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Marker;

/* renamed from: io.virtualapp.widgets.fittext.FitTextHelper */
class FitTextHelper {
    protected static final float LIMIT = 0.001f;
    private static final boolean LastNoSpace = false;
    public static final List<CharSequence> sSpcaeList = new ArrayList();
    protected volatile boolean mFittingText = false;
    protected BaseTextView textView;

    static {
        sSpcaeList.add(",");
        sSpcaeList.add(IconCache.EMPTY_CLASS_NAME);
        sSpcaeList.add(";");
        sSpcaeList.add("'");
        sSpcaeList.add("\"");
        sSpcaeList.add(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        sSpcaeList.add("?");
        sSpcaeList.add("~");
        sSpcaeList.add("!");
        sSpcaeList.add("‘");
        sSpcaeList.add("’");
        sSpcaeList.add("”");
        sSpcaeList.add("“");
        sSpcaeList.add("；");
        sSpcaeList.add("：");
        sSpcaeList.add("，");
        sSpcaeList.add("。");
        sSpcaeList.add("？");
        sSpcaeList.add("！");
        sSpcaeList.add("(");
        sSpcaeList.add(")");
        sSpcaeList.add("[");
        sSpcaeList.add("]");
        sSpcaeList.add("@");
        sSpcaeList.add("/");
        sSpcaeList.add("#");
        sSpcaeList.add("$");
        sSpcaeList.add("%");
        sSpcaeList.add("^");
        sSpcaeList.add("&");
        sSpcaeList.add(Marker.ANY_MARKER);
        sSpcaeList.add("<");
        sSpcaeList.add(">");
        sSpcaeList.add(Marker.ANY_NON_NULL_MARKER);
        sSpcaeList.add("-");
        sSpcaeList.add("·");
    }

    public FitTextHelper(BaseTextView baseTextView) {
        this.textView = baseTextView;
    }

    public static boolean isSingleLine(TextView textView2) {
        boolean z = false;
        if (textView2 == null) {
            return false;
        }
        if (textView2 instanceof BaseTextView) {
            return ((BaseTextView) textView2).isSingleLine();
        }
        if (textView2 == null) {
            return false;
        }
        if ((textView2.getInputType() & 131072) == 131072) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public int getMaxLineCount() {
        return (int) (((float) this.textView.getTextHeight()) / this.textView.getTextLineHeight());
    }

    public static int getTextWidth(TextView textView2) {
        return (textView2.getMeasuredWidth() - textView2.getCompoundPaddingLeft()) - textView2.getCompoundPaddingRight();
    }

    public StaticLayout getStaticLayout(CharSequence charSequence, TextPaint textPaint) {
        return getStaticLayout(this.textView.getTextView(), charSequence, textPaint);
    }

    public static StaticLayout getStaticLayout(TextView textView2, CharSequence charSequence, TextPaint textPaint) {
        StaticLayout staticLayout;
        TextView textView3 = textView2;
        if (textView3 instanceof FitTextView) {
            FitTextView fitTextView = (FitTextView) textView3;
            StaticLayout staticLayout2 = new StaticLayout(charSequence, textPaint, getTextWidth(textView2), getLayoutAlignment(fitTextView), fitTextView.getLineSpacingMultiplierCompat(), fitTextView.getLineSpacingExtraCompat(), fitTextView.getIncludeFontPaddingCompat());
            staticLayout = staticLayout2;
        } else if (VERSION.SDK_INT <= 16) {
            staticLayout = new StaticLayout(charSequence, textPaint, getTextWidth(textView2), getLayoutAlignment(textView2), 0.0f, 0.0f, false);
        } else {
            staticLayout = new StaticLayout(charSequence, textPaint, getTextWidth(textView2), getLayoutAlignment(textView2), textView2.getLineSpacingMultiplier(), textView2.getLineSpacingExtra(), textView2.getIncludeFontPadding());
        }
        if (isSingleLine(textView2)) {
            try {
                Field declaredField = StaticLayout.class.getDeclaredField("mMaximumVisibleLineCount");
                if (declaredField != null) {
                    declaredField.setAccessible(true);
                    declaredField.set(staticLayout, Integer.valueOf(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return staticLayout;
    }

    /* access modifiers changed from: protected */
    public boolean isFit(CharSequence charSequence, TextPaint textPaint) {
        int i;
        boolean isSingleLine = this.textView.isSingleLine();
        int maxLinesCompat = this.textView.getMaxLinesCompat();
        float lineSpacingExtraCompat = this.textView.getLineSpacingExtraCompat() * this.textView.getLineSpacingMultiplierCompat();
        int textHeight = this.textView.getTextHeight();
        if (!isSingleLine) {
            textHeight += Math.round(lineSpacingExtraCompat);
        }
        if (isSingleLine) {
            i = 1;
        } else {
            i = Math.max(1, maxLinesCompat);
        }
        StaticLayout staticLayout = getStaticLayout(charSequence, textPaint);
        if (staticLayout.getLineCount() > i || staticLayout.getHeight() > textHeight) {
            return false;
        }
        return true;
    }

    public float fitTextSize(TextPaint textPaint, CharSequence charSequence, float f, float f2) {
        if (TextUtils.isEmpty(charSequence)) {
            if (textPaint != null) {
                return textPaint.getTextSize();
            }
            if (this.textView != null) {
                return this.textView.getTextSize();
            }
        }
        TextPaint textPaint2 = new TextPaint(textPaint);
        while (Math.abs(f - f2) > LIMIT) {
            textPaint2.setTextSize((f2 + f) / 2.0f);
            if (isFit(getLineBreaks(charSequence, textPaint2), textPaint2)) {
                f2 = textPaint2.getTextSize();
            } else {
                f = textPaint2.getTextSize();
            }
        }
        return f2;
    }

    public CharSequence getLineBreaks(CharSequence charSequence, TextPaint textPaint) {
        int textWidth = this.textView.getTextWidth();
        boolean isKeepWord = this.textView.isKeepWord();
        if (textWidth <= 0 || isKeepWord) {
            return charSequence;
        }
        int length = charSequence.length();
        int i = 0;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (int i2 = 1; i2 <= length; i2++) {
            int i3 = i2 - 1;
            if (TextUtils.equals(charSequence.subSequence(i3, i2), "\n")) {
                spannableStringBuilder.append(charSequence, i, i2);
            } else {
                int i4 = (textPaint.measureText(charSequence, i, i2) > ((float) textWidth) ? 1 : (textPaint.measureText(charSequence, i, i2) == ((float) textWidth) ? 0 : -1));
                if (i4 > 0) {
                    spannableStringBuilder.append(charSequence, i, i3);
                    if (i2 < length && !TextUtils.equals(charSequence.subSequence(i3, i2), "\n")) {
                        spannableStringBuilder.append(10);
                    }
                    i = i3;
                } else if (i4 == 0) {
                    spannableStringBuilder.append(charSequence, i, i2);
                    if (i2 < length && !TextUtils.equals(charSequence.subSequence(i2, i2 + 1), "\n")) {
                        spannableStringBuilder.append(10);
                    }
                } else if (i2 == length) {
                    spannableStringBuilder.append(charSequence, i, i2);
                }
            }
            i = i2;
        }
        return spannableStringBuilder;
    }

    @TargetApi(17)
    public static Alignment getLayoutAlignment(TextView textView2) {
        Alignment alignment;
        if (VERSION.SDK_INT < 17) {
            return Alignment.ALIGN_NORMAL;
        }
        switch (textView2.getTextAlignment()) {
            case 1:
                int gravity = textView2.getGravity() & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
                if (gravity != 1) {
                    if (gravity == 3) {
                        if (textView2.getLayoutDirection() != 1) {
                            alignment = Alignment.ALIGN_NORMAL;
                            break;
                        } else {
                            alignment = Alignment.ALIGN_OPPOSITE;
                            break;
                        }
                    } else if (gravity == 5) {
                        if (textView2.getLayoutDirection() != 1) {
                            alignment = Alignment.ALIGN_OPPOSITE;
                            break;
                        } else {
                            alignment = Alignment.ALIGN_NORMAL;
                            break;
                        }
                    } else if (gravity != 8388611) {
                        if (gravity == 8388613) {
                            alignment = Alignment.ALIGN_OPPOSITE;
                            break;
                        } else {
                            alignment = Alignment.ALIGN_NORMAL;
                            break;
                        }
                    } else {
                        alignment = Alignment.ALIGN_NORMAL;
                        break;
                    }
                } else {
                    alignment = Alignment.ALIGN_CENTER;
                    break;
                }
            case 2:
                alignment = Alignment.ALIGN_NORMAL;
                break;
            case 3:
                alignment = Alignment.ALIGN_OPPOSITE;
                break;
            case 4:
                alignment = Alignment.ALIGN_CENTER;
                break;
            case 5:
                alignment = Alignment.ALIGN_NORMAL;
                break;
            case 6:
                alignment = Alignment.ALIGN_OPPOSITE;
                break;
            default:
                alignment = Alignment.ALIGN_NORMAL;
                break;
        }
        return alignment;
    }
}
