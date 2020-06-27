package p013io.virtualapp.widgets;

import android.content.Context;
import android.support.p004v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/* renamed from: io.virtualapp.widgets.MarqueeTextView */
public class MarqueeTextView extends AppCompatTextView {
    private boolean isStop = false;

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MarqueeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean isFocused() {
        if (this.isStop) {
            return super.isFocused();
        }
        return true;
    }

    public void stopScroll() {
        this.isStop = true;
    }

    public void start() {
        this.isStop = false;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        stopScroll();
        super.onDetachedFromWindow();
    }
}
